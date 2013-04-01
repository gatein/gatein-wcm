package org.gatein.wcm.impl.services.commands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.security.ACE;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WcmContentFactory;
import org.jboss.logging.Logger;

public class CreateCommand {

    private static final Logger log = Logger.getLogger(CreateCommand.class);

    Session jcrSession = null;
    User logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public CreateCommand(Session session, User user) throws ContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WcmContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     *
     * Creates a new text content in the default repository.
     *
     */
    public Content createTextContent(String id, String locale, String location, String html)
            throws ContentException, ContentIOException, ContentSecurityException {

        log.debug("createTextContent()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new ContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (html == null || "".equals(html)) {
            throw new ContentException("Parameter html cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createTextContent() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists(location, id, locale))
            throw new ContentException("Location: " + location + " Locale: " + locale + " id: " + id
                    + " exists for createTextContent() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
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
     *
     * Creates a new folder in the default repository.
     *
     * @param id - Key under which to store the folder.
     * @param locale - Locale under content is stored.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID UPDATE: in folder we don't use locale in the
     *        implementation.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content createFolder(String id, String location) throws ContentException, ContentIOException,
            ContentSecurityException {

        log.debug("createFolder()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new ContentException("Parameter id cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createFolder() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists(location, id))
            throw new ContentException("Location: " + location + " Id: " + id + " exists for createFolder() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        // Creating new folder
        try {
            jcr.createFolder(id, location);
            return factory.createFolder(id, location);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Creates new file resource in the default repository.
     *
     * @param id - Key under which to store the resource.
     * @param locale - Locale under content is stored.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param contentType - ContentType's file.
     * @param size - Size's file.
     * @param fileName - Name's file.
     * @param content - Source of the file.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content createBinaryContent(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content) throws ContentException, ContentIOException, ContentSecurityException {

        log.debug("createBinaryContent()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new ContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (contentType == null || "".endsWith(contentType)) {
            throw new ContentException("Parameter contentType cannot be null or empty");
        }
        if (size == null || size == 0) {
            throw new ContentException("Parameter size cannot be null or 0");
        }
        if (fileName == null || "".endsWith(fileName)) {
            throw new ContentException("Parameter fileName cannot be null or empty");
        }
        if (content == null) {
            throw new ContentException("Parameter content in InputStream cannot be null");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createBinaryContent() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists(location, id, locale))
            throw new ContentException("Location: " + location + " Locale: " + locale + " id: " + id
                    + " exists for createTextContent() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        // Creating new Node
        try {

            byte[] _content = jcr.toByteArray(content);

            jcr.createBinaryNode(id, locale, location, contentType, size, fileName, new ByteArrayInputStream(_content));

            return factory.createBinaryContent(id, locale, location, contentType, size, fileName, new ByteArrayInputStream(
                    _content));

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Creates new Category in the repository. <br>
     * Categories can be organized in a hierarchical tree of categories parents and children.
     *
     * @param id - Category id.
     * @param locale - Locale of category.
     * @param description - Category description.
     * @param categoryLocation - Location where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Category created (if ok), null (if error).
     * @throws ContentException if the id exists (categories are not versionable items).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public Category createCategory(String id, String locale, String description, String categoryLocation)
            throws ContentException, ContentIOException, ContentSecurityException {
        log.debug("createCategory()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new ContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (description == null || "".equals(description)) {
            throw new ContentException("Parameter description cannot be null or empty");
        }
        if (categoryLocation == null || "".endsWith(categoryLocation)) {
            throw new ContentException("Parameter categoryLocation cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        if ("/".equals(categoryLocation))
            categoryLocation = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryLocation))
            throw new ContentException("Location: " + categoryLocation + " doesn't exist for createCategory() operation. ");

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists("/__categories" + categoryLocation, id, locale))
            throw new ContentException("Location: " + categoryLocation + " Locale: " + locale + " id: " + id
                    + " exists for createCategory() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
                    + "/__categories");

        // Creating new Category
        try {
            jcr.createCategory(id, locale, "/__categories" + categoryLocation, description);
            return factory.getCategory("/__categories" + categoryLocation + "/" + id, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Creates a comment under the specified Content location. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to add comment
     * @param comment - Comment to add
     * @return Content with comment updated.
     * @throws ContentException if content doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create comments.
     */
    public Content createContentComment(String location, String locale, String comment) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("createContentComment()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (comment == null || "".equals(comment)) {
            throw new ContentException("Parameter comment cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createContentComment() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserCommentsACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not COMMENTS rights in location: "
                    + location);

        try {
            jcr.createContentComment(location, comment);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Creates a property in the form KEY/VALUE to a Content. <br>
     * Properties are shared between locales of same Content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @param value - Value
     * @return Content (default locale) with properties updated.
     * @throws ContentException if content doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create properties.
     */
    public Content createContentProperty(String location, String locale, String name, String value) throws ContentException,
            ContentIOException, ContentSecurityException {

        log.debug("createContentComment()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new ContentException("Parameter name cannot be null or empty");
        }
        if (value == null || "".equals(value)) {
            throw new ContentException("Parameter value cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createContentProperty() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        try {
            jcr.createContentProperty(location, name, value);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    public Content createContentACE(String location, String locale, String name, Principal.PrincipalType principal, ACE.PermissionType permission)
            throws ContentException, ContentIOException, ContentSecurityException {

        log.debug("createContentACE()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new ContentException("Parameter name cannot be null or empty");
        }
        if (principal == null || "".equals(principal)) {
            throw new ContentException("Parameter principal cannot be null or empty");
        }
        if (permission == null || "".equals(permission)) {
            throw new ContentException("Parameter permission cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for createContentProperty() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        try {
            jcr.createContentACE(location, name, principal, permission);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }


}