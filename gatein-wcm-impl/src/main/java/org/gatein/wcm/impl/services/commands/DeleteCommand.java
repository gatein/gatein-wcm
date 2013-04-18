package org.gatein.wcm.impl.services.commands;

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

public class DeleteCommand {

    private static final Logger log = Logger.getLogger(DeleteCommand.class);

    Session jcrSession = null;
    WcmUser logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public DeleteCommand(Session session, WcmUser user) throws WcmContentIOException {
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
    public String deleteContent(String location) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        log.debug("deleteContent()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(location))
            throw new WcmContentException("Root location cannot be deteled by API");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for deleteContet() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
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
     * @see {@link WcmContentService#deleteContent(String, String)}
     */
    public String deleteContent(String location, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        log.debug("deleteContent()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(location))
            throw new WcmContentException("Root location cannot be deteled by API");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location, locale))
            throw new WcmContentException("Location: " + location + " with locale: " + locale
                    + " doesn't exist for deleteContent() operation. ");

        // If we try to delete a folder we invoke the generic deleteContent() method
        if (!jcr.checkLocaleContent(location)) {
            return deleteContent(location);
        }

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
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
     * @see {@link WcmContentService#deleteCategory(String)}
     */
    public void deleteCategory(String categoryPath) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {
        log.debug("deleteCategory()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WcmContentException("Parameter categoryLocation cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            throw new WcmContentException("Cannot delete root categories");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryPath))
            throw new WcmContentException("Location: " + categoryPath + " doesn't exist for deleteCategory() operation. ");

        if (jcr.checkCategoryReferences(categoryPath))
            throw new WcmContentException("Category in location: " + categoryPath + " has references.");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
                    + "/__categories");

        try {
            jcr.deleteCategory("/__categories" + categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     *
     * Deletes a Category from repository.
     *
     */
    public WcmCategory deleteCategory(String categoryLocation, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        log.debug("deleteCategory()");

        // Check null parameters
        if (categoryLocation == null || "".equals(categoryLocation)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(categoryLocation))
            throw new WcmContentException("Cannot delete root categories");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryLocation, locale))
            throw new WcmContentException("Location: " + categoryLocation + " and locale " + locale
                    + " doesn't exist for addContentCategory() operation. ");

        if (jcr.checkCategoryReferences(categoryLocation))
            throw new WcmContentException("Category in location: " + categoryLocation + " has references.");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
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
    public WcmObject deleteContentComment(String location, String locale, String idComment) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        log.debug("deleteContentComment()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (idComment == null || "".equals(idComment)) {
            throw new WcmContentException("Parameter comment cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for deleteContentComment() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
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
    public WcmObject deleteContentProperty(String location, String locale, String name) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        log.debug("deleteContentProperty()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new WcmContentException("Parameter name cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for deleteContentComment() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        try {
            jcr.deleteContentProperty(location, locale, name);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#deleteContentAce(String, String, String)}
     */
    public WcmObject deleteContentAce(String path, String locale, String name) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new WcmContentException("Parameter name cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for deleteContentComment() operation. ");

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not COMMENTS rights in location: "
                    + path);

        try {
            jcr.deleteContentAce(path, name);
            return factory.getContent(path, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

}
