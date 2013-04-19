package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmTextObject;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.WcmRepositoryService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class VersioningTest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-tests.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WcmRepositoryService repos;

    @Test
    public void checkVersionComments() throws WcmContentIOException, WcmContentSecurityException, WcmContentException {

        WcmContentService cs = repos.createContentSession("sample", "default", "admin", "admin");

        cs.createFolder("checkVersionComments", "/");
        cs.createTextContent("test1", "en", "/checkVersionComments", "This is a test1");
        cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 1");
        cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 2");
        WcmObject c = cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 3");

        Assert.assertTrue(c instanceof WcmTextObject);

        WcmTextObject tc = (WcmTextObject) c;
        Assert.assertEquals(tc.getComments().size(), 3);
        Assert.assertEquals("1.0", tc.getVersion());

        cs.deleteContent("/checkVersionComments");
    }

    @Test
    public void checkSeveralVersions() throws WcmContentIOException, WcmContentSecurityException, WcmContentException {

        WcmContentService cs = repos.createContentSession("sample", "default", "admin", "admin");

        cs.createFolder("checkSeveralVersions", "/");
        cs.createTextContent("test1", "en", "/checkSeveralVersions", "This is a test1");
        cs.createTextContent("test1", "es", "/checkSeveralVersions", "This is a test1");
        cs.createTextContent("test1", "fr", "/checkSeveralVersions", "This is a test1");
        cs.createTextContent("test1", "de", "/checkSeveralVersions", "This is a test1");

        List<String> versions = cs.getContentVersions("/checkSeveralVersions/test1");

        Assert.assertEquals(versions.size(), 4);

        versions = cs.getContentVersions("/checkSeveralVersions");

        Assert.assertEquals(versions, null);

        cs.deleteContent("/checkSeveralVersions");
    }

    // This test fails at the moment. Need to review it later once versioning is resolve in modeshape.
    public void checkGetSpecificVersion() throws Exception {

        WcmContentService cs = repos.createContentSession("sample", "default", "admin", "admin");

        cs.createFolder("checkGetSpecificVersion", "/");
        cs.createTextContent("test1", "en", "/checkGetSpecificVersion", "This is first version");
        cs.updateTextContent("/checkGetSpecificVersion/test1", "en", "This is second version");
        cs.updateTextContent("/checkGetSpecificVersion/test1", "en", "This is third version");
        cs.updateTextContent("/checkGetSpecificVersion/test1", "en", "This is fourth version");

        List<String> versions = cs.getContentVersions("/checkGetSpecificVersion/test1");

        Assert.assertEquals(versions.size(), 4);

        // Better way:
        //  restoreversion()
        //  getContent()
        //
        // WcmTextObject c = (WcmTextObject)cs.getContent("/checkGetSpecificVersion/test1", "en", "1.0");
        // Assert.assertEquals(c.getContent(), "This is first version");

        cs.deleteContent("/checkGetSpecificVersion");
    }

}