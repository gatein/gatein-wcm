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

package org.gatein.wcm.portlet.util;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.domain.Category;

/**
 * Helper class to manage View's metadata like pagination and filters.
 * Helper methods to render domain objects in the View.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ViewMetadata {
    public enum ViewType {
        POSTS, UPLOADS, TEMPLATES
    }
    private ViewType viewType;
    private boolean filterCategory = false;
    private Long categoryId;
    private boolean filterName = false;
    private String name;
    private int fromIndex;
    private int toIndex;
    private int totalIndex;

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public boolean isFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(boolean filterCategory) {
        this.filterCategory = filterCategory;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isFilterName() {
        return filterName;
    }

    public void setFilterName(boolean filterName) {
        this.filterName = filterName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

    public int getTotalIndex() {
        return totalIndex;
    }

    public void setTotalIndex(int totalIndex) {
        this.totalIndex = totalIndex;
    }

    public void resetPagination() {
        fromIndex = 0;
        toIndex = (Wcm.VIEWS.MAX_PER_PAGE >= totalIndex ? totalIndex - 1: Wcm.VIEWS.MAX_PER_PAGE -1);
        if (toIndex == -1) toIndex = 0;
    }

    public void rightPage() {
        fromIndex = toIndex + 1;
        toIndex = fromIndex + Wcm.VIEWS.MAX_PER_PAGE - 1;
        if (toIndex >= totalIndex) {
            toIndex = totalIndex - 1;
        }
        if (toIndex < 0) toIndex = 0;
    }

    public void leftPage() {
        toIndex = fromIndex - 1;
        if (toIndex < 0) toIndex = 0;
        fromIndex = toIndex - Wcm.VIEWS.MAX_PER_PAGE + 1;
        if (fromIndex < 0) fromIndex = 0;
    }

    public void checkPagination() {
        if (fromIndex < 0) fromIndex = 0;
        if (toIndex < 0) toIndex = (Wcm.VIEWS.MAX_PER_PAGE > totalIndex? totalIndex - 1: Wcm.VIEWS.MAX_PER_PAGE - 1);
        if ((toIndex - fromIndex) +1 < Wcm.VIEWS.MAX_PER_PAGE) {
            toIndex = fromIndex + Wcm.VIEWS.MAX_PER_PAGE;
        }
        if (fromIndex >= totalIndex) fromIndex = totalIndex - 1;
        if (toIndex >= totalIndex) toIndex = totalIndex - 1;
    }

    // Helpers to render attributes
    public static String categoryTitle(Category cat) {
        if (cat == null) return "";
        if (cat.getParent() == null)
            return cat.getName();
        else
            return categoryTitle(cat.getParent()) + "/" + cat.getName();
    }

    public static String aclType(Character type) {
        if (type == Wcm.ACL.WRITE) return "WRITE";
        else return "NONE";
    }
}
