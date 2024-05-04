package proto;

public record Tattach(short tag, int fid, int afid, String uname, String aname) implements Tmessage {}