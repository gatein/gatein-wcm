package org.gatein.wcm.impl.model;

import java.util.Date;

import org.gatein.wcm.api.model.metadata.WcmComment;
import org.gatein.wcm.api.model.security.WcmUser;

public class WcmCommentImpl implements WcmComment {

    private String id;
    private Date created;
    private WcmUser createdBy;
    private String comment;

    protected WcmCommentImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getCreatedOn() {
        return created;
    }

    @Override
    public WcmUser getCreatedBy() {
        return createdBy;
    }

    @Override
    public String getComment() {
        return comment;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setCreatedOn(Date created) {
        this.created = created;
    }

    protected void setCreatedBy(WcmUser createdBy) {
        this.createdBy = createdBy;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }

}
