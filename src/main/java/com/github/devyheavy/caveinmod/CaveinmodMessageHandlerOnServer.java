package com.github.devyheavy.caveinmod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

// Processes the packets that arrive here, at the server, and make
// tasks occur in the world for all players. NOTE: this runs in its
// own thread, need to create tasks for modifying the world
// https://github.com/TheGreyGhost/MinecraftByExample/blob/master/src/main/java/minecraftbyexample/mbe60_network_messages/MessageHandlerOnServer.java
public class CaveinmodMessageHandlerOnServer {


    //
    // Config options (MOVE TO JSON)
    //
    final private static int distanceAbovePlayerToCavein = 7;   // How many blocks above a player to search for blocks to fall (respects maxCaveinYLevel)
    final private static int distanceBelowPlayerToCavein = 2;   // How many blocks below a player to search for blocks to fall (respects maxCaveinYLevel)
    final private static int maxCaveinYLevel = 60;              // The highest cave-ins can appear for any player


    private static final Logger LOGGER = LogManager.getLogger();    // For outputting errors to terminal/console


    /**
     * Called when a message is received of the appropriate type.
     * CALLED BY THE NETWORK THREAD, NOT THE SERVER THREAD
     * @param message The message
     */
    public static void onMessageReceived(final CaveinmodMessageToServer message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();                       // Init network context object
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();   // Get network data travel direction
        ctx.setPacketHandled(true);                                         // Let network context packet was handled

        // Do error checking if packet is messed up or packet was sent as if this were a client
        if (sideReceived != LogicalSide.SERVER) {
            LOGGER.warn("CaveinmodMessageToServer received on wrong side:" + ctx.getDirection().getReceptionSide());
            return;
        }
        if (!message.isMessageValid()) {
            LOGGER.warn("CaveinmodMessageToServer was invalid" + message.toString());
            return;
        }

        // Assume ServerPlayerEntity always exists
        final ServerPlayerEntity sendingPlayer = ctx.getSender();
        if (sendingPlayer == null) {
            LOGGER.warn("EntityPlayerMP(OLD)/ServerPlayerEntity was null when CaveinmodMessageToServer was received");
        }

        // The function that modifies the Minecraft world is done
        // on the main thread through a task (DO NOT DO THAT HERE!)
        ctx.enqueueWork(() -> processMessage(message, sendingPlayer));
    }


    // This message is called from the Server thread (not the network thread like onMessageReceived)
    static void processMessage(CaveinmodMessageToServer message, ServerPlayerEntity sendingPlayer) {
        World sendingPlayerWorld = sendingPlayer.world;
        Chunk sendingPlayerChunk = sendingPlayerWorld.getChunkAt(sendingPlayer.getPosition());
        int chunkGlobalPosX = sendingPlayerChunk.getPos().asBlockPos().getX();
        int chunkGlobalPosZ = sendingPlayerChunk.getPos().asBlockPos().getZ();

        // Define volume that will be searched for to make blocks fall
        int xmin = chunkGlobalPosX;
        int xmax = chunkGlobalPosX + 16;
        int ymin = sendingPlayer.getPosition().getY() - distanceBelowPlayerToCavein;
        int ymax = sendingPlayer.getPosition().getY() + distanceAbovePlayerToCavein;
        int zmin = chunkGlobalPosZ;
        int zmax = chunkGlobalPosZ + 16;

        for(int i = 0; i < 1000; i++){
            int x = (int) ((Math.random() * (xmax - xmin)) + xmin);
            int y = (int) ((Math.random() * (ymax - ymin)) + ymin);
            int z = (int) ((Math.random() * (zmax - zmin)) + zmin);

            Material blockToFallMaterial = sendingPlayerChunk.getBlockState(new BlockPos(x,y,z)).getMaterial();
            Material blockBelowToFallmaterial = sendingPlayerChunk.getBlockState(new BlockPos(x,y-1,z)).getMaterial();

            if(blockToFallMaterial != Material.AIR && blockBelowToFallmaterial == Material.AIR){
                sendingPlayerChunk.setBlockState(new BlockPos(x,y,z), Blocks.GRAVEL.getDefaultState(), true);
                System.out.println("Falling!");
            }

            //System.out.println(sendingPlayerChunk.getBlockState(new BlockPos(x,y,z)).getBlock().getNameTextComponent().getString());
        }
        return;
    }


    // Special function for checking network protocal versions
    public static boolean isThisProtocolAcceptedByServer(String protocolVersion) {
        return CaveinmodPacketHandler.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }
}
