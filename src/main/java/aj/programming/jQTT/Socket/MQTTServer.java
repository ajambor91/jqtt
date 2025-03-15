package aj.programming.jQTT.Socket;

import aj.programming.jQTT.Configuration;
import aj.programming.jQTT.TopicsAggregator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MQTTServer {
    private final Logger logger = Logger.getLogger(MQTTServer.class.getName());
    private final Set<MQTTBroker> mqttBrokers = new HashSet<>();
    private TopicsAggregator topicsAggregator;
    private ExecutorService executorService;
    private Thread serverThread;

    public void startServer() {
        this.topicsAggregator = new TopicsAggregator();
        this.executorService = Executors.newCachedThreadPool();
        this.serverThread = new Thread(new Server());
        this.serverThread.start();
    }

    private class Server implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {

            try (ServerSocket serverSocket = new ServerSocket(Configuration.getPort())) {
                logger.info(String.format("Server listening on port: %s", Configuration.getPort()));

                while (this.running) {
                    Socket socket = serverSocket.accept();
                    logger.info("New client connected");
                    executorService.submit(() -> {
                        MQTTBroker broker = new MQTTBroker(UUID.randomUUID(), socket, topicsAggregator);
                        mqttBrokers.add(broker);
                    });
                }

            } catch (IOException e) {
                logger.severe(String.format("Server Error, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                this.running = false;

            }
        }
    }
}
