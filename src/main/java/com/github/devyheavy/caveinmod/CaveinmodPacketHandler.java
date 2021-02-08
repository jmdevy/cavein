package com.github.devyheavy.caveinmod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;            // @EventBusSubscriber
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;

// https://github.com/TheGreyGhost/MinecraftByExample/blob/master/src/main/java/minecraftbyexample/mbe60_network_messages/StartupCommon.java
// https://mcforge.readthedocs.io/en/1.15.x/networking/simpleimpl/
@EventBusSubscriber(modid = CaveinmodMain.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CaveinmodPacketHandler {


    public static final byte CAVEIN_TO_SERVER_MESSAGE_ID = 35;     // Unique ID for this message type (don't use 0, easier to detect errors this way)
    public static final byte CAVEIN_TO_CLIENT_MESSAGE_ID = 36;     // Unique ID for this message type (don't use 0, easier to detect errors this way)
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";    // A version number protocol, can be used to maintain backward compatibility


    public static SimpleChannel simpleChannel;    // Used to transmit network messages (packets)
    public static final ResourceLocation simpleChannelRL = new ResourceLocation("caveinmod", "mbechannel");


    // Register a channel for packet
    @SubscribeEvent
    public static void onPacketHandlerSetupEvent(FMLCommonSetupEvent event) {

        // This mod does not implment a server -> client channel, just client -> server
        simpleChannel = NetworkRegistry.newSimpleChannel(simpleChannelRL, () -> MESSAGE_PROTOCOL_VERSION,
                                                         CaveinmodMessageHandlerOnClient::isThisProtocolAcceptedByClient,
                                                         CaveinmodMessageHandlerOnServer::isThisProtocolAcceptedByServer);

        // Register type of message/packet for server -> client data transfer
        simpleChannel.registerMessage(CAVEIN_TO_CLIENT_MESSAGE_ID, CaveinmodCaveinMessageToClient.class,
            CaveinmodCaveinMessageToClient::encode, CaveinmodCaveinMessageToClient::decode,
            CaveinmodMessageHandlerOnClient::onMessageReceived,
            Optional.of(PLAY_TO_CLIENT));

        // Register type of message/packet for client -> server data transfer
        simpleChannel.registerMessage(CAVEIN_TO_SERVER_MESSAGE_ID, CaveinmodMessageToServer.class,
            CaveinmodMessageToServer::encode, CaveinmodMessageToServer::decode,
            CaveinmodMessageHandlerOnServer::onMessageReceived,
            Optional.of(PLAY_TO_SERVER));
    }
}
