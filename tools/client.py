import sys
import socket
from p9 import *

def main():
    if len(sys.argv) != 3:
        print(f'Usage: {sys.argv[0]:s} <HOST> <PORT>')
        exit(1)
    
    io = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    io.connect((sys.argv[1], int(sys.argv[2], 10)))

    # version
    io.send(encode_Tversion(-1, 0x2000, b'9P2000'))
    decode_msg(io.recv(1024))

    # attach
    io.send(encode_Tattach(0, 0, -1, b'riruoda', b''))
    decode_msg(io.recv(1024))

    # stat
    io.send(encode_Tstat(0, 0))
    decode_msg(io.recv(1024))

    # walk
    io.send(encode_Twalk(0, 0, 1, []))
    decode_msg(io.recv(1024))

    # open
    io.send(encode_Topen(0, 1, 0))
    decode_msg(io.recv(1024))

    # read(下のTwalkのエラーを直すため追加)
    io.send(encode_Tread(0, 1, 0, 0x1fe8))
    decode_msg(io.recv(1024))

    # clunk(下のTwalkのエラーを直すため追加)
    io.send(encode_Tclunk(0, 1))
    decode_msg(io.recv(1024))

    # walk
    io.send(encode_Twalk(0, 0, 1, []))
    decode_msg(io.recv(1024))

    # create
    io.send(encode_Tcreate(0, 1, b'a', 0x1a4, 1))
    decode_msg(io.recv(1024))

    #この先2つはTwriteを確認するために追加 client.pyが動かなくなったので一旦コメントアウトしてる
    # stat(追加)
    #io.send(encode_Tstat(0, 1))
    #decode_msg(io.recv(1024))

    # walk(追加)
    #io.send(encode_Twalk(0, 0, 2, [b'a']))
    #decode_msg(io.recv(1024))

    # write
    #io.send(encode_Twrite(0, 1, 0, 2, b'a\n'))
    #decode_msg(io.recv(1024))

    # walk (Tremove確認のためにこれを追加)
    io.send(encode_Twalk(0, 0, 2, [b'a']))
    decode_msg(io.recv(1024))

    # delete
    io.send(encode_Tremove(0, 2))
    decode_msg(io.recv(1024))

    # clunk
    io.send(encode_Tclunk(0, 1))
    decode_msg(io.recv(1024))
    
if __name__ == '__main__':
    main()