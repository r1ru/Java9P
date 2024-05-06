import struct

# https://man.cat-v.org/plan_9/5/intro
# https://github.com/brho/plan9/blob/master/sys/include/fcall.h
class MessageType:
    TVERSION    = 100
    RVERSION    = 101
    TAUTH       = 102
    RAUTH       = 103
    TATTACH     = 104
    RATTACH     = 105
    TERROR      = 106
    RERROR      = 107
    TFLUSH      = 108
    RFLUSH      = 109
    TWALK       = 110
    RWALK       = 111
    TOPEN       = 112
    ROPEN       = 113
    TCREATE     = 114
    RCREATE     = 115
    TREAD       = 116
    RREAD       = 117
    TWRITE      = 118
    RWRITE      = 119
    TCLUNK      = 120
    RCLUNK      = 121
    TREMOVE     = 122
    RREMOVE     = 123
    TSTAT       = 124
    RSTAT       = 125
    TWSTAT      = 126
    RWSTAT      = 127
    TMAX        = 128

# utils
def u16(d):
    return struct.unpack("<h", d)[0]

def u32(d):
    return struct.unpack("<i", d)[0]

def u64(d):
    return struct.unpack("<q", d)[0]

def p8(d):
    return struct.pack("<b", d)

def p16(d):
    return struct.pack("<h", d)

def p32(d):
     return struct.pack("<i", d)

def p64(d):
    return struct.pack("<q", d)

def pstr(d):
    return p16(len(d)) + d

def pmsg(d):
    return p32(len(d) + 4) + d

# decoders
def decode_strings(msg, n = 1):
    strings = []
    p = 0
    for i in range(n):
        l = u16(msg[p:p+2])
        p = p + 2
        strings.append(msg[p:p+l].decode())
        p = p + l
    return strings

def decode_qids(msg, n = 1):
    qids = []
    p = 0
    for i in range(n):
        qids.append(msg[p:p+13])
        p = p + 13
    return qids

# https://man.cat-v.org/plan_9/5/version
def decode_Tversion(msg):
    tag = u16(msg[1:3])
    msize = u32(msg[3:7])
    version, = decode_strings(msg[7:])
    print(f'Tversion: tag = {tag:#x}, msize = {msize:#x}, version = {version:s}')

def decode_Rversion(msg):
    tag = u16(msg[1:3])
    msize = u32(msg[3:7])
    version, = decode_strings(msg[7:])
    print(f'Rversion: tag = {tag:#x}, msize = {msize:#x}, version = {version:s}')

# https://man.cat-v.org/plan_9/5/attach
def decode_Tattach(msg):
    tag = u16(msg[1:3])
    fid = u32(msg[3:7])
    afid = u32(msg[7:11])
    uname, = decode_strings(msg[11:])
    print(f'Tattach: tag = {tag:#x}, fid = {fid:#x}, afid = {afid:#x}, uname = {uname:s}')

def decode_Rattach(msg):
    tag = u16(msg[1:3])
    qid_type = msg[3]
    qid_vers = u32(msg[4:8])
    qid_path = u64(msg[8:16])
    print(f'Rattach: tag = {tag:#x}, qid_type = {qid_type:#x}, qid_vers = {qid_vers:#x}, qid_path={qid_path:#x}')

# https://man.cat-v.org/plan_9/5/stat
def decode_Tstat(msg):
    tag = u16(msg[1:3])
    fid = u32(msg[3:7])
    print(f'Tstat: tag = {tag:#x}, fid = {fid:#x}')

def decode_Rstat(msg):
    tag = u16(msg[1:3])
    n = u16(msg[3:5])
    size = u16(msg[5:7])
    t1pe = u16(msg[7:9])
    dev = u32(msg[9:13])
    qid_type = msg[13]
    qid_vers = u32(msg[14:18])
    qid_path = u64(msg[18:26])
    mode = u32(msg[26:30])
    atime = u32(msg[30:34])
    mtime = u32(msg[34:38])
    length = u64(msg[38:46])
    name, uid, gid, muid = decode_strings(msg[46:], 4)
    print(f'''Rstat: tag = {tag:#x}, n = {n:#x} size = {size: #x}, type = {t1pe:#x}, dev = {dev:#x}, 
          qid_type = {qid_type}, qid_vers = {qid_vers:#x}, qid_path={qid_path:#x}
          mode = {mode:#x}, atime = {atime: #x}, mtime = {mtime:#x}, length = {length:#x}
          name = {name:s}, uid = {uid:s}, gid = {gid:s}, muid = {muid:s}''')

