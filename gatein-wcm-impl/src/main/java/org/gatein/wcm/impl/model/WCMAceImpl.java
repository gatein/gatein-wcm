/*
 * JBoss, a division of Red Hat
 * Copyright 2013, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.security.WCMAce;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipal;

/**
 * @see {@link WCMAce}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMAceImpl implements WCMAce {

    WCMPrincipal principal;
    WCMPermissionType permission;

    protected WCMAceImpl(WCMPrincipal principal, WCMPermissionType permission) {
        this.principal = principal;
        this.permission = permission;
    }

    @Override
    public WCMPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public WCMPermissionType getPermission() {
        return permission;
    }

    protected void setPrincipal(WCMPrincipal principal) {
        this.principal = principal;
    }

    protected void setPermission(WCMPermissionType permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "WCMAceImpl [principal=" + principal + ", permission=" + permission + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((permission == null) ? 0 : permission.hashCode());
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WCMAceImpl other = (WCMAceImpl) obj;
        if (permission != other.permission)
            return false;
        if (principal == null) {
            if (other.principal != null)
                return false;
        } else if (!principal.equals(other.principal))
            return false;
        return true;
    }

}
