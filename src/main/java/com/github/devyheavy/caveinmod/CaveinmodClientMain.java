package com.github.devyheavy.caveinmod;


import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;            // @EventBusSubscriber
import net.minecraftforge.eventbus.api.SubscribeEvent;                  // @SubscribeEvent

// NOTE: -Mod-specific events like Registry events and Mod Loading events are fired on the 'MOD' event bus
//       -Non-mod-specific events such as Tick events, Rendering events, World events and Player events are fired on the 'FORGE' event bus.
//       *Only events (i.e. objects whose class extends net.minecraftforge.eventbus.api.Event) can be listened for/subscribed to on the Event Buses.
@EventBusSubscriber(modid = CaveinmodMain.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class CaveinmodClientMain {

    // Internal items used for for tracking
    private static int currentTicksToCavein = 0;                // Number randomly assigned to number in range of minTicksToCavein & maxTicksToCavein
    private static int ticksPassedToCavein = -1;                // Related to minTicksToCavein & maxTicksToCavein and is used to count to random number in that range
    private static boolean isScreenCurrentlyShaking = false;    // When set true, player eye position shakes relative to head origin
    final static private float maxCameraPitchVariation = 0.75f;
    final static private float minCameraPitchVariation = -0.75f;
    final static private float maxCameraYawVariation = 0.75f;
    final static private float minCameraYawVariation = -0.75f;


    // Varies pitch, yaw, and position based on the intensity of the cave-in
    public static void CaveinmodShakePlayer() {
        float addedPitch = (float) ((Math.random() * (maxCameraPitchVariation - minCameraPitchVariation)) + minCameraPitchVariation);
        float addedYaw = (float) ((Math.random() * (maxCameraYawVariation - minCameraYawVariation)) + minCameraYawVariation);
        Minecraft.getInstance().player.rotationPitch += addedPitch;
        Minecraft.getInstance().player.rotationYaw += addedYaw;
        Minecraft.getInstance().player.moveRelative(0.05f, new Vector3d(addedPitch, 0, addedYaw));
    }


    @SubscribeEvent
    public static void CaveinmodUpdate(TickEvent.ClientTickEvent event) {
        // Don't do anything if the player does not exist since user is in main menu of Minecraft
        if(Minecraft.getInstance().player == null) {
            return;
        }
        //CaveinmodShakePlayer();
        // Only update ticks at end of phase (between START & END a snap-back due to poor TPS can happen)
        // and this ensures the blocks are the most up to date when picking ones to fall
        if(event.phase == TickEvent.Phase.END) {
            // Chance of cave-in can occur now
            if(ticksPassedToCavein >= currentTicksToCavein){
                // Last check, only do cave-ins if player is below max limit
                if(Minecraft.getInstance().player.getPosition().getY() < CaveinmodConfigHandler.maxCaveinYLevel) {
                    CaveinmodMessageToServer caveinMessageToServer = new CaveinmodMessageToServer(true);
                    CaveinmodPacketHandler.simpleChannel.sendToServer(caveinMessageToServer);
                    currentTicksToCavein = (int) ((Math.random() * (CaveinmodConfigHandler.maxTicksToCavein - CaveinmodConfigHandler.minTicksToCavein)) + CaveinmodConfigHandler.minTicksToCavein);  // New amount of ticks until cave-in chance
                    ticksPassedToCavein = 0;    // Cave-in had chance to occur, reset
                }
            }
            ticksPassedToCavein++;  // Reset every currentTicksToCavein but depends on ticksPassedToUpdate
        }
    }
}
