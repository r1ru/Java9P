package proto;

import util.Blob;

public interface Rmessage {
    short tag();
    void write(Blob buf); 
}