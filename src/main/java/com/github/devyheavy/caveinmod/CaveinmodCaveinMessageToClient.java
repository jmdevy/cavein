package com.github.devyheavy.caveinmod;

import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CaveinmodCaveinMessageToClient {
    private boolean messageIsValid;                                 // Is the received message OK?
    private static final Logger LOGGER = LogManager.getLogger();    // For outputting errors to terminal/console

    // Client needs to know position and intensity
    private static double caveinIntensity;
    private static double caveinPositionX;
    private static double caveinPositionY;
    private static double caveinPositionZ;
    private static int caveinUniqueIndexID;  // same ID used on client and server so server can tell client to remove it later

    // Mostly used to retrieve values from this packet and storing in
    // client-side classes like CaveinmodClientMain when handled in CaveinmodMessagehandlerOnCLient
    public double getCaveinIntensity() { return caveinIntensity; }
    public double getCaveinPositionX() { return caveinPositionX; }
    public double getCaveinPositionY() { return caveinPositionY; }
    public double getCaveinPositionZ() { return caveinPositionZ; }
    public int getCaveinUniqueIndexID(){ return caveinUniqueIndexID; }


    // Constructor - valid message construction - assign data, change message status
    public CaveinmodCaveinMessageToClient(double _caveinIntensity, double _caveinPositionX, double _caveinPositionY, double _caveinPositionZ, int _caveinUniqueIndexID) {
        caveinIntensity = _caveinIntensity;
        caveinPositionX = _caveinPositionX;
        caveinPositionY = _caveinPositionY;
        caveinPositionZ = _caveinPositionZ;
        caveinUniqueIndexID = _caveinUniqueIndexID;
        messageIsValid = true;
    }


    // Constructor - invalid message construction - change message status since did not pass parameters
    public CaveinmodCaveinMessageToClient() {
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
    public static CaveinmodCaveinMessageToClient decode(PacketBuffer buf) {
        CaveinmodCaveinMessageToClient retval = new CaveinmodCaveinMessageToClient();
        try {
            retval.caveinIntensity = buf.readDouble();
            retval.caveinPositionX = buf.readDouble();
            retval.caveinPositionY = buf.readDouble();
            retval.caveinPositionZ = buf.readDouble();
            retval.caveinUniqueIndexID = buf.readInt();
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
        buf.writeDouble(caveinIntensity);
        buf.writeDouble(caveinPositionX);
        buf.writeDouble(caveinPositionY);
        buf.writeDouble(caveinPositionZ);
        buf.writeDouble(caveinUniqueIndexID);
    }


    // Allow turning message into string in a certain way
    @Override
    public String toString()  {
        return "CaveinmodMessageToClient[updateRateTicks=" + caveinIntensity + "," +
                "caveinPositionX=" + caveinPositionX + "," +
                "caveinPositionY=" + caveinPositionY + "," +
                "caveinPositionZ=" + caveinPositionZ + "," +
                "caveinUniqueIndexID=" + caveinUniqueIndexID
                + "]";
    }
}
