package aj.programming.jQTT.IncomingPacketsEncoders.Payload;

public interface SubscriptionData {
    int getQoS();
    String getTopicFilter();
}
