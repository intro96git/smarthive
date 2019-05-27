#!/bin/sh

pacman -Syu --noconfirm
pacman -S --noconfirm extra/jdk-openjdk scala scala-docs scala-sources sbt git docker nfs-utils
if [ -f /vagrant/.bashrc ]; then
	cat /vagrant/.bashrc >> /home/vagrant/.bashrc
fi
curl -L https://github.com/lihaoyi/Ammonite/releases/download/1.6.7/2.12-1.6.7 > /usr/local/bin/amm && chmod +x /usr/local/bin/amm
cat >>/home/vagrant/.bashrc <<EOF
export PATH=/vagrant:$PATH
EOF
echo ==== Run 'vagrant up' again to start using the machine ====
poweroff
