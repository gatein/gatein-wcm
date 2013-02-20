package org.gatein.wcm.impl.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.BinaryContent;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.content.Folder;
import org.gatein.wcm.api.model.content.TextContent;
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
public class Test002_BasicAPI {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.impl.tests.test002");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-test002.war")
                .addAsResource(new File("src/main/resources/cmis-spec-v1.0.pdf"))
                .addAsResource(new File("src/main/resources/wcm-whiteboard.jpg"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void basic_text_content() {

        try {
            log.info("[[ START TEST  basic_text_content ]]");

            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            log.info( cs );

            log.info( "Creating content..." );
            Content content = cs.createTextContent("test1", "es", "/", "<h1>Primer test...</h1>", "UTF8");
            log.info( "Content:" + content );
            String location = content.getLocation();
            if ("/".equals( location))
                location += content.getId();
            else
                location += "/" + content.getId();
            String parent = cs.deleteContet( location );
            log.info( "Deleted. Parent location: " + parent );

            log.info("[[ STOP TEST  basic_text_content ]]");
            Assert.assertTrue( true );
        }  catch (Exception e) {

            log.error(e.getMessage());

            Assert.assertTrue( false );
        }

    }

    @Test
    public void basic_folders() {

        try {
            log.info("[[ START TEST  basic_folders ]]");

            InputStream pdf = getClass().getClassLoader().getResourceAsStream("/cmis-spec-v1.0.pdf");
            InputStream jpg = getClass().getClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
            byte[] _pdf = toByteArray( pdf );
            byte[] _jpg = toByteArray( jpg );



            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            log.info( cs );

            String folder = "folder";

            long start = System.currentTimeMillis();

            for (int i=0; i<2; i++) {

                cs.createFolder(folder + i, "/");
                cs.createFolder("sea", "/" + folder + i);
                cs.createTextContent("test1", "es", "/folder" + i + "/sea", "<h1>Primer test loop " + i + " ...</h1>", "UTF8");
                cs.createTextContent("test1", "en", "/folder" + i + "/sea", "<h1>First test loop " + i + " ...</h1>", "UTF8");
                cs.createFolder("land", "/" + folder + i);
                cs.createBinaryContent("test2", "en", "/folder" + i + "/land", "application/pdf", new Long( _pdf.length ), "cmis-spec-v1.0.pdf", new ByteArrayInputStream( _pdf ) );
                cs.createFolder("air", "/" + folder + i);
                cs.createBinaryContent("test3", "en", "/folder" + i + "/air", "image/jpeg", new Long( _jpg.length ), "wcm-whiteboard.jpg", new ByteArrayInputStream( _jpg ) );

            }

            long stop = System.currentTimeMillis();

            log.info("Upload content: " + (stop-start) + " ms");

            log.info("[[ READING ... ]]");

            start = System.currentTimeMillis()
                    ;
            Content root_es = cs.getContent("/", "es");
            Content root_en = cs.getContent("/", "en");

            print (root_es);
            print (root_en);

            stop = System.currentTimeMillis();

            log.info("Reading content: " + (stop-start) + " ms");

            log.info("[[ STOP TEST  basic_folders ]]");

            Assert.assertTrue( true );

        } catch (Exception e) {

            log.error(e.getMessage());

            Assert.assertTrue( false );
        }

    }

    @Test
    public void test_locales() {

        try {
            log.info("[[ START TEST  test_locales ]]");

            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");

            cs.createTextContent("test1", "es", "/", "<h1>Primer test...</h1>", "UTF8");
            cs.createTextContent("test1", "en", "/", "<h1>First test...</h1>", "UTF8");
            cs.createTextContent("test1", "fr", "/", "<h1>Premier test...</h1>", "UTF8");

            List<String> locales = cs.getContentLocales( "/test1" );

            log.info("Locales: " + locales);

            log.info("[[ STOP TEST  test_locales ]]");

            Assert.assertTrue( true );

        }  catch (Exception e) {

            log.error(e.getMessage());

            Assert.assertTrue( false );
        }

    }

    private byte[] toByteArray(InputStream is) {
        try {
            byte[] data = new byte[16000];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (Exception e) {
            log.error("Error creating createBinaryContent() transforming toByteArray(). Msg: " + e.getMessage());
        }
        return null;
    }

    public void print(Content c) {

        log.info("--> " + c.getLocation() + " - " + c.getId());
        if (c instanceof Folder) {
            List<Content> children = ((Folder)c).getChildren();
            for (Content _c : children)
                print( _c );
        }
        if (c instanceof TextContent) {
            TextContent t = (TextContent)c;
            log.info("Text: " + t.getContent());
        }
        if (c instanceof BinaryContent) {
            BinaryContent b = (BinaryContent)c;
            String filename = b.getFileName();
            log.info(" Writting " + filename );
            inputStreamToFile(b.getContent(), filename);
        }
        log.info("<-- " + c.getLocation() + " - " + c.getId());

    }

    public void inputStreamToFile(InputStream entrada, String file) {
        try{
          File f=new File("/tmp/tests/" + file);
          OutputStream salida=new FileOutputStream(f);
          byte[] buf =new byte[1024];
          int len;
          while((len=entrada.read(buf))>0) {
             salida.write(buf,0,len);
          }
          salida.close();
          entrada.close();
        } catch(IOException e) {
          log.error("Error creating file: " + e.getMessage());
       }
    }


}
