package jmdevy.cavein;

import jmdevy.cavein.config.ServerConfigHandler;
import jmdevy.cavein.network.MessageRegistration;
import jmdevy.cavein.network.messages.toclient.ToClientMessageCaveinStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Cavein.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class Server {
    private static Random random = new Random(System.currentTimeMillis());

    private static final int caveinSizeXZ = 32;     // Really double since will look for blocks in -caveSizeXZ to +caveSizeXZ
    private static final int caveinSizeAbove = 16;  // Just above the Y position the cave in starts at
    private static final int caveinSizeBelow = 4;   // Just below the Y position the cave in starts at

    private static long lastCaveinTickCount = 0;    // General timer
    private static long currentCaveinTickCount = 0; // General timer

    private static BlockPos[] caveinBlockPositions = new BlockPos[256]; // Up to 256 blocks can be chosen to fall, makes sense to store and track so not always fetching chunks and block states
    private static FallingBlockEntity[] entities = new FallingBlockEntity[256];
    private static int caveinBlockPositionsIndex = 0;                   // The index up to the end of how many blocks were chosen to fall.
    private static int entitiesIndex = 0;

    private static List<String> whitelistedBlocks;


    enum STATE{
        CHECKING,
        CAVING,
    }

    private static STATE currentState = STATE.CHECKING;

    // Variables that are randomly chosen and during the event of a cave in
    private static int caveinDurationSeconds = 0;

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event) {
        if(whitelistedBlocks == null){
            String whitelistedBlocksStr = ServerConfigHandler.SERVER_CONFIG.whitelistedBlocks.get();
            whitelistedBlocksStr = whitelistedBlocksStr.substring(1, whitelistedBlocksStr.length()-1);
            whitelistedBlocks = List.of(whitelistedBlocksStr.split(","));
        }


        // Store the number of ticks that have been occurring
        currentCaveinTickCount++;

        if(currentState == STATE.CHECKING){
            checkStartCavein(event);
        }else if(currentState == STATE.CAVING){
            doCavein(event);
        }
    }


    private static void sendMessageToAllPlayers(List<ServerPlayer> players, ToClientMessageCaveinStatus message){
        for(int ipx=0; ipx<players.size(); ipx++){
            ServerPlayer player = players.get(ipx);
            MessageRegistration.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }


    private static boolean checkIfWhitelisted(String block){
        return whitelistedBlocks.contains(block.substring(6, block.length()-1));
    }


    // See if a cave in should start for a random player
    private static void checkStartCavein(TickEvent.ServerTickEvent event){
        // Only potentially start a cave-in at the end of the tick when all blocks most up to date
        if(event.phase == TickEvent.Phase.END){
            long lastCaveinCheckSecond = lastCaveinTickCount / Cavein.ticksPerSecond;
            long currentCaveinCheckSecond = currentCaveinTickCount / Cavein.ticksPerSecond;

            // It's been long enough according to the server config values, see if chance starts a cave-in
            if(currentCaveinCheckSecond-lastCaveinCheckSecond > ServerConfigHandler.SERVER_CONFIG.secondsToCavein.get()){

                // Randomly check if cave in should start, if so, change state and save some random values for later usage
                if((int)(Math.random() * ServerConfigHandler.SERVER_CONFIG.caveinChance.get()) == 1){
                    List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();

                    // Only actually cave in if there is at least one player
                    if(players.size() > 0) {
                        currentState = STATE.CAVING;

                        int minSeconds = ServerConfigHandler.SERVER_CONFIG.minCaveinDurationSeconds.get();
                        int maxSeconds = ServerConfigHandler.SERVER_CONFIG.maxCaveinDurationSeconds.get();

                        // Generate random cave in attribute
                        caveinDurationSeconds = minSeconds + random.nextInt(maxSeconds - minSeconds + 1);

                        // Store list of blocks to fall
                        ServerPlayer caveinPlayer = players.get(random.nextInt(players.size()));
                        Vec3 caveinOrigin = caveinPlayer.position();
                        caveinBlockPositionsIndex = 0;
                        entitiesIndex = 0;

                        int y = ((int)caveinOrigin.y) - caveinSizeBelow;

                        // Find and store block positions for cave in
                        for(int i=0; i<caveinBlockPositions.length*2; i++){
                            // Generate random points in circle
                            double r = ServerConfigHandler.SERVER_CONFIG.caveinRadius.get() * Math.sqrt(random.nextFloat());
                            double theta = random.nextFloat() * 2 * Math.PI;
                            int x = (int)(caveinOrigin.x + r * Math.cos(theta));
                            int z = (int)(caveinOrigin.z + r * Math.sin(theta));

                            // Serach bottom to top of cave in circle for blocks that have air or water under them
                            for(int iyx=0; iyx<caveinSizeBelow+caveinSizeAbove; iyx++){
                                if(iyx+y < ServerConfigHandler.SERVER_CONFIG.maxCaveinYLevel.get()) {
                                    BlockPos block = new BlockPos(x, iyx + y, z);
                                    String blockStr = event.getServer().overworld().getBlockState(block).getBlock().toString();

                                    BlockPos blockBelow = new BlockPos(x, iyx + y - 1, z);
                                    Material blockBelowMat = event.getServer().overworld().getBlockState(blockBelow).getMaterial();

                                    if (checkIfWhitelisted(blockStr) && (blockBelowMat == Material.AIR || blockBelowMat == Material.WATER)) {
                                        caveinBlockPositions[caveinBlockPositionsIndex] = new BlockPos(x, iyx + y, z);
                                        caveinBlockPositionsIndex++;
                                        break;
                                    }
                                }else{
                                    break;
                                }
                            }

                            // Full, can't fit anymore blocks to fall, stop looking
                            if(caveinBlockPositionsIndex >= caveinBlockPositions.length){
                                break;
                            }
                        }

                        // Everything is setup for the cave in, tell the clients about to shake their cameras and play sound
                        sendMessageToAllPlayers(players, new ToClientMessageCaveinStatus(
                                true,
                                (int)caveinOrigin.x,
                                (int)caveinOrigin.y,
                                (int)caveinOrigin.z,
                                ServerConfigHandler.SERVER_CONFIG.caveinRadius.get()));
                    }
                }

                // Update this so next time can try track how long it's been (also used as cave in start time if state changed above)
                lastCaveinTickCount = currentCaveinTickCount;
            }
        }
    }


    private static void doCavein(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            for(int iex=0; iex<entitiesIndex; iex++){
                if(entities[iex] != null && entities[iex].isRemoved()){
                    entities[iex].playSound(SoundEvents.STONE_FALL);
                    entities[iex] = null;
                }
            }

            long lastCaveinCheckSecond = lastCaveinTickCount / Cavein.ticksPerSecond;
            long currentCaveinCheckSecond = currentCaveinTickCount / Cavein.ticksPerSecond;

            // If cave in still happening, make blocks fall, otherwise, go back to checking
            if(currentCaveinCheckSecond-lastCaveinCheckSecond < caveinDurationSeconds && caveinBlockPositionsIndex > 0){
                Cavein.LOGGER.debug(String.valueOf(caveinDurationSeconds));

                for(int ifx=0; ifx<1; ifx++){
                    int fallingBlockIndex = random.nextInt(caveinBlockPositionsIndex);
                    BlockPos currentBlockPos = caveinBlockPositions[fallingBlockIndex];

                    FallingBlockEntity entity = FallingBlockEntity.fall(event.getServer().overworld(), currentBlockPos, event.getServer().overworld().getBlockState(currentBlockPos));
                    entity.setHurtsEntities(1.0F, 4);

                    boolean foundPosition = false;
                    for(int iex=0; iex<entitiesIndex; iex++){
                        if(entities[iex] == null){
                            entities[iex] = entity;
                            foundPosition = true;
                            break;
                        }
                    }

                    // If this doesn't work, I guess the block doesn't get sound when it drops, whatever
                    if(!foundPosition && entitiesIndex < entities.length){
                        entities[entitiesIndex] = entity;
                        entitiesIndex++;
                    }

                    // Only increase falling block height if will not exceed limit
                    BlockPos nextBlockPos = new BlockPos(currentBlockPos.getX(), currentBlockPos.getY()+1, currentBlockPos.getZ());
                    String nextBlockPosStr = event.getServer().overworld().getBlockState(nextBlockPos).getBlock().toString();
                    if(nextBlockPos.getY() < ServerConfigHandler.SERVER_CONFIG.maxCaveinYLevel.get() && checkIfWhitelisted(nextBlockPosStr)) {
                        caveinBlockPositions[fallingBlockIndex] = nextBlockPos;
                    }
                }
            }else{
                // Cave in over, stop
                currentState = STATE.CHECKING;
                lastCaveinTickCount = currentCaveinTickCount;
                sendMessageToAllPlayers(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers(), new ToClientMessageCaveinStatus(false, 0, 0, 0, 0));
            }
        }
    }
}
