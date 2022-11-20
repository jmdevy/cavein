package jmdevy.cavein.network.messages.toclient;

import jmdevy.cavein.Cavein;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToClientMessageCaveinStatus {
    // Specific to messages data (ID needs to be unique to every message server or client)
    public static final byte ID = 35;
    public static ToClientMessageCaveinStatus lastReceivedMessage = new ToClientMessageCaveinStatus(false, 0, 0, 0, 0);
    private boolean messageIsValid;

    public boolean active;
    public int caveinOriginX;
    public int caveinOriginY;
    public int caveinOriginZ;
    public int caveinRadius;



    /**
     * Called when a message is received of the appropriate type.
     * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
     */
    public static void onMessageReceived(final ToClientMessageCaveinStatus message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived != LogicalSide.CLIENT) {
            Cavein.LOGGER.warn("ToClientMessageCaveinStatus received on wrong side:" + ctx.getDirection().getReceptionSide());
            return;
        }

        if (!message.isMessageValid()) {
            Cavein.LOGGER.warn("ToClientMessageCaveinStatus was invalid" + message.toString());
            return;
        }

        ctx.enqueueWork(() -> processMessage(message));
    }

    // CALLED ON MAIN CLIENT THREAD, NOT NETWORK
    private static void processMessage(ToClientMessageCaveinStatus message) {
        lastReceivedMessage = message;
    }



    // Constructor - valid message construction - assign data, change message status
    public ToClientMessageCaveinStatus(boolean _active, int _caveinOriginX, int _caveinOriginY, int _caveinOriginZ, int _caveinRadius) {
        active = _active;
        caveinOriginX = _caveinOriginX;
        caveinOriginY = _caveinOriginY;
        caveinOriginZ = _caveinOriginZ;
        caveinRadius = _caveinRadius;
        messageIsValid = true;
    }

    // Constructor - invalid message construction - change message status since did not pass parameters
    public ToClientMessageCaveinStatus() {
        messageIsValid = false;
    }


    // Getter - return the private data for message status
    public boolean isMessageValid() {
        return messageIsValid;
    }

    /**
     * Called by the network code.
     * Once it has received the message bytes over the network.
     * Used to read the PacketBuffer contents into member variables
     * @param buf
     */
    public static ToClientMessageCaveinStatus decode(FriendlyByteBuf buf) {
        ToClientMessageCaveinStatus retval = new ToClientMessageCaveinStatus();
        try {
            retval.active = buf.readBoolean();
            retval.caveinOriginX = buf.readInt();
            retval.caveinOriginY = buf.readInt();
            retval.caveinOriginZ = buf.readInt();
            retval.caveinRadius = buf.readInt();
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            Cavein.LOGGER.warn("Exception while reading CaveinmodMessageToClient: " + e);
            return retval;
        }
        retval.messageIsValid = true;
        return retval;
    }

    /**
     * Called by the network code.
     * Used to write the contents of message member variables into
     * the PacketBuffer, ready for transmission over the network.
     * @param buf
     */
    public void encode(FriendlyByteBuf buf) {
        if (!messageIsValid) return;
        buf.writeBoolean(active);
        buf.writeInt(caveinOriginX);
        buf.writeInt(caveinOriginY);
        buf.writeInt(caveinOriginZ);
        buf.writeInt(caveinRadius);
    }

    // Allow turning message into string in a certain way
    @Override
    public String toString()  {
        return "ToClientMessageCaveinStatus[" +
                "active=" + active + "," +
                "caveinOriginX=" + caveinOriginX + "," +
                "caveinOriginY=" + caveinOriginY + "," +
                "caveinOriginZ=" + caveinOriginZ + "," +
                "caveinRadius=" + caveinRadius  + "]";
    }
}
