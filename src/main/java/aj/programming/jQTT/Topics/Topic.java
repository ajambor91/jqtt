package aj.programming.jQTT.Topics;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.SubscribeDecoder;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.SubscriberData;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.SubscribeVariableHeader;
import aj.programming.jQTT.Client.Subscriber;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Topic {
    private Logger logger = Logger.getLogger(Topic.class.getName());
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
        TopicSubsciption existingSub = subscribers.get(topicSubsciption.getClientId());
        if (existingSub == null) {
            subscribers.put(topicSubsciption.getClientId(), topicSubsciption);
        } else if (existingSub.getSubscriber().getSocket().isClosed()) {
            subscribers.put(topicSubsciption.getClientId(), topicSubsciption);
        } else {
            throw new IllegalArgumentException("Active client trying subscribe already subscribed topic");
        }
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
                try {if (sub.subscriber.getSocket().isConnected()) {
                    sub.getSubscriber().sendMessage(packetData);

                }

                } catch (IOException e) {
                    System.out.println("Error" + e.getMessage());
                }
            });
        } else {
            topicName = topics.removeFirst();
            Topic topic = this.topicMap.get(topicName);
            topic.publish(packetData, topics);
        }
    }

    public void publish(PacketsAnalyzer.PacketData packetData, List<String> topicsList) {
        if (topicsList.isEmpty() && topicName.equals(this.topicName)) {
            this.subscribers.forEach((id, sub) -> {
                try {if (sub.getSubscriber().getSocket().isConnected()) {
                    sub.getSubscriber().sendMessage(packetData);
                }
                } catch (IOException e) {
                    System.out.println("Err " + e.getMessage());
                }
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
