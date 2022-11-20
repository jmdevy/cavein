package jmdevy.cavein.network;

import jmdevy.cavein.Cavein;
import jmdevy.cavein.network.messages.toclient.ToClientMessageCaveinStatus;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;


@Mod.EventBusSubscriber(modid = Cavein.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MessageRegistration {
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";

    public static SimpleChannel channel;
    public static final ResourceLocation channelResourceLocation = new ResourceLocation("cavein", "mbechannel");

    @SubscribeEvent
    public static void onPacketHandlerSetupEvent(FMLCommonSetupEvent event) {
        // Make the channel the mod will use to talk between the server and client
        channel = NetworkRegistry.newSimpleChannel(channelResourceLocation, () -> MESSAGE_PROTOCOL_VERSION,
                ClientMessageHandler::isThisProtocolAcceptedByClient,
                ServerMessageHandler::isThisProtocolAcceptedByServer);

        // Register type of message/packet for server -> client data transfer
        channel.registerMessage(ToClientMessageCaveinStatus.ID, ToClientMessageCaveinStatus.class,
                ToClientMessageCaveinStatus::encode, ToClientMessageCaveinStatus::decode,
                ClientMessageHandler::onMessageReceived,
                Optional.of(PLAY_TO_CLIENT));
//
//        // Register type of message/packet for client -> server data transfer
//        channel.registerMessage(CAVEIN_TO_SERVER_MESSAGE_ID, CaveinmodMessageToServer.class,
//                CaveinmodMessageToServer::encode, CaveinmodMessageToServer::decode,
//                CaveinmodMessageHandlerOnServer::onMessageReceived,
//                Optional.of(PLAY_TO_SERVER));
    }
}
