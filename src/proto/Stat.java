package proto;

import java.nio.ByteBuffer;

// https://man.cat-v.org/plan_9/5/stat
public record Stat(
    short type, int dev, Qid qid, 
    int mode, int atime, int mtime, long length, 
    String name, String uid, String gid, String muid
) {
    public void write(ByteBuffer buf) {
        int pos = buf.position();
        buf.putShort((short)0);
        buf.putShort(type);
        buf.putInt(dev);
        qid.write(buf);
        buf.putInt(mode);
        buf.putInt(atime);
        buf.putInt(mtime);
        buf.putLong(length);
        buf.putShort((short)name.length());
        buf.put(name.getBytes());
        buf.putShort((short)uid.length());
        buf.put(uid.getBytes());
        buf.putShort((short)gid.length());
        buf.put(gid.getBytes());
        buf.putShort((short)muid.length());
        buf.put(muid.getBytes());
        buf.putShort(pos, (short)(buf.position() - pos - 2));
    }
}
