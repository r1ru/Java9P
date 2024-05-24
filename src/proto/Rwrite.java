package proto;

import util.Blob;

public record Rwrite(short tag, int count) implements Rmessage{
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RWRITE);
        buf.putShort(tag);
        buf.putInt(count);
        buf.putInt(pos, buf.position() - pos);
    }
}