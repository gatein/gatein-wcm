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

import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet("/test/disconnection")
public class Disconnection extends HttpServlet {
    private static final long serialVersionUID = -4504050896299011926L;
    private static final Logger log = Logger.getLogger(Disconnection.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Connect form
        out.println("<html><head><title>GateIn WCM: DISCONNECT test</title></head><body>");

        Connect c = checkConnection(req);
        WcmContentService cs = (WcmContentService)req.getSession().getAttribute("cs");
        if (c != null && cs != null && !c.isConnected()) {
            try {
                cs.closeSession();
                req.getSession().setAttribute("connection", null);
                req.getSession().setAttribute("cs", null);
            } catch (WcmContentIOException e) {
                log.error(e.getMessage());
                out.println(e.getMessage());
                e.printStackTrace();
            }
            out.println("<p>Disconnecting... Ok</p>");
        } else {
            out.println("<p>Session is disconnected</p>");
        }

        req.getSession().invalidate();
        out.println("<p><a href=\"connection\">connection</a> <a href=\"folder\">folder</a> <a href=\"text\">text</a> <a href=\"binary\">binary</a> <a href=\"disconnection\">disconnection</a></p>");
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
