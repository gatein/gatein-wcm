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

@WebServlet("/test/folder")
public class CreateFolder extends HttpServlet {
    private static final long serialVersionUID = -4504050896299011926L;
    private static final Logger log = Logger.getLogger(CreateFolder.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CREATE FOLDER test</title></head><body>");
        Connect c = checkConnection(req);
        if (c == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {
            out.println("<form action=\"folder\" method=\"post\" id=\"folderForm\" >");
            out.println("<p>Id new folder: <input type=\"text\" name=\"id\" id=\"id\" /></p>");
            out.println("<p>Location: <input type=\"text\" name=\"location\" id=\"location\" /></p>");
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
        String location = req.getParameter("location");

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CREATE FOLDER test</title></head><body>");

        Connect c = checkConnection(req);
        WCMContentService cs = (WCMContentService)req.getSession().getAttribute("cs");
        if (c == null || cs == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {
            WCMObject _c = null;
            try {
                _c = cs.createFolder(id, location);
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
                out.println("<p>Create folder at : " + ("/".equals(_c.getParentPath())?"":_c.getParentPath()) + "/" + _c.getId() + "</p>");
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
