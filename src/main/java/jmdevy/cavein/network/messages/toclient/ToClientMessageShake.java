package jmdevy.cavein.network.messages.toclient;

import jmdevy.cavein.Cavein;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToClientMessageShake {
    // Specific to messages data (ID needs to be unique to every message server or client)
    public static final byte ID = 36;
    private boolean messageIsValid;

    public boolean shake;
    public double shakeAmount;



    /**
     * Called when a message is received of the appropriate type.
     * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
     */
    public static void onMessageReceived(final ToClientMessageShake message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived != LogicalSide.CLIENT) {
            Cavein.LOGGER.warn("ToClientMessageShake received on wrong side:" + ctx.getDirection().getReceptionSide());
            return;
        }

        if (!message.isMessageValid()) {
            Cavein.LOGGER.warn("ToClientMessageShake was invalid" + message.toString());
            return;
        }

        ctx.enqueueWork(() -> processMessage(message));
    }

    // CALLED ON MAIN CLIENT THREAD, NOT NETWORK
    private static void processMessage(ToClientMessageShake message) {
        if(message.shake) {
            double shakeAmount = message.shakeAmount;
            float shakeX = (float) ((Math.random() * (shakeAmount + shakeAmount)) - shakeAmount);
            float shakeZ = (float) ((Math.random() * (shakeAmount + shakeAmount)) - shakeAmount);
            Minecraft.getInstance().player.moveRelative(0.05f, new Vec3(shakeX, 0, shakeZ));
        }
    }

    // Constructor - valid message construction - assign data, change message status
    public ToClientMessageShake(boolean _shake, double _shakeAmount) {
        shake = _shake;
        shakeAmount = _shakeAmount;
        messageIsValid = true;
    }

    // Constructor - invalid message construction - change message status since did not pass parameters
    public ToClientMessageShake() {
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
    public static ToClientMessageShake decode(FriendlyByteBuf buf) {
        ToClientMessageShake retval = new ToClientMessageShake();
        try {
            retval.shake = buf.readBoolean();
            retval.shakeAmount = buf.readDouble();
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            Cavein.LOGGER.warn("Exception while reading ToClientMessageShake: " + e);
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
        buf.writeBoolean(shake);
        buf.writeDouble(shakeAmount);
    }

    // Allow turning message into string in a certain way
    @Override
    public String toString()  {
        return "ToClientMessageShake[" +
                "shake=" + shake +
                "shakeAmount=" + shakeAmount +
                "]";
    }
}
