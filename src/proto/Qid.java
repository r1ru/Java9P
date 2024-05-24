package proto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

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

    public static Qid of(Path path, BasicFileAttributes attr) {
        byte ty = attr.isDirectory() ? Qid.QTDIR : Qid.QTFILE;
        long p = attr.fileKey() != null ? attr.fileKey().hashCode() : path.hashCode();
        return new Qid(ty, 1, p);
        
    }

    public static Qid of(Path path) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(
            path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS
        );

        byte ty = attr.isDirectory() ? Qid.QTDIR : Qid.QTFILE;
        long p = attr.fileKey() != null ? attr.fileKey().hashCode() : path.hashCode();
        return new Qid(ty, 1, p);
    }

    public void write(ByteBuffer buf) {
        buf.put(type);
        buf.putInt(version);
        buf.putLong(path);
    }
}