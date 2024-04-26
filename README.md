# 環境構築
- [u9fs](https://github.com/unofficial-mirror/u9fs)と[9pfuse](https://github.com/aperezdc/9pfuse)をビルドする。方法はそれぞれのREADMEを参照。

# 動作確認
以下のコマンドを実行する。
```
$ git clone https://github.com/r1ru/Java9P
$ cd Java9P
$ mkdir server client
$ echo "flag" > ./server/flag
$ socat TCP4-LISTEN:1234,range=127.0.0.1/32 EXEC:"./u9fs/u9fs -a none -u `whoami` ./server"
$ sudo tcpflow -i lo port 1234 # 別のターミナルで
$ ./9pfuse/build/9pfuse 'tcp!localhost!1234' ./client # 別のターミナルで
$ cd client
$ ls
```
最後のlsコマンドを実行した際にflagというファイルが表示されていれば成功。以下のコマンドで後片付け。
```
$ fusermount -u client
```
次に通信内容を確認する。[toolsディレクトリ](https://github.com/r1ru/Java9P/tree/main/tools)に解析プログラムがあるので、これを実行して9Pプロトコルの通信であることを確認する。
```
$ python3 ./tools/decode.py <ファイル名>
```

# 参考文献
- https://man.cat-v.org/plan_9/5/intro
