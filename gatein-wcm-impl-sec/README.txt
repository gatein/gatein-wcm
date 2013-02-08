gatein-wcm-impl-sec
===================

This submodule is an implementation of

- org.modeshape.jcr.security.AuthenticationProvider
- org.modeshape.jcr.security.SecurityContext

The idea is to control whom authenticate and authorizate will do modeshape.

The idea is that modeshape and gatein-wcm-impl uses the same source of authentication.

So, a user logged via gatein-wcm-api will be equals an a user logged via modeshape (JCR or CMIS interface).

This is only a draft, next steps is to connect this submodule with GateIn Authentication or others.

To install:

- Copy the generated jar into the classpath of modeshape:

	jboss-as-7.1.1.Final/modules/org/modehsape/main
	
- Update jboss-as-7.1.1.Final/modules/org/modehsape/main/module.xml with the jar's.

- Configure modeshape repository to use this custom authenticator provider:

	standalone-modeshape.xml:

    <subsystem xmlns="urn:jboss:domain:modeshape:1.0">
    <repository name="sample">
        <authenticators>
            <authenticator name="gatein-wcm-security" classname="org.gatein.wcm.impl.security.GateInWCMProvider"/>
        </authenticators>
    </repository>
	[...]
