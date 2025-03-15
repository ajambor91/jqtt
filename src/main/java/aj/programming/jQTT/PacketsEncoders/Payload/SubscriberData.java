package aj.programming.jQTT.PacketsEncoders.Payload;

import aj.programming.jQTT.Socket.Subscriber;

public interface SubscriberData extends SubscriptionData {
    Subscriber getSubscriber();
    String getTopTopic();
    int getPacketId();
    String getClientId();
}
