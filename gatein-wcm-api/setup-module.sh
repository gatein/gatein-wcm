# Retrieving JBOSS_HOME
. $(pwd)/../setup.properties

if [ -f target/gatein-wcm-api-0.0.1-SNAPSHOT.jar ]; then
  echo "Copying target/gatein-wcm-api-0.0.1-SNAPSHOT.jar into $JBOSS_HOME/modules/org/gatein/wcm/gatein-wcm-api/main"
  cp target/gatein-wcm-api-0.0.1-SNAPSHOT.jar $JBOSS_HOME/modules/org/gatein/wcm/gatein-wcm-api/main
fi
