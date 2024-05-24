package proto;

import java.nio.ByteBuffer;

public record Rversion(short tag, int msize, String version) implements Rmessage {
    @Override
    public void write(ByteBuffer buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RVERSION);
        buf.putShort(tag);
        buf.putInt(msize);
        buf.putShort((short)version.length());
        buf.put(version.getBytes());
        buf.putInt(pos, buf.position() - pos);
    }
};