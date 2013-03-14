package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.security.ACE;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
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
public class AclTest {

	private static final Logger log = Logger
			.getLogger("org.gatein.wcm.impl.tests");

	@Deployment
	public static Archive<?> createDeployment() {

		return ShrinkWrap
				.create(WebArchive.class, "gatein-wcm-impl-tests.war")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

	}

	@Resource(mappedName = "java:jboss/gatein-wcm")
	RepositoryService repos;

	@Test
	public void createAcl() throws ContentIOException,
			ContentSecurityException, ContentException {

		log.info("[[ START TEST  createAcl ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");

		cs.createFolder("createacl", "/");
		cs.createTextContent("test1", "en", "/createacl", "This is a test1", "UTF8");
		cs.createTextContent("test2", "en", "/createacl", "This is a test2", "UTF8");
		cs.createTextContent("test3", "en", "/createacl", "This is a test3", "UTF8");

		// Root ACL
		cs.createContentACE("/", "en", "admin", Principal.PrincipalType.USER, ACE.PermissionType.ALL);
		cs.createContentACE("/", "en", "other", Principal.PrincipalType.GROUP, ACE.PermissionType.READ);
        Content c = cs.createContentACE("/", "en", "guess", Principal.PrincipalType.USER, ACE.PermissionType.COMMENTS);
        List<ACE> aces = c.getAcl().getAces();
        Assert.assertEquals(3, aces.size());

        // Child ACL
        cs.createContentACE("/createacl/test3", "en", "admin", Principal.PrincipalType.USER, ACE.PermissionType.ALL);
        Content _c = cs.createContentACE("/createacl/test3", "en", "other", Principal.PrincipalType.GROUP, ACE.PermissionType.READ);
        aces = _c.getAcl().getAces();
        Assert.assertEquals(2, aces.size());

		cs.deleteContent("/createacl");

		log.info("[[ STOP TEST  createAcl ]]");
		Assert.assertTrue(true);
	}

	@Test
    public void deleteAcl() throws ContentIOException,
            ContentSecurityException, ContentException {

        log.info("[[ START TEST  deleteAcl ]]");
        ContentService cs = repos.createContentSession("sample", "default",
                "admin", "admin");

        cs.createFolder("deleteacl", "/");
        cs.createTextContent("test1", "en", "/deleteacl", "This is a test1", "UTF8");
        cs.createTextContent("test2", "en", "/deleteacl", "This is a test2", "UTF8");
        cs.createTextContent("test3", "en", "/deleteacl", "This is a test3", "UTF8");

        cs.deleteContent("/deleteacl");

        log.info("[[ STOP TEST  deleteAcl ]]");
        Assert.assertTrue(true);
    }


}