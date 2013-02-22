package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.security.ACE;
import org.gatein.wcm.api.model.security.Principal;

public class WCMACE implements ACE {

    Principal principal;
    PermissionType permission;

    protected WCMACE(Principal principal, PermissionType permission) {
        this.principal = principal;
        this.permission = permission;
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public PermissionType getPermission() {
        return permission;
    }

    protected void setPrincipal(Principal principal) {
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
