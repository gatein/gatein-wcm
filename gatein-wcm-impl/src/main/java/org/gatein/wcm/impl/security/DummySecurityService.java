package org.gatein.wcm.impl.security;

import java.util.Date;

import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.WcmSecurityService;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
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
public class DummySecurityService implements WcmSecurityService {

    private static final Logger log = Logger.getLogger(DummySecurityService.class);

    public WcmUser authenticate(String idUser, String password) throws WcmContentSecurityException {

        log.debug("Authenticating user... " + idUser);

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
            user.setCreatedOn(new Date());
            user.setLastLoginOn(new Date());

            String[] groups = {"group1", "group2"};
            user.setGroups( groups );

            return user;
        } else {
            throw new WcmContentSecurityException("Bad password for user " + idUser);
        }

    }

    // Roles:
    // role
    // role.repositoryName
    // role.repositoryName.workspaceName
    //
    // Roles: {readonly, readwrite, admin}

    public boolean hasRole(WcmUser user, String role) throws WcmContentSecurityException {

        if (user == null) {
            throw new WcmContentSecurityException("Bad user ");
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
