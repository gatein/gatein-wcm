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
package org.gatein.wcm.impl.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.metadata.WCMCategory;
import org.gatein.wcm.api.model.metadata.WCMComment;
import org.gatein.wcm.api.model.publishing.WCMPublishStatus;
import org.gatein.wcm.api.model.security.WCMAcl;
import org.gatein.wcm.api.model.security.WCMPrincipal;
import org.gatein.wcm.api.model.security.WCMUser;

/**
 * @see {@link WCMObject}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public abstract class WCMObjectImpl implements WCMObject {

    protected String id;
    protected String parentPath;
    protected String path;
    protected WCMAcl acl;
    protected Date created;
    protected Date lastModified;
    protected WCMPublishStatus publishStatus;
    protected List<WCMPrincipal> publishingRoles;
    protected String createdBy;
    protected String lastModifiedBy;
    protected List<WCMComment> comments;
    protected Set<WCMCategory> categories;
    protected Map<String, String> properties;
    protected boolean locked;
    protected WCMUser lockOwner;

    protected WCMObjectImpl() {
    }

    @Override
    public String getId() {
        return id;
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
    public WCMAcl getAcl() {
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
    public WCMPublishStatus getPublishStatus() {
        return publishStatus;
    }

    @Override
    public List<WCMPrincipal> getPublishingRoles() {
        return publishingRoles;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public List<WCMComment> getComments() {
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
    public WCMUser getLockOwner() {
        return lockOwner;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    protected void setAcl(WCMAcl acl) {
        this.acl = acl;
    }

    protected void setCreatedOn(Date created) {
        this.created = created;
    }

    protected void setLastModifiedOn(Date lastModified) {
        this.lastModified = lastModified;
    }

    protected void setPublishStatus(WCMPublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    protected void setPublishingRoles(List<WCMPrincipal> publishingRoles) {
        this.publishingRoles = publishingRoles;
    }

    protected void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    protected void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    protected void setComments(List<WCMComment> comments) {
        this.comments = comments;
    }

    protected void setCategories(Set<WCMCategory> categories) {
        this.categories = categories;
    }

    protected void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    protected void setLocked(boolean locked) {
        this.locked = locked;
    }

    protected void setLockOwner(WCMUser lockOwner) {
        this.lockOwner = lockOwner;
    }

    @Override
    public String toString() {
        return "WcmContent [id=" + id + ", parentPath=" + parentPath
                + ", path=" + path + ", acl=" + acl + ", created=" + created + ", lastModified=" + lastModified
                + ", publishStatus=" + publishStatus + ", publishingRoles=" + publishingRoles + ", createdBy=" + createdBy
                + ", lastModifiedBy=" + lastModifiedBy + ", comments=" + comments + ", categories=" + categories
                + ", properties=" + properties + ", locked=" + locked + ", lockOwner=" + lockOwner + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((acl == null) ? 0 : acl.hashCode());
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((lastModifiedBy == null) ? 0 : lastModifiedBy.hashCode());
        result = prime * result + ((lockOwner == null) ? 0 : lockOwner.hashCode());
        result = prime * result + (locked ? 1231 : 1237);
        result = prime * result + ((parentPath == null) ? 0 : parentPath.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((publishStatus == null) ? 0 : publishStatus.hashCode());
        result = prime * result + ((publishingRoles == null) ? 0 : publishingRoles.hashCode());
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
        WCMObjectImpl other = (WCMObjectImpl) obj;
        if (acl == null) {
            if (other.acl != null)
                return false;
        } else if (!acl.equals(other.acl))
            return false;
        if (categories == null) {
            if (other.categories != null)
                return false;
        } else if (!categories.equals(other.categories))
            return false;
        if (comments == null) {
            if (other.comments != null)
                return false;
        } else if (!comments.equals(other.comments))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (createdBy == null) {
            if (other.createdBy != null)
                return false;
        } else if (!createdBy.equals(other.createdBy))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastModified == null) {
            if (other.lastModified != null)
                return false;
        } else if (!lastModified.equals(other.lastModified))
            return false;
        if (lastModifiedBy == null) {
            if (other.lastModifiedBy != null)
                return false;
        } else if (!lastModifiedBy.equals(other.lastModifiedBy))
            return false;
        if (lockOwner == null) {
            if (other.lockOwner != null)
                return false;
        } else if (!lockOwner.equals(other.lockOwner))
            return false;
        if (locked != other.locked)
            return false;
        if (parentPath == null) {
            if (other.parentPath != null)
                return false;
        } else if (!parentPath.equals(other.parentPath))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (publishStatus == null) {
            if (other.publishStatus != null)
                return false;
        } else if (!publishStatus.equals(other.publishStatus))
            return false;
        if (publishingRoles == null) {
            if (other.publishingRoles != null)
                return false;
        } else if (!publishingRoles.equals(other.publishingRoles))
            return false;
        return true;
    }
}