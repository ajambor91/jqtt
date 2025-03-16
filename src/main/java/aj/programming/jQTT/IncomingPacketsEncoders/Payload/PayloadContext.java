package aj.programming.jQTT.IncomingPacketsEncoders.Payload;

import aj.programming.jQTT.Utils.Incrementator;

public class PayloadContext {
    private PayloadDecoder decoder;

    public void setDecoder(PayloadDecoder decoder) {
        this.decoder = decoder;
    }

    public PayloadData decode(byte[] packet, Incrementator offset) {
        return decoder.decodePayload(packet, offset);
    }
}
