package org.gatein.wcm.impl.model;

import java.util.Date;

import javax.jcr.Node;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.security.ACE.PermissionType;
import org.gatein.wcm.api.model.security.ACL;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.impl.services.commands.JcrMappings;

/**
 *
 * All org.gatein.wcm.model factory methods should be placed here.
 *
 * @author lucas
 *
 */
public class WCMContentFactory {

    User logged = null;
    JcrMappings jcr = null;

    public WCMContentFactory(JcrMappings jcr, User user)
        throws ContentIOException
    {
        logged = user;
        this.jcr = jcr;
    }

    public Content createTextContent(Node n, String id, String locale, String location, String html,
            String encoding) {

        WCMTextContent c = new WCMTextContent();

        // New document, so new version starting at 1
        c.setVersion( 1 );
        c.setId( id );
        c.setLocale( locale );
        c.setLocation( location );
        c.setAcl( createDefaultACL(logged, location + "/" + id, "Owner ACL") );
        c.setCreated( new Date() );
        c.setLastModified( new Date() );

        // TODO Fix once defined publishing block
        c.setPublishStatus( null );
        c.setPublishingRoles( null );

        c.setCreatedBy( logged );
        c.setLastModifiedBy( logged );

        // TODO Fix once defined
        c.setComments( null );
        c.setCategories( null );
        c.setProperties( null );

        c.setLocked( false );

        c.setLockOwner( null );

        return c;

    }

    // ACL that owns a ACE related the owner
    private ACL createDefaultACL(User user, String id, String description) {

        WCMPrincipal wcmPrincipal = new WCMPrincipal(user.getUserName(), Principal.PrincipalType.USER);
        WCMACE wcmACE = new WCMACE(wcmPrincipal, PermissionType.ALL);
        WCMACL wcmACL = new WCMACL(id, description);
        wcmACL.getAces().add( wcmACE );

        return wcmACL;
    }

    /**
     *
     * GateIn WCM represents a ACL list a String stored into a file called "__acl" in the content location.
     * This file is a String with the following structure:
     *
     *  user:[USER|GROUP]:[NONE|READ|COMMENTS|WRITE|ALL],user:[USER|GROUP]:[NONE|READ|COMMENTS|WRITE|ALL], ...
     *
     * @param str
     * @return
     */
    public ACL parseACL(String id, String description, String acl) {
        WCMACL wcmACL = new WCMACL(id, description);
        String[] aces = acl.split(",");
        for (String ace : aces) {
            String user = ace.split(":")[0];
            String type = ace.split(":")[1];
            String permission = ace .split(":")[2];

            WCMPrincipal wcmPrincipal = null;
            WCMACE wcmACE = null;
            if (type.equals("USER"))
                wcmPrincipal = new WCMPrincipal(user, Principal.PrincipalType.USER);
            else
                wcmPrincipal = new WCMPrincipal(user, Principal.PrincipalType.GROUP);

            if (permission.equals("NONE")) {
                wcmACE = new WCMACE(wcmPrincipal, PermissionType.NONE);
            }
            if (permission.equals("READ")) {
                wcmACE = new WCMACE(wcmPrincipal, PermissionType.READ);
            }
            if (permission.equals("COMMENTS")) {
                wcmACE = new WCMACE(wcmPrincipal, PermissionType.COMMENTS);
            }
            if (permission.equals("WRITE")) {
                wcmACE = new WCMACE(wcmPrincipal, PermissionType.WRITE);
            }
            if (permission.equals("ALL")) {
                wcmACE = new WCMACE(wcmPrincipal, PermissionType.ALL);
            }
            wcmACL.getAces().add( wcmACE );
        }
        return wcmACL;
    }


}
