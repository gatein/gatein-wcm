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

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.PublishService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.impl.security.WCMSecurityFactory;
import org.gatein.wcm.impl.services.WCMContentService;
import org.jboss.logging.Logger;
import org.modeshape.jcr.api.Repositories;



public class WCMServicesManager implements RepositoryService, ObjectFactory {

    private static final Logger log = Logger.getLogger("org.gatein.wcm");

    Repositories repositories;

    public WCMServicesManager() {

        log.info( "[[ Init WCMServicesManager Service ]] ");
    }

    @Override
    public Object getObjectInstance(Object arg0, Name arg1, Context arg2, Hashtable<?, ?> arg3) throws Exception {
        return this;
    }

    @Override
    public ContentService createContentSession(String idRepository, String idWorkspace, String user, String password) throws ContentIOException,
            ContentSecurityException {

        User u = null;

        try {

            u = WCMSecurityFactory.getSecurityService().authenticate(user, password);

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

           return new WCMContentService( s, u );

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


}
