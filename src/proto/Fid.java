package proto;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import util.Blob;

public class Fid {
    public int fid;
    public Path path;
    private boolean isOpen = false;
    private Blob buf;

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

        Qid qid = Qid.of(path, attr);
        // https://github.com/brho/plan9/blob/master/nix/sys/src/cmd/unix/u9fs/plan9.h#L163
        // https://man.cat-v.org/plan_9/5/intro
        int mode = (int)qid.type << 24;
        mode |= 0b110110110;

        // TODO: mode, uid, gid, muidの部分を直す。
        return new Stat(
            (short)0, (short)0, qid , mode, 
            (int)(attr.lastAccessTime().toMillis() / 1000),
            (int)(attr.lastModifiedTime().toMillis() / 1000),
            attr.size(),
            path.getFileName().toString(),
            "taro",
            "taro",
            ""
        );
    }

    public void open(byte mode) throws ProtocolException {
        if (!Files.isDirectory(path)) {
            throw new ProtocolException("Not yet implemented");  
        }
        // openできるのは最大1クライアントまで
        if (isOpen) {
            throw new ProtocolException("bad use of fid");
        }

        // ディレクトリの場合、バッファにstatを読み込んでおく。
        buf = Blob.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p: stream) {
                new Fid(-1, p).stat().write(buf);
            }
        } catch (IOException e) {
            throw new ProtocolException(e.getMessage(), e);
        }
        buf.flip();
        isOpen = true;
    }

    public Blob read(long offset, int count) throws ProtocolException {
        // offsetは0か、現在の値と等しくなる必要がある
        if (offset == 0) {
            buf.position(0); 
        }
        if (offset != buf.position()) {
            throw new ProtocolException("bad offset in directory read");
        }

        Blob data = Blob.allocate(count).order(ByteOrder.LITTLE_ENDIAN);
        
        // BufferOverflowExceptionに対応するため、dataの容量だけ読み込む
        if (buf.remaining() <= count) {
            data.put(buf);
        } else {
            int oldLimit = buf.limit();
            buf.limit(buf.position() + count);
            data.put(buf);
            buf.limit(oldLimit);
        }
        
        return data;
    }

    public void close() {
        buf = null;
    }

    public Qid qid() throws ProtocolException {
        return this.stat().qid();
    }
}
