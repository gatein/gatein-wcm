package org.gatein.wcm.impl.services.commands;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
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
        try {
            Node root = jcrSession.getNode(location);
            log.info( root.getPrimaryNodeType().getName().equals("nt:folder") );
        } catch (PathNotFoundException e) {
            return true;
        } catch (RepositoryException e) {
            log.error("Unexpected error in location " + location + ". Message: " + e.getMessage());
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

    public void checkJCRException(RepositoryException e) throws ContentException, ContentIOException, ContentSecurityException {
        // TODO
    }

    // JCR Aux methods
    public Value jcrValue(String content, String encoding) throws RepositoryException {
        try {
            return jcrSession.getValueFactory().createValue( new String(content.getBytes(encoding), encoding) );
        } catch (UnsupportedEncodingException e) {
            throw new RepositoryException("Bad encoding : " + encoding);
        }
    }

    public String parent(String location) {

        String[] locs = location.split("/");

        if (locs.length > 1) {
            StringBuffer sb = new StringBuffer( location.length() );
            for (int i=1; i < (locs.length -1); i++) {
                sb.append("/" + locs[i]);
            }
            return sb.toString();
        }
        return location;
    }

}
