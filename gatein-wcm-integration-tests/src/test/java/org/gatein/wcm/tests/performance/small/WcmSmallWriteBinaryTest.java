package org.gatein.wcm.tests.performance.small;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.BinaryContent;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.tests.performance.WcmThreadFactory;
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
public class WcmSmallWriteBinaryTest {
    private static final Logger log = Logger.getLogger("org.gatein.wcm.tests.performance");

    private static final int NTHREADS = 10;
    private static final int NTESTS = 500;

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-integration-tests-performance.war")
                .addClasses(WcmSmallWorker.class, WcmThreadFactory.class)
                .addAsResource(new File("src/test/resources/jbossportletbridge.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void performanceBinaryTest() {
        log.info( "[[ START TEST performanceBinaryTest ]]" );
        long start = System.currentTimeMillis();
        try {
            ExecutorService executor = Executors.newFixedThreadPool(NTHREADS, new WcmThreadFactory());
            for (int i = 0; i < NTESTS; i++) {
                ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
                Runnable worker = new WcmSmallWorker(i, cs);
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        } catch(Exception e) {
            log.error(e.getMessage());
            Assert.fail(e.getMessage());
        }
        long stop = System.currentTimeMillis();
        double total = ((double)(stop-start)/1000);
        double pertest = total / NTESTS;
        DecimalFormat df = new DecimalFormat("0.###");

        log.info( "[[ WcmSmallWriteBinaryTest # of operations " + NTESTS + " | # of threads " + NTHREADS +
                  " | total time " + df.format(total) + " seconds " +
                  " | time per operation " + df.format(pertest) + " seconds ]]");
        log.info( "[[ STOP TEST performanceBinaryTest ]]" );
    }

    public class WcmSmallWorker implements Runnable {
        private final int nTest;

        ContentService cs;

        WcmSmallWorker(int nTest, ContentService cs) {
            this.nTest = nTest;
            this.cs = cs;
        }

        @Override
        public void run() {

            try {
                log.info( "WcmSmallWriteBinaryTest #" + nTest );

                InputStream jpg = getClass().getClassLoader().getResourceAsStream(
                        "/wcm-whiteboard.jpg");
                InputStream pdf2 = getClass().getClassLoader().getResourceAsStream(
                        "/jbossportletbridge.pdf");
                byte[] _jpg = toByteArray(jpg);
                byte[] _pdf2 = toByteArray(pdf2);

                long sizeJpg = (long) _jpg.length;
                long sizePdf2 = (long) _pdf2.length;

                cs.createFolder("bintest" + nTest, "/");
                cs.createBinaryContent("jbossportletbridge", "en",
                        "/bintest" + nTest, "application/pdf", sizePdf2,
                        "GateIn-UserGuide-v3.5.pdf", new ByteArrayInputStream(_pdf2));
                cs.createBinaryContent("wcm-whiteboard", "en", "/bintest" + nTest,
                        "image/jpeg", sizeJpg, "wcm-whiteboard.jpg",
                        new ByteArrayInputStream(_jpg));

                // Reading content
                Content c1 = cs.getContent("/bintest" + nTest + "/jbossportletbridge", "en");
                Content c2 = cs.getContent("/bintest" + nTest + "/wcm-whiteboard", "en");

                if (c1 instanceof BinaryContent) {
                    BinaryContent b1 = (BinaryContent)c1;
                    byte[] checkPdf = toByteArray(b1.getContent());
                    if (checkPdf.length != _pdf2.length)
                        throw new Exception ("Error checking binary pdf content");
                }

                if (c2 instanceof BinaryContent) {
                    BinaryContent b2 = (BinaryContent)c2;
                    byte[] checkJpg = toByteArray(b2.getContent());
                    if (checkJpg.length != _jpg.length)
                        throw new Exception ("Error checking binary jpg content");
                }

                // Cleaning test
                cs.deleteContent("/bintest" + nTest);
                cs.closeSession();

            } catch (Exception e) {
                log.error("WcmSmallWriteBinaryTest #" + nTest + " Failed " + e.getMessage());
                Assert.fail(e.getMessage());
            }
       }

    }

    // Aux methods to manipulate InputStreams and print Content
    private byte[] toByteArray(InputStream is) {
        byte[] data = new byte[16384];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.close();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
