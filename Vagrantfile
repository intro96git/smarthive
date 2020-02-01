# -*- mode: ruby -*-
# vi: set ft=ruby :

current_dir    = File.dirname(File.expand_path(__FILE__))

if not File.exist?('./local/local_config.rb')
  puts ""
  puts ""
  puts ""
  puts "#########################################"
  puts "#########################################"
  puts "Initializing new Vagrant configration file, #{current_dir}/local/local_config.rb"
  puts "You should edit this file before continuing..."
  puts "#########################################"
  puts "#########################################"
  puts ""
  puts ""
  puts ""

  `cp ./local/local_config.example.rb ./local/local_config.rb`
  raise("Check configuration file '#{current_dir}/local/local_config.rb' and re-run")
end

require_relative './local/local_config.rb'

localConfig = Config.new()


Vagrant.configure("2") do |config|
  config.vm.box = "archlinux/archlinux"

  config.vm.box_check_update = false

  if localConfig.network['type'] == :private
    config.vm.network "private_network", ip: localConfig.network['guestIp'], host_ip: localConfig.network['hostIp']

  elsif localConfig.network['type'] == :public
    config.vm.network "public_network"

  elsif localConfig.network['type'] == :forwarded
    if localConfig.network['hostIp'] == nil
      config.vm.network "forwarded_port", guest: localConfig.network['guestPort'], host: localConfig.network['hostPort']
    else
      config.vm.network "forwarded_port", guest: localConfig.network['guestPort'], host: localConfig.network['hostPort'], host_ip: localConfig.network['hostIp']
    end

  else
    puts "Bad Vagrant config; network type invalid."
    raise
  end


  localConfig.setSyncFolder(config.vm)
  

  config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #

    vb.memory = localConfig.computeMemory
    vb.cpus = localConfig.cpus

    localConfig.vmcustom.each { |arr| vb.customize arr }
  end

  config.vm.provision "shell", path: "provision.sh"
end
