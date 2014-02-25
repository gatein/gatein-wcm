/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
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

package org.gatein.wcm.api.domain;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Comments are attached to Posts.
 * Light representation from org.gatein.wcm.domain.Comment class for remote api.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class Comment implements Serializable {

    /**
     * Post can accept comments from non-logged users.
     */
    public static final Character ANONYMOUS = 'A';
    /**
     * Post accepts only comments from GateIn users.
     */
    public static final Character LOGGED = 'L';
    /**
     * This Post doesn't accept comments.
     */
    public static final Character NO_COMMENTS = 'N';

    private Long id;
    private Long postId;
    private String author;
    private String authorEmail;
    private String authorUrl;
    private Calendar created;
    private String content;
    private Character status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (author != null ? !author.equals(comment.author) : comment.author != null) return false;
        if (authorEmail != null ? !authorEmail.equals(comment.authorEmail) : comment.authorEmail != null) return false;
        if (authorUrl != null ? !authorUrl.equals(comment.authorUrl) : comment.authorUrl != null) return false;
        if (content != null ? !content.equals(comment.content) : comment.content != null) return false;
        if (created != null ? !created.equals(comment.created) : comment.created != null) return false;
        if (id != null ? !id.equals(comment.id) : comment.id != null) return false;
        if (postId != null ? !postId.equals(comment.postId) : comment.postId != null) return false;
        if (status != null ? !status.equals(comment.status) : comment.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (postId != null ? postId.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (authorEmail != null ? authorEmail.hashCode() : 0);
        result = 31 * result + (authorUrl != null ? authorUrl.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", author='" + author + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", created=" + created +
                ", content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}
