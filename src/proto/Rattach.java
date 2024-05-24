package proto;

import java.nio.ByteBuffer;

public record Rattach(short tag, Qid qid) implements Rmessage {
    @Override
    public void write(ByteBuffer buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RATTACH);
        buf.putShort(tag);
        qid.write(buf);
        buf.putInt(pos, buf.position() - pos);
    }
}
