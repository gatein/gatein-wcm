package org.gatein.wcm.impl.tests;

import java.io.File;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.WcmRepositoryService;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
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
public class BasicCDITest {

    private static final Logger log = Logger.getLogger(BasicCDITest.class);

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-tests.war")
        		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WcmRepositoryService repos;

    @Test
    public void goodPassword() {

        log.debug("[[ START TEST  goodPassword ]]");
        try {
            WcmContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            log.debug( cs );
            log.debug("[[ END TEST  goodPassword ]]");
            Assert.assertTrue( true );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void badPassword() {

        log.debug("[[ START TEST  badPassword ]]");
        try {
            WcmContentService cs = repos.createContentSession("sample", "default", "admin", "badpassword");
            log.debug( cs );
            Assert.fail("Expecting exception...");

        } catch (WcmContentSecurityException e) {
            log.debug(" [[ Expected: " + e.getMessage());
            log.debug("[[ END TEST  badPassword ]]");
            Assert.assertTrue( true );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


}
