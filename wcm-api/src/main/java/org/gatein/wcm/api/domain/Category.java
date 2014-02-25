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

/**
 * Categories group content (Posts, Uploads or Templates).
 * Categories have a tree structure with Category's parent reference.
 * Light representation from org.gatein.wcm.domain.Category class for remote api.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class Category implements Serializable {

    /**
     * Normal Category.
     * It's defined for semantic porpuse, an object should have only 1 Category but it can have N Tags.
     */
    public static final Character CATEGORY = '2';
    /**
     * Tag Category.
     * It's defined for semantic porpuse, an object should have only 1 Category but it can have N Tags.
     */
    public static final Character TAG = '3';
    /**
     * Folder Category.
     * A Folder Category can group other categories.
     */
    public static final Character FOLDER = '1';

    private Long id;
    private String name;
    private Character type;
    private int numChildren;
    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (numChildren != category.numChildren) return false;
        if (id != null ? !id.equals(category.id) : category.id != null) return false;
        if (name != null ? !name.equals(category.name) : category.name != null) return false;
        if (path != null ? !path.equals(category.path) : category.path != null) return false;
        if (type != null ? !type.equals(category.type) : category.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + numChildren;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", numChildren=" + numChildren +
                ", path='" + path + '\'' +
                '}';
    }
}
