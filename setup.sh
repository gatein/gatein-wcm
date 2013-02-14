echo "Script for updating GateIn WCM into JBoss AS 7.1.1"
echo "==================================================="
echo
echo "Prerrequisites: "
echo " - Install JBoss AS 7.1.1 "
echo "   For example: unzip jboss-as-7.1.1.Final.zip -d /opt/Software/servers "
echo
echo " - Install modeshape 3.1.1.Final into JBossAS 7.1.1 "
echo "   For example: unzip modeshape-3.1.1.Final-jboss-71-dist.zip -d /opt/Software/servers/jboss-as-7.1.1.Final"
echo
echo "   Note: Installing Modeshape you will prompt about replace existings files (infinispan configuration), say [All] as default option. "
echo

. $(pwd)/setup.properties

echo "$JBOSS_HOME"

# Check if argument
if [ -z "$JBOSS_HOME" ]; then
	echo "... JBOSS_HOME has not set in setup.properties"
	exit 0
fi
if [ ! -d $JBOSS_HOME ]; then
	echo "... define a valid JBOSS_HOME in setup.properties"
	exit 0
fi

# Check if modeshape installed
if [ ! -d "$JBOSS_HOME/modules/org/modeshape" ]; then
	echo "... not modeshape detected in $JBOSS_HOME !!"
	exit 0
fi

# Copying files and structure
cp -Rf setup/* $JBOSS_HOME

# Create a fresh install without tests
mvn clean install -DskipTests

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

function cp_p {
    echo "Copying $1 into $2"
    mkdir -p "$2"
    cp "$1" "$2"
}

cp_p "gatein-wcm-api/target/gatein-wcm-api-0.0.1-SNAPSHOT.jar" "$JBOSS_HOME/modules/org/gatein/wcm/gatein-wcm-api/main"
cp_p "gatein-wcm-impl/target/gatein-wcm-impl-0.0.1-SNAPSHOT.jar" "$JBOSS_HOME/modules/org/gatein/wcm/gatein-wcm-impl/main"
cp_p "gatein-wcm-impl-sec/target/gatein-wcm-impl-sec-0.0.1-SNAPSHOT.jar" "$JBOSS_HOME/modules/org/modeshape/main"

# Final message
echo "Finished GateIn WCM configuration."
echo
echo "Test it running: mvn clean install -Denv.JBOSS_HOME=$JBOSS_HOME"



