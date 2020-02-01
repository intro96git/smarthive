require_relative '../vagrant_config.rb'

class Config < VagrantConfig

  def initialize()
    super()
    
    ## network configuration
    # @network = {
    #   'type'      => :private,
    #   'guestIp'   => '192.168.42.10',
    #   'hostIp'    => '192.168.42.1'
    # }
    # attr_reader @network = {
    #   'type'      => :forwarded,
    #   'hostIp'    => '127.0.0.1',
    #   'guestPort' => 80,
    #   'hostPort'  => 8080
    # }
    # attr_reader @network = {
    #   'type'      => :forwarded,
    #   'guestPort' => 80,
    #   'hostPort'  => 8080
    # }
    # attr_reader @network = {
    #   'type'      => :public
    # }
  
    
    ## Number of CPUs for the guest to use
    # @cpus = 1
  
    ## VM customizations to be applied
    # e.g.: for virtualbox, enabling IOAPIC
    # @vmcustom = [
      # ["modifyvm", :id, "--ioapic", "on"]
    # ]
    
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
    # vm.synced_folder "../data", "/vagrant_data"
  end

end