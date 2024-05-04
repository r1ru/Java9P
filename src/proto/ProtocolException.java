package proto;

public class ProtocolException extends Exception {
    public ProtocolException(String msg) {
        super(msg);
    }
    
    public ProtocolException(String msg, Throwable cause) {
        super(msg, cause);
    }
}