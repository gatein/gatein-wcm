package org.gatein.wcm.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class WcmRestActivator extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public WcmRestActivator() {
        singletons.add(new WcmRestService());
    }

    public Set<Object> getSingletons() {
        return singletons;
    }

}
