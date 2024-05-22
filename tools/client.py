import sys
import socket
from p9 import *

def main():
    if len(sys.argv) != 3:
        print(f'Usage: {sys.argv[0]:s} <HOST> <PORT>')
        exit(1)
    
    io = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    io.connect((sys.argv[1], int(sys.argv[2], 10)))
    
    # コネクションを確立する
    # version
    io.send(encode_Tversion(-1, 0x2000, b'9P2000'))
    decode_msg(io.recv(1024))

    # attach
    io.send(encode_Tattach(0, 0, -1, b'riruoda', b''))
    decode_msg(io.recv(1024))

    # ルートフォルダの以下のファイル一覧を取得
    # stat
    io.send(encode_Tstat(0, 0))
    decode_msg(io.recv(1024))

    # walk
    io.send(encode_Twalk(0, 0, 1, []))
    decode_msg(io.recv(1024))

    # open
    io.send(encode_Topen(0, 1, 0))
    decode_msg(io.recv(1024))

    # read
    io.send(encode_Tread(0, 1, 0, 0x1fe8))
    decode_msg(io.recv(1024))

    # clunk
    io.send(encode_Tclunk(0, 1))
    decode_msg(io.recv(1024))

    # ルートフォルダの下に'a'という新しいファイルを作る
    # walk
    io.send(encode_Twalk(0, 0, 1, []))
    decode_msg(io.recv(1024))

    # create
    io.send(encode_Tcreate(0, 1, b'a', 0x1a4, 1))
    decode_msg(io.recv(1024))
    
    # 'a'に書き込みを行う (encode_Twriteを上記のように修正した前提)
    # write
    io.send(encode_Twrite(0, 1, 0, b'a\n'))
    decode_msg(io.recv(1024))
    
    # 'a'を削除する
    io.send(encode_Tremove(0, 1))
    decode_msg(io.recv(1024))

if __name__ == '__main__':
    main()