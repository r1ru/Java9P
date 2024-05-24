package proto;

import util.Blob;

public record Rremove(short tag) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RREMOVE);
        buf.putShort(tag);
        buf.putInt(pos, buf.position() - pos);
    }
}
