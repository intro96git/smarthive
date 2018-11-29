#!/bin/sh

pacman -Syu --noconfirm
pacman -S --noconfirm extra/jdk-openjdk scala scala-docs scala-sources sbt git docker
cat >>/home/vagrant/.bashrc <<EOF
export PATH=/vagrant:$PATH
EOF
shutdown --reboot
