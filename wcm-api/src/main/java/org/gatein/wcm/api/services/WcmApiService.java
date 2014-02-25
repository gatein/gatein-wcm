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

package org.gatein.wcm.api.services;

import java.util.List;

import javax.ejb.Remote;

import org.gatein.wcm.api.domain.Category;
import org.gatein.wcm.api.domain.Comment;
import org.gatein.wcm.api.domain.Post;
import org.gatein.wcm.api.domain.Upload;

/**
 * Remote API for org.gatein.wcm.api.domain.* model.
 * Query operations.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Remote
public interface WcmApiService {

    /*
     * Categories API
     */
    /**
     * @param user who performs operation
     * @return List of Categories with no parent that user can read
     * @throws Exception
     */
    List<Category> findRootCategories(String user) throws Exception;

    /**
     * @param cat Parent category
     * @param user who performs operation
     * @return List of children Categories for cat
     * @throws Exception
     */
    List<Category> findChildren(Category cat, String user) throws Exception;

    /**
     * @param path Category's path
     * @param user who performs operation
     * @return Category's defined by path or null if user has not rights to read or if Category doesn't exist
     * @throws Exception
     */
    Category findCategory(String path, String user) throws Exception;

    /*
     * Posts API
     */
    /**
     * @param user who performs operation
     * @return List of Posts that user can read
     * @throws Exception
     */
    List<Post> findPosts(String user) throws Exception;

    /**
     * @param categoryId Category's id
     * @param user who performs operation
     * @return List of Posts attached to a Category
     * @throws Exception
     */
    List<Post> findPosts(Long categoryId, String user) throws Exception;

    /**
     * @param filterName Post name for filter
     * @param user who performs operation
     * @return List of Posts attached filtered by name
     * @throws Exception
     */
    List<Post> findPosts(String filterName, String user) throws Exception;

    /**
     * @param categoryId Category's id
     * @param status Post's status
     * @param user who performs operation
     * @return List of Posts linked by Category defined by categoryId that user can read filtered by Post's status
     * @throws Exception
     */
    List<Post> findPosts(Long categoryId, Character status, String user) throws Exception;

    /**
     * @param categoryId Category's id
     * @param locale Locale used as a key for a Relationship
     * @param status Post's status
     * @param user who performs operation
     * @return List of Posts linked by Category defined by categoryId that user can read filtered by Post's status
     * @throws Exception
     */
    List<Post> findPosts(Long categoryId, String locale, Character status, String user) throws Exception;

    /**
     * @param id Post's id
     * @param user who performs operation
     * @return Post defined by id if user has rights to read or null otherwise
     * @throws Exception
     */
    Post findPost(Long id, String user) throws Exception;

    /**
     * @param id Post's id
     * @param locale Locale used as a key for a Relationship
     * @param user who performs operation
     * @return Post defined by id if user has rights to read or null otherwise
     * @throws Exception
     */
    Post findPost(Long id, String locale, String user) throws Exception;

    /*
     * Uploads API
     */
    /**
     * @param user who performs operation
     * @return List of Uploads that user can read
     * @throws Exception
     */
    List<Upload> findUploads(String user) throws Exception;

    /**
     * @param filterName Filter for Upload's description
     * @param user who performs operation
     * @return List of Uploads that user can read filtered by Upload's description
     * @throws Exception
     */
    List<Upload> findUploads(String filterName, String user) throws Exception;

    /**
     * @param categoryId Category's id
     * @param user who performs operation
     * @return List of Uploads that user can read filtered by Category defined by id
     * @throws Exception
     */
    List<Upload> findUploads(Long categoryId, String user) throws Exception;

    /**
     * @param id Upload's id
     * @param user who performs operation
     * @return Upload defined by id if user has rights to read
     * @throws Exception
     */
    Upload findUpload(Long id, String user) throws Exception;

    /*
     * Comments API
     */

    /**
     * Adds a Comment in a Post.
     *
     * @param postId Post's id
     * @param comment Comment to add
     * @param user who performs operation
     * @throws Exception
     */
    void add(Long postId, Comment comment, String user) throws Exception;

}
