package org.gatein.wcm.tests;

import java.io.File;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.services.RepositoryService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.logging.Logger;

@RunWith(Arquillian.class)
public class BasicCDITest {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.tests");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-test.war").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;


    @Test
    public void accesing_from_module() {

        log.info("[[ START TEST  accesing_from_module ]]");

        log.info(repos);

        log.info("[[ END TEST  accesing_from_module ]]");

        Assert.assertTrue(repos != null);

    }

}
