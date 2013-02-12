package org.gatein.wcm.tests;

import java.io.File;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BasicCDITest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl.war").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Test
    public void accesing_from_module() {

        System.out.println("[[ START TEST  accesing_from_module ]]");
        System.out.println("[[ END TEST  accesing_from_module ]]");

        Assert.assertTrue(true);

    }

}
