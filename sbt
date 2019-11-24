#!/bin/sh

TERM=xterm-color /usr/bin/sbt -mem 2048 "$@"
