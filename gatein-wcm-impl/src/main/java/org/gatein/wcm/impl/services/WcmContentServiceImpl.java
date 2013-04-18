package org.gatein.wcm.impl.services;

import java.io.InputStream;
import java.util.List;

import javax.jcr.Session;

import org.gatein.wcm.api.model.content.WcmBinaryObject;
import org.gatein.wcm.api.model.content.WcmFolder;
import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmTextObject;
import org.gatein.wcm.api.model.metadata.WcmCategory;
import org.gatein.wcm.api.model.security.WcmAce.PermissionType;
import org.gatein.wcm.api.model.security.WcmPrincipal;
import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.api.services.exceptions.WcmPublishException;
import org.gatein.wcm.impl.services.commands.CreateCommand;
import org.gatein.wcm.impl.services.commands.DeleteCommand;
import org.gatein.wcm.impl.services.commands.ReadCommand;
import org.gatein.wcm.impl.services.commands.UpdateCommand;
import org.jboss.logging.Logger;

public class WcmContentServiceImpl implements WcmContentService {

    private static final Logger log = Logger.getLogger(WcmContentServiceImpl.class);

    Session jcrSession = null;
    WcmUser logged = null;
    String repository = null;

    public WcmContentServiceImpl(String repository, Session session, WcmUser user) throws WcmContentIOException {
        jcrSession = session;
        logged = user;
        this.repository = repository;
    }

    /**
     * @see {@link WcmContentService#createTextContent(String, String, String, String)}
     */
    @Override
    public WcmTextObject createTextContent(String id, String locale, String path, String html) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        WcmTextObject output = command.createTextContent(id, locale, path, html);

        long stop = System.currentTimeMillis();

