/*
 * JBoss, a division of Red Hat
 * Copyright 2013, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.wcm.impl.security;

import javax.annotation.Resource;
import javax.jcr.Credentials;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginException;

import org.gatein.wcm.api.model.security.WCMAce;
import org.gatein.wcm.api.model.security.WCMAcl;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.WCMSecurityService;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.jboss.logging.Logger;
import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.ModeShapePermissions;
import org.modeshape.jcr.api.Repositories;
import org.modeshape.jcr.security.AuthorizationProvider;
import org.modeshape.jcr.security.SecurityContext;
import org.modeshape.jcr.value.Path;

public class WCMSecurityContext implements SecurityContext, AuthorizationProvider {

    private static final Logger log = Logger.getLogger(WCMSecurityContext.class);

    SimpleCredentials sCredentials = null;
    WCMSecurityService wcmSecurityService = null;
    WCMUser loggedUser = null;

    // TODO This admin user is needed to validate ACL
    // It's like a LDAP where you need a user with grants for all branchs to get info for other user.
    String ADMIN_USER = "admin";
    String ADMIN_PASSWORD = "admin";

    @Resource(mappedName = "java:/jcr")
    Repositories repositories;
    JcrMappings jcr;

    protected WCMSecurityContext(Credentials credentials) throws LoginException {
        // Expecting JCR SimpleCredentials
        sCredentials = (SimpleCredentials) credentials;
        log.debug("Getting security credentials for user " + sCredentials.getUserID());

        try {
            wcmSecurityService = WcmSecurityFactory.getSecurityService();
        } catch (WCMContentIOException e) {
            throw new LoginException("Unable to connect to SecuritySystem: " + e.getMessage());
        }

        try {
            loggedUser = wcmSecurityService.authenticate(sCredentials.getUserID(), new String(sCredentials.getPassword()));
        } catch (WCMContentSecurityException e) {
            throw new LoginException(e.getMessage());
        }

    }

    public String getUserName() {
        return (loggedUser != null ? loggedUser.getUserName() : null);
    }

    public boolean hasRole(String role) {
        if (loggedUser == null) return false;
        return loggedUser.hasRole(role);
    }

    public boolean isAnonymous() {
        return false;
    }

    public void logout() {
        sCredentials = null;
        loggedUser = null;
    }

    @Override
    public boolean hasPermission(ExecutionContext context, String repositoryName, String repositorySourceName,
            String workspaceName, Path path, String... actions) {

        // If user has WCM admin role, then he has full access
        if (loggedUser.hasRole("admin")) return true;

        /*
           Modeshape roles
           ===============

           If a user wants to write into JCR it needs an "admin" role or "readwrite".
           We need hasRole() == "admin" || "readwrite"

           Modeshape actions
           =================

           Only for read JCR:
                ModeShapePermissions.READ.equals(action)

           Only for admin JCR: (read or write users can't perform these actions)
                ModeShapePermissions.REGISTER_NAMESPACE.equals(action)
                ModeShapePermissions.REGISTER_TYPE.equals(action)
                ModeShapePermissions.UNLOCK_ANY.equals(action)
                ModeShapePermissions.CREATE_WORKSPACE.equals(action)
                ModeShapePermissions.DELETE_WORKSPACE.equals(action)
                ModeShapePermissions.MONITOR.equals(action)
                ModeShapePermissions.DELETE_WORKSPACE.equals(action)
                ModeShapePermissions.INDEX_WORKSPACE.equals(action)

           Only for write JCR: (read users can't perform these actions)
                ModeShapePermissions.ADD_NODE
                ModeShapePermissions.SET_PROPERTY
                ModeShapePermissions.REMOVE
                ModeShapePermissions.BACKUP
                ModeShapePermissions.RESTORE

           GateIn WCM permissions (attached to a path in a ACL way)
           ========================================================

           WCMPermissionType.NONE       (A user can't read a path)
           WCMPermissionType.READ       (A user can read a path)
           WCMPermissionType.WRITE      (A user can read and write in a path and all their metadata, but it can't write in categories or in other paths)
           WCMPermissionType.ALL        (Full access to all paths and categories)

         *
         */

        String checkPath;
        if (path == null) {
            // If path == null we will map WCM ACL to the root node of repository/workspace
            checkPath = "/";
        } else {
            checkPath = path.getString();
        }

        if (jcr == null)
            initJcrMappings(repositoryName, workspaceName);

        WCMAcl acl = null;
        try {
            jcr.getSession().refresh(false);
            acl = jcr.jcrACL(checkPath);
        } catch (Exception e) {
            log.error("Unexpected error trying to get ACL for user " + loggedUser.getUserName() + " and path: " + checkPath + ". Msg: " + e.toString(), e);
        }

        // JCR actions
        boolean jcrRead = false;
        boolean jcrWrite = false;
        boolean jcrAdmin = false;

        for (String action : actions) {
            if (ModeShapePermissions.READ.equals(action)) {
                jcrRead = true;
            } else if (ModeShapePermissions.REGISTER_NAMESPACE.equals(action)
                    || ModeShapePermissions.REGISTER_TYPE.equals(action)
                    || ModeShapePermissions.UNLOCK_ANY.equals(action)
                    || ModeShapePermissions.CREATE_WORKSPACE.equals(action)
                    || ModeShapePermissions.DELETE_WORKSPACE.equals(action)
                    || ModeShapePermissions.MONITOR.equals(action)
                    || ModeShapePermissions.DELETE_WORKSPACE.equals(action)
                    || ModeShapePermissions.INDEX_WORKSPACE.equals(action)) {
                jcrAdmin = true;
            } else {
                jcrWrite = true;
            }
        }

        // Validate WCM role + JCR requeriment + Path with ACL
        boolean found = false;
        boolean granted = false;
        // READ case
        if (jcrRead) {
            // If user wants to read and there is not ACL it has access
            if (acl == null) return true;
            else {
                // Check READ, WRITE, ALL roles in the ACL
                found = false;
                granted = false;
                for (WCMAce ace : acl.getAces()) {
                    if (ace.getPrincipal().getType() == WCMPrincipalType.USER) {
                        found = ace.getPrincipal().getId().equals(loggedUser.getUserName());
                        found = found || ace.getPrincipal().getId().equals("*");
                        granted = found && ace.getPermission() != WCMPermissionType.NONE;
                    } else {
                        for (String r : loggedUser.getRoles()) {
                            if (ace.getPrincipal().getId().equals(r) ||
                                ace.getPrincipal().getId().equals("*")) {
                                granted = ace.getPermission() != WCMPermissionType.NONE;
                            }
                        }
                    }
                }
            }
        }

        // WRITE case
        if (jcrWrite) {
            // If user wants to read and there is not ACL it has access
            if (acl == null) return true;
            else {
                // Check READ, WRITE, ALL roles in the ACL
                found = false;
                granted = false;
                for (WCMAce ace : acl.getAces()) {
                    if (ace.getPrincipal().getType() == WCMPrincipalType.USER) {
                        found = ace.getPrincipal().getId().equals(loggedUser.getUserName());
                        found = found || ace.getPrincipal().getId().equals("*");
                        granted = found && (ace.getPermission() == WCMPermissionType.WRITE) || (ace.getPermission() == WCMPermissionType.ALL);
                    } else {
                        for (String r : loggedUser.getRoles()) {
                            if (ace.getPrincipal().getId().equals(r) ||
                                ace.getPrincipal().getId().equals("*")) {
                                found = true;
                                granted = (ace.getPermission() == WCMPermissionType.WRITE) || (ace.getPermission() == WCMPermissionType.ALL);
                            }
                        }
                    }
                }
            }
        }

        // ADMIN case
        if (jcrAdmin) {
            return loggedUser.hasRole("admin");
        }

        return granted;
    }

    private void initJcrMappings(String repository, String workspace) {
        try {
            repositories = (Repositories)(new InitialContext().lookup("java:/jcr"));
            Session adminSession = repositories.getRepository(repository).login(new SimpleCredentials(this.ADMIN_USER, this.ADMIN_PASSWORD.toCharArray()), workspace);
            jcr = new JcrMappings(adminSession, loggedUser);
        } catch (Exception e) {
            log.error("Error trying to access JCR repository: " + repository + "/" + workspace + " for authorization porpuses. ", e);
        }
    }

}

