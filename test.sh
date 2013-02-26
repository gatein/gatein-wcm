if [ ! -f $(pwd)/gatein-wcm-impl/src/test/resources/wcm-whiteboard.jpg ]; then
. $(pwd)/test-resources.sh
fi
if [ ! -f $(pwd)/gatein-wcm-impl/src/test/resources/GateIn-UserGuide-v3.5.pdf ]; then
. $(pwd)/test-resources.sh
fi
if [ ! -f $(pwd)/gatein-wcm-impl/src/test/resources/jbossportletbridge.pdf ]; then
. $(pwd)/test-resources.sh
fi
. $(pwd)/setup.properties
mvn clean install -Denv.JBOSS_HOME=$JBOSS_HOME
