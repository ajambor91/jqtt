package aj.programming.jQTT.Core;

import aj.programming.jQTT.Socket.RemoveClientProvider;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadsManager {
    private final String serverId;
    private final CurrentThread currentThread;
    private final RemoveClientProvider removeClientProvider;

    public ThreadsManager(String serverId, RemoveClientProvider removeClientProvider) {
        this.serverId = serverId;
        this.removeClientProvider = removeClientProvider;
        this.currentThread = new CurrentThread();
    }

    public class CurrentThread {
        private final int SHUTDOWN_TIMEOUT;
        private final AtomicBoolean shutdownInProgress;
        private MQTTSocketRunnable mqttSocketRunnable;
        private Thread mqttSocketThread;
        private PacketAnalyzerRunnable packetAnalyzerRunnable;
        private Thread packetAnalyzerThread;
        private MessageHandlerRunnable messageHandlerRunnable;
        private Thread messageHandlerThread;

        public CurrentThread() {
            this.shutdownInProgress = new AtomicBoolean(false);
            this.SHUTDOWN_TIMEOUT = 2000;
        }

        public synchronized void runMQTTSocket(MQTTSocketRunnable socketRunnable) {
            if (this.mqttSocketThread == null || !this.mqttSocketThread.isAlive()) {
                this.mqttSocketRunnable = socketRunnable;
                this.mqttSocketThread = new Thread(socketRunnable);
                this.mqttSocketThread.start();
            }
        }

        public synchronized void runPacketAnalyzer(PacketAnalyzerRunnable packetAnalyzerRunnable) {
            if (this.packetAnalyzerThread == null || !this.packetAnalyzerThread.isAlive()) {
                this.packetAnalyzerRunnable = packetAnalyzerRunnable;
                this.packetAnalyzerThread = new Thread(packetAnalyzerRunnable);
                this.packetAnalyzerThread.start();
            }
        }

        public synchronized void runMessageHandler(MessageHandlerRunnable messageHandlerRunnable) {
            if (this.messageHandlerThread == null || !this.messageHandlerThread.isAlive()) {
                this.messageHandlerRunnable = messageHandlerRunnable;
                this.messageHandlerThread = new Thread(messageHandlerRunnable);
                this.messageHandlerThread.start();;
            }
        }

        public synchronized void disconnect() throws InterruptedException, IOException {
            if (shutdownInProgress.get()) {
                throw new IOException("Shutdown in progress");
            }

            if (this.messageHandlerThread != null && this.messageHandlerThread.isAlive()) {
                this.messageHandlerRunnable.stop();
                this.messageHandlerThread.interrupt();
                this.messageHandlerThread.join(SHUTDOWN_TIMEOUT);
            }

            if (this.packetAnalyzerThread != null && this.packetAnalyzerThread.isAlive()) {
                this.packetAnalyzerRunnable.stop();
                this.packetAnalyzerThread.interrupt();
                this.packetAnalyzerThread.join(SHUTDOWN_TIMEOUT);
            }

            if (this.mqttSocketThread != null && this.mqttSocketThread.isAlive()) {
                this.mqttSocketRunnable.stop();
                this.mqttSocketRunnable.getSocket().close();
                this.mqttSocketThread.interrupt();
                this.mqttSocketThread.join(SHUTDOWN_TIMEOUT);
            }
        }
    }

    public void runSocket(MQTTSocketRunnable socketRunnable) {
        this.currentThread.runMQTTSocket(socketRunnable);
    }

    public void runPacketAnalyzer(PacketAnalyzerRunnable packetAnalyzerRunnable) {
        this.currentThread.runPacketAnalyzer(packetAnalyzerRunnable);
    }

    public void runMessageHandler(MessageHandlerRunnable messageHandlerRunnable) {
        this.currentThread.runMessageHandler(messageHandlerRunnable);
    }

    public void disconnect() throws IOException, InterruptedException {
        this.currentThread.disconnect();
    }

}
