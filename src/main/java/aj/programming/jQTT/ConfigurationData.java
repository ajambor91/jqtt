package aj.programming.jQTT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigurationData {
    private static String address = "localhost";
    private static int port = 1883;
    private static final Logger logger = Logger.getLogger(ConfigurationData.class.getName());

    static {
        Properties properties = new Properties();
        try (InputStream inputStream = ConfigurationData.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
            address = properties.getProperty("mqtt.addr", "localhost");
            port = Integer.parseInt(properties.getProperty("mqtt.port", "1883"));
        } catch (IOException e) {
            logger.warning("Cannot load config properties");
        }
    }

    public static String getAddress() {
        return ConfigurationData.address;
    }

    public static int getPort() {
        return ConfigurationData.port;
    }

    public static void updateConfig(String key, String value) {
        if (key.equals("address")) {
            address = value;
        } else if (key.equals("port")) {
            port = Integer.parseInt(value);
        }
    }
}
