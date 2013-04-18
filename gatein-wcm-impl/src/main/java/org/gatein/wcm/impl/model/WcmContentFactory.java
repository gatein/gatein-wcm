package org.gatein.wcm.impl.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gatein.wcm.api.model.content.WcmBinaryObject;
import org.gatein.wcm.api.model.content.WcmFolder;
import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmTextObject;
import org.gatein.wcm.api.model.metadata.WcmCategory;
import org.gatein.wcm.api.model.metadata.WcmComment;
import org.gatein.wcm.api.model.security.WcmAce.PermissionType;
import org.gatein.wcm.api.model.security.WcmAcl;
import org.gatein.wcm.api.model.security.WcmPrincipal;
import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.impl.jcr.JcrMappings;

/**
 *
 * All org.gatein.wcm.model factory methods should be placed here.
 *
 * @author lucas
 *
 */
public class WcmContentFactory {

    WcmUser logged = null;
    JcrMappings jcr = null;

    private final String MARK = "__";

    public WcmContentFactory(JcrMappings jcr, WcmUser user) throws WcmContentIOException {
        logged = user;
        this.jcr = jcr;
    }

    public WcmTextObject createTextContent(String id, String locale, String location, String html) {

        WcmTextObjectImpl c = new WcmTextObjectImpl();

        String tmpLocation = ("/".equals(location)?"":location);
        // String absLocation = tmpLocation + "/" + id + "/" + MARK + locale + "/" + MARK + id;
        // Parent node will have full info about last modified and versioning
        String absLocation = tmpLocation + "/" + id;

        // New document, so new version starting at 1
        c.setVersion(jcr.jcrVersion(absLocation));
        c.setId(id);
        c.setLocale(locale);
        List<String> locales = new ArrayList<String>();
        locales.add(locale);
        c.setLocales(locales);
        c.setParentPath(location);
        c.setPath(absLocation);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        c.setAcl(null);

        c.setCreatedOn(jcr.jcrCreatedOn(absLocation));
        c.setLastModifiedOn(jcr.jcrLastModifiedOn(absLocation));

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        c.setPublishStatus(null);
        c.setPublishingRoles(null);

        c.setCreatedBy(logged);
        c.setLastModifiedBy(logged);

        // By default a new content will not use attached

        c.setLocked(false);

        c.setLockOwner(null);

        // Specific fields for TextContent
        c.setContent(html);

        return c;
    }

    /**
     *
     * GateIn WCM represents a ACL list a String stored into a file called "__acl" in the content location. This file is a
     * String with the following structure:
     *
     * user:[USER|GROUP]:[NONE|READ|COMMENTS|WRITE|ALL],user:[USER|GROUP]:[NONE|READ|COMMENTS|WRITE|ALL], ...
     *
     * @param str
     * @return
     */
    public WcmAcl parseACL(String id, String description, String acl) {
        WcmAclImpl wcmACL = new WcmAclImpl(id, description);
        String[] aces = acl.split(",");
        for (String ace : aces) {
            String user = ace.split(":")[0];
            String type = ace.split(":")[1];
            String permission = ace.split(":")[2];

            WcmPrincipalImpl wcmPrincipal = null;
            WcmAceImpl wcmACE = null;
            if (type.equals("USER"))
                wcmPrincipal = new WcmPrincipalImpl(user, WcmPrincipal.PrincipalType.USER);
            else
                wcmPrincipal = new WcmPrincipalImpl(user, WcmPrincipal.PrincipalType.GROUP);

            if (permission.equals("NONE")) {
                wcmACE = new WcmAceImpl(wcmPrincipal, PermissionType.NONE);
            }
            if (permission.equals("READ")) {
                wcmACE = new WcmAceImpl(wcmPrincipal, PermissionType.READ);
            }
            if (permission.equals("COMMENTS")) {
                wcmACE = new WcmAceImpl(wcmPrincipal, PermissionType.COMMENTS);
            }
            if (permission.equals("WRITE")) {
                wcmACE = new WcmAceImpl(wcmPrincipal, PermissionType.WRITE);
            }
            if (permission.equals("ALL")) {
                wcmACE = new WcmAceImpl(wcmPrincipal, PermissionType.ALL);
            }
            wcmACL.getAces().add(wcmACE);
        }
        return wcmACL;
    }

