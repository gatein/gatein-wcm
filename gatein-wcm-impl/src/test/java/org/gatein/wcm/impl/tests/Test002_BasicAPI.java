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
                .addAsResource(new File("src/test/resources/cmis-spec-v1.0.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
                .addAsResource(new File("src/test/resources/jcr-2.0_specification.pdf"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void create_text_content() {

        log.info("[[ START TEST  create_text_content ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content c1 = cs.createTextContent("test01", "es", "/", "<h1>Primer test...</h1><p>Este es un párrafo.</p>", "UTF8");
            Content c2 = cs.createTextContent("test01", "en", "/", "<h1>First test...</h1><p>This is a paragraph</p>", "UTF8");
            Content c3 = cs.createTextContent("test01", "fr", "/", "<h1>First test...</h1><p>Ceci est un paragraphe</p>", "UTF8");
            Content c4 = cs.createTextContent("test01", "de", "/", "<h1>Erster Test...</h1><p>Dies ist ein Absatz</p>", "UTF8");
            log.info(c1);
            log.info(c2);
            log.info(c3);
            log.info(c4);

        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  create_text_content ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void create_folders() {

        log.info("[[ START TEST  create_folders ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content f1 = cs.createFolder("test02", "/");
            Content f2 = cs.createFolder("a", "/test02");
            Content f3 = cs.createFolder("b", "/test02");
            Content f4 = cs.createFolder("c", "/test02/a");
            Content f5 = cs.createFolder("d", "/test02/a");
            Content f6 = cs.createFolder("e", "/test02/b");
            Content f7 = cs.createFolder("f", "/test02/b");
            Content f8 = cs.createFolder("g", "/test02/a/c");
            log.info(f1);
            log.info(f2);
            log.info(f3);
            log.info(f4);
            log.info(f5);
            log.info(f6);
            log.info(f7);
            log.info(f8);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  create_folders ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void create_binary_content() {

        log.info("[[ START TEST  create_binary_content ]]");
        try {
            InputStream pdf = getClass().getClassLoader().getResourceAsStream("/cmis-spec-v1.0.pdf");
            InputStream jpg = getClass().getClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
            byte[] _pdf = toByteArray( pdf );
            byte[] _jpg = toByteArray( jpg );

            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content f1 = cs.createFolder("test03", "/");
            Content b1 = cs.createBinaryContent("cmis-spec", "en", "/test03", "application/pdf", (long)_pdf.length, "cmis-spec-v1.0.pdf", new ByteArrayInputStream( _pdf ) );
            Content b2 = cs.createBinaryContent("wcm-whiteboard", "en", "/test03", "image/jpeg", (long)_jpg.length, "wcm-whiteboard.jpg", new ByteArrayInputStream( _jpg ) );
            log.info(f1);
            log.info(b1);
            log.info(b2);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  create_binary_content ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void get_content() {

        log.info("[[ START TEST  get_content ]]");
        int MAX_FOLDERS = 10;
        try {
            InputStream pdf = getClass().getClassLoader().getResourceAsStream("/cmis-spec-v1.0.pdf");
            InputStream jpg = getClass().getClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
            byte[] _pdf = toByteArray( pdf );
            byte[] _jpg = toByteArray( jpg );
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            cs.createFolder("test04", "/");
            for (int i=0; i<MAX_FOLDERS; i++) {
                cs.createFolder("folder" + i, "/test04");
                cs.createFolder("sea", "/test04/folder" + i);
                cs.createTextContent("welcome", "es", "/test04/folder" + i + "/sea", "<h1>Bienvenido loop " + i + " ...</h1>", "UTF8");
                cs.createTextContent("welcome", "en", "/test04/folder" + i + "/sea", "<h1>Welcome loop " + i + " ...</h1>", "UTF8");
                cs.createFolder("land", "/test04/folder" + i);
                cs.createBinaryContent("document_pdf", "en", "/test04/folder" + i + "/land", "application/pdf", (long)_pdf.length, "cmis-spec-v1.0.pdf", new ByteArrayInputStream( _pdf ) );
                cs.createFolder("air", "/test04/folder" + i);
                cs.createBinaryContent("picture_jpg", "en", "/test04/folder" + i + "/air", "image/jpeg", (long)_jpg.length, "wcm-whiteboard.jpg", new ByteArrayInputStream( _jpg ) );
            }
            Content root_es = cs.getContent("/", "es");
            Content root_en = cs.getContent("/", "en");
            print (root_es, "/tmp");
            print (root_en, "/tmp");
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  get_content ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void get_content_locales() {

        log.info("[[ START TEST  get_content_locales ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            cs.createTextContent("test05", "es", "/", "<h1>Primer test...</h1>", "UTF8");
            cs.createTextContent("test05", "en", "/", "<h1>First test...</h1>", "UTF8");
            cs.createTextContent("test05", "fr", "/", "<h1>Premier test...</h1>", "UTF8");
            cs.createTextContent("test05", "de", "/", "<h1>Erster Test...</h1>", "UTF8");
            cs.createTextContent("test05", "pl", "/", "<h1>Pierwszy test...</h1>", "UTF8");
            cs.createTextContent("test05", "pt", "/", "<h1>Primeiro teste...</h1>", "UTF8");
            cs.createTextContent("test05", "it", "/", "<h1>Primo test...</h1>", "UTF8");

            List<String> locales = cs.getContentLocales( "/test05" );
            log.info("Locales: " + locales);
        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  test_locales ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void update_text_content() {

        log.info("[[ START TEST  update_text_content ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content c1 = cs.createTextContent("test06", "es", "/", "<h1>Primer test...</h1><p>Este es un párrafo.</p>", "UTF8");
            Content c2 = cs.createTextContent("test06", "en", "/", "<h1>First test...</h1><p>This is a paragraph</p>", "UTF8");
            Content c3 = cs.createTextContent("test06", "fr", "/", "<h1>First test...</h1><p>Ceci est un paragraphe</p>", "UTF8");
            Content c4 = cs.createTextContent("test06", "de", "/", "<h1>Erster Test...</h1><p>Dies ist ein Absatz</p>", "UTF8");
            log.info(c1);
            log.info(c2);
            log.info(c3);
            log.info(c4);
            c1 = cs.updateTextContent("/test06", "es", "<h1>Segundo test...</h1><p>Este es otro párrafo.</p>", "UTF8");
            c2 = cs.updateTextContent("/test06", "en", "<h1>Second test...</h1><p>This is another paragraph.</p>", "UTF8");
            log.info(c1);
            log.info(c2);

        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  update_text_content ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void update_folder_location() {

        log.info("[[ START TEST  update_folder_location ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content f1 = cs.createFolder("test07", "/");
            Content f2 = cs.createFolder("a", "/test07");
            Content f3 = cs.createFolder("b", "/test07");
            Content f4 = cs.createFolder("c", "/test07/a");
            Content f5 = cs.createFolder("d", "/test07/a");
            Content f6 = cs.createFolder("e", "/test07/b");
            Content f7 = cs.createFolder("f", "/test07/b");
            Content f8 = cs.createFolder("g", "/test07/a/c");
            log.info(f1);
            log.info(f2);
            log.info(f3);
            log.info(f4);
            log.info(f5);
            log.info(f6);
            log.info(f7);
            log.info(f8);
            Content f10 = cs.updateFolderLocation("/test07/a", "en", "/test07/b");
            log.info(f10);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  update_folder_location ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void update_folder_name() {

        log.info("[[ START TEST  update_folder_name ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content f1 = cs.createFolder("test08", "/");
            Content f2 = cs.createFolder("a", "/test08");
            Content f3 = cs.createFolder("b", "/test08");
            Content f4 = cs.createFolder("c", "/test08/a");
            Content f5 = cs.createFolder("d", "/test08/a");
            Content f6 = cs.createFolder("e", "/test08/b");
            Content f7 = cs.createFolder("f", "/test08/b");
            Content f8 = cs.createFolder("g", "/test08/a/c");
            log.info(f1);
            log.info(f2);
            log.info(f3);
            log.info(f4);
            log.info(f5);
            log.info(f6);
            log.info(f7);
            log.info(f8);
            Content f10 = cs.updateFolderName("/test08/a", "en", "new-name");
            log.info(f10);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  update_folder_name ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void update_binary_content() {

        log.info("[[ START TEST  update_binary_content ]]");
        try {
            InputStream pdf = getClass().getClassLoader().getResourceAsStream("/cmis-spec-v1.0.pdf");
            InputStream jpg = getClass().getClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
            InputStream pdf2 = getClass().getClassLoader().getResourceAsStream("/jcr-2.0_specification.pdf");
            byte[] _pdf = toByteArray( pdf );
            byte[] _jpg = toByteArray( jpg );
            byte[] _pdf2 = toByteArray( pdf2 );

            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content f1 = cs.createFolder("test09", "/");
            Content b1 = cs.createBinaryContent("cmis-spec", "en", "/test09", "application/pdf", (long)_pdf.length, "cmis-spec-v1.0.pdf", new ByteArrayInputStream( _pdf ) );
            Content b2 = cs.createBinaryContent("wcm-whiteboard", "en", "/test09", "image/jpeg", (long)_jpg.length, "wcm-whiteboard.jpg", new ByteArrayInputStream( _jpg ) );
            log.info(f1);
            log.info(b1);
            log.info(b2);
            b1 = cs.updateBinaryContent("/test09/cmis-spec", "en", "application/pdf", (long)_pdf2.length, "jcr-2.0_specification.pdf", new ByteArrayInputStream( _pdf2 ) );

        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  update_binary_content ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void delete_content() {

        log.info("[[ START TEST  delete_content ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Content c1 = cs.createTextContent("test10", "es", "/", "<h1>Primer test...</h1><p>Este es un párrafo.</p>", "UTF8");
            Content c2 = cs.createTextContent("test10", "en", "/", "<h1>First test...</h1><p>This is a paragraph</p>", "UTF8");
            Content c3 = cs.createTextContent("test10", "fr", "/", "<h1>First test...</h1><p>Ceci est un paragraphe</p>", "UTF8");
            Content c4 = cs.createTextContent("test10", "de", "/", "<h1>Erster Test...</h1><p>Dies ist ein Absatz</p>", "UTF8");
            log.info(c1);
            log.info(c2);
            log.info(c3);
            log.info(c4);

            log.info( cs.deleteContent("/test10", "es") );
            log.info( cs.deleteContent("/test10", "en") );
            log.info( cs.deleteContent("/test10", "fr") );
        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  delete_content ]]");
        Assert.assertTrue( true );
    }


    // Aux methods to manipulate InputStreams and print Content
    private byte[] toByteArray(InputStream is) {
        try {
            byte[] data = new byte[16384];
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

    private void print(Content c, String tmpFolder) {

        log.info("--> " + c.getLocation() + " - " + c.getId());
        if (c instanceof Folder) {
            List<Content> children = ((Folder)c).getChildren();
            for (Content _c : children)
                print( _c, tmpFolder );
        }
        if (c instanceof TextContent) {
            TextContent t = (TextContent)c;
            log.info("Text: " + t.getContent());
        }
        if (c instanceof BinaryContent) {
            BinaryContent b = (BinaryContent)c;
            String filename = b.getFileName();
            log.info(" Writting " + filename );
            inputStreamToFile(b.getContent(), filename, tmpFolder);
        }
        log.info("<-- " + c.getLocation() + " - " + c.getId());

    }

    private void inputStreamToFile(InputStream entrada, String file, String tmpFolder) {
        try{
          File f=new File(tmpFolder + "/" + file);
          OutputStream salida=new FileOutputStream(f);
          byte[] buf =new byte[16384];
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
