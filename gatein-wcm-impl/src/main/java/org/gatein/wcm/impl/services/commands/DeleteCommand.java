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

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

/**
 * All delete* methods for WCM Content should be placed here.
 * <p>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class DeleteCommand {

    private static final Logger log = Logger.getLogger(DeleteCommand.class);

    Session jcrSession = null;
    WCMUser logged = null;
    WCMContentFactory factory = null;
    JcrMappings jcr = null;

    public DeleteCommand(Session session, WCMUser user) throws WCMContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WCMContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     * @see {@link WCMContentService#deleteContent(String)}
     */
    public String deleteContent(String path) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {

        log.debug("deleteContent()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(path))
            throw new WCMContentException("Root path cannot be deteled by API");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for deleteContent() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in location: "
                    + path);

        // Delete a node
        try {
            String parent = jcr.deleteNode(path);
            return parent;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     * @see {@link WCMContentService#deleteContentVersion(String, String)}
     */
    public String deleteContentVersion(String path, String version) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {

        log.debug("deleteContentVersion()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (version == null || "".equals(version)) {
            throw new WCMContentException("Parameter locale cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(path))
            throw new WCMContentException("Root path cannot be deteled by API");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for deleteContentVersion() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        // Delete a node
        try {
            jcr.jcrDeleteVersion(path, version);
            return path;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
        return null;
    }

    /**
     * @see {@link WCMContentService#deleteCategory(String)}
     */
    public void deleteCategory(String categoryPath) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        log.debug("deleteCategory()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryLocation cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            throw new WCMContentException("Cannot delete root categories");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath + " doesn't exist for deleteCategory() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }


        if (jcr.checkCategoryReferences(categoryPath))
            throw new WCMContentException("Category in path: " + categoryPath + " has references.");

        // Check if user has rights to access
        if (!jcr.checkUserAdminCategories())
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in categories");

        try {
            jcr.deleteCategory(categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     * @see {@link WCMContentService#deleteCategory(String, String)}
     */
    public void deleteCategory(String categoryPath, String locale) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("deleteCategory()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }
        if (locale == null || "".equals(locale)) {
            throw new WCMContentException("Parameter locale cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            throw new WCMContentException("Cannot delete root categories");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath
                        + " doesn't exist for deleteCategory() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }

        if (jcr.checkCategoryReferences(categoryPath))
            throw new WCMContentException("Category in location: " + categoryPath + " has references.");

        // Check if user has rights to access
        if (!jcr.checkUserAdminCategories())
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not ADMIN rights in categories.");

        try {
            jcr.deleteCategory(categoryPath, locale);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }
    }

    /**
     * @see {@link WCMContentService#deleteCategory(String, String)}
     */
    public WCMObject deleteContentComment(String path, String idComment) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        log.debug("deleteContentComment()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (idComment == null || "".equals(idComment)) {
            throw new WCMContentException("Parameter comment cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for deleteContentComment() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            jcr.deleteContentComment(path, idComment);
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#deleteContentProperty(String, String)}
     */
    public WCMObject deleteContentProperty(String path, String name) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {
        log.debug("deleteContentProperty()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (name == null || "".equals(name)) {
            throw new WCMContentException("Parameter name cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for deleteContentProperty() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            jcr.deleteContentProperty(path, name);
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#deleteContentAce(String, String)}
     */
    public WCMObject deleteContentAce(String path, String principalId) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (principalId == null || "".equals(principalId)) {
            throw new WCMContentException("Parameter principalId cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for deleteContentAce() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access - delete operations must be performed by an admin
        if (!jcr.checkUserWriteACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not WRITE rights in path: "
                    + path);

        try {
            jcr.deleteContentAce(path, principalId);
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

}
