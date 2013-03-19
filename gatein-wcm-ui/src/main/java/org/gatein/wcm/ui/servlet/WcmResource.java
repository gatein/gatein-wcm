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

import org.gatein.wcm.api.model.content.BinaryContent;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.ui.Connect;
import org.jboss.logging.Logger;

@WebServlet(value="/res")
public class WcmResource extends HttpServlet {
    private static final long serialVersionUID = -6118383950661824863L;

    private static final Logger log = Logger.getLogger(WcmResource.class);

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

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
            ContentService cs = repos.createContentSession(c.getRepository(), c.getWorkspace(), c.getUser(), c.getPassword());
            Content content = cs.getContent(path, locale);
            if (content instanceof BinaryContent) {
                BinaryContent b = (BinaryContent)content;
                resp.setHeader("Content-Type", b.getContentType() );
                resp.setHeader("Content-Length", String.valueOf(b.getSize()));
                resp.setHeader("Content-Disposition", "inline; filename=\"" + b.getFileName() + "\"");
                resp.setContentType(b.getContentType());
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
        } catch(ContentException e) {
            log.info("Cannot get root content from " + c.getRepository() + "/" + c.getWorkspace() + ", path: " + path + " and locale: " + locale + ". Msg: " + e.getMessage());
        } catch (ContentIOException e) {
            log.error(e.getMessage(), e);
        } catch (ContentSecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}