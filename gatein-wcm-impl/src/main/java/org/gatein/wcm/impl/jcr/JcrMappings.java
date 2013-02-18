package org.gatein.wcm.impl.jcr;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;

import org.gatein.wcm.api.model.security.ACE;
import org.gatein.wcm.api.model.security.ACE.PermissionType;
import org.gatein.wcm.api.model.security.ACL;
import org.gatein.wcm.api.model.security.Principal.PrincipalType;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

/**
 *
 * All JCR low level operations should be placed here.
 *
 * @author lucas
 *
 */
public class JcrMappings {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.jcr");

    private final String MARK = "__";

    WCMContentFactory factory = null;

    Session jcrSession = null;
    User logged = null;
    VersionManager vm = null;

    public JcrMappings(Session session, User user)
            throws ContentIOException
    {
        try {
            jcrSession = session;
            logged = user;
            vm = jcrSession.getWorkspace().getVersionManager();
        } catch (RepositoryException e) {
            throw new ContentIOException ("Unexpected error initializating session JCR objects. Msg: " + e.getMessage());
        }
    }

    public WCMContentFactory getFactory() {
        return factory;
    }

    public void setFactory(WCMContentFactory factory) {
        this.factory = factory;
    }

    public boolean checkSession() {
        if (this.jcrSession == null || this.logged == null)
            return false;
        return true;
    }

    public boolean checkLocation(String location) {

        if (location == null) return false;
        if (location.equals("/")) return true;

        try {
            return jcrSession.nodeExists(location);
        } catch (RepositoryException e) {
            log.error("Location " + location + " bad specified. Message: " + e.getMessage());
        }
        return false;
    }

    public boolean checkIdExists(String location, String id, String locale) {
        try {
            Node root = jcrSession.getNode(location + "/" + id + "/" + MARK + locale);
            if (root.getPrimaryNodeType().getName().equals("nt:folder"))
                return true;
        } catch (PathNotFoundException e) {
            return false;
        } catch (RepositoryException e) {
            log.error("Unexpected error in location " + location + "/" + id + "/" + MARK + locale + ". Message: " + e.getMessage());
        }
        return false;
    }

    public boolean checkIdExists(String location, String id) {
        try {
            Node root = jcrSession.getNode(location + "/" + id);
            if (root.getPrimaryNodeType().getName().equals("nt:folder"))
                return true;
        } catch (PathNotFoundException e) {
            return false;
        } catch (RepositoryException e) {
            log.error("Unexpected error in location " + location + "/" + id + ". Message: " + e.getMessage());
        }
        return false;
    }

    public boolean checkUserACL(String location) {

        // Create ACL from location
        ACL acl = null;
        try {
            // Check if we are in the root node or child node
            Node n = null;
            if (location.equals("/")) {
                n = jcrSession.getNode("/__acl");
            } else {
                n = jcrSession.getNode(location + "/" + MARK + "acl");
            }

            String __acl = n.getNode("jcr:content").getProperty("jcr:data").getString();
            acl = factory.parseACL(location, "ACL for " + location, __acl);

        } catch (PathNotFoundException e) {
            // If there are not __acl folder in the location, we will check to the parent node
            if ( ! location.equals("/") ) {
                return checkUserACL( parent(location) );
            } else {
                // If root node has not __acl folder means that there are not security in this repository
                return true;
            }
        } catch (RepositoryException e) {
            log.error("Unexpected error looking for acl in location " + location + ". Msg: " + e.getMessage());
            return false;
        }

        // Validate ACL with logged user
        for (ACE ace : acl.getAces()) {
            // Check if we have a GROUP ACE
            if ( ace.getPrincipal().getType() == PrincipalType.GROUP &&
                 Arrays.asList( PermissionType.WRITE, PermissionType.ALL ).contains( ace.getPermission() )) {
                for ( String group : logged.getGroups() )
                    if ( group.equals( ace.getPrincipal().getId() ) )
                        return true;
            }
            // Check if we have a USER ACE
            if ( ace.getPrincipal().getType() == PrincipalType.USER &&
                 ace.getPrincipal().getId().equals( logged.getUserName() ) &&
                 Arrays.asList( PermissionType.WRITE, PermissionType.ALL ).contains( ace.getPermission() ) )
                return true;
        }

        return false;
    }

    public void checkJCRException(RepositoryException e)
        throws ContentException, ContentIOException, ContentSecurityException
    {
        if (e instanceof PathNotFoundException) {
            throw new ContentException("Location doesn't found. Msg: " + e.getMessage());
        }
        if (e instanceof ItemExistsException) {
            throw new ContentException("Item exists. Msg: " + e.getMessage());
        }
        if (e instanceof NoSuchNodeTypeException) {
            throw new ContentException("Trying to write in a different node type. Msg: " + e.getMessage());
        }
        if (e instanceof LockException) {
            throw new ContentSecurityException("Trying to write in a lock node. Msg: " + e.getMessage());
        }
        if (e instanceof VersionException) {
            throw new ContentSecurityException("Error in versioning. Msg: " + e.getMessage());
        }
        if (e instanceof ConstraintViolationException) {
            throw new ContentSecurityException("Unexpected constraint violation. Msg: " + e.getMessage());
        }
        if (e instanceof ValueFormatException) {
            throw new ContentException("Wrong value format. Msg: " + e.getMessage());
        }
        if (e instanceof AccessDeniedException) {
            throw new ContentSecurityException("Access denied. Msg: " + e.getMessage());
        }
        if (e instanceof ReferentialIntegrityException) {
            throw new ContentException("Unexpected referencial integrity. Msg: " + e.getMessage());
        }
        throw new ContentIOException("Unexpected repository error. Msg: " + e.getMessage());
    }

