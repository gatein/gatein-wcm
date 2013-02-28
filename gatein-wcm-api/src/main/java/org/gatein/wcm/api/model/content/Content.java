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
package org.gatein.wcm.api.model.content;

import java.util.Date;
import java.util.List;

import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.metadata.Comment;
import org.gatein.wcm.api.model.metadata.Property;
import org.gatein.wcm.api.model.publishing.PublishStatus;
import org.gatein.wcm.api.model.security.ACL;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;

/**
 *
 * Base class for content representation. <br />
 * It stores metadata: id, authoring, status, versioning, collaboration, categories and properties.<br />
 * All content classes extends this class. <br />
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface Content {

    /**
     *
     * @return This method returns the version of the content.
     */
    Integer getVersion();

    /**
     *
     * @return This method returns ID of the document. <br>
     *         ID will be unique in a folder.
     */
    String getId();

    /**
     *
     * @return This method returns locale of the document. <br>
     */
    String getLocale();

    /**
     *
     * @return This method returns path to the document. <br>
     *         This method doesn't include the ID of the document. <br>
     *         Example: <br>
     *         Path: "/folder1/folder2/folder3/mydocument" <br>
     *         <b>getLocation()</b> will return "/folder1/folder2/folder3".
     */
    String getLocation();

    /**
     *
     * @return This method returns the current Access Control List for this content. <br>
     *         An ACL defines a groups of permissions for different users and/or roles. <br>
     */
    ACL getAcl();

    /**
     *
     * @return This method returns when the content where created.
     */
    Date getCreated();

    /**
     *
     * @return This method returns when content was last modified.
     */
    Date getLastModified();

    /**
     *
     * @return This method returns the publishing status of content.
     */
    PublishStatus getPublishStatus();

    /**
     *
     * @return This method will return the publishing roles attached to this content. <br>
     *         If null the publishing roles active will be delegated to the parent publishing roles.
     */
    List<Principal> getPublishingRoles();

    /**
     *
     * @return This method returns User who created the content.
     */
    User getCreatedBy();

    /**
     *
     * @return This method returns last user who modified the content.
     */
    User getLastModifiedBy();

    /**
     *
     * @return This method returns comments attached to a content.
     */
    List<Comment> getComments();

    /**
     *
     * @return This method will returns categories attached to a content.
     */
    List<Category> getCategories();

    /**
     *
     * @return This method will return properties attached to a content.
     */
    List<Property> getProperties();

    /**
     *
     * @return This method returns if content is locked for writting.
     */
    boolean isLocked();

    /**
     *
     * @return This method returns User owner of Lock
     */
    User getLockOwner();

}
