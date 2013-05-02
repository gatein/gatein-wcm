package org.gatein.wcm.ui.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMRepositoryService;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet(value="/res")
public class WcmResource extends HttpServlet {
    private static final long serialVersionUID = -6118383950661824863L;

    private static final Logger log = Logger.getLogger(WcmResource.class);

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WCMRepositoryService repos;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = (String)req.getAttribute("url");
        if (url != null && url.length() > "/res".length()) {
            String locale = req.getLocale().getLanguage();
            String param = req.getParameter("l");
            if (param != null && !"".equals(param))
                locale = param;
            String path = url.substring( url.indexOf("/res") + "/res".length());
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
            log.info("/res without arguments");
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
            WCMContentService cs = repos.createContentSession(c.getUser(), c.getPassword());
            WCMObject content = cs.getContent(path); // TODO to review
            if (content instanceof WCMBinaryDocument) {
                WCMBinaryDocument b = (WCMBinaryDocument)content;
                resp.setHeader("Content-Type", b.getMimeType() );
                resp.setHeader("Content-Length", String.valueOf(b.getSize()));
                resp.setHeader("Content-Disposition", "inline; filename=\"" + b.getFileName() + "\"");
                resp.setContentType(b.getMimeType());
                resp.setStatus(HttpServletResponse.SC_OK);

                byte[] buffer = new byte[16384];
                InputStream in = b.getContent();
                BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
                for (int length = 0; (length = in.read(buffer)) > 0;) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.flush();
                out.close();
            } else {
                log.warn("Content in path: " + path + " and locale: " + locale + " is not a resource");
            }
        } catch(WCMContentException e) {
            log.info("Cannot get root content from " + c.getRepository() + "/" + c.getWorkspace() + ", path: " + path + " and locale: " + locale + ". Msg: " + e.getMessage());
        } catch (WCMContentIOException e) {
            log.error(e.getMessage(), e);
        } catch (WCMContentSecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}