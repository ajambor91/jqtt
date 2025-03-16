package aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.PublishDecoder;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.Abstract.AbstractTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PublishTransformer extends AbstractTransformer implements MessageTransformer {

    @Override
    public byte[] transform(PacketsAnalyzer.PacketData packetData) {
        try {
            byte fixedHeader = buildFixedHeader(packetData);

            byte[] variableHeader = buildVariableHeader(packetData);

            byte[] payload = buildPayload(packetData);

            int remainingLength = variableHeader.length + payload.length;
            byte[] encodedRemainingLength = encodeRemainingLength(remainingLength);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(fixedHeader);
            outputStream.write(encodedRemainingLength);
            outputStream.write(variableHeader);
            outputStream.write(payload);

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to build PUBLISH packet", e);
        }
    }

    private byte buildFixedHeader(PacketsAnalyzer.PacketData packetData) {
        return this.mqttFixedFixedHeaderEnc
                .setMessageType(packetData.getFixedData().getMessageType())
                .setQoS(packetData.getFixedData().getQoSLevel().getQosLevel())
                .setRetain(packetData.getFixedData().isRetain())
                .concate();
    }

    private byte[] buildVariableHeader(PacketsAnalyzer.PacketData packetData) {
        ByteArrayOutputStream headerStream = new ByteArrayOutputStream();

        PublishVariableHeader.PublishVariableHeaderData variableHeader =
                (PublishVariableHeader.PublishVariableHeaderData) packetData.getVariableHeaderData();
        String topicName = variableHeader.getTopicName();
        byte[] topicBytes = topicName.getBytes(StandardCharsets.UTF_8);

        headerStream.write((topicBytes.length >> 8) & 0xFF);
        headerStream.write(topicBytes.length & 0xFF);

        headerStream.write(topicBytes, 0, topicBytes.length);

        if (packetData.getFixedData().getQoSLevel().getQosLevel() > 0) {
            int packetId = variableHeader.getPacketIdentifier();
            headerStream.write((packetId >> 8) & 0xFF);
            headerStream.write(packetId & 0xFF);
        }

        return headerStream.toByteArray();
    }

    private byte[] buildPayload(PacketsAnalyzer.PacketData packetData) {
        PublishDecoder.PublishData publishData = (PublishDecoder.PublishData) packetData.getPayload();
        String payload = new String(publishData.getPayload(), StandardCharsets.UTF_8);
        return payload.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] encodeRemainingLength(int value) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        do {
            byte encodedByte = (byte) (value % 128);
            value /= 128;
            if (value > 0) {
                encodedByte |= 0x80;
            }
            outputStream.write(encodedByte);
        } while (value > 0);
        return outputStream.toByteArray();
    }
}