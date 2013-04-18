package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.security.WcmAce;
import org.gatein.wcm.api.model.security.WcmPrincipal;
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
public class AclTest {

	@Deployment
	public static Archive<?> createDeployment() {

		return ShrinkWrap
				.create(WebArchive.class, "gatein-wcm-impl-tests.war")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

	}

	@Resource(mappedName = "java:jboss/gatein-wcm")
	WcmRepositoryService repos;

	@Test
	public void createAcl() throws WcmContentIOException,
			WcmContentSecurityException, WcmContentException {

	    WcmContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");

		cs.createFolder("createacl", "/");
		cs.createTextContent("test1", "en", "/createacl", "This is a test1");
		cs.createTextContent("test2", "en", "/createacl", "This is a test2");
		cs.createTextContent("test3", "en", "/createacl", "This is a test3");

		// Root ACL
		cs.createContentAce("/", "en", "admin", WcmPrincipal.PrincipalType.USER, WcmAce.PermissionType.ALL);
		cs.createContentAce("/", "en", "other", WcmPrincipal.PrincipalType.GROUP, WcmAce.PermissionType.READ);
        WcmObject c = cs.createContentAce("/", "en", "guess", WcmPrincipal.PrincipalType.USER, WcmAce.PermissionType.COMMENTS);
        List<WcmAce> aces = c.getAcl().getAces();
        Assert.assertEquals(3, aces.size());

        // Child ACL
        cs.createContentAce("/createacl/test3", "en", "admin", WcmPrincipal.PrincipalType.USER, WcmAce.PermissionType.ALL);
        WcmObject _c = cs.createContentAce("/createacl/test3", "en", "other", WcmPrincipal.PrincipalType.GROUP, WcmAce.PermissionType.READ);
        aces = _c.getAcl().getAces();
        Assert.assertEquals(2, aces.size());

		cs.deleteContent("/createacl");

		Assert.assertTrue(true);
	}

	@Test
    public void deleteAcl() throws WcmContentIOException,
            WcmContentSecurityException, WcmContentException {

        WcmContentService cs = repos.createContentSession("sample", "default",
                "admin", "admin");

        cs.createFolder("deleteacl", "/");
        cs.createTextContent("test1", "en", "/deleteacl", "This is a test1");
        cs.createTextContent("test2", "en", "/deleteacl", "This is a test2");
        cs.createTextContent("test3", "en", "/deleteacl", "This is a test3");

        cs.deleteContent("/deleteacl");

    }


}