    public WcmFolder createFolder(String id, String location) {

        WcmFolderImpl f = new WcmFolderImpl();

        String tmpLocation = ("/".equals(location)?"":location);
        String absLocation = tmpLocation + "/" + id;

        f.setId(id);
        // Folders can have multiple locales, so, it will be null.
        f.setLocale(null);
        f.setParentPath(location);
        f.setPath(absLocation);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        f.setAcl(null);

        f.setCreatedOn(jcr.jcrCreatedOn(absLocation));
        f.setLastModifiedOn(jcr.jcrLastModifiedOn(absLocation));

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        f.setPublishStatus(null);
        f.setPublishingRoles(null);

        f.setCreatedBy(logged);
        f.setLastModifiedBy(logged);

        // By default a new content will not use attached

        f.setLocked(false);

        f.setLockOwner(null);

        // Specific fields for Folder
        // New node, so no children at this point
        f.setChildren(null);

        return f;
    }

    public WcmBinaryObject createBinaryContent(String id, String locale, String location, String contentType, long size,
            String fileName, InputStream content) {

        WcmBinaryObjectImpl b = new WcmBinaryObjectImpl();

        String tmpLocation = ("/".equals(location)?"":location);
        // String absLocation = tmpLocation + "/" + id + "/" + MARK + locale + "/" + MARK + id;
        // Parent node will have full info about last modified and versioning
        String absLocation = tmpLocation + "/" + id;

        // New document, so new version starting at 1
        b.setVersion(jcr.jcrVersion(absLocation));
        b.setId(id);
        b.setLocale(locale);
        List<String> locales = new ArrayList<String>();
        locales.add(locale);
        b.setLocales(locales);
        b.setParentPath(location);
        b.setPath(absLocation);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        b.setAcl(null);

        b.setCreatedOn(jcr.jcrCreatedOn(absLocation));
        b.setLastModifiedOn(jcr.jcrLastModifiedOn(absLocation));

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        b.setPublishStatus(null);
        b.setPublishingRoles(null);

        b.setCreatedBy(logged);
        b.setLastModifiedBy(logged);

        // By default a new content will not use attached

        b.setLocked(false);

        b.setLockOwner(null);

        // Specific fields for TextContent
        b.setFileName(fileName);
        b.setSize(size);
        b.setContentType(contentType);

        // Creating the in memory
        // Point to improve in the future

        b.setContent(content);

        return b;
    }

    public WcmObject getContent(String path, String locale) throws RepositoryException {
        // Get root node of search
        Node n = jcr.getSession().getNode(path);

        // Checking reserved words
        if (WcmConstants.RESERVED_ENTRIES.contains(n.getName())) {
            return null;
        }

        // Check if we are in a reference or in the original path
        if (!"/".equals(path)) {
            String description = n.getProperty("jcr:description").getString();
            String absPath = description.split(":")[1];
            n = jcr.getSession().getNode(absPath);
        }
        WcmObject c = convertToContent(n, locale);

        if (c instanceof WcmFolderImpl) {
            WcmFolderImpl f = (WcmFolderImpl) c;
            ArrayList<WcmObject> children = new ArrayList<WcmObject>();
            f.setChildren(children);
            NodeIterator ni = n.getNodes();
            while (ni.hasNext()) {
                Node child = ni.nextNode();
                WcmObject cChild = getContent(child.getPath(), locale);
                if (cChild != null)
                    children.add(cChild);
                updateLocales(f, cChild);
            }
        }

        return c;
    }

