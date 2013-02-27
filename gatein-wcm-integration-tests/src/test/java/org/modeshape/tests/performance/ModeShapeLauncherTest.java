package org.modeshape.tests.performance;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ModeShapeLauncherTest {
    private static final Logger log = Logger.getLogger("org.modeshape.tests.performance");

    private static final int NTHREDS = 10;
    private static final int NTESTS = 500;

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "modeshape-tests-performance.war")
                .addClasses(ModeShapeWorker.class, ModeShapeThreadFactory.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void msTestPerformance() {
        log.info( "[[ START TEST msTestPerformance ]]" );
        try {
            ExecutorService executor = Executors.newFixedThreadPool(NTHREDS, new ModeShapeThreadFactory());
            for (int i = 0; i < NTESTS; i++) {

                SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
                javax.jcr.Session jcrSession = repository.login(credentials, "default");
                Runnable worker = new ModeShapeWorker(i, jcrSession);
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
        log.info( "[[ STOP TEST msTestPerformance ]]" );
    }

}
