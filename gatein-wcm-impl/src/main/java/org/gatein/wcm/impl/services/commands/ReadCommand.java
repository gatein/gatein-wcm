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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gatein.wcm.api.model.content.WCMObject;
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
 * All read* methods for WCM Content should be placed here.
 * <p>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class ReadCommand {

    private static final Logger log = Logger.getLogger(ReadCommand.class);

    Session jcrSession = null;
    WCMUser logged = null;
    WCMContentFactory factory = null;
    JcrMappings jcr = null;

    public ReadCommand(Session session, WCMUser user) throws WCMContentIOException {
        jcrSession = session;
        logged = user;
        jcr = new JcrMappings(jcrSession, logged);
        factory = new WCMContentFactory(jcr, logged);
        jcr.setFactory(factory);
    }

    /**
     * @see {@link WCMContentService#getContent(String)}
     */
    public WCMObject getContent(String path) throws WCMContentException, WCMContentIOException, WCMContentSecurityException {
        log.debug("getContent()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for getContent() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Retrieving Content
        try {
            return factory.getContent(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#getContent(String, String)}
     */
    public WCMObject getContent(String path, String key) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("getContent()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }
        if (key == null || "".equals(key)) {
            throw new WCMContentException("Parameter key cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for getContent() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        // Check if user has rights to access
        if (!jcr.checkUserReadACL(path))
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);

        // Retrieving Content
        try {
            return factory.getContentRelation(path, key);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#getContentRelationKeys(String)}
     */
    public Set<String> getContentRelationKeys(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        log.debug("getContentRelationKeys()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for getContentRelationKeys() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        try {
            return jcr.getContentRelationKeys(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#getCategories(String, String)}
     */
    public List<WCMCategory> getCategories(String categoryPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {

        log.debug("getCategories()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            categoryPath = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath + " doesn't exist for getCategories() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }

        try {
            return factory.getCategories(categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#getCategories(String, String)}
     */
    public WCMCategory getCategory(String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {

        log.debug("getCategories()");

        // Check null parameters
        if (categoryPath == null || "".equals(categoryPath)) {
            throw new WCMContentException("Parameter categoryPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        if ("/".equals(categoryPath))
            categoryPath = "";

        // Check if the location specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkCategoryPath(categoryPath))
                throw new WCMContentException("Path: " + categoryPath + " doesn't exist for getCategories() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in categories. ");
        }

        try {
            return factory.getCategory(categoryPath);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#getContentCategories(String)}
     */
    public List<WCMCategory> getContentCategories(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {

        log.debug("getContentCategories()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for getContentCategories() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        try {
            List<WCMCategory> result = factory.getContentCategories(path);
            return result;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#getContent(List, String)}
     */
    public List<WCMObject> getContent(List<WCMCategory> categories, String filterPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException {

        log.debug("getContent()");

        // Check null parameters
        if (categories == null || categories.size() == 0) {
            throw new WCMContentException("Parameter categories cannot be null or empty");
        }
        if (filterPath == null || "".equals(filterPath)) {
            throw new WCMContentException("Parameter filterPath cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(filterPath))
                throw new WCMContentException("Path: " + filterPath + " doesn't exist for getContent() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + filterPath);
        }

        try {
            ArrayList<WCMObject> result = new ArrayList<WCMObject>();
            for (WCMCategory c : categories) {
                factory.getCategoryContent(c, filterPath, result);
            }
            return result;
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }

    /**
     * @see {@link WCMContentService#getVersions(String)}
     */
    public List<String> getVersions(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {

        log.debug("getVersions()");

        // Check null parameters
        if (path == null || "".equals(path)) {
            throw new WCMContentException("Parameter path cannot be null or empty");
        }

        // Check if the current JCR Session is valid
        if (!jcr.checkSession())
            throw new WCMContentIOException("JCR Session is null");

        // Check if the path specified exists in the JCR Repository/Workspace
        try {
            if (!jcr.checkPath(path))
                throw new WCMContentException("Path: " + path + " doesn't exist for getVersions() operation. ");
        } catch (AccessDeniedException e) {
            throw new WCMContentSecurityException("User: " + logged.getUserName() + " has not READ rights in path: " + path);
        }

        try {
            return jcr.jcrVersions(path);
        } catch (RepositoryException e) {
            jcr.checkJCRException(e);
        }

        return null;
    }
}
