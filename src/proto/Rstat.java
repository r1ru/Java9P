package proto;

import util.Blob;

public record Rstat(short tag, Stat stat) implements Rmessage {
    @Override
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putInt(0);
        buf.put(MessageType.RSTAT);
        buf.putShort(tag);
        int pos1 = buf.position();
        buf.putShort((short)0);
        stat.write(buf);
        buf.putInt(pos, buf.position() - pos);
        buf.putShort(pos1, (short)(buf.position() - pos1 - 2));
    }
}