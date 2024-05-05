package proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record Rstat(short tag, Stat stat) implements Rmessage {
    @Override
    public byte[] raw() {
        byte[] rawStat = stat.raw();
        int len = 4 + 1 + 2 + 2 + rawStat.length;
        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        buf.putInt(len);
        buf.put(MessageType.RSTAT);
        buf.putShort(tag);
        buf.putShort((short)rawStat.length);
        buf.put(rawStat);
        
        return buf.array();
    }
}
