package org.gatein.wcm.ui.tests;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
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
public class SamplePageTest {

    private static final Logger log = Logger
            .getLogger(SamplePageTest.class);

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap
                .create(WebArchive.class, "gatein-wcm-impl-test.war")
                .addAsResource(
                        new File("src/test/resources/testBackground.png"))
                .addAsResource(
                        new File("src/test/resources/testMain.jpg"))
                .addAsResource(
                        new File("src/test/resources/testStyle.css"))
                .addAsResource(
                        new File("src/test/resources/test.html"))
                .addAsResource(
                        new File("src/test/resources/test.html"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));
    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void createSimpleWebSite() throws ContentIOException,
            ContentSecurityException, ContentException, IOException {

        InputStream png = getClass().getClassLoader().getResourceAsStream(
                "/testBackground.png");
        InputStream jpg = getClass().getClassLoader().getResourceAsStream(
                "/testMain.jpg");
        InputStream css = getClass().getClassLoader().getResourceAsStream(
                "/testStyle.css");
        InputStream html = getClass().getClassLoader().getResourceAsStream(
                "/test.html");

        byte[] _png = toByteArray(png);
        byte[] _jpg = toByteArray(jpg);
        byte[] _css = toByteArray(css);
        String _html = toString(html);

        log.debug("[[ START TEST  createSimpleWebSite ]]");
        ContentService cs = repos.createContentSession("sample", "default",
                "admin", "admin");

        cs.createFolder("testWebSite", "/");
        // Locale "en"
        cs.createTextContent("test", "en", "/testWebSite", _html);
        cs.createBinaryContent("testBackground.png", "en",
                "/testWebSite", "image/png", (long) _png.length,
                "testBackground.png", new ByteArrayInputStream(_png));
        cs.createBinaryContent("testMain.jpg", "en",
                "/testWebSite", "image/jpg", (long) _jpg.length,
                "testMain.jpg", new ByteArrayInputStream(_jpg));
        cs.createBinaryContent("testStyle.css", "en",
                "/testWebSite", "text/css", (long) _css.length,
                "testStyle.css", new ByteArrayInputStream(_css));
        // Locale "es"
        cs.createTextContent("test", "es", "/testWebSite", _html);
        cs.createBinaryContent("testBackground.png", "es",
                "/testWebSite", "image/png", (long) _png.length,
                "testBackground.png", new ByteArrayInputStream(_png));
        cs.createBinaryContent("testMain.jpg", "es",
                "/testWebSite", "image/jpg", (long) _jpg.length,
                "testMain.jpg", new ByteArrayInputStream(_jpg));
        cs.createBinaryContent("testStyle.css", "es",
                "/testWebSite", "text/css", (long) _css.length,
                "testStyle.css", new ByteArrayInputStream(_css));

        // Create a subfolder to check if organize content in subfolders
        cs.createFolder("folder1", "/testWebSite");
        cs.createContentProperty("/testWebSite/folder1", "es", "title", "Mauris vulputate dolor");

        String test_content = "Maecenas pede nisl, elementum eu, ornare ac, malesuada at, erat. " +
        		"Proin gravida orci porttitor enim accumsan lacinia. " +
        		"Donec condimentum, urna non molestie semper, ligula enim ornare nibh, quis laoreet eros quam eget ante. " +
        		"Aliquam libero. Vivamus nisl nibh, iaculis vitae, viverra sit amet, ullamcorper vitae, turpis. Aliquam erat volutpat. " +
        		"Vestibulum dui sem, pulvinar sed, imperdiet nec, iaculis nec, leo. Fusce odio. Etiam arcu dui, faucibus eget, placerat vel, sodales eget, orci. " +
        		"Donec ornare neque ac sem. Mauris aliquet. Aliquam sem leo, vulputate sed, convallis at, ultricies quis, justo. Donec nonummy magna quis risus.";

        cs.createTextContent("c1", "es", "/testWebSite/folder1", test_content);
        cs.createContentProperty("/testWebSite/folder1/c1", "es", "title", "Maecenas luctus lectus");
        cs.createContentProperty("/testWebSite/folder1/c1", "es", "summary", "Quisque dictum integer nisl risus, sagittis convallis, rutrum id, congue, and nibh.");

        cs.createTextContent("c1", "en", "/testWebSite/folder1", test_content);
        cs.createContentProperty("/testWebSite/folder1/c1", "en", "title", "Maecenas luctus lectus");
        cs.createContentProperty("/testWebSite/folder1/c1", "en", "summary", "Quisque dictum integer nisl risus, sagittis convallis, rutrum id, congue, and nibh.");

        log.debug("[[ STOP TEST  createSimpleWebSite ]]");
        Assert.assertTrue(true);
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

    private String toString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

}
