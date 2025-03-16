package aj.programming.jQTT.IncomingPacketsEncoders.Enums;

public enum ConnectFlags {
    RESERVED(0x01), // Bit 0
    CLEAN_SESSION(0x02), // Bit 1
    WILL_FLAG(0x04), // Bit 2
    WILL_QOS_BIT_3(0x08), // Bit 3
    WILL_QOS_BIT_4(0x10), // Bit 4
    WILL_RETAIN(0x20), // Bit 5
    PASSWORD_FLAG(0x40), // Bit 6
    USER_NAME_FLAG(0x80); // Bit 7

    private final int value;

    ConnectFlags(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
