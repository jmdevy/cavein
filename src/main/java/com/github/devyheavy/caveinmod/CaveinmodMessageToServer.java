package com.github.devyheavy.caveinmod;

import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CaveinmodMessageToServer {


    private int caveinTickDuration;                                 // Duration of cave-in, in ticks
    private boolean messageIsValid;                                 // Is the received message OK?
    private static final Logger LOGGER = LogManager.getLogger();    // For outputting errors to terminal/console


    // Constructor - valid message construction - assign data, change message status
    public CaveinmodMessageToServer(int i_caveinTickDuration) {
        caveinTickDuration = i_caveinTickDuration;
        messageIsValid = true;
    }


    // Constructor - invalid message construction - change message status since did not pass a duration parameter
    private CaveinmodMessageToServer() {
        messageIsValid = false;
    }


    // Getter - return the private data for message status
    public boolean isMessageValid() {
        return messageIsValid;
    }


    /**
     * Called by the network code.
     * once it has received the message bytes over the network.
     * Used to read the PacketBuffer contents into member variables
     * @param buf
     */
    public static CaveinmodMessageToServer decode(PacketBuffer buf) {
        CaveinmodMessageToServer retval = new CaveinmodMessageToServer();
        try {
            int tickDuration = buf.readInt();           // Get duration of cave-in
            retval.caveinTickDuration = tickDuration;   // Store in member of this class
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            LOGGER.warn("Exception while reading CaveinmodMessageToServer: " + e);  // Output error if needed
            return retval;
        }
        retval.messageIsValid = true;   // Now message construction is valid
        return retval;
    }


    /**
     * Called by the network code.
     * Used to write the contents of message member variables into
     * the PacketBuffer, ready for transmission over the network.
     * @param buf
     */
    public void encode(PacketBuffer buf) {
        if (!messageIsValid) return;
        buf.writeInt(caveinTickDuration);
    }


    // Allow turning message into string in a certain way
    @Override
    public String toString()  {
        return "CaveinmodMessageToServer[caveinTickDuration=" + caveinTickDuration + "]";
    }
}
