package aj.programming.jQTT.IncomingPacketsEncoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PacketsBuffer {
    private ByteArrayOutputStream byteArrayOutputStreams = new ByteArrayOutputStream();

    public synchronized void addPacket(byte[] incomingPacket) throws IOException {
        byteArrayOutputStreams.write(incomingPacket);
    }

    public synchronized byte getByte(int offset) {
        byte[] buffer = byteArrayOutputStreams.toByteArray();
        if (offset < 0 || offset >= buffer.length) {
            throw new IndexOutOfBoundsException("Offset out of bounds: " + offset);
        }
        return buffer[offset];
    }
    public synchronized byte[] getBytes(int offset, int length) {
        byte[] buffer = byteArrayOutputStreams.toByteArray();
        if (offset + length > buffer.length) {
            throw new IndexOutOfBoundsException("Invalid offset/length");
        }
        byte[] result = new byte[length];
        System.arraycopy(buffer, offset, result, 0, length);
        return result;
    }

    public synchronized int getSize() {
        return byteArrayOutputStreams.size();
    }

    public synchronized void delete(int from) {
        byte[] buffer = byteArrayOutputStreams.toByteArray();
        if (from < 0 || from > buffer.length) {
            throw new IndexOutOfBoundsException("Invalid 'from' index: " + from);
        }
        ByteArrayOutputStream newStream = new ByteArrayOutputStream();
        newStream.write(buffer, from, buffer.length - from);
        this.byteArrayOutputStreams = newStream;
    }

}
