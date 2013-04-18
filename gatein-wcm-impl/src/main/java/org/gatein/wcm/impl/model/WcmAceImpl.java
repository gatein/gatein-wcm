package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.security.WcmAce;
import org.gatein.wcm.api.model.security.WcmPrincipal;

public class WcmAceImpl implements WcmAce {

    WcmPrincipal principal;
    PermissionType permission;

    protected WcmAceImpl(WcmPrincipal principal, PermissionType permission) {
        this.principal = principal;
        this.permission = permission;
    }

    @Override
    public WcmPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public PermissionType getPermission() {
        return permission;
    }

    protected void setPrincipal(WcmPrincipal principal) {
        this.principal = principal;
    }

    protected void setPermission(PermissionType permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "WCMACE [principal=" + principal + ", permission=" + permission + "]";
    }

}
