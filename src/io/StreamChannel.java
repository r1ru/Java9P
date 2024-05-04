package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import proto.MessageType;
import proto.Rmessage;
import proto.Tattach;
import proto.Tmessage;
import proto.Tversion;

public class StreamChannel {
    private static final int BUF_SIZE = 4096;
    private InputStream is;
    private OutputStream os;
    private ByteBuffer buf;

    public StreamChannel(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        this.buf = ByteBuffer.allocate(StreamChannel.BUF_SIZE);
        this.buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    // wrappers
    public int read() throws IOException {
        buf.position(0);
        return is.read(buf.array());
    }

    public byte read8() {
        return buf.get();
    }

    public short read16() {
        return buf.getShort();
    }

    public int read32() {
        return buf.getInt();
    }

    public String readString() {
        short len = buf.getShort();
        byte[] str = new byte[len];
        buf.get(str);
        return new String(str, StandardCharsets.UTF_8);

    }

    public Tmessage recv() throws IOException {
        int avail = read();

        if (avail < 1)
            return null;

        int msg_size = read32();
        assert avail == msg_size;
        byte msg_type = read8();

        var msg = switch (msg_type) {
            case MessageType.TVERSION -> new Tversion(read16(), read32(), readString());
            case MessageType.TATTACH -> new Tattach(read16(), read32(), read32(), readString(), readString());
            default -> null;
        };

        return msg;
    }

    public void reply(Rmessage msg) throws IOException {
        os.write(msg.raw());
    }
}
