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
package org.gatein.wcm.impl.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMFolder;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.metadata.WCMCategory;
import org.gatein.wcm.api.model.metadata.WCMComment;
import org.gatein.wcm.api.model.security.WCMAcl;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.impl.jcr.JcrMappings;

/**
 *
 * All org.gatein.wcm.model factory methods should be placed here.
 * <p>
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMContentFactory {

    WCMUser logged = null;
    JcrMappings jcr = null;

    public WCMContentFactory(JcrMappings jcr, WCMUser user) throws WCMContentIOException {
        logged = user;
        this.jcr = jcr;
    }

    /**
     *
     * GateIn WCM represents a ACL list a String stored into a file called "__acl" in the content location. This file is a
     * String with the following structure:
     *
     * user:[USER|ROLE]:[NONE|READ|WRITE|ALL],user:[USER|ROLE]:[NONE|READ|WRITE|ALL], ...
     *
     * @param str
     * @return
     */
    public static WCMAcl parseACL(String id, String description, String acl) {
        WCMAclImpl wcmACL = new WCMAclImpl(id, description);
        String[] aces = acl.split(",");
        for (String ace : aces) {
            String user = ace.split(":")[0];
            String type = ace.split(":")[1];
            String permission = ace.split(":")[2];

            WCMPrincipalImpl wcmPrincipal = null;
            WCMAceImpl wcmACE = null;
            if (type.equals("USER"))
                wcmPrincipal = new WCMPrincipalImpl(user, WCMPrincipalType.USER);
            else
                wcmPrincipal = new WCMPrincipalImpl(user, WCMPrincipalType.ROLE);

            if (permission.equals("NONE")) {
                wcmACE = new WCMAceImpl(wcmPrincipal, WCMPermissionType.NONE);
            }
            if (permission.equals("READ")) {
                wcmACE = new WCMAceImpl(wcmPrincipal, WCMPermissionType.READ);
            }
            if (permission.equals("WRITE")) {
                wcmACE = new WCMAceImpl(wcmPrincipal, WCMPermissionType.WRITE);
            }
            if (permission.equals("ALL")) {
                wcmACE = new WCMAceImpl(wcmPrincipal, WCMPermissionType.ALL);
            }
            wcmACL.getAces().add(wcmACE);
        }
        return wcmACL;
    }

    public WCMFolder createFolder(String id, String path) {

        WCMFolderImpl f = new WCMFolderImpl();

        String tmpPath = ("/".equals(path)?"":path);
        String absPath = tmpPath + "/" + id;

        f.setId(id);
        f.setParentPath(path);
        f.setPath(absPath);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        f.setAcl(null);

        f.setCreatedOn(jcr.jcrCreatedOn(absPath));
        f.setLastModifiedOn(jcr.jcrLastModifiedOn(absPath));

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        f.setPublishStatus(null);
        f.setPublishingRoles(null);

        f.setCreatedBy(logged.getUserName());
        f.setLastModifiedBy(logged.getUserName());

        // By default a new content will not use attached

        f.setLocked(false);
        f.setLockOwner(null);

        // Specific fields for Folder
        // New node, so no children at this point
        f.setChildren(null);

        return f;
    }

    public WCMBinaryDocument createBinaryContent(String id, String locale, String path, String mimeType, String encoding, String fileName) {
        WCMBinaryDocumentImpl result = encoding != null ? new WCMTextDocumentImpl() : new WCMBinaryDocumentImpl();
        String tmpPath = ("/".equals(path)?"":path);
        String absPath = tmpPath + "/" + id;

        // New document, so new version starting at 1
        result.setVersion(jcr.jcrVersion(absPath));
        result.setId(id);
        result.setLocale(locale);
        List<String> locales = new ArrayList<String>();
        locales.add(locale);
        result.setParentPath(path);
        result.setPath(absPath);
        result.setEncoding(encoding);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        result.setAcl(null);

        result.setCreatedOn(jcr.jcrCreatedOn(absPath));
        result.setLastModifiedOn(jcr.jcrLastModifiedOn(absPath));

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        result.setPublishStatus(null);
        result.setPublishingRoles(null);

        result.setCreatedBy(logged.getUserName());
        result.setLastModifiedBy(logged.getUserName());

        // By default a new content will not be locked
        result.setLocked(false);
        result.setLockOwner(null);


        result.setFileName(fileName);
        result.setMimeType(mimeType);

        Binary bin = jcr.jcrBinary(absPath);
        // Creating the in memory
        // Point to improve in the future
        try {
            result.setContent(bin.getStream());
            result.setSize(bin.getSize());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        bin.dispose();
        return result;
    }

    /**
     * Maps content from JCR to WCMObject recursively
     *
     * @param path - Path of the JCR to map
     * @return a WCMObject or null if exception
     * @throws RepositoryException
     */
    public WCMObject getContent(String path) throws RepositoryException {
        // Get root node of search
        Node n = jcr.getSession().getNode(path);

        // Checking reserved words
        if (WCMConstants.RESERVED_ENTRIES.contains(n.getName())) {
            return null;
        }

        // Check if we are in a reference or in the original path
        if (!"/".equals(path)) {
            String description = n.getProperty("jcr:description").getString();
            String absPath = description.split(":")[1];
            n = jcr.getSession().getNode(absPath);
        }
        WCMObject c = convertToContent(n);

        if (c instanceof WCMFolderImpl) {
            WCMFolderImpl f = (WCMFolderImpl) c;
            ArrayList<WCMObject> children = new ArrayList<WCMObject>();
            NodeIterator ni = n.getNodes();
            while (ni.hasNext()) {
                Node child = ni.nextNode();
                WCMObject cChild = getContent(child.getPath());
                if (cChild != null)
                    children.add(cChild);
            }
            if (children.size()>0) f.setChildren(children);
        }

        return c;
    }

    /**
     * Maps content from JCR to WCMObject recursively.
     * This content coming from a relationship defined at WCM level.
     *
     * @param path - Path of the JCR to map
     * @return a WCMObject or null if exception
     * @throws RepositoryException
     */
    public WCMObject getContentRelation(String path, String key) throws RepositoryException {

        String linkedPath = "/__relationships" + path + "/__" + key;

        if (!jcr.getSession().itemExists(linkedPath)) return null;

        // Get root node of search
        Node n = jcr.getSession().getNode(linkedPath);

        // Checking reserved words
        if (WCMConstants.RESERVED_ENTRIES.contains(n.getName())) {
            return null;
        }

        // Check if we are in a reference or in the original path
        if (!"/".equals(path)) {
            String description = n.getProperty("jcr:description").getString();
            String absPath = description.split(":")[1];
            n = jcr.getSession().getNode(absPath);
        }
        WCMObject c = convertToContent(n);

        if (c instanceof WCMFolderImpl) {
            WCMFolderImpl f = (WCMFolderImpl) c;
            ArrayList<WCMObject> children = new ArrayList<WCMObject>();
            NodeIterator ni = n.getNodes();
            while (ni.hasNext()) {
                Node child = ni.nextNode();
                WCMObject cChild = getContent(child.getPath());
                if (cChild != null)
                    children.add(cChild);
            }
            if (children.size()>0) f.setChildren(children);
        }

        return c;
    }

    /**
     * Returns list of categories attached to a content
     * @param path - Path of the content
     * @return List of WCMCategory or null if there is not category
     * @throws RepositoryException
     */
    public List<WCMCategory> getContentCategories(String path) throws RepositoryException {

        ArrayList<WCMCategory> list = new ArrayList<WCMCategory>();
        Node n = jcr.getSession().getNode(path);
        NodeIterator ni = n.getSharedSet();
        while (ni.hasNext()) {
            Node o = ni.nextNode();
            String rPath = o.getPath();
            // Check if this is a category
            if (rPath.startsWith("/__categories")) {
                String categoryPath = rPath.substring("/__categories".length(), rPath.indexOf("/__references"));
                WCMCategory cat = getCategory(categoryPath);
                list.add(cat);
            }
        }
        if (!list.isEmpty())
            return list;
        return null;
    }

    private WCMObject convertToContent(Node n) throws RepositoryException {

        if (n == null)
            return null;

        boolean root = false;
        boolean folder = false;

        if ("/".equals(n.getPath()))
            root = true;

        String description = null;

        // In jcr:description we will store the canonical path and type
        // This is useful when we work with categories or relationships
        // and we need to work as a graph instead of a tree.
        try {
            if (n.getProperty("jcr:description") != null) {
                description = n.getProperty("jcr:description").getString();
                description = description.split(":")[0];
            }
        } catch (PathNotFoundException e) {
            // This node has not mix:title, so exception ignored
        }

        // WCM types: we have a close 1to1 with JCR but in some cases we need to get this info in WCM domain
        if (description != null && "folder".equals(description))
            folder = true;


        // Look and convert node to content
        if (root) {
            WCMFolderImpl _folder = new WCMFolderImpl();

            _folder.setId("root");
            _folder.setParentPath("/"); // Special case for root
            _folder.setPath("/"); // Special case for root

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _folder.setAcl(jcr.jcrACL(n.getPath()));

            _folder.setCreatedOn(jcr.jcrCreatedOn(n));
            _folder.setLastModifiedOn(jcr.jcrLastModifiedOn(n));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _folder.setPublishStatus(jcr.jcrPublishStatus(n));
            _folder.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _folder.setCreatedBy(logged.getUserName());
            _folder.setLastModifiedBy(logged.getUserName());

            // By default a folder will not be locked
            _folder.setLocked(false);
            _folder.setLockOwner(null);

            // Children are calculated outside of convertToContent()
            _folder.setChildren(null);

            readComments(_folder);
            readProperties(_folder);

            return _folder;
        }
        if (folder) {
            WCMFolderImpl _folder = new WCMFolderImpl();

            _folder.setId(n.getName());

            String location = n.getProperty("jcr:description").getString().split(":")[1];
            _folder.setParentPath(jcr.parent(location));
            _folder.setPath(location);

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _folder.setAcl(jcr.jcrACL(n.getPath()));

            _folder.setCreatedOn(jcr.jcrCreatedOn(n));
            _folder.setLastModifiedOn(jcr.jcrLastModifiedOn(n));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _folder.setPublishStatus(jcr.jcrPublishStatus(n));
            _folder.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _folder.setCreatedBy(logged.getUserName());
            _folder.setLastModifiedBy(logged.getUserName());

            // By default a folder will not be locked
            _folder.setLocked(false);
            _folder.setLockOwner(null);

            // Children are calculated outside of convertToContent()
            _folder.setChildren(null);

            readComments(_folder);
            readProperties(_folder);

            return _folder;
//        if (textcontent) {
//            WCMTextDocumentImpl _textcontent = new WCMTextDocumentImpl();
//
//            _textcontent.setVersion(jcr.jcrVersion(n));
//            _textcontent.setId(n.getName());
//            _textcontent.setLocale(jcr.jcrLocale(n));
//
//            String path = n.getProperty("jcr:description").getString().split(":")[1];
//            _textcontent.setParentPath(jcr.parent(path));
//            _textcontent.setPath(path);
//
//            // By default a new content will get the ACL of parent parent.
//            // A null value means that this content is using ACL of parent folder.
//            _textcontent.setAcl(jcr.jcrACL(n.getPath()));
//
//            _textcontent.setCreatedOn(jcr.jcrCreatedOn(n));
//            _textcontent.setLastModifiedOn(jcr.jcrLastModifiedOn(n));
//
//            // By default a new content will get the Publishing status of his parent
//            // A null value means that this content is using parent's publishing information
//            _textcontent.setPublishStatus(jcr.jcrPublishStatus(n));
//            _textcontent.setPublishingRoles(jcr.jcrPublishingRoles(n));
//
//            _textcontent.setCreatedBy(logged.getUserName());
//            _textcontent.setLastModifiedBy(logged.getUserName());
//
//            // By default a folder will not be locked
//            _textcontent.setLocked(false);
//            _textcontent.setLockOwner(null);
//
//            _textcontent.setContent(jcr.jcrTextContent(n));
//
//            readComments(_textcontent);
//            readProperties(_textcontent);
//
//            return _textcontent;
//        }
        } else {

            Node ntResource = n.getNode("jcr:content");
            String encoding = ntResource.hasProperty("jcr:encoding") ? ntResource.getProperty("jcr:encoding").getString() : null;

            WCMBinaryDocumentImpl _binarycontent = encoding != null ? new WCMTextDocumentImpl() : new WCMBinaryDocumentImpl();

            _binarycontent.setVersion(jcr.jcrVersion(n));
            _binarycontent.setId(n.getName());
            _binarycontent.setLocale(jcr.jcrLocale(n));

            String path = n.getProperty("jcr:description").getString().split(":")[1];
            _binarycontent.setParentPath(jcr.parent(path));
            _binarycontent.setPath(path);

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _binarycontent.setAcl(jcr.jcrACL(n.getPath()));

            _binarycontent.setCreatedOn(jcr.jcrCreatedOn(n));
            _binarycontent.setLastModifiedOn(jcr.jcrLastModifiedOn(n));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _binarycontent.setPublishStatus(jcr.jcrPublishStatus(n));
            _binarycontent.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _binarycontent.setCreatedBy(jcr.jcrCreatedBy(n));
            _binarycontent.setLastModifiedBy(jcr.jcrLastModifiedBy(n));

            // By default a folder will not be locked
            _binarycontent.setLocked(false);
            _binarycontent.setLockOwner(null);

            _binarycontent.setMimeType(jcr.jcrMimeType(n));
            _binarycontent.setSize(jcr.jcrSize(n));
            _binarycontent.setEncoding(encoding);
            _binarycontent.setFileName(jcr.jcrTitle(n));
            _binarycontent.setContent(jcr.jcrContent(n));

            readComments(_binarycontent);
            readProperties(_binarycontent);

            return _binarycontent;
        }
    }

    /**
     * Read comments from JCR and update WCMObject
     * @param c - WCMObject to update with properties from JCR
     */
    private void readComments(WCMObject c) {

        String tmpPath = ("/".equals(c.getParentPath())?"":c.getParentPath());
        String path = "/__comments" + tmpPath + "/" + c.getId();
        List<WCMComment> comments = null;

        try {
            NodeIterator ni = jcr.getSession().getNode(path).getNodes();
            while (ni.hasNext()) {
                if (comments == null) comments = new ArrayList<WCMComment>();
                Node child = ni.nextNode();
                if (child != null) {
                    WCMCommentImpl comment = new WCMCommentImpl();
                    comment.setId(child.getName());
                    comment.setCreatedBy(new WCMUserImpl(child.getProperty("jcr:createdBy").toString()));
                    comment.setCreatedOn(child.getProperty("jcr:created").getDate().getTime());
                    comment.setComment(child.getProperty("jcr:description").getString());
                    comments.add(comment);
                }
            }
        } catch (RepositoryException ignored) {
        }

        if (comments != null) {
            if (c instanceof WCMObjectImpl)
                ((WCMObjectImpl)c).setComments(comments);
        }
    }

    /**
     * Read properties from JCR and update WCMObject
     * @param c - WCMObject to update with properties
     */
    private void readProperties(WCMObject c) {

        String tmpPath = ("/".equals(c.getParentPath())?"":c.getParentPath());
        String path = "/__properties" + tmpPath + "/" + c.getId();
        Map<String, String> properties = null;

        try {
            NodeIterator ni = jcr.getSession().getNode(path).getNodes();
            while (ni.hasNext()) {
                if (properties == null) properties = new HashMap<String, String>();
                Node child = ni.nextNode();
                if (child != null) {
                    properties.put(child.getName(), child.getProperty("jcr:description").getString());
                }
            }
        } catch (RepositoryException ignored) {
        }

        if (properties != null) {
            if (c instanceof WCMObjectImpl)
                ((WCMObjectImpl)c).setProperties(properties);
        }
    }

    /**
     * Maps a JCR category into a WCMCategory object
     * @param categoryPath - Path to category
     * @param locale - Locale to map
     * @return WCMCategory or null if error
     * @throws RepositoryException
     */
    public WCMCategory getCategory(String categoryPath) throws RepositoryException {
        if (categoryPath == null)
            return null;
        if ("/".equals(categoryPath))
            return null;

        if ("".equals(categoryPath))
            return null;

        String fullPath = null;
        if (categoryPath.startsWith("/__categories"))
            fullPath = categoryPath;
        else
            fullPath = "/__categories" + categoryPath;

        // Root entry for
        WCMCategoryImpl cat = new WCMCategoryImpl();
        cat.setId(fullPath.substring(fullPath.lastIndexOf("/") + 1));

        String location = fullPath.substring("/__categories".length(), fullPath.lastIndexOf("/"));
        if ("".equals( location )) location = "/"; // Root of categories
        cat.setParentPath( location );
        if ("/".equals(location))
            cat.setPath(cat.getParentPath() + cat.getId());
        else
            cat.setPath(cat.getParentPath() + "/" + cat.getId());
        cat.setDescription(jcr.jcrCategoryDescription(fullPath));
        String[] childLocations = jcr.jcrChildCategories(fullPath);
        if (childLocations != null) {
            ArrayList<WCMCategory> childs = new ArrayList<WCMCategory>();
            for (String childLocation : childLocations) {
                WCMCategory child = getCategory(childLocation);
                if (child != null)
                    childs.add(child);
            }
            cat.setChildCategories( childs );
        } else
            cat.setChildCategories(null);

        return cat;
    }

    /**
     * Retrieves a list of WCMCategory from JCR
     * @param categoryPath - Path where to start to find categories.
     * @param locale - Locale of category
     * @return List of categories
     * @throws RepositoryException
     */
    public List<WCMCategory> getCategories(String categoryPath) throws RepositoryException {
        ArrayList<WCMCategory> output = new ArrayList<WCMCategory>();

        String fullPath = "/__categories" + categoryPath;
        String[] children = jcr.jcrChildCategories(fullPath);
        for (String child : children)
            output.add(getCategory(child));

        return output;
    }

    /**
     * Get all content attached to a category under a specific path
     * @param filterCategory - Category to filter
     * @param filterPath - Path to start searching content
     * @param output - Matched objects
     * @throws RepositoryException
     */
    public void getCategoryContent(WCMCategory filterCategory, String filterPath, ArrayList<WCMObject> output)
            throws RepositoryException {
        String pathRootCategory;
        if ("/".equals( filterCategory.getParentPath() ))
            pathRootCategory = "/__categories" + filterCategory.getParentPath() + filterCategory.getId() + "/__references";
        else
            pathRootCategory = "/__categories" + filterCategory.getParentPath() + "/" + filterCategory.getId() + "/__references";
        Node rootCategory = jcr.getSession().getNode( pathRootCategory );
        // References that are in the main category
        // or references that are in the children
        if (rootCategory.hasNodes()) {
            NodeIterator ni = rootCategory.getNodes();
            while (ni.hasNext()) {
                Node n = ni.nextNode();
                WCMObject content = getContent(n.getPath());
                if (content.getParentPath().startsWith(filterPath)) {
                    output.add( content );
                    addChildrenContent(output, content);
                }
            }
        } else {
            for (WCMCategory child : filterCategory.getChildCategories()) {
                getCategoryContent(child, filterPath, output);
            }
        }
    }

    private void addChildrenContent(ArrayList<WCMObject> output, WCMObject c) {
        if (c == null) return;
        if (c instanceof WCMFolder) {
            List<WCMObject> children = ((WCMFolder) c).getChildren();
            for (WCMObject cc : children) {
                output.add( cc );
                addChildrenContent( output, cc );
            }
        }
    }

    public String parent(String location) {
        if (location == null)
            return null;
        if ("/".equals(location))
            return null;

        // Return without "/" at the end
        return location.substring(0, location.lastIndexOf("/"));
    }

    public static WCMUser createUserInstance(String userName, String[] roles) {
        return new WCMUserImpl(userName, roles);
    }

}
