package aj.programming.jQTT.IncomingPacketsEncoders.Payload;

import aj.programming.jQTT.IncomingPacketsEncoders.Enums.ConnectFlags;
import aj.programming.jQTT.IncomingPacketsEncoders.VariableHeader.ConnectVariableHeader;
import aj.programming.jQTT.Utils.Incrementator;

import java.nio.charset.StandardCharsets;

public class ConnectDecoder implements PayloadDecoder {
    ConnectVariableHeader.ConnectVariableHeaderData headerData;

    public ConnectDecoder(ConnectVariableHeader.ConnectVariableHeaderData headerData) {
        this.headerData = headerData;
    }

    @Override
    public PayloadData decodePayload(byte[] packet, Incrementator offset) {
        String clientId = null;
        String willTopic = null;
        String willMessage = null;
        String userName = null;
        String password = null;
        while (offset.incrementAndGet() < packet.length) {
            clientId = getData(packet, offset);
            if (headerData.getConnectFlags().contains(ConnectFlags.WILL_FLAG)) {
                willTopic = getData(packet, offset);
                willMessage = getData(packet, offset);
            }
            if (
                    headerData.getConnectFlags().contains(ConnectFlags.PASSWORD_FLAG) &&
                            headerData.getConnectFlags().contains(ConnectFlags.USER_NAME_FLAG)
            ) {
                userName = getData(packet, offset);
                password = getData(packet, offset);
            }
            break;
        }
        return new ConnectData()
                .setClientId(clientId)
                .setWillTopic(willTopic)
                .setWillMessage(willMessage)
                .setUserName(userName)
                .setPassword(password);
    }

    private String getData(byte[] packet, Incrementator incrementator) {
        int willTopicLen = caclLen(packet, incrementator);
        String data = new String(packet, incrementator.incrementAndGet(), willTopicLen, StandardCharsets.UTF_8);
        incrementator.incrementOf(willTopicLen);
        return data;
    }

    private int caclLen(byte[] packet, Incrementator incrementator) {
        int highByte = packet[incrementator.getValue()] & 0xFF;
        int lowByte = packet[incrementator.incrementAndGet()] & 0xFF;
        return (highByte << 8) | lowByte;
    }

    public class ConnectData implements PayloadData {
        private String clientId;
        private String willTopic;
        private String willMessage;
        private String userName;
        private String password;

        public String getClientId() {
            return clientId;
        }

        public ConnectData setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public String getWillTopic() {
            return willTopic;
        }

        public ConnectData setWillTopic(String willTopic) {
            this.willTopic = willTopic;
            return this;
        }

        public String getWillMessage() {
            return willMessage;
        }

        public ConnectData setWillMessage(String willMessage) {
            this.willMessage = willMessage;
            return this;
        }

        public String getUserName() {
            return userName;
        }

        public ConnectData setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public ConnectData setPassword(String password) {
            this.password = password;
            return this;
        }
    }

}
