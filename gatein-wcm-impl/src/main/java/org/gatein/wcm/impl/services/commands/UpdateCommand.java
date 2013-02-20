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

public class UpdateCommand {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.commands");

    Session jcrSession = null;
    User logged = null;
    WCMContentFactory factory = null;
    JcrMappings jcr = null;

    public UpdateCommand (Session session, User user)
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
           ContentIOException, ContentSecurityException
   {
       log.debug("updateTextContent()");

       checkNullParameters(location, locale, html, encoding);

       // Check if the current JCR Session is valid
       if ( ! jcr.checkSession() )
           throw new ContentIOException("JCR Session is null");

       // Check if the location specified exists in the JCR Repository/Workspace
       if ( ! jcr.checkLocation(location, locale) )
           throw new ContentException("Location: " + location + " with locale : " + locale + " doesn't exist for updateTextContent() operation. ");

       if ( ! jcr.checkUserWriteACL( location ))
           throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: " + location);

       // Updating existing Node
       try {

           Value content = jcr.jcrValue(html, encoding);
           jcr.updateTextNode(location, locale, content, encoding);

           return factory.getContent(location, locale);

       } catch (RepositoryException e) {
           jcr.checkJCRException( e );
       }

       return null;
   }

   private void checkNullParameters(String locale, String location, String html, String encoding) throws ContentException
   {
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
          ContentIOException, ContentSecurityException
  {
      log.debug("updateFolderLocation()");

      checkNullParameters(location, locale, newLocation);

      // Check if the current JCR Session is valid
      if ( ! jcr.checkSession() )
          throw new ContentIOException("JCR Session is null");

      // Check if the location specified exists in the JCR Repository/Workspace
      if ( ! jcr.checkLocation(location) )
          throw new ContentException("Location: " + location + " doesn't exist for updateFolderLocation() operation. ");

      if ( ! jcr.checkLocation(newLocation) )
          throw new ContentException("Location: " + newLocation + " doesn't exist for updateFolderLocation() operation. ");

      try {
          jcr.updateFolderLocation(location, newLocation);
          return factory.getContent(newLocation, locale);
      } catch (RepositoryException e) {
          jcr.checkJCRException( e );
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
  public Content updateFolderName(String location,String locale, String newName) throws ContentException,
         ContentIOException, ContentSecurityException
  {
     log.debug("updateFolderName()");

     checkNullParameters(location, newName);

     // Check if the current JCR Session is valid
     if ( ! jcr.checkSession() )
         throw new ContentIOException("JCR Session is null");

     // Check if the location specified exists in the JCR Repository/Workspace
     if ( ! jcr.checkLocation(location) )
         throw new ContentException("Location: " + location + " doesn't exist for updateFolderName() operation. ");

     String newLocation = location.substring(0, location.lastIndexOf("/")+1) + newName;

     if ( jcr.checkLocation(newLocation) )
         throw new ContentException("Location: " + newLocation + " exists for updateFolderName() operation. ");

     try {
         jcr.updateFolderName(location, newLocation);
         return factory.getContent(newLocation, locale);
     } catch (RepositoryException e) {
         jcr.checkJCRException( e );
     }

     return null;
 }

  private void checkNullParameters(String location, String locale, String newLocation) throws ContentException
  {
      if (location == null || "".equals( location )) {
          new ContentException("Parameter location cannot be null or empty");
      }
      if (locale == null || "".equals( locale )) {
          new ContentException("Parameter locale cannot be null or empty");
      }
      if (newLocation == null || "".equals( newLocation )) {
          new ContentException("Parameter newLocation cannot be null or empty");
      }
  }

  private void checkNullParameters(String location, String newName) throws ContentException
  {
      if (location == null || "".equals( location )) {
          new ContentException("Parameter location cannot be null or empty");
      }
      if (newName == null || "".equals( newName )) {
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
         InputStream content) throws ContentException, ContentIOException, ContentSecurityException
 {
     log.debug("updateBinaryContent()");

     checkNullParameters(location, locale, contentType, size, fileName, content);

     // Check if the current JCR Session is valid
     if ( ! jcr.checkSession() )
         throw new ContentIOException("JCR Session is null");

     // Check if the location specified exists in the JCR Repository/Workspace
     if ( ! jcr.checkLocation(location, locale) )
         throw new ContentException("Location: " + location + " with locale : " + locale + " doesn't exist for updateBinaryContent() operation. ");

     if ( ! jcr.checkUserWriteACL( location ))
         throw new ContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: " + location);

     // Updating existing Node
     try {

         byte[] _content = jcr.toByteArray(content);

         jcr.updateBinaryNode(location, locale, contentType, size, fileName, new ByteArrayInputStream( _content ));

         return factory.getContent(location, locale);

     } catch (RepositoryException e) {
         jcr.checkJCRException( e );
     }

     return null;
 }

 private void checkNullParameters(String location, String locale, String contentType, Long size,
         String fileName, InputStream content) throws ContentException
 {
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
