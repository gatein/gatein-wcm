/*
 * JBoss, a division of Red Hat
 * Copyright 2013, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.wcm.impl.services;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.jcr.Session;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMFolder;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;
import org.gatein.wcm.api.model.metadata.WCMCategory;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.impl.services.commands.CreateCommand;
import org.gatein.wcm.impl.services.commands.DeleteCommand;
import org.gatein.wcm.impl.services.commands.ReadCommand;
import org.gatein.wcm.impl.services.commands.UpdateCommand;

/**
 * @see {@link WCMContentService}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMContentServiceImpl implements WCMContentService {

    Session jcrSession = null;
    WCMUser logged = null;
    String repository = null;
    String defaultLocale = null;

    public WCMContentServiceImpl(String repository, Session session, WCMUser user, String defaultLocale)
            throws WCMContentIOException {
        jcrSession = session;
        logged = user;
        this.repository = repository;
        this.defaultLocale = defaultLocale;
    }

    /**
     * @see {@link WCMContentService#createTextDocument(String, String, String, String)}
     */
    @Override
    public WCMTextDocument createTextDocument(String id, String locale, String path, String content)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMTextDocument output = command.createTextDocument(id, locale, path, content);
        return output;
    }

    /**
     * @see {@link WCMContentService#createTextContent(String, String, String)}
     */
    @Override
    public WCMTextDocument createTextDocument(String id, String path, String html) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMTextDocument output = command.createTextDocument(id, defaultLocale, path, html);
        return output;
    }

    /**
     * @see {@link WCMContentService#createFolder(String, String)}
     */
    @Override
    public WCMFolder createFolder(String id, String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMFolder output = command.createFolder(id, path);
        return output;
    }

    /**
     * @see {@link WCMContentService#createBinaryContent(String, String, String, String, long, String, InputStream)}
     */
    @Override
    public WCMBinaryDocument createBinaryDocument(String id, String locale, String path, String contentType, long size,
            String fileName, InputStream content) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMBinaryDocument output = command.createBinaryContent(id, locale, path, contentType, size, fileName, content);
        return output;
    }

    /**
     * @see {@link WCMContentService#createBinaryContent(String, String, String, long, String, InputStream)}
     */
    @Override
    public WCMBinaryDocument createBinaryDocument(String id, String path, String contentType, long size, String fileName,
            InputStream content) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMBinaryDocument output = command.createBinaryContent(id, defaultLocale, path, contentType, size, fileName, content);
        return output;
    }

    /**
     * @see {@link WCMContentService#getContent(String)}
     */
    @Override
    public WCMObject getContent(String path) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        WCMObject output = command.getContent(path);
        return output;
    }

    /**
     * @see {@link WCMContentService#createContentRelation(String, String, String)}
     */
    @Override
    public void createContentRelation(String sourcePath, String targetPath, String sourceKey) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        command.createContentRelation(sourcePath, targetPath, sourceKey);
    }

    /**
     * @see {@link WCMContentService#getContent(String, String)}
     */
    @Override
    public WCMObject getContent(String path, String key) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        WCMObject output = command.getContent(path, key);
        return output;
    }

    /**
     * @see {@link WCMContentService#getContentRelationKeys(String)}
     */
    @Override
    public Set<String> getContentRelationKeys(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        Set<String> output = command.getContentRelationKeys(path);
        return output;
    }

    /**
     * @see {@link WCMContentService#updateTextDocument(String, WCMTextDocument)}
     */
    @Override
    public WCMTextDocument updateTextDocument(String path, WCMTextDocument doc) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WCMTextDocument output = command.updateTextDocument(path, doc);
        return output;
    }

    /**
     * @see {@link WCMContentService#updatePath(String, String)}
     */
    @Override
    public WCMObject updateContentPath(String path, String newParentPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WCMObject output = command.updateContentPath(path, newParentPath);
        return output;
    }

    /**
     * @see {@link WCMContentService#updateContentName(String, String)}
     */
    @Override
    public WCMObject updateContentName(String path, String newName) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WCMObject output = command.updateContentName(path, newName);
        return output;
    }

    /**
     * @see {@link WCMContentService#updateBinaryDocument(String, WCMBinaryDocument)}
     */
    @Override
    public WCMBinaryDocument updateBinaryDocument(String path, WCMBinaryDocument doc) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WCMBinaryDocument output = command.updateBinaryDocument(path, doc);
        return output;
    }

    /**
     * @see {@link WCMContentService#deleteContent(String)}
     */
    @Override
    public String deleteContent(String path) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        String parent = command.deleteContent(path);
        return parent;
    }

    /**
     * @see {@link WCMContentService#createCategory(String, String, String, String)}
     */
    @Override
    public WCMCategory createCategory(String id, String locale, String description, String categoryPath)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMCategory category = command.createCategory(id, locale, description, categoryPath);
        return category;
    }

    /**
     * @see {@link WCMContentService#createCategory(String, String, String)}
     */
    @Override
    public WCMCategory createCategory(String id, String description, String categoryPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMCategory category = command.createCategory(id, defaultLocale, description, categoryPath);
        return category;
    }

    /**
     * @see {@link WCMContentService#updateCategoryDescription(String, String, String)}
     */
    @Override
    public WCMCategory updateCategoryDescription(String categoryPath, String locale, String description)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WCMCategory category = command.updateCategoryDescription(categoryPath, locale, description);
        return category;
    }

    /**
     * @see {@link WCMContentService#updateCategoryDescription(String, String)}
     */
    @Override
    public WCMCategory updateCategoryDescription(String categoryPath, String description) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WCMCategory category = command.updateCategoryDescription(categoryPath, defaultLocale, description);
        return category;
    }

    /**
     * @see {@link WCMContentService#updateCategoryPath(String, String)}
     */
    @Override
    public void updateCategoryPath(String categoryPath, String newParentCategoryPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        command.updateCategoryPath(categoryPath, newParentCategoryPath);
    }

    /**
     * @see {@link WCMContentService#getCategories(String)}
     */
    @Override
    public List<WCMCategory> getCategories(String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<WCMCategory> category = command.getCategories(categoryPath);
        return category;
    }

    /**
     * @see {@link WCMContentService#getCategory(String)}
     */
    @Override
    public WCMCategory getCategory(String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        WCMCategory category = command.getCategory(categoryPath);
        return category;
    }

    /**
     * @see {@link WCMContentService#putContentCategory(String, String)}
     */
    @Override
    public void putContentCategory(String path, String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        command.putContentCategory(path, categoryPath);
    }

    /**
     * @see {@link WCMContentService#getContentCategories(String)}
     */
    @Override
    public List<WCMCategory> getContentCategories(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<WCMCategory> output = command.getContentCategories(path);
        return output;
    }

    /**
     * @see {@link WCMContentService#}
     */
    @Override
    public void removeContentCategory(String path, String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        command.removeContentCategory(path, categoryPath);
    }

    /**
     * @see {@link WCMContentService#deleteCategory(String)}
     */
    @Override
    public void deleteCategory(String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        command.deleteCategory(categoryPath);
    }

    /**
     * @see {@link WCMContentService#deleteCategory(String, String)}
     */
    @Override
    public void deleteCategory(String categoryPath, String locale) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        command.deleteCategory(categoryPath, locale);
    }

    /**
     * @see {@link WCMContentService#getContent(List, String, String)}
     */
    @Override
    public List<WCMObject> getContent(List<WCMCategory> categories, String filterPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<WCMObject> result = command.getContent(categories, filterPath);
        return result;
    }

    /**
     * @see {@link WCMContentService#createContentComment(String, String)}
     */
    @Override
    public WCMObject createContentComment(String path, String comment) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMObject result = command.createContentComment(path, comment);
        return result;
    }

    /**
     * @see {@link WCMContentService#deleteContentComment(String, String, String)}
     */
    @Override
    public WCMObject deleteContentComment(String location, String idComment) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        WCMObject result = command.deleteContentComment(location, idComment);
        return result;
    }

    /**
     * @see {@link WCMContentService#createContentProperty(String, String, String)}
     */
    @Override
    public WCMObject createContentProperty(String path, String name, String value) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMObject result = command.createContentProperty(path, name, value);
        return result;
    }

    /**
     * @see {@link WCMContentService#updateContentProperty(String, String, String)}
     */
    @Override
    public WCMObject updateContentProperty(String path, String name, String value) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        WCMObject result = command.updateContentProperty(path, name, value);
        return result;
    }

    /**
     * @see {@link WCMContentService#deleteContentProperty(String, String)}
     */
    @Override
    public WCMObject deleteContentProperty(String path, String name) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        WCMObject result = command.deleteContentProperty(path, name);
        return result;
    }

    /**
     * @see {@link WCMContentService#deleteContentAce(String, String)}
     */
    @Override
    public WCMObject createContentAce(String path, String name, WCMPrincipalType principal, WCMPermissionType permission)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        CreateCommand command = new CreateCommand(jcrSession, logged);
        WCMObject result = command.createContentAce(path, name, principal, permission);
        return result;
    }

    /**
     * @see {@link WCMContentService#deleteContentAce(String, String)}
     */
    @Override
    public WCMObject deleteContentAce(String path, String principalId) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        WCMObject result = command.deleteContentAce(path, principalId);
        return result;
    }

    /**
     * @see {@link WCMContentService#getVersions(String)}
     */
    @Override
    public List<String> getVersions(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        ReadCommand command = new ReadCommand(jcrSession, logged);
        List<String> result = command.getVersions(path);
        return result;
    }

    /**
     * @see {@link WCMContentService#restore(String, String)}
     */
    @Override
    public void restore(String path, String version) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        UpdateCommand command = new UpdateCommand(jcrSession, logged);
        command.restore(path, version);
    }

    /**
     * @see {@link WCMContentService#deleteContentVersion(String, String)}
     */
    @Override
    public String deleteContentVersion(String path, String version) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        DeleteCommand command = new DeleteCommand(jcrSession, logged);
        String currentPath = command.deleteContentVersion(path, version);
        return currentPath;
    }

    /**
     * @see {@link WCMContentService#closeSession()}
     */
    @Override
    public void closeSession() throws WCMContentIOException {
        if (jcrSession == null)
            throw new WCMContentIOException("");
        jcrSession.logout();
    }

    @Override
    public String toString() {

        String str = "[[ WCMContentService - User: " + this.logged.getUserName() + " - Repository: " + repository
                + " - Workspace: " + this.jcrSession.getWorkspace().getName() + " ]]";
        return str;
    }
}
