package aj.programming.jQTT.Socket;

import aj.programming.jQTT.PacketsEncoders.Enums.MessageType;
import aj.programming.jQTT.PacketsEncoders.FixedHeaders.MqttFixedFixedHeaderEnc;
import aj.programming.jQTT.PacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.PacketsEncoders.Payload.SubscribeDecoder;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.SubscribeVariableHeader;
import aj.programming.jQTT.TopicsAggregator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
//Example publisher class to send publish message for testing
public class Publisher implements Subscriber {
    private final MqttFixedFixedHeaderEnc mqttFixedHeaderEnc;
    private final Socket socket;
    private final TopicsAggregator topicsAggregator;
    private String clientId;
    public Publisher(Socket socket, TopicsAggregator topicsAggregator) {
        this.socket = socket;
        this.topicsAggregator = topicsAggregator;
        this.mqttFixedHeaderEnc = new MqttFixedFixedHeaderEnc();
    }

    public void sendSubback(PacketsAnalyzer.PacketData packetData) {
        try {
            SubscribeVariableHeader.SubscribeVariableHeaderData headerData = (SubscribeVariableHeader.SubscribeVariableHeaderData) packetData.getVariableHeaderData();
            byte type = (byte) 0x90;

            int remainingLength = 3;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            do {
                byte encodedByte = (byte) (remainingLength % 128);
                remainingLength = remainingLength / 128;
                if (remainingLength > 0) {
                    encodedByte |= 128;
                }
                byteArrayOutputStream.write(encodedByte);
            } while (remainingLength > 0);

            byte[] remainingLengthBytes = byteArrayOutputStream.toByteArray();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(type);
            outputStream.write(remainingLengthBytes);

            outputStream.write((byte) (headerData.getPacketIdentifier() >> 8));
            outputStream.write((byte) (headerData.getPacketIdentifier() & 0xFF));

            outputStream.write(0x01);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void sendConnack() {
        try {
            byte type = this.mqttFixedHeaderEnc.setMessageType(MessageType.CONNACK).concate();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(type);
            outputStream.write(0x02);
            outputStream.write(0x00); //is present
            outputStream.write(0x00); // return code

            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void publish(PacketsAnalyzer.PacketData packetData) {
//        this.topicsAggregator.addBroker();
    }

    public void pushToSubscriber() {

    }

    @Override
    public void sendMessage(PacketsAnalyzer.PacketData packetData) {
        try {

            PublishVariableHeader.PublishVariableHeaderData publishVariableHeader =
                    (PublishVariableHeader.PublishVariableHeaderData) packetData.getVariableHeaderData();
            String topicName = publishVariableHeader.getTopicName();
            int qosLevel = packetData.getFixedData().getQoSLevel().getQosLevel();
            boolean retain = packetData.getFixedData().isRetain();

            // Fixed Header
            byte type = this.mqttFixedHeaderEnc
                    .setMessageType(MessageType.PUBLISH)
                    .setQoS(qosLevel)
                    .setRetain(retain ? 1 : 0)
                    .concate();

            // Variable Header
            ByteArrayOutputStream variableHeaderStream = new ByteArrayOutputStream();
            // Topic Name (UTF-8 encoded)
            byte[] topicNameBytes = topicName.getBytes(StandardCharsets.UTF_8);
            variableHeaderStream.write((byte) (topicNameBytes.length >> 8)); // MSB
            variableHeaderStream.write((byte) (topicNameBytes.length & 0xFF)); // LSB
            variableHeaderStream.write(topicNameBytes);

            if (qosLevel > 0) {
                int packetIdentifier = publishVariableHeader.getPacketIdentifier();
                variableHeaderStream.write((byte) (packetIdentifier >> 8)); // MSB
                variableHeaderStream.write((byte) (packetIdentifier & 0xFF)); // LSB
            }

            // Payload
            ByteArrayOutputStream payloadStream = new ByteArrayOutputStream();
//            if (payload != null) {
//                payloadStream.write(payload);
//            }

            int remainingLength = variableHeaderStream.size() + payloadStream.size();

            ByteArrayOutputStream remainingLengthStream = new ByteArrayOutputStream();
            do {
                byte encodedByte = (byte) (remainingLength % 128);
                remainingLength /= 128;
                if (remainingLength > 0) {
                    encodedByte |= 128;
                }
                remainingLengthStream.write(encodedByte);
            } while (remainingLength > 0);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(type); // Fixed Header
            outputStream.write(remainingLengthStream.toByteArray());
            outputStream.write(variableHeaderStream.toByteArray()); // Variable Header
            outputStream.write(payloadStream.toByteArray()); // Payload

            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addSubscription(SubscribeDecoder.Subscription subscription) {

    }

    @Override
    public String getClientId() {
        return this.clientId;
    }
}
