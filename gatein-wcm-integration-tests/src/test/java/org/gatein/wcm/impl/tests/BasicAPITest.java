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
public class BasicAPITest {

	private static final Logger log = Logger
			.getLogger("org.gatein.wcm.impl.tests");

	@Deployment
	public static Archive<?> createDeployment() {

		return ShrinkWrap
				.create(WebArchive.class, "gatein-wcm-impl-test.war")
				.addAsResource(
						new File("src/test/resources/GateIn-UserGuide-v3.5.pdf"))
				.addAsResource(
						new File("src/test/resources/wcm-whiteboard.jpg"))
				.addAsResource(
						new File("src/test/resources/jbossportletbridge.pdf"))
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

	}

	@Resource(mappedName = "java:jboss/gatein-wcm")
	RepositoryService repos;

	@Test
	public void createTextContent() throws ContentIOException,
			ContentSecurityException, ContentException {

		log.info("[[ START TEST  createTextContent ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		Content c1 = cs.createTextContent("test01", "es", "/",
				"<h1>Primer test...</h1><p>Este es un párrafo.</p>");
		Content c2 = cs.createTextContent("test01", "en", "/",
				"<h1>First test...</h1><p>This is a paragraph</p>");
		Content c3 = cs.createTextContent("test01", "fr", "/",
				"<h1>First test...</h1><p>Ceci est un paragraphe</p>");
		Content c4 = cs.createTextContent("test01", "de", "/",
				"<h1>Erster Test...</h1><p>Dies ist ein Absatz</p>");
		log.info(c1);
		Assert.assertTrue(c1.getId().equals("test01"));
		Assert.assertTrue(c1.getLocale().equals("es"));
		Assert.assertTrue(c1.getLocation().equals("/"));
		log.info(c2);
		Assert.assertTrue(c2.getId().equals("test01"));
		Assert.assertTrue(c2.getLocale().equals("en"));
		Assert.assertTrue(c2.getLocation().equals("/"));
		log.info(c3);
		Assert.assertTrue(c3.getId().equals("test01"));
		Assert.assertTrue(c3.getLocale().equals("fr"));
		Assert.assertTrue(c3.getLocation().equals("/"));
		log.info(c4);
		Assert.assertTrue(c4.getId().equals("test01"));
		Assert.assertTrue(c4.getLocale().equals("de"));
		Assert.assertTrue(c4.getLocation().equals("/"));

		// Cleaning test
		cs.deleteContent("/test01");
		log.info("[[ STOP TEST  createTextContent ]]");
		Assert.assertTrue(true);
	}

