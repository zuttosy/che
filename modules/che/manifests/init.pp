class che {

  $config_dirs = [
    "/opt/che/config/che/",
    "/opt/che/config/che/conf"
  ]

# creating folders
  file { $config_dirs:
    ensure  => "directory",
    mode    => "755",
  }

# creating che.env
  file { "/opt/che/config/che/che.env":
    ensure  => "present",
    content => template("che/che.env.erb"),
    mode    => "644",
  }

# creating che.properties
  file { "/opt/che/config/che/conf/che.properties":
    ensure  => "present",
    content => template("che/che.properties.erb"),
    mode    => "644",
    require => File[$config_dirs],
  }
}
