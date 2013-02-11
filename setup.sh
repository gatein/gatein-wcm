echo "Script for updating GateIn WCM into JBoss AS 7.1.1"
echo "==================================================="
echo
echo "Prerrequisites: "
echo " - Install JBoss AS 7.1.1 "
echo "   For example: unzip jboss-as-7.1.1.Final.zip -d /opt/Software/servers "
echo 
echo " - Install modeshape 3.1.1.Final into JBossAS 7.1.1 "
echo "   For example: unzip modeshape-3.1.1.Final-jboss-71-dist.zip "
echo

# Check if argument
if [ -z "$1" ]; then
	echo "... usage <path_to_jboss-as-7.1.1+modeshape-3.1.1>"
	exit 0
fi

JBOSS_HOME=$1

# Check if modeshape installed
if [ ! -d "$JBOSS_HOME/modules/org/modeshape" ]; then
	echo "... not modeshape detected in $JBOSS_HOME !!"
	exit 0
fi

# Copying files and structure
cp -Rf setup/* $JBOSS_HOME

# Check packages
if [ ! -f gatein-wcm-api/target/gatein-wcm-api-0.0.1-SNAPSHOT.jar ]; then
	echo "... gatein-wcm-api package not detected. Run... mvn clean install"
	exit 0
fi

if [ ! -f gatein-wcm-impl/target/gatein-wcm-impl-0.0.1-SNAPSHOT.jar ]; then
	echo "... gatein-wcm-impl package not detected. Run... mvn clean install"
	exit 0
fi

if [ ! -f gatein-wcm-impl-sec/target/gatein-wcm-impl-sec-0.0.1-SNAPSHOT.jar ]; then
	echo "... gatein-wcm-impl-sec package not detected. Run... mvn clean install"
	exit 0
fi

cp gatein-wcm-api/target/gatein-wcm-api-0.0.1-SNAPSHOT.jar $JBOSS_HOME/modules/org/gatein/wcm/gatein-wcm-api/main
cp gatein-wcm-impl/target/gatein-wcm-impl-0.0.1-SNAPSHOT.jar $JBOSS_HOME/modules/org/gatein/wcm/gatein-wcm-impl/main
cp gatein-wcm-impl-sec/target/gatein-wcm-impl-sec-0.0.1-SNAPSHOT.jar $JBOSS_HOME/modules/org/modeshape/main

# Final message
echo "Finished GateIn WCM configuration."
echo
echo "Test it running: mvn clean install -Denv.JBOSS_HOME=<path_to_jboss-as-7.1.1+modeshape-3.1.1>"



