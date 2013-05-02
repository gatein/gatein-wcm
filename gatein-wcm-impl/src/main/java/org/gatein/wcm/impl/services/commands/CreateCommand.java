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
package org.gatein.wcm.impl.services.commands;

import java.io.InputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

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
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

/**
 * All create* methods for WCM Content should be placed here.
 * <p>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class CreateCommand {

    private static final Logger log = Logger.getLogger(CreateCommand.class);

    Session jcrSession = null;
    WCMUser logged = null;
    WCMContentFactory factory = null;
    JcrMappings jcr = null;

    public CreateCommand(Session session, WCMUser user) throws WCMContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WCMContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     * @see {@link WCMContentService#createTextDocument(String, String, String, String)}
     */
    public WCMTextDocument createTextDocument(String id, String locale, String path, String content)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        log.debug("createTextDocument()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WCMContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WCMContentException("Parameter locale cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (content == null || "".equals(content)) {
            throw new WCMContentException("Parameter content cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkParentPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for createTextDocument() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if there is a content with same id in the specified location
        String checkId = jcr.checkIdExists(path, id, locale);
        if (checkId != null && checkId.equals(id))
            throw new WCMContentException("Path: " + path + " Locale: " + locale + " id: " + id
                    + " exists for createTextDocument() operation. ");
        checkId = (checkId == null ? id : checkId);

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: " + path);

        // Creating new Node
        try {

            Value contentValue = jcr.jcrValue(content);
            jcr.createTextNode(checkId, locale, path, contentValue);
            // Creates a relationship if the id is a suggested one
            if (!checkId.equals(id)) {
                String sourcePath = ("/".equals(path)?"/" + id:path + "/" + id);
                String targetPath = ("/".equals(path)?"/" + checkId:path + "/" + checkId);
                jcr.jcrRelationShip(sourcePath, targetPath, locale);
            }
            // Return the model with the content
            return factory.createTextDocument(checkId, locale, path, content);

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#createFolder(String, String)}
     */
    public WCMFolder createFolder(String id, String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("createFolder()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WCMContentException("Parameter id cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkParentPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for createFolder() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if there is a content with same id in the specified location
        if (jcr.checkIdExists(path, id))
            throw new WCMContentException("Path: " + path + " Id: " + id + " exists for createFolder() operation. ");

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
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
     * @see {@link WCMContentService#createBinaryContent(String, String, String, String, long, String, InputStream)}
     */
    public WCMBinaryDocument createBinaryContent(String id, String locale, String path, String mimeType, long size,
            String fileName, InputStream content) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("createBinaryContent()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WCMContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WCMContentException("Parameter locale cannot be null or empty");
        }
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (mimeType == null || "".equals(mimeType)) {
            throw new WCMContentException("Parameter mimeType cannot be null or empty");
        }
        if (size == 0) {
            throw new WCMContentException("Parameter size cannot be null or 0");
        }
        if (fileName == null || "".equals(fileName)) {
            throw new WCMContentException("Parameter fileName cannot be null or empty");
        }
        if (content == null) {
            throw new WCMContentException("Parameter content in InputStream cannot be null");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkParentPath(path))
                throw new WCMContentException("Location: " + path + " doesn't exist for createBinaryContent() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if there is a content with same id in the specified location
        String checkId = jcr.checkIdExists(path, id, locale);
        if (checkId != null && checkId.equals(id))
            throw new WCMContentException("Path: " + path + " Locale: " + locale + " id: " + id
                    + " exists for createBinaryDocument() operation. ");
        checkId = (checkId == null ? id : checkId);

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Creating new Node
        try {
            jcr.createBinaryNode(checkId, locale, path, mimeType, size, fileName, content);
            // Creates a relationship if the id is a suggested one
            if (!checkId.equals(id)) {
                String sourcePath = ("/".equals(path)?"/" + id:path + "/" + id);
                String targetPath = ("/".equals(path)?"/" + checkId:path + "/" + checkId);
                jcr.jcrRelationShip(sourcePath, targetPath, locale);
            }
            return factory.createBinaryContent(checkId, locale, path, mimeType, size, fileName);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#createContentRelation(String, String, String)}
     */
    public void createContentRelation(String sourcePath, String targetPath, String sourceKey) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        log.debug("createContentRelation()");

        // Check null parameters
        if (sourcePath == null || "".equals(sourcePath)) {
            throw new WCMContentException("Parameter sourcePath cannot be null or empty");
        }
        if (targetPath == null || "".equals(targetPath)) {
            throw new WCMContentException("Parameter targetPath cannot be null or empty");
        }
        if (sourceKey == null || "".equals(sourceKey)) {
            throw new WCMContentException("Parameter sourceKey cannot be null or empty");
        }

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(sourcePath))
                throw new WCMContentException("Path: " + sourcePath + " doesn't exist for createContentRelation() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + sourcePath);
        }

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(targetPath))
                throw new WCMContentException("Path: " + targetPath + " doesn't exist for createContentRelation() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + targetPath);
        }

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(sourcePath))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + sourcePath);

        // Creating/updating relationship
        try {
            jcr.jcrRelationShip(sourcePath, targetPath, sourceKey);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     * @see {@link WCMContentService#createCategory(String, String, String, String)}
     */
    public WCMCategory createCategory(String id, String locale, String description, String categoryPath)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        log.debug("createCategory()");

        // Check null parameters
        if (id == null || "".equals(id)) {
            throw new WCMContentException("Parameter id cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WCMContentException("Parameter locale cannot be null or empty");
        }
        if (description == null || "".equals(description)) {
            throw new WCMContentException("Parameter description cannot be null or empty");
        }
        if (categoryPath == null || "".endsWith(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            categoryPath = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkCategoryParentPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath + " doesn't exist for createCategory() operation.");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + categoryPath);
        }

        // Check if there is a content with same id in the specified location
         if (jcr.checkCategoryIdExists(categoryPath, id, locale))
             throw new WCMContentException("Path: " + categoryPath + " Locale: " + locale + " id: " + id
                     + " exists for createCategory() operation.");

        // Check if user has rights to access
        if (!jcr.checkUserAdminCategories())
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in categories.");

        // Creating new Category
        try {
            jcr.createCategory(id, locale, categoryPath, description);
            return factory.getCategory(categoryPath + "/" + id);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#createContentComment(String, String)}
     */
    public WCMObject createContentComment(String path, String comment) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        log.debug("createContentComment()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (comment == null || "".equals(comment)) {
            throw new WCMContentException("Parameter comment cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for createContentComment() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: "
                    + path);

        try {
            jcr.createContentComment(path, comment);
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#createContentProperty(String, String, String)}
     */
    public WCMObject createContentProperty(String path, String name, String value) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {

        log.debug("createContentComment()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new WCMContentException("Parameter name cannot be null or empty");
        }
        if (value == null || "".equals(value)) {
            throw new WCMContentException("Parameter value cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for createContentProperty() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        try {
            jcr.createContentProperty(path, name, value);
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#createContentAce(String, String, WCMPrincipalType, WCMPermissionType)}
     */
    public WCMObject createContentAce(String path, String principalId, WCMPrincipalType principal,
            WCMPermissionType permission) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {

        log.debug("createContentAce()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (principalId == null || "".equals(principalId)) {
            throw new WCMContentException("Parameter principalId cannot be null or empty");
        }
        if (principal == null) {
            throw new WCMContentException("Parameter principal cannot be null");
        } else {
            boolean found = false;
            for (WCMPrincipalType t : WCMPrincipalType.values()) {
                if (t.equals(principal)) found = true;
            }
            if (!found)
                throw new WCMContentException("Parameter principal has to be a value of WCMPrincipalType");
        }

        if (permission == null) {
            throw new WCMContentException("Parameter permission cannot be null");
        } else {
            boolean found = false;
            for (WCMPermissionType t : WCMPermissionType.values()) {
                if (t.equals(permission)) found = true;
            }
            if (!found)
                throw new WCMContentException("Parameter permission has to be a value of WCMPermissionType");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for createContentAce() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            jcr.createContentAce(path, principalId, principal, permission);
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

}