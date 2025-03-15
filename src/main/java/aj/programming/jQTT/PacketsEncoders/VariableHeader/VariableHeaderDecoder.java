package aj.programming.jQTT.PacketsEncoders.VariableHeader;

import aj.programming.jQTT.Utils.Incrementator;

public interface VariableHeaderDecoder {
    VariableHeaderData decodeHeader(byte[] packet, Incrementator offset);


}
