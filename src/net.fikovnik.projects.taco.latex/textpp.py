#!/usr/bin/env python

import sys
import subprocess
import logging
import re

HEADER = re.compile('%\s+textpp\s+([a-zA-Z_0-9]+)\s*(.*)')

def _textpp_graphviz(format, input, output):
    cmd = 'dot -T %s %s -o %s' % (format, input, output)

    logging.debug('[graphviz]: %s' % cmd)
    try:
        ext = subprocess.call(cmd, shell=True)
        if ext != 0:
            print >> sys.stderr, '%s: Failed %d' % (cmd, ext)
    except OSError as (errno, strerror):
        print >> sys.stderr, '%s: Unable to execute %d: %s' % (cmd, errno,
                strerror)


def call_macro(name, args):
    globals()['_textpp_'+name](*args)

def main(path):
    with open(path) as f:
        for (i,l) in enumerate(f.readlines()):
            match = HEADER.match(l)
            if match:
                logging.debug('[main]: Found macro call at line %d: %s' % (i,l))
                call_macro(match.group(1), match.group(2).split(','))

if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)
    main(sys.argv[1])
