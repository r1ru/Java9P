import java.io.IOException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import io.StreamChannel;
import proto.Connection;
import proto.Fid;
import proto.Qid;
import proto.Rerror;
import proto.Rmessage;
import proto.Ropen;
import proto.Rcreate;
import proto.Rread;
import proto.Rwrite;
import proto.Rstat;
import proto.Tmessage;
import proto.Tstat;
import proto.Tversion;
import proto.Twalk;
import proto.Rversion;
import proto.Rwalk;
import proto.Tattach;
import proto.Tclunk;
import proto.Tcreate;
import proto.Rattach;
import proto.Rclunk;
import proto.Topen;
import proto.Tread;
import proto.Twrite;
import proto.Tremove;
import proto.Rremove;


public class Server {
    private int port;
    private Connection conn;
    private Path rootPath;

    public Server(int port, Path rootPath) {
        this.port = port;
        this.rootPath = rootPath;
    }

    public void run() {
        try {
            ServerSocket socket = new ServerSocket(this.port);
            try {
                Socket newSocket = socket.accept();
                StreamChannel ch = new StreamChannel(newSocket.getInputStream() ,newSocket.getOutputStream());

                while (true) {
                    Tmessage msg = ch.recv();
                    Rmessage replyMsg = null;

                    if (msg == null)
                        break;
                    
                    // メッセージの種類ごとに処理する。
                    try {
                        System.out.println("Received: " + msg);

                        if (msg instanceof Tversion) {
                            // コネクションを初期化する。(msizeとversionはとりあえず固定値)
                            conn = new Connection(0x2000, "9P2000");
                            replyMsg = new Rversion((short)-1, conn.msize, conn.version);
                        } 
                        else if (msg instanceof Tattach req) {
                            // 新しいクライアントを登録する。
                            Fid tree = conn.registerClient(req.uname(), req.fid(), rootPath);
                            replyMsg = new Rattach(req.tag(), tree.qid());
                        }
                        else if (msg instanceof Twalk req) {
                            Fid fid = conn.findFid(req.fid());
                            ArrayList<Qid> qids = new ArrayList<Qid>();
                            // TODO: エラー処理を仕様(https://man.cat-v.org/plan_9/5/walk)に合わせる。
                            Path newPath = fid.path;
                            for (String s : req.wnames()) {
                                newPath = newPath.resolve(s).normalize();
                                // 存在しない場合はエラー
                                if (!Files.exists(newPath)) {
                                    throw new ProtocolException("No such file or directory");
                                }
                                qids.add(Qid.of(newPath));
                            }
                            if (req.fid() != req.newfid()) {
                                conn.addFid(new Fid(req.newfid(), newPath));
                            }
                            replyMsg = new Rwalk(req.tag(), qids);
                        }
                        else if (msg instanceof Topen req) {
                            Fid fid = conn.findFid(req.fid());
                            fid.open(req.mode());
                            replyMsg = new Ropen(req.tag(), fid.qid(), 0);
                        }
                        else if (msg instanceof Tcreate req) {
                            // TODO: Tcreateの処理をして、Rcreateを返す。
                            Fid fid = conn.findFid(req.fid());
                             if (fid == null) {
                                // Fidが存在しない場合エラー処理
                                continue;
                            }
                            Path newPath = fid.path.resolve(req.name()).normalize();
                            Fid newFid = fid.create(req.fid(), newPath);
                            conn.addFid(newFid);
                            replyMsg = new Rcreate(req.tag(), newFid.qid(), 0);
                        }
                        else if (msg instanceof Tread req) {
                            Fid fid = conn.findFid(req.fid());
                            replyMsg = new Rread(req.tag(), fid.read(req.offset(), req.count()));
                        }
                        else if (msg instanceof Twrite req) {
                            Fid fid = conn.findFid(req.fid());
                            int bytesWritten = fid.write(req.offset(), req.data());
                            replyMsg = new Rwrite(req.tag(), bytesWritten);
                        }
                        else if (msg instanceof Tremove req) {
                            Fid fid = conn.findFid(req.fid());
                            Files.delete(fid.path);
                            conn.removeFid(req.fid());
                            replyMsg = new Rremove(req.tag());
                        }
                        else if (msg instanceof Tclunk req) {
                            conn.removeFid(req.fid());
                            replyMsg = new Rclunk(req.tag());
                        }
                        else if (msg instanceof Tstat req) {
                            Fid fid = conn.findFid(req.fid());
                            replyMsg = new Rstat(req.tag(), fid.stat());
                        }
                    }
                    catch (Exception e) {
                        // エラーが起きた場合はRerrorメッセージを返す。
                        replyMsg = new Rerror(msg.tag(), e.getMessage());
                    }
                    finally {
                        System.out.println("Reply: " + replyMsg);
                        ch.reply(replyMsg);
                    }
                }
                newSocket.close();
            }
            finally {
                socket.close();
            }

        }
        catch (Exception e) {
            System.out.println ("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java Server <PORT> <PATH>");
            System.exit(0);
        }

        Server s = new Server(Integer.valueOf(args[0]), Paths.get(args[1]));
        s.run();
    }
}