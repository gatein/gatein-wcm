package org.gatein.wcm.impl.security;

import javax.jcr.SimpleCredentials;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.jboss.logging.Logger;
import org.modeshape.jcr.security.SecurityContext;

public class WcmSecurityContext implements SecurityContext {

    private static final Logger log = Logger.getLogger(WcmSecurityContext.class);

    private SimpleCredentials credentials;
    private final SecurityService wcmSecurityService;
    private User authenticatedUser;

    protected WcmSecurityContext(SimpleCredentials credentials) throws ContentSecurityException {
        this.credentials = credentials;
        log.debugf("About to authenticate user '%s'", this.credentials.getUserID());

        try {
            wcmSecurityService = WcmSecurityFactory.getSecurityService();
            authenticatedUser = wcmSecurityService.authenticate(this.credentials.getUserID(),
                    new String(this.credentials.getPassword()));
        } catch (ContentIOException e) {
            throw new ContentSecurityException("Could not connect to " + SecurityService.class.getName(), e);
        }

    }

    public String getUserName() {
        return (authenticatedUser != null ? authenticatedUser.getUserName() : null);
    }

    public boolean hasRole(String role) {
        if (this.authenticatedUser == null) {
            throw new IllegalStateException("Cannot query role membership after user logout");
        } else {
            try {
                return wcmSecurityService.hasRole(authenticatedUser, role);
            } catch (ContentSecurityException e) {
                final String msg = "Could not find out if user '%1s' has role '%2s'";
                if (log.isDebugEnabled()) {
                    log.errorf(e, msg, getUserName(), role);
                } else {
                    log.errorf(msg, getUserName(), role);
                }
            }
            return false;
        }
    }

    public boolean isAnonymous() {
        return false;
    }

    public void logout() {
        this.credentials = null;
        this.authenticatedUser = null;
    }

}
