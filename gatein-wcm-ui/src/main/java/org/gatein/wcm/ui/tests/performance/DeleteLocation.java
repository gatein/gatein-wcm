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

import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet("/test/delete")
public class DeleteLocation extends HttpServlet {
    private static final long serialVersionUID = -4504050896299011926L;
    private static final Logger log = Logger.getLogger("org.gatein.wcm.ui.tests.performance");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: DELETE LOCATION test</title></head><body>");
        Connect c = checkConnection(req);
        if (c == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {
            out.println("<form action=\"delete\" method=\"post\" id=\"folderForm\" >");
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

        String location = req.getParameter("location");

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: DELETE LOCATION test</title></head><body>");

        Connect c = checkConnection(req);
        ContentService cs = (ContentService)req.getSession().getAttribute("cs");
        if (c == null || cs == null || !c.isConnected()) {
            out.println("<p>WARNING: it's needed to be connected to use this test</p>");
        } else {
            String parent = null;
            try {
                parent = cs.deleteContent(location);
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
            if (parent != null)
                out.println("<p>Delete location with parent : " + location + ". Parent: " + parent + "</p>");
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
