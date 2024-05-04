package proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record Rattach(short tag, Qid qid) implements Rmessage {
    @Override
    public byte[] raw() {
        int len = 4 + 1 + 2 + Qid.SIZE;
        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        buf.putInt(len);
        buf.put(MessageType.RATTACH);
        buf.putShort(tag);
        buf.put(qid.raw());

        return buf.array();
    }
}
