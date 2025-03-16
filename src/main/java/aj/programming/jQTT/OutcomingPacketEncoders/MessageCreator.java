package aj.programming.jQTT.OutcomingPacketEncoders;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.MessageTransformer;

import java.io.IOException;

public class MessageCreator {
    private PacketsAnalyzer.PacketData packetData;
    private MessageTransformer messageTransformer;

    public MessageCreator(PacketsAnalyzer.PacketData packetData, MessageTransformer messageTransformer) {
        this.packetData = packetData;
        this.messageTransformer = messageTransformer;
    }

    public byte[] getMessage() throws IOException {
        return this.messageTransformer.transform(this.packetData);
    }
}
