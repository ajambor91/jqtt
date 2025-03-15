package aj.programming.jQTT;

public interface Configuration {

    static String getAddress() {
        return ConfigurationData.getAddress();
    }

    static int getPort() {
        return ConfigurationData.getPort();
    }

}