    public WcmObject getContent(String path, String locale, String version) throws RepositoryException {

        // Check version
        // Get root node of search
        Node n = jcr.jcrVersionNode(path, version);

        // Checking reserved words
        if (WcmConstants.RESERVED_ENTRIES.contains(n.getName())) {
            return null;
        }

        // Check if we are in a reference or in the original path
        // We are in a versioning node
//        if (!"/".equals(location)) {
//            String description = n.getProperty("jcr:description").getString();
//            String absPath = description.split(":")[1];
//            n = jcr.getSession().getNode(absPath);
//        }
        WcmObject c = convertToContent(n, locale);

        if (c instanceof WcmFolderImpl) {
            WcmFolderImpl f = (WcmFolderImpl) c;
            ArrayList<WcmObject> children = new ArrayList<WcmObject>();
            f.setChildren(children);
            NodeIterator ni = n.getNodes();
            while (ni.hasNext()) {
                Node child = ni.nextNode();
                WcmObject cChild = getContent(child.getPath(), locale);
                if (cChild != null)
                    children.add(cChild);
                updateLocales(f, cChild);
            }
        }

        return c;
    }

    private WcmObject convertToContent(Node n, String locale) throws RepositoryException {

        // Check if we are using some reserved entries in the JCR
        if (n == null || locale == null)
            return null;

        // We have a folder if we don't have any "__*" sub-folder representing locale.
        // We discard specials folders:
        // __acl -> for __acl
        // __wcmstatus -> for Publishing status
        // __wcmroles -> for Publishing roles
        // __comments -> for Comments
        // __categories -> for Categories
        // __properties -> for Properties
        boolean root = false;
        boolean folder = false;
        boolean textcontent = false;
        boolean binarycontent = false;
        // boolean havelocale = false;

        if ("/".equals(n.getPath()))
            root = true;

        String description = null;

        try {
            if (n.getProperty("jcr:description") != null) {
                description = n.getProperty("jcr:description").getString();
                description = description.split(":")[0];
            }
        } catch (PathNotFoundException e) {
            // This node has not mix:title, so exception ignored
        }

        if (description != null && "folder".equals(description))
            folder = true;

        if (description != null && "textcontent".contains(description))
            textcontent = true;

        if (description != null && "binarycontent".contains(description))
            binarycontent = true;

        // Check if the content has the proper locale

        if (textcontent || binarycontent) {
            try {
                n.getNode(MARK + locale + "/" + MARK + n.getName());
            } catch (PathNotFoundException e) {
                return null;
            }
        }

        // Look and convert node to content

        if (root) {
            WcmFolderImpl _folder = new WcmFolderImpl();

            _folder.setId("root");
            // Folders only have locales at properties level
            _folder.setLocale(locale);
            _folder.setLocales(jcr.jcrLocalesProperties(n));
            _folder.setParentPath("/"); // Special case for root
            _folder.setPath("/"); // Special case for root

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _folder.setAcl(jcr.jcrACL(n.getPath()));

            _folder.setCreatedOn(null);
            _folder.setLastModifiedOn(null);

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _folder.setPublishStatus(jcr.jcrPublishStatus(n));
            _folder.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _folder.setCreatedBy(null);
            _folder.setLastModifiedBy(null);

            // By default a folder will not be locked
            // TODO: Set up in future
            _folder.setLocked(false);

            _folder.setLockOwner(null);

            // Specific fields for Folder
            // New node, so no children at this point
            _folder.setChildren(null);

            readComments(_folder);
            readProperties(_folder);

            return _folder;
        }
        if (folder) {
            WcmFolderImpl _folder = new WcmFolderImpl();

            _folder.setId(n.getName());
            // Folders only have locales at properties level
            _folder.setLocale(locale);
            _folder.setLocales(jcr.jcrLocalesProperties(n));

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

            _folder.setCreatedBy(new WcmUserImpl(jcr.jcrCreatedBy(n)));
            _folder.setLastModifiedBy(new WcmUserImpl(jcr.jcrLastModifiedBy(n)));

            // By default a folder will not be locked
            // TODO: Set up in future
            _folder.setLocked(false);

            _folder.setLockOwner(null);

            // Specific fields for Folder
            // Children are calculated outside
            _folder.setChildren(null);

            readComments(_folder);
            readProperties(_folder);

            return _folder;
        }
        if (textcontent) {
            WcmTextObjectImpl _textcontent = new WcmTextObjectImpl();

            // _textcontent.setVersion(jcr.jcrVersion(n.getNode(MARK + locale + "/" + MARK + n.getName())));
            _textcontent.setVersion(jcr.jcrVersion(n));
            _textcontent.setId(n.getName());
            // Folders can have multiple locales, so, it will be null.
            _textcontent.setLocale(locale);
            _textcontent.setLocales(jcr.jcrLocales(n));

            String location = n.getProperty("jcr:description").getString().split(":")[1];
            _textcontent.setParentPath(jcr.parent(location));
            _textcontent.setPath(location);

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _textcontent.setAcl(jcr.jcrACL(n.getPath()));

            _textcontent.setCreatedOn(jcr.jcrCreatedOn(n));
            _textcontent.setLastModifiedOn(jcr.jcrLastModifiedOn(n));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _textcontent.setPublishStatus(jcr.jcrPublishStatus(n));
            _textcontent.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _textcontent.setCreatedBy(new WcmUserImpl(jcr.jcrCreatedBy(n)));
            _textcontent.setLastModifiedBy(new WcmUserImpl(
                    jcr.jcrLastModifiedBy(n)));

            // By default a folder will not be locked
            // TODO: Set up in future
            _textcontent.setLocked(false);

            _textcontent.setLockOwner(null);

            _textcontent.setContent(jcr.jcrTextContent(n.getNode(MARK + locale + "/" + MARK + n.getName())));

            readComments(_textcontent);
            readProperties(_textcontent);

            return _textcontent;
        }
        if (binarycontent) {
            WcmBinaryObjectImpl _binarycontent = new WcmBinaryObjectImpl();

            _binarycontent.setVersion(jcr.jcrVersion(n));
            _binarycontent.setId(n.getName());
            // Folders can have multiple locales, so, it will be null.
            _binarycontent.setLocale(locale);
            _binarycontent.setLocales(jcr.jcrLocales(n));

            String location = n.getProperty("jcr:description").getString().split(":")[1];
            _binarycontent.setParentPath(jcr.parent(location));
            _binarycontent.setPath(location);

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _binarycontent.setAcl(jcr.jcrACL(n.getPath()));

            _binarycontent.setCreatedOn(jcr.jcrCreatedOn(n));
            _binarycontent.setLastModifiedOn(jcr.jcrLastModifiedOn(n));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _binarycontent.setPublishStatus(jcr.jcrPublishStatus(n));
            _binarycontent.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _binarycontent.setCreatedBy(new WcmUserImpl(jcr.jcrCreatedBy(n)));
            _binarycontent.setLastModifiedBy(new WcmUserImpl(jcr.jcrLastModifiedBy(n)));

            // By default a folder will not be locked
            _binarycontent.setLocked(false);

            _binarycontent.setLockOwner(null);

            _binarycontent.setContentType(jcr.jcrContentType(n.getNode(MARK + locale + "/" + MARK + n.getName())));

            Long size = new Long( jcr.jcrDescription( n.getNode(MARK + locale + "/" + MARK + n.getName()) ) );
            _binarycontent.setSize( size );

            _binarycontent.setFileName(jcr.jcrTitle(n.getNode(MARK + locale + "/" + MARK + n.getName())));
            _binarycontent.setContent(jcr.jcrContent(n.getNode(MARK + locale + "/" + MARK + n.getName())));

            readComments(_binarycontent);
            readProperties(_binarycontent);

            return _binarycontent;
        }

        return null;
    }

