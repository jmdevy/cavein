package com.github.devyheavy.caveinmod;

import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class CaveinmodMessageHandlerOnClient {


    /**
     * Called when a message is received of the appropriate type.
     * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
     */
    public static void onMessageReceived(final CaveinmodCaveinMessageToClient message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived != LogicalSide.CLIENT) {
            LOGGER.warn("CaveinmodMessageToClient received on wrong side:" + ctx.getDirection().getReceptionSide());
            return;
        }

        if (!message.isMessageValid()) {
            LOGGER.warn("CaveinmodMessageToClient was invalid" + message.toString());
            return;
        }

        Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent()) {
            LOGGER.warn("CaveinmodMessageToClient context could not provide a ClientWorld");
            return;
        }
        ctx.enqueueWork(() -> processMessage(clientWorld.get(), message));
    }


    private static void processMessage(ClientWorld worldClient, CaveinmodCaveinMessageToClient message) {

        return;
    }


    public static boolean isThisProtocolAcceptedByClient(String protocolVersion) {
        return CaveinmodPacketHandler.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }


    private static final Logger LOGGER = LogManager.getLogger();    // For outputting errors to terminal/console
}
