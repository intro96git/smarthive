class VagrantConfig
  
  attr_reader :network, :cpus, :vmcustom

  def initialize()
    @cpus = 1
    @vmcustom = []
    @network = {
      'type'      => :private,
      'guestIp'   => '192.168.42.10',
      'hostIp'    => '192.168.42.1'
    }
  end



  ##############
  # Set the amount of RAM that the guest will use 
  def computeMemory()
    mem = `grep 'MemTotal' /proc/meminfo | sed -e 's/MemTotal://' -e 's/ kB//'`.to_i
    mem / 1024 / 4
  end


  ##############
  # Modify config to setup sync folder
  #
  # Due to the complexity of the various folder sync methods, this simply passes-thru
  # the config for manual construction.  Techicnally, any config (not just folders) can be
  # adjusted here, but that would confuse other developers.
  #
  # vm - The Vagrant machine config 
  #
  def setSyncFolder(vm)
    # do nothing
  end

end