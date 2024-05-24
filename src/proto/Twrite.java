package proto;
import java.nio.ByteBuffer;

public record Twrite(short tag, int fid, long offset, ByteBuffer data) implements Tmessage {}