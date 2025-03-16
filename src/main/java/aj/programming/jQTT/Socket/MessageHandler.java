package aj.programming.jQTT.Socket;

import aj.programming.jQTT.Client.Publisher;
import aj.programming.jQTT.Core.MessageHandlerRunnable;
import aj.programming.jQTT.Core.ThreadsManager;
import aj.programming.jQTT.IncomingPacketsEncoders.Enums.MessageType;
import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.PacketsBuffer;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.ConnectDecoder;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.SubscribeVariableHeader;
import aj.programming.jQTT.Topics.TopicsAggregator;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class MessageHandler implements MessageHandlerRunnable {
    private final Logger logger = Logger.getLogger(MessageHandler.class.getName());
    private final BlockingQueue<PacketsAnalyzer.PacketData> messagesQueue;
    private boolean running;
    private final PacketsAnalyzer packetsAnalyzer;
    private final Publisher publisher;
    private final TopicsAggregator topicsAggregator;
    private final PacketsBuffer packetsBuffer;
    private ThreadsManager threadsManager;
    public MessageHandler(
            Publisher publisher,
            TopicsAggregator topicsAggregator,
            PacketsBuffer packetsBuffer,
            ThreadsManager threadsManager) {
        logger.info("Creating MessageHandler");
        this.running = true;
        this.threadsManager = threadsManager;
        this.messagesQueue = new LinkedBlockingQueue<>();
        this.publisher = publisher;
        this.topicsAggregator = topicsAggregator;
        this.packetsBuffer = packetsBuffer;
        this.packetsAnalyzer = new PacketsAnalyzer(packetsBuffer, messagesQueue);
        this.threadsManager.runPacketAnalyzer(this.packetsAnalyzer);
        logger.info("MessageHandler was created");

    }

    public synchronized void stop() {
        this.running = false;
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
                    this.publisher.sendConnack(packetData);
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
                    this.publisher.sendSubback(packetData);
                } else if (packetData.getFixedData().getMessageType() == MessageType.DISCONNECT) {
//                    this.disconnect();
                }
            } catch (InterruptedException  e) {
                logger.info(String.format("MessageHandler interrupted, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
            } catch (IOException e) {
                logger.severe(String.format("Error when taking data from buffer, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));

            }
        }

    }
}
