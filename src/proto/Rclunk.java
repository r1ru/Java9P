package proto;

import java.nio.ByteBuffer;

public record Rclunk(short tag) implements Rmessage {
    @Override
    public void write(ByteBuffer buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RCLUNK);
        buf.putShort(tag);
        buf.putInt(pos, buf.position() - pos);
    }
}
