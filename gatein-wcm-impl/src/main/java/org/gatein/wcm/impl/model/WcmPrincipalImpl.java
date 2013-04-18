package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.security.WcmPrincipal;

public class WcmPrincipalImpl implements WcmPrincipal {

    String id;
    PrincipalType type;

    protected WcmPrincipalImpl(String id, PrincipalType type) {
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

    @Override
    public String toString() {
        return "WCMPrincipal [id=" + id + ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        WcmPrincipalImpl other = (WcmPrincipalImpl) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}
