package aj.programming.jQTT.PacketsEncoders;

import aj.programming.jQTT.PacketsEncoders.Enums.MessageType;
import aj.programming.jQTT.PacketsEncoders.FixedHeaders.MqttFixedFixedHeaderDec;
import aj.programming.jQTT.PacketsEncoders.Payload.*;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.*;
import aj.programming.jQTT.Utils.Incrementator;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class PacketsAnalyzer extends Thread {
    private final Logger logger = Logger.getLogger(PacketsAnalyzer.class.getName());

    private final MqttFixedFixedHeaderDec mqttFixedHeaderDec;
    private final VariableHeaderContext headerContext;
    private final PayloadContext payloadContext;
    private Incrementator incrementator;
    private PacketData packetData;
    private final BlockingQueue<PacketData> messagesQueue;
    private boolean running;
    private final PacketsBuffer packetsBuffer;
    private volatile boolean processingFlag;
    public PacketsAnalyzer(PacketsBuffer packetsBuffer, BlockingQueue<PacketData> messagesQueue) {
        logger.info("Creating packet analyzer");
        this.mqttFixedHeaderDec = new MqttFixedFixedHeaderDec();
        this.headerContext = new VariableHeaderContext();
        this.payloadContext = new PayloadContext();
        this.packetsBuffer = packetsBuffer;
        this.messagesQueue = messagesQueue;
        this.running = true;
        this.processingFlag = false;
        this.start();
        logger.info("Packet analyzer created");

    }

    @Override
    public void run() {
        logger.info("Packet analyzer running");

        try {
            synchronized (this) {
                this.wait();
            }
            while (running) {
                if (packetsBuffer.getSize() > 0) {
                    if (!this.processingFlag) {
                        logger.info("Analyzing new packet");

                        this.processingFlag = true;
                        this.packetData = new PacketData();
                        incrementator = new Incrementator();
                        byte b = packetsBuffer.getByte(incrementator.getAndIncrement());
                        MqttFixedFixedHeaderDec.FixedHeaderData fixedHeaderData = this.mqttFixedHeaderDec.decode(b);
                        packetData.setFixedData(fixedHeaderData);
                        logger.info("Decode and set fixed header");

                        int remainingLength = 0;
                        int remainingHeaderLen = 0;
                        int multiplier = 1;
                        while (true) {
                            remainingHeaderLen++;
                            int encodedByte = packetsBuffer.getByte(incrementator.getAndIncrement()) & 0xFF;
                            remainingLength += (encodedByte & 0x7F) * multiplier;
                            multiplier *= 128;
                            if ((encodedByte & 0x80) == 0) {
                                break;
                            }
                        }
                        int packetLen = remainingLength + incrementator.getValue();
                        this.packetData.setPacketLength(packetLen);
                        this.packetData.setPacketLengthCount(remainingHeaderLen);
                        logger.info(String.format("Decode and set remaining packet length: %s, with current buffer len: %s", packetLen, this,packetsBuffer.getSize()));
                    }

                    if (this.packetData.getPacketLength() <= packetsBuffer.getSize()) {

                        byte[] packet = packetsBuffer.getBytes(0, this.packetData.getPacketLength());
                        logger.info(String.format("Buffer contains all packet data "));

                        PayloadData payloadData = null;
                        VariableHeaderData headerData = null;
                        if (this.packetData.getFixedData().getMessageType() == MessageType.CONNECT) {
                            logger.info(String.format("Analyze Variable Header of CONNECT "));
                            this.headerContext.setDecoder(new ConnectVariableHeader());
                            headerData = this.headerContext.decode(packet, incrementator);
                            this.packetData.setVariableHeaderData(headerData);
                            this.payloadContext.setDecoder(new ConnectDecoder((ConnectVariableHeader.ConnectVariableHeaderData) headerData));
                            payloadData = this.payloadContext.decode(packet, incrementator);
                            this.packetData.setPayload(payloadData);
                        } else if (this.packetData.getFixedData().getMessageType() == MessageType.PUBLISH) {
                            logger.info(String.format("Analyze Variable Header of PUBLISH "));

                            this.headerContext.setDecoder(new PublishVariableHeader(packetData));
                            headerData = this.headerContext.decode(packet, incrementator);
                            this.packetData.setVariableHeaderData(headerData);
                            this.payloadContext.setDecoder(new PublishDecoder(packetData));
                            payloadData = this.payloadContext.decode(packet, incrementator);
                            this.packetData.setPayload(payloadData);

                        } else if (this.packetData.getFixedData().getMessageType() == MessageType.SUBSCRIBE) {
                            logger.info(String.format("Analyze Variable Header of SUBSCRIBE "));

                            this.headerContext.setDecoder(new SubscribeVariableHeader());
                            this.payloadContext.setDecoder(new SubscribeDecoder());
                            headerData = this.headerContext.decode(packet, incrementator);
                            this.packetData.setVariableHeaderData(headerData);
                            incrementator.increment();
                            payloadData = this.payloadContext.decode(packet, incrementator);
                            this.packetData.setPayload(payloadData);
                        }
                        this.packetsBuffer.delete(this.packetData.getPacketLength());
                        this.messagesQueue.put(this.packetData);
                        this.processingFlag = false;
                    }

                }
            }
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            logger.warning(String.format("Error when analyzing packet, message: %s, stack:", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
    }

    public synchronized void disconnect() {
        try {
            this.running = false;
            this.interrupt();
            this.join();
            logger.info("Packet analyzer disconnected successfull");
        } catch (InterruptedException e) {
            logger.warning(String.format("Error when disconnecting packet analyzer, message: %s, stack:", e.getMessage(), Arrays.toString(e.getStackTrace())));

        }

    }

    public class PacketData {
        private MqttFixedFixedHeaderDec.FixedHeaderData fixedData;
        private int packetLength;
        private int packetLengthCount;
        private VariableHeaderData variableHeaderData;
        private PayloadData payload;

        public int getPacketLengthCount() {
            return this.packetLengthCount;
        }

        public void setPacketLengthCount(int packetLengthCount) {
            this.packetLengthCount = packetLengthCount;
        }

        public MqttFixedFixedHeaderDec.FixedHeaderData getFixedData() {
            return fixedData;
        }

        public void setFixedData(MqttFixedFixedHeaderDec.FixedHeaderData fixedData) {
            this.fixedData = fixedData;
        }

        public PayloadData getPayload() {
            return payload;
        }

        public void setPayload(PayloadData payload) {
            this.payload = payload;
        }

        public int getPacketLength() {
            return packetLength;
        }

        public void setPacketLength(int packetLength) {
            this.packetLength = packetLength;
        }

        public VariableHeaderData getVariableHeaderData() {
            return variableHeaderData;
        }

        public void setVariableHeaderData(VariableHeaderData variableHeaderData) {
            this.variableHeaderData = variableHeaderData;
        }
    }
}
