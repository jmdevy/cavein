package jmdevy.cavein.network;

public class ServerMessageHandler {
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";

    public static boolean isThisProtocolAcceptedByServer(String protocolVersion) {
        return MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }
}
