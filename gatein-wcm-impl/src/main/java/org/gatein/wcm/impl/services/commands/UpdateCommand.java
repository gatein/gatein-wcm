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
import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WcmContentFactory;
import org.jboss.logging.Logger;

public class UpdateCommand {

    private static final Logger log = Logger.getLogger(UpdateCommand.class);

    Session jcrSession = null;
    WcmUser logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public UpdateCommand(Session session, WcmUser user) throws WcmContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WcmContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     * @see {@link WcmContentService#updateTextContent(String, String, String)}
     */
    public WcmTextObject updateTextContent(String path, String locale, String html) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        log.debug("updateTextContent()");

        // Check null parameters
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (html == null || "".equals(html)) {
            throw new WcmContentException("Parameter html cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path, locale))
            throw new WcmContentException("Location: " + path + " with locale : " + locale
                    + " doesn't exist for updateTextContent() operation. ");

        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Updating existing Node
        try {

            Value content = jcr.jcrValue(html);
            jcr.updateTextNode(path, locale, content);

            WcmObject obj = factory.getContent(path, locale);

            if (obj instanceof WcmTextObject)
                return (WcmTextObject)obj;
            else
                return null;

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     * @see {@link WcmContentService#updateFolderLocation(String, String, String)}
     */
    public WcmFolder updateFolderLocation(String path, String locale, String newPath) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        log.debug("updateFolderLocation()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (newPath == null || "".equals(newPath)) {
            throw new WcmContentException("Parameter newPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for updateFolderLocation() operation. ");

        if (!jcr.checkLocation(newPath))
            throw new WcmContentException("Location: " + newPath + " doesn't exist for updateFolderLocation() operation. ");

        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        if (!jcr.checkUserWriteACL(newPath))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + newPath);

        try {
            String id = path.substring(path.lastIndexOf("/") + 1);
            jcr.updateFolderLocation(path, newPath + "/" + id);

            WcmObject obj = factory.getContent(newPath + "/" + id, locale);

            if (obj instanceof WcmFolder)
                return (WcmFolder)obj;
            else
                return null;

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     * @see {@link WcmContentService#updateFolderName(String, String, String)}
     */
    public WcmFolder updateFolderName(String location, String locale, String newName) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        log.debug("updateFolderName()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (newName == null || "".equals(newName)) {
            throw new WcmContentException("Parameter newName cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for updateFolderName() operation. ");

        String newLocation = location.substring(0, location.lastIndexOf("/") + 1) + newName;

        if (jcr.checkLocation(newLocation))
            throw new WcmContentException("Location: " + newLocation + " exists for updateFolderName() operation. ");

        if (!jcr.checkUserWriteACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        try {
            jcr.updateFolderName(location, newName);

            WcmObject obj = factory.getContent(newLocation, locale);

            if (obj instanceof WcmFolder)
                return (WcmFolder)obj;
            else
                return null;

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#updateBinaryContent(String, String, String, long, String, InputStream)}
     */
    public WcmBinaryObject updateBinaryContent(String path, String locale, String contentType, Long size, String fileName,
            InputStream content) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {
        log.debug("updateBinaryContent()");

        // Check null parameters
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }
        if (contentType == null || "".endsWith(contentType)) {
            throw new WcmContentException("Parameter contentType cannot be null or empty");
        }
        if (size == null || size == 0) {
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
        if (!jcr.checkLocation(path, locale))
            throw new WcmContentException("Location: " + path + " with locale : " + locale
                    + " doesn't exist for updateBinaryContent() operation. ");

        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Updating existing Node
        try {

            byte[] _content = jcr.toByteArray(content);

            jcr.updateBinaryNode(path, locale, contentType, size, fileName, new ByteArrayInputStream(_content));

            WcmObject obj = factory.getContent(path, locale);

            if (obj instanceof WcmBinaryObject)
                return (WcmBinaryObject)obj;
            else
                return null;

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Updates existing Category in the repository. <br>
     * Categories can be organized in a hierarchical tree of categories parents and children.
     *
     */
    public WcmCategory updateCategoryDescription(String categoryLocation, String locale, String description)
            throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        log.debug("updateCategoryDescription()");

        // Check null parameters
        if (categoryLocation == null || "".equals(categoryLocation)) {
            throw new WcmContentException("Parameter categoryLocation cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (description == null || "".equals(description)) {
            throw new WcmContentException("Parameter description cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(categoryLocation))
            categoryLocation = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryLocation))
            throw new WcmContentException("Location: " + categoryLocation
                    + " doesn't exist for updateCategoryDescription() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
                    + "/__categories");

        // Creating new Category
        try {
            jcr.updateCategory("/__categories" + categoryLocation, locale, description);
            return factory.getCategory("/__categories" + categoryLocation, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#updateCategoryDescription(String, String, String)}
     */
    public WcmCategory updateCategoryLocation(String categoryPath, String locale, String newPath) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {

        log.debug("updateCategoryLocation()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WcmContentException("Parameter categoryPath cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WcmContentException("Parameter locale cannot be null or empty");
        }
        if (newPath == null || "".equals(newPath)) {
            throw new WcmContentException("Parameter newPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            categoryPath = "";
        if ("/".equals(newPath))
            newPath = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation("/__categories" + categoryPath))
            throw new WcmContentException("Location: " + categoryPath
                    + " doesn't exist for updateCategoryLocation() operation. ");

        if (!jcr.checkLocation("/__categories" + newPath))
            throw new WcmContentException("Location: " + newPath + " doesn't exist for updateCategoryLocation() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserAdminACL("/__categories"))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: "
                    + "/__categories");

        try {
            String fullOldLocation = "/__categories" + categoryPath;
            String fullNewLocation = "/__categories" + newPath;

            String id = fullOldLocation.substring(fullOldLocation.lastIndexOf("/") + 1);
            jcr.updateCategoryLocation(fullOldLocation, fullNewLocation + "/" + id);

            return factory.getCategory("/__categories" + newPath + "/" + id, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#addContentCategory(String, String)}
     */
    public void addContentCategory(String path, String categoryPath) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        log.debug("updateCategoryLocation()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WcmContentException("Parameter categoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for addContentCategory() operation. ");

        if (!jcr.checkLocation("/__categories" + categoryPath))
            throw new WcmContentException("Location: " + categoryPath + " doesn't exist for addContentCategory() operation. ");

        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        try {
            jcr.jcrCategoryReference(path, categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     * @see {@link WcmContentService#updateContentProperty(String, String, String, String)}
     */
    public WcmObject updateContentProperty(String location, String locale, String name, String value) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        log.debug("updateContentProperty()");

        // Check null parameteres
        if (location == null || "".equals(location)) {
            throw new WcmContentException("Parameter location cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
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
        if (!jcr.checkLocation(location))
            throw new WcmContentException("Location: " + location + " doesn't exist for updateContentProperty() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(location))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        try {
            // Creates or update property
            jcr.createContentProperty(location, locale, name, value);
            return factory.getContent(location, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WcmContentService#restore(String, String)}
     */
    public void restore(String path, String version) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {
        log.debug("restore()");

        if (path == null || "".equals(path)) {
            throw new WcmContentException("Parameter path cannot be null or empty");
        }

        if (version == null || "".equals(version)) {
            throw new WcmContentException("Parameter version cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WcmContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(path))
            throw new WcmContentException("Location: " + path + " doesn't exist for restore() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WcmContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        try {
            // Restore content
            jcr.jcrRestore(path, version);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }


    }

}
