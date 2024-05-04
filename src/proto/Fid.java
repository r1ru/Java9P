package proto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class Fid {
    private int fid;
    private Path path;

    public Fid(int fid, Path path) {
        this.fid = fid;
        this.path = path;
    }

    public Qid qid() throws ProtocolException {
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new ProtocolException(e.getMessage(), e);
        }

        byte ty = attr.isDirectory() ? Qid.QTDIR : Qid.QTFILE;
        long p = attr.fileKey() != null ? attr.fileKey().hashCode() : path.hashCode();
        // TODO: versionの更新処理
        return new Qid(ty, 1, p);
    }
}
