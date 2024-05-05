package proto;

import util.Blob;

// https://man.cat-v.org/plan_9/5/stat
public record Stat(
    short type, int dev, Qid qid, 
    int mode, int atime, int mtime, long length, 
    String name, String uid, String gid, String muid
) {
    public void write(Blob buf) {
        int pos = buf.position();
        buf.putShort((short)0);
        buf.putShort(type);
        buf.putInt(dev);
        qid.write(buf);
        buf.putInt(mode);
        buf.putInt(atime);
        buf.putInt(mtime);
        buf.putLong(length);
        buf.putString(name);
        buf.putString(uid);
        buf.putString(gid);
        buf.putString(muid);
        buf.putShort(pos, (short)(buf.position() - pos - 2));
    }
}
