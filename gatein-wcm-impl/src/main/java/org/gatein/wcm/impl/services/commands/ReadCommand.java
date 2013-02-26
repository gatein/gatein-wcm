package org.gatein.wcm.impl.services.commands;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WcmContentFactory;
import org.jboss.logging.Logger;

public class ReadCommand {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.commands");

    Session jcrSession = null;
    User logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public ReadCommand(Session session, User user) throws ContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WcmContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     *
     * Retrieves a list of content from a specified location. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @return List of content under specified location.
     * @throws ContentException if content doesn't exist in the location.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to access content under specified location.
     */
    public Content getContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        log.debug("getContent()");

        checkNullParameters(location, locale);

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createFolder() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                    + location);

        // Retrieving Content
        try {
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    private void checkNullParameters(String location, String locale) throws ContentException {
        if (locale == null || "".equals(locale)) {
            new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            new ContentException("Parameter location cannot be null or empty");
        }
    }

    /**
     *
     * Retrieves a list of locales available for a specified content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return List of locales.
     * @throws ContentException if content doesn't exist in the location.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to access content under specified location.
     */
    public List<String> getContentLocales(String location) throws ContentException, ContentIOException,
            ContentSecurityException {
        log.debug("getContentLocales()");

        checkNullParameters(location);

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createFolder() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                    + location);

        if (!jcr.checkLocaleContent(location))
            throw new ContentException("Location: " + location + " is not a TextContent or BinaryContent");

        try {
            return jcr.getLocales(location);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    private void checkNullParameters(String location) throws ContentException {
        if (location == null || "".equals(location)) {
            new ContentException("Parameter location cannot be null or empty");
        }
    }

    public List<Category> getCategories(String categoryLocation, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {

        log.debug("getCategories()");

        checkNullParameters(categoryLocation, locale);

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        if ("/".equals(categoryLocation))
            categoryLocation = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryLocation))
            throw new ContentException("Location: " + categoryLocation
                    + " doesn't exist for updateCategoryLocation() operation. ");

        try {
            return factory.getCategories("/__categories" + categoryLocation, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    public List<Content> getContent(List<Category> categories, String location, String locale) throws ContentException,
            ContentIOException, ContentSecurityException {

        log.debug("getContent()");

        checkNullParameters(categories, location, locale);

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location
                    + " doesn't exist for getContent() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not READ rights in location: "
                    + location);
        try {
            ArrayList<Content> result = new ArrayList<Content>();
            for (Category c : categories) {
                factory.getCategoryContent(c, location, locale, result);
            }
            return result;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    private void checkNullParameters(List<Category> categories, String location, String locale) throws ContentException {
        if (categories == null || categories.size() == 0) {
            new ContentException("Parameter categories cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            new ContentException("Parameter location cannot be null or empty");
        }
    }

}
