package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.content.TextContent;

public class WcmTextContent extends WcmContent implements TextContent {

    String version;
    String content;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getContent() {
        return content;
    }

    // Protected methods

    protected WcmTextContent() {
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    protected void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "WcmTextContent [version=" + version + ", id=" + id + ", locale=" + locale + ", locales=" + locales
                + ", location=" + location + ", acl=" + acl + ", created=" + created + ", lastModified=" + lastModified
                + ", publishStatus=" + publishStatus + ", publishingRoles=" + publishingRoles + ", createdBy=" + createdBy
                + ", lastModifiedBy=" + lastModifiedBy + ", comments=" + comments + ", categories=" + categories
                + ", properties=" + properties + ", locked=" + locked + ", lockOwner=" + lockOwner + ", content=" + content
                + "]";
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
        WcmTextContent other = (WcmTextContent) obj;
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
