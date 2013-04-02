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

import org.gatein.wcm.api.model.security.Principal.PrincipalType;
import org.gatein.wcm.api.model.security.ACE;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.PublishService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.jcr.JcrMappings;
import org.gatein.wcm.impl.security.WcmSecurityFactory;
import org.gatein.wcm.impl.services.WcmContentService;
import org.jboss.logging.Logger;
import org.modeshape.jcr.api.Repositories;


public class WcmServicesManager implements RepositoryService, ObjectFactory {

    private static final Logger log = Logger.getLogger(WcmServicesManager.class);

    private Repositories repositories;
    User u = null;
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
    public ContentService createContentSession(String idRepository, String idWorkspace, String user, String password) throws ContentIOException,
            ContentSecurityException {

        checkParameters(idRepository, idWorkspace, user, password);

        try {
            SecurityService service = WcmSecurityFactory.getSecurityService();
            u = service.authenticate(user, password);
            isAdmin = service.hasRole(u, ADMIN_ROLE);
        } catch (ContentIOException e) {
            throw new ContentIOException( "Unable to connect to WCM Security Service: " + e.getMessage() );
        } catch (ContentSecurityException e) {
            throw new ContentSecurityException( "Bad user/password for user: " + user + " " + e.getMessage() );
        }

        try {
           Context ctx = new InitialContext();
           repositories = (Repositories)ctx.lookup( "java:/jcr" );
           Repository rep = repositories.getRepository(idRepository);
           if (rep == null)
               throw new ContentIOException( "Unable to connect to JCR repository: " + idRepository );

           SimpleCredentials credentials = new SimpleCredentials(u.getUserName(), u.getPassword().toCharArray());
           Session s = rep.login(credentials, idWorkspace);

           initCategories( s );

           return new WcmContentService( idRepository, s, u );
        } catch (NamingException e) {
            throw new ContentIOException( "Unable to connect to ModeShape JNDI java:/jcr" );
        }  catch (NullPointerException e) {
            throw new ContentIOException( "Unable to connect to ModeShape JNDI java:/jcr" );
        } catch (LoginException e) {
            throw new ContentSecurityException( "User " + u.getUserName() + " has not rights on " + idRepository + "/" + idWorkspace);
        } catch (RepositoryException e) {
            throw new ContentIOException( "Unexpected error in reporitory " + idRepository + ". Error: " + e.getMessage() );
        }
    }

    @Override
    public PublishService createPublishSession(String idRepository, String idWorkspace, String user, String password) throws ContentIOException,
            ContentSecurityException {

        log.info("[[ TESTING createPublishSession() ");

        return null;
    }

    private void initCategories(Session session) {
        try {
            if (!session.itemExists("/__categories")) {
                session.getRootNode().addNode("__categories", "nt:folder");
                session.save();
                // Setting specific ACL for categories for admin users
                if (isAdmin) {
                    JcrMappings jcr = new JcrMappings(session, u);
                    jcr.createContentACE("/__categories", u.getUserName(), PrincipalType.USER, ACE.PermissionType.ALL);
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error initCategories in workspace. Msg: " + e.getMessage());
        }
    }

    private void checkParameters(String idRepository, String idWorkspace, String user, String password) throws ContentSecurityException {
        if (idRepository == null || "".equals(idRepository))
            throw new ContentSecurityException("Repository name cannot be null or empty");
        if (idWorkspace == null || "".equals(idWorkspace))
            throw new ContentSecurityException("Workspace name cannot be null or empty");
        if (user == null || "".equals(user))
            throw new ContentSecurityException("User cannot be null or empty");
        if (password == null || "".equals(password))
            throw new ContentSecurityException("Password cannot be null or empty");
    }

}
