package proto;

import util.Blob;

public record Rattach(short tag, Qid qid) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RATTACH);
        buf.putShort(tag);
        qid.write(buf);
        buf.putInt(pos, buf.position() - pos);
    }
}
