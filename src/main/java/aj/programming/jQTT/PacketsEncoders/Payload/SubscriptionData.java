package aj.programming.jQTT.PacketsEncoders.Payload;

public interface SubscriptionData {
    int getQoS();
    String getTopicFilter();
}
