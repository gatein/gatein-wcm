package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.metadata.WcmComment;
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
public class CommentsTest {

	private static final Logger log = Logger
			.getLogger(CommentsTest.class);

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
	public void createComments() throws WcmContentIOException,
			WcmContentSecurityException, WcmContentException {

		log.debug("[[ START TEST  createComments ]]");
		WcmContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");

		cs.createFolder("testcomments", "/");
		cs.createTextContent("test1", "en", "/testcomments", "This is a test1");
		cs.createTextContent("test2", "en", "/testcomments", "This is a test2");
		cs.createTextContent("test3", "en", "/testcomments", "This is a test3");

		cs.createContentComment("/testcomments", "en", "This is a comment A");
		cs.createContentComment("/testcomments", "en", "This is a comment B");
		WcmObject c = cs.createContentComment("/testcomments", "en", "This is a comment C");

		Assert.assertEquals(3, c.getComments().size());

		List<WcmComment> comments = c.getComments();
		for (WcmComment comment : comments) {
		    log.debug(comment.getCreatedBy().getUserName() + " date: " + comment.getCreatedOn().toString() + ": " + comment.getComment());
		}

		cs.deleteContent("/testcomments");

		log.debug("[[ STOP TEST  createComments ]]");
		Assert.assertTrue(true);
	}

    @Test
    public void deleteComments() throws WcmContentIOException,
            WcmContentSecurityException, WcmContentException {

        log.debug("[[ START TEST  deleteComments ]]");
        WcmContentService cs = repos.createContentSession("sample", "default",
                "admin", "admin");

        cs.createFolder("deletecomments", "/");
        cs.createTextContent("test1", "en", "/deletecomments", "This is a test1");
        cs.createTextContent("test2", "en", "/deletecomments", "This is a test2");
        cs.createTextContent("test3", "en", "/deletecomments", "This is a test3");

        cs.createContentComment("/deletecomments", "en", "This is a comment A");
        cs.createContentComment("/deletecomments", "en", "This is a comment B");
        cs.createContentComment("/deletecomments", "en", "This is a comment C");
        cs.createContentComment("/deletecomments", "en", "This is a comment D");
        WcmObject c = cs.createContentComment("/deletecomments", "en", "This is a comment D");

        Assert.assertEquals(5, c.getComments().size());

        List<WcmComment> comments = c.getComments();
        for (WcmComment comment : comments) {
            log.debug(comment.getCreatedBy().getUserName() + " date: " + comment.getCreatedOn().toString() + ": " + comment.getComment());
            cs.deleteContentComment("/deletecomments", "en", comment.getId());
        }

        WcmObject _c = cs.getContent("/deletecomments", "en");
        Assert.assertEquals(null, _c.getComments());

        cs.createContentComment("/deletecomments", "en", "This is a comment E");
        WcmObject __c = cs.createContentComment("/deletecomments", "en", "This is a comment F");
        Assert.assertEquals(2, __c.getComments().size());

        cs.deleteContent("/deletecomments");

        log.debug("[[ STOP TEST  deleteComments ]]");
        Assert.assertTrue(true);
    }


}