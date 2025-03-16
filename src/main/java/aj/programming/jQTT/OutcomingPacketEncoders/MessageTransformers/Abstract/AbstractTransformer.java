package aj.programming.jQTT.OutcomingPacketEncoders.MessageTransformers.Abstract;

import aj.programming.jQTT.IncomingPacketsEncoders.FixedHeaders.MqttFixedFixedHeaderEnc;

public abstract class AbstractTransformer {
    protected MqttFixedFixedHeaderEnc mqttFixedFixedHeaderEnc;

    protected AbstractTransformer() {
        this.mqttFixedFixedHeaderEnc = new MqttFixedFixedHeaderEnc();
    }
}
