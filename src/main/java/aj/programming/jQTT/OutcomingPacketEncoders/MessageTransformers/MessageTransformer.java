package aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;

import java.io.IOException;

public interface MessageTransformer {
    byte[] transform(PacketsAnalyzer.PacketData packetData) throws IOException;
}
