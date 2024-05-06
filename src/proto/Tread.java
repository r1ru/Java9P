package proto;

public record Tread(short tag, int fid, long offset, int count) implements Tmessage {}