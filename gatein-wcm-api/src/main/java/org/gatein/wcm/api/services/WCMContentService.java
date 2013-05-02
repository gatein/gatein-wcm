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
import java.util.Set;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMFolder;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;
import org.gatein.wcm.api.model.metadata.WCMCategory;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipal;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.services.exceptions.WCMContentException;
import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;

/**
 *
 * Defines content API services for org.gatein.wcm.api.model. <br>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WCMContentService {

    /**
     *
     * Creates a new text content in the repository/workspace.
     *
     * @param id - Key under which to store the content.
     * @param locale - Locale of the content.
     * @param path - Path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param html - HTML content as string.
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id exists in the repository (then user should use updateTextContent to create a new
     *         version).
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified path.
     */
    WCMTextDocument createTextDocument(String id, String locale, String path, String content) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Creates a new text content in the default repository using default locale.
     *
     * @param id - Key under which to store the content.
     * @param path - Path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param html - HTML content as string.
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id exists in the repository (then user should use updateTextContent to create a new
     *         version).
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified path.
     */
    WCMTextDocument createTextDocument(String id, String path, String html) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Creates a new folder in the default repository.
     *
     * @param id - Key under which to store the folder.
     * @param path - Path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id exists in the repository (folder can not be updated, folder gets latest version of
     *         their most recent item).
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified path.
     */
    WCMFolder createFolder(String id, String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Creates new file resource in the default repository.
     *
     * @param id - Key under which to store the resource.
     * @param locale - Locale of the file
     * @param path - Path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param mimeType - MIME Type content type
     * @param size - Size's file.
     * @param fileName - Name's file.
     * @param content - Source of the file.
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id exists in the repository
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified path.
     */
    WCMBinaryDocument createBinaryDocument(String id, String locale, String path, String mimeType, long size, String fileName,
            InputStream content) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Creates new file resource in the default repository using default locale.
     *
     * @param id - Key under which to store the resource.
     * @param locale - Locale under content is stored.
     * @param path - Path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param mimeType - MIME Type content type
     * @param size - Size's file.
     * @param fileName - Name's file.
     * @param content - Source of the file.
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id exists in the repository
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified path.
     */
    WCMBinaryDocument createBinaryDocument(String id, String path, String mimeType, long size, String fileName,
            InputStream content) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Retrieves a content from a specified path. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        path with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Content under specified location.
     * @throws WCMContentException if content doesn't exist in the path.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to access content under specified path.
     */
    WCMObject getContent(String path) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     * Creates a relationship between two existing contents. <br>
     * This relationship is specified by a "key". <br>
     * This key can be used to specified a locale or other kind of relationships. <br>
     * For example: <br>
     * We have these 2 contents in the following paths /folder1/folder2/content1 /other1/other2/othercontent3
     *
     * We would like to define a relationship to define that second content is a translation of first one: <br>
     * createContentRelation("/folder1/folder2/content1", "/other1/other2/othercontent3", "es"); <br>
     *
     * Then we can get the relationship invoking: <br>
     * getContent("/folder1/folder2/content1", "es") = WCMObject() of /other1/other2/othercontent3 <br>
     * This way is not only used for localization, it can be use for different semantics type of relationships.<br>
     * The relationship is only in one direction, we can define both directions as:
     * createContentRelation("/other1/other2/othercontent3", "/folder1/folder2/content1", "en"); <br>
     *
     * @param sourcePath - Path of source content to link in the relationship
     * @param targetPath - Path of target content to link in the relationship
     * @param sourceKey - Key to link
     * @throws WCMContentException - If source or target content doesn't exist in the path.
     * @throws WCMContentIOException - If any IO related problem with repository.
     * @throws WCMContentSecurityException - If user has not been granted to access content under specified path.
     */
    void createContentRelation(String sourcePath, String targetPath, String sourceKey) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Retrieves a linked content from a specified path and key. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        path with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param key - Key under the linked content is stored
     * @return Content under specified location or null if there is not linked content.
     * @throws WCMContentException if content doesn't exist in the path.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to access content under specified path.
     */
    WCMObject getContent(String path, String key) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Retrieves a list of relation keys available for a specified content. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return List of keys.
     * @throws WCMContentException if content doesn't exist in the path.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to access content under specified path.
     */
    Set<String> getContentRelationKeys(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Updates a existing text content in the default repository.
     * <p>
     * It creates a new version in the repository.
     * <p>
     * @param path - Path where to update the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param doc - WCMTextDocument to update.
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the path doesn't exists in the repository.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to write content under specified path.
     */
    WCMTextDocument updateTextDocument(String path, WCMTextDocument doc) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Moves an existing content in the default repository.
     * <p>
     *
     * @param path - Path where content is stored the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param newParentPath - New parent path where to store the content. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id exists in the repository.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified path.
     */
    WCMObject updateContentPath(String path, String newParentPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Renames an existing content in the default repository. <br>
     *
     * @param path - Path where content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id exists in the repository.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified path.
     */
    WCMObject updateContentName(String path, String newName) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Updates new file resource in the default repository.
     * <p>
     * It creates a new version in the repository.
     * <p>
     *
     * @param path - Path where content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param doc - WCMBinaryDocument to update
     * @return Content updated (if ok), null (if error).
     * @throws WCMContentException if the id doesn't exist in the repository
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create content under specified location.
     */
    WCMBinaryDocument updateBinaryDocument(String path, WCMBinaryDocument doc) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     * Deletes content from a specified path. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Parent path
     * @throws WCMContentException if content doesn't exist
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to modify content under specified path.
     */
    String deleteContent(String path) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Creates new Category in the repository.
     * <p>
     * Categories can be organized in a hierarchical tree of categories parents and children. <br>
     * An admin user can organize content in categories.
     * <p>
     *
     * @param id - Category id.
     * @param locale - Locale of category.
     * @param description - Category description.
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Category created (if ok), null (if error).
     * @throws WCMContentException if the id exists.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    WCMCategory createCategory(String id, String locale, String description, String categoryPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Creates new Category in the repository under default locale.
     * <p>
     * Categories can be organized in a hierarchical tree of categories parents and children. <br>
     * An admin user can organize content in categories.
     * <p>
     *
     * @param id - Category id.
     * @param description - Category description.
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Category created (if ok), null (if error).
     * @throws WCMContentException if the id exists
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    WCMCategory createCategory(String id, String description, String categoryPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Updates existing Category in the repository.
     * <p>
     * Categories can be organized in a hierarchical tree of categories parents and children. <br>
     * An admin user can organize content in categories.
     * <p>
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category.
     * @param description - Category description.
     * @return Category created (if ok), null (if error).
     * @throws WCMContentException if the categoryPath doesn't exist
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    WCMCategory updateCategoryDescription(String categoryPath, String locale, String description) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Updates existing Category in the repository under default locale. <br>
     * Categories can be organized in a hierarchical tree of categories parents and children. <br />
     * An admin user can organize content in categories. <br />
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param description - Category description.
     * @return Category created (if ok), null (if error).
     * @throws WCMContentException if the categoryPath doesn't exist
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    WCMCategory updateCategoryDescription(String categoryPath, String description) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Updates an existing Category into a new category path.
     * <p>
     * It's moved for all locales.
     * <p>
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param newParentCategoryPath - New parent path where category will be moved
     * @throws WCMContentException if the categoryPath doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    void updateCategoryPath(String categoryPath, String newParentCategoryPath) throws WCMContentException,
            WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Get all categories under a specific path and default locale.
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param locale - Locale of category
     * @return List of categories
     * @throws WCMContentException if the categoryPath doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to read categories.
     */
    List<WCMCategory> getCategories(String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Get category from a specific path.
     *
     * @param categoryPath - Path where the category is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return Category specified or null if doesn't exist or root
     * @throws WCMContentException if the categoryPath doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to read categories.
     */
    WCMCategory getCategory(String categoryPath) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     * Get categories attached to a content.
     * @param path - Content path where content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return List of categories attached or null if there is not any
     * @throws WCMContentException if path doesn's exist
     * @throws WCMContentIOException if any IO related problem with repository
     * @throws WCMContentSecurityException if user has not been granted to read categories
     */
    List<WCMCategory> getContentCategories(String path) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Attaches a Category to a Content.
     *
     * @param path - Content path.
     * @param categoryPath - Category path.
     * @return Content updated.
     * @throws WCMContentException if content or category don't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    void putContentCategory(String path, String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     * Dettaches a Category from a Content
     * @param path
     * @param categoryPath
     * @throws WCMContentException
     * @throws WCMContentIOException
     * @throws WCMContentSecurityException
     */
    void removeContentCategory(String path, String categoryPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Deletes a Category from repository.
     *
     * @param categoryPath - Category path.
     * @return parent Category.
     * @throws WCMContentException if category has been asigned to Content.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    void deleteCategory(String categoryPath) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Deletes a Category from repository for a specific locale.
     *
     * @param categoryPath - Category path.
     * @param locale - Category locale.
     * @return parent Category.
     * @throws WCMContentException if category has been asigned to Content.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    void deleteCategory(String categoryPath, String locale) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Get Content filtered by categories. <br>
     *
     * @param categories - List of Categories to filter Content.
     * @param filterPath - Filter for path. Method will search under specified path. "/" for whole repository. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return List of Content.
     * @throws WCMContentException if filterPath it doesn's exist
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create categories.
     */
    List<WCMObject> getContent(List<WCMCategory> categories, String filterPath) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Creates a comment under the specified Content path. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param comment - Comment to add
     * @return Content with comment updated.
     * @throws WCMContentException if content doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create comments.
     */
    WCMObject createContentComment(String path, String comment) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Removes a comment under the specified Content path.
     * <p>
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param comment - Comment to add
     * @return Content with comment updated.
     * @throws WCMContentException if content doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create comments.
     */
    WCMObject deleteContentComment(String path, String idComment) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Creates a property in the form KEY/VALUE to a Content. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @param value - Value
     * @return Content with properties updated.
     * @throws WCMContentException if content doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create properties.
     */
    WCMObject createContentProperty(String path, String name, String value) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Modifies a property in the form KEY/VALUE to a Content. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @param value - Value
     * @return Content with properties updated.
     * @throws WCMContentException if content doesn't exist or property doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create properties.
     */
    WCMObject updateContentProperty(String path, String name, String value) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Deletes a property in the form {KEY}/VALUE to a Content. <br>
     * This property is deleted from default locale. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param name - Name of property
     * @return Content with properties updated.
     * @throws WCMContentException if content doesn't exist or property doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create properties.
     */
    WCMObject deleteContentProperty(String path, String name) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Adds an authorization to a specific content. <br>
     *
     * in org.gatein.wcm a Principal can represent: <br>
     * - A USER. <br>
     * - A ROLE. <br>
     * <br>
     * An WcmAce represents a link between a Principal and their authorization rights. <br>
     * We define the following permissions: <br>
     * - NONE: No access to the content. <br>
     * - READ: Access to the content for read or add comments. <br>
     * - WRITE: Access to the content to modify the content. <br>
     * - ALL: Principal can access all content and categories. <br>
     * <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param principalId - ID of Principal.
     * @param principal - Principal of ACE.
     * @param permission - Type of permission for this principal.
     * @return Content with ACL updated.
     * @throws WCMContentException if content doesn't exist or property doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to add ACLs.
     * @see {@link WCMPrincipal }
     */
    WCMObject createContentAce(String path, String principalId, WCMPrincipalType principal, WCMPermissionType permission)
            throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Remove an authorization to a specific content. <br>
     *
     * in org.gatein.wcm a Principal can represent: <br>
     * - A USER. <br>
     * - A ROLE. <br>
     * <br>
     * An WcmAce represents a link between a Principal and their authorization rights. <br>
     * We define the following permissions: <br>
     * - NONE: No access to the content. <br>
     * - READ: Access to the content for read or add comments. <br>
     * - WRITE: Access to the content to modify the content. <br>
     * - ALL: Principal can access all content and categories. <br>
     * <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param principalId - Principal of ACE.
     * @return Content with ACL updated.
     * @throws WCMContentException if content doesn't exist or property doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to add ACLs.
     * @see {@link WCMPrincipal }
     */
    WCMObject deleteContentAce(String path, String principalId) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Retrieves a list of versions available for a specified content. <br>
     * Only WCMTextDocument and WCMBinaryDocument objects are versionable.
     * <p>
     * Versions are created automatically when a WCMTextDocument or WCMBinaryDocument is updated.
     * <p>
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @return List of versions or null if path is not versionable or there is not versions
     * @throws WCMContentException if content doesn't exist in the path.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to access content under specified path.
     */
    List<String> getVersions(String path) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Restores a Content version in the specified path. <br>
     *
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param version - Version of the Content to restore.
     * @throws WCMContentException if content or version doesn't exist.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to write in the content.
     */
    void restore(String path, String version) throws WCMContentException, WCMContentIOException, WCMContentSecurityException;

    /**
     *
     * Removes content from a specified path and version.
     * <p>
     * @param path - Path where the content is stored. <br>
     *        String with format: / &lt;id&gt; / &lt;id&gt; / &lt;id&gt; <br>
     *        where "/" is the root of repository and &lt;id&gt; folders ID
     * @param version - Content version to remove.
     * @return Path of the content
     * @throws WCMContentException if content or version doesn't exist
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to modify content under specified path.
     */
    String deleteContentVersion(String path, String version) throws WCMContentException, WCMContentIOException,
            WCMContentSecurityException;

    /**
     *
     * Close WCM session and flush info. <br>
     *
     * @throws WCMContentIOException if any IO related problem with repository.
     */
    void closeSession() throws WCMContentIOException;
}