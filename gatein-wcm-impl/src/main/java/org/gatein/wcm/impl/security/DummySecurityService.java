package org.gatein.wcm.impl.security;

import java.util.Date;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;

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

    public User authenticate(String idUser, String password) throws ContentSecurityException {

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

            return user;
        } else {
            throw new ContentSecurityException("Bad password for user " + idUser);
        }

    }

    public boolean hasRole(User user, String role) throws ContentSecurityException {

        // Dymmy rule for roles
        // Hard coding for modeshape roles
        // In the future we need connect to the real system
        // Now I'm using modeshape mapping roles in
        //
        // jboss-as-7.1.1.Final/modules/org/modeshape/main/conf/modehsape-roles.properties

        if (user == null) {
            throw new ContentSecurityException("Bad user ");
        }

        if (role != null && (role.equals("admin") || role.equals("guess")))
            return true;

        return false;
    }

}
