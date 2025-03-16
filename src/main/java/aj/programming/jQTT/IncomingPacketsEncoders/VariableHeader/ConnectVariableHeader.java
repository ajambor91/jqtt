package aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader;

import aj.programming.jQTT.IncomingPacketsEncoders.Enums.ConnectFlags;
import aj.programming.jQTT.Utils.Incrementator;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ConnectVariableHeader implements VariableHeaderDecoder {

    @Override
    public VariableHeaderData decodeHeader(byte[] packet, Incrementator offset) {
        ConnectVariableHeaderData variableHeaderData = new ConnectVariableHeaderData();
        return variableHeaderData
                .setProtocolName(this.getProtocolName(packet, offset))
                .setProtocolVersion(this.getProtocolVersion(packet, offset))
                .setConnectFlags(this.getConnectFlags(packet, offset))
                .setKeepAlive(this.getKeepAlive(packet, offset));
    }

    private String getProtocolName(byte[] packet, Incrementator offset) {
        int protocolNameLength = (packet[offset.getValue()] << 8) + packet[offset.incrementAndGet()];
        String protocolName = new String(packet, offset.incrementAndGet(), protocolNameLength, StandardCharsets.UTF_8);
        offset.incrementOf(protocolNameLength);
        return protocolName;
    }

    private int getKeepAlive(byte[] packet, Incrementator offset) {
        int highByte = packet[offset.incrementAndGet()] & 0xFF;
        int lowByte = packet[offset.incrementAndGet()] & 0xFF;
        return (highByte << 8) | lowByte;
    }

    private int getProtocolVersion(byte[] packet, Incrementator offset) {
        return packet[offset.getValue()];
    }

    private Set<ConnectFlags> getConnectFlags(byte[] packet, Incrementator incrementator) {
        byte flagsByte = packet[incrementator.incrementAndGet()];
        Set<ConnectFlags> connectFlags = new HashSet<>();
        if ((flagsByte & ConnectFlags.USER_NAME_FLAG.getValue()) != 0) {
            connectFlags.add(ConnectFlags.USER_NAME_FLAG);
        }
        if ((flagsByte & ConnectFlags.PASSWORD_FLAG.getValue()) != 0) {
            connectFlags.add(ConnectFlags.PASSWORD_FLAG);

        }
        if ((flagsByte & ConnectFlags.WILL_RETAIN.getValue()) != 0) {
            connectFlags.add(ConnectFlags.WILL_RETAIN);

        }
        if ((flagsByte & ConnectFlags.WILL_QOS_BIT_3.getValue()) != 0) {
            connectFlags.add(ConnectFlags.WILL_QOS_BIT_3);

        }
        if ((flagsByte & ConnectFlags.WILL_QOS_BIT_4.getValue()) != 0) {
            connectFlags.add(ConnectFlags.WILL_QOS_BIT_4);

        }
        if ((flagsByte & ConnectFlags.WILL_FLAG.getValue()) != 0) {
            connectFlags.add(ConnectFlags.WILL_FLAG);

        }
        if ((flagsByte & ConnectFlags.CLEAN_SESSION.getValue()) != 0) {
            connectFlags.add(ConnectFlags.CLEAN_SESSION);

        }
        if ((flagsByte & ConnectFlags.RESERVED.getValue()) != 0) {
            connectFlags.add(ConnectFlags.RESERVED);

        }
        return connectFlags;
    }

    public class ConnectVariableHeaderData implements VariableHeaderData {
        private String protocolName;
        private int protocolVersion;
        private int keepAlive;

        private Set<ConnectFlags> connectFlags;

        public Set<ConnectFlags> getConnectFlags() {
            return this.connectFlags;
        }

        public ConnectVariableHeaderData setConnectFlags(Set<ConnectFlags> connectFlags) {
            this.connectFlags = connectFlags;
            return this;
        }

        public int getKeepAlive() {
            return this.keepAlive;
        }

        public ConnectVariableHeaderData setKeepAlive(int keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public ConnectVariableHeaderData setProtocolVersion(int protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public ConnectVariableHeaderData setProtocolName(String protocolName) {
            this.protocolName = protocolName;
            return this;
        }

    }


}
