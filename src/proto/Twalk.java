package proto;

public record Twalk(short tag, int fid, int newfid, String[] wnames) implements Tmessage {}