package org.gatein.wcm.jcr.tests;

import java.io.File;

import javax.annotation.Resource;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

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
public class Test004JcrQueriesTest {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.integration.tests.test004");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-integration-tests-test004.war")
                .addAsResource(new File("src/test/resources/jbossportletbridge.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
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

            // All nodes
            // String expression = "select * from [nt:base]";

            // All textcontent nodes
            //String expression = "select * from [nt:folder] as id " +
            //		"where id.[jcr:description] IN ('textcontent')";

            // All nodes from one category
            String expression = "select * from [nt:base] as id " +
            		"where [jcr:path] LIKE '/__categories/news4/__references%'";

            Query query = session.getWorkspace().getQueryManager().createQuery(expression, Query.JCR_SQL2);
            QueryResult results = query.execute();

            tools.print(expression);
            tools.print(results);

            log.info("[[ END TEST query ]]");

            Assert.assertTrue( true );

        } catch (Exception e) {
            log.error( e.getMessage() );
            Assert.assertTrue( false );
        }

    }

}
