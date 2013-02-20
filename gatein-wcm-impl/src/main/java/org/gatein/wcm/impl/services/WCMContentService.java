package org.gatein.wcm.impl.services;

import java.io.InputStream;
import java.util.List;

import javax.jcr.Session;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.security.ACE.PermissionType;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.api.services.exceptions.PublishException;
import org.gatein.wcm.impl.services.commands.CreateCommand;
import org.gatein.wcm.impl.services.commands.DeleteCommand;
import org.gatein.wcm.impl.services.commands.ReadCommand;
import org.gatein.wcm.impl.services.commands.UpdateCommand;
import org.jboss.logging.Logger;

public class WCMContentService implements ContentService {

    private static final Logger log = Logger.getLogger("org.gatein.wcm");

    Session jcrSession = null;
    User logged = null;

    public WCMContentService (Session session, User user)
        throws ContentIOException
    {
        jcrSession = session;
        logged = user;
    }

    @Override
    public Content createTextContent(String id, String locale, String location, String html, String encoding)
            throws ContentException, ContentIOException, ContentSecurityException {

        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        Content output = command.createTextContent(id, locale, location, html, encoding);

        long stop = System.currentTimeMillis();

        log.debug("createTextContent() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public Content createFolder(String id, String location)
            throws ContentException, ContentIOException, ContentSecurityException {

        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        Content output = command.createFolder(id, location);

        long stop = System.currentTimeMillis();

        log.debug("createFolder() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public Content createBinaryContent(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content) throws ContentException, ContentIOException, ContentSecurityException {

        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        Content output = command.createBinaryContent(id, locale, location, contentType, size, fileName, content);
        long stop = System.currentTimeMillis();

        log.debug("createBinaryContent() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public Content getContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        Content output = command.getContent(location, locale);

        long stop = System.currentTimeMillis();

        log.debug("getContent() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public List<String> getContentLocales(String location) throws ContentException, ContentIOException,
            ContentSecurityException {
        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<String> output = command.getContentLocales(location);

        long stop = System.currentTimeMillis();

        log.debug("getContentLocales() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public Content updateTextContent(String location, String locale, String html, String encoding) throws ContentException,
            ContentIOException, ContentSecurityException {
        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        Content output = command.updateTextContent(location, locale, html, encoding);

        long stop = System.currentTimeMillis();

        log.debug("updateTextContent() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public Content updateFolderLocation(String location, String locale, String newLocation) throws ContentException,
            ContentIOException, ContentSecurityException {
        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        Content output = command.updateFolderLocation(location, locale, newLocation);

        long stop = System.currentTimeMillis();

        log.debug("updateFolder() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public Content updateFolderName(String location, String locale, String newName) throws ContentException,
            ContentIOException, ContentSecurityException {
        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        Content output = command.updateFolderName(location, locale, newName);

        long stop = System.currentTimeMillis();

        log.debug("updateFolder() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public Content updateBinaryContent(String location, String locale, String contentType, Long size, String fileName,
            InputStream content) throws ContentException, ContentIOException, ContentSecurityException {
        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        Content output = command.updateBinaryContent(location, locale, contentType, size, fileName, content);

        long stop = System.currentTimeMillis();

        log.debug("updateTextContent() takes " + ((long)(stop-start)) + " ms");

        return output;
    }

    @Override
    public String deleteContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        String parent = command.deleteContent( location, locale );

        long stop = System.currentTimeMillis();

        log.debug("deleteContet() takes " + ((long)(stop-start)) + " ms");

        return parent;
    }

    @Override
    public Category createCategory(String id, String locale, String description, String categoryLocation)
            throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category updateCategory(String categoryLocation, String locale, String description) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category updateCategory(String categoryLocation, String newLocation) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content addContentCategory(String location, Category category) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category deleteCategory(String categoryLocation) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> getContent(List<Category> categories, String location, String locale) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createContentComment(String location, String locale, String comment) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content deleteContentComment(String location, String locale, String idComment) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createContentProperty(String location, String name, String value) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content updateContentProperty(String location, String name, String value) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content deleteContentProperty(String location, String name) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createContentACE(String location, Principal principal, PermissionType permission) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content deleteContentACE(String location, Principal principal) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getContentVersions(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> checkOut(String location, String locale, Integer version) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content checkIn(Content content) throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> getContent(String location, String locale, Integer version) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String deleteContent(String location, String locale, Integer version) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String deleteContet(String location) throws ContentException, ContentIOException, ContentSecurityException {

        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        String parent = command.deleteContent( location );

        long stop = System.currentTimeMillis();

        // TODO move to Debug once is tested
        log.info("deleteContet() takes " + ((long)(stop-start)) + " ms");

        return parent;
    }

    @Override
    public Content submitPublish(Content content) throws PublishException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content submitDraft(Content content) throws PublishException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void closeSession() throws ContentException, ContentIOException {
        // TODO Auto-generated method stub

    }

    // Private Methods

    @Override
    public String toString() {
        String str = "[[ WCMContentService - User: " + this.logged.getUserName() +
                     " - Repository: " + this.jcrSession.getRepository().toString() +
                     " - Workspace: " + this.jcrSession.getWorkspace().getName() + " ]]";

        return str;
    }




}
