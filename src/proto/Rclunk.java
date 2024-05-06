package proto;

import util.Blob;

public record Rclunk(short tag) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RCLUNK);
        buf.putShort(tag);
        buf.putInt(pos, buf.position() - pos);
    }
}
