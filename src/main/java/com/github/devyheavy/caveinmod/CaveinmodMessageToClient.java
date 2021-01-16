package com.github.devyheavy.caveinmod;

import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CaveinmodMessageToClient {


    //
    // Items resulting from config values (see CaveinmodServerMain.java to see where sent from)
    //
    final private static int TICKS_PER_SECOND = 20;   // How many server ticks in each second (20 Hz)
    public static int updateRateTicks;                    // Number of server ticks between updates (truncated for int)
    public static int minTicksToCavein;                   // Low end of range until a cave-in can occur in ticks
    public static int maxTicksToCavein;                   // Hi end of range until a cave-in can occur in ticks
    public static int maxCaveinYLevel = 60;               // The highest cave-ins can appear for any player


    private boolean messageIsValid;                                 // Is the received message OK?
    private static final Logger LOGGER = LogManager.getLogger();    // For outputting errors to terminal/console


    //
    // Mostly used to retrieve values from this packet and storing in
    // client-side classes like CaveinmodClientMain when handled in CaveinmodMessagehandlerOnCLient
    //
    public int getUpdateRateTicks() { return updateRateTicks; }
    public int getMinTicksToCavein() {
        return minTicksToCavein;
    }
    public int getMaxTicksToCavein() {
        return maxTicksToCavein;
    }
    public int getMaxCaveinYLevel() {
        return maxCaveinYLevel;
    }


    // Constructor - valid message construction - assign data, change message status
    public CaveinmodMessageToClient(int i_updateRateTicks, int i_minTicksToCavein, int i_maxTicksToCavein, int i_maxCaveinYLevel) {
        updateRateTicks = i_updateRateTicks;
        minTicksToCavein = i_minTicksToCavein;
        maxTicksToCavein = i_maxTicksToCavein;
        maxCaveinYLevel = i_maxCaveinYLevel;
        messageIsValid = true;
    }


    // Constructor - invalid message construction - change message status since did not pass parameters
    public CaveinmodMessageToClient() {
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
    public static CaveinmodMessageToClient decode(PacketBuffer buf) {
        CaveinmodMessageToClient retval = new CaveinmodMessageToClient();
        try {
            retval.updateRateTicks = buf.readInt();
            retval.minTicksToCavein = buf.readInt();
            retval.maxTicksToCavein = buf.readInt();
            retval.maxCaveinYLevel = buf.readInt();
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            LOGGER.warn("Exception while reading CaveinmodMessageToClient: " + e);
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
    public void encode(PacketBuffer buf) {
        if (!messageIsValid) return;
        buf.writeInt(updateRateTicks);
        buf.writeInt(minTicksToCavein);
        buf.writeInt(maxTicksToCavein);
        buf.writeInt(maxCaveinYLevel);
    }

    // Allow turning message into string in a certain way
    @Override
    public String toString()  {
        return "CaveinmodMessageToClient[updateRateTicks=" + updateRateTicks + "," +
                "minTicksToCavein=" + minTicksToCavein + "," +
                "maxTicksToCavein=" + maxTicksToCavein + "," +
                "maxCaveinYLevel=" + maxCaveinYLevel + "," + "]";
    }
}
