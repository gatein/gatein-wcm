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
package org.gatein.wcm.api.model.security;

import java.util.Date;

/**
 * User representation in WCM context.
 * <p>
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WCMUser {

    /**
     * This method should return the username of the user.
     * <p>
     * The username should be unique and the user database should not have 2
     * user record with the same username
     *
     * @return
     */
    String getUserName();

    /**
     * @return This method return the password of the user account
     */
    String getPassword();

    /**
     * This method is used to change the user account password.
     *
     * @param s
     */
    void setPassword(String s);

    /**
     * @return This method return the first name of the user
     */
    String getFirstName();

    /**
     * @param s the new first name
     */
    void setFirstName(String s);

    /**
     * @return The last name of the user
     */
    String getLastName();

    /**
     * @param s The new last name of the user
     */
    void setLastName(String s);

    /**
     * @return The email address of the user
     */
    String getEmail();

    /**
     * @param s The new user email address
     */
    void setEmail(String s);

    /**
     * @return The date that the user register or create the account
     */
    Date getCreatedOn();

    /**
     * @return Return the last time that the user access the account
     */
    Date getLastLoginOn();

    /**
     * @return return the display name
     */
    String getDisplayName();

    /**
     * @param displayName The name that should show in the display name
     */
    void setDisplayName(String displayName);

    /**
     * @return the id of organization the user belongs to or null if not applicable
     */
    String getOrganizationId();

    /**
     * sets the prganizationId
     */
    void setOrganizationId(String organizationId);

    /**
     * @return the groups that user belongs to
     */
    String[] getRoles();

    /**
     *
     * @param role - Role to check
     * @return Returns true if role is in the user's roles.
     */
    boolean hasRole(String role);
}
