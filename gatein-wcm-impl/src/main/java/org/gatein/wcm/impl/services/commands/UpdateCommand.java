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
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @param html - HTML content as string.
     * @param encoding - Specific encoding, by default UTF8.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the location doesn't exists in the repository.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateTextContent(String location, String locale, String html, String encoding) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("updateTextContent()");

        checkNullParameters(location, locale, html, encoding);

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

            Value content = jcr.jcrValue(html, encoding);
            jcr.updateTextNode(location, locale, content, encoding);

            return factory.getContent(location, locale);

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    private void checkNullParameters(String locale, String location, String html, String encoding) throws ContentException {
        if (locale == null || "".equals(locale)) {
            new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            new ContentException("Parameter location cannot be null or empty");
        }
        if (html == null || "".equals(html)) {
            new ContentException("Parameter html cannot be null or empty");
        }
        if (encoding == null || "".equals(encoding)) {
            new ContentException("Parameter encoding cannot be null or empty");
        }
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

        checkNullParameters(location, locale, newLocation);

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
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @param newName new name of the folder.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateFolderName(String location, String locale, String newName) throws ContentException,
            ContentIOException, ContentSecurityException {
        log.debug("updateFolderName()");

        checkNullParameters(location, newName);

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

    private void checkNullParameters(String location, String locale, String newLocation) throws ContentException {
        if (location == null || "".equals(location)) {
            new ContentException("Parameter location cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            new ContentException("Parameter locale cannot be null or empty");
        }
        if (newLocation == null || "".equals(newLocation)) {
            new ContentException("Parameter newLocation cannot be null or empty");
        }
    }

    private void checkNullParameters(String location, String newName) throws ContentException {
        if (location == null || "".equals(location)) {
            new ContentException("Parameter location cannot be null or empty");
        }
        if (newName == null || "".equals(newName)) {
            new ContentException("Parameter newName cannot be null or empty");
        }
    }

    /**
     *
     * Updates new binary resource in the default repository.
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
     * @throws ContentException if the id doesn't exist in the repository
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateBinaryContent(String location, String locale, String contentType, Long size, String fileName,
            InputStream content) throws ContentException, ContentIOException, ContentSecurityException {
        log.debug("updateBinaryContent()");

        checkNullParameters(location, locale, contentType, size, fileName, content);

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

    private void checkNullParameters(String location, String locale, String contentType, Long size, String fileName,
            InputStream content) throws ContentException {
        if (locale == null || "".equals(locale)) {
            new ContentException("Parameter locale cannot be null or empty");
        }
        if (location == null || "".equals(location)) {
            new ContentException("Parameter location cannot be null or empty");
        }
        if (contentType == null || "".endsWith(contentType)) {
            new ContentException("Parameter contentType cannot be null or empty");
        }
        if (size == null || size == 0) {
            new ContentException("Parameter size cannot be null or 0");
        }
        if (fileName == null || "".endsWith(fileName)) {
            new ContentException("Parameter fileName cannot be null or empty");
        }
        if (content == null) {
            new ContentException("Parameter content in InputStream cannot be null");
        }
    }

    /**
     *
     * Updates existing Category in the repository. <br>
     * Categories can be organized in a hierarchical tree of categories parents and children.
     *
     * @param categoryLocation - Location where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category.
     * @param description - Category description.
     * @return Category created (if ok), null (if error).
     * @throws ContentException if the categoryLocation doesn't exist
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public Category updateCategoryDescription(String categoryLocation, String locale, String description) throws ContentException,
            ContentIOException, ContentSecurityException {

        log.debug("updateCategoryDescription()");

        checkNullParameters(categoryLocation, locale, description);

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
    * @param categoryLocation - Location where the category is stored. <br>
    *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
    *        where "/" is the root of repository and &lt;id&gt; folders ID
    * @param newLocation - New Location where category will be moved
    * @return Category updated
    * @throws ContentException if the categoryLocation doesn't exist.
    * @throws ContentIOException if any IO related problem with repository.
    * @throws ContentSecurityException if user has not been granted to create categories.
    */
   public Category updateCategoryLocation(String categoryLocation, String locale, String newLocation) throws ContentException, ContentIOException,
           ContentSecurityException {

       log.debug("updateCategoryLocation()");

       checkNullParameters(categoryLocation, locale, newLocation);

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
           // TODO modify with categories
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
   * @param location - Content location id
   * @param categoryLocation - Category location id.
   * @return Content updated.
   * @throws ContentException if content or category don't exist.
   * @throws ContentIOException if any IO related problem with repository.
   * @throws ContentSecurityException if user has not been granted to create categories.
   */
  public void addContentCategory(String location, String categoryLocation) throws ContentException, ContentIOException,
      ContentSecurityException {
      log.debug("updateCategoryLocation()");

      checkNullParametersForCategory(location, categoryLocation);

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

  private void checkNullParametersForCategory(String location, String categoryLocation) throws ContentException {
      if (location == null || "".equals(location)) {
          new ContentException("Parameter location cannot be null or empty");
      }
      if (categoryLocation == null || "".equals(categoryLocation)) {
          new ContentException("Parameter categoryLocation cannot be null or empty");
      }
  }


}
