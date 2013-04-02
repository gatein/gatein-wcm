package org.gatein.wcm.tests.performance.medium;

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

import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.tests.performance.WcmResources;
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

/*
 * Metric for this test:
 *
 *  Each test creates:
 *      - 36 Folders
 *      - 112 TextContent
 *      - 224 BinaryContent
 */
@RunWith(Arquillian.class)
public class WcmMediumSiteTest {
    private static final Logger log = Logger.getLogger(WcmMediumSiteTest.class);

    private static final int NTHREADS = 1;
    private static final int NTESTS = 1;

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-integration-tests-performance.war")
                .addClasses(WcmSmallWorker.class, WcmThreadFactory.class)
                .addAsResource(new File("src/test/resources/jbossportletbridge.pdf"))
                .addAsResource(new File("src/test/resources/GateIn-UserGuide-v3.5.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void performanceMediumSiteTest() {
        log.info( "[[ START TEST performanceMediumSiteTest ]]" );
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

        log.info( "[[ WcmMediumSiteTest # of operations " + NTESTS + " | # of threads " + NTHREADS +
                  " | total time " + df.format(total) + " seconds " +
                  " | time per operation " + df.format(pertest) + " seconds ]]");
        log.info( "[[ STOP TEST performance ]]" );
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
                log.info( "WcmMediumSiteTest #" + nTest );

                // Loading resources
                InputStream jpg = getClass().getClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
                InputStream pdf = getClass().getClassLoader().getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
                byte[] _jpg = toByteArray(jpg);
                byte[] _pdf = toByteArray(pdf);
                long sizeJpg = (long) _jpg.length;
                long sizePdf = (long) _pdf.length;

                // Creating a structure
                // We will simulate a mobile shop

                String main = "mobileshop" + nTest;
                cs.createFolder(main, "/");

                main = "/" + main;
                cs.createFolder("Mobile phones", main);
                cs.createFolder("Price plans", main);
                cs.createFolder("Upgrades", main);
                cs.createFolder("Accessories", main);
                cs.createFolder("Tablets", main);
                cs.createFolder("Offers", main);
                cs.createFolder("Help & News", main);

                String mobilePhones = "/" + main + "/Mobile phones";
                cs.createFolder("Android", mobilePhones);

                String mobile = "Android";
                cs.createTextContent(mobile + " 1", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 1", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 1 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 1 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(mobile + " 2", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 2", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 2 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 2 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Apple iOS", mobilePhones);

                mobile = "Apple iOS";
                cs.createTextContent(mobile + " 1", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 1", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 1 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 1 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(mobile + " 2", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 2", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 2 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 2 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("BlackBerry", mobilePhones);

                mobile = "BlackBerry";
                cs.createTextContent(mobile + " 1", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 1", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 1 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 1 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(mobile + " 2", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 2", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 2 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 2 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Symbian", mobilePhones);

                mobile = "Symbian";
                cs.createTextContent(mobile + " 1", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 1", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 1 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 1 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(mobile + " 2", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 2", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 2 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 2 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));


                cs.createFolder("Windows", mobilePhones);

                mobile = "Windows";
                cs.createTextContent(mobile + " 1", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 1", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 1 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 1 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 1 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(mobile + " 2", "en", mobilePhones + "/" + mobile, WcmResources.HTML_EN);
                cs.createTextContent(mobile + " 2", "es", mobilePhones + "/" + mobile, WcmResources.HTML_ES);
                cs.createBinaryContent(mobile + " 2 description", "en", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 description", "es", mobilePhones + "/" + mobile, "application/pdf", sizePdf, mobile + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(mobile + " 2 picture", "en", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(mobile + " 2 picture", "es", mobilePhones + "/" + mobile, "image/jpeg", sizeJpg, mobile + "-picture.jpg", new ByteArrayInputStream(_jpg));

                String pricePlans = "/" + main + "/Price plans";
                cs.createFolder("O2", pricePlans);

                String pricePlan = "O2";
                cs.createTextContent(pricePlan + " 1", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 1", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 1 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 1 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(pricePlan + " 2", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 2", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 2 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 2 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Orange", pricePlans);

                pricePlan = "Orange";
                cs.createTextContent(pricePlan + " 1", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 1", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 1 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 1 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(pricePlan + " 2", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 2", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 2 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 2 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Talkmobile", pricePlans);

                pricePlan = "Talkmobile";
                cs.createTextContent(pricePlan + " 1", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 1", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 1 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 1 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(pricePlan + " 2", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 2", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 2 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 2 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Three", pricePlans);

                pricePlan = "Three";
                cs.createTextContent(pricePlan + " 1", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 1", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 1 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 1 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(pricePlan + " 2", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 2", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 2 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 2 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("T-Mobile", pricePlans);

                pricePlan = "T-Mobile";
                cs.createTextContent(pricePlan + " 1", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 1", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 1 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 1 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(pricePlan + " 2", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 2", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 2 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 2 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Virgin Media", pricePlans);

                pricePlan = "Virgin Media";
                cs.createTextContent(pricePlan + " 1", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 1", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 1 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 1 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(pricePlan + " 2", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 2", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 2 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 2 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Vodafone", pricePlans);

                pricePlan = "Vodafone";
                cs.createTextContent(pricePlan + " 1", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 1", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 1 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 1 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 1 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(pricePlan + " 2", "en", pricePlans + "/" + pricePlan, WcmResources.HTML_EN);
                cs.createTextContent(pricePlan + " 2", "es", pricePlans + "/" + pricePlan, WcmResources.HTML_ES);
                cs.createBinaryContent(pricePlan + " 2 description", "en", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 description", "es", pricePlans + "/" + pricePlan, "application/pdf", sizePdf, pricePlan + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(pricePlan + " 2 picture", "en", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(pricePlan + " 2 picture", "es", pricePlans + "/" + pricePlan, "image/jpeg", sizeJpg, pricePlan + "-picture.jpg", new ByteArrayInputStream(_jpg));


                String upgrades = "/" + main + "/Upgrades";
                cs.createFolder("Pay monthly upgrades", upgrades);

                String upgrade = "Pay monthly upgrades";
                cs.createTextContent(upgrade + " 1", "en", upgrades + "/" + upgrade, WcmResources.HTML_EN);
                cs.createTextContent(upgrade + " 1", "es", upgrades + "/" + upgrade, WcmResources.HTML_ES);
                cs.createBinaryContent(upgrade + " 1 description", "en", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 1 description", "es", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 1 picture", "en", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(upgrade + " 1 picture", "es", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(upgrade + " 2", "en", upgrades + "/" + upgrade, WcmResources.HTML_EN);
                cs.createTextContent(upgrade + " 2", "es", upgrades + "/" + upgrade, WcmResources.HTML_ES);
                cs.createBinaryContent(upgrade + " 2 description", "en", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 2 description", "es", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 2 picture", "en", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(upgrade + " 2 picture", "es", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Latest phones", upgrades);

                upgrade = "Latest phones";
                cs.createTextContent(upgrade + " 1", "en", upgrades + "/" + upgrade, WcmResources.HTML_EN);
                cs.createTextContent(upgrade + " 1", "es", upgrades + "/" + upgrade, WcmResources.HTML_ES);
                cs.createBinaryContent(upgrade + " 1 description", "en", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 1 description", "es", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 1 picture", "en", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(upgrade + " 1 picture", "es", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(upgrade + " 2", "en", upgrades + "/" + upgrade, WcmResources.HTML_EN);
                cs.createTextContent(upgrade + " 2", "es", upgrades + "/" + upgrade, WcmResources.HTML_ES);
                cs.createBinaryContent(upgrade + " 2 description", "en", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 2 description", "es", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 2 picture", "en", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(upgrade + " 2 picture", "es", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Coming soon", upgrades);

                upgrade = "Coming soon";
                cs.createTextContent(upgrade + " 1", "en", upgrades + "/" + upgrade, WcmResources.HTML_EN);
                cs.createTextContent(upgrade + " 1", "es", upgrades + "/" + upgrade, WcmResources.HTML_ES);
                cs.createBinaryContent(upgrade + " 1 description", "en", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 1 description", "es", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 1 picture", "en", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(upgrade + " 1 picture", "es", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(upgrade + " 2", "en", upgrades + "/" + upgrade, WcmResources.HTML_EN);
                cs.createTextContent(upgrade + " 2", "es", upgrades + "/" + upgrade, WcmResources.HTML_ES);
                cs.createBinaryContent(upgrade + " 2 description", "en", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 2 description", "es", upgrades + "/" + upgrade, "application/pdf", sizePdf, upgrade + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(upgrade + " 2 picture", "en", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(upgrade + " 2 picture", "es", upgrades + "/" + upgrade, "image/jpeg", sizeJpg, upgrade + "-picture.jpg", new ByteArrayInputStream(_jpg));

                String accessories = "/" + main + "/Accessories";
                cs.createFolder("Cases & screen protectors", accessories);

                String accessory = "Cases & screen protectors";
                cs.createTextContent(accessory + " 1", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 1", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 1 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 1 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(accessory + " 2", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 2", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 2 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 2 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Memory cards & USB sticks", accessories);

                accessory = "Memory cards & USB sticks";
                cs.createTextContent(accessory + " 1", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 1", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 1 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 1 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(accessory + " 2", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 2", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 2 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 2 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("In-car & hands-free", accessories);

                accessory = "In-car & hands-free";
                cs.createTextContent(accessory + " 1", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 1", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 1 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 1 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(accessory + " 2", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 2", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 2 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 2 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Chargers & batteries", accessories);

                accessory = "Chargers & batteries";
                cs.createTextContent(accessory + " 1", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 1", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 1 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 1 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(accessory + " 2", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 2", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 2 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 2 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Headphones & music", accessories);

                accessory = "Headphones & music";
                cs.createTextContent(accessory + " 1", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 1", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 1 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 1 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 1 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(accessory + " 2", "en", accessories + "/" + accessory, WcmResources.HTML_EN);
                cs.createTextContent(accessory + " 2", "es", accessories + "/" + accessory, WcmResources.HTML_ES);
                cs.createBinaryContent(accessory + " 2 description", "en", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 description", "es", accessories + "/" + accessory, "application/pdf", sizePdf, accessory + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(accessory + " 2 picture", "en", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(accessory + " 2 picture", "es", accessories + "/" + accessory, "image/jpeg", sizeJpg, accessory + "-picture.jpg", new ByteArrayInputStream(_jpg));

                String tablets = "/" + main + "/Tablets";
                cs.createFolder("Amazon", tablets);

                String tablet = "Amazon";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Apple", tablets);

                tablet = "Apple";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Asus", tablets);

                tablet = "Asus";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("BlackBerry", tablets);

                tablet = "BlackBerry";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Motorola", tablets);

                tablet = "Motorola";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Samsung", tablets);

                tablet = "Samsung";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Sony", tablets);

                tablet = "Sony";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                cs.createFolder("Versus", tablets);

                tablet = "Versus";
                cs.createTextContent(tablet + " 1", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 1", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 1 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 1 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 1 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createTextContent(tablet + " 2", "en", tablets + "/" + tablet, WcmResources.HTML_EN);
                cs.createTextContent(tablet + " 2", "es", tablets + "/" + tablet, WcmResources.HTML_ES);
                cs.createBinaryContent(tablet + " 2 description", "en", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 description", "es", tablets + "/" + tablet, "application/pdf", sizePdf, tablet + "-description.pdf", new ByteArrayInputStream(_pdf));
                cs.createBinaryContent(tablet + " 2 picture", "en", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));
                cs.createBinaryContent(tablet + " 2 picture", "es", tablets + "/" + tablet, "image/jpeg", sizeJpg, tablet + "-picture.jpg", new ByteArrayInputStream(_jpg));

                // Cleaning test
                // Not cleaning at the moment
                cs.deleteContent("/mobileshop" + nTest);
                cs.closeSession();

            } catch (Exception e) {
                log.error("WcmMediumSiteTest #" + nTest + " Failed " + e.getMessage());
                e.printStackTrace();
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
