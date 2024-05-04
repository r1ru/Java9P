package proto;

public record Tversion(short tag, int msize, String version) implements Tmessage {};