        log.debug("createTextContent() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#createFolder(String, String)}
     */
    @Override
    public WcmFolder createFolder(String id, String path) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        WcmFolder output = command.createFolder(id, path);

        long stop = System.currentTimeMillis();

        log.debug("createFolder() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#createBinaryContent(String, String, String, String, long, String, InputStream)}
     */
    @Override
    public WcmBinaryObject createBinaryContent(String id, String locale, String path, String contentType, long size,
            String fileName, InputStream content) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        WcmBinaryObject output = command.createBinaryContent(id, locale, path, contentType, size, fileName, content);

        long stop = System.currentTimeMillis();

        log.debug("createBinaryContent() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#getContent(String, String)}
     */
    @Override
    public WcmObject getContent(String path, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        WcmObject output = command.getContent(path, locale);

        long stop = System.currentTimeMillis();

        log.debug("getContent() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#getContentLocales(String)}
     */
    @Override
    public List<String> getContentLocales(String location) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<String> output = command.getContentLocales(location);

        long stop = System.currentTimeMillis();

        log.debug("getContentLocales() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#updateTextContent(String, String, String)}
     */
    @Override
    public WcmTextObject updateTextContent(String path, String locale, String html) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WcmTextObject output = command.updateTextContent(path, locale, html);

        long stop = System.currentTimeMillis();

        log.debug("updateTextContent() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#updateFolderLocation(String, String, String)}
     */
    @Override
    public WcmFolder updateFolderLocation(String path, String locale, String newPath) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WcmFolder output = command.updateFolderLocation(path, locale, newPath);

        long stop = System.currentTimeMillis();

        log.debug("updateFolderLocation() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#updateFolderName(String, String, String)}
     */
    @Override
    public WcmFolder updateFolderName(String path, String locale, String newName) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WcmFolder output = command.updateFolderName(path, locale, newName);

        long stop = System.currentTimeMillis();

        log.debug("updateFolderName() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#updateBinaryContent(String, String, String, long, String, InputStream)}
     */
    @Override
    public WcmBinaryObject updateBinaryContent(String path, String locale, String contentType, long size, String fileName,
            InputStream content) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WcmBinaryObject output = command.updateBinaryContent(path, locale, contentType, size, fileName, content);

        long stop = System.currentTimeMillis();

        log.debug("updateBinaryContent() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#deleteContent(String, String)}
     */
    @Override
    public String deleteContent(String path, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        String parent = command.deleteContent(path, locale);

        long stop = System.currentTimeMillis();

        log.debug("deleteContent() takes " + ((long) (stop - start)) + " ms");

        return parent;
    }

    /**
     * @see {@link WcmContentService#createCategory(String, String, String, String)}
     */
    @Override
    public WcmCategory createCategory(String id, String locale, String description, String categoryPath)
            throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        WcmCategory category = command.createCategory(id, locale, description, categoryPath);

        long stop = System.currentTimeMillis();

        log.debug("createCategory() takes " + ((long) (stop - start)) + " ms");

        return category;
    }

    /**
     * @see {@link WcmContentService#updateCategoryDescription(String, String, String)}
     */
    @Override
    public WcmCategory updateCategoryDescription(String categoryPath, String locale, String description)
            throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WcmCategory category = command.updateCategoryDescription(categoryPath, locale, description);

        long stop = System.currentTimeMillis();

        log.debug("updateCategoryDescription() takes " + ((long) (stop - start)) + " ms");

        return category;
    }

    /**
     * @see {@link WcmContentService#updateCategoryDescription(String, String, String)}
     */
    @Override
    public WcmCategory updateCategoryLocation(String categoryPath, String locale, String newPath) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WcmCategory category = command.updateCategoryLocation(categoryPath, locale, newPath);

        long stop = System.currentTimeMillis();

        log.debug("updateCategoryLocation() takes " + ((long) (stop - start)) + " ms");

        return category;
    }

    /**
     * @see {@link WcmContentService#getCategories(String, String)}
     */
    @Override
    public List<WcmCategory> getCategories(String categoryPath, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<WcmCategory> category = command.getCategories(categoryPath, locale);

        long stop = System.currentTimeMillis();

        log.debug("getCategories() takes " + ((long) (stop - start)) + " ms");

        return category;
    }

    /**
     * @see {@link WcmContentService#getCategories(String, String)}
     */
    @Override
    public void addContentCategory(String path, String categoryPath) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        command.addContentCategory(path, categoryPath);

        long stop = System.currentTimeMillis();

        log.debug("addContentCategory() takes " + ((long) (stop - start)) + " ms");
    }

    /**
     * @see {@link WcmContentService#deleteCategory(String)}
     */
    @Override
    public void deleteCategory(String categoryPath) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        command.deleteCategory(categoryPath);

        long stop = System.currentTimeMillis();

        log.debug("deleteCategory() takes " + ((long) (stop - start)) + " ms");
    }

    /**
     * @see {@link WcmContentService#deleteCategory(String, String)}
     */
    @Override
    public WcmCategory deleteCategory(String categoryPath, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {

        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        WcmCategory c = command.deleteCategory(categoryPath, locale);

        long stop = System.currentTimeMillis();

        log.debug("deleteCategory() takes " + ((long) (stop - start)) + " ms");

        return c;
    }

    /**
     * @see {@link WcmContentService#getContent(List, String, String)}
     */
    @Override
    public List<WcmObject> getContent(List<WcmCategory> categories, String location, String locale) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<WcmObject> result = command.getContent(categories, location, locale);

        long stop = System.currentTimeMillis();

        log.debug("deleteCategory() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#createContentComment(String, String, String)}
     */
    @Override
    public WcmObject createContentComment(String path, String locale, String comment) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        WcmObject result = command.createContentComment(path, locale, comment);

        long stop = System.currentTimeMillis();

        log.debug("createContentComment() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#deleteContentComment(String, String, String)}
     */
    @Override
    public WcmObject deleteContentComment(String location, String locale, String idComment) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        WcmObject result = command.deleteContentComment(location, locale, idComment);

        long stop = System.currentTimeMillis();

        log.debug("createContentComment() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#createContentProperty(String, String, String, String)}
     */
    @Override
    public WcmObject createContentProperty(String path, String locale, String name, String value) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        WcmObject result = command.createContentProperty(path, locale, name, value);

        long stop = System.currentTimeMillis();

        log.debug("createContentProperty() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#updateContentProperty(String, String, String, String)}
     */
    @Override
    public WcmObject updateContentProperty(String path, String locale, String name, String value) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WcmObject result = command.updateContentProperty(path, locale, name, value);

        long stop = System.currentTimeMillis();

        log.debug("updateContentProperty() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#deleteContentProperty(String, String, String)}
     */
    @Override
    public WcmObject deleteContentProperty(String path, String locale, String name) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        WcmObject result = command.deleteContentProperty(path, locale, name);

        long stop = System.currentTimeMillis();

        log.debug("deleteContentProperty() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#createContentAce(String, String, String, org.gatein.wcm.api.model.security.WcmPrincipal.PrincipalType, PermissionType)}
     */
    @Override
    public WcmObject createContentAce(String path, String locale, String name, WcmPrincipal.PrincipalType principal,
            PermissionType permission) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        CreateCommand command = new CreateCommand(jcrSession, logged);
        WcmObject result = command.createContentAce(path, locale, name, principal, permission);

        long stop = System.currentTimeMillis();

        log.debug("createContentACE() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#deleteContentAce(String, String, String)}
     */
    @Override
    public WcmObject deleteContentAce(String path, String locale, String name) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        WcmObject result = command.deleteContentAce(path, locale, name);

        long stop = System.currentTimeMillis();

        log.debug("deleteContentACE() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#getContentVersions(String)}
     */
    @Override
    public List<String> getContentVersions(String path) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<String> result = command.getContentVersions(path);

        long stop = System.currentTimeMillis();

        log.debug("getContentVersions() takes " + ((long) (stop - start)) + " ms");

        return result;
    }

    /**
     * @see {@link WcmContentService#restore(String, String)}
     */
    @Override
    public void restore(String path, String version) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        command.restore(path, version);

        long stop = System.currentTimeMillis();

        log.debug("restore() takes " + ((long) (stop - start)) + " ms");
    }

    /**
     * @see {@link WcmContentService#getContent(String, String, String)}
     */
    @Override
    public WcmObject getContent(String path, String locale, String version) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException {
        long start = System.currentTimeMillis();

        ReadCommand command = new ReadCommand(jcrSession, logged);
        WcmObject output = command.getContent(path, locale, version);

        long stop = System.currentTimeMillis();

        log.debug("getContent() takes " + ((long) (stop - start)) + " ms");

        return output;
    }

    /**
     * @see {@link WcmContentService#deleteContentVersion(String, String)}
     */
    @Override
    public String deleteContentVersion(String path, String version) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException {
        // TODO To implement once that is clear how to delete properly versions in modeshape
        return null;
    }

    @Override
    public String deleteContent(String location) throws WcmContentException, WcmContentIOException, WcmContentSecurityException {

        long start = System.currentTimeMillis();

        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        String parent = command.deleteContent(location);

        long stop = System.currentTimeMillis();

        log.debug("deleteContet() takes " + ((long) (stop - start)) + " ms");

        return parent;
    }

    @Override
    public WcmObject submitPublish(WcmObject content) throws WcmPublishException, WcmContentIOException, WcmContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WcmObject submitDraft(WcmObject content) throws WcmPublishException, WcmContentIOException, WcmContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see {@link WcmContentService#deleteContentVersion(String, String)}
     */
    @Override
    public void closeSession() throws WcmContentIOException {
        if (jcrSession == null) throw new WcmContentIOException("");
        jcrSession.logout();
    }

    @Override
    public String toString() {

        String str = "[[ WCMContentService - User: " + this.logged.getUserName() + " - Repository: " + repository
                + " - Workspace: " + this.jcrSession.getWorkspace().getName() + " ]]";
        return str;
    }

}
