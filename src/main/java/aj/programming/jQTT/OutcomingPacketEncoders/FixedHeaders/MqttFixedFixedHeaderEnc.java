package aj.programming.jQTT.OutcomingPacketEncoders.FixedHeaders;

import aj.programming.jQTT.Utils.ByteUtils;
import aj.programming.jQTT.CorePackets.FixedHeaders.Abstract.MqttFixedHeader;
import aj.programming.jQTT.IncomingPacketsEncoders.Enums.MessageType;

public final class MqttFixedFixedHeaderEnc extends MqttFixedHeader {

    public MqttFixedFixedHeaderEnc setRetain(int reatain) {
        if (reatain == 0) {
            headerRetainByte |= ByteUtils.setFlag(headerRetainByte, MqttHeaderRetainFlags.NOT_RETAIN_FLAG.getMask());
        } else if (reatain == 1) {
            headerRetainByte |= ByteUtils.setFlag(headerRetainByte, MqttHeaderRetainFlags.RETAIN_FLAG.getMask());
        }
        return this;
    }

    public MqttFixedFixedHeaderEnc setQoS(int qoS) {
        switch (qoS) {
            case 0 -> headerQoSByte |= ByteUtils.setFlag(headerQoSByte, MqttHeaderQoSFlags.QOS_LEVEL_0.getMask());
            case 1 -> headerQoSByte |= ByteUtils.setFlag(headerQoSByte, MqttHeaderQoSFlags.QOS_LEVEL_1.getMask());
            case 2 -> headerQoSByte |= ByteUtils.setFlag(headerQoSByte, MqttHeaderQoSFlags.QOS_LEVEL_2.getMask());
        }
        return this;
    }

    public MqttFixedFixedHeaderEnc setMessageType(MessageType messageType) {
        switch (messageType) {
            case CONNECT ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.CONNECT.getMask());
            case CONNACK ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.CONNACK.getMask());
            case PUBLISH ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.PUBLISH.getMask());
            case PUBACK ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.PUBACK.getMask());
            case PUBREC ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.PUBREC.getMask());
            case PUBREL ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.PUBREL.getMask());
            case PUBCOMP ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.PUBCOMP.getMask());
            case SUBSCRIBE ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.SUBSCRIBE.getMask());
            case SUBACK ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.SUBACK.getMask());
            case UNSUBSCRIBE ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.UNSUBSCRIBE.getMask());
            case UNSUBACK ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.UNSUBACK.getMask());
            case PINGREQ ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.PINGREQ.getMask());
            case PINGRESP ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.PINGRESP.getMask());
            case DISCONNECT ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.DISCONNECT.getMask());
            case AUTH ->
                    messageTopicByte |= ByteUtils.setFlag(messageTopicByte, MqttHeaderMessageTypeFlags.AUTH.getMask());
        }
        return this;
    }

    public byte concate() {
        byte qosByte = (byte) (headerQoSByte << 1);
        byte messageTypeBits = (byte) (messageTopicByte << 4);
        return (byte) (messageTypeBits | qosByte | headerRetainByte);
    }

}
