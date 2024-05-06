package proto;

public record Topen(short tag, int fid, byte mode) implements Tmessage {}
