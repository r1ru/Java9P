package proto;

import java.nio.ByteBuffer;

public record Ropen(short tag, Qid qid, int iounit) implements Rmessage {
    @Override
    public void write(ByteBuffer buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.ROPEN);
        buf.putShort(tag);
        qid.write(buf);
        buf.putInt(iounit);
        buf.putInt(pos, buf.position() - pos);
    }
}
