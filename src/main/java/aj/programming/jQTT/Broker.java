package aj.programming.jQTT;

import aj.programming.jQTT.Socket.MQTTServer;

import java.util.logging.Logger;

public class Broker {
    private static final Logger logger = Logger.getLogger(Broker.class.getName());
    private static Broker instance;
    private final MQTTServer mqttServer;

    public Broker() {
        this.mqttServer = new MQTTServer();
        this.mqttServer.startServer();
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--port") || args[i].startsWith("-p")) {
                ConfigurationData.updateConfig("port", args[i].substring(args[i].indexOf("=") + 1));
                logger.info(String.format("Get broker %s port", ConfigurationData.getPort()));
            }
        }
        Broker.instance = new Broker();


    }
}