    private void updateLocales(WcmFolderImpl parent, WcmObject child) {
        if (child == null) return;
        if (parent == null) return;
        if (child.getLocales() == null) return;
        List<String> childrenLocales = child.getLocales();
        for (String locale : childrenLocales) {
            if (parent.getLocales() == null) parent.setLocales(new ArrayList<String>());
            List<String> locales = parent.getLocales();
            if (!locales.contains(locale)) locales.add(locale);
        }
    }

    private void readComments(WcmObject c) {

        String tmpLocation = ("/".equals(c.getParentPath())?"":c.getParentPath());
        String location = "/__comments" + tmpLocation + "/" + c.getId();
        List<WcmComment> comments = null;

        try {
            NodeIterator ni = jcr.getSession().getNode(location).getNodes();
            while (ni.hasNext()) {
                if (comments == null) comments = new ArrayList<WcmComment>();
                Node child = ni.nextNode();
                if (child != null) {
                    WcmCommentImpl comment = new WcmCommentImpl();
                    comment.setId(child.getName());
                    comment.setCreatedBy(new WcmUserImpl(child.getProperty("jcr:createdBy").toString()));
                    comment.setCreatedOn(child.getProperty("jcr:created").getDate().getTime());
                    comment.setComment(child.getProperty("jcr:description").getString());
                    comments.add(comment);
                }
            }
        } catch (RepositoryException ignored) {
        }

        if (comments != null) {
            if (c instanceof WcmObjectImpl)
                ((WcmObjectImpl)c).setComments(comments);
        }
    }

