package aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers;

import aj.programming.jQTT.IncomingPacketsEncoders.Enums.MessageType;
import aj.programming.jQTT.IncomingPacketsEncoders.FixedHeaders.MqttFixedFixedHeaderEnc;
import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.Abstract.AbstractTransformer;

public class ConnackTransformer extends AbstractTransformer implements MessageTransformer {

    @Override
    public byte[] transform(PacketsAnalyzer.PacketData packetData) {

        byte remainPacket = 0x02;
        byte sessionPresent = 0x00;
        byte sessionAccept = 0x00;
        byte fixedHeader = this.mqttFixedFixedHeaderEnc
                .setMessageType(MessageType.CONNACK)
                .setQoS(packetData.getFixedData().getQoSLevel().getQosLevel())
                .setRetain(packetData.getFixedData().isRetain())
                .concate();
        return new byte[] {
            fixedHeader,
                remainPacket,
                sessionPresent,
                sessionAccept
        };
    }
}