	@Test
	public void createFolders() throws ContentException, ContentIOException,
			ContentSecurityException {

		log.info("[[ START TEST  createFolders ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		Content f1 = cs.createFolder("test02", "/");
		Content f2 = cs.createFolder("a", "/test02");
		Content f3 = cs.createFolder("b", "/test02");
		Content f4 = cs.createFolder("c", "/test02/a");
		Content f5 = cs.createFolder("d", "/test02/a");
		Content f6 = cs.createFolder("e", "/test02/b");
		Content f7 = cs.createFolder("f", "/test02/b");
		Content f8 = cs.createFolder("g", "/test02/a/c");
		log.info(f1);
		Assert.assertTrue(f1.getId().equals("test02"));
		Assert.assertTrue(f1.getLocation().equals("/"));
		log.info(f2);
		Assert.assertTrue(f2.getId().equals("a"));
		Assert.assertTrue(f2.getLocation().equals("/test02"));
		log.info(f3);
		Assert.assertTrue(f3.getId().equals("b"));
		Assert.assertTrue(f3.getLocation().equals("/test02"));
		log.info(f4);
		Assert.assertTrue(f4.getId().equals("c"));
		Assert.assertTrue(f4.getLocation().equals("/test02/a"));
		log.info(f5);
		Assert.assertTrue(f5.getId().equals("d"));
		Assert.assertTrue(f5.getLocation().equals("/test02/a"));
		log.info(f6);
		Assert.assertTrue(f6.getId().equals("e"));
		Assert.assertTrue(f6.getLocation().equals("/test02/b"));
		log.info(f7);
		Assert.assertTrue(f7.getId().equals("f"));
		Assert.assertTrue(f7.getLocation().equals("/test02/b"));
		log.info(f8);
		Assert.assertTrue(f8.getId().equals("g"));
		Assert.assertTrue(f8.getLocation().equals("/test02/a/c"));

		// Cleaning test
		cs.deleteContent("/test02");
		log.info("[[ STOP TEST  createFolders ]]");
	}

	@Test
	public void createBinaryContent() throws ContentException,
			ContentIOException, ContentSecurityException {

		log.info("[[ START TEST  createBinaryContent ]]");
		InputStream pdf = getClass().getClassLoader().getResourceAsStream(
				"/GateIn-UserGuide-v3.5.pdf");
		InputStream jpg = getClass().getClassLoader().getResourceAsStream(
				"/wcm-whiteboard.jpg");
		byte[] _pdf = toByteArray(pdf);
		byte[] _jpg = toByteArray(jpg);

		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		Content f1 = cs.createFolder("test03", "/");
		Content b1 = cs.createBinaryContent("gatein-userguide", "en",
				"/test03", "application/pdf", (long) _pdf.length,
				"GateIn-UserGuide-v3.5.pdf", new ByteArrayInputStream(_pdf));
		Content b2 = cs.createBinaryContent("wcm-whiteboard", "en", "/test03",
				"image/jpeg", Long.valueOf(_jpg.length), "wcm-whiteboard.jpg",
				new ByteArrayInputStream(_jpg));
		log.info(f1);
		log.info(b1);
		Assert.assertTrue(((BinaryContent) b1).getFileName().equals(
				"GateIn-UserGuide-v3.5.pdf"));
		Assert.assertTrue(((BinaryContent) b1).getSize() == _pdf.length);
		Assert.assertTrue(((BinaryContent) b1).getContentType().equals(
				"application/pdf"));
		Assert.assertTrue(((BinaryContent) b1).getContent() != null);
		log.info(b2);
		Assert.assertTrue(((BinaryContent) b2).getFileName().equals(
				"wcm-whiteboard.jpg"));
		Assert.assertTrue(((BinaryContent) b2).getSize() == _jpg.length);
		Assert.assertTrue(((BinaryContent) b2).getContentType().equals(
				"image/jpeg"));
		Assert.assertTrue(((BinaryContent) b2).getContent() != null);

		// Cleaning test
		cs.deleteContent("/test03");
		log.info("[[ STOP TEST  createBinaryContent ]]");
	}

	@Test
	public void getContent() throws ContentIOException,
			ContentSecurityException, ContentException {

		log.info("[[ START TEST  getContent ]]");
		int MAX_FOLDERS = 10;
		InputStream pdf = getClass().getClassLoader().getResourceAsStream(
				"/GateIn-UserGuide-v3.5.pdf");
		InputStream jpg = getClass().getClassLoader().getResourceAsStream(
				"/wcm-whiteboard.jpg");
		byte[] _pdf = toByteArray(pdf);
		byte[] _jpg = toByteArray(jpg);
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		cs.createFolder("test04", "/");
		for (int i = 0; i < MAX_FOLDERS; i++) {
			cs.createFolder("folder" + i, "/test04");
			cs.createFolder("sea", "/test04/folder" + i);
			cs.createTextContent("welcome", "es",
					"/test04/folder" + i + "/sea", "<h1>Bienvenido loop " + i
							+ " ...</h1>");
			cs.createTextContent("welcome", "en",
					"/test04/folder" + i + "/sea", "<h1>Welcome loop " + i
							+ " ...</h1>");
			cs.createFolder("land", "/test04/folder" + i);
			cs.createBinaryContent("document_pdf", "en", "/test04/folder" + i
					+ "/land", "application/pdf", (long) _pdf.length,
					"GateIn-UserGuide-v3.5.pdf", new ByteArrayInputStream(_pdf));
			cs.createFolder("air", "/test04/folder" + i);
			cs.createBinaryContent("picture_jpg", "en", "/test04/folder" + i
					+ "/air", "image/jpeg", (long) _jpg.length,
					"wcm-whiteboard.jpg", new ByteArrayInputStream(_jpg));
		}
		Content root_es = cs.getContent("/", "es");
		Content root_en = cs.getContent("/", "en");
		print(root_es, "/tmp");
		print(root_en, "/tmp");

		// Cleaning test
		cs.deleteContent("/test04");
		log.info("[[ STOP TEST  getContent ]]");
	}

	@Test
	public void getContentLocales() throws ContentException,
			ContentIOException, ContentSecurityException {

		log.info("[[ START TEST  getContentLocales ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		cs.createTextContent("test05", "es", "/", "<h1>Primer test...</h1>");
		cs.createTextContent("test05", "en", "/", "<h1>First test...</h1>");
		cs.createTextContent("test05", "fr", "/", "<h1>Premier test...</h1>");
		cs.createTextContent("test05", "de", "/", "<h1>Erster Test...</h1>");
		cs.createTextContent("test05", "pl", "/", "<h1>Pierwszy test...</h1>");
		cs.createTextContent("test05", "pt", "/", "<h1>Primeiro teste...</h1>");
		cs.createTextContent("test05", "it", "/", "<h1>Primo test...</h1>");

		List<String> locales = cs.getContentLocales("/test05");
		log.info("Locales: " + locales);
		Assert.assertTrue(locales.contains("it"));

		Content c = cs.getContent("/test05", "es");
		Assert.assertEquals(7, c.getLocales().size());

		// Cleaning test
		cs.deleteContent("/test05");
		log.info("[[ STOP TEST  getContentLocales ]]");
		Assert.assertTrue(true);
	}

	@Test
	public void updateTextContent() throws ContentException,
			ContentIOException, ContentSecurityException {

		log.info("[[ START TEST  updateTextContent ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		Content c1 = cs.createTextContent("test06", "es", "/",
				"<h1>Primer test...</h1><p>Este es un párrafo.</p>");
		Content c2 = cs.createTextContent("test06", "en", "/",
				"<h1>First test...</h1><p>This is a paragraph</p>");
		Content c3 = cs.createTextContent("test06", "fr", "/",
				"<h1>First test...</h1><p>Ceci est un paragraphe</p>");
		Content c4 = cs.createTextContent("test06", "de", "/",
				"<h1>Erster Test...</h1><p>Dies ist ein Absatz</p>");
		log.info(c1);
		Assert.assertTrue(((TextContent) c1).getContent().equals(
				"<h1>Primer test...</h1><p>Este es un párrafo.</p>"));
		log.info(c2);
		Assert.assertTrue(((TextContent) c2).getContent().equals(
				"<h1>First test...</h1><p>This is a paragraph</p>"));
		log.info(c3);
		log.info(c4);
		c1 = cs.updateTextContent("/test06", "es",
				"<h1>Segundo test...</h1><p>Este es otro párrafo.</p>");
		c2 = cs.updateTextContent("/test06", "en",
				"<h1>Second test...</h1><p>This is another paragraph.</p>");
		log.info(c1);
		Assert.assertTrue(((TextContent) c1).getContent().equals(
				"<h1>Segundo test...</h1><p>Este es otro párrafo.</p>"));
		log.info(c2);
		Assert.assertTrue(((TextContent) c2).getContent().equals(
				"<h1>Second test...</h1><p>This is another paragraph.</p>"));

		// Cleaning test
		cs.deleteContent("/test06");
		log.info("[[ STOP TEST  updateTextContent ]]");
	}

	@Test
	public void updateFolderLocation() throws ContentException,
			ContentIOException, ContentSecurityException {

		log.info("[[ START TEST  updateFolderLocation ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
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
		Assert.assertTrue(((Folder) f10).getLocation().equals("/test07/b"));

		// Cleaning test
		cs.deleteContent("/test07");
		log.info("[[ STOP TEST  updateFolderLocation ]]");
		Assert.assertTrue(true);
	}

	@Test
	public void updateFolderName() throws ContentException, ContentIOException,
			ContentSecurityException {

		log.info("[[ START TEST  updateFolderName ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
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
		Assert.assertTrue(f10.getId().equals("new-name"));

		// Cleaning test
		cs.deleteContent("/test08");
		log.info("[[ STOP TEST  updateFolderName ]]");
		Assert.assertTrue(true);
	}

	@Test
	public void updateBinaryContent() throws ContentIOException,
			ContentSecurityException, ContentException {

		log.info("[[ START TEST  updateBinaryContent ]]");
		InputStream pdf = getClass().getClassLoader().getResourceAsStream(
				"/GateIn-UserGuide-v3.5.pdf");
		InputStream jpg = getClass().getClassLoader().getResourceAsStream(
				"/wcm-whiteboard.jpg");
		InputStream pdf2 = getClass().getClassLoader().getResourceAsStream(
				"/jbossportletbridge.pdf");
		byte[] _pdf = toByteArray(pdf);
		byte[] _jpg = toByteArray(jpg);
		byte[] _pdf2 = toByteArray(pdf2);
		long sizePdf = _pdf.length;
		long sizeJpg = _jpg.length;
		long sizePdf2 = _pdf2.length;

		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		Content f1 = cs.createFolder("test09", "/");
		Content b1 = cs.createBinaryContent("gatein-userguide", "en",
				"/test09", "application/pdf", sizePdf,
				"GateIn-UserGuide-v3.5.pdf", new ByteArrayInputStream(_pdf));
		Content b2 = cs.createBinaryContent("wcm-whiteboard", "en", "/test09",
				"image/jpeg", sizeJpg, "wcm-whiteboard.jpg",
				new ByteArrayInputStream(_jpg));
		log.info(f1);
		log.info(b1);
		log.info(b2);
		b1 = cs.updateBinaryContent("/test09/gatein-userguide", "en",
				"application/pdf", sizePdf2, "jbossportletbridge.pdf",
				new ByteArrayInputStream(_pdf2));

		Assert.assertTrue(((BinaryContent) b1).getFileName().equals(
				"jbossportletbridge.pdf"));
		Assert.assertTrue(((BinaryContent) b1).getSize() == sizePdf2);
		Assert.assertTrue(((BinaryContent) b1).getContent() != null);

		// Cleaning test
		cs.deleteContent("/test09");
		log.info("[[ STOP TEST  updateBinaryContent ]]");
		Assert.assertTrue(true);
	}

	@Test
	public void deleteContent() throws ContentException, ContentIOException,
			ContentSecurityException {

		log.info("[[ START TEST  deleteContent ]]");
		ContentService cs = repos.createContentSession("sample", "default",
				"admin", "admin");
		Content c1 = cs.createTextContent("test10", "es", "/",
				"<h1>Primer test...</h1><p>Este es un párrafo.</p>");
		Content c2 = cs.createTextContent("test10", "en", "/",
				"<h1>First test...</h1><p>This is a paragraph</p>");
		Content c3 = cs.createTextContent("test10", "fr", "/",
				"<h1>First test...</h1><p>Ceci est un paragraphe</p>");
		Content c4 = cs.createTextContent("test10", "de", "/",
				"<h1>Erster Test...</h1><p>Dies ist ein Absatz</p>");
		log.info(c1);
		log.info(c2);
		log.info(c3);
		log.info(c4);

		log.info(cs.deleteContent("/test10", "es"));
		log.info(cs.deleteContent("/test10", "en"));
		log.info(cs.deleteContent("/test10", "fr"));

		List<String> locales = cs.getContentLocales("/test10");
		Assert.assertTrue(locales.size() == 1);

		// Cleaning test
		cs.deleteContent("/test10");
		log.info("[[ STOP TEST  deleteContent ]]");
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

	private void print(Content c, String tmpFolder) {

		log.info("--> " + c.getLocation() + " - " + c.getId());
		if (c instanceof Folder) {
			List<Content> children = ((Folder) c).getChildren();
			for (Content _c : children)
				print(_c, tmpFolder);
		}
		if (c instanceof TextContent) {
			TextContent t = (TextContent) c;
			log.info("Text: " + t.getContent());
		}
		if (c instanceof BinaryContent) {
			BinaryContent b = (BinaryContent) c;
			String filename = b.getFileName();
			log.info(" Writting " + filename);
			inputStreamToFile(b.getContent(), filename, tmpFolder);
		}
		log.info("<-- " + c.getLocation() + " - " + c.getId());

	}

	private void inputStreamToFile(InputStream entrada, String file,
			String tmpFolder) {
		try {
			File f = new File(tmpFolder + "/" + file);
			OutputStream salida = new FileOutputStream(f);
			byte[] buf = new byte[16384];
			int len;
			while ((len = entrada.read(buf)) > 0) {
				salida.write(buf, 0, len);
			}
			salida.close();
			entrada.close();
		} catch (IOException e) {
			log.error("Error creating file: " + e.getMessage());
		}
	}

}