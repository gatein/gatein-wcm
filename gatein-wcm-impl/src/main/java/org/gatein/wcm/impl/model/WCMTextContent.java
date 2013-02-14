package org.gatein.wcm.impl.model;

import java.util.Date;
import java.util.List;

import org.gatein.wcm.api.model.content.TextContent;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.metadata.Comment;
import org.gatein.wcm.api.model.metadata.Property;
import org.gatein.wcm.api.model.publishing.PublishStatus;
import org.gatein.wcm.api.model.security.ACL;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;

public class WCMTextContent implements TextContent {

    Integer version;
    String id;
    String locale;
    String location;
    ACL acl;
    Date created;
    Date lastModified;
    PublishStatus publishStatus;
    List<Principal> publishingRoles;
    User createdBy;
    User lastModifiedBy;
    List<Comment> comments;
    List<Category> categories;
    List<Property> properties;
    boolean locked;
    User lockOwner;
    String content;
    String encoding;

    @Override
    public Integer getVersion() {
        return version;
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

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    // Protected methods

    protected WCMTextContent() {

    }

    protected void setVersion(Integer version) {
        this.version = version;
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

    protected void setContent(String content) {
        this.content = content;
    }

    protected void setEncoding(String encoding) {
        this.encoding = encoding;
    }




}
