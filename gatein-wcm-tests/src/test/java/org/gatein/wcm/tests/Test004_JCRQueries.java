package org.gatein.wcm.tests;

import java.io.File;

import javax.annotation.Resource;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.modeshape.jcr.api.JcrTools;

@RunWith(Arquillian.class)
public class Test004_JCRQueries {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.tests");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-tests-003.war")
                .addAsResource(new File("src/main/resources/cmis-spec-v1.0.pdf"))
                .addAsResource(new File("src/main/resources/wcm-whiteboard.jpg"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void query() {

        JcrTools tools = new JcrTools( true );

        try {

            log.info( "[[ START TEST query ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
            javax.jcr.Session session = repository.login(credentials, "default");

            String expression = "select * from [nt:base]";

            tools.printQuery(session, expression);

            log.info("[[ END TEST query ]]");

            Assert.assertTrue( true );

        } catch (Exception e) {
            Assert.assertTrue( false );
        }

    }

}
