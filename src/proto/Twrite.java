package proto;

public record Twrite(short tag, int fid, long offset, int count, String data) implements Tmessage {}