package proto;

import util.Blob;

public record Rerror(short tag, String ename) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RERROR);
        buf.putShort(tag);
        buf.putString(ename);
        buf.putInt(pos, buf.position() - pos);
    }
}
