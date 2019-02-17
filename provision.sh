#!/bin/sh

pacman -Syu --noconfirm
pacman -S --noconfirm extra/jdk-openjdk scala scala-docs scala-sources sbt git docker
if [ -f /vagrant/.bashrc ]; then
	cat /vagrant/.bashrc >> /home/vagrant/.bashrc
fi
cat >>/home/vagrant/.bashrc <<EOF
export PATH=/vagrant:$PATH
EOF
echo ==== Run 'vagrant up' again to start using the machine ====
shutdown --halt
