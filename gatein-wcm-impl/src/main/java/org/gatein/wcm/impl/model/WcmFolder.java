package org.gatein.wcm.impl.model;

import java.util.List;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.content.Folder;

public class WcmFolder extends WcmContent implements Folder {

    List<Content> children;

    @Override
    public List<Content> getChildren() {
        return children;
    }

    // Protected methods

    protected WcmFolder() {

    }

    protected void setChildren(List<Content> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "WcmFolder [children=" + children + ", id=" + id + ", locale=" + locale + ", locales=" + locales + ", location="
                + location + ", acl=" + acl + ", created=" + created + ", lastModified=" + lastModified + ", publishStatus="
                + publishStatus + ", publishingRoles=" + publishingRoles + ", createdBy=" + createdBy + ", lastModifiedBy="
                + lastModifiedBy + ", comments=" + comments + ", categories=" + categories + ", properties=" + properties
                + ", locked=" + locked + ", lockOwner=" + lockOwner + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
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
        WcmFolder other = (WcmFolder) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        return true;
    }

}
