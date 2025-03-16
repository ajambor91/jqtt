package aj.programming.jQTT.CorePackets.FixedHeaders.Abstract;

public abstract class MqttFixedHeader {

    protected byte headerQoSByte = 0;
    protected byte headerRetainByte = 0;
    protected byte messageTopicByte = 0;


    protected enum MqttHeaderRetainFlags {
        NOT_RETAIN_FLAG((byte) 0x00),
        RETAIN_FLAG((byte) 0x01);

        private final byte mask;

        MqttHeaderRetainFlags(byte mask) {
            this.mask = mask;
        }

        public byte getMask() {
            return mask;
        }
    }

    protected enum MqttHeaderQoSFlags {
        QOS_LEVEL_0((byte) 0x00),
        QOS_LEVEL_1((byte) 0x02),
        QOS_LEVEL_2((byte) 0x04);

        private final byte mask;

        MqttHeaderQoSFlags(byte mask) {
            this.mask = mask;
        }

        public byte getMask() {
            return mask;
        }
    }

    protected enum MqttHeaderMessageTypeFlags {
        CONNECT((byte) 0x01),
        CONNACK((byte) 0x02),
        PUBLISH((byte) 0x03),
        PUBACK((byte) 0x04),
        PUBREC((byte) 0x05),
        PUBREL((byte) 0x06),
        PUBCOMP((byte) 0x07),
        SUBSCRIBE((byte) 0x08),
        SUBACK((byte) 0x09),
        UNSUBSCRIBE((byte) 0x0A),
        UNSUBACK((byte) 0x0B),
        PINGREQ((byte) 0x0C),
        PINGRESP((byte) 0x0D),
        DISCONNECT((byte) 0x0E),
        AUTH((byte) 0x0F);

        private final byte mask;

        MqttHeaderMessageTypeFlags(byte mask) {
            this.mask = mask;
        }

        public byte getMask() {
            return mask;
        }
    }
}
