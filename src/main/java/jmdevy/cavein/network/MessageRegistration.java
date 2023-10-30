package jmdevy.cavein.network;

import jmdevy.cavein.Cavein;
import jmdevy.cavein.network.messages.toclient.ToClientMessageShake;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.ChannelBuilder;

import java.util.Optional;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;


@Mod.EventBusSubscriber(modid = Cavein.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MessageRegistration {
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";
    public static final String CLIENT_MESSAGE_PROTOCOL_VERSION = "1.0";
    public static final String SERVER_MESSAGE_PROTOCOL_VERSION = "1.0";

    public static SimpleChannel channel;
    public static final ResourceLocation channelResourceLocation = new ResourceLocation("cavein", "mbechannel");

    @SubscribeEvent
    public static void onPacketHandlerSetupEvent(FMLCommonSetupEvent event) {

        channel = ChannelBuilder
                .named(channelResourceLocation)
                .simpleChannel();

        channel.messageBuilder(ToClientMessageShake.class)
                .encoder(ToClientMessageShake::encode)
                .decoder(ToClientMessageShake::decode);

//        // Make the channel the mod will use to talk between the server and client
//        channel = NetworkRegistry.newSimpleChannel(channelResourceLocation, () -> MESSAGE_PROTOCOL_VERSION,
//                (String messageProtocol) -> {return CLIENT_MESSAGE_PROTOCOL_VERSION.equals(messageProtocol);},
//                (String messageProtocol) -> {return SERVER_MESSAGE_PROTOCOL_VERSION.equals(messageProtocol);});
//
//        // Register type of message/packet for server -> client data transfer
//        channel.registerMessage(ToClientMessageShake.ID, ToClientMessageShake.class,
//                ToClientMessageShake::encode, ToClientMessageShake::decode,
//                ToClientMessageShake::onMessageReceived,
//                Optional.of(PLAY_TO_CLIENT));
    }
}
