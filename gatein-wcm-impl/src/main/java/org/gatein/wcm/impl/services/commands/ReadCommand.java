package org.gatein.wcm.impl.services.commands;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.metadata.WcmCategory;
import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WcmContentFactory;
import org.jboss.logging.Logger;

public class ReadCommand {

    private static final Logger log = Logger.getLogger(ReadCommand.class);

    Session jcrSession = null;
    WcmUser logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public ReadCommand(Session session, WcmUser user) throws WcmContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WcmContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     * @see {@link WcmContentService#getContent(String, String)}
     */
    public WcmObject getContent(String path, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        log.debug("getContent()");

        // Check null parameters
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for getContent() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                    + path);

        // Retrieving Content
        try {
            return factory.getContent(path, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Retrieves a list of locales available for a specified content. <br>
     *
     */
    public List<String> getContentLocales(String location) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        log.debug("getContentLocales()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for getContentLocales() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                    + location);

        if (!jcr.checkLocaleContent(location))
            throw new WcmContentException("Location: " + location + " is not a TextContent or BinaryContent");

        try {
            return jcr.getLocales(location);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    public List<WcmCategory> getCategories(String categoryLocation, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        log.debug("getCategories()");

        // Check null parameters
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (categoryLocation == null || "".equals(categoryLocation)) {
            throw new WcmContentException("Parameter categoryLocation cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(categoryLocation))
            categoryLocation = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryLocation))
            throw new WcmContentException("Location: " + categoryLocation + " doesn't exist for getCategories() operation. ");

        try {
            return factory.getCategories("/__categories" + categoryLocation, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    public List<WcmObject> getContent(List<WcmCategory> categories, String location, String locale) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {

        log.debug("getContent()");

        // Check null parameters
        if (categories == null || categories.size() == 0) {
            throw new WcmContentException("Parameter categories cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for getContent() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                    + location);
        try {
            ArrayList<WcmObject> result = new ArrayList<WcmObject>();
            for (WcmCategory c : categories) {
                factory.getCategoryContent(c, location, locale, result);
            }
            return result;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    public List<String> getContentVersions(String location) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        log.debug("getContentVersions()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for getContentVersions() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                    + location);
        try {
            return jcr.jcrContentVersions(location);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#getContent(String, String, String)}
     */
   public WcmObject getContent(String path, String locale, String version) throws WcmContentException, WcmContentIOException,
           WcmContentSecurityException {
       log.debug("getContent()");

       // Check null parameters
       if (locale == null || "".equals(locale)) {
           throw new WcmContentException("Parameter locale cannot be null or empty");
       }
       if (path == null || "".equals(path)) {
           throw new WcmContentException("Parameter location cannot be null or empty");
       }
       if (version == null || "".equals(version)) {
           throw new WcmContentException("Parameter version cannot be null or empty");
       }

       // Check if the current JCR Session is valid
       if (!jcr.checkSession())
           throw new WcmContentIOException("JCR Session is null");

       // Check if the location specified exists in the JCR Repository/Workspace
       if (!jcr.checkLocation(path))
           throw new WcmContentException("Location: " + path + " doesn't exist for getContent() operation. ");

       // Check if user has rights to access
       if (!jcr.checkUserReadACL(path))
           throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                   + path);

       // Retrieving Content
       try {
           return factory.getContent(path, locale, version);
       } catch (RepositoryException e) {
           jcr.checkJCRException(e);
       }

       return null;
   }

}
