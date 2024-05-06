package proto;

import java.util.List;
import util.Blob;

public record Rwalk(short tag, List<Qid> qids) implements Rmessage {
    @Override
    public void write(Blob buf) {
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
