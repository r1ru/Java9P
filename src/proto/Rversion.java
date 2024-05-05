package proto;

import util.Blob;

public record Rversion(short tag, int msize, String version) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RVERSION);
        buf.putShort(tag);
        buf.putInt(msize);
        buf.putString(version);
        buf.putInt(pos, buf.position() - pos);
    }
};