import sys
import struct

def u16(d):
    return struct.unpack("<h", d)[0]

def u32(d):
    return struct.unpack("<i", d)[0]

def u64(d):
    return struct.unpack("<q", d)[0]

# https://man.cat-v.org/plan_9/5/intro
# https://github.com/brho/plan9/blob/master/sys/include/fcall.h
msg_types = f'''
Tversion
Rversion
Tauth
Rauth
Tattach
Rattach
Terror
Rerror
Tflush
Rflush
Twalk
Rwalk
Topen
Ropen
Tcreate
Rcreate
Tread
Rread
Twrite
Rwrite
Tclunk
Rclunk
Tremove
Rremove
Tstat
Rstat
Twstat
Rwstat
Tmax
'''.split()

def decode_strings(data, n = 1):
    strings = []
    p = 0
    for i in range(n):
        l = u16(data[p:p+2])
        p = p + 2
        strings.append(data[p:p+l].decode())
        p = p + l
    return strings

def decode_qids(data, n = 1):
    qids = []
    p = 0
    for i in range(n):
        qids.append(data[p:p+13])
        p = p + 13
    return qids

# https://man.cat-v.org/plan_9/5/version
def decode_Tversion_data(data):
    tag = u16(data[1:3])
    msize = u32(data[3:7])
    version, = decode_strings(data[7:])
    print(f'Tversion: tag = {tag:#x}, msize = {msize:#x}, version = {version:s}')

def decode_Rversion_data(data):
    tag = u16(data[1:3])
    msize = u32(data[3:7])
    version, = decode_strings(data[7:])
    print(f'Rversion: tag = {tag:#x}, msize = {msize:#x}, version = {version:s}')

# https://man.cat-v.org/plan_9/5/attach
def decode_Tattach_data(data):
    tag = u16(data[1:3])
    fid = u32(data[3:7])
    afid = u32(data[7:11])
    uname, = decode_strings(data[11:])
    print(f'Tattach: tag = {tag:#x}, fid = {fid:#x}, afid = {afid:#x}, uname = {uname:s}')

# TODO: aqidの意味を調べる
def decode_Rattach_data(data):
    tag = u16(data[1:3])
    aqid = data[3:16]
    print(f'Rattach: tag = {tag:#x}, aqid = {aqid}')

# https://man.cat-v.org/plan_9/5/stat
def decode_Tstat_data(data):
    tag = u16(data[1:3])
    fid = u32(data[3:7])
    print(f'Tstat: tag = {tag:#x}, fid = {fid:#x}')

def decode_Rstat_data(data):
    tag = u16(data[1:3])
    n = u16(data[3:5])
    size = u16(data[5:7])
    t1pe = u16(data[7:9])
    dev = u32(data[9:13])
    qid_type = data[13]
    qid_vers = u32(data[14:18])
    qid_path = u64(data[18:26])
    mode = u32(data[26:30])
    atime = u32(data[30:34])
    mtime = u32(data[34:38])
    length = u64(data[38:46])
    name, uid, gid, muid = decode_strings(data[46:], 4)
    print(f'''Rstat: tag = {tag:#x}, n = {n:#x} size = {size: #x}, type = {t1pe:#x}, dev = {dev:#x}, 
          qid_type = {qid_type}, qid_vers = {qid_vers:#x}, qid_path={qid_path:#x}
          mode = {mode:#x}, atime = {atime: #x}, mtime = {mtime:#x}, length = {length:#x}
          name = {name:s}, uid = {uid:s}, gid = {gid:s}, muid = {muid:s}''')

# https://man.cat-v.org/plan_9/5/walk
def decode_Twalk_data(data):
    tag = u16(data[1:3])
    fid = u32(data[3:7])
    newfid = u32(data[7:11])
    nwname = u16(data[11:13])
    wname = decode_strings(data[13:], nwname)
    print(f'Twalk: tag = {tag:#x}, fid = {fid:#x}, newfid = {newfid:#x}, wname = {wname}')

def decode_Rwalk_data(data):
    tag = u16(data[1:3])
    nwqid = u16(data[3:5])
    qid = decode_qids(data[5:], nwqid)
    print(f'Rwalk: tag = {tag:#x}, nwqid = {nwqid:#x}, qid = {qid}')

# https://man.cat-v.org/plan_9/5/open
def decode_Topen_data(data):
    tag = u16(data[1:3])
    fid = u32(data[3:7])
    mode = data[7]
    print(f'Topen: tag = {tag:#x}, fid = {fid:#x}, mode = {mode}')

def decode_Ropen_data(data):
    tag = u16(data[1:3])
    qid = decode_qids(data[3:])
    iounit = u32(data[16:20])
    print(f'Ropen: tag = {tag:#x}, qid = {qid}, iounit = {iounit:#x}')

# https://man.cat-v.org/plan_9/5/clunk
def decode_Tclunk_data(data):
    tag = u16(data[1:3])
    fid = u32(data[3:7])
    print(f'Tclunk: tag = {tag:#x}, fid = {fid:#x}')

def decode_Rclunk_data(data):
    tag = u16(data[1:3])
    print(f'Rclunk: tag = {tag:#x}')

# https://man.cat-v.org/plan_9/5/read
def decode_Tread_data(data):
    tag = u16(data[1:3])
    fid = u32(data[3:7])
    offset = u64(data[7:15])
    count = u32(data[15:19])
    print(f'Tread: tag = {tag:#x}, fid = {fid:#x}, offset = {offset:#x}, count = {count:#x}')

def decode_Rread_data(data):
    tag = u16(data[1:3])
    count = u32(data[3:7])
    d = data[7:7+count]
    print(f'Rread: tag = {tag:#x}, count = {count:#x}, data = {d}')

def decode_msg_data(data):
    msg_type = msg_types[data[0] - 100]

    match msg_type:
        case 'Tversion':
            decode_Tversion_data(data)
        case 'Tattach':
            decode_Tattach_data(data)
        case 'Tstat':
            decode_Tstat_data(data)
        case 'Twalk':
            decode_Twalk_data(data)
        case 'Topen':
            decode_Topen_data(data)
        case 'Tclunk':
            decode_Tclunk_data(data)
        case 'Tread':
            decode_Tread_data(data)
        case 'Rversion':
            decode_Rversion_data(data)
        case 'Rattach':
            decode_Rattach_data(data)
        case 'Rstat':
            decode_Rstat_data(data)
        case 'Rwalk':
            decode_Rwalk_data(data)
        case 'Ropen':
            decode_Ropen_data(data)
        case 'Rclunk':
            decode_Rclunk_data(data)
        case 'Rread':
            decode_Rread_data(data)
        case _:
            print(data)

def main():
    if len(sys.argv) != 2:
        print(f'Usage: {sys.argv[0]:s} <RAW DATA>')
        exit(1)
    
    with open(sys.argv[1], 'rb') as f:
        while True:
            size = f.read(4)
            
            if not size:
                break

            size = u32(size)
            data = f.read(size - 4)
            decode_msg_data(data)

if __name__ == '__main__':
    main()