package proto;

import java.nio.ByteBuffer;

public record Rerror(short tag, String ename) implements Rmessage {
    @Override
    public void write(ByteBuffer buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RERROR);
        buf.putShort(tag);
        buf.putShort((short)ename.length());
        buf.put(ename.getBytes());
        buf.putInt(pos, buf.position() - pos);
    }
}
