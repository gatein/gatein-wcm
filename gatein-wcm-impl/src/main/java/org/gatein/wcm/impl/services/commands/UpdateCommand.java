package org.gatein.wcm.impl.services.commands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WcmContentFactory;
import org.jboss.logging.Logger;

public class UpdateCommand {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.commands");

    Session jcrSession = null;
    User logged = null;
    WcmContentFactory factory = null;
    JcrMappings jcr = null;

    public UpdateCommand(Session session, User user) throws ContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WcmContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     *
     * Updates a existing text content in the default repository.
     *
     */
    public Content updateTextContent(String location, String locale, String html) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("updateTextContent()");

        // Check null parameters
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
        if (!jcr.checkLocation(location, locale))
            throw new ContentException("Location: " + location + " with locale : " + locale
                    + " doesn't exist for updateTextContent() operation. ");

        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        // Updating existing Node
        try {

            Value content = jcr.jcrValue(html);
            jcr.updateTextNode(location, locale, content);

            return factory.getContent(location, locale);

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Moves an existing folder in the default repository.
     *
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateFolderLocation(String location, String locale, String newLocation) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("updateFolderLocation()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (newLocation == null || "".equals(newLocation)) {
            throw new ContentException("Parameter newLocation cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for updateFolderLocation() operation. ");

        if (!jcr.checkLocation(newLocation))
            throw new ContentException("Location: " + newLocation + " doesn't exist for updateFolderLocation() operation. ");

        try {
            String id = location.substring(location.lastIndexOf("/") + 1);
            jcr.updateFolderLocation(location, newLocation + "/" + id);
            return factory.getContent(newLocation + "/" + id, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Renames an existing folder in the default repository.
     *
     */
    public Content updateFolderName(String location, String locale, String newName) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("updateFolderName()");

        // Check null parameters
        if (location == null || "".equals(location)) {
            throw new ContentException("Parameter location cannot be null or empty");
        }
        if (newName == null || "".equals(newName)) {
            throw new ContentException("Parameter newName cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new ContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        if (!jcr.checkLocation(location))
            throw new ContentException("Location: " + location + " doesn't exist for updateFolderName() operation. ");

        String newLocation = location.substring(0, location.lastIndexOf("/") + 1) + newName;

        if (jcr.checkLocation(newLocation))
            throw new ContentException("Location: " + newLocation + " exists for updateFolderName() operation. ");

        try {
            jcr.updateFolderName(location, newName);
            return factory.getContent(newLocation, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     *
     * Updates new binary resource in the default repository.
     *
     */
    public Content updateBinaryContent(String location, String locale, String contentType, Long size, String fileName,
            InputStream content) throws ContentException, ContentIOException, ContentSecurityException {
        log.debug("updateBinaryContent()");

        // Check null parameters
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
        if (!jcr.checkLocation(location, locale))
            throw new ContentException("Location: " + location + " with locale : " + locale
                    + " doesn't exist for updateBinaryContent() operation. ");

        if (!jcr.checkUserWriteACL(location))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + location);

        // Updating existing Node
        try {

            byte[] _content = jcr.toByteArray(content);

            jcr.updateBinaryNode(location, locale, contentType, size, fileName, new ByteArrayInputStream(_content));

            return factory.getContent(location, locale);

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
    public Category updateCategoryDescription(String categoryLocation, String locale, String description) throws ContentException,
            ContentIOException, ContentSecurityException {

        log.debug("updateCategoryDescription()");

        // Check null parameters
        if (categoryLocation == null || "".equals(categoryLocation)) {
            throw new ContentException("Parameter categoryLocation cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new ContentException("Parameter locale cannot be null or empty");
        }
        if (description == null || "".equals(description)) {
            throw new ContentException("Parameter description cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if ( ! jcr.checkSession() )
            throw new ContentIOException("JCR Session is null");

        if ("/".equals( categoryLocation )) categoryLocation = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        if ( ! jcr.checkLocation("/__categories" + categoryLocation) )
            throw new ContentException("Location: " + categoryLocation + " doesn't exist for updateCategoryDescription() operation. ");

        // Check if user has rights to access
        if ( ! jcr.checkUserAdminACL( "/__categories" ))
            throw new ContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in location: " + "/__categories");

        // Creating new Category
        try {
            jcr.updateCategory("/__categories" + categoryLocation, locale, description);
            return factory.getCategory("/__categories" + categoryLocation, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException( e );
        }

        return null;
    }

    /**
    *
    * Updates an existing Category into a new categoryLocation. <br>
    *
    */
   public Category updateCategoryLocation(String categoryLocation, String locale, String newLocation) throws ContentException, ContentIOException,
           ContentSecurityException {

       log.debug("updateCategoryLocation()");

       // Check null parameters
       if (categoryLocation == null || "".equals(categoryLocation)) {
           throw new ContentException("Parameter categoryLocation cannot be null or empty");
       }
       if (locale == null || "".equals(locale)) {
           throw new ContentException("Parameter locale cannot be null or empty");
       }
       if (newLocation == null || "".equals(newLocation)) {
           throw new ContentException("Parameter newLocation cannot be null or empty");
       }

       // Check if the current JCR Session is valid
       if (!jcr.checkSession())
           throw new ContentIOException("JCR Session is null");

       if ("/".equals( categoryLocation )) categoryLocation = "";
       if ("/".equals( newLocation )) newLocation = "";

       // Check if the location specified exists in the JCR Repository/Workspace
       if (!jcr.checkLocation("/__categories" + categoryLocation))
           throw new ContentException("Location: " + categoryLocation + " doesn't exist for updateCategoryLocation() operation. ");

       if (!jcr.checkLocation("/__categories" + newLocation))
           throw new ContentException("Location: " + newLocation + " doesn't exist for updateCategoryLocation() operation. ");

       try {
           String fullOldLocation = "/__categories" + categoryLocation;
           String fullNewLocation = "/__categories" + newLocation;

           String id = fullOldLocation.substring(fullOldLocation.lastIndexOf("/") + 1);
           jcr.updateCategoryLocation(fullOldLocation, fullNewLocation + "/" + id);

           return factory.getCategory("/__categories" + newLocation + "/" + id, locale);
       } catch (RepositoryException e) {
           jcr.checkJCRException(e);
       }

       return null;
   }

   /**
   *
   * Attaches a Category in a Content.
   *
   */
  public void addContentCategory(String location, String categoryLocation) throws ContentException, ContentIOException,
      ContentSecurityException {
      log.debug("updateCategoryLocation()");

      // Check null parameters
      if (location == null || "".equals(location)) {
          throw new ContentException("Parameter location cannot be null or empty");
      }
      if (categoryLocation == null || "".equals(categoryLocation)) {
          throw new ContentException("Parameter categoryLocation cannot be null or empty");
      }

      // Check if the current JCR Session is valid
      if (!jcr.checkSession())
          throw new ContentIOException("JCR Session is null");

      // Check if the location specified exists in the JCR Repository/Workspace
      if (!jcr.checkLocation( location ))
          throw new ContentException("Location: " + location + " doesn't exist for addContentCategory() operation. ");

      if (!jcr.checkLocation("/__categories" + categoryLocation))
          throw new ContentException("Location: " + categoryLocation + " doesn't exist for addContentCategory() operation. ");

      try {
          jcr.jcrCategoryReference(location, categoryLocation);
      } catch (RepositoryException e) {
          jcr.checkJCRException(e);
      }
  }

  /**
  *
  * Modifies a property in the form KEY/VALUE to a Content. <br>
  * Properties are shared between locales of same Content. <br>
  *
  */
  public Content updateContentProperty(String location, String locale, String name, String value) throws ContentException, ContentIOException,
         ContentSecurityException {
      log.debug("updateContentProperty()");

      // Check null parameteres
      if (location == null || "".equals( location )) {
          throw new ContentException("Parameter location cannot be null or empty");
      }
      if (location == null || "".equals( location )) {
          throw new ContentException("Parameter location cannot be null or empty");
      }
      if (name == null || "".equals( name ) ) {
          throw new ContentException("Parameter name cannot be null or empty");
      }
      if (value == null || "".equals( value ) ) {
          throw new ContentException("Parameter value cannot be null or empty");
      }

      // Check if the current JCR Session is valid
      if ( ! jcr.checkSession() )
          throw new ContentIOException("JCR Session is null");

      // Check if the location specified exists in the JCR Repository/Workspace
      if ( ! jcr.checkLocation(location) )
          throw new ContentException("Location: " + location + " doesn't exist for updateContentProperty() operation. ");

      // Check if user has rights to access
      if ( ! jcr.checkUserWriteACL( location ))
          throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: " + location);

      try {
          // Creates or update property
          jcr.createContentProperty(location, name, value);
          return factory.getContent(location, locale);
      } catch (RepositoryException e) {
          jcr.checkJCRException( e );
      }

      return null;
  }

}
