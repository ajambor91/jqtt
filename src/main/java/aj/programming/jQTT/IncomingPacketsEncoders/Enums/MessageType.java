package aj.programming.jQTT.IncomingPacketsEncoders.Enums;

public enum MessageType {
    CONNECT(0),
    CONNACK(1),
    PUBLISH(2),
    PUBACK(3),
    PUBREC(4),
    PUBREL(5),
    PUBCOMP(6),
    SUBSCRIBE(7),
    SUBACK(8),
    UNSUBSCRIBE(9),
    UNSUBACK(10),
    PINGREQ(11),
    PINGRESP(12),
    DISCONNECT(13),
    AUTH(14);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public static MessageType fromValue(int value) {
        for (MessageType type : MessageType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid MessageType value: " + value);
    }

    public int getValue() {
        return value;
    }
}
