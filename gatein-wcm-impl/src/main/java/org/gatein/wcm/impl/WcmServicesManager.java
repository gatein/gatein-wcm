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

import org.gatein.wcm.api.model.security.WcmPrincipal.PrincipalType;
import org.gatein.wcm.api.model.security.WcmAce;
import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.WcmPublishService;
import org.gatein.wcm.api.services.WcmRepositoryService;
import org.gatein.wcm.api.services.WcmSecurityService;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.security.WcmSecurityFactory;
import org.gatein.wcm.impl.services.WcmContentServiceImpl;
import org.jboss.logging.Logger;
import org.modeshape.jcr.api.Repositories;


public class WcmServicesManager implements WcmRepositoryService, ObjectFactory {

    private static final Logger log = Logger.getLogger(WcmServicesManager.class);

    private Repositories repositories;
    WcmUser u = null;
    private boolean isAdmin = false;
    private String ADMIN_ROLE = "admin";

    public WcmServicesManager() {

        log.info( "[[ Init WCMServicesManager Service ]] ");
    }

    @Override
    public Object getObjectInstance(Object arg0, Name arg1, Context arg2, Hashtable<?, ?> arg3) throws Exception {
        return this;
    }

    @Override
    public WcmContentService createContentSession(String idRepository, String idWorkspace, String user, String password) throws WcmContentIOException,
            WcmContentSecurityException {

        checkParameters(idRepository, idWorkspace, user, password);

        try {
            WcmSecurityService service = WcmSecurityFactory.getSecurityService();
            u = service.authenticate(user, password);
            isAdmin = service.hasRole(u, ADMIN_ROLE);
        } catch (WcmContentIOException e) {
            throw new WcmContentIOException( "Unable to connect to WCM Security Service: " + e.getMessage() );
        } catch (WcmContentSecurityException e) {
            throw new WcmContentSecurityException( "Bad user/password for user: " + user + " " + e.getMessage() );
        }

        try {
           Context ctx = new InitialContext();
           repositories = (Repositories)ctx.lookup( "java:/jcr" );
           Repository rep = repositories.getRepository(idRepository);
           if (rep == null)
               throw new WcmContentIOException( "Unable to connect to JCR repository: " + idRepository );

           SimpleCredentials credentials = new SimpleCredentials(u.getUserName(), u.getPassword().toCharArray());
           Session s = rep.login(credentials, idWorkspace);

           initMetadata( s );

           return new WcmContentServiceImpl( idRepository, s, u );
        } catch (NamingException e) {
            throw new WcmContentIOException( "Unable to connect to ModeShape JNDI java:/jcr" );
        }  catch (NullPointerException e) {
            throw new WcmContentIOException( "Unable to connect to ModeShape JNDI java:/jcr" );
        } catch (LoginException e) {
            throw new WcmContentSecurityException( "User " + u.getUserName() + " has not rights on " + idRepository + "/" + idWorkspace);
        } catch (RepositoryException e) {
            throw new WcmContentIOException( "Unexpected error in reporitory " + idRepository + ". Error: " + e.getMessage() );
        }
    }

    @Override
    public WcmPublishService createPublishSession(String idRepository, String idWorkspace, String user, String password) throws WcmContentIOException,
            WcmContentSecurityException {

        log.info("[[ TESTING createPublishSession() ");

        return null;
    }

    private void initMetadata(Session session) {
        try {
            if (!session.itemExists("/__acl")) {
                session.getRootNode().addNode("__acl", "nt:folder")
                .addMixin("mix:title");
                session.save();
                // Setting specific ACL for / for admin users
                if (isAdmin) {
                    JcrMappings jcr = new JcrMappings(session, u);
                    jcr.createContentAce("/", u.getUserName(), PrincipalType.USER, WcmAce.PermissionType.ALL);
                }
            }
            if (!session.itemExists("/__categories")) {
                session.getRootNode().addNode("__categories", "nt:folder");
                session.save();
                // Setting specific ACL for categories for admin users
                if (isAdmin) {
                    JcrMappings jcr = new JcrMappings(session, u);
                    jcr.createContentAce("/__categories", u.getUserName(), PrincipalType.USER, WcmAce.PermissionType.ALL);
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

        } catch (Exception e) {
            log.error("Unexpected error initMetadata in workspace. Msg: " + e.getMessage());
        }
    }

    private void checkParameters(String idRepository, String idWorkspace, String user, String password) throws WcmContentSecurityException {
        if (idRepository == null || "".equals(idRepository))
            throw new WcmContentSecurityException("Repository name cannot be null or empty");
        if (idWorkspace == null || "".equals(idWorkspace))
            throw new WcmContentSecurityException("Workspace name cannot be null or empty");
        if (user == null || "".equals(user))
            throw new WcmContentSecurityException("User cannot be null or empty");
        if (password == null || "".equals(password))
            throw new WcmContentSecurityException("Password cannot be null or empty");
    }

}
