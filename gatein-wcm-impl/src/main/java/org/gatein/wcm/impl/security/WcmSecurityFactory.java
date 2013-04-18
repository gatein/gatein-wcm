package org.gatein.wcm.impl.security;

import org.gatein.wcm.api.services.WcmSecurityService;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;

public class WcmSecurityFactory {

    private static WcmSecurityService securityInstance = null;

    // TODO This is only for dummy testing, this is a known bottleneck in a load test.
    public static WcmSecurityService getSecurityService() throws WcmContentIOException {
        if (securityInstance == null)
            securityInstance = new DummySecurityService();

        return securityInstance;
    }

}
