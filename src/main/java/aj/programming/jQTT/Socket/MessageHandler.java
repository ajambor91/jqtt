package aj.programming.jQTT.Socket;

import aj.programming.jQTT.PacketsEncoders.Enums.MessageType;
import aj.programming.jQTT.PacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.PacketsEncoders.PacketsBuffer;
import aj.programming.jQTT.PacketsEncoders.Payload.ConnectDecoder;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.ConnectVariableHeader;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.SubscribeVariableHeader;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.VariableHeaderData;
import aj.programming.jQTT.TopicsAggregator;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class MessageHandler implements Runnable {
    private final Logger logger = Logger.getLogger(MessageHandler.class.getName());
    private final BlockingQueue<PacketsAnalyzer.PacketData> messagesQueue;
    private boolean running;
    private final PacketsAnalyzer packetsAnalyzer;
    private final Publisher publisher;
    private final TopicsAggregator topicsAggregator;
    private final PacketsBuffer packetsBuffer;
    private final DisconnectProvider disconnectProvider;
    public MessageHandler(
            Publisher publisher,
            TopicsAggregator topicsAggregator,
            PacketsBuffer packetsBuffer,
            DisconnectProvider disconnectProvider) {
        logger.info("Creating MessageHandler");
        this.running = true;
        this.disconnectProvider = disconnectProvider;
        this.messagesQueue = new LinkedBlockingQueue<>();
        this.publisher = publisher;
        this.topicsAggregator = topicsAggregator;
        this.packetsBuffer = packetsBuffer;
        this.packetsAnalyzer = new PacketsAnalyzer(packetsBuffer, messagesQueue);
        logger.info("MessageHandler was created");

    }

    public void handleMessage() {
        synchronized (this.packetsAnalyzer) {
            this.packetsAnalyzer.notify();
        }

    }

    @Override
    public void run() {
        while (this.running) {
            try {
                PacketsAnalyzer.PacketData packetData = this.messagesQueue.take();
                logger.info("Handled message");
                if (packetData.getFixedData().getMessageType() == MessageType.CONNECT) {
                    logger.info(String.format("Processing %s message", packetData.getFixedData().getMessageType()));
                    this.publisher.setClientId(((ConnectDecoder.ConnectData) packetData.getPayload()).getClientId());
                    this.publisher.sendConnack();
                } else if (packetData.getFixedData().getMessageType() == MessageType.PUBLISH) {
                    logger.info(String.format("Processing %s message, topic: %s, QoS: %s",
                            packetData.getFixedData().getMessageType(),
                            packetData.getFixedData().getQoSLevel(),
                            ((PublishVariableHeader.PublishVariableHeaderData) packetData.getVariableHeaderData()).getTopicName()));
                    this.topicsAggregator.publishOrAddTopicAndPublish(packetData);
                } else if (packetData.getFixedData().getMessageType() == MessageType.SUBSCRIBE) {
                    logger.info(String.format("Processing %s message, QoS: %s, packetId: %s",
                            packetData.getFixedData().getMessageType(),
                            packetData.getFixedData().getQoSLevel(),
                            ((SubscribeVariableHeader.SubscribeVariableHeaderData) packetData.getVariableHeaderData()).getPacketIdentifier()));
                    this.topicsAggregator.subscribe(packetData, this.publisher);
                } else if (packetData.getFixedData().getMessageType() == MessageType.DISCONNECT) {
                    this.disconnect();
                }
            } catch (InterruptedException e) {
                logger.warning(String.format("Error when taking data from buffer, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
            }
        }

    }

    private void disconnect() {
        synchronized (this) {
            this.running = false;

        }
        this.packetsAnalyzer.disconnect();
        this.disconnectProvider.disconnect();
    }
}