    public void createTextNode(String id, String locale, String location, Value content)
        throws RepositoryException
    {
        if (! checkIdExists(location, id)) {
            jcrSession.getNode(location).addNode(id, "nt:folder");
        }

        jcrSession.getNode(location + "/" + id)
            .addNode(MARK + locale, "nt:folder")
            .addNode(MARK + id, "nt:file")
            .addNode("jcr:content", "nt:resource");

        Node n = jcrSession.getNode(location + "/" + id);
        n.addMixin("mix:lastModified");


        n = jcrSession.getNode(location + "/" + id + "/" + MARK + locale + "/" + MARK + id);
        n.addMixin("mix:title");
        n.addMixin("mix:versionable");
        n.addMixin("mix:lastModified");

        // Adding properties
        n.getNode("jcr:content").setProperty("jcr:data", content);
        n.setProperty("jcr:description", content.getString());

        // Saving changes into JCR
        jcrSession.save();
    }

    public String deleteNode(String location)
        throws RepositoryException
    {
        jcrSession.removeItem(location);

        // Saving changes into JCR
        jcrSession.save();

        return parent(location);
    }

    public void createFolder(String id, String location)
        throws RepositoryException
    {
        Node n = jcrSession.getNode( location ).addNode(id, "nt:folder");
        n.addMixin("mix:title");
        n.addMixin("mix:versionable");
        n.addMixin("mix:lastModified");

        // Saving changes into JCR
        jcrSession.save();
    }

    public void createBinaryNode(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content)
        throws RepositoryException
    {
        if (! checkIdExists(location, id)) {
            jcrSession.getNode(location).addNode(id, "nt:folder");
        }

        jcrSession.getNode(location + "/" + id)
            .addNode(MARK + locale, "nt:folder")
            .addNode(MARK + id, "nt:file")
            .addNode("jcr:content", "nt:resource");

        Node n = jcrSession.getNode(location + "/" + id);
        n.addMixin("mix:lastModified");


        n = jcrSession.getNode(location + "/" + id + "/" + MARK + locale + "/" + MARK + id);
        n.addMixin("mix:title");
        n.addMixin("mix:versionable");
        n.addMixin("mix:lastModified");
        n.addMixin("mix:mimeType");

        // Adding properties

        Binary _content = jcrSession.getValueFactory().createBinary(content);

        n.getNode("jcr:content").setProperty("jcr:data", _content);
        n.setProperty("jcr:title", fileName);
        n.setProperty("jcr:mimeType", contentType);
        n.setProperty("jcr:description", size);

        // Saving changes into JCR
        jcrSession.save();
    }


    // JCR Aux methods
    public Value jcrValue(String content, String encoding)
        throws RepositoryException
    {
        try {
            return jcrSession.getValueFactory().createValue( new String(content.getBytes(encoding), encoding) );
        } catch (UnsupportedEncodingException e) {
            throw new RepositoryException("Bad encoding : " + encoding);
        }
    }

    public Integer jcrVersion(String location) {
        try {
            VersionHistory h = vm.getVersionHistory( location );
            return new Integer( (int) h.getAllLinearFrozenNodes().getSize() );
        } catch (Exception e) {
            log.error( "Unexpected error getting version history of " + location + ". Msg: " + e.getMessage() );
            return 0;
        }
    }

    public Date jcrCreated(String location) {
        try {
            return jcrSession.getNode( location ).getProperty( "jcr:created" ).getDate().getTime();
        } catch (Exception e) {
            log.error( "Unexpected error getting created date for " + location + ". Msg: " + e.getMessage() );
            return null;
        }
    }

    public Date jcrLastModified(String location) {
        try {
            return jcrSession.getNode( location ).getProperty( "jcr:lastModified" ).getDate().getTime();
        } catch (Exception e) {
            log.error( "Unexpected error getting created date for " + location + ". Msg: " + e.getMessage() );
            return null;
        }
    }

    private String parent(String location) {

        if (location == null) return null;

        if ("/".equals( location )) return location;

        String[] locs = location.split("/");

        if (locs.length > 2) {
            StringBuffer sb = new StringBuffer( location.length() );
            for (int i=1; i < (locs.length -1); i++) {
                sb.append("/" + locs[i]);
            }
            return sb.toString();
        } else {
            return "/";
        }
    }

    public byte[] toByteArray(InputStream is, Long size) {
        try {

            byte[] data = new byte[16000];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (Exception e) {
            log.error("Error creating createBinaryContent() transforming toByteArray(). Msg: " + e.getMessage());
        }
        return null;
    }


}
