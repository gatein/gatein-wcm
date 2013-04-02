package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Comment;
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
	RepositoryService repos;

	@Test
	public void createComments() throws ContentIOException,
			ContentSecurityException, ContentException {

		log.info("[[ START TEST  createComments ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");

		cs.createFolder("testcomments", "/");
		cs.createTextContent("test1", "en", "/testcomments", "This is a test1");
		cs.createTextContent("test2", "en", "/testcomments", "This is a test2");
		cs.createTextContent("test3", "en", "/testcomments", "This is a test3");

		cs.createContentComment("/testcomments", "en", "This is a comment A");
		cs.createContentComment("/testcomments", "en", "This is a comment B");
		Content c = cs.createContentComment("/testcomments", "en", "This is a comment C");

		Assert.assertEquals(3, c.getComments().size());

		List<Comment> comments = c.getComments();
		for (Comment comment : comments) {
		    log.info(comment.getCreatedBy().getUserName() + " date: " + comment.getCreated().toString() + ": " + comment.getComment());
		}

		cs.deleteContent("/testcomments");

		log.info("[[ STOP TEST  createComments ]]");
		Assert.assertTrue(true);
	}

    @Test
    public void deleteComments() throws ContentIOException,
            ContentSecurityException, ContentException {

        log.info("[[ START TEST  deleteComments ]]");
        ContentService cs = repos.createContentSession("sample", "default",
                "admin", "admin");

        cs.createFolder("deletecomments", "/");
        cs.createTextContent("test1", "en", "/deletecomments", "This is a test1");
        cs.createTextContent("test2", "en", "/deletecomments", "This is a test2");
        cs.createTextContent("test3", "en", "/deletecomments", "This is a test3");

        cs.createContentComment("/deletecomments", "en", "This is a comment A");
        cs.createContentComment("/deletecomments", "en", "This is a comment B");
        cs.createContentComment("/deletecomments", "en", "This is a comment C");
        cs.createContentComment("/deletecomments", "en", "This is a comment D");
        Content c = cs.createContentComment("/deletecomments", "en", "This is a comment D");

        Assert.assertEquals(5, c.getComments().size());

        List<Comment> comments = c.getComments();
        for (Comment comment : comments) {
            log.info(comment.getCreatedBy().getUserName() + " date: " + comment.getCreated().toString() + ": " + comment.getComment());
            cs.deleteContentComment("/deletecomments", "en", comment.getId());
        }

        Content _c = cs.getContent("/deletecomments", "en");
        Assert.assertEquals(null, _c.getComments());

        cs.createContentComment("/deletecomments", "en", "This is a comment E");
        Content __c = cs.createContentComment("/deletecomments", "en", "This is a comment F");
        Assert.assertEquals(2, __c.getComments().size());

        cs.deleteContent("/deletecomments");

        log.info("[[ STOP TEST  deleteComments ]]");
        Assert.assertTrue(true);
    }


}