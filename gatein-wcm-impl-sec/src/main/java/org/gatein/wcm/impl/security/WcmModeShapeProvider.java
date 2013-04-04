package org.gatein.wcm.impl.security;

import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.jboss.logging.Logger;
import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.ModeShapePermissions;
import org.modeshape.jcr.ModeShapeRoles;
import org.modeshape.jcr.security.AuthenticationProvider;
import org.modeshape.jcr.security.AuthorizationProvider;
import org.modeshape.jcr.security.SecurityContext;
import org.modeshape.jcr.value.Path;

public class WcmModeShapeProvider implements AuthenticationProvider, AuthorizationProvider {

    private static final Logger log = Logger.getLogger(WcmModeShapeProvider.class);

    // Roles:
    // role
    // role.repositoryName
    // role.repositoryName.workspaceName
    //
    // Roles: {readonly, readwrite, admin}
    private static boolean hasRole(SecurityContext context, String roleName, String repositoryName, String workspaceName) {
        if (context.hasRole(roleName)) {
            return true;
        }
        roleName = roleName + "." + repositoryName;
        if (context.hasRole(roleName)) {
            return true;
        }
        roleName = roleName + "." + workspaceName;
        return context.hasRole(roleName);
    }

    public ExecutionContext authenticate(Credentials credentials, String repositoryName, String workspaceName,
            ExecutionContext repositoryContext, Map<String, Object> sessionAttributes) {

        if (credentials instanceof SimpleCredentials) {
            try {
                SimpleCredentials sCredentials = (SimpleCredentials) credentials;
                return repositoryContext.with(new WcmSecurityContext(sCredentials));
            } catch (ContentSecurityException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e);
                } else {
                    log.warn(e.getMessage());
                }
                return null;
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debugf("Expected an instance of '%s' but found '%s'", SimpleCredentials.class.getName(),
                        credentials == null ? null : credentials.getClass().getName());
            }
            return null;
        }
    }

    @Override
    public boolean hasPermission(ExecutionContext context, String repositoryName, String repositorySourceName,
            String workspaceName, Path path, String... actions) {

        // Get the credentials from context
        SecurityContext sec = context.getSecurityContext();

        boolean hasPermission = true;
        for (String action : actions) {
            if (ModeShapePermissions.READ.equals(action)) {
                hasPermission &= hasRole(sec, ModeShapeRoles.READONLY, repositoryName, workspaceName)
                        || hasRole(sec, ModeShapeRoles.READWRITE, repositoryName, workspaceName)
                        || hasRole(sec, ModeShapeRoles.ADMIN, repositoryName, workspaceName);
            } else if (ModeShapePermissions.REGISTER_NAMESPACE.equals(action)
                    || ModeShapePermissions.REGISTER_TYPE.equals(action) || ModeShapePermissions.UNLOCK_ANY.equals(action)
                    || ModeShapePermissions.CREATE_WORKSPACE.equals(action)
                    || ModeShapePermissions.DELETE_WORKSPACE.equals(action) || ModeShapePermissions.MONITOR.equals(action)
                    || ModeShapePermissions.DELETE_WORKSPACE.equals(action)
                    || ModeShapePermissions.INDEX_WORKSPACE.equals(action)) {
                hasPermission &= hasRole(sec, ModeShapeRoles.ADMIN, repositoryName, workspaceName);
            } else {
                hasPermission &= hasRole(sec, ModeShapeRoles.ADMIN, repositoryName, workspaceName)
                        || hasRole(sec, ModeShapeRoles.READWRITE, repositoryName, workspaceName);
            }
        }

        return hasPermission;
    }

}
