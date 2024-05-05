package proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// https://man.cat-v.org/plan_9/5/stat
public record Stat(
    short type, int dev, Qid qid, 
    int mode, int atime, int mtime, long length, 
    String name, String uid, String gid, String muid
) {
    public byte[] raw() {
        int len = 2 + 2 + 4 + Qid.SIZE + 4 + 4 + 4 + 8 + 2 + name.length() + 2 + uid.length() + 2 + gid.length() + 2 + muid.length();
        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        buf.putShort((short)(len - 2));
        buf.putShort(type);
        buf.putInt(dev);
        buf.put(qid.raw());
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
        
        return buf.array();
    }
}
