package org.gatein.wcm.impl.security;

import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;

public class WCMSecurityFactory {

    private static SecurityService securityInstance = null;

    public static synchronized SecurityService getSecurityService() throws ContentIOException {
        if (securityInstance == null)
            securityInstance = new DummySecurityService();

        return securityInstance;
    }

}
