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

package org.gatein.wcm.services;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.gatein.wcm.WcmAuthorizationException;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.WcmLockException;
import org.gatein.wcm.domain.*;

/**
 * Public API for org.gatein.wcm.domain.* model.
 * Basic CRUD operations and specific query operations.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Local
public interface WcmService {
	
	/*
	 * Categories API.
	 */

    /**
     * Persists a new Category into backend.
     * User has to have rights to write.
     * If Category has a parent, user has to be granted to write on parent's Category.
     * Category will inherit parent's ACL.
     * If Category has not parent, will have WRITE permission from user's write group.
     *
     * @param cat new Category to persist
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void create(Category cat, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Updates an existing Category.
     * User has to have rights to write.
     *
     * @param cat Category to update
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void update(Category cat, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Deletes an existing Category.
     * User has to have rights to write.
     * If user has rights into children, it deletes in cascade.
     * If user has not rights into children, children are detached from parent.
     *
     * @param id Category's id
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void deleteCategory(Long id, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * @param id Category's id
     * @param user UserWcm who performs operation
     * @return Category's defined by id or null if user has not rights to read or if Category doesn't exist
     * @throws WcmException
     */
    Category findCategory(Long id, UserWcm user) throws WcmException;

    /**
     * @param path Category's path
     * @param user UserWcm who performs operation
     * @return Category's defined by path or null if user has not rights to read or if Category doesn't exist
     * @throws WcmException
     */
    Category findCategory(String path, UserWcm user) throws WcmException;

    /**
     * @param user UserWcm who performs operation
     * @return List of Categories that user can read
     * @throws WcmException
     */
    List<Category> findCategories(UserWcm user) throws WcmException;

    /**
     * @param type Category's filter
     * @param user UserWcm who performs operation
     * @return List of Categories that user can read filtered by type
     * @throws WcmException
     */
	List<Category> findCategories(Character type, UserWcm user) throws WcmException;

    /**
     * @param user UserWcm who performs operation
     * @return List of Categories with no parent that user can read
     * @throws WcmException
     */
    List<Category> findRootCategories(UserWcm user) throws WcmException;

    /**
     * @param cat Parent category
     * @param user UserWcm who performs operation
     * @return List of children Categories for cat
     * @throws WcmException
     */
    List<Category> findChildren(Category cat, UserWcm user) throws WcmException;

    /**
     * @param id of Category
     * @param user UserWcm who performs operation
     * @return List of children categories for Category defined by id
     * @throws WcmException
     */
    List<Category> findChildren(Long id, UserWcm user) throws WcmException;

    /**
     * @param path Category's parent path
     * @param type Category's filter
     * @param user UserWcm who performs operation
     * @return List of children Categories for Category's parent defined by path and filtered by type
     * @throws WcmException
     */
    List<Category> findChildren(String path, Character type, UserWcm user) throws WcmException;

	/*
	 * Post API.
	 */

    /**
     * Persists a new Post into backend.
     * User has to have rights to write.
     *
     * @param post new Post to persist
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void create(Post post, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Updates an existing Post.
     * User has to have rights to write.
     *
     * @param post existing Post to update
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void update(Post post, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Deletes an existing Post.
     * User has to have rights to write.
     *
     * @param id Post's id to delete
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void deletePost(Long id, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Links a Post into a Category.
     * Post and Category have to exist.
     * User has to have rights to write both on Post and Category.
     *
     * @param post existing Post to link
     * @param cat existing Category to link
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void add(Post post, Category cat, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Unlinks Post from Category.
     * User has to have rights to write both on Post and Category.
     * Unlinks children from Category.
     *
     * @param postId Post's id to unlink
     * @param catId Category's id to unlink
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void removePostCategory(Long postId, Long catId, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Adds a new Comment into an existing Post.
     * User has to have rights to write.
     *
     * @param post existing Post where to add a new Comment
     * @param comment new Comment to add into Post
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void add(Post post, Comment comment) throws WcmAuthorizationException, WcmException;

    /**
     * Deletes an existing Comment from Post.
     * User has to have rights to write.
     *
     * @param post existing Post where to remove an existing Comment
     * @param comment existing Comment from Post
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void remove(Post post, Comment comment, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * @param id Post's id
     * @param user UserWcm who performs operation
     * @return Post defined by id if user has rights to read or null otherwise
     * @throws WcmException
     */
    Post findPost(Long id, UserWcm user) throws WcmException;

    /**
     * @param id Post's id
     * @param locale Locale used as a key for a Relationship
     * @param user UserWcm who performs operation
     * @return Post defined by id if user has rights to read or null otherwise
     * @throws WcmException
     */
    Post findPost(Long id, String locale, UserWcm user) throws WcmException;

    /**
     * @param categoryId Category's id
     * @param user UserWcm who performs operation
     * @return List of Posts linked with Category defined by categoryId that user can read
     * @throws WcmException
     */
    List<Post> findPosts(Long categoryId, UserWcm user) throws WcmException;

    /**
     * @param user UserWcm who performs operation
     * @return List of Posts that user can read
     * @throws WcmException
     */
    List<Post> findPosts(UserWcm user) throws WcmException;

    /**
     * @param categoryId Category's id
     * @param status Post's status
     * @param user UserWcm who performs operation
     * @return List of Posts linked by Category defined by categoryId that user can read filtered by Post's status
     * @throws WcmException
     */
    List<Post> findPosts(Long categoryId, Character status, UserWcm user) throws WcmException;

    /**
     * @param categoryId Category's id
     * @param locale Locale used as a key for a Relationship
     * @param status Post's status
     * @param user UserWcm who performs operation
     * @return List of Posts linked by Category defined by categoryId that user can read filtered by Post's status
     * @throws WcmException
     */
    List<Post> findPosts(Long categoryId, String locale, Character status, UserWcm user) throws WcmException;


    /**
     * @param filterName Filter for Post's title/name
     * @param user UserWcm who performs operation
     * @return List of Posts that user can read filtered by Post's title/name
     * @throws WcmException
     */
    List<Post> findPosts(String filterName, UserWcm user) throws WcmException;

    /**
     *
     * @param postId Post's id
     * @param user UserWcm who performs operation
     * @return List of versions numbers for Post defined by postId
     * @throws WcmException
     */
    List<Long> versionsPost(Long postId, UserWcm user) throws WcmException;

    /**
     * Changes version of Post entry.
     *
     * @param postId Post's id
     * @param version Version to change
     * @param user UserWcm who performs operation
     * @throws WcmException
     */
    void changeVersionPost(Long postId, Long version, UserWcm user) throws WcmException;

    /*
     * Acl API
     */

    /**
     * Removes an unlinked Acl from backend.
     * User has to have rights to write.
     *
     * @param acl unlinked Acl to remove
     * @param user UserWcm who performs operation
     * @throws WcmException
     */
    void remove(Acl acl, UserWcm user) throws WcmException;

    /*
     * Comments API
     */

    /**
     * Removes an unlinked Comment from backend.
     * User has to have rights to write.
     *
     * @param c unlinked Comment to remove
     * @param user UserWcm who performs operation
     * @throws WcmException
     */
    void remove(Comment c, UserWcm user) throws WcmException;

	/*
	 * Upload API.
	 */

    /**
     * Persists a new Upload into backend.
     * User has to have rights to write.
     *
     * @param upload new Upload to persist
     * @param is InputStream attached to upload
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void create(Upload upload, InputStream is, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Updates an existing Upload.
     * User has to have rights to write.
     *
     * @param upload existing Upload to update
     * @param is InputStream attached to upload
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void update(Upload upload, InputStream is, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Updates an existing Upload.
     * User has to have rights to write.
     *
     * @param upload existing Upload to update
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void update(Upload upload, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Deletes an existing Upload from backend.
     * User has to have rights to write.
     *
     * @param upload existing Upload to delete
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
	void delete(Upload upload, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Deletes an existing Upload from backend.
     * User has to have rights to write.
     *
     * @param id Upload's id
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void deleteUpload(Long id, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Links an Upload into a Category.
     * Upload and Category have to exist.
     * User has to have rights to write both on Upload and Category.
     *
     * @param upload existing Upload to link
     * @param cat existing Category to link
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void add(Upload upload, Category cat, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Unlinks Upload from Category.
     * User has to have rights to write both on Upload and Category.
     * Unlinks children from Category.
     *
     * @param uploadId Upload's id
     * @param catId Category's id
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void removeUploadCategory(Long uploadId, Long catId, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * @param id Upload's id
     * @param user UserWcm who performs operation
     * @return Upload defined by id if user has rights to read
     * @throws WcmException
     */
    Upload findUpload(Long id, UserWcm user) throws WcmException;

    /**
     * @param categoryId Category's id
     * @param user UserWcm who performs operation
     * @return List of Uploads that user can read filtered by Category defined by id
     * @throws WcmException
     */
    List<Upload> findUploads(Long categoryId, UserWcm user) throws WcmException;

    /**
     * @param user UserWcm who performs operation
     * @return List of Uploads that user can read
     * @throws WcmException
     */
    List<Upload> findUploads(UserWcm user) throws WcmException;

    /**
     * @param filterName Filter for Upload's description
     * @param user UserWcm who performs operation
     * @return List of Uploads that user can read filtered by Upload's description
     * @throws WcmException
     */
    List<Upload> findUploads(String filterName, UserWcm user) throws WcmException;

    /**
     *
     * @param uploadId Post's id
     * @param user UserWcm who performs operation
     * @return List of versions numbers for Upload defined by uploadId
     * @throws WcmException
     */
    List<Long> versionsUpload(Long uploadId, UserWcm user) throws WcmException;

    /**
     * Changes version of Upload entry.
     *
     * @param uploadId Upload's id
     * @param version
     * @param user UserWcm who performs operation
     * @throws WcmException
     */
    void changeVersionUpload(Long uploadId, Long version, UserWcm user) throws WcmException;

    /*
     * Template API
     */

    /**
     * Persists a new Template into the backend.
     * User has to have Wcm.GROUPS.MANAGER membership into Wcm.GROUPS.WCM GateIn group.
     *
     * @param temp new Template to persist
     * @param user UserWcm who performs operation
     * @see org.gatein.wcm.Wcm.GROUPS
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void create(Template temp, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Updates an existing Template.
     * User has to have Wcm.GROUPS.MANAGER membership into Wcm.GROUPS.WCM GateIn group.
     *
     * @param template existing Template to persist
     * @param user UserWcm who performs operation
     * @see org.gatein.wcm.Wcm.GROUPS
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void update(Template template, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Deletes an existing Template from backend.
     * User has to have Wcm.GROUPS.MANAGER membership into Wcm.GROUPS.WCM GateIn group.
     *
     * @param id Template's id
     * @param user UserWcm who performs operation
     * @see org.gatein.wcm.Wcm.GROUPS
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void deleteTemplate(Long id, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Links a Template into a Category.
     * Template and Category have to exist.
     * User has to have rights to write both on Upload and Category.
     *
     * @param template existing Template to link
     * @param cat existing Category to link
     * @param user UserWcm who performs operation
     * @see org.gatein.wcm.Wcm.GROUPS
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void add(Template template, Category cat, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Unlinks Template from Category.
     * User has to have rights to write both on Upload and Category.
     * Unlinks children from Category.
     *
     * @param templateId existing Template's id
     * @param catId existing Category's id
     * @param user UserWcm who performs operation
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    void removeTemplateCategory(Long templateId, Long catId, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * @param id Template's id
     * @param user UserWcm who performs operation
     * @return existing Template defined by id
     * @throws WcmException
     */
    Template findTemplate(Long id, UserWcm user) throws WcmException;

    /**
     * @param id Template's id
     * @param locale Locale used as a key for a Relationship
     * @param user UserWcm who performs operation
     * @return existing Template defined by id
     * @throws WcmException
     */
    Template findTemplate(Long id, String locale, UserWcm user) throws WcmException;


    /**
     * @param categoryId Category's id
     * @param user UserWcm who performs operation
     * @return List of Templates filtered by Category's id
     * @throws WcmException
     */
    List<Template> findTemplates(Long categoryId, UserWcm user) throws WcmException;

    /**
     * @param user UserWcm who performs operation
     * @return List of Templates from backend
     * @throws WcmException
     */
    List<Template> findTemplates(UserWcm user) throws WcmException;

    /**
     * @param filterName filter for Template's name
     * @param user UserWcm who performs operation
     * @return List of Templates filtered by Template's name
     * @throws WcmException
     */
    List<Template> findTemplates(String filterName, UserWcm user) throws WcmException;

    /**
     * @param templateId Post's id
     * @param user UserWcm who performs operation
     * @return List of versions numbers for Post defined by postId
     * @throws WcmException
     */
    List<Long> versionsTemplate(Long templateId, UserWcm user) throws WcmException;

    /**
     * Changes Template's entry.
     *
     * @param templateId
     * @param version
     * @param user UserWcm who performs operation
     * @throws WcmException
     */
    void changeVersionTemplate(Long templateId, Long version, UserWcm user) throws WcmException;

    /*
     * Relationships API
     */

    /**
     * Creates a soft relationship between Posts objects
     *
     * @param originId Post's id for origin object
     * @param key defines the relationship in the logic Post(originId) + key = Post(targetId)
     * @param targetId defining the target Post will be linked with Post defined by originId
     * @param user UserWcm who performs operation
     * @throws WcmException
     * @see Relationship
     * @see org.gatein.wcm.Wcm.RELATIONSHIP
     */
    void createRelationshipPost(Long originId, String key, Long targetId, UserWcm user) throws WcmException;

    /**
     * Removes a soft relationshp between Posts objects
     *
     * @param originId Post's id for origin object
     * @param key defines the relationship in the logic Post(originId) + key = Post(targetId)
     * @param user UserWcm who performs operation
     * @throws WcmException
     * @see Relationship
     * @see org.gatein.wcm.Wcm.RELATIONSHIP
     */
    void removeRelationshipPost(Long originId, String key, UserWcm user) throws WcmException;

    /**
     * Returns list of Relationship attached with Post
     *
     * @param postId Post's id
     * @param user UserWcm who performs operation
     * @return list of Relationship linked with Post
     * @throws WcmException
     * @see Relationship
     */
    List<Relationship> findRelationshipsPost(Long postId, UserWcm user) throws WcmException;

    /**
     * Returns list of Posts linked with Post with Relationship object.
     * Relationship object is a soft relation, so we manage it explicitly.
     *
     * @param postId Post's id
     * @param user UserWcm who performs operation
     * @return list of Posts linked with Post via Relationships
     * @throws WcmException
     */
    List<Post> findPostsRelationshipPost(Long postId, UserWcm user) throws WcmException;

    /**
     * Creates a soft relationship between Templates objects
     *
     * @param originId Template's id for origin object
     * @param key defines the relationship in the logic Template(originId) + key = Template(targetId)
     * @param targetId defining the target Template will be linked with Post defined by originId
     * @param user UserWcm who performs operation
     * @throws WcmException
     * @see Relationship
     * @see org.gatein.wcm.Wcm.RELATIONSHIP
     */
    void createRelationshipTemplate(Long originId, String key, Long targetId, UserWcm user) throws WcmException;

    /**
     * Removes a soft relationshp between Template objects
     *
     * @param originId Template's id for origin object
     * @param key defines the relationship in the logic Template(originId) + key = Template(targetId)
     * @param user UserWcm who performs operation
     * @throws WcmException
     * @see Relationship
     * @see org.gatein.wcm.Wcm.RELATIONSHIP
     */
    void removeRelationshipTemplate(Long originId, String key, UserWcm user) throws WcmException;

    /**
     * Returns list of Relationship attached with Template
     *
     * @param templateId Template's id
     * @param user UserWcm who performs operation
     * @return list of Relationship linked with Template
     * @throws WcmException
     * @see Relationship
     */
    List<Relationship> findRelationshipsTemplate(Long templateId, UserWcm user) throws WcmException;

    /**
     * Returns list of Templates linked with Post with Relationship object.
     * Relationship object is a soft relation, so we manage it explicitly.
     *
     * @param templateId Template's id
     * @param user UserWcm who performs operation
     * @return list of Templates linked with Post via Relationships
     * @throws WcmException
     */
    List<Template> findTemplatesRelationshipTemplate(Long templateId, UserWcm user) throws WcmException;

    /*
     * Locks API
     */

    /**
     * Locks and item to prevent concurrency issues in a multi user authoring scenario.
     *
     * @param originId Item's id to lock
     * @param type Type of item to lock
     * @param user UserWcm who performs operation
     * @throws WcmLockException if item has been locked previously
     * @throws WcmException
     * @see org.gatein.wcm.Wcm.LOCK
     * @see Lock
     */
    void lock(Long originId, Character type, UserWcm user) throws WcmLockException, WcmException;

    /**
     * Unlocks and item to prevent concurrency issues in a multi user authoring scenario.
     * Only user who locked the item has right to unlock it.
     *
     * @param originId Item's id to unlock
     * @param type Type of item to unlock
     * @param user UserWcm who performs operation
     * @throws WcmLockException if item has been locked previously by a different user
     * @throws WcmException
     * @see org.gatein.wcm.Wcm.LOCK
     * @see Lock
     */
    void unlock(Long originId, Character type, UserWcm user) throws WcmLockException, WcmException;

    /**
     * Removes a lock defined by a different user.
     * Only admin users can remove locks.
     *
     * @param originId Item's id to unlock
     * @param type Type of item to unlock
     * @param user UserWcm who performs operation
     * @throws WcmLockException if item has been locked previously by a different user
     * @throws WcmException
     * @see org.gatein.wcm.Wcm.LOCK
     * @see Lock
     */
    void removeLock(Long originId, Character type, UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * Returns list of Locks defined.
     * Only admin users can retrieve locks list.
     *
     * @param user UserWcm who performs operation
     * @return list of locks defined.
     * @throws WcmException
     */
    List<Lock> findLocks(UserWcm user) throws WcmAuthorizationException, WcmException;

    /**
     * @param locks list of Locks
     * @param user UserWcm who performs operation
     * @return list of Objects attached to Locks
     * @throws WcmAuthorizationException
     * @throws WcmException
     */
    Map<Long, Object> findLocksObjects(List<Lock> locks, UserWcm user) throws WcmAuthorizationException, WcmException;
}
