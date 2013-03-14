package org.gatein.wcm.ui.tests.performance;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet("/test/binary")
public class CreateBinary extends HttpServlet {
    private static final long serialVersionUID = -4504050896299011926L;
    private static final Logger log = Logger.getLogger("org.gatein.wcm.ui.tests.performance");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CREATE BINARY test</title></head><body>");
        Connect c = checkConnection(req);
        if (c == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {
            out.println("<form action=\"binary\" method=\"post\" enctype=\"multipart/form-data\" id=\"binaryForm\" >");
            out.println("<p>Id new binary: <input type=\"text\" name=\"id\" id=\"id\" /></p>");
            out.println("<p>Locale: <input type=\"text\" name=\"locale\" id=\"locale\" /></p>");
            out.println("<p>Location: <input type=\"text\" name=\"location\" id=\"location\" /></p>");
            out.println("<p>File: <input type=\"file\" name=\"file\" id=\"file\" /></p>");
            out.println("<p><input type=\"submit\" value=\"Submit\" /></p>");
        }
        out.println(Common.MENU);
        out.println("</body></html>");
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CREATE BINARY test</title></head><body>");

        Connect c = checkConnection(req);
        ContentService cs = (ContentService)req.getSession().getAttribute("cs");
        if (c == null || cs == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {

            // Processing multipart/form-data
            int maxsize = 10485760; // 10 Mb max in memory per file, larger files will be stored on disk
            FileItemFactory factory = new DiskFileItemFactory(maxsize, new File("/tmp/gatein-wcm"));
            ServletFileUpload upload = new ServletFileUpload(factory);
            String id = null;
            String locale = null;
            String location = null;
            FileItem file = null;
            try {
                @SuppressWarnings("unchecked")
                List<FileItem> items = upload.parseRequest(req);
                for (FileItem f : items) {
                    if (f.isFormField()) {
                        String name = f.getFieldName();
                        String value = f.getString();
                        if ("id".equals(name)) id = value;
                        if ("locale".equals(name)) locale = value;
                        if ("location".equals(name)) location = value;
                        log.debug("name: " + name + " value: " + value);
                    } else {
                        String fieldName = f.getFieldName();
                        String fileName = f.getName();
                        String contentType = f.getContentType();
                        boolean isInMemory = f.isInMemory();
                        long sizeInBytes = f.getSize();
                        file = f;
                        log.debug("fieldName: " + fieldName + " fileName: " + fileName + " contentType: " + contentType + " isInMemory: " + isInMemory + " sizeInBytes: " + sizeInBytes);
                    }
                }
            } catch(FileUploadException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            }

            Content _c = null;
            try {
                if (id != null && locale != null && location != null && file != null)
                    _c = cs.createBinaryContent(id, locale, location, file.getContentType(), file.getSize(), file.getFieldName(), file.getInputStream());
            } catch (ContentException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            } catch (ContentIOException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            } catch (ContentSecurityException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            }
            if (_c != null)
                out.println("<p>Create binary at : " + ("/".equals(_c.getLocation())?"":_c.getLocation()) + "/" + _c.getId() + "</p>");
        }
        out.println(Common.MENU);
        out.println("</body></html>");

        out.flush();
        out.close();
    }

    @SuppressWarnings("rawtypes")
    private Connect checkConnection(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Enumeration names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Object o = session.getAttribute(name);
            if (o instanceof Connect) return (Connect)o;
        }
        return null;
    }

}
