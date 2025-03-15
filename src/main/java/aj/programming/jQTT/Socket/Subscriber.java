package aj.programming.jQTT.Socket;

import aj.programming.jQTT.PacketsEncoders.PacketsAnalyzer;
import aj.programming.jQTT.PacketsEncoders.Payload.SubscribeDecoder;

public interface Subscriber {

    void sendMessage(PacketsAnalyzer.PacketData packetData);
    void addSubscription(SubscribeDecoder.Subscription subscription);
    String getClientId();
}
