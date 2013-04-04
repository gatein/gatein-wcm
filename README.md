GateIn WCM Service
==================

GateIn WCM is a lightweight Web Content Management service.
It's designed to offer a simple Java API for WCM covering the following features:

- Basic content creation.
- Categories.
- Publishing workflow.
- Versioning.

This draft version has been tested on JBoss EAP 6.1.0.Alpha1.

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
- JBoss EAP 6.1.0.Alpha1 - installed automatically by Maven during build
- Modeshape 3.2-SNAPSHOT:

        cd ~/git
        git clone https://github.com/ModeShape/modeshape.git
        cd modeshape
        mvn clean install -Pintegration -DskipTests


Maven repositories
------------------

See example `settings.xml` file in this directory. In the most common case you will want to copy this file to `$HOME/.m2`
(`%HOMEPATH%\.m2` on Windows) with the following content:

Build
-----

    mvn clean install

... and see a ready to run distribution under `gatein-wcm-distribution/target/gatein-wcm-*-dist.zip`