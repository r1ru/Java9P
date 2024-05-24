package proto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import util.Blob;

public class Fid {
    public int fid;
    public Path path;
    private boolean isOpen = false;
    private Blob buf;
    private FileChannel channel = null;

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
        // openできるのは最大1クライアントまで
        if (isOpen) {
            throw new ProtocolException("bad use of fid");
        }

        // ファイルだった場合に、channelを初期化
        if (!Files.isDirectory(path)) {
            Set<OpenOption> options = new HashSet<>();

            // modeごとにoptionを設定
            if (mode == 0x0) { // OREAD
                options.add(StandardOpenOption.READ);
            } else if (mode == 0x1) { // OWRITE
                options.add(StandardOpenOption.WRITE);
            } else if (mode == 0x2) { // ORDWR
                options.add(StandardOpenOption.READ);
                options.add(StandardOpenOption.WRITE);
            } else if (mode == 0x3) { // OEXEC
                options.add(StandardOpenOption.READ);
            } else if (mode == 0x10) { // OTRUNC
                options.add(StandardOpenOption.TRUNCATE_EXISTING);
            } else if (mode == 0x40) { // ORCLOSE
                options.add(StandardOpenOption.DELETE_ON_CLOSE);
            }

            try {
                // channelを初期化
                channel = FileChannel.open(path, options);
                isOpen = true;
            } catch (IOException e) {
                throw new ProtocolException(e.getMessage(), e);
            }

        } else {
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

    public Fid create (int fid, Path filePath, byte mode) throws ProtocolException {
        try {
            //ファイルを作成
            Files.createFile(filePath); 
            // 新しいFidを作成
            Fid new_fid = new Fid(fid, filePath);
            // openメソッドを呼び出す
            new_fid.open(mode);
            // 新しいFidを返す
            return new_fid;
        } catch (IOException e) {
            throw new ProtocolException("Failed to create file: " + e.getMessage(), e);
        }
    }

    public int write (ByteBuffer data, long offset) throws ProtocolException {
        if (!isOpen) {
            throw new ProtocolException("Bad use of fid");
        }

        try {
            channel.position(offset);
            return channel.write(data);
        } catch (IOException e) {
            throw new ProtocolException(e.getMessage(), e);
        } 


    }

    public void remove(Path filePath) throws ProtocolException {
        try {
            // ファイルを消去
            Files.delete(filePath);
        } catch (IOException e) {
            throw new ProtocolException("Failed to remove file: " + e.getMessage(), e);
        } 
    }

    public void close() {
        buf = null;
    }

    public Qid qid() throws ProtocolException {
        return this.stat().qid();
    }
}
