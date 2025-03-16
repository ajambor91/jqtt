package aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader;

import aj.programming.jQTT.Utils.Incrementator;

public class VariableHeaderContext {
    private VariableHeaderDecoder decoder;

    public void setDecoder(VariableHeaderDecoder decoder) {
        this.decoder = decoder;
    }

    public VariableHeaderData decode(byte[] packet, Incrementator offset) {
        return decoder.decodeHeader(packet, offset);
    }
}
