package org.gatein.wcm.impl.tests;

import java.io.File;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class Test001BasicCDITest {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.impl.tests.test001");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-tests-test001.war")
        		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void goodPassword() {

        log.info("[[ START TEST  goodPassword ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            log.info( cs );
            log.info("[[ END TEST  goodPassword ]]");
            Assert.assertTrue( true );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void badPassword() {

        log.info("[[ START TEST  badPassword ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "badpassword");
            log.info( cs );
            Assert.fail("Expecting exception...");

        } catch (ContentSecurityException e) {
            log.info(" [[ Expected: " + e.getMessage());
            log.info("[[ END TEST  badPassword ]]");
            Assert.assertTrue( true );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


}
