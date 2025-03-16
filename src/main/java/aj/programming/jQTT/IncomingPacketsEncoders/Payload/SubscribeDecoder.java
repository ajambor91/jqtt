package aj.programming.jQTT.IncomingPacketsEncoders.Payload;

import aj.programming.jQTT.Utils.Incrementator;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SubscribeDecoder implements PayloadDecoder {

    @Override
    public PayloadData decodePayload(byte[] packet, Incrementator offset) {
        SubscribeData subscribeData = new SubscribeData();
        while (offset.getValue() < packet.length) {
            int topicFilterHighByte = packet[offset.getValue()] & 0xFF;
            int topicFilterLowByte = packet[offset.incrementAndGet()] & 0xFF;
            int topicFilterLength = (topicFilterHighByte << 8) | topicFilterLowByte;
            String topicFilter = new String(packet, offset.incrementAndGet(), topicFilterLength, StandardCharsets.UTF_8);
            offset.incrementOf(topicFilterLength);
            int qos = packet[offset.getAndIncrement()];
            subscribeData.addSubscription(new Subscription(topicFilter, qos));

        }
        return subscribeData;
    }

    public class Subscription implements SubscriptionData{
        private String topicFilter;
        private int qos;

        public Subscription(String topicFilter, int qos) {
            this.topicFilter = topicFilter;
            this.qos = qos;
        }

        @Override
        public int getQoS() {
            return this.qos;
        }

        @Override
        public String getTopicFilter() {
            return this.topicFilter;
        }
    }

    public class SubscribeData implements PayloadData {
        private final List<Subscription> subscriptions;

        public SubscribeData() {
            this.subscriptions = new ArrayList<>();
        }

        public void addSubscription(Subscription subscription) {
            this.subscriptions.add(subscription);
        }

        public List<Subscription> getSubscriptions() {
            return this.subscriptions;
        }
    }
}
