package aj.programming.jQTT.Socket;

import aj.programming.jQTT.Configuration;
import aj.programming.jQTT.Topics.TopicsAggregator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MQTTServer implements RemoveClientProvider{
    private final Logger logger = Logger.getLogger(MQTTServer.class.getName());
    private final Map<String, MQTTBroker> mqttBrokers = new HashMap<>();
    private TopicsAggregator topicsAggregator;
    private Thread serverThread;

    public void startServer() {
        this.topicsAggregator = new TopicsAggregator();
        this.serverThread = new Thread(new Server(this));
        this.serverThread.start();
    }

    @Override
    public void removeClient(String serverClientId) {
        this.mqttBrokers.remove(serverClientId);
    }

    private class Server implements Runnable {
        private volatile boolean running;
        private final RemoveClientProvider removeClientProvider;
        public Server(RemoveClientProvider removeClientProvider) {
            this.running = true;
            this.removeClientProvider = removeClientProvider;
        }

        @Override
        public void run() {

            try (ServerSocket serverSocket = new ServerSocket(Configuration.getPort())) {
                logger.info(String.format("Server listening on port: %s", Configuration.getPort()));

                while (this.running) {
                    Socket socket = serverSocket.accept();
                    logger.info("New client connected");
                        String serverId = UUID.randomUUID().toString();
                        MQTTBroker broker = new MQTTBroker(serverId, socket,this.removeClientProvider, topicsAggregator);
                        mqttBrokers.putIfAbsent(serverId, broker);

                }

            } catch (IOException e) {
                logger.severe(String.format("Server Error, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                this.running = false;

            }
        }
    }
}
