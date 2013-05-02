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

import java.util.List;
import java.util.Map;

import org.gatein.wcm.api.model.metadata.WCMCategory;

/**
 * @see {@link WCMCategory}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMCategoryImpl implements WCMCategory {

    String id;
    String parentPath;
    String path;
    Map<String,String> description;
    List<WCMCategory> childCategories;

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
    public Map<String, String> getDescription() {
        return description;
    }

    @Override
    public List<WCMCategory> getChildCategories() {
        return childCategories;
    }

    // Protected methods

    protected WCMCategoryImpl() {
        super();
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

    protected void setDescription(Map<String, String> description) {
        this.description = description;
    }

    protected void setChildCategories(List<WCMCategory> childCategories) {
        this.childCategories = childCategories;
    }

    @Override
    public String toString() {
        return "WCMCategory [id=" + id + ", parentPath=" + parentPath + ", description=" + description
                + ", childCategories=" + childCategories + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        WCMCategoryImpl other = (WCMCategoryImpl) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (parentPath == null) {
            if (other.parentPath != null)
                return false;
        } else if (!parentPath.equals(other.parentPath))
            return false;
        return true;
    }

}
