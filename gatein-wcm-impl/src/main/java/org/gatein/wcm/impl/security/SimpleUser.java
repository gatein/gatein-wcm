package org.gatein.wcm.impl.security;

import java.util.Date;

import org.gatein.wcm.api.model.security.User;

public class SimpleUser implements User {

    String userName;
    String password;
    String firstName;
    String lastName;
    String email;
    Date createdDate;
    Date lastLoginTime;
    String displayName;
    String organizationId;
    String[] groups;

    protected void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String s) {
        password = s;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String s) {
        firstName = s;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String s) {
        lastName = s;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String s) {
        email = s;
    }

    protected void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    protected void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String s) {
        displayName = s;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String s) {
        organizationId = s;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "SimpleUser [userName=" + userName + "]";
    }

}
