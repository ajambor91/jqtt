package aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers;

import aj.programming.jQTT.IncomingPacketsEncoders.Enums.MessageType;
import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.SubscribeDecoder;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.SubscribeVariableHeader;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.Abstract.AbstractTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class SubackTransformer extends AbstractTransformer implements MessageTransformer {

    @Override
    public byte[] transform(PacketsAnalyzer.PacketData packetData) throws IOException {
        int packetId = ((SubscribeVariableHeader.SubscribeVariableHeaderData )packetData.getVariableHeaderData()).getPacketIdentifier();
        byte headerHighByte = (byte) (packetId >> 8);
        byte headerLowByte = (byte) (packetId & 0xFF);
        SubscribeDecoder.SubscribeData subscribeData = (SubscribeDecoder.SubscribeData) packetData.getPayload();
        List<SubscribeDecoder.Subscription> subscriptions = subscribeData.getSubscriptions();
        byte[] qos = new byte[subscriptions.size()];

        for (int i = 0; i < subscriptions.size(); i++) {
            qos[i] = (byte) subscriptions.get(i).getQoS();
        }

        byte remainPacket = 0x03;

        byte fixedHeader = this.mqttFixedFixedHeaderEnc
                .setMessageType(MessageType.SUBACK)
                .setQoS(packetData.getFixedData().getQoSLevel().getQosLevel())
                .setRetain(packetData.getFixedData().isRetain())
                .concate();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(fixedHeader);
        byteArrayOutputStream.write(remainPacket);
        byteArrayOutputStream.write(headerHighByte);
        byteArrayOutputStream.write(headerLowByte);
        byteArrayOutputStream.write(qos);
        return byteArrayOutputStream.toByteArray();
    }
}
