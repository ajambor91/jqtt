package aj.programming.jQTT.Client;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.SubscribeDecoder;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageCreator;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.ConnackTransformer;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.PublishTransformer;
import aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.SubackTransformer;
import aj.programming.jQTT.Topics.TopicsAggregator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class Publisher implements Subscriber {
    private Logger logger = Logger.getLogger(Publisher.class.getName());
    private final Socket socket;

    private String clientId;
    public Publisher(Socket socket, TopicsAggregator topicsAggregator) {
        this.socket = socket;}

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Socket getSocket() {
        return this.socket;
    }


    public void sendSubback(PacketsAnalyzer.PacketData packetData) throws  IOException {

            OutputStream outputStream = socket.getOutputStream();
            MessageCreator messageCreator = new MessageCreator(packetData, new SubackTransformer());
            outputStream.write(messageCreator.getMessage());
            outputStream.flush();
            logger.warning("Cannot send SUBACK");

    }


    public void sendConnack(PacketsAnalyzer.PacketData packetData)  throws  IOException {
            OutputStream outputStream = socket.getOutputStream();
            MessageCreator messageCreator = new MessageCreator(packetData, new ConnackTransformer());
            outputStream.write(messageCreator.getMessage());
            outputStream.flush();

    }

    @Override
    public void sendMessage(PacketsAnalyzer.PacketData packetData)  throws  IOException {
            OutputStream outputStream = socket.getOutputStream();
            MessageCreator messageCreator = new MessageCreator(packetData, new PublishTransformer());
            outputStream.write(messageCreator.getMessage());
            outputStream.flush();

    }

    @Override
    public void addSubscription(SubscribeDecoder.Subscription subscription) {

    }

    @Override
    public String getClientId() {
        return this.clientId;
    }
}
