package org.gatein.wcm.impl.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.content.Folder;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.metadata.Comment;
import org.gatein.wcm.api.model.metadata.Property;
import org.gatein.wcm.api.model.security.ACE.PermissionType;
import org.gatein.wcm.api.model.security.ACL;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.jboss.logging.Logger;

/**
 *
 * All org.gatein.wcm.model factory methods should be placed here.
 *
 * @author lucas
 *
 */
public class WcmContentFactory {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.model");

    User logged = null;
    JcrMappings jcr = null;

    private final String MARK = "__";

    public WcmContentFactory(JcrMappings jcr, User user) throws ContentIOException {
        logged = user;
        this.jcr = jcr;
    }

    public Content createTextContent(String id, String locale, String location, String html, String encoding) {

        WcmTextContent c = new WcmTextContent();

        String tmpLocation = location;
        if ("/".equals(location))
            tmpLocation = "";

        String absLocation = tmpLocation + "/" + id + "/" + MARK + locale + "/" + MARK + id;

        // New document, so new version starting at 1
        c.setVersion(jcr.jcrVersion(absLocation));
        c.setId(id);
        c.setLocale(locale);
        c.setLocation(location);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        c.setAcl(null);

        c.setCreated(jcr.jcrCreated(absLocation));
        c.setLastModified(jcr.jcrLastModified(absLocation));

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
        c.setEncoding(encoding);

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
    public ACL parseACL(String id, String description, String acl) {
        WcmACL wcmACL = new WcmACL(id, description);
        String[] aces = acl.split(",");
        for (String ace : aces) {
            String user = ace.split(":")[0];
            String type = ace.split(":")[1];
            String permission = ace.split(":")[2];

            WcmPrincipal wcmPrincipal = null;
            WcmACE wcmACE = null;
            if (type.equals("USER"))
                wcmPrincipal = new WcmPrincipal(user, Principal.PrincipalType.USER);
            else
                wcmPrincipal = new WcmPrincipal(user, Principal.PrincipalType.GROUP);

            if (permission.equals("NONE")) {
                wcmACE = new WcmACE(wcmPrincipal, PermissionType.NONE);
            }
            if (permission.equals("READ")) {
                wcmACE = new WcmACE(wcmPrincipal, PermissionType.READ);
            }
            if (permission.equals("COMMENTS")) {
                wcmACE = new WcmACE(wcmPrincipal, PermissionType.COMMENTS);
            }
            if (permission.equals("WRITE")) {
                wcmACE = new WcmACE(wcmPrincipal, PermissionType.WRITE);
            }
            if (permission.equals("ALL")) {
                wcmACE = new WcmACE(wcmPrincipal, PermissionType.ALL);
            }
            wcmACL.getAces().add(wcmACE);
        }
        return wcmACL;
    }

    public Content createFolder(String id, String location) {

        WcmFolder f = new WcmFolder();

        String tmpLocation = location;
        if ("/".equals(location))
            tmpLocation = "";

        String absLocation = tmpLocation + "/" + id;

        // New document, so new version starting at 1
        f.setVersion(jcr.jcrVersion(absLocation));
        f.setId(id);
        // Folders can have multiple locales, so, it will be null.
        f.setLocale(null);
        f.setLocation(location);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        f.setAcl(null);

        f.setCreated(jcr.jcrCreated(absLocation));
        f.setLastModified(jcr.jcrLastModified(absLocation));

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

    public Content createBinaryContent(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content) {

        WcmBinaryContent b = new WcmBinaryContent();

        String tmpLocation = location;
        if ("/".equals(location))
            tmpLocation = "";

        String absLocation = tmpLocation + "/" + id + "/" + MARK + locale + "/" + MARK + id;

        // New document, so new version starting at 1
        b.setVersion(jcr.jcrVersion(absLocation));
        b.setId(id);
        b.setLocale(locale);
        b.setLocation(location);

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        b.setAcl(null);

        b.setCreated(jcr.jcrCreated(absLocation));
        b.setLastModified(jcr.jcrLastModified(absLocation));

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

    public Content getContent(String location, String locale) throws RepositoryException {
        // Get root node of search
        Node n = jcr.getSession().getNode(location);

        Content c = convertToContent(n, locale);

        if (c instanceof WcmFolder) {
            WcmFolder f = (WcmFolder) c;
            ArrayList<Content> children = new ArrayList<Content>();
            f.setChildren(children);
            NodeIterator ni = n.getNodes();
            while (ni.hasNext()) {
                Node child = ni.nextNode();
                Content cChild = getContent(child.getPath(), locale);
                if (cChild != null)
                    children.add(cChild);
            }
        }

        return c;
    }

    private Content convertToContent(Node n, String locale) throws RepositoryException {

        // Check if we are using some reserved entries in the JCR
        if (n == null || locale == null)
            return null;

        if (WcmConstants.RESERVED_ENTRIES.contains(n.getName())) {
            return null;
        }

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
        boolean havelocale = false;

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
            WcmFolder _folder = new WcmFolder();

            _folder.setVersion(0); // Special for root
            _folder.setId("root");
            // Folders can have multiple locales, so, it will be null.
            _folder.setLocale(null);
            _folder.setLocation("/");

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _folder.setAcl(jcr.jcrACL(n));

            _folder.setCreated(null);
            _folder.setLastModified(null);

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
            WcmFolder _folder = new WcmFolder();

            _folder.setVersion(jcr.jcrVersion(n));
            _folder.setId(n.getName());
            // Folders can have multiple locales, so, it will be null.
            _folder.setLocale(null);

            String location = n.getProperty("jcr:description").getString().split(":")[1];
            _folder.setLocation(jcr.parent(location));

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _folder.setAcl(jcr.jcrACL(n));

            _folder.setCreated(jcr.jcrCreated(n));
            _folder.setLastModified(jcr.jcrLastModified(n));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _folder.setPublishStatus(jcr.jcrPublishStatus(n));
            _folder.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _folder.setCreatedBy(new WcmUser(jcr.jcrCreatedBy(n)));
            _folder.setLastModifiedBy(new WcmUser(jcr.jcrLastModifiedBy(n)));

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
            WcmTextContent _textcontent = new WcmTextContent();

            _textcontent.setVersion(jcr.jcrVersion(n.getNode(MARK + locale + "/" + MARK + n.getName())));
            _textcontent.setId(n.getName());
            // Folders can have multiple locales, so, it will be null.
            _textcontent.setLocale(locale);

            String location = n.getProperty("jcr:description").getString().split(":")[1];
            _textcontent.setLocation(jcr.parent(location));

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _textcontent.setAcl(jcr.jcrACL(n));

            _textcontent.setCreated(jcr.jcrCreated(n.getNode(MARK + locale + "/" + MARK + n.getName())));
            _textcontent.setLastModified(jcr.jcrLastModified(n.getNode(MARK + locale + "/" + MARK + n.getName())));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _textcontent.setPublishStatus(jcr.jcrPublishStatus(n));
            _textcontent.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _textcontent.setCreatedBy(new WcmUser(jcr.jcrCreatedBy(n.getNode(MARK + locale + "/" + MARK + n.getName()))));
            _textcontent.setLastModifiedBy(new WcmUser(
                    jcr.jcrLastModifiedBy(n.getNode(MARK + locale + "/" + MARK + n.getName()))));

            // By default a folder will not be locked
            // TODO: Set up in future
            _textcontent.setLocked(false);

            _textcontent.setLockOwner(null);

            _textcontent.setEncoding(jcr.jcrEncoding(n.getNode(MARK + locale + "/" + MARK + n.getName())));

            _textcontent.setContent(jcr.jcrTextContent(n.getNode(MARK + locale + "/" + MARK + n.getName())));

            readComments(_textcontent);
            readProperties(_textcontent);

            return _textcontent;
        }
        if (binarycontent) {
            WcmBinaryContent _binarycontent = new WcmBinaryContent();

            _binarycontent.setVersion(jcr.jcrVersion(n.getNode(MARK + locale + "/" + MARK + n.getName())));
            _binarycontent.setId(n.getName());
            // Folders can have multiple locales, so, it will be null.
            _binarycontent.setLocale(locale);

            String location = n.getProperty("jcr:description").getString().split(":")[1];
            _binarycontent.setLocation(jcr.parent(location));

            // By default a new content will get the ACL of parent parent.
            // A null value means that this content is using ACL of parent folder.
            _binarycontent.setAcl(jcr.jcrACL(n));

            _binarycontent.setCreated(jcr.jcrCreated(n.getNode(MARK + locale + "/" + MARK + n.getName())));
            _binarycontent.setLastModified(jcr.jcrLastModified(n.getNode(MARK + locale + "/" + MARK + n.getName())));

            // By default a new content will get the Publishing status of his parent
            // A null value means that this content is using parent's publishing information
            _binarycontent.setPublishStatus(jcr.jcrPublishStatus(n));
            _binarycontent.setPublishingRoles(jcr.jcrPublishingRoles(n));

            _binarycontent.setCreatedBy(new WcmUser(jcr.jcrCreatedBy(n.getNode(MARK + locale + "/" + MARK + n.getName()))));
            _binarycontent.setLastModifiedBy(new WcmUser(jcr.jcrLastModifiedBy(n.getNode(MARK + locale + "/" + MARK
                    + n.getName()))));

            // By default a folder will not be locked
            // TODO: Set up in future
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
        if (havelocale) {
            log.info("Found node: " + n.getPath() + " but without locale: " + locale);
        }

        return null;
    }

    private void readComments(Content c) {
        String location = c.getLocation() + "/" + c.getId() + "/__comments";
        List<Comment> comments = null;

        try {
            NodeIterator ni = jcr.getSession().getNode(location).getNodes();
            while (ni.hasNext()) {
                if (comments == null) comments = new ArrayList<Comment>();
                Node child = ni.nextNode();
                if (child != null) {
                    WcmComment comment = new WcmComment();
                    comment.setId(child.getName());
                    comment.setCreatedBy(new WcmUser(child.getProperty("jcr:createdBy").toString()));
                    comment.setCreated(child.getProperty("jcr:created").getDate().getTime());
                    comment.setComment(child.getProperty("jcr:description").getString());
                    comments.add(comment);
                }
            }
        } catch (RepositoryException ignored) {
        }

        if (comments != null) {
            if (c instanceof WcmFolder)
                ((WcmFolder)c).setComments(comments);
            if (c instanceof WcmTextContent)
                ((WcmTextContent)c).setComments(comments);
            if (c instanceof WcmBinaryContent)
                ((WcmBinaryContent)c).setComments(comments);
        }
    }

    private void readProperties(Content c) {
        String location = c.getLocation() + "/" + c.getId() + "/__properties";
        List<Property> properties = null;
        try {
            NodeIterator ni = jcr.getSession().getNode(location).getNodes();
            while (ni.hasNext()) {
                if (properties == null) properties = new ArrayList<Property>();
                Node child = ni.nextNode();
                if (child != null) {
                    WcmProperty property = new WcmProperty();
                    property.setName(child.getName());
                    property.setValue(child.getProperty("jcr:description").getString());
                    properties.add(property);
                }
            }
        } catch (RepositoryException ignored) {
        }

        if (properties != null) {
            if (c instanceof WcmFolder)
                ((WcmFolder)c).setProperties(properties);
            if (c instanceof WcmTextContent)
                ((WcmTextContent)c).setProperties(properties);
            if (c instanceof WcmBinaryContent)
                ((WcmBinaryContent)c).setProperties(properties);
        }
    }

    public Category getCategory(String fullLocation, String locale) throws RepositoryException {

        if (fullLocation == null)
            return null;
        if ("/".equals(fullLocation))
            return null;
        // Root entry for
        if ("/__categories".equals(fullLocation))
            return null;

        WcmCategory cat = new WcmCategory();

        cat.setId(fullLocation.substring(fullLocation.lastIndexOf("/") + 1));
        cat.setLocale(locale);
        String location = fullLocation.substring("/__categories".length(), fullLocation.lastIndexOf("/"));
        if ("".equals( location )) location = "/"; // Root of categories
        cat.setLocation( location );
        cat.setDescription(jcr.jcrCategoryDescription(fullLocation, locale));
        String[] childLocations = jcr.jcrChildCategories(fullLocation);
        if (childLocations != null) {
            ArrayList<Category> childs = new ArrayList<Category>();
            for (String childLocation : childLocations) {
                Category child = getCategory(childLocation, locale);
                if (child != null)
                    childs.add(child);
            }
            cat.setChildCategories( childs );
        } else
            cat.setChildCategories(null);

        return cat;
    }

    public List<Category> getCategories(String fullLocation, String locale) throws RepositoryException {
        ArrayList<Category> output = new ArrayList<Category>();

        if ("/__categories".equals(fullLocation)) {
            String[] children = jcr.jcrChildCategories(fullLocation);
            for (String child : children)
                output.add(getCategory(child, locale));
        } else
            output.add(getCategory(fullLocation, locale));

        return output;
    }

    // Query methods
    public void getCategoryContent(Category c, String filterLocation, String filterLocale, ArrayList<Content> output)
            throws RepositoryException {
        String pathRootCategory;
        if ("/".equals( c.getLocation() ))
            pathRootCategory = "/__categories" + c.getLocation() + c.getId() + "/__references";
        else
            pathRootCategory = "/__categories" + c.getLocation() + "/" + c.getId() + "/__references";
        Node rootCategory = jcr.getSession().getNode( pathRootCategory );
        // References that are in the main category
        // or references that are in the children
        if (rootCategory.hasNodes()) {
            NodeIterator ni = rootCategory.getNodes();
            while (ni.hasNext()) {
                Node n = ni.nextNode();
                Content content = getContent(n.getPath(), filterLocale);
                if (content.getLocation().startsWith(filterLocation)) {
                    output.add( content );
                    addChildrenContent(output, content);
                }
            }
        } else {
            for (Category child : c.getChildCategories()) {
                getCategoryContent(child, filterLocation, filterLocale, output);
            }
        }
    }

    private void addChildrenContent(ArrayList<Content> output, Content c) {
        if (c == null) return;
        if (c instanceof Folder) {
            List<Content> children = ((Folder) c).getChildren();
            for (Content cc : children) {
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
