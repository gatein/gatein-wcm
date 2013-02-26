package org.gatein.wcm.impl.security;

import org.gatein.wcm.api.services.SecurityService;
import org.gatein.wcm.api.services.exceptions.ContentIOException;

public class WcmSecurityFactory {

    private static SecurityService securityInstance = null;

    // TODO This is only for dummy testing, this is a known bottleneck in a load test.
    public static synchronized SecurityService getSecurityService() throws ContentIOException {
        if (securityInstance == null)
            securityInstance = new DummySecurityService();

        return securityInstance;
    }

}
