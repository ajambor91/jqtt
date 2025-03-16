package aj.programming.jQTT.IncomingPacketsEncoders.Payload;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.Utils.Incrementator;

public class PublishDecoder implements PayloadDecoder {


    private final PacketsAnalyzer.PacketData packetData;

    public PublishDecoder(PacketsAnalyzer.PacketData packetData) {
        this.packetData = packetData;
    }

    @Override
    public PayloadData decodePayload(byte[] packet, Incrementator offset) {
        int payloadLen = packet.length -
                packetData.getPacketLengthCount() -
                ((PublishVariableHeader.PublishVariableHeaderData) packetData.getVariableHeaderData()).getLen() - 1;
        byte[] payload = new byte[payloadLen];
        System.arraycopy(packet, offset.getValue(), payload, 0, payloadLen);
        return new PublishData().setPayload(payload);
    }

    public class PublishData implements PayloadData {
        private byte[] payload;

        public byte[] getPayload() {
            return this.payload;
        }

        public PublishData setPayload(byte[] payload) {
            this.payload = payload;
            return this;
        }
    }
}
