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
 * Upload represents files uploaded into wcm as images or resources.
 * These resources will be accessible through special wcm links.
 * Files binary are stored directly into file system, this class represents metadata of the file.
 * Light representation from org.gatein.wcm.domain.Upload class for remote api.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class Upload implements Serializable {

    private Long id;
    private Long version;
    private String fileName;
    private String storedName;
    private String mimeType;
    private Calendar created;
    private Calendar modified;
    private String user;
    private String description;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getModified() {
        return modified;
    }

    public void setModified(Calendar modified) {
        this.modified = modified;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        Upload upload = (Upload) o;

        if (created != null ? !created.equals(upload.created) : upload.created != null) return false;
        if (description != null ? !description.equals(upload.description) : upload.description != null) return false;
        if (fileName != null ? !fileName.equals(upload.fileName) : upload.fileName != null) return false;
        if (id != null ? !id.equals(upload.id) : upload.id != null) return false;
        if (mimeType != null ? !mimeType.equals(upload.mimeType) : upload.mimeType != null) return false;
        if (modified != null ? !modified.equals(upload.modified) : upload.modified != null) return false;
        if (storedName != null ? !storedName.equals(upload.storedName) : upload.storedName != null) return false;
        if (user != null ? !user.equals(upload.user) : upload.user != null) return false;
        if (version != null ? !version.equals(upload.version) : upload.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (storedName != null ? storedName.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
