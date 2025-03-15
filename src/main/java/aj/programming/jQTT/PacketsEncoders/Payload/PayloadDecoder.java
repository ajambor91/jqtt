package aj.programming.jQTT.PacketsEncoders.Payload;

import aj.programming.jQTT.Utils.Incrementator;

public interface PayloadDecoder {
    PayloadData decodePayload(byte[] packet, Incrementator offset);


}
