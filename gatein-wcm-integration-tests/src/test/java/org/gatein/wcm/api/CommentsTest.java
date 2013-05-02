package org.gatein.wcm.api;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.metadata.WCMComment;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMRepositoryService;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

// @RunWith(Arquillian.class)
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
	WCMRepositoryService repos;

//  Waiting refactoring
//
//	@Test
//	public void createComments() throws WCMContentIOException,
//			WCMContentSecurityException, WCMContentException {
//
//		log.debug("[[ START TEST  createComments ]]");
//		WCMContentService cs = repos.createContentSession("sample", "default",
//				"admin", "admin");
//
//		cs.createFolder("testcomments", "/");
//		cs.createTextContent("test1", "en", "/testcomments", "This is a test1");
//		cs.createTextContent("test2", "en", "/testcomments", "This is a test2");
//		cs.createTextContent("test3", "en", "/testcomments", "This is a test3");
//
//		cs.createContentComment("/testcomments", "en", "This is a comment A");
//		cs.createContentComment("/testcomments", "en", "This is a comment B");
//		WCMObject c = cs.createContentComment("/testcomments", "en", "This is a comment C");
//
//		Assert.assertEquals(3, c.getComments().size());
//
//		List<WCMComment> comments = c.getComments();
//		for (WCMComment comment : comments) {
//		    log.debug(comment.getCreatedBy().getUserName() + " date: " + comment.getCreatedOn().toString() + ": " + comment.getComment());
//		}
//
//		cs.deleteContent("/testcomments");
//
//		log.debug("[[ STOP TEST  createComments ]]");
//		Assert.assertTrue(true);
//	}
//
//    @Test
//    public void deleteComments() throws WCMContentIOException,
//            WCMContentSecurityException, WCMContentException {
//
//        log.debug("[[ START TEST  deleteComments ]]");
//        WCMContentService cs = repos.createContentSession("sample", "default",
//                "admin", "admin");
//
//        cs.createFolder("deletecomments", "/");
//        cs.createTextContent("test1", "en", "/deletecomments", "This is a test1");
//        cs.createTextContent("test2", "en", "/deletecomments", "This is a test2");
//        cs.createTextContent("test3", "en", "/deletecomments", "This is a test3");
//
//        cs.createContentComment("/deletecomments", "en", "This is a comment A");
//        cs.createContentComment("/deletecomments", "en", "This is a comment B");
//        cs.createContentComment("/deletecomments", "en", "This is a comment C");
//        cs.createContentComment("/deletecomments", "en", "This is a comment D");
//        WCMObject c = cs.createContentComment("/deletecomments", "en", "This is a comment D");
//
//        Assert.assertEquals(5, c.getComments().size());
//
//        List<WCMComment> comments = c.getComments();
//        for (WCMComment comment : comments) {
//            log.debug(comment.getCreatedBy().getUserName() + " date: " + comment.getCreatedOn().toString() + ": " + comment.getComment());
//            cs.deleteContentComment("/deletecomments", "en", comment.getId());
//        }
//
//        WCMObject _c = cs.getContent("/deletecomments", "en");
//        Assert.assertEquals(null, _c.getComments());
//
//        cs.createContentComment("/deletecomments", "en", "This is a comment E");
//        WCMObject __c = cs.createContentComment("/deletecomments", "en", "This is a comment F");
//        Assert.assertEquals(2, __c.getComments().size());
//
//        cs.deleteContent("/deletecomments");
//
//        log.debug("[[ STOP TEST  deleteComments ]]");
//        Assert.assertTrue(true);
//    }


}