# Retrieving JBOSS_HOME
. $(pwd)/../setup.properties

if [ -f target/gatein-wcm-impl-sec-0.0.1-SNAPSHOT.jar ]; then
  echo "Copying target/gatein-wcm-impl-sec-0.0.1-SNAPSHOT.jar into $JBOSS_HOME/modules/org/modeshape/main"
  cp target/gatein-wcm-impl-sec-0.0.1-SNAPSHOT.jar $JBOSS_HOME/modules/org/modeshape/main
fi
