package org.gatein.wcm.api.model.security;

import java.util.Date;

public interface WcmUser {

    /**
     * This method should return the username of the user. The username should be unique and the user database should not have 2
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
    String[] getGroups();
}
