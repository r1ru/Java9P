package proto;

public record Tcreate(short tag, int fid, String name, int perm, byte mode) implements Tmessage {}