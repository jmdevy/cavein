package com.github.devyheavy.caveinmod;

import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CaveinmodMessageToServer {


    public boolean startServerCavein;                              // Data sent when cave-in is to start at sending player's position
    private boolean messageIsValid;                                 // Is the received message OK?
    private static final Logger LOGGER = LogManager.getLogger();    // For outputting errors to terminal/console


    // Constructor - valid message construction - assign data, change message status
    public CaveinmodMessageToServer(boolean i_startServerCavein) {
        startServerCavein = i_startServerCavein;
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
            retval.startServerCavein = buf.readBoolean(); ;                             // Store in member of this class
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            LOGGER.warn("Exception while reading CaveinmodMessageToServer: " + e);  // Output error if needed
            return retval;
        }
        retval.messageIsValid = true;                                                   // Now message construction is valid
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
        buf.writeBoolean(startServerCavein);
    }


    // Allow turning message into string in a certain way
    @Override
    public String toString()  {
        return "CaveinmodMessageToServer[startServerCavein=" + startServerCavein + "]";
    }
}
