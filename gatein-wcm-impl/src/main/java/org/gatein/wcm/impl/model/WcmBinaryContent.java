package org.gatein.wcm.impl.model;

import java.io.InputStream;

import org.gatein.wcm.api.model.content.BinaryContent;

public class WcmBinaryContent extends WcmContent implements BinaryContent {

    String version;
    String contentType;
    Long size;
    String fileName;
    InputStream content;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public InputStream getContent() {
        return content;
    }

    // Protected methods

    protected WcmBinaryContent() {
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected void setSize(Long size) {
        this.size = size;
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected void setContent(InputStream content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "WcmBinaryContent [version=" + version + ", id=" + id + ", locale=" + locale + ", locales=" + locales
                + ", location=" + location + ", acl=" + acl + ", created=" + created + ", lastModified=" + lastModified
                + ", publishStatus=" + publishStatus + ", publishingRoles=" + publishingRoles + ", createdBy=" + createdBy
                + ", lastModifiedBy=" + lastModifiedBy + ", comments=" + comments + ", categories=" + categories
                + ", properties=" + properties + ", locked=" + locked + ", lockOwner=" + lockOwner + ", contentType="
                + contentType + ", size=" + size + ", fileName=" + fileName + ", content=" + content + "]";
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
        WcmBinaryContent other = (WcmBinaryContent) obj;
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
