package proto;

import util.Blob;

public record Ropen(short tag, Qid qid, int iounit) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.ROPEN);
        buf.putShort(tag);
        qid.write(buf);
        buf.putInt(iounit);
        buf.putInt(pos, buf.position() - pos);
    }
}
