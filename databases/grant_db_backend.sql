l
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO root IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO root@localhost IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO root@testbed006 IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO root@testbed006.cnaf.infn.it IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO storm IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO storm@testbed006 IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO storm@localhost IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO root@testbed006 IDENTIFIED BY 'storm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO root@testbed006.cnaf.infn.it IDENTIFIED BY 'storm' WITH GRANT OPTION;

#
# Modificare METTENDO al posto di egrid-7 il nome del HOST 
# 

# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO root                      IDENTIFIED BY 'storm' WITH GRANT OPTION;
# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO 'root'@'localhost'        IDENTIFIED BY 'storm' WITH GRANT OPTION;
# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO 'root'@'egrid-7'          IDENTIFIED BY 'storm' WITH GRANT OPTION;
# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO 'root'@'egrid-7.egrid.it' IDENTIFIED BY 'storm' WITH GRANT OPTION;
# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO storm                     IDENTIFIED BY 'storm' WITH GRANT OPTION;
# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO 'storm'@'localhost'       IDENTIFIED BY 'storm' WITH GRANT OPTION;
# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO 'root'@'egrid-7'          IDENTIFIED BY 'storm' WITH GRANT OPTION;
# GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO 'root'@'egrid-7.egrid.it' IDENTIFIED BY 'storm' WITH GRANT OPTION;


FLUSH PRIVILEGES;

