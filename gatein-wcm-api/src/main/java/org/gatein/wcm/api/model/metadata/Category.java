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

package org.gatein.wcm.api.model.metadata;

import java.util.List;

/**
 * 
 * Basic category representation. <br />
 * Conten can have one ore more categories. <br />
 * Categories can be represented as a tree of subcategories. <br />
 * 
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 * 
 */
public interface Category {

    /**
     * 
     * @return This method returns ID of Category.
     */
    public String getId();

    /**
     * 
     * @return This method returns locale of Category.
     */
    public String getLocale();

    /**
     * 
     * @return This method returns description of Category
     */
    public String getDescription();

    /**
     * 
     * @param description New description of Category
     */
    public void setDescription(String description);

    /**
     * 
     * @return This method returns parent Category for this category.
     */
    public Category getParentCategory();

    /**
     * 
     * @return This method returns child categories.
     */
    public List<Category> getChildCategories();

}
