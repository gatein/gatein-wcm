package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WcmObject;
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
	WcmRepositoryService repos;

	@Test
	public void createProperties() throws WcmContentIOException,
			WcmContentSecurityException, WcmContentException {

		log.debug("[[ START TEST  createProperties ]]");
		WcmContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");

		cs.createFolder("testproperties", "/");
		cs.createTextContent("test1", "en", "/testproperties", "This is a test1");
		cs.createTextContent("test2", "en", "/testproperties", "This is a test2");
		cs.createTextContent("test3", "en", "/testproperties", "This is a test3");

		cs.createContentProperty("/testproperties", "en", "special", "ID123456789 ");
		cs.createContentProperty("/testproperties", "en", "name", "This is a name property");
		WcmObject c = cs.createContentProperty("/testproperties", "en", "custom", "This is a name property");
		Assert.assertEquals(3, c.getProperties().size());

		Map<String, String> properties = c.getProperties();
		Set<String> sProperties = properties.keySet();
		for (String key : sProperties) {
		    log.debug("Property: " + key + " Value: " + properties.get(key));
		}

		cs.deleteContent("/testproperties");

		log.debug("[[ STOP TEST  createProperties ]]");
		Assert.assertTrue(true);
	}

	@Test
    public void deleteProperties() throws WcmContentIOException,
            WcmContentSecurityException, WcmContentException {

        log.debug("[[ START TEST  deleteProperties ]]");
        WcmContentService cs = repos.createContentSession("sample", "default",
                "admin", "admin");

        cs.createFolder("deleteproperties", "/");
        cs.createTextContent("test1", "en", "/deleteproperties", "This is a test1");
        cs.createTextContent("test2", "en", "/deleteproperties", "This is a test2");
        cs.createTextContent("test3", "en", "/deleteproperties", "This is a test3");

        cs.createContentProperty("/deleteproperties/test1", "en", "special", "ID123456789-test1");
        cs.createContentProperty("/deleteproperties/test1", "en", "name", "This is a name property test1");

        cs.createContentProperty("/deleteproperties/test2", "en", "special", "ID123456789-test2");
        cs.createContentProperty("/deleteproperties/test2", "en", "name", "This is a name property test2");

        cs.createContentProperty("/deleteproperties/test3", "en", "special", "ID123456789-test3");
        cs.createContentProperty("/deleteproperties/test3", "en", "name", "This is a name property test3");

        WcmObject c = cs.deleteContentProperty("/deleteproperties/test1", "en", "name");
        Assert.assertEquals(1, c.getProperties().size());

        WcmObject _c = cs.deleteContentProperty("/deleteproperties/test2", "en", "name");
        Assert.assertEquals(1, _c.getProperties().size());

        cs.deleteContentProperty("/deleteproperties/test3", "en", "name");
        WcmObject __c = cs.deleteContentProperty("/deleteproperties/test3", "en", "special");
        Assert.assertEquals(null, __c.getProperties());

        cs.deleteContent("/deleteproperties");

        log.debug("[[ STOP TEST  deleteProperties ]]");
        Assert.assertTrue(true);
    }


}