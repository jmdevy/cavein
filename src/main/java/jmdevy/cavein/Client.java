package jmdevy.cavein;

import jmdevy.cavein.network.messages.toclient.ToClientMessageCaveinStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


// Each client potentially tells the server to start a cave-in
@Mod.EventBusSubscriber(modid = Cavein.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Client {
    private static double relativeShakeMovement;

    Client(){
//        relativeShakeMovement = ServerConfigHandler.SERVER_CONFIG.relativeShakeMovement.get();
    }

    // Varies pitch, yaw, and position based on the intensity of the cave in
    public static void shakePlayer() {
        float shakeX = (float) ((Math.random() * (0.5f + 0.5f)) - 0.5f);
        float shakeZ = (float) ((Math.random() * (0.5f + 0.5f)) - 0.5f);

        Minecraft.getInstance().player.moveRelative(0.05f, new Vec3(shakeX, 0, shakeZ));
        // Makes the player sick to move the pitch and yaw
//        Minecraft.getInstance().player.setXRot(Minecraft.getInstance().player.getXRot() + shakeX);
//        Minecraft.getInstance().player.setYRot(Minecraft.getInstance().player.getYRot() + shakeZ);
//        Minecraft.getInstance().level.playL
    }

    @SubscribeEvent
    public static void update(TickEvent.ClientTickEvent event) {
//        Cavein.LOGGER.debug(String.valueOf(ServerConfigHandler.SERVER_CONFIG.relativeShakeMovement.get()));
        // If the last cave in message was an active cave in, shake player and play sound
        if(ToClientMessageCaveinStatus.lastReceivedMessage.active){
            shakePlayer();
        }
    }
}
