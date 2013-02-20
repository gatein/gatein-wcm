package org.gatein.wcm.impl.security;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.security.SecurityContext;

public class WCMSecurityContext implements SecurityContext {

    private static final Logger log = Logger.getLogger(WCMSecurityContext.class);

    SimpleCredentials sCredentials = null;
    SecurityService wcmSecurityService = null;
    User loggedUser = null;

    protected WCMSecurityContext(Credentials credentials) throws LoginException {
        // Expecting JCR SimpleCredentials
        sCredentials = (SimpleCredentials) credentials;
        log.info(new WCMLog("Getting security credentials for user " + sCredentials.getUserID()), sCredentials.getUserID());

        try {
            wcmSecurityService = WCMSecurityFactory.getSecurityService();
        } catch (ContentIOException e) {
            throw new LoginException("Unable to connect to SecuritySystem: " + e.getMessage());
        }

        try {
            loggedUser = wcmSecurityService.authenticate(sCredentials.getUserID(), new String(sCredentials.getPassword()));
        } catch (ContentSecurityException e) {
            throw new LoginException(e.getMessage());
        }

    }

    public String getUserName() {
        return (loggedUser != null ? loggedUser.getUserName() : null);
    }

    public boolean hasRole(String role) {

        try {
            return wcmSecurityService.hasRole(loggedUser, role);
        } catch (ContentSecurityException e) {
            log.error(new WCMLog("Error getting role: " + e.getMessage()), loggedUser);
        }

        return false;
    }

    public boolean isAnonymous() {
        return false;
    }

    public void logout() {
        sCredentials = null;
        loggedUser = null;
    }

}

