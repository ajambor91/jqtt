package aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader;

import aj.programming.jQTT.Utils.Incrementator;

public class SubscribeVariableHeader implements VariableHeaderDecoder {

    @Override
    public VariableHeaderData decodeHeader(byte[] packet, Incrementator offset) {
        return new SubscribeVariableHeaderData().setPacketIdentifier(this.getPacketId(packet, offset));
    }

    private int getPacketId(byte[] packet, Incrementator offset) {
        int highByte = packet[offset.getValue()] & 0xFF;
        int lowByte = packet[offset.incrementAndGet()] & 0xFF;

        return (highByte << 8) | lowByte;
    }

    public class SubscribeVariableHeaderData implements VariableHeaderData {
        private int packetIdentifier;

        public int getPacketIdentifier() {
            return packetIdentifier;
        }

        public SubscribeVariableHeaderData setPacketIdentifier(int packetIdentifier) {
            this.packetIdentifier = packetIdentifier;
            return this;
        }
    }

}