# https://man.cat-v.org/plan_9/5/walk
def decode_Twalk(msg):
    tag = u16(msg[1:3])
    fid = u32(msg[3:7])
    newfid = u32(msg[7:11])
    nwname = u16(msg[11:13])
    wname = decode_strings(msg[13:], nwname)
    print(f'Twalk: tag = {tag:#x}, fid = {fid:#x}, newfid = {newfid:#x}, wname = {wname}')

def decode_Rwalk(msg):
    tag = u16(msg[1:3])
    nwqid = u16(msg[3:5])
    qid = decode_qids(msg[5:], nwqid)
    print(f'Rwalk: tag = {tag:#x}, nwqid = {nwqid:#x}, qid = {qid}')

# https://man.cat-v.org/plan_9/5/open
def decode_Topen(msg):
    tag = u16(msg[1:3])
    fid = u32(msg[3:7])
    mode = msg[7]
    print(f'Topen: tag = {tag:#x}, fid = {fid:#x}, mode = {mode}')

def decode_Ropen(msg):
    tag = u16(msg[1:3])
    qid = decode_qids(msg[3:])
    iounit = u32(msg[16:20])
    print(f'Ropen: tag = {tag:#x}, qid = {qid}, iounit = {iounit:#x}')

# https://man.cat-v.org/plan_9/5/clunk
def decode_Tclunk(msg):
    tag = u16(msg[1:3])
    fid = u32(msg[3:7])
    print(f'Tclunk: tag = {tag:#x}, fid = {fid:#x}')

def decode_Rclunk(msg):
    tag = u16(msg[1:3])
    print(f'Rclunk: tag = {tag:#x}')

# https://man.cat-v.org/plan_9/5/read
def decode_Tread(msg):
    tag = u16(msg[1:3])
    fid = u32(msg[3:7])
    offset = u64(msg[7:15])
    count = u32(msg[15:19])
    print(f'Tread: tag = {tag:#x}, fid = {fid:#x}, offset = {offset:#x}, count = {count:#x}')

def decode_Rread(msg):
    tag = u16(msg[1:3])
    count = u32(msg[3:7])
    d = msg[7:7+count]
    print(f'Rread: tag = {tag:#x}, count = {count:#x}, msg = {d}')

def decode_Rerror(msg):
    tag = u16(msg[1:3])
    ename, = decode_strings(msg[3:])
    print(f'Rerror: tag = {tag:#x}, ename = {ename}')

def decode_msg(msg):
    msg = msg[4:] # skip size

    match msg[0]:
        case MessageType.TVERSION:
            decode_Tversion(msg)
        case MessageType.TATTACH:
            decode_Tattach(msg)
        case MessageType.TSTAT:
            decode_Tstat(msg)
        case MessageType.TWALK:
            decode_Twalk(msg)
        case MessageType.TOPEN:
            decode_Topen(msg)
        case MessageType.TCLUNK:
            decode_Tclunk(msg)
        case MessageType.TREAD:
            decode_Tread(msg)
        case MessageType.RVERSION:
            decode_Rversion(msg)
        case MessageType.RATTACH:
            decode_Rattach(msg)
        case MessageType.RSTAT:
            decode_Rstat(msg)
        case MessageType.RWALK:
            decode_Rwalk(msg)
        case MessageType.ROPEN:
            decode_Ropen(msg)
        case MessageType.RCLUNK:
            decode_Rclunk(msg)
        case MessageType.RREAD:
            decode_Rread(msg)
        case MessageType.RERROR:
            decode_Rerror(msg)
        case _:
            print(msg)

# encoders
def encode_Tversion(tag, msize, version):
    msg = p8(MessageType.TVERSION) + p16(tag) + p32(msize) + p16(len(version)) + version
    return pmsg(msg)

def encode_Tattach(tag, fid, afid, uname, aname):
    msg = p8(MessageType.TATTACH) + p16(tag) + p32(fid) + p32(afid) + pstr(uname) + pstr(aname)
    return pmsg(msg)

def encode_Twalk(tag, fid, newfid, wname):
    msg = p8(MessageType.TWALK) + p16(tag) + p32(fid) + p32(newfid) + p16(len(wname))
    for s in wname:
        msg += pstr(s)
    return pmsg(msg)

def encode_Topen(tag, fid, mode):
    msg = p8(MessageType.TOPEN) + p16(tag) + p32(fid) + p8(mode)
    return pmsg(msg)

def encode_Tread(tag, fid, offset, count):
    msg = p8(MessageType.TREAD) + p16(tag) + p32(fid) + p64(offset) + p32(count)
    return pmsg(msg)

def encode_Tstat(tag, fid):
    msg = p8(MessageType.TSTAT) + p16(tag) + p32(fid)
    return pmsg(msg)