package aj.programming.jQTT;

import aj.programming.jQTT.PacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.PacketsEncoders.Payload.SubscribeDecoder;
import aj.programming.jQTT.PacketsEncoders.Payload.SubscriberData;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.PacketsEncoders.VariableHeader.SubscribeVariableHeader;
import aj.programming.jQTT.Socket.Subscriber;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Topic {
    private class TopicSubsciption implements SubscriberData {
        private final int qos;
        private final String topicFilter;
        private final Subscriber subscriber;
        private final String topTopic;
        private final int packetId;
        private final String clientId;
        public TopicSubsciption(
                int qos,
                String topicFilter,
                Subscriber subscriber,
                String topTopic,
                int packetId,
                String clientId
        ) {
            this.packetId = packetId;
            this.clientId = clientId;
            this.qos = qos;
            this.topTopic = topTopic;
            this.subscriber = subscriber;
            this.topicFilter = topicFilter;
        }
        @Override
        public Subscriber getSubscriber() {
            return this.subscriber;
        }

        @Override
        public String getTopTopic() {
            return this.topTopic;
        }

        @Override
        public int getPacketId() {
            return packetId;
        }

        @Override
        public String getClientId() {
            return this.clientId;
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
    private String topicName;
    private final Map<String, Topic> topicMap = new ConcurrentHashMap<>();
    private final Map<String, TopicSubsciption> subscribers = new ConcurrentHashMap<>();

    public Topic(List<String> topics, String topicName) {
        this.topicName = topicName;
        this.createTopics(topics);
    }

    public Topic(List<String> topics) {
        this.createTopics(topics);
    }

    private void addBroker(TopicSubsciption topicSubsciption) {
        if (topicSubsciption == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        subscribers.putIfAbsent(topicSubsciption.getClientId(), topicSubsciption);
    }

    public void subscribe(SubscribeDecoder.Subscription subscription, List<String> topics, Subscriber subscriber, PacketsAnalyzer.PacketData packetData) {
        String topicName = topics.removeFirst();

        if (topics.isEmpty() && topicName.equals(this.topicName)) {
            this.addBroker(new TopicSubsciption(
                    subscription.getQoS(),
                    subscription.getTopicFilter(),
                    subscriber,
                    topicName,
                    ((SubscribeVariableHeader.SubscribeVariableHeaderData) packetData.getVariableHeaderData()).getPacketIdentifier(),
                    subscriber.getClientId()
            ));
        } else {
            Topic top = topicMap.get(topics.getFirst());
            if (top == null) {
                throw new NullPointerException("Cannot find next topic");
            }
            top.subscribe(subscription, topics, subscriber, packetData);

        }
    }

    public void publish(PacketsAnalyzer.PacketData packetData) {
        PublishVariableHeader.PublishVariableHeaderData publishVariableHeader = (PublishVariableHeader.PublishVariableHeaderData) packetData.getVariableHeaderData();
        List<String> topics = this.getTopicList(publishVariableHeader.getTopicName());
        String topicName = topics.removeFirst();
        if (topics.isEmpty() && topicName.equals(this.topicName)) {
            this.subscribers.forEach((id, sub) -> {
                sub.getSubscriber().sendMessage(packetData);
            });
        } else {
            topicName = topics.removeFirst();
            Topic topic = this.topicMap.get(topicName);
            topic.publish(packetData, topics);
        }
    }

    public void publish(PacketsAnalyzer.PacketData packetData, List<String> topicsList) {
//        PublishVariableHeader.PublishVariableHeaderData publishVariableHeader = (PublishVariableHeader.PublishVariableHeaderData) packetData.getVariableHeaderData();
        if (topicsList.isEmpty() && topicName.equals(this.topicName)) {
            this.subscribers.forEach((id, sub) -> {
                sub.getSubscriber().sendMessage(packetData);
            });
        } else {
            topicName = topicsList.removeFirst();
            Topic topic = this.topicMap.get(topicName);
            topic.publish(packetData, topicsList);
        }

    }

    private void createTopics(List<String> topics) {
        this.topicName = topics.removeFirst();
        if (!topics.isEmpty()) {
            this.topicMap.computeIfAbsent(topics.getFirst(), k -> new Topic(topics, topics.getFirst()));
        }
    }

    private List<String> getTopicList(String topics) {
        return new LinkedList<>(Arrays.asList(topics.split("/")));
    }
}
