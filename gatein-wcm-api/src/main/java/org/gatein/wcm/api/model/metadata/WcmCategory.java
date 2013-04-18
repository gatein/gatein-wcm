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
 * Content can have one ore more categories. <br />
 * Categories can be represented as a tree of subcategories. <br />
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WcmCategory {

    /**
     *
     * @return This method returns ID of Category.
     */
    String getId();

    /**
     *
     * @return This method returns locale of Category.
     */
    String getLocale();

   /**
    *
    * @return This method returns path to the parent folder of category. <br>
    *         This method doesn't include the ID of the category. <br>
    *         Example: <br>
    *         Path: "/cat1/cat2/cat3" <br>
    *         <b>getParentPath()</b> will return "/cat1/cat2".
    */
    String getParentPath();

    /**
    *
    * @return This method returns full path to the category. <br>
    *         This method include the ID of the category. <br>
    *         Example: <br>
    *         Path: "/cat1/cat2/cat3" <br>
    *         <b>getPath()</b> will return "/cat1/cat2/cat3".
    */
    String getPath();

    /**
     *
     * @return This method returns description of Category
     */
    String getDescription();

    /**
     *
     * @return This method returns child categories.
     */
    List<WcmCategory> getChildCategories();

}
