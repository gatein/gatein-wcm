GateIn WCM Service
==================

GateIn WCM is a lightweight Web Content Management service.
It's designed to offer a simple Java API for WCM covering the following features:

- Basic content creation.
- Categories.
- Publishing workflow.
- Versioning.

This draft version has been tested on JBoss AS 7.1.1.Final.

Components
----------

- gatein-wcm-api ( Model and API Services )
- gatein-wcm-impl ( Implementation draft based on Modeshape deployed in JBoss AS 7.1.1 )
- gatein-wcm-impl-sec ( AuthenticationProvider for ModeShape )
- gatein-wcm-portlet ( Examples of portlets using ModeShape as Storage )
- gatein-wcm-distribution ( Configuration of modeshape component for GateIn WCM)
- gatein-wcm-integration-tests ( Set of tests for GateIn WCM )

Requeriments
------------

- JDK 1.6 or JDK 1.7
- Maven 3.0.x

Downloaded via Maven dependencies (so you don't need to install it):

- JBoss AS 7.1.1 Final
- Modeshape 3.1.1.Final for JBoss AS.

Maven dependencies
------------------

Create file settings.xml in $HOME/.m2  (%HOMEPATH%\.m2 on Windows) with the following content:

	<settings>
	  <profiles>
	    <profile>
	      <id>jboss-public-repository</id>
	      <repositories>
	        <repository>
	          <id>jboss-public-repository-group</id>
	          <name>JBoss Public Maven Repository Group</name>
	          <url>https://repository.jboss.org/nexus/content/groups/public-jboss/</url>
	          <layout>default</layout>
	          <releases>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </releases>
	          <snapshots>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </snapshots>
	        </repository>
	      </repositories>
	      <pluginRepositories>
	        <pluginRepository>
	          <id>jboss-public-repository-group</id>
	          <name>JBoss Public Maven Repository Group</name>
	          <url>https://repository.jboss.org/nexus/content/groups/public-jboss/</url>
	          <layout>default</layout>
	          <releases>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </releases>
	          <snapshots>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </snapshots>
	        </pluginRepository>
	      </pluginRepositories>
	    </profile>

	    <profile>
	      <id>exo-public-repository</id>
	      <repositories>
	        <repository>
	          <id>exo-public-repository-group</id>
	          <name>eXo Public Maven Repository Group</name>
	          <url>http://repository.exoplatform.org/content/groups/public</url>
	          <layout>default</layout>
	          <releases>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </releases>
	          <snapshots>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </snapshots>
	        </repository>
	      </repositories>
	      <pluginRepositories>
	        <pluginRepository>
	          <id>exo-public-repository-group</id>
	          <name>eXo Public Maven Repository Group</name>
	          <url>http://repository.exoplatform.org/content/groups/public</url>
	          <layout>default</layout>
	          <releases>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </releases>
	          <snapshots>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	          </snapshots>
	        </pluginRepository>
	      </pluginRepositories>
	    </profile>
	  </profiles>

	  <activeProfiles>
	    <activeProfile>jboss-public-repository</activeProfile>
	    <activeProfile>exo-public-repository</activeProfile>
	  </activeProfiles>
	</settings>

Installation
------------

- mvn clean install