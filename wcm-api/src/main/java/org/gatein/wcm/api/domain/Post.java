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
import java.util.HashSet;
import java.util.Set;

/**
 * Post represents a content publication in the GateIn WCM system.
 * Light representation from org.gatein.wcm.domain.Post class for remote api.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class Post implements Serializable {

    /**
     * Post is public and it can be accessible from Content Portlets.
     */
    public static final Character PUBLISHED = 'P';
    /**
     * Post is in draft mode and it can not be accessible from Content Portlets.
     */
    public static final Character DRAFT = 'D';

    private Long id;
    private Long version;
    private String author;
    private Calendar created;
    private String content;
    private String title;
    private String excerpt;
    private Character postStatus;
    private Calendar modified;
    private String locale;
    private Character commentsStatus;
    private Set<Comment> comments = new HashSet<Comment>();
    private Set<Category> categories = new HashSet<Category>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Character getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Character postStatus) {
        this.postStatus = postStatus;
    }

    public Calendar getModified() {
        return modified;
    }

    public void setModified(Calendar modified) {
        this.modified = modified;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Character getCommentsStatus() {
        return commentsStatus;
    }

    public void setCommentsStatus(Character commentsStatus) {
        this.commentsStatus = commentsStatus;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (author != null ? !author.equals(post.author) : post.author != null) return false;
        if (categories != null ? !categories.equals(post.categories) : post.categories != null) return false;
        if (comments != null ? !comments.equals(post.comments) : post.comments != null) return false;
        if (commentsStatus != null ? !commentsStatus.equals(post.commentsStatus) : post.commentsStatus != null) return false;
        if (content != null ? !content.equals(post.content) : post.content != null) return false;
        if (created != null ? !created.equals(post.created) : post.created != null) return false;
        if (excerpt != null ? !excerpt.equals(post.excerpt) : post.excerpt != null) return false;
        if (id != null ? !id.equals(post.id) : post.id != null) return false;
        if (locale != null ? !locale.equals(post.locale) : post.locale != null) return false;
        if (modified != null ? !modified.equals(post.modified) : post.modified != null) return false;
        if (postStatus != null ? !postStatus.equals(post.postStatus) : post.postStatus != null) return false;
        if (title != null ? !title.equals(post.title) : post.title != null) return false;
        if (version != null ? !version.equals(post.version) : post.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (excerpt != null ? excerpt.hashCode() : 0);
        result = 31 * result + (postStatus != null ? postStatus.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (commentsStatus != null ? commentsStatus.hashCode() : 0);
        return result;
    }
}
