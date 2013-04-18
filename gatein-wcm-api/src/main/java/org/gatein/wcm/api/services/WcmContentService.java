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

import org.gatein.wcm.api.model.content.WcmBinaryObject;
import org.gatein.wcm.api.model.content.WcmFolder;
import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmTextObject;
import org.gatein.wcm.api.model.metadata.WcmCategory;
import org.gatein.wcm.api.model.security.WcmAce;
import org.gatein.wcm.api.model.security.WcmPrincipal;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.api.services.exceptions.WcmPublishException;

/**
 *
 * Defines content API services for org.gatein.wcm.api.model. <br>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WcmContentService {

    /**
     *
     * Creates a new text content in the default repository.
     *
     * @param id - Key under which to store the content.
     * @param locale - Locale under content is stored.
     * @param path - Path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param html - HTML content as string.
     * @return Content updated (if ok), null (if error).
     * @throws WcmContentException if the id exists in the repository (then user should use updateTextContent to create a new
     *         version).
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create content under specified location.
     */
    WcmTextObject createTextContent(String id, String locale, String path, String html) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Creates a new folder in the default repository.
     *
     * @param id - Key under which to store the folder.
     * @param path - Path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Content updated (if ok), null (if error).
     * @throws WcmContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create content under specified location.
     */
    WcmFolder createFolder(String id, String path) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Creates new file resource in the default repository.
     *
     * @param id - Key under which to store the resource.
     * @param locale - Locale under content is stored.
     * @param path - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param contentType - MIME Type content type
     * @param size - Size's file.
     * @param fileName - Name's file.
     * @param content - Source of the file.
     * @return Content updated (if ok), null (if error).
     * @throws WcmContentException if the id exists in the repository
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create content under specified location.
     */
    WcmBinaryObject createBinaryContent(String id, String locale, String path, String contentType, long size, String fileName,
            InputStream content) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Retrieves a content from a specified path. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        path with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @return List of content under specified location.
     * @throws WcmContentException if content doesn't exist in the location.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to access content under specified location.
     */
    WcmObject getContent(String path, String locale) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Retrieves a list of locales available for a specified content. <br>
     *
     * @param path - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return List of locales.
     * @throws WcmContentException if content doesn't exist in the location.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to access content under specified location.
     */
    List<String> getContentLocales(String path) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Updates a existing text content in the default repository.
     *
     * @param path - Location where to update the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @param html - HTML content as string.
     * @return Content updated (if ok), null (if error).
     * @throws WcmContentException if the location doesn't exists in the repository.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create content under specified location.
     */
    WcmTextObject updateTextContent(String path, String locale, String html) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Moves an existing folder in the default repository.
     *
     * @param path - Path where content is stored the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @param newPath - New path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Content updated (if ok), null (if error).
     * @throws WcmContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create content under specified location.
     */
    WcmFolder updateFolderLocation(String path, String locale, String newPath) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Renames an existing folder in the default repository.
     *
     * @param path - Path where content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @return Content updated (if ok), null (if error).
     * @throws WcmContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create content under specified location.
     */
    WcmFolder updateFolderName(String path, String locale, String newName) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Updates new file resource in the default repository.
     *
     * @param id - Key under which to store the resource.
     * @param locale - Locale under content is stored.
     * @param path - Location where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param contentType - ContentType's file.
     * @param size - Size's file.
     * @param fileName - Name's file.
     * @param content - Source of the file.
     * @return Content updated (if ok), null (if error).
     * @throws WcmContentException if the id doesn't exist in the repository
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create content under specified location.
     */
    WcmBinaryObject updateBinaryContent(String path, String locale, String contentType, long size, String fileName,
            InputStream content) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     * Deletes content from a specified path. <br>
     *
     * @param path - PAth where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale version of the content to remove.
     * @return Current path of the content, or parent path if all locales were removed.
     * @throws WcmContentException if content doesn't exist
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to modify content under specified location.
     */
    String deleteContent(String path, String locale) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Creates new Category in the repository. <br />
     * Categories can be organized in a hierarchical tree of categories parents and children. <br />
     * An admin user can organize content in categories. <br />
     *
     * @param id - Category id.
     * @param locale - Locale of category.
     * @param description - Category description.
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Category created (if ok), null (if error).
     * @throws WcmContentException if the id exists (categories are not versionable items).
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create categories.
     */
    WcmCategory createCategory(String id, String locale, String description, String categoryPath) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Updates existing Category in the repository. <br>
     * Categories can be organized in a hierarchical tree of categories parents and children. <br />
     * An admin user can organize content in categories. <br />
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category.
     * @param description - Category description.
     * @return Category created (if ok), null (if error).
     * @throws WcmContentException if the categoryLocation doesn't exist
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create categories.
     */
    WcmCategory updateCategoryDescription(String categoryPath, String locale, String description) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Updates an existing Category into a new category path. <br>
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category
     * @param newPath - New path where category will be moved
     * @return Category updated
     * @throws WcmContentException if the categoryLocation doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create categories.
     */
    WcmCategory updateCategoryLocation(String categoryPath, String locale, String newPath) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;


    /**
     *
     * Get all categories for a specific path and locale.
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category
     * @return List of categories
     * @throws WcmContentException
     * @throws WcmContentIOException
     * @throws WcmContentSecurityException
     */
    List<WcmCategory> getCategories(String categoryPath, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Attaches a Category in a Content.
     *
     * @param path - Content path.
     * @param categoryPath - Category path.
     * @return Content updated.
     * @throws WcmContentException if content or category don't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create categories.
     */
    void addContentCategory(String path, String categoryPath) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Deletes a Category from repository.
     *
     * @param categoryPath - Category path.
     * @return parent Category.
     * @throws WcmContentException if category has been asigned to Content.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create categories.
     */
    void deleteCategory(String categoryPath) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Deletes a Category from repository.
     *
     * @param categoryPath - Category path.
     * @return parent Category.
     * @throws WcmContentException if category has been asigned to Content.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create categories.
     */
    WcmCategory deleteCategory(String categoryPath, String locale) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Get Content filtered by categories. <br>
     *
     * @param categories - List of Categories to filter Content.
     * @param path - Filter for location. Method will search under specified path. "/" for whole repository. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Filter for locale.
     * @return List of Content.
     * @throws WcmContentException if location it doesn's exist
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create categories.
     */
    List<WcmObject> getContent(List<WcmCategory> categories, String path, String locale) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Creates a comment under the specified Content path. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to add comment
     * @param comment - Comment to add
     * @return Content with comment updated.
     * @throws WcmContentException if content doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create comments.
     */
    WcmObject createContentComment(String path, String locale, String comment) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Removes a comment under the specified Content path. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to add comment
     * @param comment - Comment to add
     * @return Content with comment updated.
     * @throws WcmContentException if content doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create comments.
     */
    WcmObject deleteContentComment(String path, String locale, String idComment) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Creates a property in the form {KEY,LOCALE}/VALUE to a Content. <br>
     * Properties are specific for each locale. <br>
     * Example: <br>
     * createContentProperty("/mycontent","en","title", "Title in english"); <br>
     * createContentProperty("/mycontent","es","title", "Título en español"); <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @param value - Value
     * @return Content (default locale) with properties updated.
     * @throws WcmContentException if content doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create properties.
     */
    WcmObject createContentProperty(String path, String locale, String name, String value) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Modifies a property in the form {KEY,LOCALE}/VALUE to a Content. <br>
     * Properties are specific for each locale. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @param value - Value
     * @return Content (default locale) with properties updated.
     * @throws WcmContentException if content doesn't exist or property doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create properties.
     */
    WcmObject updateContentProperty(String path, String locale, String name, String value) throws WcmContentException,
            WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Deletes a property in the form {KEY,LOCALE}/VALUE to a Content. <br>
     * Properties are shared between locales of same Content. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @return Content (default locale) with properties updated.
     * @throws WcmContentException if content doesn't exist or property doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to create properties.
     */
    WcmObject deleteContentProperty(String path, String locale, String name) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

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
     * An WcmAce represents a link between a Principal and their authorization rights. <br>
     * We define the following permissions: <br>
     * - NONE: No access to the content. <br>
     * - READ: Access to the content for read. <br>
     * - COMMENTS: Access to the content for read and add comments. <br>
     * - WRITE: Access to the content to modify the content but it can not delete it, only versioning. <br>
     * - ALL: Principal can delete content. <br>
     * <br>
     * All WcmAcl and WcmAce applies to all locale version of the content. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param principal - Principal of ACE.
     * @param permission - Type of permission for this principal.
     * @return Content (default locale) with ACL updated.
     * @throws WcmContentException if content doesn't exist or property doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to add ACLs.
     */
    WcmObject createContentAce(String path, String locale, String name, WcmPrincipal.PrincipalType principal,
            WcmAce.PermissionType permission) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

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
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param principal - Principal of ACE.
     * @return Content (default locale) with ACL updated.
     * @throws WcmContentException if content doesn't exist or property doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to add ACLs.
     */
    WcmObject deleteContentAce(String path, String locale, String name) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Retrieves a list of versions available for a specified content. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale to retrieve number of versions. <br>
     * @return List of versions or null if location is not versionable
     * @throws WcmContentException if content doesn't exist in the location.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to access content under specified location.
     */
    List<String> getContentVersions(String path) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Restores a Content version.
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param version - Version of the Content to restore.
     * @throws WcmContentException if content or version doesn't exist.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to write in the content.
     */
    void restore(String path, String version) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Retrieves a list of content from a specified path. <br>
     * Also specify specific version to retrieve.
     *
     * @param location - Location where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale under content is stored.
     * @param version - Content version to retrieve.
     * @return List of content under specified location.
     * @throws WcmContentException if content doesn't exist in the location.
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to access content under specified location.
     */
    WcmObject getContent(String path, String locale, String version) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Removes content from a specified path and version. <br>
     * Differences between: <br>
     * <p>
     * <b>deleteContent(String, String)</b>
     * <li>Remove all versions for a given locale.
     * </p>
     * <p>
     * <b>deleteContentVersion(String, String)</b>
     * <li>Remove just version specified for a given locale.
     * </p>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale version of the content to remove.
     * @param version - Content version to remove.
     * @return Current location of the content, or parent location if all locales were removed.
     * @throws WcmContentException if content doesn't exist
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to modify content under specified location.
     */
    String deleteContentVersion(String path, String version) throws WcmContentException, WcmContentIOException,
            WcmContentSecurityException;

    /**
     *
     * Removes content from a specified location. <br>
     * All locales and versions are removed. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Parent location of the removed content
     * @throws WcmContentException if content doesn't exist
     * @throws WcmContentIOException if any IO related problem with repository.
     * @throws WcmContentSecurityException if user has not been granted to modify content under specified location.
     */
    String deleteContent(String path) throws WcmContentException, WcmContentIOException, WcmContentSecurityException;

    /*
     * Workflow API.
     *
     * Context: - Task manager todo_list for basic approvals. - Possibility to include own processes.
     */
    WcmObject submitPublish(WcmObject content) throws WcmPublishException, WcmContentIOException, WcmContentSecurityException;

    WcmObject submitDraft(WcmObject content) throws WcmPublishException, WcmContentIOException, WcmContentSecurityException;

    /**
     *
     * Close WCM session and flush info. <br>
     *
     * @throws WcmContentIOException if any IO related problem with repository.
     */
    void closeSession() throws WcmContentIOException;
}
