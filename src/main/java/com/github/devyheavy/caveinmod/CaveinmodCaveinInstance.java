package com.github.devyheavy.caveinmod;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.chunk.Chunk;

import java.util.Vector;

// Class containing various properties and tracking values used for managing cave-ins
// in CaveinmodServerMain. This class is the instance of a cave-in and allows for multiple
// cave-ins to occur at the same time on a server
public class CaveinmodCaveinInstance {
    final private static double CAVEIN_MIN_INTENSITY = 0.1;
    final private static double CAVEIN_MAX_INTENSITY = 1.0;
    final private static int TICKS_PER_SECOND = 20;

    public double caveinIntensity;               // Defines how fast and how many blocks fall per tick, random based on CAVEIN_MIN_INTENSITY & CAVEIN_MAX_INTENSITY
    public int caveinTicksDuration;              // Number of server ticks a cave-in lasts, scales linearly and proportionally to caveinIntensity
    public Vector3d caveinOriginPosition;           // Position of this cave-in in block coords supplied by player that the cave-in was caused for

    private static int caveinBlockPerTick = 10;         // Number of blocks that fall per tick. Tied to caveinIntensity
    private static int caveinChunkRadius = 1;           // Radius of (in chunks) that the cave-in affects. Tied to caveinIntensity

    private static int currentCaveinTickCount = 0;                                              // Number of ticks cave-in has been running/processed
    private static Vector<BlockPos> cachedFallingBlockPositions = new Vector<BlockPos>();       // Cached cave-in block positions with-in chunks and height restrictions
    private static ServerPlayerEntity caveinInstancePlayer;

    public int getCurrentCaveinTickCount(){ return currentCaveinTickCount; }
    public int getCaveinTicksDuration(){ return caveinTicksDuration; }

    public CaveinmodCaveinInstance(ServerPlayerEntity _caveinInstancePlayer) {
        // Set postion of cave-in from player that had a cave-in start on them
        caveinOriginPosition = _caveinInstancePlayer.getPositionVec();

        // Preserve player object that sent the cave-in event
        caveinInstancePlayer = _caveinInstancePlayer;

        // Pick random number between hard-coded range (intensity not based on external factors)
        caveinIntensity = ((Math.random() * (CAVEIN_MAX_INTENSITY - CAVEIN_MIN_INTENSITY)) + CAVEIN_MIN_INTENSITY);

        // Max cave-in duration is 60s and lasts this long for 1.0 intense cave-ins
        caveinTicksDuration = (int) (caveinIntensity * (60 * TICKS_PER_SECOND));

        // Max number of blocks per tick is 30 for 1.0 intense cave-ins
        caveinBlockPerTick = (int) (caveinIntensity * 30);

        // Max chunk radius containing chunks affected by cave-in is 3 chunks for 1.0 intense cave-ins
        caveinChunkRadius = (int) (caveinIntensity * 3);

        // Get middle (X,Z) of the chunk the player was standing in during cave-in event
        BlockPos chunkGlobalPos = _caveinInstancePlayer.world.getChunkAt(_caveinInstancePlayer.getPosition()).getPos().asBlockPos();
        BlockPos chunkGlobalCenterPos = new BlockPos(chunkGlobalPos.getX()+8, caveinOriginPosition.y, chunkGlobalPos.getZ()+8);

        // In steps of block radius 16*N, find chunks within caveinChunkRadius (square haped)
        for(int chunkBlockRadius=0; chunkBlockRadius<16*caveinChunkRadius; chunkBlockRadius+=16){

            // Draw filled circle containing chunks: https://stackoverflow.com/a/1237519
            for(int iz=-chunkBlockRadius; iz<=chunkBlockRadius; iz++){
                for(int ix=-chunkBlockRadius; ix<=chunkBlockRadius; ix++){
                    if(ix*ix+iz*iz <= chunkBlockRadius*chunkBlockRadius){
                        Chunk currentChunk = _caveinInstancePlayer.world.getChunkAt(new BlockPos(chunkGlobalCenterPos.getX()+ix,
                                                                                                     chunkGlobalCenterPos.getY(),
                                                                                                  chunkGlobalCenterPos.getZ()+iz));

                        // Define volume that will be searched for to make blocks fall
                        int chunkGlobalPosX = currentChunk.getPos().asBlockPos().getX();
                        int chunkGlobalPosZ = currentChunk.getPos().asBlockPos().getZ();
                        int xmin = chunkGlobalPosX;
                        int xmax = chunkGlobalPosX + 16;
                        int ymin = (int) (caveinOriginPosition.y - 10);
                        int ymax = (int) (caveinOriginPosition.y + 30);
                        int zmin = chunkGlobalPosZ;
                        int zmax = chunkGlobalPosZ + 16;

                        // Take 250 random guesses within each chunk to find blocks to make fall
                        // (does not try to reach enough blocks to satisfy caveinBlockPerTick)
                        for(int i = 0; i < 256; i++){
                            int x = (int) ((Math.random() * (xmax - xmin)) + xmin);
                            int y = (int) ((Math.random() * (ymax - ymin)) + ymin);
                            int z = (int) ((Math.random() * (zmax - zmin)) + zmin);

                            Material blockToFallMaterial = currentChunk.getBlockState(new BlockPos(x,y,z)).getMaterial();
                            Material blockBelowToFallmaterial = currentChunk.getBlockState(new BlockPos(x,y-1,z)).getMaterial();

                            // Allow blocks to fall in air and also water filled caves, block letting some other blocks fall
                            if(blockToFallMaterial != Material.BARRIER && blockToFallMaterial != Material.FIRE && blockToFallMaterial != Material.LAVA &&
                            blockToFallMaterial != Material.PORTAL && blockToFallMaterial != Material.STRUCTURE_VOID) {
                                if ((blockToFallMaterial != Material.AIR && blockBelowToFallmaterial == Material.AIR) ||
                                        (blockToFallMaterial != Material.WATER && blockBelowToFallmaterial == Material.WATER)) {
                                    cachedFallingBlockPositions.add(new BlockPos(x, y, z));    // duplicate positions can be added
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    // Actually make the cave-ins make blocks fall, called from ServerMain
    public static void processCavein(){
        // Pick caveinBlockPerTick random blocks from cached and make them fall
        if(cachedFallingBlockPositions.size() > 0) {
            for (int i = 0; i < caveinBlockPerTick; i++) {
                int randomIndex = (int) (Math.random() * (cachedFallingBlockPositions.size() - 1));
                BlockPos randomBlockPos = cachedFallingBlockPositions.get(randomIndex);

                // Should check if chunk is laoded here
                Chunk currentRandomChunk = caveinInstancePlayer.world.getChunkAt(randomBlockPos);
                currentRandomChunk.setBlockState(randomBlockPos, Blocks.GRAVEL.getDefaultState(), true);

                // After a block falls, adjust the cached position to the block position above the one that fell (as long as it is not certain blocks)
                BlockPos blockAbovePos = new BlockPos(randomBlockPos.getX(), randomBlockPos.getY() + 1, randomBlockPos.getZ());
                Material blockAboveMat = currentRandomChunk.getBlockState(blockAbovePos).getMaterial();

                if(blockAboveMat != Material.AIR && blockAboveMat != Material.WATER) {
                    cachedFallingBlockPositions.set(randomIndex, blockAbovePos);
                }
            }
        }

        currentCaveinTickCount++;   // track how many times this cave-in has been processed
    }
}
