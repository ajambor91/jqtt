package aj.programming.jQTT.Client;

import aj.programming.jQTT.IncomingPacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.IncomingPacketsEncoders.Payload.SubscribeDecoder;
import aj.programming.jQTT.Socket.SocketProvider;

import java.io.IOException;

public interface Subscriber extends SocketProvider {

    void sendMessage(PacketsAnalyzer.PacketData packetData)   throws IOException;
    void addSubscription(SubscribeDecoder.Subscription subscription);
    String getClientId();
}
