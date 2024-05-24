package proto;

import java.nio.ByteBuffer;

public interface Rmessage {
    short tag();
    void write(ByteBuffer buf); 
}