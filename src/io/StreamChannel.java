package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import proto.MessageType;
import proto.Rmessage;
import proto.Tattach;
import proto.Tmessage;
import proto.Topen;
import proto.Tstat;
import proto.Tversion;
import proto.Twalk;
import util.Blob;

public class StreamChannel {
    private static final int BUF_SIZE = 4096;
    private InputStream is;
    private OutputStream os;
    private Blob rbuf;
    private Blob wbuf;

    public StreamChannel(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        this.rbuf = Blob.allocate(StreamChannel.BUF_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        this.wbuf = Blob.allocate(StreamChannel.BUF_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    }

    public int read() throws IOException {
        rbuf.clear();
        return is.read(rbuf.array());
    }

    public byte read8() {
        return rbuf.get();
    }

    public short read16() {
        return rbuf.getShort();
    }

    public int read32() {
        return rbuf.getInt();
    }

    public String readString() {
        short len = rbuf.getShort();
        byte[] str = new byte[len];
        rbuf.get(str);
        return new String(str, StandardCharsets.UTF_8);
    }

    public String[] readStrings() {
        short n = read16();
        String [] a = new String[n];
        for (short i = 0; i < n; i++) {
            a[i] = readString();
        }
        return a;
    }

    public Tmessage recv() throws IOException {
        int avail = read();

        if (avail < 1)
            return null;

        int msgSize = read32();
        assert avail == msgSize;
        byte msgType = read8();

        var msg = switch (msgType) {
            case MessageType.TVERSION -> new Tversion(read16(), read32(), readString());
            case MessageType.TATTACH -> new Tattach(read16(), read32(), read32(), readString(), readString());
            case MessageType.TWALK -> new Twalk(read16(), read32(), read32(), readStrings());
            case MessageType.TOPEN -> new Topen(read16(), read32(), read8());
            case MessageType.TSTAT -> new Tstat(read16(), read32());
            default -> null;
        };

        return msg;
    }

    public void reply(Rmessage msg) throws IOException {
        msg.write(wbuf);
        os.write(wbuf.array(), 0, wbuf.position());
        wbuf.clear();
    }
}
