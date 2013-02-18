JBOSS_HOME=/opt/Software/servers/jboss-as-7.1.1.Final
JBOSS_PID=$(ps -ef | grep java | grep jboss-modules | awk ' { print $2 } ')
echo "JBOSS_PID : " $JBOSS_PID
if [ ! -z $JBOSS_PID ]; then
	kill -9 $JBOSS_PID
fi
mvn clean install -DskipTests
cd $JBOSS_HOME/bin
./run-modeshape.sh &

