package aj.programming.jQTT.Socket;

import aj.programming.jQTT.PacketsEncoders.PacketsBuffer;
import aj.programming.jQTT.TopicsAggregator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

public class MQTTBroker implements DisconnectProvider {
    private final Logger logger = Logger.getLogger(MQTTBroker.class.getName());

    private Socket socket;
    private final PacketsBuffer packetsBuffer;
    private MQTTSocket mqttSocket;
    private Thread messangerThread;
    private Thread mqttSockeTthread;
    private final Publisher publisher;
    private MessageHandler messanger;

    public MQTTBroker(UUID uuid, Socket socket, TopicsAggregator topicsAggregator) {
        this.socket = socket;
        this.packetsBuffer = new PacketsBuffer();
        this.publisher = new Publisher(socket, topicsAggregator);
        this.messanger = new MessageHandler(publisher, topicsAggregator, packetsBuffer, this);
        this.mqttSocket = new MQTTSocket();
        this.mqttSockeTthread = new Thread(this.mqttSocket);
        this.messangerThread = new Thread(this.messanger);
        mqttSockeTthread.start();
        this.messangerThread.start();
    }


    @Override
    public synchronized void disconnect() {
        logger.info("Disconnecting client");
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            if (this.mqttSocket != null) {
                this.mqttSocket.stop();
            }
            if (this.messangerThread != null) {
                this.messangerThread.interrupt();
                this.messangerThread.join();
            }
            if (this.mqttSockeTthread != null) {
                this.mqttSockeTthread.interrupt();
                this.mqttSockeTthread.join();
            }

            logger.info("Client disconnected");

        } catch (InterruptedException | IOException e) {
            logger.warning(String.format("Break waiting for interrupt threads: %s", e.getMessage()));
        } finally {
            this.socket = null;
            this.messanger = null;
            this.messangerThread = null;
            this.mqttSocket = null;
            this.mqttSockeTthread = null;
            logger.warning("Client resources released");
        }
    }

    private class MQTTSocket implements Runnable {

        private volatile boolean running = true;

        @Override
        public void run() {
            try (BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream())) {
                while (running && socket != null && socket.isConnected() && !socket.isClosed()) {
                    logger.info("Received a new TCP packet");
                    byte[] buffer = new byte[4096];
                    int bytesRead = inputStream.read(buffer);

                    if (bytesRead == -1) {
                        logger.info("Cannot read bytes");
                        break;
                    } else if (bytesRead > 0) {
                        logger.info("Reading bytes");
                        byte[] packet = new byte[bytesRead];
                        System.arraycopy(buffer, 0, packet, 0, bytesRead);
                        try {
                            packetsBuffer.addPacket(packet);
                            messanger.handleMessage();
                        } catch (Exception e) {
                            logger.warning("Error when handling message");
                        }
                    }
                }
            } catch (IOException e) {
                logger.warning(String.format("Error when reading bytes, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
            } finally {
                        disconnect();


            }
        }

        public void stop() {
            this.running = false;
        }
    }


}
