package org.gatein.wcm.impl.model;

import java.util.Date;

import org.gatein.wcm.api.model.security.User;

public class WCMUser implements User {

    String user;

    @Override
    public String getUserName() {
        return user;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPassword(String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getFirstName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFirstName(String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getLastName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLastName(String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getEmail() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEmail(String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public Date getCreatedDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getLastLoginTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDisplayName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDisplayName(String displayName) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getOrganizationId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] getGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    protected WCMUser(String username) {
        this.user = username;
    }

}
