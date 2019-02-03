#!/bin/sh

#TERM=xterm-color JAVA_OPTS=-Xms2G /usr/bin/sbt -Dsbt.ivy.home=/vagrant/.ivy2/ -Divy.home=/vagrant/.ivy2/ "$@"
TERM=xterm-color /usr/bin/sbt -Dsbt.ivy.home=/vagrant/.ivy2/ -Divy.home=/vagrant/.ivy2/ -mem 2048 "$@"
