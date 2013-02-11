# Target
JBOSS_HOME=/opt/Software/servers/jboss-as-7.1.1.Final
MODESHAPE=$JBOSS_HOME/modules/org/modeshape/main

# Updating ModeShape 
cp target/*.jar $MODESHAPE
cp ../gatein-wcm-api/target/*.jar $MODESHAPE

