package org.gatein.wcm.impl.security;

import java.util.Date;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.jboss.logging.Logger;

/**
 *
 * Dummy Service facade to simulate an interaction with the Portal's authentication system. <br>
 *
 * In this case the user is hardcoded inside class. <br>
 *
 * This class will be replaced for another that connects to
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class DummySecurityService implements SecurityService {

    private static final Logger log = Logger.getLogger(DummySecurityService.class);

    public User authenticate(String idUser, String password) throws ContentSecurityException {

        log.debugf("Authenticating user '%s'", idUser);

        // Dummy rule for authentication
        if (idUser.equals(password)) {

            // Dummy user creating
            SimpleUser user = new SimpleUser();
            user.setUserName(idUser);
            user.setFirstName(idUser + " - FirstName");
            user.setLastName(idUser + " - LastName");
            user.setDisplayName(idUser);
            user.setOrganizationId(idUser + " - Organization");
            user.setPassword(password);
            user.setEmail(idUser + "@dummy.com");
            user.setCreatedDate(new Date());
            user.setLastLoginTime(new Date());

            String[] groups = {"group1", "group2"};
            user.setGroups( groups );

            return user;
        } else {
            throw new ContentSecurityException("Invalid password for user " + idUser);
        }

    }

    // Roles:
    // role
    // role.repositoryName
    // role.repositoryName.workspaceName
    //
    // Roles: {readonly, readwrite, admin}

    public boolean hasRole(User user, String role) throws ContentSecurityException {

        if (user == null) {
            throw new ContentSecurityException("Bad user ");
        }

        log.debug("Authorization of user " + user.getUserName() + " with role " + role + " ...");

        // Dymmy rule for roles
        // Hard coding for modeshape roles
        // In the future we need connect to the real system

        if ("guess".equals( user.getUserName() )) {
            if ("readonly".equals( role ) ) return true;
            else return false;
        }
        if ("admin".equals( user.getUserName() )) {
            return true;
        }

        if ("lucas".equals( user.getUserName() )) {
            if ( "readonly".equals( role ) ) return true;
        }

        if ("user1".equals( user.getUserName() )) {
            // testing that this user only has rights on workspace sample
            if ("readwrite.sample".equals( role ))
                return true;
        }
        if ("user2".equals( user.getUserName() )) {
            return true;
        }
        if ("user3".equals( user.getUserName() )) {
            return true;
        }


        return false;
    }

}
