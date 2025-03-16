package aj.programming.jQTT.Topics;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.SubscribeDecoder;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.PublishVariableHeader;
import aj.programming.jQTT.Client.Publisher;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class TopicsAggregator {
    private final Logger logger = Logger.getLogger(TopicsAggregator.class.getName());
    private final Map<String, Topic> map = new ConcurrentHashMap<>();

    public void subscribe(PacketsAnalyzer.PacketData packetData, Publisher publisher) {
        logger.info("Topic Aggregator, searching topic");
        SubscribeDecoder.SubscribeData subscribeData = (SubscribeDecoder.SubscribeData) packetData.getPayload();
        subscribeData.getSubscriptions().forEach((sub -> {
            logger.info("Incoming subscription topic iterating");
            List<String> topics = this.getTopicList(sub.getTopicFilter());
            String currentTopic = topics.getFirst();
            try {
                map.get(currentTopic).subscribe(sub, topics, publisher, packetData);
            } catch (Exception e) {
                logger.warning(String.format("Cannot find topic, message: %s, stack: %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
            }
        }));
    }

    public void publishOrAddTopicAndPublish(PacketsAnalyzer.PacketData packetData) {
        logger.info("Incoming topic, try publish or publish message and create topic");
        PublishVariableHeader.PublishVariableHeaderData publishVariableHeaderData = (PublishVariableHeader.PublishVariableHeaderData) packetData.getVariableHeaderData();
        List<String> topics = this.getTopicList(publishVariableHeaderData.getTopicName());
        Topic topic = this.map.computeIfAbsent(topics.getFirst(), k -> new Topic(topics));
        topic.publish(packetData);
        logger.info("Publishing message");
    }


    private List<String> getTopicList(String topics) {
        return new LinkedList<>(Arrays.asList(topics.split("/")));
    }

}
