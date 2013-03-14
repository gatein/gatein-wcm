package org.gatein.wcm.ui.tests.performance;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet("/test/connection")
public class Connection extends HttpServlet {
    private static final long serialVersionUID = -4504050896299011926L;
    private static final Logger log = Logger.getLogger("org.gatein.wcm.ui.tests.performance");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CONNECT test</title></head><body>");
        out.println("<form action=\"connection\" method=\"post\" id=\"connectForm\" >");
        out.println("<p>Repository: <input type=\"text\" name=\"repository\" id=\"repository\" /></p>");
        out.println("<p>Workspace: <input type=\"text\" name=\"workspace\" id=\"workspace\" /></p>");
        out.println("<p>User: <input type=\"text\" name=\"user\" id=\"user\" /></p>");
        out.println("<p>Password: <input type=\"password\" name=\"password\" id=\"password\" /></p>");
        out.println("<p><input type=\"submit\" value=\"Submit\" /></p>");
        out.println(Common.MENU);
        out.println("</body></html>");

        out.flush();
        out.close();
    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String repository = req.getParameter("repository");
        String workspace = req.getParameter("workspace");
        String user = req.getParameter("user");
        String password = req.getParameter("password");

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: CONNECT test</title></head><body>");

        Connect c = checkConnection(req);
        if (c == null || !c.isConnected()) {
            c = new Connect();
            c.setRepository(repository);
            c.setWorkspace(workspace);
            c.setUser(user);
            c.setPassword(password);
            ContentService cs = null;
            try {
                cs = repos.createContentSession(c.getRepository(), c.getWorkspace(), c.getUser(), c.getPassword());
                c.setConnected(true);
                req.getSession().setAttribute("connection", c);
                req.getSession().setAttribute("cs", cs);
                out.println("<p>Connected to " + c.getRepository() + "/" + c.getWorkspace() + "  " + c.getUser() + "/" + c.getPassword() + " </p>");
            } catch (ContentIOException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            } catch (ContentSecurityException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            }
        } else
            out.println("<p>Session is connected to " + c.getRepository() + "/" + c.getWorkspace() + "  " + c.getUser() + "/" + c.getPassword() + " </p>");
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
