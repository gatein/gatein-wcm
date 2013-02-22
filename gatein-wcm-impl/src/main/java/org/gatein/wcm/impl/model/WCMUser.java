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

    @Override
    public String toString() {
        return "WCMUser [user=" + user + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        WCMUser other = (WCMUser) obj;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

}
