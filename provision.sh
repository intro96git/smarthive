#!/bin/sh

pacman -Syu --noconfirm

# binutils, meson, gtest, gmock, cmake, pkg-config, fakeroot, and gcc needed to build and use AUR tool (auracle)
pacman -S --noconfirm extra/jdk-openjdk scala scala-docs scala-sources sbt git docker binutils meson gtest gmock gcc pkg-config cmake fakeroot

cd /tmp
sudo -u vagrant -- git clone https://aur.archlinux.org/auracle-git.git
cd auracle-git/
sudo -u vagrant -- makepkg -iCr --noconfirm
cd ..
rm -rf auracle-git
sudo -u vagrant -- auracle clone bloop
cd bloop
sudo -u vagrant -- makepkg -iCr --noconfirm
cd ..
rm -rf bloop

sudo -u vagrant -- systemctl --user enable bloop

if [ -f /vagrant/.bashrc ]; then
	cat /vagrant/.bashrc >> /home/vagrant/.bashrc
fi
curl -L https://github.com/lihaoyi/Ammonite/releases/download/1.7.4/2.12-1.7.4 > /usr/local/bin/amm && chmod +x /usr/local/bin/amm
cat >>/home/vagrant/.bashrc <<EOF
export PATH=/vagrant:$PATH
EOF
echo ==== Run 'vagrant up' again to start using the machine ====
poweroff
