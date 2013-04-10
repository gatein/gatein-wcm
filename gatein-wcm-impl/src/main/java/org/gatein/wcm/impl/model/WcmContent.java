package org.gatein.wcm.impl.model;

import java.util.Date;
import java.util.List;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.metadata.Comment;
import org.gatein.wcm.api.model.metadata.Property;
import org.gatein.wcm.api.model.publishing.PublishStatus;
import org.gatein.wcm.api.model.security.ACL;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;

public abstract class WcmContent implements Content {

    protected String id;
    protected String locale;
    protected List<String> locales;
    protected String location;
    protected ACL acl;
    protected Date created;
    protected Date lastModified;
    protected PublishStatus publishStatus;
    protected List<Principal> publishingRoles;
    protected User createdBy;
    protected User lastModifiedBy;
    protected List<Comment> comments;
    protected List<Category> categories;
    protected List<Property> properties;
    protected boolean locked;
    protected User lockOwner;

    protected WcmContent() {
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
    public String getLocation() {
        return location;
    }

    @Override
    public ACL getAcl() {
        return acl;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public PublishStatus getPublishStatus() {
        return publishStatus;
    }

    @Override
    public List<Principal> getPublishingRoles() {
        return publishingRoles;
    }

    @Override
    public User getCreatedBy() {
        return createdBy;
    }

    @Override
    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public User getLockOwner() {
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

    protected void setLocation(String location) {
        this.location = location;
    }

    protected void setAcl(ACL acl) {
        this.acl = acl;
    }

    protected void setCreated(Date created) {
        this.created = created;
    }

    protected void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    protected void setPublishStatus(PublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    protected void setPublishingRoles(List<Principal> publishingRoles) {
        this.publishingRoles = publishingRoles;
    }

    protected void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    protected void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    protected void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    protected void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    protected void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    protected void setLocked(boolean locked) {
        this.locked = locked;
    }

    protected void setLockOwner(User lockOwner) {
        this.lockOwner = lockOwner;
    }

}
