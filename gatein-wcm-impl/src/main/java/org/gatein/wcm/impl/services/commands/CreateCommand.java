package org.gatein.wcm.impl.services.commands;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

public class CreateCommand {

    private static final Logger log = Logger.getLogger("org.gatein.wcm");

    Session jcrSession = null;
    User logged = null;
    WCMContentFactory factory = null;
    JcrMappings jcr = null;

    private final String MARK = "__";

    public CreateCommand (Session session, User user)
            throws ContentIOException
        {
            jcrSession = session;
            logged = user;
            jcr = new JcrMappings(jcrSession, logged);
            factory = new WCMContentFactory(jcr, logged);
            jcr.setFactory( factory );
        }

    public Content createTextContent(String id, String locale, String location, String html, String encoding)
            throws ContentException, ContentIOException, ContentSecurityException {

        // Check if the current JCR Session is valid
        if ( ! jcr.checkSession() )
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if ( ! jcr.checkLocation(location) )
            throw new ContentException("Location: " + location + " doesn't exist for createTextContent() operation. ");

        // Check if there is a content with same id in the specified location
        if ( jcr.checkIdExists(location, id, locale) )
            throw new ContentException("Location: " + location + " Locale: " + locale + " id: " + id + " exists for createTextContent() operation. ");

        // Check if user has rights to access
        if ( ! jcr.checkUserACL( location ))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: " + location);

        try {

            Value content = jcr.jcrValue(html, encoding);

            if (jcr.checkIdExists(location, id)) {
                // New locale
                Node n = jcrSession.getNode(location + "/" + id);
                n.addNode(MARK + locale, "nt:folder")
                    .addNode(MARK + id, "nt:file")
                    .addNode("jcr:content", "nt:resource")
                    .setProperty("jcr:data", content);

            } else {
                Node n = jcrSession.getNode(location);
                n.addNode(id, "nt:folder")
                    .addNode(MARK + locale, "nt:folder")
                    .addNode(MARK + id, "nt:file")
                    .addNode("jcr:content", "nt:resource")
                    .setProperty("jcr:data", content);
            }

            // Adding versioning on the locale
            jcrSession.getNode(location + "/" + id + "/" + MARK + locale + "/" + MARK + id)
                .addMixin("mix:versionable");

            // Common properties
            Node n = jcrSession.getNode(location + "/" + id);
            n.addMixin("mix:created");
            n.addMixin("mix:lastModified");

            // Adding WCM ACL representation
            n.addNode(MARK + "acl", "nt:folder");


        } catch (RepositoryException e) {
            jcr.checkJCRException( e );
        }

        // TODO Auto-generated method stub
        return null;
    }


}
