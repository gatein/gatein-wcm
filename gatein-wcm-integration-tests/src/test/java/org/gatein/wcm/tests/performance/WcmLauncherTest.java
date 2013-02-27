package org.gatein.wcm.tests.performance;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
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
public class WcmLauncherTest {
    private static final Logger log = Logger.getLogger("org.gatein.wcm.integration.tests.performance");

    private static final int NTHREDS = 10;
    private static final int NTESTS = 500;

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-integration-tests-performance.war")
                .addClasses(WcmWorker.class, WcmThreadFactory.class)
                .addAsResource(new File("src/test/resources/jbossportletbridge.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void performance() {
        log.info( "[[ START TEST performance ]]" );
        try {
            ExecutorService executor = Executors.newFixedThreadPool(NTHREDS, new WcmThreadFactory());
            for (int i = 0; i < NTESTS; i++) {
                ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
                Runnable worker = new WcmWorker(i, cs);
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
        log.info( "[[ STOP TEST performance ]]" );
    }

}
