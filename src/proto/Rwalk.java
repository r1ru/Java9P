package proto;

import java.nio.ByteBuffer;
import java.util.List;

public record Rwalk(short tag, List<Qid> qids) implements Rmessage {
    @Override
    public void write(ByteBuffer buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RWALK);
        buf.putShort(tag);
        buf.putShort((short)qids.size());
        for (Qid q: qids) {
            q.write(buf);
        }
        buf.putInt(pos, buf.position() - pos);
    }
}
