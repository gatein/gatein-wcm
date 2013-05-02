package org.gatein.wcm.api;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WCMObject;
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
public class PropertiesTest {

	private static final Logger log = Logger
			.getLogger(PropertiesTest.class);

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
//	public void createProperties() throws WCMContentIOException,
//			WCMContentSecurityException, WCMContentException {
//
//		log.debug("[[ START TEST  createProperties ]]");
//		WCMContentService cs = repos.createContentSession("sample", "default",
//				"admin", "admin");
//
//		cs.createFolder("testproperties", "/");
//		cs.createTextContent("test1", "en", "/testproperties", "This is a test1");
//		cs.createTextContent("test2", "en", "/testproperties", "This is a test2");
//		cs.createTextContent("test3", "en", "/testproperties", "This is a test3");
//
//		cs.createContentProperty("/testproperties", "en", "special", "ID123456789 ");
//		cs.createContentProperty("/testproperties", "en", "name", "This is a name property");
//		WCMObject c = cs.createContentProperty("/testproperties", "en", "custom", "This is a name property");
//		Assert.assertEquals(3, c.getProperties().size());
//
//		Map<String, String> properties = c.getProperties();
//		Set<String> sProperties = properties.keySet();
//		for (String key : sProperties) {
//		    log.debug("Property: " + key + " Value: " + properties.get(key));
//		}
//
//		cs.deleteContent("/testproperties");
//
//		log.debug("[[ STOP TEST  createProperties ]]");
//		Assert.assertTrue(true);
//	}
//
//	@Test
//    public void deleteProperties() throws WCMContentIOException,
//            WCMContentSecurityException, WCMContentException {
//
//        log.debug("[[ START TEST  deleteProperties ]]");
//        WCMContentService cs = repos.createContentSession("sample", "default",
//                "admin", "admin");
//
//        cs.createFolder("deleteproperties", "/");
//        cs.createTextContent("test1", "en", "/deleteproperties", "This is a test1");
//        cs.createTextContent("test2", "en", "/deleteproperties", "This is a test2");
//        cs.createTextContent("test3", "en", "/deleteproperties", "This is a test3");
//
//        cs.createContentProperty("/deleteproperties/test1", "en", "special", "ID123456789-test1");
//        cs.createContentProperty("/deleteproperties/test1", "en", "name", "This is a name property test1");
//
//        cs.createContentProperty("/deleteproperties/test2", "en", "special", "ID123456789-test2");
//        cs.createContentProperty("/deleteproperties/test2", "en", "name", "This is a name property test2");
//
//        cs.createContentProperty("/deleteproperties/test3", "en", "special", "ID123456789-test3");
//        cs.createContentProperty("/deleteproperties/test3", "en", "name", "This is a name property test3");
//
//        WCMObject c = cs.deleteContentProperty("/deleteproperties/test1", "en", "name");
//        Assert.assertEquals(1, c.getProperties().size());
//
//        WCMObject _c = cs.deleteContentProperty("/deleteproperties/test2", "en", "name");
//        Assert.assertEquals(1, _c.getProperties().size());
//
//        cs.deleteContentProperty("/deleteproperties/test3", "en", "name");
//        WCMObject __c = cs.deleteContentProperty("/deleteproperties/test3", "en", "special");
//        Assert.assertEquals(null, __c.getProperties());
//
//        cs.deleteContent("/deleteproperties");
//
//        log.debug("[[ STOP TEST  deleteProperties ]]");
//        Assert.assertTrue(true);
//    }


}