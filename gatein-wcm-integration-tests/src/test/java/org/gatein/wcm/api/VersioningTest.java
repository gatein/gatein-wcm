package org.gatein.wcm.api;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMRepositoryService;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

// @RunWith(Arquillian.class)
public class VersioningTest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-tests.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WCMRepositoryService repos;

//    Waiting refactoring
//
//    @Test
//    public void checkVersionComments() throws WCMContentIOException, WCMContentSecurityException, WCMContentException {
//
//        WCMContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
//
//        cs.createFolder("checkVersionComments", "/");
//        cs.createTextContent("test1", "en", "/checkVersionComments", "This is a test1");
//        cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 1");
//        cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 2");
//        WCMObject c = cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 3");
//
//        Assert.assertTrue(c instanceof WCMTextDocument);
//
//        WCMTextDocument tc = (WCMTextDocument) c;
//        Assert.assertEquals(tc.getComments().size(), 3);
//        Assert.assertEquals("1.0", tc.getVersion());
//
//        cs.deleteContent("/checkVersionComments");
//    }
//
//    @Test
//    public void checkSeveralVersions() throws WCMContentIOException, WCMContentSecurityException, WCMContentException {
//
//        WCMContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
//
//        cs.createFolder("checkSeveralVersions", "/");
//        cs.createTextContent("test1", "en", "/checkSeveralVersions", "This is a test1");
//        cs.createTextContent("test1", "es", "/checkSeveralVersions", "This is a test1");
//        cs.createTextContent("test1", "fr", "/checkSeveralVersions", "This is a test1");
//        cs.createTextContent("test1", "de", "/checkSeveralVersions", "This is a test1");
//
//        List<String> versions = cs.getContentVersions("/checkSeveralVersions/test1");
//
//        Assert.assertEquals(versions.size(), 4);
//
//        versions = cs.getContentVersions("/checkSeveralVersions");
//
//        Assert.assertEquals(versions, null);
//
//        cs.deleteContent("/checkSeveralVersions");
//    }
//
//    // This test fails at the moment. Need to review it later once versioning is resolve in modeshape.
//    public void checkGetSpecificVersion() throws Exception {
//
//        WCMContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
//
//        cs.createFolder("checkGetSpecificVersion", "/");
//        cs.createTextContent("test1", "en", "/checkGetSpecificVersion", "This is first version");
//        cs.updateTextContent("/checkGetSpecificVersion/test1", "en", "This is second version");
//        cs.updateTextContent("/checkGetSpecificVersion/test1", "en", "This is third version");
//        cs.updateTextContent("/checkGetSpecificVersion/test1", "en", "This is fourth version");
//
//        List<String> versions = cs.getContentVersions("/checkGetSpecificVersion/test1");
//
//        Assert.assertEquals(versions.size(), 4);
//
//        // Better way:
//        //  restoreversion()
//        //  getContent()
//        //
//        // WcmTextObject c = (WcmTextObject)cs.getContent("/checkGetSpecificVersion/test1", "en", "1.0");
//        // Assert.assertEquals(c.getContent(), "This is first version");
//
//        cs.deleteContent("/checkGetSpecificVersion");
//    }

}