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

import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;
import org.gatein.wcm.api.model.metadata.WCMCategory;
import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

/**
 * All update* methods for WCM Content should be placed here.
 * <p>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class UpdateCommand {

    private static final Logger log = Logger.getLogger(UpdateCommand.class);

    Session jcrSession = null;
    WCMUser logged = null;
    WCMContentFactory factory = null;
    JcrMappings jcr = null;

    public UpdateCommand(Session session, WCMUser user) throws WCMContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WCMContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     * @see {@link WCMContentService#updateTextDocument(String, WCMTextDocument)}
     */
    public WCMTextDocument updateTextDocument(String path, WCMTextDocument doc) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("updateTextContent()");

        // Check null parameters
        if (path == null || "".equals(path))
            throw new WCMContentException("Parameter path cannot be null or empty");
        if (doc == null)
            throw new WCMContentException("Parameter doc cannot be null or empty");
        if (doc.getLocale() == null || "".equals(doc.getLocale()))
            throw new WCMContentException("Parameter doc.getLocale() cannot be null or empty");
        if (doc.getContentAsInputStream() == null || doc.getSize() == 0)
            throw new WCMContentException("Parameter doc.getContent() cannot be null or empty");

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for updateTextContent() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Updating existing Node
        try {

            jcr.updateBinaryNode(path, doc);

            WCMObject obj = factory.getContent(path);
            if (obj instanceof WCMTextDocument)
                return (WCMTextDocument)obj;
            else
                return null;

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     * @see {@link WCMContentService#updatePath(String, String)}
     */
    public WCMObject updateContentPath(String path, String newParentPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        log.debug("updateFolderLocation()");

        // Check null parameters
        if (path == null || "".equals(path))
            throw new WCMContentException("Parameter path cannot be null or empty");
        if (newParentPath == null || "".equals(newParentPath))
            throw new WCMContentException("Parameter newParentPath cannot be null or empty");

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Location: " + path + " doesn't exist for updateContentPath() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        try {
            if (!jcr.checkParentPath(newParentPath))
                throw new WCMContentException("Location: " + newParentPath + " doesn't exist for updateContentPath() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        if (!jcr.checkUserWriteACL(newParentPath))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + newParentPath);

        try {
            String id = path.substring(path.lastIndexOf("/") + 1);
            String newPath = ("/".equals(newParentPath)?"/" + id:newParentPath + "/" + id);
            jcr.updatePath(path, newPath);

            WCMObject obj = factory.getContent(newParentPath + "/" + id);
            return obj;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     * @see {@link WCMContentService#updateContentName(String, String)}
     */
    public WCMObject updateContentName(String path, String newName) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        log.debug("updateFolderName()");

        // Check null parameters
        if (path == null || "".equals(path))
            throw new WCMContentException("Parameter path cannot be null or empty");
        if (newName == null || "".equals(newName))
            throw new WCMContentException("Parameter newName cannot be null or empty");

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for updateContentName() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }


        String newPath = path.substring(0, path.lastIndexOf("/") + 1) + newName;

        try {
            if (jcr.checkPath(newPath))
                throw new WCMContentException("Path: " + newPath + " exists for updateContentName() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            jcr.updatePath(path, newPath);

            WCMObject obj = factory.getContent(newPath);
            return obj;

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#updateBinaryDocument(String, WCMBinaryDocument)}
     */
    public WCMBinaryDocument updateBinaryDocument(String path, WCMBinaryDocument doc) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        log.debug("updateBinaryDocument()");

        // Check null parameters
        if (path == null || "".equals(path))
            throw new WCMContentException("Parameter path cannot be null or empty");
        if (doc == null)
            throw new WCMContentException("Parameter doc cannot be null");
        if (doc.getLocale() == null || "".equals(doc.getLocale()))
            throw new WCMContentException("Parameter doc.getLocale() cannot be null or empty");
        if (doc.getMimeType() == null || "".endsWith(doc.getMimeType()))
            throw new WCMContentException("Parameter doc.getMimeType() cannot be null or empty");
        if (doc.getSize() == 0)
            throw new WCMContentException("Parameter doc.getSize() cannot be 0");
        if (doc.getFileName() == null || "".endsWith(doc.getFileName()))
            throw new WCMContentException("Parameter doc.getFileName() cannot be null or empty");
        if (doc.getContentAsInputStream() == null)
            throw new WCMContentException("Parameter content in InputStream cannot be null");

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for updateBinaryDocument() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Updating existing Node
        try {
            jcr.updateBinaryNode(path, doc);

            WCMObject obj = factory.getContent(path);
            if (obj instanceof WCMBinaryDocument)
                return (WCMBinaryDocument)obj;
            else
                return null;

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#updateCategoryDescription(String, String, String)}
     */
    public WCMCategory updateCategoryDescription(String categoryPath, String locale, String description)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException {

        log.debug("updateCategoryDescription()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WCMContentException("Parameter locale cannot be null or empty");
        }
        if (description == null || "".equals(description)) {
            throw new WCMContentException("Parameter description cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            categoryPath = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath
                        + " doesn't exist for updateCategoryDescription() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }

        // Check if user has rights to access
        if (!jcr.checkUserAdminCategories())
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in categories.");

        try {
            jcr.updateCategory(categoryPath, locale, description);
            return factory.getCategory(categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#updateCategoryPath(String, String)}
     */
    public void updateCategoryPath(String categoryPath, String newParentCategoryPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {

        log.debug("updateCategoryPath()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }

        if (newParentCategoryPath == null || "".equals(newParentCategoryPath)) {
            throw new WCMContentException("Parameter newCategoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            categoryPath = "";
        if ("/".equals(newParentCategoryPath))
            newParentCategoryPath = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath
                        + " doesn't exist for updateCategoryPath() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }

        try {
            if (!jcr.checkCategoryParentPath(newParentCategoryPath))
                throw new WCMContentException("Path: " + newParentCategoryPath + " doesn't exist for updateCategoryPath() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }

        // Check if user has rights to access
        if (!jcr.checkUserAdminCategories())
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in categories. ");

        try {
            String id = categoryPath.substring(categoryPath.lastIndexOf("/") + 1);
            jcr.updateCategoryPath(categoryPath, newParentCategoryPath + "/" + id);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     * @see {@link WCMContentService#putContentCategory(String, String)}
     */
    public void putContentCategory(String path, String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("putContentCategory()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for putContentCategory() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath + " doesn't exist for putContentCategory() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories.");
        }

        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            jcr.jcrCategoryReference(path, categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     * @see {@link WCMContentService#removeContentCategory(String, String)}
     */
    public void removeContentCategory(String path, String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("removeContentCategory()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for removeContentCategory() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath + " doesn't exist for removeContentCategory() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }

        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            jcr.jcrRemoveCategoryReference(path, categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     * @see {@link WCMContentService#updateContentProperty(String, String, String, String)}
     */
    public WCMObject updateContentProperty(String path, String name, String value) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        log.debug("updateContentProperty()");

        // Check null parameteres
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
                throw new WCMContentException("Path: " + path + " doesn't exist for updateContentProperty() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            // Creates or update property
            jcr.createContentProperty(path, name, value);
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#restore(String, String)}
     */
    public void restore(String path, String version) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        log.debug("restore()");

        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }

        if (version == null || "".equals(version)) {
            throw new WCMContentException("Parameter version cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for restore() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {

            // Check current versions
            List<String> versions = jcr.jcrVersions(path);
            // No versions available - we skip
            if (versions == null || versions.size() == 0)
                throw new WCMContentException("Path doesnt have any versions");

            boolean found = false;
            for (String v : versions) {
                if (version.equals(v)) {
                    // Restore content
                    jcr.jcrRestore(path, version);
                    found = true;
                }
            }

            if (!found)
                throw new WCMContentException("Version: " + version + " doesn't found for path: " + path);

        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

}
