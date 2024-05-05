package proto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class Fid {
    public int fid;
    private Path path;

    public Fid(int fid, Path path) {
        this.fid = fid;
        this.path = path;
    }

    public Stat stat() throws ProtocolException {
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new ProtocolException(e.getMessage(), e);
        }

        byte ty = attr.isDirectory() ? Qid.QTDIR : Qid.QTFILE;
        long p = attr.fileKey() != null ? attr.fileKey().hashCode() : path.hashCode();
        // https://github.com/brho/plan9/blob/master/nix/sys/src/cmd/unix/u9fs/plan9.h#L163
        // https://man.cat-v.org/plan_9/5/intro
        int mode = (int)ty << 24;
        mode |= 0b110110110;

        // TODO: mode, uid, gid, muidの部分を直す。
        return new Stat(
            (short)0, (short)0, new Qid(ty, 1, p) , mode, 
            (int)(attr.lastAccessTime().toMillis() / 1000),
            (int)(attr.lastModifiedTime().toMillis() / 1000),
            attr.size(),
            path.getFileName().toString(),
            "taro",
            "taro",
            ""
        );
    }

    public Qid qid() throws ProtocolException {
        return this.stat().qid();
    }
}
