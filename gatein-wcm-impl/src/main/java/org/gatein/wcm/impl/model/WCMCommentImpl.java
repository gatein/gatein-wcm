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

import org.gatein.wcm.api.model.metadata.WCMComment;
import org.gatein.wcm.api.model.security.WCMUser;

/**
 * @see {@link WCMComment}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMCommentImpl implements WCMComment {

    private String id;
    private Date created;
    private WCMUser createdBy;
    private String comment;

    protected WCMCommentImpl() {
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
    public WCMUser getCreatedBy() {
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

    protected void setCreatedBy(WCMUser createdBy) {
        this.createdBy = createdBy;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        WCMCommentImpl other = (WCMCommentImpl) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
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
        return true;
    }
}
