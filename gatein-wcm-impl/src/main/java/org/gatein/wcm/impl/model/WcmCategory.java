package org.gatein.wcm.impl.model;

import java.util.List;

import org.gatein.wcm.api.model.metadata.Category;

public class WcmCategory implements Category {

    String id;
    String locale;
    String location;
    String description;
    List<Category> childCategories;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<Category> getChildCategories() {
        return childCategories;
    }

    // Protected methods

    protected WcmCategory() {
        super();
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setLocale(String locale) {
        this.locale = locale;
    }

    protected void setLocation(String location) {
        this.location = location;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void setChildCategories(List<Category> childCategories) {
        this.childCategories = childCategories;
    }

    @Override
    public String toString() {
        return "WCMCategory [id=" + id + ", locale=" + locale + ", location=" + location + ", description=" + description
                + ", childCategories=" + childCategories + "]";
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
        WcmCategory other = (WcmCategory) obj;
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
