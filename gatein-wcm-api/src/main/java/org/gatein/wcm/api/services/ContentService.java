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
package org.gatein.wcm.api.services;

import java.io.InputStream;
import java.util.List;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.security.ACE;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.api.services.exceptions.PublishException;

/**
 *
 * Defines content API services around org.gatein.wcm.api.model. <br>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface ContentService {

    /*
     * Basic content manipulation API block.
     *
     * Context:
     *
     * - A user is logged into portal and portlet needs invoke API to create and update content.
     */

    /**
     *
     * Creates a new text content in the default repository.
     *
     * @param id - Key under which to store the content.
     * @param locale - Locale under content is stored.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param html - HTML content as string.
     * @param encoding - Specific encoding, by default UTF8.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (then user should use updateSimpleContent to create a new
     *         version).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content createTextContent(String id, String locale, String location, String html, String encoding)
            throws ContentException, ContentIOException, ContentSecurityException;

    /**
     *
     * Creates a new folder in the default repository.
     *
     * @param id - Key under which to store the folder.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content createFolder(String id, String location) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Creates new file resource in the default repository.
     *
     * @param id - Key under which to store the resource.
     * @param locale - Locale under content is stored.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param contentType - ContentType's file.
     * @param size - Size's file.
     * @param fileName - Name's file.
     * @param content - Source of the file.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content createBinaryContent(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content) throws ContentException, ContentIOException, ContentSecurityException;

    /**
     *
     * Retrieves a list of content from a specified location. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @return List of content under specified location.
     * @throws ContentException if content doesn't exist in the location.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to access content under specified location.
     */
    public Content getContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Retrieves a list of locales available for a specified content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return List of locales.
     * @throws ContentException if content doesn't exist in the location.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to access content under specified location.
     */
    public List<String> getContentLocales(String location) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Updates a existing text content in the default repository.
     *
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @param html - HTML content as string.
     * @param encoding - Specific encoding, by default UTF8.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the location doesn't exists in the repository.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateTextContent(String location, String locale, String html, String encoding) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Moves an existing folder in the default repository.
     *
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateFolderLocation(String location, String locale, String newLocation) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Renames an existing folder in the default repository.
     *
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateFolderName(String location, String locale, String newName) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Updates new file resource in the default repository.
     *
     * @param id - Key under which to store the resource.
     * @param locale - Locale under content is stored.
     * @param location - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param contentType - ContentType's file.
     * @param size - Size's file.
     * @param fileName - Name's file.
     * @param content - Source of the file.
     * @return Content updated (if ok), null (if error).
     * @throws ContentException if the id doesn't exist in the repository
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create content under specified location.
     */
    public Content updateBinaryContent(String location, String locale, String contentType, Long size, String fileName,
            InputStream content) throws ContentException, ContentIOException, ContentSecurityException;

    /**
     * Deletes content from a specified location. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale version of the content to remove.
     * @return Current location of the content, or parent location if all locales were removed.
     * @throws ContentException if content doesn't exist
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to modify content under specified location.
     */
    public String deleteContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException;

    /*
     * Basic metadata manipulation API block.
     *
     * Context:
     *
     * - A user admin wants to organize content in categories
     */

    /**
     *
     * Creates new Category in the repository. <br>
     * Categories can be organized in a hierarchical tree of categories parents and children.
     *
     * @param id - Category id.
     * @param locale - Locale of category.
     * @param description - Category description.
     * @param categoryLocation - Location where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Category created (if ok), null (if error).
     * @throws ContentException if the id exists (categories are not versionable items).
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public Category createCategory(String id, String locale, String description, String categoryLocation)
            throws ContentException, ContentIOException, ContentSecurityException;

    /**
     *
     * Updates existing Category in the repository. <br>
     * Categories can be organized in a hierarchical tree of categories parents and children.
     *
     * @param categoryLocation - Location where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category.
     * @param description - Category description.
     * @return Category created (if ok), null (if error).
     * @throws ContentException if the categoryLocation doesn't exist
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public Category updateCategoryDescription(String categoryLocation, String locale, String description)
            throws ContentException, ContentIOException, ContentSecurityException;

    /**
     *
     * Updates an existing Category into a new categoryLocation. <br>
     *
     * @param categoryLocation - Location where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category
     * @param newLocation - New Location where category will be moved
     * @return Category updated
     * @throws ContentException if the categoryLocation doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public Category updateCategoryLocation(String categoryLocation, String locale, String newLocation) throws ContentException,
            ContentIOException, ContentSecurityException;

    public List<Category> getCategories(String categoryLocation, String locale) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Attaches a Category in a Content.
     *
     * @param location - Content location id
     * @param categoryLocation - Category location id.
     * @return Content updated.
     * @throws ContentException if content or category don't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public void addContentCategory(String location, String categoryLocation) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Deletes a Category from repository.
     *
     * @param idCategory - Category ID.
     * @return parent Category.
     * @throws ContentException if category has been asigned to Content.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public void deleteCategory(String categoryLocation) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
    *
    * Deletes a Category from repository.
    *
    * @param idCategory - Category ID.
    * @return parent Category.
    * @throws ContentException if category has been asigned to Content.
    * @throws ContentIOException if any IO related problem with repository.
    * @throws ContentSecurityException if user has not been granted to create categories.
    */
   public Category deleteCategory(String categoryLocation, String locale) throws ContentException, ContentIOException,
           ContentSecurityException;

    /**
     *
     * Get Content filtered by categories. <br>
     *
     * @param categories - List of Categories to filter Content.
     * @param location - Filter for location. Method will search under specified location. "/" for whole repository. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Filter for locale.
     * @return List of Content.
     * @throws ContentException if location it doesn's exist
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create categories.
     */
    public List<Content> getContent(List<Category> categories, String location, String locale) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Creates a comment under the specified Content location. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to add comment
     * @param comment - Comment to add
     * @return Content with comment updated.
     * @throws ContentException if content doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create comments.
     */
    public Content createContentComment(String location, String locale, String comment) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Removes a comment under the specified Content location. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to add comment
     * @param comment - Comment to add
     * @return Content with comment updated.
     * @throws ContentException if content doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create comments.
     */
    public Content deleteContentComment(String location, String locale, String idComment) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Creates a property in the form KEY/VALUE to a Content. <br>
     * Properties are shared between locales of same Content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @param value - Value
     * @return Content (default locale) with properties updated.
     * @throws ContentException if content doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create properties.
     */
    public Content createContentProperty(String location, String name, String value) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Modifies a property in the form KEY/VALUE to a Content. <br>
     * Properties are shared between locales of same Content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @param value - Value
     * @return Content (default locale) with properties updated.
     * @throws ContentException if content doesn't exist or property doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create properties.
     */
    public Content updateContentProperty(String location, String name, String value) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Deletes a property in the form KEY/VALUE to a Content. <br>
     * Properties are shared between locales of same Content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @return Content (default locale) with properties updated.
     * @throws ContentException if content doesn't exist or property doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to create properties.
     */
    public Content deleteContentProperty(String location, String name) throws ContentException, ContentIOException,
            ContentSecurityException;

    /*
     * Security API.
     *
     * Context:
     *
     * - A user with rights can define new ACE and ACL for content. - Admin user is defined at configuration level.
     */

    /**
     *
     * Adds an authorization to a specific content. <br>
     *
     * in org.gatein.wcm a Principal can represent: <br>
     * - A user. <br>
     * - A group. <br>
     * - A specific role in specific group. <br>
     * - A specific role in whatever group. <br>
     * <br>
     * An ACE represents a link between a Principal and their authorization rights. <br>
     * We define the following permissions: <br>
     * - NONE: No access to the content. <br>
     * - READ: Access to the content for read. <br>
     * - COMMENTS: Access to the content for read and add comments. <br>
     * - WRITE: Access to the content to modify the content but it can not delete it, only versioning. <br>
     * - ALL: Principal can delete content. <br>
     * <br>
     * All ACL and ACE applies to all locale version of the content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param principal - Principal of ACE.
     * @param permission - Type of permission for this principal.
     * @return Content (default locale) with ACL updated.
     * @throws ContentException if content doesn't exist or property doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to add ACLs.
     */
    public Content createContentACE(String location, Principal principal, ACE.PermissionType permission)
            throws ContentException, ContentIOException, ContentSecurityException;

    /**
     *
     * Remove an authorization to a specific content. <br>
     *
     * in org.gatein.wcm a Principal can represent: <br>
     * - A user. <br>
     * - A group. <br>
     * - A specific role in specific group. <br>
     * - A specific role in whatever group. <br>
     * <br>
     * An ACE represents a link between a Principal and their authorization rights. <br>
     * We define the following permissions: <br>
     * - NONE: No access to the content. <br>
     * - READ: Access to the content for read. <br>
     * - COMMENTS: Access to the content for read and add comments. <br>
     * - WRITE: Access to the content to modify the content but it can not delete it, only versioning. <br>
     * - ALL: Principal can delete content. <br>
     * <br>
     * All ACL and ACE applies to all locale version of the content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param principal - Principal of ACE.
     * @return Content (default locale) with ACL updated.
     * @throws ContentException if content doesn't exist or property doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to add ACLs.
     */

    public Content deleteContentACE(String location, Principal principal) throws ContentException, ContentIOException,
            ContentSecurityException;

    /*
     * Queries API.
     */

    // It's highly dependent of the implementation.
    // I should start just with full text queries, in order to be more agnostic in the impl and also the more useful queries.
    // I would try to study what to index and what not.

    /*
     * Versioning API.
     *
     * Context: - CRUD operations over content modified implicitly version of the content. - Also a User can explicitly work
     * with versioning updating
     */

    /**
     *
     * Retrieves a list of versions available for a specified content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to retrieve number of versions. <br>
     * @return List of versions.
     * @throws ContentException if content doesn't exist in the location.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to access content under specified location.
     */
    public List<Integer> getContentVersions(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Retrieves a Content version an block it for modify. <br>
     * getContent() is designed mostly for reading and it doesn't block the content. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to checkout content.
     * @param version - Version of the Content to retrieve.
     * @return List Content blocked to be modified.
     * @throws ContentException if content or version doesn't exist.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to write in the content.
     */
    public List<Content> checkOut(String location, String locale, Integer version) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Update and release a blocked Content. <br>
     * This method update the version number of the content. <br>
     *
     * Differences between: <br>
     * <p>
     * <b>modifyContent(Content)</b><br>
     * <li>Every user with rights can get a Content and modify.
     * <li>Each time user write on the content it creates a new version.
     * <li>Two users can get same content, write at same time, last one, will update the content.
     * </p>
     * <p>
     * <b>checkOutContent(Content)</b><br>
     * <li>Only user that make a previous checkIn(Content) can write in the content.
     * <li>Content is blocked until is released by the lock's owner or an administrator.
     * <li>
     *
     * @param content
     * @return Content to be updated.
     * @throws ContentException
     * @throws ContentIOException
     * @throws ContentSecurityException
     */
    public Content checkIn(Content content) throws ContentException, ContentIOException, ContentSecurityException;

    /**
     *
     * Retrieves a list of content from a specified location. <br>
     * Also specify specific version to retrieve.
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @param version - Content version to retrieve.
     * @return List of content under specified location.
     * @throws ContentException if content doesn't exist in the location.
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to access content under specified location.
     */
    public List<Content> getContent(String location, String locale, Integer version) throws ContentException,
            ContentIOException, ContentSecurityException;

    /**
     *
     * Removes content from a specified location, locale and version. <br>
     * Differences between: <br>
     * <p>
     * <b>removeContent(String, String)</b>
     * <li>Remove all versions for a given locale.
     * </p>
     * <p>
     * <b>removeContent(String, String, Integer)</b>
     * <li>Remove just version specified for a given locale.
     * </p>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale version of the content to remove.
     * @param version - Content version to remove.
     * @return Current location of the content, or parent location if all locales were removed.
     * @throws ContentException if content doesn't exist
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to modify content under specified location.
     */
    public String deleteContent(String location, String locale, Integer version) throws ContentException, ContentIOException,
            ContentSecurityException;

    /**
     *
     * Removes content from a specified location. <br>
     * All locales and versions are removed. <br>
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Parent location of the removed content
     * @throws ContentException if content doesn't exist
     * @throws ContentIOException if any IO related problem with repository.
     * @throws ContentSecurityException if user has not been granted to modify content under specified location.
     */
    public String deleteContent(String location) throws ContentException, ContentIOException, ContentSecurityException;

    /*
     * Workflow API.
     *
     * Context: - Task manager todo_list for basic approvals. - Possibility to include own processes.
     */
    public Content submitPublish(Content content) throws PublishException, ContentIOException, ContentSecurityException;

    public Content submitDraft(Content content) throws PublishException, ContentIOException, ContentSecurityException;

    /*
     * Session API.
     *
     * Context:
     *
     * - A user has finished and release and flush state info.
     */

    public void closeSession() throws ContentException, ContentIOException;
}
