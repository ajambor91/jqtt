package aj.programming.jQTT.Utils;

public final class ByteUtils {

    public static byte setFlag(byte header, byte mask) {
        return (byte) (header | mask);
    }

    public static byte clearFlag(byte header, byte mask) {
        return (byte) (header & (~mask));
    }

    public static boolean isFlagSet(byte header, byte mask) {
        return (header & mask) != 0;
    }
}
