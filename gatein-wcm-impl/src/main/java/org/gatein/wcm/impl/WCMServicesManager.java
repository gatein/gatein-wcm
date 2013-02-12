package org.gatein.wcm.impl;

import javax.ejb.Stateless;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.PublishService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;

import org.jboss.logging.Logger;

@Stateless
public class WCMServicesManager implements RepositoryService {

    private static final Logger LOGGER = Logger.getLogger(WCMServicesManager.class);

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
