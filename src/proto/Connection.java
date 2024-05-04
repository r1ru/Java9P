package proto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Connection {
    public int msize;
    public String version;

    private ArrayList<Fid> fidSpace;
    private Client client;

    public Connection(int msize, String version) {
        this.msize = msize;
        this.version = version;
        this.fidSpace = new ArrayList<Fid>();
    }
    
    public Fid registerClient(String uname, int fid, Path path) throws ProtocolException{
        // ルートディレクトリが存在しないかディレクトリでない場合はエラー。
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new ProtocolException("No such file or directory");
        }

        // TODO: fidが使用中の場合エラーを返す。
        // TODO: 複数クライアントに対応する。
        Fid root = new Fid(fid, path);
        fidSpace.add(root);
        client = new Client(uname, root);

        return root;
    }
}
