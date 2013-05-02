package org.gatein.wcm.api;

import java.io.File;

import javax.annotation.Resource;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMRepositoryService;
import org.gatein.wcm.impl.security.DummySecurityService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Security tests.
 * <p>
 * Testing WCM and JCR.
 * <p>
 * Sharing single point for authentication and authorization.
 * <p>
 * ModeShape doesn't provide ACL out of the box, so we have implemented an ACL service for WCM.
 * <p>
 * WCM ACL service is also used by JCR to offer a 1to1 mapping on security aspects.
 * <p>
 * @see {@link DummySecurityService} for details with test's users and roles.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
@RunWith(Arquillian.class)
public class SecurityTest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "SecurityTest.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    // For accesing directly to underlying JCR
    // NOTE: This is not allowed to WCM users !!
    // This is for testing security only
    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    // Accesing to WCM system
    @Resource(mappedName = "java:jboss/gatein-wcm")
    WCMRepositoryService repos;
    WCMContentService cs;

    @Test
    public void accessAdminNotAccessOthers() throws Exception {
        // WCM
        cs = repos.createContentSession("admin", "admin");
        cs.closeSession();

        boolean fail = false;
        try {
            repos.createContentSession("user1", "gtn");
        } catch (Exception expected) {
            // Expected, there is not ACL by default on repository, only for admin
            fail = true;
        }

        if (!fail)
            Assert.assertFalse(true);

        // JCR
        fail = false;
        try {
            SimpleCredentials credentials = new SimpleCredentials("user1", "gtn".toCharArray());
            javax.jcr.Session jcrSession = repository.login(credentials, "default");
            jcrSession.hasPermission("/", "read");
            jcrSession.logout();
        } catch (Exception expected) {
            // Expected, WCM and JCR shares same authentication and authorization point
            // JCR also takes ACL from WCM
            fail = true;
        }

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void accessForUser1ButNotOthers() throws Exception {
        // WCM
        cs = repos.createContentSession("admin", "admin");
        cs.createContentAce("/", "user1", WCMPrincipalType.USER, WCMPermissionType.READ);
        cs.closeSession();

        cs = repos.createContentSession("user1", "gtn");
        cs.closeSession();

        boolean fail = false;
        try {
            repos.createContentSession("user2", "gtn");
        } catch (Exception expected) {
            // Expected
            fail = true;
        }

        if (!fail)
            Assert.assertFalse(true);

        // JCR
        SimpleCredentials credentials = new SimpleCredentials("user1", "gtn".toCharArray());
        javax.jcr.Session jcrSession = repository.login(credentials, "default");
        jcrSession.hasPermission("/", "read");
        jcrSession.logout();

        fail = false;
        try {
            credentials = new SimpleCredentials("user2", "gtn".toCharArray());
            jcrSession = repository.login(credentials, "default");
            jcrSession.hasPermission("/", "read");
            jcrSession.logout();
        } catch (Exception expected) {
            // Expected
            fail = true;
        }

        if (!fail)
            Assert.assertFalse(true);

        // Cleaning ACLs for future tests
        cs = repos.createContentSession("admin", "admin");
        cs.deleteContentAce("/", "user1");
        cs.closeSession();
    }

    @Test
    public void accessForEuropeRoleButNotOthers() throws Exception {
        // WCM
        cs = repos.createContentSession("admin", "admin");
        cs.createContentAce("/", "europe", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        cs.closeSession();

        cs = repos.createContentSession("user1", "gtn");
        cs.closeSession();

        cs = repos.createContentSession("user2", "gtn");
        cs.closeSession();

        boolean fail = false;
        try {
            cs = repos.createContentSession("user3", "gtn");
            cs.closeSession();
        } catch (Exception e) {
            // Expected
            fail = true;
        }

        if (!fail)
            Assert.assertFalse(true);

        fail = false;
        try {
            cs = repos.createContentSession("user4", "gtn");
            cs.closeSession();
        } catch (Exception e) {
            // Expected
            fail = true;
        }

        if (!fail)
            Assert.assertFalse(true);

        // JCR
        SimpleCredentials credentials = new SimpleCredentials("user1", "gtn".toCharArray());
        javax.jcr.Session jcrSession = repository.login(credentials, "default");
        jcrSession.hasPermission("/", "read");
        jcrSession.logout();

        credentials = new SimpleCredentials("user2", "gtn".toCharArray());
        jcrSession = repository.login(credentials, "default");
        jcrSession.hasPermission("/", "read");
        jcrSession.logout();

        // Cleaning ACLs for future tests
        cs = repos.createContentSession("admin", "admin");
        cs.deleteContentAce("/", "europe");
        cs.closeSession();
    }

    @Test
    public void readAccessWithSubfolders() throws Exception {
        // WCM
        cs = repos.createContentSession("admin", "admin");
        cs.createFolder("europe", "/");
        cs.createFolder("america", "/");
        // Both roles can access to root node
        cs.createContentAce("/", "europe", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        cs.createContentAce("/", "america", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        // Only can read their folder
        cs.createContentAce("/europe", "europe", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        cs.createContentAce("/europe", "america", WCMPrincipalType.ROLE, WCMPermissionType.NONE);
        // Only can read their folder
        cs.createContentAce("/america", "europe", WCMPrincipalType.ROLE, WCMPermissionType.NONE);
        cs.createContentAce("/america", "america", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        cs.closeSession();

        cs = repos.createContentSession("user1", "gtn");
        WCMObject o = cs.getContent("/europe");
        Assert.assertEquals("/europe", o.getPath());

        boolean fail = false;
        try {
            o = cs.getContent("/america");
        } catch (Exception expected) {
            // Expected, We don't have rights to access /america
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);

        cs = repos.createContentSession("user3", "gtn");
        o = cs.getContent("/america");
        Assert.assertEquals("/america", o.getPath());

        fail = false;
        try {
            o = cs.getContent("/europe");
        } catch (Exception expected) {
            // Expected, We don't have rights to access /america
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);

        // JCR
        SimpleCredentials credentials = new SimpleCredentials("user1", "gtn".toCharArray());
        javax.jcr.Session jcrSession = repository.login(credentials, "default");
        jcrSession.getNode("/europe");
        fail = false;
        try {
            jcrSession.getNode("/america");
        } catch (Exception expected) {
            // Expected, We don't have rights to access /america
            fail = true;
        }
        jcrSession.logout();

        if (!fail)
            Assert.assertFalse(true);

        credentials = new SimpleCredentials("user3", "gtn".toCharArray());
        jcrSession = repository.login(credentials, "default");
        jcrSession.getNode("/america");
        fail = false;
        try {
            jcrSession.getNode("/europe");
        } catch (Exception expected) {
            // Expected, We don't have rights to access /europe
            fail = true;
        }
        jcrSession.logout();

        if (!fail)
            Assert.assertFalse(true);

        // Cleaning ACLs for future tests
        cs = repos.createContentSession("admin", "admin");
        cs.deleteContent("/europe");
        cs.deleteContent("/america");
        cs.deleteContentAce("/", "europe");
        cs.deleteContentAce("/", "america");
        cs.closeSession();
    }

    @Test
    public void rolesWriting() throws Exception {
        // WCM
        cs = repos.createContentSession("admin", "admin");
        cs.createFolder("europe", "/");
        cs.createFolder("america", "/");
        // Both roles can access to root node
        cs.createContentAce("/", "europe", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        cs.createContentAce("/", "america", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        // Only can read their folder
        cs.createContentAce("/europe", "europe", WCMPrincipalType.ROLE, WCMPermissionType.WRITE);
        cs.createContentAce("/europe", "america", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        // Only can read their folder
        cs.createContentAce("/america", "europe", WCMPrincipalType.ROLE, WCMPermissionType.READ);
        cs.createContentAce("/america", "america", WCMPrincipalType.ROLE, WCMPermissionType.WRITE);
        cs.closeSession();

        // Writing
        cs = repos.createContentSession("user1", "gtn");
        cs.createTextDocument("eu1", "/europe", "This is a document written by user1");

        boolean fail = false;
        try {
            cs.createTextDocument("eu2", "/america", "This is a document written by user1");
        } catch (Exception expected) {
            // Expected, We don't have rights to access /america
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);

        cs = repos.createContentSession("user3", "gtn");
        cs.createTextDocument("us1", "/america", "This is a document written by user3");

        fail = false;
        try {
            cs.createTextDocument("us2", "/europe", "This is a document written by user3");
        } catch (Exception expected) {
            // Expected, We don't have rights to access /america
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);

        // Reading
        cs = repos.createContentSession("user1", "gtn");
        WCMTextDocument doc = (WCMTextDocument) cs.getContent("/america/us1");
        Assert.assertEquals("us1", doc.getId());
        Assert.assertEquals("/america/us1", doc.getPath());
        Assert.assertEquals("This is a document written by user3", doc.getContent());
        cs.closeSession();

        cs = repos.createContentSession("user3", "gtn");
        doc = (WCMTextDocument) cs.getContent("/europe/eu1");
        Assert.assertEquals("eu1", doc.getId());
        Assert.assertEquals("/europe/eu1", doc.getPath());
        Assert.assertEquals("This is a document written by user1", doc.getContent());
        cs.closeSession();

        // Cleaning ACLs for future tests
        cs = repos.createContentSession("admin", "admin");
        cs.deleteContent("/europe");
        cs.deleteContent("/america");
        cs.deleteContentAce("/", "europe");
        cs.deleteContentAce("/", "america");
        cs.closeSession();
    }

}