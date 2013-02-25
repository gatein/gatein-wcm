package org.gatein.wcm.tests;

import java.io.File;

import javax.annotation.Resource;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class Test005_JCRCategories {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.tests");


    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-tests-004.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void categories() {

        log.info( "[[ START TEST categories ]]" );
        try {

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
            javax.jcr.Session session = repository.login(credentials, "default");

            log.info(session);

        } catch (Exception e) {
            Assert.assertTrue( false );
        }
        log.info("[[ END TEST categories ]]");
        Assert.assertTrue( true );

    }


}
