package aj.programming.jQTT.IncomingPacketsEncoders.FixedHeaders;

import aj.programming.jQTT.IncomingPacketsEncoders.Enums.MessageType;
import aj.programming.jQTT.IncomingPacketsEncoders.Enums.QoSLevel;
import aj.programming.jQTT.CorePackets.FixedHeaders.Abstract.MqttFixedHeader;

public final class MqttFixedFixedHeaderDec extends MqttFixedHeader {

    public FixedHeaderData decode(byte b) {
        FixedHeaderData fixedHeaderData = new FixedHeaderData();
        return fixedHeaderData
                .setMessageType(extractMessageType(b))
                .setRetain(extractRetain(b))
                .setQoSLevel(extractQoS(b));
    }

    private MessageType extractMessageType(byte header) {
        int unsignedByte = (header & 0xFF);
        int type = (unsignedByte >> 4) & 0xFF;
        if (type == MqttHeaderMessageTypeFlags.CONNECT.getMask()) {
            return MessageType.CONNECT;
        } else if (type == MqttHeaderMessageTypeFlags.PUBLISH.getMask()) {
            return MessageType.PUBLISH;
        } else if (type == MqttHeaderMessageTypeFlags.SUBSCRIBE.getMask()) {
            return MessageType.SUBSCRIBE;
        } else if (type == MqttHeaderMessageTypeFlags.DISCONNECT.getMask()) {
            return MessageType.DISCONNECT;
        } else if (type == MqttHeaderMessageTypeFlags.CONNACK.getMask()) {
            return MessageType.CONNACK;
        } else if (type == MqttHeaderMessageTypeFlags.PUBACK.getMask()) {
            return MessageType.PUBACK;
        } else if (type == MqttHeaderMessageTypeFlags.PUBREC.getMask()) {
            return MessageType.PUBREC;
        } else if (type == MqttHeaderMessageTypeFlags.PUBREL.getMask()) {
            return MessageType.PUBREL;
        } else if (type == MqttHeaderMessageTypeFlags.PUBCOMP.getMask()) {
            return MessageType.PUBCOMP;
        } else if (type == MqttHeaderMessageTypeFlags.SUBACK.getMask()) {
            return MessageType.SUBACK;
        } else if (type == MqttHeaderMessageTypeFlags.UNSUBSCRIBE.getMask()) {
            return MessageType.UNSUBSCRIBE;
        } else if (type == MqttHeaderMessageTypeFlags.UNSUBACK.getMask()) {
            return MessageType.UNSUBACK;
        } else if (type == MqttHeaderMessageTypeFlags.PINGREQ.getMask()) {
            return MessageType.PINGREQ;
        } else if (type == MqttHeaderMessageTypeFlags.PINGRESP.getMask()) {
            return MessageType.PINGRESP;
        } else if (type == MqttHeaderMessageTypeFlags.AUTH.getMask()) {
            return MessageType.AUTH;
        } else {
            return null;
        }
    }

    private boolean extractRetain(byte header) {

        return (header & 0x01) == 1;
    }

    private QoSLevel extractQoS(byte header) {

        int result = (header & 0x06) >> 1;

        if (result == QoSLevel.QOS_LEVEL_1.getQosLevel()) {
            return QoSLevel.QOS_LEVEL_1;
        } else if (result == QoSLevel.QOS_LEVEL_2.getQosLevel()) {
            return QoSLevel.QOS_LEVEL_2;
        } else {
            return QoSLevel.QOS_LEVEL_0;
        }
    }

    public static class FixedHeaderData {
        private boolean isRetain;
        private QoSLevel qoSLevel;
        private MessageType messageType;

        public boolean isRetain() {
            return isRetain;
        }

        public FixedHeaderData setRetain(boolean retain) {
            isRetain = retain;
            return this;
        }

        public QoSLevel getQoSLevel() {
            return qoSLevel;
        }

        public FixedHeaderData setQoSLevel(QoSLevel qoSLevel) {
            this.qoSLevel = qoSLevel;
            return this;
        }

        public MessageType getMessageType() {
            return messageType;
        }

        public FixedHeaderData setMessageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }
    }

}
