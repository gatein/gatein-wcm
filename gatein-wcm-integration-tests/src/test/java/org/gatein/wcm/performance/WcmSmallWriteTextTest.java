package org.gatein.wcm.performance;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMRepositoryService;
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
public class WcmSmallWriteTextTest {
//    private static final Logger log = Logger.getLogger(WcmSmallWriteTextTest.class);
//
//    private static final int NTHREADS = 10;
//    private static final int NTESTS = 500;
//
//    @Deployment
//    public static Archive<?> createDeployment() {
//
//        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-integration-tests-performance.war")
//                .addClasses(WcmSmallWorker.class, WcmThreadFactory.class)
//                .addAsResource(new File("src/test/resources/jbossportletbridge.pdf"))
//                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
//                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
//                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));
//
//    }
//
//    @Resource(mappedName = "java:jboss/gatein-wcm")
//    WCMRepositoryService repos;
//
//    @Test
//    public void performanceTextTest() {
//
//        long start = System.currentTimeMillis();
//        try {
//            ExecutorService executor = Executors.newFixedThreadPool(NTHREADS, new WcmThreadFactory());
//            for (int i = 0; i < NTESTS; i++) {
//                WCMContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
//                Runnable worker = new WcmSmallWorker(i, cs);
//                executor.execute(worker);
//            }
//            executor.shutdown();
//            while (!executor.isTerminated()) {
//            }
//        } catch(Exception e) {
//            log.error(e.getMessage());
//            Assert.fail(e.getMessage());
//        }
//        long stop = System.currentTimeMillis();
//        double total = ((double)(stop-start)/1000);
//        double pertest = total / NTESTS;
//        DecimalFormat df = new DecimalFormat("0.###");
//
//        log.info( "[[ WcmSmallWriteTextTest # of operations " + NTESTS + " | # of threads " + NTHREADS +
//                  " | total time " + df.format(total) + " seconds " +
//                  " | time per operation " + df.format(pertest) + " seconds ]]");
//
//    }
//
//    public class WcmSmallWorker implements Runnable {
//        private final int nTest;
//
//        WCMContentService cs;
//
//        WcmSmallWorker(int nTest, WCMContentService cs) {
//            this.nTest = nTest;
//            this.cs = cs;
//        }
//
//        @Override
//        public void run() {
//
//            try {
//                log.debug( "WcmSmallWriteTextTest #" + nTest );
//                cs.createTextContent("test" + nTest, "es", "/", WcmResources.HTML_ES);
//                cs.createTextContent("test" + nTest, "en", "/", WcmResources.HTML_EN);
//                cs.createTextContent("test" + nTest, "fr", "/", WcmResources.HTML_FR);
//                cs.createTextContent("test" + nTest, "de", "/", WcmResources.HTML_DE);
//
//                List<String> locales = cs.getContentLocales("/test" + nTest);
//                if ( (!locales.contains("es")) ||
//                     (!locales.contains("en")) ||
//                     (!locales.contains("fr")) ||
//                     (!locales.contains("de"))
//                   ) {
//                    throw new Exception("Not locales in content");
//                }
//
//                // Cleaning test
//                cs.deleteContent("/test" + nTest);
//                cs.closeSession();
//            } catch (Exception e) {
//                log.error("WCM Small Test #" + nTest + " Failed " + e.getMessage());
//                Assert.fail(e.getMessage());
//            }
//       }
//
//    }
//
//

}
