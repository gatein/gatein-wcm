package org.gatein.wcm.impl.services.commands;

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

public class DeleteCommand {

    private static final Logger log = Logger.getLogger(DeleteCommand.class);

    Session jcrSession = null;
    User logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public DeleteCommand(Session session, User user) throws ContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WcmContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     *
     * Removes content from a specified location. <br>
     * All locales and versions are removed. <br>
     *
     */
    public String deleteContent(String location) throws ContentException, ContentIOException, ContentSecurityException {

        log.debug("deleteContent()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        if ("/".equals(location))
            throw new ContentException("Root location cannot be deteled by API");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for deleteContet() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        // Delete a node
        try {
            String parent = jcr.deleteNode(location);
            return parent;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     * Deletes content from a specified location. <br>
     *
     */
    public String deleteContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {

        log.debug("deleteContent()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        if ("/".equals(location))
            throw new ContentException("Root location cannot be deteled by API");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location, locale))
            throw new ContentException("Location: " + location + " with locale: " + locale
                    + " doesn't exist for deleteContent() operation. ");

        // If we try to delete a folder we invoke the generic deleteContent() method
        if (!jcr.checkLocaleContent(location)) {
            return deleteContent(location);
        }

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        // Delete a node
        try {
            String parent = jcr.deleteNode(location, locale);
            return parent;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     *
     * Deletes a Category from repository.
     *
     */
    public void deleteCategory(String categoryLocation) throws ContentException, ContentIOException, ContentSecurityException {
        log.debug("deleteCategory()");

        // Check null parameters
        if (categoryLocation == null || "".equals(categoryLocation)) {
            throw new ContentException("Parameter categoryLocation cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        if ("/".equals(categoryLocation))
            throw new ContentException("Cannot delete root categories");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryLocation))
            throw new ContentException("Location: " + categoryLocation + " doesn't exist for deleteCategory() operation. ");

        if (jcr.checkCategoryReferences(categoryLocation))
            throw new ContentException("Category in location: " + categoryLocation + " has references.");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
                    + "/__categories");

        try {
            jcr.deleteCategory("/__categories" + categoryLocation);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     *
     * Deletes a Category from repository.
     *
     */
    public Category deleteCategory(String categoryLocation, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        log.debug("deleteCategory()");

        // Check null parameters
        if (categoryLocation == null || "".equals(categoryLocation)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        if ("/".equals(categoryLocation))
            throw new ContentException("Cannot delete root categories");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryLocation, locale))
            throw new ContentException("Location: " + categoryLocation + " and locale " + locale
                    + " doesn't exist for addContentCategory() operation. ");

        if (jcr.checkCategoryReferences(categoryLocation))
            throw new ContentException("Category in location: " + categoryLocation + " has references.");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
                    + "/__categories");

        try {
            jcr.deleteCategory("/__categories" + categoryLocation, locale);
            String parent = jcr.parent(categoryLocation);
            if ("/".contains(parent))
                return null;
            return factory.getCategory(jcr.parent(categoryLocation), locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Removes a comment under the specified Content location. <br>
     *
     */
    public Content deleteContentComment(String location, String locale, String idComment) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("deleteContentComment()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (idComment == null || "".equals(idComment)) {
            throw new ContentException("Parameter comment cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for deleteContentComment() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        try {
            jcr.deleteContentComment(location, idComment);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Deletes a property in the form KEY/VALUE to a Content. <br>
     * Properties are shared between locales of same Content. <br>
     *
     */
    public Content deleteContentProperty(String location, String locale, String name) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("deleteContentProperty()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new ContentException("Parameter name cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for deleteContentComment() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        try {
            jcr.deleteContentProperty(location, name);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    public Content deleteContentACE(String location, String locale, String name) throws ContentException, ContentIOException,
            ContentSecurityException {

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new ContentException("Parameter name cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for deleteContentComment() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not COMMENTS rights in location: "
                    + location);

        try {
            jcr.deleteContentACE(location, name);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

}
