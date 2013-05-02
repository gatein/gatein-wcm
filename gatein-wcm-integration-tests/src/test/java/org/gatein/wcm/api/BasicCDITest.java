package org.gatein.wcm.api;

import java.io.File;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMRepositoryService;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BasicCDITest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-tests.war")
        		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));
    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WCMRepositoryService repos;

    @Test
    public void goodPassword() throws Exception {
        WCMContentService cs = repos.createContentSession("admin", "admin");
        cs.closeSession();
    }

    @Test
    public void badPassword() throws Exception {
        try {
            repos.createContentSession("admin", "badpassword");
            Assert.fail("Expecting exception...");
        } catch (WCMContentSecurityException expected) {
        }
    }

}
