package jmdevy.cavein.network;

import jmdevy.cavein.Cavein;
import jmdevy.cavein.network.messages.toclient.ToClientMessageCaveinStatus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientMessageHandler {
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";

    public static ToClientMessageCaveinStatus lastMessage = new ToClientMessageCaveinStatus(false, 0, 0, 0, 0);

    /**
     * Called when a message is received of the appropriate type.
     * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
     */
    public static void onMessageReceived(final ToClientMessageCaveinStatus message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived != LogicalSide.CLIENT) {
            Cavein.LOGGER.warn("CaveinmodMessageToClient received on wrong side:" + ctx.getDirection().getReceptionSide());
            return;
        }

        if (!message.isMessageValid()) {
            Cavein.LOGGER.warn("CaveinmodMessageToClient was invalid" + message.toString());
            return;
        }

        ctx.enqueueWork(() -> processMessage(message));
    }

    // CALLED ON MAIN CLIENT THREAD, NOT NETWORK
    private static void processMessage(ToClientMessageCaveinStatus message) {
        lastMessage = message;
//        Cavein.LOGGER.debug(String.valueOf(message.active));
    }

    public static boolean isThisProtocolAcceptedByClient(String protocolVersion) {
        return MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }
}
