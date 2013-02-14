package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.security.Principal;

public class WCMPrincipal implements Principal {

    String id;
    PrincipalType type;

    protected WCMPrincipal(String id, PrincipalType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public PrincipalType getType() {
        return type;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setType(PrincipalType type) {
        this.type = type;
    }

}
