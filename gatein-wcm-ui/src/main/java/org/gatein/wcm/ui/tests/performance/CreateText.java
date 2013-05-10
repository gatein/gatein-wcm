package org.gatein.wcm.ui.tests.performance;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet("/test/text")
public class CreateText extends HttpServlet {
    private static final long serialVersionUID = -4504050896299011926L;
    private static final Logger log = Logger.getLogger(CreateText.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CREATE TEXT test</title></head><body>");
        Connect c = checkConnection(req);
        if (c == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {
            out.println("<form action=\"text\" method=\"post\" id=\"folderForm\" >");
            out.println("<p>Id new text: <input type=\"text\" name=\"id\" id=\"id\" /></p>");
            out.println("<p>Locale: <input type=\"text\" name=\"locale\" id=\"locale\" /></p>");
            out.println("<p>Location: <input type=\"text\" name=\"location\" id=\"location\" /></p>");
            out.println("<p>Text: <textarea name=\"text\" id=\"text\"></textarea></p>");
            out.println("<p><input type=\"submit\" value=\"Submit\" /></p>");
        }
        out.println(Common.MENU);
        out.println("</body></html>");
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String id = req.getParameter("id");
        String locale = req.getParameter("locale");
        String location = req.getParameter("location");
        String text = req.getParameter("text");
        String mimeType = "text/html";

        resp.setContentType(mimeType);
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CREATE TEXT test</title></head><body>");

        Connect c = checkConnection(req);
        WCMContentService cs = (WCMContentService)req.getSession().getAttribute("cs");
        if (c == null || cs == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {
            WCMObject _c = null;
            try {
                _c = cs.createTextDocument(id, locale, location, mimeType, text);
            } catch (WCMContentException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            } catch (WCMContentIOException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            } catch (WCMContentSecurityException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            }
            if (_c != null)
                out.println("<p>Create text at : " + ("/".equals(_c.getParentPath())?"":_c.getParentPath()) + "/" + _c.getId() + "</p>");
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