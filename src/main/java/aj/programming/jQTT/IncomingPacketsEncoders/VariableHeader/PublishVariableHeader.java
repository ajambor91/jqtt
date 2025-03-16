package aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader;

import aj.programming.jQTT.IncomingPacketsEncoders.Enums.QoSLevel;
import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.Utils.Incrementator;

import java.nio.charset.StandardCharsets;

public class PublishVariableHeader implements VariableHeaderDecoder {

    private int len = 0;
    private final PacketsAnalyzer.PacketData packetData;

    public PublishVariableHeader(PacketsAnalyzer.PacketData packetData) {
        this.packetData = packetData;
    }

    @Override
    public VariableHeaderData decodeHeader(byte[] packet, Incrementator offset) {
        PublishVariableHeaderData variableHeaderData = new PublishVariableHeaderData();
        return variableHeaderData
                .setTopicName(this.getTopicName(packet, offset))
                .setPacketIdentifier(this.getPacketIdentifier(packet, offset))
                .setLen(len);
    }

    private String getTopicName(byte[] packet, Incrementator offset) {
        //Add 2 bytes fot topic len
        len += 2;
        int topicNameLength = (packet[offset.getValue()] << 8) + packet[offset.incrementAndGet()];
        len += topicNameLength;
        String topicName = new String(packet, offset.incrementAndGet(), topicNameLength, StandardCharsets.UTF_8);
        offset.incrementOf(topicNameLength);
        return topicName;
    }

    private int getPacketIdentifier(byte[] packet, Incrementator offset) {
        if (packetData.getFixedData().getQoSLevel() == QoSLevel.QOS_LEVEL_0) {
            return 0;
        }
        len += 2;
        int packetIdentifier = (packet[offset.getValue()] << 8) + packet[offset.incrementAndGet()];
        offset.increment();
        return packetIdentifier;
    }

    public class PublishVariableHeaderData implements VariableHeaderData {
        private String topicName;
        private int packetIdentifier;
        private int len;

        public String getTopicName() {
            return topicName;
        }

        public PublishVariableHeaderData setTopicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public int getPacketIdentifier() {
            return packetIdentifier;
        }

        public PublishVariableHeaderData setPacketIdentifier(int packetIdentifier) {
            this.packetIdentifier = packetIdentifier;
            return this;
        }

        public int getLen() {
            return len;
        }

        public PublishVariableHeaderData setLen(int len) {
            this.len = len;
            return this;
        }
    }
}
