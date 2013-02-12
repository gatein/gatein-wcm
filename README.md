GateIn WCM Service
==================

- gatein-wcm-api -> Model and API Services
- gatein-wcm-impl -> Implementation draft based on Modeshape deployed in JBoss AS 7.1.1
- gatein-wcm-impl-sec -> AuthenticationProvider for ModeShape
- gatein-wcm-portlet -> Examples of portlets using ModeShape as Storage

Requeriments
------------

- JDK 1.6 or JDK 1.7
- JBoss AS 7.1.1 Final
- Modeshape 3.1.1.Final for JBoss AS.

Installation
------------

- unzip unzip jboss-as-7.1.1.Final.zip -d /opt/Software/servers
- unzip modeshape-3.1.1.Final-jboss-71-dist.zip -d /opt/Software/servers/jboss-as-7.1.1.Final
- edit ${basedir}/setup.properties with JBOSS_HOME=/opt/Software/servers/jboss-as-7.1.1.Final
- run ${basedir}/setup.sh
- run mvn clean install -Denv.JBOSS_HOME=/opt/Software/servers/jboss-as-7.1.1.Final