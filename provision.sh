#!/bin/sh

pacman -Syu --noconfirm
pacman -S --noconfirm extra/jdk-openjdk scala scala-docs scala-sources sbt git docker
shutdown --reboot
