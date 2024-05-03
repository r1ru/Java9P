import sys
from p9 import *

def main():
    if len(sys.argv) != 2:
        print(f'Usage: {sys.argv[0]:s} <RAW msg>')
        exit(1)
    
    with open(sys.argv[1], 'rb') as f:
        while True:
            msg = f.read(4)
            
            if not msg:
                break

            size = u32(msg)
            msg = msg + f.read(size - 4)
            decode_msg(msg)

if __name__ == '__main__':
    main()