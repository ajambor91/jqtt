package aj.programming.jQTT.IncomingPacketsEncoders.Payload;

import aj.programming.jQTT.Client.Subscriber;

public interface SubscriberData extends SubscriptionData {
    Subscriber getSubscriber();
    String getTopTopic();
    int getPacketId();
    String getClientId();
}
