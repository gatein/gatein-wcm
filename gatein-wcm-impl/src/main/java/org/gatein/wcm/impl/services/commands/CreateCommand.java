package org.gatein.wcm.impl.services.commands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

public class CreateCommand {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.commands");

    Session jcrSession = null;
    User logged = null;
    WCMContentFactory factory = null;
    JcrMappings jcr = null;

    public CreateCommand (Session session, User user)
            throws ContentIOException
    {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WCMContentFactory(jcr, logged);
        jcr.setFactory( factory );
    }

    /**
     *
     * Creates a new text content in the default repository.
     *
     * @param id - Key under which to store the content.
     * @param locale - Locale under content is stored.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param html - HTML content as string.
     * @param encoding - Specific encoding, by default UTF8.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (then user should use updateSimpleContent to create a new
     *         version).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content createTextContent(String id, String locale, String location, String html, String encoding)
            throws ContentException, ContentIOException, ContentSecurityException {

        log.debug("createTextContent()");

        checkNullParameters(id, locale, location, html, encoding);

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
        if ( ! jcr.checkUserWriteACL( location ))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: " + location);

        // Creating new Node
        try {

            Value content = jcr.jcrValue(html, encoding);
            jcr.createTextNode(id, locale, location, content, encoding);

            return factory.createTextContent(id, locale, location, html, encoding);

        } catch (RepositoryException e) {
            jcr.checkJCRException( e );
        }

        return null;
    }

    private void checkNullParameters(String id, String locale, String location, String html, String encoding) throws ContentException
    {
        if (id == null || "".equals( id )) {
            new ContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals( locale )) {
            new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals( location )) {
            new ContentException("Parameter location cannot be null or empty");
        }
        if (html == null || "".equals( html )) {
            new ContentException("Parameter html cannot be null or empty");
        }
        if (encoding == null || "".equals( encoding )) {
            new ContentException("Parameter encoding cannot be null or empty");
        }
    }

    /**
     *
     * Creates a new folder in the default repository.
     *
     * @param id - Key under which to store the folder.
     * @param locale - Locale under content is stored.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     *        UPDATE: in folder we don't use locale in the implementation.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content createFolder(String id, String location)
            throws ContentException, ContentIOException, ContentSecurityException {

        log.debug("createFolder()");

        checkNullParameters(id, location);

        // Check if the current JCR Session is valid
        if ( ! jcr.checkSession() )
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if ( ! jcr.checkLocation(location) )
            throw new ContentException("Location: " + location + " doesn't exist for createFolder() operation. ");

        // Check if there is a content with same id in the specified location
        if ( jcr.checkIdExists(location, id) )
            throw new ContentException("Location: " + location + " Id: " + id + " exists for createFolder() operation. ");

        // Check if user has rights to access
        if ( ! jcr.checkUserWriteACL( location ))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: " + location);

        // Creating new folder
        try {

            jcr.createFolder(id, location);

            return factory.createFolder(id, location);

        } catch (RepositoryException e) {
            jcr.checkJCRException( e );
        }

        return null;

    }

    private void checkNullParameters(String id, String location) throws ContentException
    {
        if (id == null || "".equals( id )) {
            new ContentException("Parameter id cannot be null or empty");
        }
        if (location == null || "".equals( location )) {
            new ContentException("Parameter location cannot be null or empty");
        }
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
            String fileName, InputStream content) throws ContentException, ContentIOException, ContentSecurityException
    {

        log.debug("createBinaryContent()");

        checkNullParameters(id, locale, location, contentType, size, fileName, content);

        // Check if the current JCR Session is valid
        if ( ! jcr.checkSession() )
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if ( ! jcr.checkLocation(location) )
            throw new ContentException("Location: " + location + " doesn't exist for createBinaryContent() operation. ");

        // Check if there is a content with same id in the specified location
        if ( jcr.checkIdExists(location, id, locale) )
            throw new ContentException("Location: " + location + " Locale: " + locale + " id: " + id + " exists for createTextContent() operation. ");

        // Check if user has rights to access
        if ( ! jcr.checkUserWriteACL( location ))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: " + location);

        // Creating new Node
        try {

            byte[] _content = jcr.toByteArray(content);

            jcr.createBinaryNode(id, locale, location, contentType, size, fileName, new ByteArrayInputStream( _content ) );

            return factory.createBinaryContent(id, locale, location, contentType, size, fileName, new ByteArrayInputStream( _content ) );

        } catch (RepositoryException e) {
            jcr.checkJCRException( e );
        }

        return null;
    }

    private void checkNullParameters(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content) throws ContentException
    {
        if (id == null || "".equals( id )) {
            new ContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals( locale ) ) {
            new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals( location ) ) {
            new ContentException("Parameter location cannot be null or empty");
        }
        if (contentType == null || "".endsWith( contentType ) ) {
            new ContentException("Parameter contentType cannot be null or empty");
        }
        if (size == null || size == 0) {
            new ContentException("Parameter size cannot be null or 0");
        }
        if (fileName == null || "".endsWith( fileName )) {
            new ContentException("Parameter fileName cannot be null or empty");
        }
        if (content == null) {
            new ContentException("Parameter content in InputStream cannot be null");
        }
    }

}