package proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record Rversion(short tag, int msize, String version) implements Rmessage {
    @Override
    public byte[] raw() {
        int len = 4 + 1 + 2 + 4 + 2 + version.length();
        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        buf.putInt(len);
        buf.put(MessageType.RVERSION);
        buf.putShort(tag);
        buf.putInt(msize);
        buf.putShort((short)version.length());
        buf.put(version.getBytes());

        return buf.array();
    }
};