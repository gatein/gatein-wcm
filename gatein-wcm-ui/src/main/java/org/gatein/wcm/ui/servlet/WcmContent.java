package org.gatein.wcm.ui.servlet;

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

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmTextObject;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.WcmRepositoryService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet(value="/wcm")
public class WcmContent extends HttpServlet {
    private static final long serialVersionUID = -6118383950661824863L;

    private static final Logger log = Logger.getLogger(WcmContent.class);

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WcmRepositoryService repos;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = (String)req.getAttribute("url");
        if (url != null && url.length() > "/wcm".length()) {
            String locale = req.getLocale().getLanguage();
            String param = req.getParameter("l");
            if (param != null && !"".equals(param))
                locale = param;
            String path = url.substring( url.indexOf("/wcm") + "/wcm".length());
            path = path.replace("%20", " ");
            path = path.replace("+", " ");
            Connect c = checkConnection(req);
            if (c == null) return;
            if (!c.isConnected()) {
                log.warn("Accesing to resource in path " + path + " without connection established");
                return;
            }
            sendContent(c, path, locale, resp);
        } else {
            log.info("/wcm without arguments");
            return;
        }
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

    private void sendContent(Connect c, String path, String locale, HttpServletResponse resp) {
        try {
            if (repos == null) {
                log.error("Error accesing to repository");
                return;
            }
            WcmContentService cs = repos.createContentSession(c.getRepository(), c.getWorkspace(), c.getUser(), c.getPassword());
            WcmObject content = cs.getContent(path, locale);
            if (content instanceof WcmTextObject) {
                WcmTextObject t = (WcmTextObject)content;
                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();
                out.print(t.getContent());
                out.flush();
                out.close();
            } else {
                log.warn("Content in path: " + path + " and locale: " + locale + " is not a resource");
            }
        } catch(WcmContentException e) {
            log.info("Cannot get root content from " + c.getRepository() + "/" + c.getWorkspace() + ", path: " + path + " and locale: " + locale + ". Msg: " + e.getMessage());
        } catch (WcmContentIOException e) {
            log.error(e.getMessage(), e);
        } catch (WcmContentSecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}