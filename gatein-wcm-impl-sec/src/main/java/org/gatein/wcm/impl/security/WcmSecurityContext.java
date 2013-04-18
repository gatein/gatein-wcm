package org.gatein.wcm.impl.security;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.WcmSecurityService;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.security.SecurityContext;

public class WcmSecurityContext implements SecurityContext {

    private static final Logger log = Logger.getLogger(WcmSecurityContext.class);

    SimpleCredentials sCredentials = null;
    WcmSecurityService wcmSecurityService = null;
    WcmUser loggedUser = null;

    protected WcmSecurityContext(Credentials credentials) throws LoginException {
        // Expecting JCR SimpleCredentials
        sCredentials = (SimpleCredentials) credentials;
        log.debug("Getting security credentials for user " + sCredentials.getUserID(), sCredentials.getUserID());

        try {
            wcmSecurityService = WcmSecurityFactory.getSecurityService();
        } catch (WcmContentIOException e) {
            throw new LoginException("Unable to connect to SecuritySystem: " + e.getMessage());
        }

        try {
            loggedUser = wcmSecurityService.authenticate(sCredentials.getUserID(), new String(sCredentials.getPassword()));
        } catch (WcmContentSecurityException e) {
            throw new LoginException(e.getMessage());
        }

    }

    public String getUserName() {
        return (loggedUser != null ? loggedUser.getUserName() : null);
    }

    public boolean hasRole(String role) {

        try {
            return wcmSecurityService.hasRole(loggedUser, role);
        } catch (WcmContentSecurityException e) {
            log.error(new WcmLog("Error getting role: " + e.getMessage()), loggedUser);
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

