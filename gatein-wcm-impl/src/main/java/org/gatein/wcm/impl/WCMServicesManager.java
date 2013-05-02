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
package org.gatein.wcm.impl;

import java.util.Hashtable;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMPublishService;
import org.gatein.wcm.api.services.WCMRepositoryService;
import org.gatein.wcm.api.services.WCMSecurityService;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.security.WcmSecurityFactory;
import org.gatein.wcm.impl.services.WCMContentServiceImpl;
import org.jboss.logging.Logger;
import org.modeshape.jcr.api.Repositories;

/**
 * Manager of WCM services.
 * <p>
 * It is responsible to access to underlying repository and creates specific APIs for WCM manipulation.
 * <p>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMServicesManager implements WCMRepositoryService, ObjectFactory {

    private static final Logger log = Logger.getLogger(WCMServicesManager.class);

    private Repositories repositories;
    WCMUser u = null;
    private boolean isAdmin = false;
    private String ADMIN_ROLE = "admin";
    // TODO this DEFAULT LOCALE should be read from configuration file, for example in the subsystem configuration
    private String defaultLocale = "en";
    // TODO this DEFAULT_REPOSITORY should be read from configuration file, for example in the subsystem configuration
    private String defaultRepository = "sample";
    // TODO this DEFAULT_WORKSPACE should be read from configuration file, for example in the subsystem configuration
    private String defaultWorkspace = "default";

    public WCMServicesManager() {
        log.info( "[[ Init WCMServicesManager Service ]] ");
    }

    /**
     * WCMServicesManager is bound into the JNDI tree via an object factory as it's shown in the following example:
     * <p>
     * {@code
     * <subsystem xmlns="urn:jboss:domain:naming:1.1">
     *  <bindings>
     *    <object-factory name="java:jboss/gatein-wcm" module="org.gatein.wcm.gatein-wcm-impl" class="org.gatein.wcm.impl.WCMServicesManager" />
     *  </bindings>
     * </subsystem>
     * }
     */
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        return this;
    }

    /**
     * @see {@link WCMRepositoryService#createContentSession(String, String)}
     */
    @Override
    public WCMContentService createContentSession(String user, String password) throws WCMContentIOException,
            WCMContentSecurityException {

        checkParameters(defaultRepository, defaultWorkspace, user, password);

        try {
            WCMSecurityService service = WcmSecurityFactory.getSecurityService();
            u = service.authenticate(user, password);
            isAdmin = u.hasRole(ADMIN_ROLE);
        } catch (WCMContentIOException e) {
            throw new WCMContentIOException( "Unable to connect to WCM Security Service: " + e.getMessage(), e);
        } catch (WCMContentSecurityException e) {
            throw new WCMContentSecurityException( "Bad user/password for user: " + user + " " + e.getMessage(), e);
        }

        try {
           Context ctx = new InitialContext();
           repositories = (Repositories)ctx.lookup( "java:/jcr" );
           Repository rep = repositories.getRepository(defaultRepository);
           if (rep == null)
               throw new WCMContentIOException( "Unable to connect to JCR repository: " + defaultRepository );

           SimpleCredentials credentials = new SimpleCredentials(u.getUserName(), u.getPassword().toCharArray());
           Session s = rep.login(credentials, defaultWorkspace);

           initMetadata( s );

           return new WCMContentServiceImpl( defaultRepository, s, u, defaultLocale );
        } catch (NamingException e) {
            throw new WCMContentIOException( "Unable to connect to ModeShape JNDI java:/jcr", e);
        }  catch (NullPointerException e) {
            throw new WCMContentIOException( "Unable to connect to ModeShape JNDI java:/jcr", e);
        } catch (LoginException e) {
            throw new WCMContentSecurityException( "User " + u.getUserName() + " has not rights on " + defaultRepository + "/" + defaultWorkspace);
        } catch (RepositoryException e) {
            throw new WCMContentIOException( "Unexpected error in reporitory " + defaultRepository + ". Error: " + e.getMessage(), e);
        }
    }

    /**
     * @see {@link WCMRepositoryService#createPublishSession(String, String)}
     */
    @Override
    public WCMPublishService createPublishSession(String user, String password) throws WCMContentIOException,
            WCMContentSecurityException {

        log.info("[[ TESTING createPublishSession() ");

        return null;
    }

    /**
     * @see {@link WCMRepositoryService#getDefaultLocale()}
     */
    @Override
    public String getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * @see {@link WCMRepositoryService#setDefaultLocale(String)}
     */
    @Override
    public void setDefaultLocale(String locale) {
        this.defaultLocale = locale;
    }

    /**
     * @see {@link WCMRepositoryService#getDefaultRepository()}
     */
    @Override
    public String getDefaultRepository() {
        return defaultRepository;
    }

    /**
     * @see {@link WCMRepositoryService#setDefaultRepository(String)}
     */
    @Override
    public void setDefaultRepository(String defaultRepository) {
        this.defaultRepository = defaultRepository;
    }

    /**
     * @see {@link WCMRepositoryService#getDefaultWorkspace()}
     */
    @Override
    public String getDefaultWorkspace() {
        return defaultWorkspace;
    }

    /**
     * @see {@link WCMRepositoryService#setDefaultLocale(String)}
     */
    @Override
    public void setDefaultWorkspace(String defaultWorkspace) {
        this.defaultWorkspace = defaultWorkspace;
    }

    /**
     * Initializes the JCR repository with metadata for WCM domain.
     * <p>
     * @param session - JCR Session
     */
    private void initMetadata(Session session) {
        try {
            if (!session.itemExists("/__acl")) {
                // Adding first time in repository mix:lastModified
                // Using /__acl as a flag to not re-add mixins into root
                session.getRootNode().addMixin("mix:lastModified");

                // Acl set-up
                session.getRootNode().addNode("__acl", "nt:folder")
                .addMixin("mix:title");
                session.save();
                // Setting specific ACL for / for admin users
                if (isAdmin) {
                    JcrMappings jcr = new JcrMappings(session, u);
                    jcr.createContentAce("/", u.getUserName(), WCMPrincipalType.USER, WCMPermissionType.ALL);
                    // jcr.createContentAce("/", "*", WCMPrincipalType.USER, WCMPermissionType.ALL);
                }
            }
            if (!session.itemExists("/__categories")) {
                session.getRootNode().addNode("__categories", "nt:folder");
                session.save();
                // Setting specific ACL for categories for admin users
                if (isAdmin) {
                    JcrMappings jcr = new JcrMappings(session, u);
                    jcr.createContentAce("/__categories", u.getUserName(), WCMPrincipalType.USER, WCMPermissionType.ALL);
                    jcr.createContentAce("/__categories", "*", WCMPrincipalType.USER, WCMPermissionType.READ);
                }
            }
            if (!session.itemExists("/__comments")) {
                session.getRootNode().addNode("__comments", "nt:folder");
                session.save();
            }
            if (!session.itemExists("/__properties")) {
                session.getRootNode().addNode("__properties", "nt:folder");
                session.save();
            }
            if (!session.itemExists("/__relationships")) {
                session.getRootNode().addNode("__relationships", "nt:folder");
                session.save();
            }
        } catch (Exception e) {
            log.error("Unexpected error initMetadata in workspace. Msg: " + e.getMessage());
        }
    }

    /**
     * Checks null or empty parameters.
     * <p>
     * @param idRepository - Repository name
     * @param idWorkspace - Workspace name
     * @param user - User name
     * @param password - User's password
     * @throws WCMContentSecurityException
     */
    private void checkParameters(String idRepository, String idWorkspace, String user, String password) throws WCMContentSecurityException {
        if (idRepository == null || "".equals(idRepository))
            throw new WCMContentSecurityException("Repository name cannot be null or empty");
        if (idWorkspace == null || "".equals(idWorkspace))
            throw new WCMContentSecurityException("Workspace name cannot be null or empty");
        if (user == null || "".equals(user))
            throw new WCMContentSecurityException("User cannot be null or empty");
        if (password == null || "".equals(password))
            throw new WCMContentSecurityException("Password cannot be null or empty");
    }

}
