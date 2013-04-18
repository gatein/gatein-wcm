package org.gatein.wcm.impl.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.metadata.WcmCategory;
import org.gatein.wcm.api.model.metadata.WcmComment;
import org.gatein.wcm.api.model.publishing.WcmPublishStatus;
import org.gatein.wcm.api.model.security.WcmAcl;
import org.gatein.wcm.api.model.security.WcmPrincipal;
import org.gatein.wcm.api.model.security.WcmUser;

public abstract class WcmObjectImpl implements WcmObject {

    protected String id;
    protected String locale;
    protected List<String> locales;
    protected String parentPath;
    protected String path;
    protected WcmAcl acl;
    protected Date created;
    protected Date lastModified;
    protected WcmPublishStatus publishStatus;
    protected List<WcmPrincipal> publishingRoles;
    protected WcmUser createdBy;
    protected WcmUser lastModifiedBy;
    protected List<WcmComment> comments;
    protected Set<WcmCategory> categories;
    protected Map<String, String> properties;
    protected boolean locked;
    protected WcmUser lockOwner;

    protected WcmObjectImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public List<String> getLocales() {
        return locales;
    }

    @Override
    public String getParentPath() {
        return parentPath;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public WcmAcl getAcl() {
        return acl;
    }

    @Override
    public Date getCreatedOn() {
        return created;
    }

    @Override
    public Date getLastModifiedOn() {
        return lastModified;
    }

    @Override
    public WcmPublishStatus getPublishStatus() {
        return publishStatus;
    }

    @Override
    public List<WcmPrincipal> getPublishingRoles() {
        return publishingRoles;
    }

    @Override
    public WcmUser getCreatedBy() {
        return createdBy;
    }

    @Override
    public WcmUser getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public List<WcmComment> getComments() {
        return comments;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public WcmUser getLockOwner() {
        return lockOwner;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setLocale(String locale) {
        this.locale = locale;
    }

    protected void setLocales(List<String> locales) {
        this.locales = locales;
    }

    protected void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    protected void setAcl(WcmAcl acl) {
        this.acl = acl;
    }

    protected void setCreatedOn(Date created) {
        this.created = created;
    }

    protected void setLastModifiedOn(Date lastModified) {
        this.lastModified = lastModified;
    }

    protected void setPublishStatus(WcmPublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    protected void setPublishingRoles(List<WcmPrincipal> publishingRoles) {
        this.publishingRoles = publishingRoles;
    }

    protected void setCreatedBy(WcmUser createdBy) {
        this.createdBy = createdBy;
    }

    protected void setLastModifiedBy(WcmUser lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    protected void setComments(List<WcmComment> comments) {
        this.comments = comments;
    }

    protected void setCategories(Set<WcmCategory> categories) {
        this.categories = categories;
    }

    protected void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    protected void setLocked(boolean locked) {
        this.locked = locked;
    }

    protected void setLockOwner(WcmUser lockOwner) {
        this.lockOwner = lockOwner;
    }

    @Override
    public String toString() {
        return "WcmContent [id=" + id + ", locale=" + locale + ", locales=" + locales + ", parentPath=" + parentPath
                + ", path=" + path + ", acl=" + acl + ", created=" + created + ", lastModified=" + lastModified
                + ", publishStatus=" + publishStatus + ", publishingRoles=" + publishingRoles + ", createdBy=" + createdBy
                + ", lastModifiedBy=" + lastModifiedBy + ", comments=" + comments + ", categories=" + categories
                + ", properties=" + properties + ", locked=" + locked + ", lockOwner=" + lockOwner + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((parentPath == null) ? 0 : parentPath.hashCode());
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
        WcmObjectImpl other = (WcmObjectImpl) obj;
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
        if (parentPath == null) {
            if (other.parentPath != null)
                return false;
        } else if (!parentPath.equals(other.parentPath))
            return false;
        return true;
    }

}