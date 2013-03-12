package org.gatein.wcm.impl.model;

import java.util.Date;

import org.gatein.wcm.api.model.metadata.Comment;
import org.gatein.wcm.api.model.security.User;

public class WcmComment implements Comment {

    private String id;
    private Date created;
    private User createdBy;
    private String comment;

    protected WcmComment() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public User getCreatedBy() {
        return createdBy;
    }

    @Override
    public String getComment() {
        return comment;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setCreated(Date created) {
        this.created = created;
    }

    protected void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }

}
