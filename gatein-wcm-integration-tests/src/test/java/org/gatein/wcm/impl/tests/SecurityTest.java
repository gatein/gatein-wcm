package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.Locale;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.metadata.WcmCategory;
import org.gatein.wcm.api.model.security.WcmAce;
import org.gatein.wcm.api.model.security.WcmPrincipal.PrincipalType;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.WcmRepositoryService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
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
public class SecurityTest {

    private static final Logger log = Logger.getLogger(SecurityTest.class);

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-tests.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WcmRepositoryService repos;

    @Test
    public void checkRead() throws WcmContentIOException, WcmContentSecurityException, WcmContentException {

        String LOCALE = Locale.getDefault().getLanguage();

        // Creates a folder with admin
        WcmContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.createFolder("test-sec-a", "/");
        cs.createContentAce("/test-sec-a", LOCALE, "admin", PrincipalType.USER, WcmAce.PermissionType.ALL);
        cs.closeSession();

        // User user1 has not rights for this folder at this point
        try {
            cs = repos.createContentSession("sample", "default", "user1", "user1");
            WcmObject c = cs.getContent("/test-sec-a", LOCALE);
            Assert.assertEquals(c.getId(), "test-sec-a");
        } catch (WcmContentSecurityException expected) {
            log.debug(expected.getMessage());
        } finally {
            cs.closeSession();
        }

        // Adding ACE to give rights to user1
        cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.createContentAce("/test-sec-a", LOCALE, "user1", PrincipalType.USER, WcmAce.PermissionType.READ);
        cs.closeSession();

        cs = repos.createContentSession("sample", "default", "user1", "user1");
        WcmObject c = cs.getContent("/test-sec-a", LOCALE);
        Assert.assertEquals(c.getId(), "test-sec-a");
        cs.closeSession();

        // Cleaning
        cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.deleteContent("/test-sec-a");
        cs.closeSession();

    }

    @Test
    public void checkComments() throws WcmContentIOException, WcmContentSecurityException, WcmContentException {

        log.debug("[[ START TEST  checkComments ]]");

        String LOCALE = Locale.getDefault().getLanguage();

        // Creates a folder with admin
        WcmContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.createFolder("test-sec-a", "/");
        cs.createContentAce("/test-sec-a", LOCALE, "admin", PrincipalType.USER, WcmAce.PermissionType.ALL);
        cs.closeSession();

        // Trying to create a comment
        try {
            cs = repos.createContentSession("sample", "default", "user1", "user1");
            cs.createContentComment("/test-sec-a", LOCALE, "Checking if I can create a comment");
            WcmObject c = cs.getContent("/test-sec-a", LOCALE);
            Assert.assertEquals(false, c.getComments().isEmpty());
        } catch (WcmContentSecurityException expected) {
            log.debug(expected.getMessage());
        } finally {
            cs.closeSession();
        }

        // Adding ACE to give rights to user1
        cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.createContentAce("/test-sec-a", LOCALE, "user1", PrincipalType.USER, WcmAce.PermissionType.COMMENTS);
        cs.closeSession();

        cs = repos.createContentSession("sample", "default", "user1", "user1");
        cs.createContentComment("/test-sec-a", LOCALE, "Checking 2nd time if I can create a comment");
        WcmObject c = cs.getContent("/test-sec-a", LOCALE);
        Assert.assertEquals(false, c.getComments().isEmpty());
        cs.closeSession();

        // Cleaning
        cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.deleteContent("/test-sec-a");
        cs.closeSession();

        log.debug("[[ STOP TEST  checkComments ]]");

    }

    @Test
    public void checkWrite() throws WcmContentIOException, WcmContentSecurityException, WcmContentException {

        log.debug("[[ START TEST  checkWrite ]]");

        String LOCALE = Locale.getDefault().getLanguage();

        // Creates a folder with admin
        WcmContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.createFolder("test-sec-a", "/");
        cs.createContentAce("/test-sec-a", LOCALE, "admin", PrincipalType.USER, WcmAce.PermissionType.ALL);
        cs.closeSession();

        // Trying to write a content
        try {
            cs = repos.createContentSession("sample", "default", "user1", "user1");
            WcmObject c = cs.createFolder("sub-a", "/test-sec-a");
            Assert.assertEquals("sub-a", c.getId());
        } catch (WcmContentSecurityException expected) {
            log.debug(expected.getMessage());
        } finally {
            cs.closeSession();
        }

        // Adding ACE to give rights to user1
        cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.createContentAce("/test-sec-a", LOCALE, "user1", PrincipalType.USER, WcmAce.PermissionType.WRITE);
        cs.closeSession();

        cs = repos.createContentSession("sample", "default", "user1", "user1");
        WcmObject c = cs.createFolder("sub-a", "/test-sec-a");
        Assert.assertEquals("sub-a", c.getId());
        cs.closeSession();

        // Cleaning with user1
        cs = repos.createContentSession("sample", "default", "user1", "user1");
        cs.deleteContent("/test-sec-a");
        cs.closeSession();

        log.debug("[[ STOP TEST  checkWrite ]]");

    }

    @Test
    public void checkAdmin() throws WcmContentIOException, WcmContentSecurityException, WcmContentException {

        log.debug("[[ START TEST  checkAdmin ]]");

        String LOCALE = Locale.getDefault().getLanguage();
        WcmContentService cs = null;

        // Trying to write a Category
        try {
            cs = repos.createContentSession("sample", "default", "user1", "user1");
            WcmCategory cat = cs.createCategory("test-sec-category", LOCALE, "Test security", "/");
            Assert.assertEquals("test-sec-category", cat.getId());
        } catch (WcmContentSecurityException expected) {
            log.debug(expected.getMessage());
        } finally {
            cs.closeSession();
        }

        // Adding ACE to give rights to user1
        cs = repos.createContentSession("sample", "default", "admin", "admin");
        cs.createContentAce("/__categories", LOCALE, "user1", PrincipalType.USER, WcmAce.PermissionType.ALL);
        cs.closeSession();

        cs = repos.createContentSession("sample", "default", "user1", "user1");
        WcmCategory cat = cs.createCategory("test-sec-category", LOCALE, "Test security", "/");
        Assert.assertEquals("test-sec-category", cat.getId());
        cs.deleteCategory("/test-sec-category");
        cs.deleteContentAce("/__categories", LOCALE, "user1"); // Auto delete permission
        cs.closeSession();

        log.debug("[[ STOP TEST  checkAdmin ]]");

    }

}