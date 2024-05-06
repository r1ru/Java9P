package proto;

import util.Blob;

public record Rread(short tag, Blob data) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RREAD);
        buf.putShort(tag);
        data.flip();
        buf.putInt(data.remaining());
        buf.put(data);
        buf.putInt(pos, buf.position() - pos);
    }
}
