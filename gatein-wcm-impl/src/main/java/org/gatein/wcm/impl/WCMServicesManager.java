package org.gatein.wcm.impl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.PublishService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.jboss.logging.Logger;



public class WCMServicesManager implements RepositoryService, ObjectFactory {

    private static final Logger LOGGER = Logger.getLogger(WCMServicesManager.class);


    public WCMServicesManager() {
        LOGGER.info( "[[ TEST WCMServicesManager() ]]" );
    }

    @Override
    public Object getObjectInstance(Object arg0, Name arg1, Context arg2, Hashtable<?, ?> arg3) throws Exception {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public ContentService createContentSession(String idRepository, User user) throws ContentIOException,
            ContentSecurityException {

        LOGGER.info("[[ TESTING createContentSession() ");

        return null;
    }

    @Override
    public PublishService createPublishSession(String idRepository, User user) throws ContentIOException,
            ContentSecurityException {

        LOGGER.info("[[ TESTING createPublishSession() ");

        return null;
    }






}
