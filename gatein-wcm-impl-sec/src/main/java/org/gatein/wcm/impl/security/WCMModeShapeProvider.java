package org.gatein.wcm.impl.security;

import java.util.Locale;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.modeshape.common.i18n.I18nResource;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthenticationProvider;
import org.modeshape.jcr.security.SecurityContext;

public class WCMModeShapeProvider implements AuthenticationProvider {

    private static final Logger LOGGER = Logger.getLogger(WCMModeShapeProvider.class);

    public ExecutionContext authenticate(Credentials credentials, String repositoryName, String workspaceName,
            ExecutionContext repositoryContext, Map<String, Object> sessionAttributes) {

        try {
            if (credentials instanceof SimpleCredentials) {
                SimpleCredentials sCredentials = (SimpleCredentials) credentials;
                return repositoryContext.with(new WCMSecurityContext(sCredentials));
            }

        } catch (LoginException e) {
            LOGGER.warn(new WCMModeShapeProvider.LogMsg(e.toString()), e.toString());
            return null;
        }
        return null;
    }

    protected static class WCMSecurityContext implements SecurityContext {

        SimpleCredentials sCredentials = null;
        SecurityService wcmSecurityService = null;
        User loggedUser = null;

        protected WCMSecurityContext(Credentials credentials) throws LoginException {
            // Expecting JCR SimpleCredentials
            sCredentials = (SimpleCredentials) credentials;
            LOGGER.info(new WCMModeShapeProvider.LogMsg("Getting security credentials "), sCredentials.getUserID());

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
                LOGGER.error(new WCMModeShapeProvider.LogMsg("Error getting role: " + e.getMessage()), loggedUser);
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

    public static class LogMsg implements I18nResource {

        String msg;

        public LogMsg(String msg) {
            this.msg = msg;
        }

        public String text(Object... arguments) {
            return msg;
        }

        public String text(Locale locale, Object... arguments) {
            return msg;
        }

    }

}
