package proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record Rerror(short tag, String ename) implements Rmessage {
    @Override
    public byte[] raw() {
        int len = 4 + 1 + 2 + 2 + ename.length();
        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        buf.putInt(len);
        buf.put(MessageType.RERROR);
        buf.putShort(tag);
        buf.putShort((short)ename.length());
        buf.put(ename.getBytes());

        return buf.array();
    }
}
