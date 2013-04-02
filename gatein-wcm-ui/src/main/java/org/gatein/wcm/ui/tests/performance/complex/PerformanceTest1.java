package org.gatein.wcm.ui.tests.performance.complex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.jboss.logging.Logger;

@WebServlet("/test/performancetest1")
public class PerformanceTest1 extends HttpServlet {
    private static final long serialVersionUID = -4504050896299011926L;
    private static final Logger log = Logger.getLogger(PerformanceTest1.class);

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        // Connect form
        out.println("<html><head><title>GateIn WCM: Performance Test 1</title></head><body>");

        String path = System.getProperty("org.gatein.wcm.path.pdf");

        if (path == null) {
            out.println("Path to PDF is null. Skipping TEST...");
            log.info("System property org.gatein.wcm.path.pdf is null. Skipping test.");
            out.println("</body></html>");
            out.flush();
            out.close();
            return;
        }

        out.println("Path to PDF: " + path + " <br>");

        // Sample data
        String repository = "sample";
        String workspace = "default";
        String user = "admin";
        String password = "admin";

        String uuid = UUID.randomUUID().toString();
        int random = Math.round( (float)Math.random() * 99);

        // Performance test
        ContentService cs = null;
        try {
            //  0.- Connection
            cs = repos.createContentSession(repository, workspace, user, password);
            out.println("Connected to WCM repository <br>");

            //  1.- Create folder
            cs.createFolder("test_" + uuid, "/");
            out.println("Created test_" + uuid + " folder <br>");

            //  2.- Text content with locale en
            cs.createTextContent("text1", "en", "/test_" + uuid, "This is an example of text with UUID " + uuid + " and time " + new java.util.Date().toString() );
            out.println("Created text1 text content under en locale <br>");

            //  3.- Text content with locale es
            cs.createTextContent("text1", "es", "/test_" + uuid, "Este es un ejemplo de texto con UUID " + uuid + " y hora " + new java.util.Date().toString() );
            out.println("Created text1 text content under es locale <br>");

            //  4.- Binary content with locale en
            String fileName = "testpdf" + random + ".pdf";
            String fullPath = path + "/" + fileName;
            File f = new File(fullPath);
            FileInputStream fis = new FileInputStream(fullPath);
            cs.createBinaryContent("binary1", "en", "/test_" + uuid, "application/pdf", f.length(), fileName, fis);
            out.println("Created binary1 binary content under en locale <br>");


            //  5.- Binary content with locale es
            fis = new FileInputStream(fullPath); // Reset the InputStream to read it twice
            cs.createBinaryContent("binary1", "es", "/test_" + uuid, "application/pdf", f.length(), fileName, fis);
            out.println("Created binary1 binary content under es locale <br>");

            //  6.- Get content
            Content c1_es = cs.getContent("/test_" + uuid + "/text1", "es");
            out.println("Getting content " + c1_es.getId() + " <br>");

            Content c1_en = cs.getContent("/test_" + uuid + "/text1", "en");
            out.println("Getting content " + c1_en.getId() + " <br>");

            Content c2_es = cs.getContent("/test_" + uuid + "/binary1", "es");
            out.println("Getting content " + c2_es.getId() + " <br>");

            Content c2_en = cs.getContent("/test_" + uuid + "/binary1", "en");
            out.println("Getting content " + c2_en.getId() + " <br>");

            //  7.- Delete content
            cs.deleteContent("/test_" + uuid + "/text1", "es");
            cs.deleteContent("/test_" + uuid + "/text1", "en");
            cs.deleteContent("/test_" + uuid + "/binary1", "es");
            cs.deleteContent("/test_" + uuid + "/binary1", "en");
            cs.deleteContent("/test_" + uuid);

            //  8.- Disconnecting
            cs.closeSession();
            out.println("Closing session <br>");

        } catch (ContentException e) {
            log.error(e.getMessage());
            out.println(e.getMessage() + " <br>");
        } catch (ContentIOException e) {
            log.error(e.getMessage());
            out.println(e.getMessage() + " <br>");
        } catch (ContentSecurityException e) {
            log.error(e.getMessage());
            out.println(e.getMessage() + " <br>");
        }
        out.println("</body></html>");
        out.flush();
        out.close();
    }


}
