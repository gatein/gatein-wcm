package org.gatein.wcm.impl.tests;

import java.io.File;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.content.TextContent;
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
public class VersioningTest {

	private static final Logger log = Logger
			.getLogger(VersioningTest.class);

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
	public void checkVersionComments() throws ContentIOException,
			ContentSecurityException, ContentException {

		log.debug("[[ START TEST  checkVersionComments ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");

		cs.createFolder("checkVersionComments", "/");
		cs.createTextContent("test1", "en", "/checkVersionComments", "This is a test1");
		cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 1");
		cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 2");
		Content c = cs.createContentComment("/checkVersionComments/test1", "en", "This is a comment 3");

		Assert.assertTrue(c instanceof TextContent);

		TextContent tc = (TextContent)c;
		Assert.assertEquals(tc.getComments().size(), 3);
		Assert.assertEquals("1.0", tc.getVersion());

		cs.deleteContent("/checkVersionComments");

		log.debug("[[ STOP TEST  checkVersionComments ]]");
		Assert.assertTrue(true);
	}


}