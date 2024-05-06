package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Blob {
    private ByteBuffer buf;
    
    public static Blob allocate(int cap) {
        Blob b = new Blob();
        b.buf = ByteBuffer.allocate(cap);
        return b;
    }

    public Blob order(ByteOrder order) {
        this.buf.order(order);
        return this;
    }

    public int position() {
        return buf.position();
    }

    public void position(int newPos) {
        buf.position(newPos);
    }

    public int limit() {
        return buf.limit();
    }

    public void limit(int newLimit) {
        buf.limit(newLimit);
    }

    public boolean hasRemaining() {
        return buf.hasRemaining();
    }

    public int remaining() {
        return buf.remaining();
    }

    public void clear() {
        buf.clear();
    }
    
    public void flip() {
        buf.flip();
    }
    
    public byte[] array() {
        return buf.array();
    }

    public byte get() {
        return buf.get();
    }

    public void get(byte[] dst) {
        buf.get(dst);
    }

    public short getShort() {
        return buf.getShort();
    }

    public int getInt() {
        return buf.getInt();
    }

    public long getLong() {
        return buf.getLong();
    }

    public String getString() {
        short len = buf.getShort();
        byte[] str = new byte[len];
        buf.get(str);
        return new String(str);
    }

    public void put(byte v) {
        buf.put(v);
    }

    public void put(byte[] b) {
        buf.put(b);
    }

    public void put(Blob b) {
        buf.put(b.buf);
    }

    public void putShort(short v) {
        buf.putShort(v);
    }

    public void putShort(int pos, short v) {
        buf.putShort(pos, v);
    }

    public void putInt(int v) {
        buf.putInt(v);
    }

    public void putInt(int pos, int v) {
        buf.putInt(pos, v);
    }

    public void putLong(long v) {
        buf.putLong(v);
    }

    public void putString(String s) {
        short len = (short)s.length();
        putShort(len);
        buf.put(s.getBytes());
    }
}
