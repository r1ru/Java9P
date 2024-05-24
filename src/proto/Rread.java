package proto;

import java.nio.ByteBuffer;

public record Rread(short tag, ByteBuffer data) implements Rmessage {
    @Override
    public void write(ByteBuffer buf) {
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
