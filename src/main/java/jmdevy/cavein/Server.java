package jmdevy.cavein;

import jmdevy.cavein.config.CommonConfigHandler;
import jmdevy.cavein.network.MessageRegistration;
import jmdevy.cavein.network.messages.toclient.ToClientMessageShake;
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
import java.util.Vector;

@Mod.EventBusSubscriber(modid = Cavein.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class Server {
    private static Random random = new Random(System.currentTimeMillis());

    private static long lastCaveinTickCount = 0;    // General timer
    private static long currentCaveinTickCount = 0; // General timer

    private static List<FallingBlockEntity> entities = new Vector<FallingBlockEntity>();

    private static Vec3 caveinOrigin;
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
            String whitelistedBlocksStr = CommonConfigHandler.COMMON_CONFIG.whitelistedBlocks.get();
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
            if(currentCaveinCheckSecond-lastCaveinCheckSecond > CommonConfigHandler.COMMON_CONFIG.secondsToCavein.get()){

                // Randomly check if cave in should start, if so, change state and save some random values for later usage
                if((int)(Math.random() * CommonConfigHandler.COMMON_CONFIG.caveinChance.get()) == 1){
                    List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();

                    // Only actually cave in if there is at least one player
                    if(players.size() > 0) {
                        currentState = STATE.CAVING;

                        int minSeconds = CommonConfigHandler.COMMON_CONFIG.minCaveinDurationSeconds.get();
                        int maxSeconds = CommonConfigHandler.COMMON_CONFIG.maxCaveinDurationSeconds.get();

                        // Generate random cave in attribute
                        caveinDurationSeconds = minSeconds + random.nextInt(maxSeconds - minSeconds + 1);

                        ServerPlayer caveinPlayer = players.get(random.nextInt(players.size()));
                        caveinOrigin = caveinPlayer.position();
                    }
                }

                // Update this so next time can try track how long it's been (also used as cave in start time if state changed above)
                lastCaveinTickCount = currentCaveinTickCount;
            }
        }
    }


    private static void doCavein(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            // Go through and play sounds for blocks that landed, remove them too
            for(int iex=0; iex<entities.size(); iex++){
                FallingBlockEntity entity = entities.get(iex);
                if(entity.isRemoved()){
                    entity.playSound(SoundEvents.STONE_FALL);
                    entities.remove(iex);

                    // Check if any player is within range to be shaken by block falling
                    List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                    for(int ipx=0; ipx<players.size(); ipx++) {
                        ServerPlayer player = players.get(ipx);
                        double distance = player.distanceTo(entity);
                        Cavein.LOGGER.debug(String.valueOf(distance));
                        if(distance < 10.0){
                            MessageRegistration.channel.send(PacketDistributor.PLAYER.with(() -> player), new ToClientMessageShake(true, CommonConfigHandler.COMMON_CONFIG.relativeShakeAmount.get()));
                        }
                    }
                }
            }

            long lastCaveinCheckSecond = lastCaveinTickCount / Cavein.ticksPerSecond;
            long currentCaveinCheckSecond = currentCaveinTickCount / Cavein.ticksPerSecond;

            // If cave in still happening, make blocks fall, otherwise, go back to checking
            if(currentCaveinCheckSecond-lastCaveinCheckSecond < caveinDurationSeconds){

                // Generate random point in circle
                double r = CommonConfigHandler.COMMON_CONFIG.caveinRadius.get() * Math.sqrt(random.nextFloat());
                double theta = random.nextFloat() * 2 * Math.PI;
                int x = (int)(caveinOrigin.x + r * Math.cos(theta));
                int y = ((int)caveinOrigin.y) - CommonConfigHandler.COMMON_CONFIG.caveinHeightBelow.get();
                int z = (int)(caveinOrigin.z + r * Math.sin(theta));

                // Search bottom to top of cave in circle for blocks that have air or water under them
                for(int iyx=0; iyx<CommonConfigHandler.COMMON_CONFIG.caveinHeightBelow.get()+CommonConfigHandler.COMMON_CONFIG.caveinHeightAbove.get(); iyx++){
                    if(iyx+y < CommonConfigHandler.COMMON_CONFIG.maxCaveinYLevel.get()) {
                        BlockPos block = new BlockPos(x, iyx + y, z);
                        String blockStr = event.getServer().overworld().getBlockState(block).getBlock().toString();

                        BlockPos blockBelow = new BlockPos(x, iyx + y - 1, z);
                        Material blockBelowMat = event.getServer().overworld().getBlockState(blockBelow).getMaterial();

                        if (checkIfWhitelisted(blockStr) && (blockBelowMat == Material.AIR || blockBelowMat == Material.WATER)) {
                            FallingBlockEntity entity = FallingBlockEntity.fall(event.getServer().overworld(), block, event.getServer().overworld().getBlockState(block));
                            entities.add(entity);
                            break;
                        }
                    }else{
                        // Went above limit, stop searching
                        break;
                    }
                }
            }else{
                // Cave in over, stop
                currentState = STATE.CHECKING;
                lastCaveinTickCount = currentCaveinTickCount;
            }
        }
    }
}
