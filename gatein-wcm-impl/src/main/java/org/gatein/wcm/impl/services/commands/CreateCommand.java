package org.gatein.wcm.impl.services.commands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.gatein.wcm.api.model.content.WcmBinaryObject;
import org.gatein.wcm.api.model.content.WcmFolder;
import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmTextObject;
import org.gatein.wcm.api.model.metadata.WcmCategory;
import org.gatein.wcm.api.model.security.WcmAce;
import org.gatein.wcm.api.model.security.WcmPrincipal;
import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.model.security.WcmAce.PermissionType;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WcmContentFactory;
import org.jboss.logging.Logger;

public class CreateCommand {

    private static final Logger log = Logger.getLogger(CreateCommand.class);

    Session jcrSession = null;
    WcmUser logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public CreateCommand(Session session, WcmUser user) throws WcmContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WcmContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     * @see {@link WcmContentService#createTextContent(String, String, String, String)}
     */
    public WcmTextObject createTextContent(String id, String locale, String location, String html)
            throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        log.debug("createTextContent()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WcmContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (html == null || "".equals(html)) {
            throw new WcmContentException("Parameter html cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for createTextContent() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists(location, id, locale))
            throw new WcmContentException("Location: " + location + " Locale: " + locale + " id: " + id
                    + " exists for createTextContent() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        // Creating new Node
        try {

            Value content = jcr.jcrValue(html);
            jcr.createTextNode(id, locale, location, content);

            // Return the model with the content
            return factory.createTextContent(id, locale, location, html);

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#createFolder(String, String)}
     */
    public WcmFolder createFolder(String id, String path) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        log.debug("createFolder()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WcmContentException("Parameter id cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for createFolder() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists(path, id))
            throw new WcmContentException("Location: " + path + " Id: " + id + " exists for createFolder() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Creating new folder
        try {
            jcr.createFolder(id, path);
            return factory.createFolder(id, path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#createBinaryContent(String, String, String, String, long, String, InputStream)}
     */
    public WcmBinaryObject createBinaryContent(String id, String locale, String path, String contentType, long size,
            String fileName, InputStream content) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        log.debug("createBinaryContent()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WcmContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }
        if (contentType == null || "".endsWith(contentType)) {
            throw new WcmContentException("Parameter contentType cannot be null or empty");
        }
        if (size == 0) {
            throw new WcmContentException("Parameter size cannot be null or 0");
        }
        if (fileName == null || "".endsWith(fileName)) {
            throw new WcmContentException("Parameter fileName cannot be null or empty");
        }
        if (content == null) {
            throw new WcmContentException("Parameter content in InputStream cannot be null");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for createBinaryContent() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists(path, id, locale))
            throw new WcmContentException("Location: " + path + " Locale: " + locale + " id: " + id
                    + " exists for createTextContent() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Creating new Node
        try {

            byte[] _content = jcr.toByteArray(content);

            jcr.createBinaryNode(id, locale, path, contentType, size, fileName, new ByteArrayInputStream(_content));

            return factory.createBinaryContent(id, locale, path, contentType, size, fileName, new ByteArrayInputStream(
                    _content));

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#createCategory(String, String, String, String)}
     */
    public WcmCategory createCategory(String id, String locale, String description, String categoryPath)
            throws WcmContentException, WcmContentIOException, WcmContentSecurityException {
        log.debug("createCategory()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WcmContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (description == null || "".equals(description)) {
            throw new WcmContentException("Parameter description cannot be null or empty");
        }
        if (categoryPath == null || "".endsWith(categoryPath)) {
            throw new WcmContentException("Parameter categoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            categoryPath = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryPath))
            throw new WcmContentException("Location: " + categoryPath + " doesn't exist for createCategory() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists("/__categories" + categoryPath, id, locale))
            throw new WcmContentException("Location: " + categoryPath + " Locale: " + locale + " id: " + id
                    + " exists for createCategory() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
                    + "/__categories");

        // Creating new Category
        try {
            jcr.createCategory(id, locale, "/__categories" + categoryPath, description);
            return factory.getCategory("/__categories" + categoryPath + "/" + id, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#createContentComment(String, String, String)}
     */
    public WcmObject createContentComment(String path, String locale, String comment) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        log.debug("createContentComment()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (comment == null || "".equals(comment)) {
            throw new WcmContentException("Parameter comment cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for createContentComment() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserCommentsACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not COMMENTS rights in location: "
                    + path);

        try {
            jcr.createContentComment(path, comment);
            return factory.getContent(path, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#createContentProperty(String, String, String, String)}
     */
    public WcmObject createContentProperty(String path, String locale, String name, String value) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {

        log.debug("createContentComment()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new WcmContentException("Parameter name cannot be null or empty");
        }
        if (value == null || "".equals(value)) {
            throw new WcmContentException("Parameter value cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for createContentProperty() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        try {
            jcr.createContentProperty(path, locale, name, value);
            return factory.getContent(path, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#createContentAce(String, String, String, org.gatein.wcm.api.model.security.WcmPrincipal.PrincipalType, PermissionType)}
     */
    public WcmObject createContentAce(String path, String locale, String name, WcmPrincipal.PrincipalType principal, WcmAce.PermissionType permission)
            throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        log.debug("createContentACE()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new WcmContentException("Parameter name cannot be null or empty");
        }
        if (principal == null || "".equals(principal)) {
            throw new WcmContentException("Parameter principal cannot be null or empty");
        }
        if (permission == null || "".equals(permission)) {
            throw new WcmContentException("Parameter permission cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for createContentProperty() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        try {
            jcr.createContentAce(path, name, principal, permission);
            return factory.getContent(path, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }


}