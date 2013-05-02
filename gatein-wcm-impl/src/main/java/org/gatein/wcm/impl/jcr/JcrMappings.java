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
package org.gatein.wcm.impl.jcr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.publishing.WCMPublishStatus;
import org.gatein.wcm.api.model.security.WCMAce;
import org.gatein.wcm.api.model.security.WCMAcl;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipal;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.impl.model.WCMConstants;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

/**
 *
 * All JCR low level operations should be placed here.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class JcrMappings {

    private static final Logger log = Logger.getLogger(JcrMappings.class);

    private final String MARK = "__";

    WCMContentFactory factory = null;

    Session jcrSession = null;
    WCMUser logged = null;
    VersionManager vm = null;

    public JcrMappings(Session session, WCMUser user) throws WCMContentIOException {
        try {
            jcrSession = session;
            logged = user;
            vm = jcrSession.getWorkspace().getVersionManager();
        } catch (RepositoryException e) {
            throw new WCMContentIOException("Unexpected error initializating session JCR objects. Msg: " + e.getMessage());
        }
    }

    public WCMContentFactory getFactory() {
        return factory;
    }

    public void setFactory(WCMContentFactory factory) {
        this.factory = factory;
    }

    /**
     * Check if JCR session is valid
     * @return true is session is valid, false otherwise
     */
    public boolean checkSession() {
        if (this.jcrSession == null || this.logged == null)
            return false;
        return true;
    }

    /**
     * Check if path exists and also if it is a nt:folder.
     *
     * @param path - Path to check
     * @return true if path exists and it is a nt:folder
     */
    public boolean checkParentPath(String path) throws AccessDeniedException {

        if (path == null)
            return false;
        if (path.equals("/"))
            return true;
        try {
            if (!jcrSession.itemExists(path))
                return false;
            Node n = jcrSession.getNode(path);
            if (n.getPrimaryNodeType().getName().equals("nt:folder"))
                return true;
            else
                return false;
        } catch (AccessDeniedException expected) {
          // Expected. If I don't have access
            throw expected;
        } catch (RepositoryException e) {
            log.error("Path " + path + " bad specified. Message: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if categoryPath is a valid path to add a new category in JCR
     * @param categoryPath - Category path to check
     * @return true if valid, false otherwise
     */
    public boolean checkCategoryParentPath(String categoryPath) throws AccessDeniedException {
        return checkParentPath("/__categories" + categoryPath);
    }

    /**
     * Check if path exists.
     *
     * @param path - Path to check
     * @return true if path exists false otherwise
     */
    public boolean checkPath(String path) throws AccessDeniedException {
        if (path == null)
            return false;
        if (path.equals("/"))
            return true;
        try {
            return jcrSession.itemExists(path);
        } catch (AccessDeniedException expected) {
          // Expected. If I don't have access
            throw expected;
        } catch (RepositoryException e) {
            log.error("Path " + path + " bad specified. Message: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Check if path exist on category branch
     * @param categoryPath - path to category
     * @return true if path exists, false otherwise
     */
    public boolean checkCategoryPath(String categoryPath) throws AccessDeniedException {
        return checkPath("/__categories" + categoryPath);
    }

    /**
     * Check if a category exists with same locale in JCR
     * @param categoryPath - Parent path
     * @param id - ID of category to check
     * @param locale - locale of category
     * @return true if exists, false otherwise
     */
    public boolean checkCategoryIdExists(String categoryPath, String id, String locale) {
        try {
            String tmpPath = ("".equals(categoryPath) ? "/__categories"  : "/__categories" + categoryPath);
            return jcrSession.itemExists(tmpPath + "/" + id + "/__" + locale);
        } catch (PathNotFoundException e) {
            return false;
        } catch (RepositoryException e) {
            log.error("Unexpected error in path " + categoryPath + " , Id: " + id + " , and Locale: " + locale + " . Message: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Check if and id exists on a path and suggests an alternative name.
     *
     * @param path - Path to find
     * @param id - Id to validate if exists or not
     * @param locale - locale of the item
     * @return null if id doesn't exist id if id exist on path id + "__" + locale if id exist but with different locale
     */
    public String checkIdExists(String path, String id, String locale) {
        try {
            String tmpPath = ("".equals(path) ? "" : path);
            Node root = jcrSession.getNode(tmpPath + "/" + id);
            // Check if we are in the same locale
            if (root.getProperty("jcr:language").getString().equals(locale))
                return id;
            else
                return id + MARK + locale;
        } catch (PathNotFoundException e) {
            return null;
        } catch (RepositoryException e) {
            log.error("Unexpected error in path " + path + "/" + id + "/" + MARK + locale + ". Message: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Check if and id exists on a path.
     *
     * @param path - Path to find
     * @param id - Id to validate if exists or not
     * @return true if id exists
     */
    public boolean checkIdExists(String path, String id) {
        try {
            String tmpPath = ("/".equals(path) ? "" : path);
            return jcrSession.itemExists(tmpPath + "/" + id);
        } catch (RepositoryException e) {
            log.error("Unexpected error in location " + path + "/" + id + ". Message: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if logged user has WRITE rights in the JCR ACL representation
     * @param path - Path to check if user
     * @return true if user has rights, false otherwise
     */
    public boolean checkUserWriteACL(String path) {

        // If user has WCM admin role, then he has full access
        if (logged.hasRole("admin")) return true;

        // Create ACL from path
        WCMAcl acl = null;
        try {
            acl = jcrACL(path);
            // If there are not __acl folder in the location, we will check to the parent node
            if (acl == null && !"/".equals(path))
                return checkUserWriteACL(parent(path));
            if (acl == null && "/".equals("/"))
                return true;
        } catch (RepositoryException e) {
            log.error("Unexpected error looking for acl in path " + path + ". Msg: " + e.getMessage());
            return false;
        }

        // Validate ACL with logged user
        boolean found = false;
        boolean granted = false;
        for (WCMAce ace : acl.getAces()) {
            if (ace.getPrincipal().getType() == WCMPrincipalType.USER) {
                found = ace.getPrincipal().getId().equals(logged.getUserName());
                found = found || ace.getPrincipal().getId().equals("*");
                granted = found && (ace.getPermission() == WCMPermissionType.WRITE) || (ace.getPermission() == WCMPermissionType.ALL);
            } else {
                for (String r : logged.getRoles()) {
                    if (ace.getPrincipal().getId().equals(r) ||
                        ace.getPrincipal().getId().equals("*")) {
                        found = true;
                        granted = (ace.getPermission() == WCMPermissionType.WRITE) || (ace.getPermission() == WCMPermissionType.ALL);
                    }
                }
            }
        }

        if (!found && !"/".equals(path))
            return checkUserWriteACL(parent(path));
        else
            return granted;
    }

    /**
     * Check if logged user has READ rights in the JCR ACL representation.
     * <p>
     * A READ permission allows for read the path or add comments.
     * <p>
     * @param path - Path to check if user
     * @return true if user has rights, false otherwise
     */
    public boolean checkUserReadACL(String path) {

        // If user has WCM admin role, then he has full access
        if (logged.hasRole("admin")) return true;

        // Create ACL from location
        WCMAcl acl = null;
        try {
            acl = jcrACL(path);
            // If there are not __acl folder in the location, we will check to the parent node
            if (acl == null && !"/".equals(path))
                return checkUserReadACL(parent(path));
            if (acl == null && "/".equals("/"))
                return true;
        } catch (RepositoryException e) {
            log.error("Unexpected error looking for acl in location " + path + ". Msg: " + e.getMessage());
            return false;
        }

        // Validate ACL with logged user
        for (WCMAce ace : acl.getAces()) {
            // Check if we have a GROUP ACE
            if (ace.getPrincipal().getType() == WCMPrincipalType.ROLE
                    && Arrays.asList(WCMPermissionType.READ, WCMPermissionType.WRITE,
                            WCMPermissionType.ALL).contains(ace.getPermission())) {
                for (String group : logged.getRoles())
                    if (group.equals(ace.getPrincipal().getId()) || "*".equals(ace.getPrincipal().getId()))
                        return true;
            }
            // Check if we have a USER ACE
            if (ace.getPrincipal().getType() == WCMPrincipalType.USER
                    && (ace.getPrincipal().getId().equals(logged.getUserName()) ||
                        ace.getPrincipal().getId().equals("*"))
                    && Arrays.asList(WCMPermissionType.READ, WCMPermissionType.WRITE,
                            WCMPermissionType.ALL).contains(ace.getPermission()))
                return true;
        }

        boolean found = false;
        boolean granted = false;
        for (WCMAce ace : acl.getAces()) {
            if (ace.getPrincipal().getType() == WCMPrincipalType.USER) {
                found = ace.getPrincipal().getId().equals(logged.getUserName());
                found = found || ace.getPrincipal().getId().equals("*");
                granted = found && ace.getPermission() != WCMPermissionType.NONE;
            } else {
                for (String r : logged.getRoles()) {
                    if (ace.getPrincipal().getId().equals(r) ||
                        ace.getPrincipal().getId().equals("*")) {
                        granted = ace.getPermission() != WCMPermissionType.NONE;
                    }
                }
            }
        }

        // If we don't have rights in this folder we will ask if I have access in the upper folder
        if (!found)
            return checkUserReadACL(parent(path));
        else
            return granted;
    }

    /**
     * Check if user has rights to create/delete categories
     * @return
     */
    public boolean checkUserAdminCategories() {
        return checkUserAdminACL("/__categories");
    }

    /**
     * Check if logged user has ADMIN rights in the JCR ACL representation
     * @param path - Path to check if user
     * @return true if user has rights, false otherwise
     */
    public boolean checkUserAdminACL(String path) {
        return logged.hasRole("admin");
    }

    /**
     * Aux function to maps JCR Exceptions to WCM Exceptions
     * @param e - JCR Exception to map
     * @throws WCMContentException
     * @throws WCMContentIOException
     * @throws WCMContentSecurityException
     */
    public void checkJCRException(RepositoryException e) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException {
        if (e instanceof PathNotFoundException) {
            throw new WCMContentException("Location doesn't found. Msg: " + e.getMessage(), e);
        }
        if (e instanceof ItemExistsException) {
            throw new WCMContentException("Item exists. Msg: " + e.getMessage(), e);
        }
        if (e instanceof NoSuchNodeTypeException) {
            throw new WCMContentException("Trying to write in a different node type. Msg: " + e.getMessage(), e);
        }
        if (e instanceof LockException) {
            throw new WCMContentSecurityException("Trying to write in a lock node. Msg: " + e.getMessage(), e);
        }
        if (e instanceof VersionException) {
            throw new WCMContentSecurityException("Error in versioning. Msg: " + e.getMessage(), e);
        }
        if (e instanceof ConstraintViolationException) {
            throw new WCMContentSecurityException("Unexpected constraint violation. Msg: " + e.getMessage(), e);
        }
        if (e instanceof ValueFormatException) {
            throw new WCMContentException("Wrong value format. Msg: " + e.getMessage(), e);
        }
        if (e instanceof AccessDeniedException) {
            throw new WCMContentSecurityException("Access denied. Msg: " + e.getMessage(), e);
        }
        if (e instanceof ReferentialIntegrityException) {
            throw new WCMContentException("Unexpected referencial integrity. Msg: " + e.getMessage(), e);
        }
        throw new WCMContentIOException("Unexpected repository error. Msg: " + e.getMessage(), e);
    }

    /**
     * Check if a category mapped on JCR has references
     * @param categoryPath
     * @return true if category has references
     */
    public boolean checkCategoryReferences(String categoryPath) {
        try {
            Node n = jcrSession.getNode("/__categories" + categoryPath + "/__references");
            // boolean hasChilds = n.getNodes().hasNext();
            // Debug code
            NodeIterator ni = n.getNodes();
            if (ni.hasNext()) {
                ni.nextNode();
                return true;
            } else
                return false;
        } catch (NoSuchElementException e) {
            return false;
        } catch (Exception e) {
            log.error("Unexpected error loofing for references in Category: " + categoryPath + ". Msg: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Maps a WCMTextDocument into JCR
     * @param id - Id of the content
     * @param locale - locale of the content
     * @param path - Parent path of the content
     * @param content - Value of the content
     * @throws RepositoryException
     */
    public void createTextNode(String id, String locale, String path, Value content) throws RepositoryException {

        String tmpPath = ("/".equals(path) ? "" : path);
        String contentId = tmpPath + "/" + id;

        Node n;

        jcrSession.getNode(path).addNode(id, "nt:file").addNode("jcr:content", "nt:resource").setProperty("jcr:data", content);

        n = jcrSession.getNode(contentId);
        n.addMixin("mix:title");
        n.addMixin("mix:lastModified");
        n.addMixin("mix:shareable");
        n.addMixin("mix:versionable");
        n.addMixin("mix:language");

        // Checking out for add new version of content
        vm.checkout(contentId);

        n.setProperty("jcr:description", "textcontent:" + n.getPath());
        n.setProperty("jcr:language", locale);

        // Saving changes into JCR
        jcrSession.save();

        // Checkin version
        vm.checkin(contentId);
    }

    /**
     * It deletes a node from JCR representing a WCM content.
     * It deletes also its metadata.
     * @param path
     * @return Parent path
     * @throws RepositoryException
     */
    public String deleteNode(String path) throws RepositoryException {

        // Check wcm node type
        boolean versionable = false;
        String description = jcrSession.getNode(path).getProperty("jcr:description").getString();
        String type = description.split(":")[0];
        if ("textcontent".equals(type) || "binarycontent".equals(type))
            versionable = true;

        if (versionable)
            vm.checkout(path);

        // Metadata
        try {
            jcrSession.getNode("/__comments" + path).removeSharedSet();
        } catch (PathNotFoundException expected) {
        }
        try {
            jcrSession.getNode("/__properties" + path).removeSharedSet();
        } catch (PathNotFoundException expected) {
        }
        try {
            jcrSession.getNode("/__acl" + path).removeSharedSet();
        } catch (PathNotFoundException expected) {
        }
        try {
            Node n = jcrSession.getNode("/__relationships" + path);
            jcrRemoveFirstVersionable(n);
            n.removeShare();
            jcrSession.save();
            jcrSession.refresh(true);
        } catch (PathNotFoundException expected) {
            // No comments or properties found - skip
        }
        try {
            Node n = jcrSession.getNode(path);
            n.removeSharedSet();
        } catch (PathNotFoundException expected) {
        } finally {
            // Saving changes into JCR
            jcrSession.save();
            jcrSession.refresh(true);
            try {
                if (versionable)
                    vm.checkin(path);
            } catch (Exception expected) {
                // This is a bug in MODESHAPE when only I have a root version
            }
        }
        // TODO in ModeShape 3.1 and 3.2 we can't delete all versions.
        // We need to wait to an upper version for a clean of all previous version

        return parent(path);
    }

    /**
     * Aux function to remove first versionable child in a JCR tree.
     * <p>
     * This method is useful to delete branch where mix:shareable nodes are participants.
     * <p>
     * @param n - Parent JCR node
     * @throws RepositoryException
     */
    private void jcrRemoveFirstVersionable(Node n) throws RepositoryException {
        NodeIterator ni = n.getNodes();
        while (ni.hasNext()) {
            Node relationship = ni.nextNode();
            if (relationship.isNodeType("mix:shareable")) {
                relationship.removeShare();
                jcrSession.save();
            } else jcrRemoveFirstVersionable(relationship);
        }
    }

    /**
     * Creates a WCM Folder representation into JCR
     * @param id - ID of the folder
     * @param parentPath - Parent path of the JCR where folder will be stored
     * @throws RepositoryException
     */
    public void createFolder(String id, String parentPath) throws RepositoryException {
        Node n = jcrSession.getNode(parentPath).addNode(id, "nt:folder");
        n.addMixin("mix:title");
        n.addMixin("mix:lastModified");
        n.addMixin("mix:shareable");

        n.setProperty("jcr:description", "folder:" + n.getPath());

        // Saving changes into JCR
        jcrSession.save();
    }

    /**
     * Creates a binary node in JCR with a file representing a WCMBinaryDocument
     *
     * @param id - Id of the node
     * @param locale - locale
     * @param path - Parent path
     * @param mimeType
     * @param size
     * @param fileName
     * @param content
     * @throws RepositoryException
     */
    public void createBinaryNode(String id, String locale, String path, String mimeType, long size, String fileName,
            InputStream content) throws RepositoryException {

        String tmpPath = ("/".equals(path) ? "" : path);
        String contentId = tmpPath + "/" + id;

        Node n;

        Binary _content = jcrSession.getValueFactory().createBinary(content);

        jcrSession.getNode(path).addNode(id, "nt:file").addNode("jcr:content", "nt:resource").setProperty("jcr:data", _content);

        n = jcrSession.getNode(contentId);
        n.addMixin("mix:title");
        n.addMixin("mix:lastModified");
        n.addMixin("mix:shareable");
        n.addMixin("mix:versionable");
        n.addMixin("mix:language");
        n.addMixin("mix:mimeType");
        n.getNode("jcr:content").addMixin("mix:title");

        // Checking out for add new version of content
        vm.checkout(contentId);

        n.setProperty("jcr:description", "binarycontent:" + n.getPath());
        n.setProperty("jcr:language", locale);
        n.setProperty("jcr:mimeType", mimeType);
        n.setProperty("jcr:title", fileName);
        n.getNode("jcr:content").setProperty("jcr:description", size);

        // Saving changes into JCR
        jcrSession.save();

        // Checkin version
        vm.checkin(contentId);

        // Dispose binary
        _content.dispose();
    }

    /**
     * Return a direct access for JCR session.
     * @return JCR Session
     */
    public Session getSession() {
        return this.jcrSession;
    }

    /**
     * Returns a Set with relationship keys under a specific path
     *
     * @param path - Path where content is stored
     * @return Set with relationship keys or null if there is not keys.
     * @throws RepositoryException
     */
    public Set<String> getContentRelationKeys(String path) throws RepositoryException {

        String refPath = "/__relationships" + path;
        if (!jcrSession.itemExists(refPath))
            return null;

        Node n = jcrSession.getNode(refPath);

        Set<String> keys = new HashSet<String>();
        NodeIterator ni = n.getNodes();
        while (ni.hasNext()) {
            Node child = ni.nextNode();
            String name = child.getName();
            if (!WCMConstants.RESERVED_ENTRIES.contains(name)) {
                if (name.startsWith("__"))
                    keys.add(name.substring(2));
            }
        }
        if (keys.isEmpty())
            return null;

        return keys;
    }

    /**
     * Update a JCR node representing a WCMTextDocument
     *
     * @param path - Path in the JCR
     * @param locale - Locale to update
     * @param content - Content to update
     * @throws RepositoryException
     */
    public void updateTextNode(String path, String locale, Value content) throws RepositoryException {
        if ("/".equals(path))
            return;

        vm.checkout(path);

        Node n = jcrSession.getNode(path);

        n.setProperty("jcr:language", locale);
        n.getNode("jcr:content").setProperty("jcr:data", content);

        jcrSession.save();
        vm.checkin(path);
    }

    /**
     * Moves a node from JCR and update WCM representation.
     * <p>
     *
     * @param path - Path of the current folder
     * @param newPath - Parent Path
     * @throws RepositoryException
     */
    public void updatePath(String path, String newPath) throws RepositoryException {
        // Root node is not affected
        if ("/".equals(path))
            return;

        Node n = jcrSession.getNode(path);

        // Check if text or binary node
        String description = null;
        boolean checkin = false;
        try {
            if (n.getProperty("jcr:description") != null) {
                description = n.getProperty("jcr:description").getString();
                description = description.split(":")[0];
            }
        } catch (PathNotFoundException e) {
            // This node has not mix:title, so exception ignored
        }
        if (description != null && ("textcontent".contains(description) || "binarycontent".contains(description)))
            checkin = true;

        if (checkin)
            vm.checkout(path);

        jcrSession.move(path, newPath);
        String parentNewPath = parent(newPath);
        // Acl
        if (jcrSession.itemExists("/__acl" + path)) {
            if (!jcrSession.itemExists("/__acl" + parentNewPath))
                jcrCreatePath("/__acl" + parentNewPath);
            jcrSession.move("/__acl" + path, "/__acl" + newPath);
        }
        // Comments
        if (jcrSession.itemExists("/__comments" + path)) {
            if (!jcrSession.itemExists("/__comments" + parentNewPath))
                jcrCreatePath("/__comments" + parentNewPath);
            jcrSession.move("/__comments" + path, "/__comments" + newPath);
        }
        // Properties
        if (jcrSession.itemExists("/__properties" + path)) {
            if (!jcrSession.itemExists("/__properties" + parentNewPath))
                jcrCreatePath("/__properties" + parentNewPath);
            jcrSession.move("/__properties" + path, "/__properties" + newPath);
        }
        // Relationships
        if (jcrSession.itemExists("/__relationships" + path)) {
            if (!jcrSession.itemExists("/__relationships" + parentNewPath))
                jcrCreatePath("/__relationships" + parentNewPath);
            jcrSession.move("/__relationships" + path, "/__relationships" + newPath);
        }
        jcrUpdateDescriptionPath(n);
        jcrSession.save();

        if (checkin)
            vm.checkin(newPath);
    }

    /**
     * Updates path for an existing WCM Category mapped on JCR
     * <p>
     * @param path - Path to source category
     * @param newPath - New parent path to move
     * @throws RepositoryException
     */
    public void updateCategoryPath(String path, String newPath) throws RepositoryException {
        if ("/".equals(path))
            return;

        jcrSession.move("/__categories"+path, "/__categories"+newPath+"/");
        jcrSession.save();
    }

    /**
     * Updates a binary node in JCR with a file representing a WCMBinaryDocument
     * @param path - Absolute path
     * @param doc
     * @throws RepositoryException
     */
    public void updateBinaryNode(String path, WCMBinaryDocument doc) throws RepositoryException {
        // Root node is not affected
        if ("/".equals(path))
            return;

        vm.checkout(path);

        Node n = jcrSession.getNode(path);

        Binary content = jcrSession.getValueFactory().createBinary(doc.getContent());

        n.getNode("jcr:content").setProperty("jcr:data", content);
        n.setProperty("jcr:title", doc.getFileName());
        n.setProperty("jcr:mimeType", doc.getMimeType());
        n.getNode("jcr:content").setProperty("jcr:description", doc.getSize());

        jcrSession.save();
        vm.checkin(path);
    }

    /**
     * Creates a WCM category mapped on JCR
     * @param id - Id of new category
     * @param locale - Locale of category description
     * @param categoryPath - Parent path of category
     * @param description - Description of category
     * @throws RepositoryException
     */
    public void createCategory(String id, String locale, String categoryPath, String description) throws RepositoryException {
        String fullPath = "/__categories" + categoryPath;
        if (!checkIdExists(fullPath, id)) {
            jcrSession.getNode(fullPath).addNode(id, "nt:folder");
            jcrSession.getNode(fullPath + "/" + id).addNode("__references", "nt:folder").addMixin("mix:shareable");
        }
        jcrSession.getNode(fullPath + "/" + id).addNode(MARK + locale, "nt:folder");

        Node n = jcrSession.getNode(fullPath + "/" + id + "/" + MARK + locale);
        n.addMixin("mix:title");
        n.addMixin("mix:lastModified");
        n.setProperty("jcr:description", description);

        jcrSession.save();
    }

    /**
     * Update category in JCR
     * @param categoryPath - Path to category
     * @param locale - Locale to update
     * @param description - Description to update
     * @throws RepositoryException
     */
    public void updateCategory(String categoryPath, String locale, String description) throws RepositoryException {
        Node n = jcrSession.getNode("/__categories" + categoryPath + "/" + MARK + locale);
        n.setProperty("jcr:description", description);
        jcrSession.save();
    }

    /**
     * Deletes a category mapped into JCR
     * @param categoryPath - Path to category
     * @throws RepositoryException
     */
    public void deleteCategory(String categoryPath) throws RepositoryException {
        jcrSession.removeItem("/__categories" + categoryPath);
        jcrSession.save();
    }

    /**
     * Deletes a locale for a category mapped into JCR.
     * <p>
     * Deletes whole category if there is no more locales.
     * <p>
     * @param categoryPath - Path to category
     * @param locale - Locale
     * @throws RepositoryException
     */
    public void deleteCategory(String categoryPath, String locale) throws RepositoryException {

        // Check locales
        Node n = jcrSession.getNode("/__categories" + categoryPath);
        int locales = 0;
        NodeIterator ni = n.getNodes();
        while (ni.hasNext()) {
            Node loc = ni.nextNode();
            if (loc.getName().startsWith("__") && !loc.getName().equals("__references"))
                locales++;
        }
        jcrSession.removeItem("/__categories" + categoryPath + "/" + MARK + locale);
        // If we have only one category, we will delete whole category entry
        if (locales == 1)
            jcrSession.removeItem("/__categories" + categoryPath);
        jcrSession.save();
    }

    /**
     * Create a comment for a content mapped in the JCR.
     * <p>
     * @param path - Path of the content to add a comment
     * @param comment - Comment
     * @throws RepositoryException
     */
    public void createContentComment(String path, String comment) throws RepositoryException {

        String tmpPath = ("/".equals(path) ? "" : path);
        Node n;
        try {
            n = jcrSession.getNode("/__comments" + tmpPath);
        } catch (PathNotFoundException e) {
            n = jcrCreatePath("/__comments" + tmpPath);
        }

        String idComment = "" + new Date().getTime() + "_" + UUID.randomUUID().toString();
        n.addNode(idComment, "nt:folder").addMixin("mix:title");
        n.getNode(idComment).setProperty("jcr:description", comment);
        jcrSession.save();
    }

    /**
     * Creates a property into a mapped JCR.
     * <p>
     * @param path - Path of the content to add a property
     * @param name - Name of the property
     * @param value - String value
     * @throws RepositoryException
     */
    public void createContentProperty(String path, String name, String value) throws RepositoryException {

        String tmpPath = ("/".equals(path) ? "" : path);
        Node n;
        try {
            n = jcrSession.getNode("/__properties" + tmpPath);
        } catch (PathNotFoundException e) {
            n = jcrCreatePath("/__properties" + tmpPath);
        }

        if (!n.hasNode(name)) {
            n = n.addNode(name, "nt:folder");
            n.addMixin("mix:title");
        } else n = n.getNode(name);
        n.setProperty("jcr:description", value);

        jcrSession.save();
    }

    /**
     * Deletes a content's comment previously mapped on JCR.
     * <p>
     * @param path - Path of the content
     * @param idComment
     * @throws RepositoryException
     */
    public void deleteContentComment(String path, String idComment) throws RepositoryException {

        String tmpPath = ("/".equals(path) ? "" : path);

        if (jcrSession.itemExists("/__comments" + tmpPath + "/" + idComment)) {
            jcrSession.removeItem("/__comments" + tmpPath + "/" + idComment);
        }

        jcrSession.save();
    }

    /**
     * Delete a Content's property previously mapped on JCR.
     * <p>
     * @param path - Path of the content
     * @param name - Name of property
     * @throws RepositoryException
     */
    public void deleteContentProperty(String path, String name) throws RepositoryException {

        String tmpPath = ("/".equals(path) ? "" : path);

        if (jcrSession.itemExists("/__properties" + tmpPath + "/" + name)) {
            jcrSession.removeItem("/__properties" + tmpPath + "/" + name);
        }
        jcrSession.save();
    }

    /**
     * Creates an ACE into ACL of a content mapped into JCR.
     * <p>
     * @param path - Path of the content
     * @param principalId - Principal ID of the ACE
     * @param principal - Principal where the ACE affects
     * @param permission - Permission to set up on the given principal
     * @throws RepositoryException
     */
    public void createContentAce(String path, String principalId, WCMPrincipalType principal, WCMPermissionType permission)
            throws RepositoryException {

        String tmpLocation = ("/".equals(path) ? "" : path);
        String contentId = "/__acl" + tmpLocation;

        String acl = null;
        Node n;
        if (!jcrSession.itemExists(contentId)) {
            jcrCreatePath(contentId);
            n = jcrSession.getNode(contentId);
            n.addMixin("mix:title");
        }
        n = jcrSession.getNode(contentId);
        try {
            acl = n.getProperty("jcr:description").getString();
        } catch (PathNotFoundException ignored) {
        }

        acl = addAcl(acl, principalId, principal, permission);
        if (acl != null)
            n.setProperty("jcr:description", acl);

        jcrSession.save();
    }

    /**
     * Adds and ACE to the ACL's String representation mapped into JCR.
     * <p>
     * @param acl - ACL's String representation to add a new entry
     * @param principalId - Principal Id
     * @param principal - Principal type
     * @param permission - Permission type
     * @return String representation ot ACL to map into JCR
     */
    private String addAcl(String acl, String principalId, WCMPrincipalType principal, WCMPermissionType permission) {
        String newAce = "" + principalId + ":";
        if (principal == WCMPrincipalType.USER)
            newAce += "USER:";
        if (principal == WCMPrincipalType.ROLE)
            newAce += "ROLE:";
        if (permission == WCMPermissionType.NONE)
            newAce += "NONE";
        if (permission == WCMPermissionType.ALL)
            newAce += "ALL";
        if (permission == WCMPermissionType.READ)
            newAce += "READ";
        if (permission == WCMPermissionType.WRITE)
            newAce += "WRITE";

        // Check if newAce is in Acl
        if (acl == null || "".equals(acl))
            return newAce;
        if (acl.contains(newAce))
            return acl;
        acl += "," + newAce;
        return acl;
    }


    /**
     * Removes and ACE to the ACL's String representation mapped into JCR.
     * <p>
     * @param acl - ACL's String representation to add a new entry
     * @param principalId - Principal Id
     * @param principal - Principal type
     * @param permission - Permission type
     * @return String representation ot ACL to map into JCR
     */
    private String removeAcl(String acl, String name) {
        if (acl == null)
            return null;
        if ("".equals(acl))
            return "";

        String tempAcl = "";
        String[] aces = acl.split(",");
        for (String ace : aces) {
            String _user = ace.split(":")[0];
            if (!_user.equals(name))
                    tempAcl += ace + ",";
        }
        // Delete last "," character if tempAcl is not ""
        if (tempAcl.length()>1)
            tempAcl = tempAcl.substring(0, tempAcl.length()-1);
        return tempAcl;
    }

    /**
     * Deletes an ACE from a ACL mapped on JCR.
     * <p>
     * @param path - Path of the content
     * @param principalId - Id of the Principal of the ACE
     * @throws RepositoryException
     */
    public void deleteContentAce(String path, String principalId) throws RepositoryException {

        String acl = null;
        String tmpPath = ("/".equals(path) ? "" : path);
        if (!jcrSession.itemExists("/__acl" + tmpPath))
            return;

        Node n = jcrSession.getNode("/__acl" + tmpPath);

        if (n.getProperty("jcr:description") != null)
            acl = n.getProperty("jcr:description").getString();

        acl = removeAcl(acl, principalId);
        if (acl != null) {
            if (!"".equals(acl))
                n.setProperty("jcr:description", acl);
            else
                n.removeShare();
        }
        jcrSession.save();
    }

    /**
     * Creates a JCR Value.
     * <p>
     * @param content - String to mapped into a JCR Value
     * @return JCR Value
     * @throws RepositoryException
     */
    public Value jcrValue(String content) throws RepositoryException {
        return jcrSession.getValueFactory().createValue(content);
    }

    /**
     * Returns a version name for a WCMTextDocument or WCMBinaryDocument mapped on JCR.
     * <p>
     * @param path - Path of the content to get version
     * @return Version name or null if error
     */
    public String jcrVersion(String path) {
        try {
            Version v = jcrSession.getWorkspace().getVersionManager().getBaseVersion(path);
            return v.getName();
        } catch (Exception e) {
            log.error("Unexpected error getting version history of " + path + ". Msg: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns a version name for a WCMTextDocument or WCMBinaryDocument mapped on JCR.
     * <p>
     * @param n - JCR node
     * @return Version name or null if error
     */
    public String jcrVersion(Node n) {
        try {
            return jcrVersion(n.getPath());
        } catch (Exception e) {
            log.error("Unexpected error getting version history of " + n.toString() + ". Msg: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns a JCR a specific version of a Node.
     * @param path - Path to the node
     * @param version - Version to retrieve
     * @return JCR Node
     * @throws RepositoryException
     */
    public Node jcrVersionNode(String path, String version) throws RepositoryException {
        return jcrSession.getWorkspace().getVersionManager().getVersionHistory(path).getVersion(version).getFrozenNode();
    }

    /**
     * JCR mapping to CreatedOn property of WCM objects.
     * <p>
     * @param path - Path to content
     * @return Date
     */
    public Date jcrCreatedOn(String path) {
        try {
            return jcrSession.getNode(path).getProperty("jcr:created").getDate().getTime();
        } catch (PathNotFoundException e) {
            // Expected in root node
            return null;
        } catch (Exception e) {
            log.error("Unexpected error getting jcr:created for " + path + ". Msg: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * JCR mapping to CreatedOn property of WCM objects.
     * <p>
     * @param n - JCR Node
     * @return Date
     */
    public Date jcrCreatedOn(Node n) {
        try {
            return n.getProperty("jcr:created").getDate().getTime();
        } catch (PathNotFoundException e) {
            // Expected in root node
            return null;
        } catch (Exception e) {
            log.error("Unexpected error getting jcr:created for " + n.toString() + ". Msg: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * JCR mapping to LastModifiedOn property of WCM objects.
     * <p>
     * @param path - Path to content
     * @return Date
     */
    public Date jcrLastModifiedOn(String path) {
        try {
            return jcrSession.getNode(path).getProperty("jcr:lastModified").getDate().getTime();
        } catch (Exception e) {
            log.error("Unexpected error getting jcr:lastModified for " + path + ". Msg: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * JCR mapping to LastModifiedOn property of WCM objects.
     * <p>
     * @param n - JCR Node
     * @return Date
     */
    public Date jcrLastModifiedOn(Node n) {
        try {
            return n.getProperty("jcr:lastModified").getDate().getTime();
        } catch (Exception e) {
            log.error("Unexpected error getting jcr:lastModified for " + n.toString() + ". Msg: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parses a ACL representation mapped in the JCR.
     * <p>
     * @param path - Path of the content
     * @return WCMAcl representation
     * @throws RepositoryException
     */
    public WCMAcl jcrACL(String path) throws RepositoryException {
        // Create ACL from path
        WCMAcl acl = null;
        // Check if we are in the root node or child node
        Node n = null;
        String tmpPath = ("/".equals(path) ? "" : path);
        if (jcrSession.itemExists("/__acl" + tmpPath))
            n = jcrSession.getNode("/__acl" + tmpPath);
        else
            return null;

        Property _acl = null;
        try {
            _acl = n.getProperty("jcr:description");
        } catch (PathNotFoundException expected) {
            // Case where I have a left with ACL and some nodes without ACL
            // It should look into the parent
        }
        if (_acl == null)
            return null;
        String __acl = _acl.getString();
        acl = WCMContentFactory.parseACL(path, "ACL for " + path, __acl);
        return acl;
    }

    /**
     * Gets InputStream from Binary object in a nt:file node.
     * <p>
     * @param path - Path to nt:file
     * @return InputStream or null if path doesn't have a nt:file node
     */
    public InputStream jcrBinary(String path) {
        try {
            if (!jcrSession.itemExists(path))
                return null;
            Node n = jcrSession.getNode(path).getNode("jcr:content");
            Binary b = n.getProperty("jcr:data").getBinary();
            InputStream output = b.getStream();
            b.dispose();
            return output;
        } catch (RepositoryException e) {
            log.error("Unexpected error getting jcr:data for " + path + ". Msg: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Creates an paths of folders if the path doesn't exist.
     * <p>
     * @param absPath - Absolute path to create, it can be incomplete.
     * @return Node pointing to new path or null if it coudn't create the new path
     * @throws RepositoryException
     */
    private Node jcrCreatePath(String absPath) throws RepositoryException {
        if (absPath == null)
            return null;
        if ("".equals(absPath))
            return null;
        Node root = jcrSession.getRootNode();
        String[] relPath = absPath.split("/");
        // In a absPath we will start with 1 to about an empty first element
        for (int i = 1; i < relPath.length; i++) {
            if (!root.hasNode(relPath[i])) {
                root = root.addNode(relPath[i], "nt:folder");
            } else
                root = root.getNode(relPath[i]);
        }
        jcrSession.save();
        return root;
    }

    /**
     * Try to delete a version for a specific path.
     * @param path - Path of content
     * @param version - Version to delete
     * @throws RepositoryException
     */
    public void jcrDeleteVersion(String path, String version) throws RepositoryException {

        if (path == null || "".equals(path))
            return;
        if (version == null || "".equals(version))
            return;

        try {
            VersionHistory vh = vm.getVersionHistory(path);
            String baseVersion = vm.getBaseVersion(path).getName();
            // We can delete a version only if it is not the last one or rootVersion
            if (!baseVersion.equals(version) && !"jcr:rootVersion".equals(version))
                vh.removeVersion(version);
        } catch (Exception expected) {
            log.error("Problems to delete a version in ModeShape 3.1. This bug need ModeShape 3.3. Msg: " + expected, expected);
        }
        jcrSession.save();
    }

    /**
     * Returns the WCMPublishStatus mapped in JCR for a specific node.
     * <p>
     * @param n - JCR Node
     * @return WCMPublishStatus
     */
    public WCMPublishStatus jcrPublishStatus(Node n) {
        // TODO to complete
        if (n == null)
            return null;
        return null;
    }

    /**
     * Return a list of WCMPrincipal that are allowed to publish a content.
     * <p>
     * @param n
     * @return
     */
    public List<WCMPrincipal> jcrPublishingRoles(Node n) {
        // TODO to complete
        if (n == null)
            return null;
        return null;
    }

    /**
     * JCR mapping to CreatedBy property of WCM objects.
     * <p>
     * @param path - Path to content
     * @return Date
     */
    public String jcrCreatedBy(String path) {
        try {
            return jcrSession.getNode(path).getProperty("jcr:createdBy").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:createdBy user for path " + path + ". Msg: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * JCR mapping to CreatedBy property of WCM objects.
     * <p>
     * @param n - JCR Node
     * @return Date
     */
    public String jcrCreatedBy(Node n) {
        try {
            return n.getProperty("jcr:createdBy").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:createdBy user for path " + n.toString() + ". Msg: " + e.getMessage(),
                    e);
            return null;
        }
    }

    /**
     * JCR mapping to LastModifiedBy property of WCM objects.
     * <p>
     * @param path - Path to content
     * @return Date
     */
    public String jcrLastModifiedBy(String path) {
        try {
            return jcrSession.getNode(path).getProperty("jcr:lastModifiedBy").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:lastModifiedBy user for path " + path + ". Msg: " + e.getMessage(),
                    e);
            return null;
        }
    }

    /**
     * JCR mapping to LastModifiedBy property of WCM objects.
     * <p>
     * @param n - JCR Node
     * @return Date
     */
    public String jcrLastModifiedBy(Node n) {
        try {
            return n.getProperty("jcr:lastModifiedBy").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:lastModifiedBy user for path " + n.toString() + ". Msg: "
                            + e.getMessage(), e);
            return null;
        }
    }

    /**
     * JCR mapping to WCMTextDocument's content property.
     * <p>
     * @param n - JCR Node
     * @return Date
     */
    public String jcrTextContent(Node n) {
        try {
            return n.getNode("jcr:content").getProperty("jcr:data").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:data user for path " + n.toString() + ". Msg: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * JCR mapping to WCMBinaryDocument's mimeType property.
     * <p>
     * @param n - JCR Node
     * @return Date
     */
    public String jcrMimeType(Node n) {
        try {
            return n.getProperty("jcr:mimeType").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:mimeType user for path " + n.toString() + ". Msg: " + e.getMessage(),
                    e);
            return null;
        }
    }

    /**
     * JCR mapping to WCMBinaryDocument's content property.
     * <p>
     * @param n - JCR Node
     * @return Date
     */
    public InputStream jcrContent(Node n) {
        try {
            return n.getNode("jcr:content").getProperty("jcr:data").getBinary().getStream();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:content user for path " + n.toString() + ". Msg: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns jcr:description value for a JCR node.
     * <p>
     * @param n - JCR node
     * @return - String with jcr:description
     */
    public String jcrDescription(Node n) {
        try {
            return n.getProperty("jcr:description").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:description user for path " + n.toString() + ". Msg: "
                    + e.getMessage());
            return null;
        }
    }

    /**
     * Returns jcr:title value for a JCR node.
     * <p>
     * @param n - JCR node
     * @return - String with jcr:description
     */
    public String jcrTitle(Node n) {
        try {
            return n.getProperty("jcr:title").getString();
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving jcr:title user for path " + n.toString() + ". Msg: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns a map with all locale/descriptions for a mapped WCM Category.
     * <p>
     * @param path
     * @return Map<String, String> with locales/descriptions
     */
    public Map<String, String> jcrCategoryDescription(String path) {
        try {
            Node n = jcrSession.getNode(path);
            HashMap<String, String> descriptions = new HashMap<String, String>();
            NodeIterator ni = n.getNodes();
            while (ni.hasNext()) {
                Node descNode = ni.nextNode();
                if (descNode.getName().startsWith(MARK) && !"__references".equals(descNode.getName())) {
                    String locale = descNode.getName().substring(MARK.length());
                    String description = descNode.getProperty("jcr:description").getString();
                    descriptions.put(locale, description);
                }
            }
            if (!descriptions.isEmpty())
                return descriptions;
        } catch (RepositoryException e) {
            log.error("Unexpected error retrieving categories node for path " + path + ". Msg: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns list of children's path for a category.
     * <p>
     * @param path - Category abs path
     * @return Array with abs paths to children categories
     * @throws RepositoryException
     */
    public String[] jcrChildCategories(String path) throws RepositoryException {
        ArrayList<String> childLocations = new ArrayList<String>();

        Node n = jcrSession.getNode(path);
        NodeIterator ni = n.getNodes();
        while (ni.hasNext()) {
            Node child = ni.nextNode();
            String name = child.getName();
            if (!name.startsWith("__"))
                childLocations.add(child.getPath());
        }
        if (childLocations.size() == 0)
            return null;
        else {
            String[] output = new String[childLocations.size()];
            output = childLocations.toArray(output);
            return output;
        }
    }

    /**
     * Creates a reference between Category -> Content.
     * <p>
     * @param contentPath - Path of the content
     * @param categoryPath - Path of the category
     * @throws RepositoryException
     */
    public void jcrCategoryReference(String contentPath, String categoryPath) throws RepositoryException {
        String id = contentPath.substring(contentPath.lastIndexOf("/") + 1);
        jcrSession.getWorkspace().clone(jcrSession.getWorkspace().getName(), contentPath,
                "/__categories" + categoryPath + "/__references/" + id, false);
    }

    /**
     * Removes a reference between Category -> Content
     * @param contentPath - Path of the content
     * @param categoryPath - Path of the category
     * @throws RepositoryException
     */
    public void jcrRemoveCategoryReference(String contentPath, String categoryPath) throws RepositoryException {
        String id = contentPath.substring(contentPath.lastIndexOf("/") + 1);
        Node n = jcrSession.getNode("/__categories" + categoryPath + "/__references/" + id);
        n.removeShare();
        jcrSession.save();
        jcrSession.refresh(false);
    }

    /**
     * Create a relationship as a reference under /__relationships/<sourcePath>/__<sourceKey>
     *
     * @param sourcePath
     * @param targetPath
     * @param sourceKey
     * @throws RepositoryException
     */
    public void jcrRelationShip(String sourcePath, String targetPath, String sourceKey) throws RepositoryException {
        String refPath = "/__relationships" + sourcePath;
        jcrCreatePath(refPath);
        jcrSession.getWorkspace().clone(jcrSession.getWorkspace().getName(), targetPath, refPath + "/__" + sourceKey, false);
    }

    /**
     * Aux method to work with a Graph representation of the WCM.
     * <p>
     * In the JCR implementation we use shareable nodes for several functionalities.
     * <p>
     * A shareable node has not a notion of "main node", so in a WCM context we store a main path in the description.
     * <p>
     * This method allows to update internally main path for WCM nodes.<p>
     * <p>
     * @param n - Node
     * @throws RepositoryException
     */
    public void jcrUpdateDescriptionPath(Node n) throws RepositoryException {
        if (n == null)
            return;
        if (n.getProperty("jcr:description") != null) {
            String type = n.getProperty("jcr:description").getString().split(":")[0];
            boolean textcontent = "textcontent".equals(type);
            boolean binarycontent = "binarycontent".equals(type);
            boolean folder = "folder".equals(type);
            if (textcontent || binarycontent || folder) {
                n.setProperty("jcr:description", type + ":" + n.getPath());
                jcrSession.save();
                if (folder) {
                    NodeIterator ni = n.getNodes();
                    while (ni.hasNext()) {
                        Node child = ni.nextNode();
                        jcrUpdateDescriptionPath(child);
                    }
                }
            }
        }
    }

    /**
     * Returns jcr:language value for a JCR node.
     * <p>
     * @param n - JCR node
     * @return - String with jcr:description
     */
    public String jcrLocale(Node n) throws RepositoryException {
        return n.getProperty("jcr:language").getString();
    }

    /**
     * Returns a list of versions for a specified content.
     * <p>
     * @param path - Path of the content
     * @return List of versions or null if path specified is not versionable
     * @throws RepositoryException
     */
    public List<String> jcrVersions(String path) throws RepositoryException {

        if (path == null)
            return null;
        if ("".equals(path))
            return null;

        VersionHistory vh = null;
        try {
            vh = vm.getVersionHistory(path);
        } catch (UnsupportedRepositoryOperationException expected) {
            // location is a folder or non versionable element
            return null;
        }

        if (vh != null) {
            List<String> result = new ArrayList<String>();
            VersionIterator vIt = vh.getAllVersions();
            while (vIt.hasNext()) {
                Version v = vIt.nextVersion();
                if (!v.getName().equals("jcr:rootVersion"))
                    result.add(v.getName());
            }
            return result;
        }

        return null;
    }

    /**
     * Restores a specific version for a content mapped in JCR.
     * <p>
     * @param path - Path to content
     * @param version - Version of the
     * @throws RepositoryException
     */
    public void jcrRestore(String path, String version) throws RepositoryException {
        if (path == null || version == null)
            return;
        if ("".equals(path) || "".equals(version))
            return;
        vm.restore(path, version, false);

    }

    /**
     * For WCMBinaryDocument we will map the size of the file into the jcr:content -> jcr:description attribute.
     * <p>
     * @param n - JCR Node
     * @return Size of the file
     * @throws RepositoryException
     */
    public long jcrSize(Node n) throws RepositoryException {
        if (n == null)
            return -1;
        return n.getNode("jcr:content").getProperty("jcr:description").getLong();
    }

    /**
     * Return parent's path
     * @param path
     * @return Parent's path
     */
    public String parent(String path) {

        if (path == null)
            return null;

        if ("/".equals(path))
            return path;

        String[] locs = path.split("/");

        if (locs.length > 2) {
            StringBuffer sb = new StringBuffer(path.length());
            for (int i = 1; i < (locs.length - 1); i++) {
                sb.append("/" + locs[i]);
            }
            return sb.toString();
        } else {
            return "/";
        }
    }
}