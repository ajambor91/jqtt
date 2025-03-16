package aj.programming.jQTT.IncomingPacketsEncoders.Enums;

public enum QoSLevel {
    QOS_LEVEL_0(0),
    QOS_LEVEL_1(1),
    QOS_LEVEL_2(2);
    private final int qosLevel;

    QoSLevel(int qosLevel) {
        this.qosLevel = qosLevel;
    }

    public int getQosLevel() {
        return this.qosLevel;
    }
}