    private void readProperties(WcmObject c) {

        String tmpLocation = ("/".equals(c.getParentPath())?"":c.getParentPath());
        String location = "/__properties" + tmpLocation + "/" + c.getId() + "/" + MARK + c.getLocale();
        Map<String, String> properties = null;

        try {
            NodeIterator ni = jcr.getSession().getNode(location).getNodes();
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
            if (c instanceof WcmObjectImpl)
                ((WcmObjectImpl)c).setProperties(properties);
        }
    }

    public WcmCategory getCategory(String fullLocation, String locale) throws RepositoryException {

        if (fullLocation == null)
            return null;
        if ("/".equals(fullLocation))
            return null;
        // Root entry for
        if ("/__categories".equals(fullLocation))
            return null;

        WcmCategoryImpl cat = new WcmCategoryImpl();

        cat.setId(fullLocation.substring(fullLocation.lastIndexOf("/") + 1));
        cat.setLocale(locale);
        String location = fullLocation.substring("/__categories".length(), fullLocation.lastIndexOf("/"));
        if ("".equals( location )) location = "/"; // Root of categories
        cat.setParentPath( location );
        if ("/".equals(location))
            cat.setPath(cat.getParentPath() + cat.getId());
        else
            cat.setPath(cat.getParentPath() + "/" + cat.getId());
        cat.setDescription(jcr.jcrCategoryDescription(fullLocation, locale));
        String[] childLocations = jcr.jcrChildCategories(fullLocation);
        if (childLocations != null) {
            ArrayList<WcmCategory> childs = new ArrayList<WcmCategory>();
            for (String childLocation : childLocations) {
                WcmCategory child = getCategory(childLocation, locale);
                if (child != null)
                    childs.add(child);
            }
            cat.setChildCategories( childs );
        } else
            cat.setChildCategories(null);

        return cat;
    }

    public List<WcmCategory> getCategories(String fullLocation, String locale) throws RepositoryException {
        ArrayList<WcmCategory> output = new ArrayList<WcmCategory>();

        if ("/__categories".equals(fullLocation)) {
            String[] children = jcr.jcrChildCategories(fullLocation);
            for (String child : children)
                output.add(getCategory(child, locale));
        } else
            output.add(getCategory(fullLocation, locale));

        return output;
    }

    // Query methods
    public void getCategoryContent(WcmCategory c, String filterLocation, String filterLocale, ArrayList<WcmObject> output)
            throws RepositoryException {
        String pathRootCategory;
        if ("/".equals( c.getParentPath() ))
            pathRootCategory = "/__categories" + c.getParentPath() + c.getId() + "/__references";
        else
            pathRootCategory = "/__categories" + c.getParentPath() + "/" + c.getId() + "/__references";
        Node rootCategory = jcr.getSession().getNode( pathRootCategory );
        // References that are in the main category
        // or references that are in the children
        if (rootCategory.hasNodes()) {
            NodeIterator ni = rootCategory.getNodes();
            while (ni.hasNext()) {
                Node n = ni.nextNode();
                WcmObject content = getContent(n.getPath(), filterLocale);
                if (content.getParentPath().startsWith(filterLocation)) {
                    output.add( content );
                    addChildrenContent(output, content);
                }
            }
        } else {
            for (WcmCategory child : c.getChildCategories()) {
                getCategoryContent(child, filterLocation, filterLocale, output);
            }
        }
    }

    private void addChildrenContent(ArrayList<WcmObject> output, WcmObject c) {
        if (c == null) return;
        if (c instanceof WcmFolder) {
            List<WcmObject> children = ((WcmFolder) c).getChildren();
            for (WcmObject cc : children) {
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

}
