package org.gatein.wcm.impl.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.security.ACE.PermissionType;
import org.gatein.wcm.api.model.security.ACL;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.impl.jcr.JcrMappings;

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

    private final String MARK = "__";

    public WCMContentFactory(JcrMappings jcr, User user)
        throws ContentIOException
    {
        logged = user;
        this.jcr = jcr;
    }

    public Content createTextContent(String id, String locale, String location, String html,
            String encoding) {

        WCMTextContent c = new WCMTextContent();

        if ("/".equals( location ) ) location = "";

        String absLocation = location + "/" + id + "/" + MARK + locale + "/" + MARK + id;

        // New document, so new version starting at 1
        c.setVersion( jcr.jcrVersion( absLocation  ) );
        c.setId( id );
        c.setLocale( locale );
        c.setLocation( location );

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        c.setAcl( null );

        c.setCreated( jcr.jcrCreated( absLocation ) );
        c.setLastModified( jcr.jcrLastModified( absLocation ) );

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        c.setPublishStatus( null );
        c.setPublishingRoles( null );

        c.setCreatedBy( logged );
        c.setLastModifiedBy( logged );

        // By default a new content will not use attached

        c.setLocked( false );

        c.setLockOwner( null );

        // Specific fields for TextContent
        c.setContent( html );
        c.setEncoding( encoding );

        return c;
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

    public Content createFolder(String id, String location) {

        WCMFolder f = new WCMFolder();

        String absLocation = location + "/" + id ;

        if ("/".equals( location ) ) location = "";

        // New document, so new version starting at 1
        f.setVersion( jcr.jcrVersion( absLocation  ) );
        f.setId( id );
        // Folders can have multiple locales, so, it will be null.
        f.setLocale( null );
        f.setLocation( location );

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        f.setAcl( null );

        f.setCreated( jcr.jcrCreated( absLocation ) );
        f.setLastModified( jcr.jcrLastModified( absLocation ) );

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        f.setPublishStatus( null );
        f.setPublishingRoles( null );

        f.setCreatedBy( logged );
        f.setLastModifiedBy( logged );

        // By default a new content will not use attached

        f.setLocked( false );

        f.setLockOwner( null );

        // Specific fields for Folder
        // New node, so no children at this point
        f.setChildren( null );

        return f;
    }

    public Content createBinaryContent(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content) {

        WCMBinaryContent b = new WCMBinaryContent();

        if ("/".equals( location ) ) location = "";

        String absLocation = location + "/" + id + "/" + MARK + locale + "/" + MARK + id;

        // New document, so new version starting at 1
        b.setVersion( jcr.jcrVersion( absLocation  ) );
        b.setId( id );
        b.setLocale( locale );
        b.setLocation( location );

        // By default a new content will get the ACL of parent parent.
        // A null value means that this content is using ACL of parent folder.
        b.setAcl( null );

        b.setCreated( jcr.jcrCreated( absLocation ) );
        b.setLastModified( jcr.jcrLastModified( absLocation ) );

        // By default a new content will get the Publishing status of his parent
        // A null value means that this content is using parent's publishing information
        b.setPublishStatus( null );
        b.setPublishingRoles( null );

        b.setCreatedBy( logged );
        b.setLastModifiedBy( logged );

        // By default a new content will not use attached

        b.setLocked( false );

        b.setLockOwner( null );

        // Specific fields for TextContent
        b.setFileName( fileName );
        b.setSize( size );
        b.setContentType( contentType );

        // Creating the in memory
        // Point to improve in the future

        b.set_content( jcr.toByteArray(content, size) );
        b.setContent( new ByteArrayInputStream(b.get_content()) );

        return b;
    }



}
