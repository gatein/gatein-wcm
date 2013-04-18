package org.gatein.wcm.impl.model;

import java.util.List;

import org.gatein.wcm.api.model.metadata.WcmCategory;

public class WcmCategoryImpl implements WcmCategory {

    String id;
    String locale;
    String parentPath;
    String path;
    String description;
    List<WcmCategory> childCategories;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocale() {
        return locale;
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
    public String getDescription() {
        return description;
    }

    @Override
    public List<WcmCategory> getChildCategories() {
        return childCategories;
    }

    // Protected methods

    protected WcmCategoryImpl() {
        super();
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setLocale(String locale) {
        this.locale = locale;
    }

    protected void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void setChildCategories(List<WcmCategory> childCategories) {
        this.childCategories = childCategories;
    }

    @Override
    public String toString() {
        return "WCMCategory [id=" + id + ", locale=" + locale + ", parentPath=" + parentPath + ", description=" + description
                + ", childCategories=" + childCategories + "]";
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
        WcmCategoryImpl other = (WcmCategoryImpl) obj;
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
