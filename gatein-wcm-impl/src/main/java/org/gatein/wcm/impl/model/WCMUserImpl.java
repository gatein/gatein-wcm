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

import java.util.Arrays;
import java.util.Date;

import org.gatein.wcm.api.model.security.WCMUser;

/**
 * @see {@link WCMUser}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMUserImpl implements WCMUser {

    String userName;
    String password;
    String firstName;
    String lastName;
    String email;
    Date createdDate;
    Date lastLoginTime;
    String displayName;
    String organizationId;
    String[] roles;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Date getCreatedOn() {
        return createdDate;
    }

    @Override
    public Date getLastLoginOn() {
        return lastLoginTime;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String[] getRoles() {
        return roles;
    }

    protected void setRoles(String[] roles) {
        this.roles = roles;
    }

    @Override
    public boolean hasRole(String role) {
        if (role == null) return false;
        if ("".equals(role)) return false;
        if (roles == null) return false;
        for (String userRole : roles) {
            if (userRole.equals(role)) return true;
        }
        return false;
    }

    protected WCMUserImpl(String userName, String[] roles) {
        this.userName = userName;
        this.roles = roles;
    }

    protected WCMUserImpl(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "WCMUserImpl [userName=" + userName + ", roles=" + Arrays.toString(roles) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
        WCMUserImpl other = (WCMUserImpl) obj;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }
}