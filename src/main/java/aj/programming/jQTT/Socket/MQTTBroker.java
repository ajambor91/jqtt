package aj.programming.jQTT.Socket;

import aj.programming.jQTT.Client.Publisher;
import aj.programming.jQTT.Core.MQTTSocketRunnable;
import aj.programming.jQTT.Core.ThreadsManager;
import aj.programming.jQTT.IncomingPacketsEncoders.PacketsBuffer;
import aj.programming.jQTT.Topics.TopicsAggregator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

public class MQTTBroker  {
    private final Logger logger = Logger.getLogger(MQTTBroker.class.getName());

    private final ThreadsManager threadsManager;
    private Socket socket;
    private final PacketsBuffer packetsBuffer;
    private MQTTSocket mqttSocket;
    private Thread messangerThread;
    private Thread mqttSockeTthread;
    private final Publisher publisher;
    private MessageHandler messanger;

    public MQTTBroker(
            String serverId,
            Socket socket,
            RemoveClientProvider removeClientProvider,
            TopicsAggregator topicsAggregator) {
        this.socket = socket;
        this.threadsManager = new ThreadsManager(serverId, removeClientProvider);
        this.packetsBuffer = new PacketsBuffer();
        this.publisher = new Publisher(socket, topicsAggregator);
        this.messanger = new MessageHandler(publisher, topicsAggregator, packetsBuffer, this.threadsManager);
        this.mqttSocket = new MQTTSocket();
        this.threadsManager.runSocket(this.mqttSocket);
        this.threadsManager.runMessageHandler(this.messanger);
    }

    private class MQTTSocket implements MQTTSocketRunnable {

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
                        threadsManager.disconnect();

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
            } catch ( InterruptedException e){

                    logger.info(String.format("MQTTBroker interrupted, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
            }
        }

        public synchronized void stop() {
            this.running = false;
        }

        public Socket getSocket() {
            return socket;
        }
    }


}
