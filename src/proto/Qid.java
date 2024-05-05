package proto;

import util.Blob;

// https://github.com/brho/plan9/blob/master/nix/sys/src/cmd/unix/u9fs/plan9.h#L155
public class Qid {
    // TODO: 他のtypeをサポートする?
    public static final int SIZE = 13;
    public static final byte QTFILE = 0;
    public static final byte QTDIR = (byte)0x80;
    public byte type;
    public int version;
    public long path;

    public Qid(byte type, int version, long path) {
        this.type = type;
        this.version = version;
        this.path = path;
    }

    public void write(Blob buf) {
        buf.put(type);
        buf.putInt(version);
        buf.putLong(path);
    }
}