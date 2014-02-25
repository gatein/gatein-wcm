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

package org.gatein.wcm;

/**
 * Global constants for org.gatein.wcm project.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public interface Wcm {

    /**
     * Default configuration file
     */
    static final String CONFIGURATION_FILE = "wcm.properties";

    /**
     * Defines relationship between GateIn user's groups and LW WCM subsystem.
     * - There is a main wcm group, for example: /wcm, all users under this group has access to LW WCM subsystem.
     * - Users with MANAGER membership in /wcm has administrator role in LW WCM subsystem.
     * - Administrator role allows to manage template under LW WCM subsystem.
     * - Under /wcm group we can define other groups, like /wcm/theatre, /wcm/sporting, /wcm/concert.
     * - Groups under /wcm allos to define authorization about how can write content into LW WCM subsystem
     */
	interface GROUPS {
        /**
         * This group define which users can access to the LW WCM editor application.
         * All users that belongs to this group have grant to access LW WCM Editor.
         * Users with membership MANAGER (@see Wcm.MANAGER) also have administrator role in LW WCM editor.
         */
        static final String WCM = (System.getProperty("wcm.groups.wcm") == null ? "/wcm" : System.getProperty("wcm.groups.wcm"));
        /**
         * Wildcard to refer all groups under Wcm.WCM.
         * Used for ACLs to define a grant for all groups under Wcm.WCM
         */
		static final String ALL = "*";
        /**
         * Users with membership MANAGER in @see Wcm.WCM group have administrator role in LW WCM editor.
         */
        static final String MANAGER = (System.getProperty("wcm.groups.manager") == null ? "manager" : System.getProperty("wcm.groups.manager"));
        /**
         * Users
         */
        static final String LOST = (System.getProperty("wcm.groups.lost") == null ? "/wcm/lost" : System.getProperty("wcm.groups.lost"));
	}

    /**
     * Defines ACL values.
     * By default all Posts, Category and Uploads object can be readed by any users.
     */
	interface ACL {
        /**
         * Acl with WRITE type grants write to group defined to resource (Post, Category or Upload) attached.
         */
		static final Character WRITE = 'W';
        /**
         * Acl with NONE type revokes read access to group defined to resource (Post, Category or Upload) attached.
         */
		static final Character NONE = 'N';
	}

    /**
     * Defines Category's types.
     */
	interface CATEGORIES {
        /**
         * Normal Category.
         * It's defined for semantic porpuse, an object should have only 1 Category but it can have N Tags.
         */
		static final Character CATEGORY = '2';
        /**
         * Tag Category.
         * It's defined for semantic porpuse, an object should have only 1 Category but it can have N Tags.
         */
		static final Character TAG = '3';
        /**
         * Folder Category.
         * A Folder Category can group other categories.
         */
		static final Character FOLDER = '1';
	}

    /**
     * Defines Post's publishing status.
     * There is not complex workflow in GateIn WCM.
     * If a user has WRITE access, he can modify publishing status of post.
     */
	interface POSTS {
        /**
         * Post is public and it can be accessible from Content Portlets.
         */
		static final Character PUBLISHED = 'P';
        /**
         * Post is in draft mode and it can not be accessible from Content Portlets.
         */
		static final Character DRAFT = 'D';
	}

    /**
     * Defines Post's comments publication status.
     */
	interface COMMENTS {
        /**
         * Post can accept comments from non-logged users.
         */
		static final Character ANONYMOUS = 'A';
        /**
         * Post accepts only comments from GateIn users.
         */
		static final Character LOGGED = 'L';
        /**
         * This Post doesn't accept comments.
         */
		static final Character NO_COMMENTS = 'N';
	}

    /**
     * Defines Comment's type
     */
	interface COMMENT {
        /**
         * Comment is readable for all users that can read the Comment's Post.
         */
		static final Character PUBLIC = 'P';
        /**
         * Comment is not visible, only user with WRITE access can read it.
         * It's used to preview Comments or to unpublish comments.
         */
		static final Character REJECTED = 'R';
        /**
         * Comment is deleted.
         */
		static final Character DELETED = 'D';
	}

    /**
     * Defines UPLOADS common properties.
     */
	interface UPLOADS {
        /**
         * Defines system property name where to read the Upload's folder
         */
		static final String FOLDER = "wcm.uploads.folder";

        /**
         * If Wcm.UPLOADS.FOLDER is not defined is used a default location ${jboss.servere.data.dir}
         */
		static final String DEFAULT = "jboss.server.data.dir";

        /**
         * Defines system property name where to read a temporal directory used for uploads.
         * This temporal directory is used for org.apache.commons.fileupload.* package.
         */
        static final String TMP_DIR = "java.io.tmpdir";

        /**
         * Defines the buffer's length for uploads operations.
         */
		static final int LENGTH_BUFFER = (System.getProperty("wcm.uploads.length_buffer") == null ? 5242880 : new Integer(System.getProperty("wcm.uploads.length_buffer")));

        /**
         * Defines the max file size for Uploads.
         */
        static final int MAX_FILE_SIZE = (System.getProperty("wcm.uploads.max_file_size") == null ? 5242880 : new Integer(System.getProperty("wcm.uploads.max_file_size"))); // 5 Mb as default max size for file upload

        /**
         * Defines properties for Client's browser cache.
         * HTTP/1.1 Cache-Control parameters
         */
        interface CACHE {
            /**
             * @ref http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3
             */
            static final int MAX_AGE = (System.getProperty("wcm.uploads.cache.max-age") == null ? 300 : new Integer(System.getProperty("wcm.uploads.cache.max-age")));
        }
	}

    /**
     * Defines constants used in MVC portlet pattern for EditorPortlet.
     * They indicates target VIEW after an ACTION.
     */
    interface VIEWS {
        static final int MAX_PER_PAGE = (System.getProperty("wcm.views.max_per_page") == null ? 3 : new Integer(System.getProperty("wcm.views.max_per_page")));
        static final String POSTS = "posts";
        static final String CATEGORIES = "categories";
        static final String UPLOADS = "uploads";
        static final String TEMPLATES = "templates";
        static final String NEW_POST = "newpost";
        static final String NEW_CATEGORY = "newcategory";
        static final String NEW_UPLOAD = "newupload";
        static final String NEW_TEMPLATE = "newtemplate";
        static final String EDIT_CATEGORY = "editcategory";
        static final String EDIT_UPLOAD = "editupload";
        static final String EDIT_TEMPLATE = "edittemplate";
        static final String EDIT_POST = "editpost";
        static final String MANAGER = "manager";
    }

    /**
     * Defines constants used in MVC portlet pattern for EditorPortlet.
     * They indicates ACTIONS to perform in Portlet action phase.
     */
    interface ACTIONS {
        static final String POSTS = "posts";
        static final String NEW_CATEGORY = "newcategory";
        static final String DELETE_CATEGORY = "deletecategory";
        static final String EDIT_CATEGORY = "editcategory";
        static final String NEW_UPLOAD = "newupload";
        static final String RIGHT_UPLOADS = "rightuploads";
        static final String LEFT_UPLOADS = "leftuploads";
        static final String EDIT_UPLOAD = "editupload";
        static final String ADD_CATEGORY_UPLOAD = "addcategoryupload";
        static final String FILTER_CATEGORY_UPLOADS = "filtercategoryuploads";
        static final String FILTER_NAME_UPLOADS = "filternameuploads";
        static final String DELETE_UPLOAD = "deleteupload";
        static final String DELETE_SELECTED_UPLOAD = "deleteselectedupload";
        static final String ADD_SELECTED_CATEGORY_UPLOAD = "addselectedcategoryupload";
        static final String REMOVE_CATEGORY_UPLOAD = "removecategoryupload";
        static final String NEW_TEMPLATE = "newtemplate";
        static final String DELETE_SELECTED_TEMPLATE = "deleteselectedtemplate";
        static final String ADD_SELECTED_CATEGORY_TEMPLATE = "addselectedcategorytemplate";
        static final String FILTER_CATEGORY_TEMPLATES = "filtercategorytemplates";
        static final String FILTER_NAME_TEMPLATES = "filternametemplates";
        static final String RIGHT_TEMPLATES = "righttemplates";
        static final String LEFT_TEMPLATES = "lefttemplates";
        static final String ADD_CATEGORY_TEMPLATE = "addcategorytemplate";
        static final String REMOVE_CATEGORY_TEMPLATE = "removecategorytemplate";
        static final String EDIT_TEMPLATE = "edittemplate";
        static final String DELETE_TEMPLATE = "deletetemplate";
        static final String NEW_POST = "newpost";
        static final String RIGHT_POSTS = "rightposts";
        static final String LEFT_POSTS = "leftposts";
        static final String EDIT_POST = "editpost";
        static final String ADD_CATEGORY_POST = "addcategorypost";
        static final String FILTER_CATEGORY_POSTS = "filtercategoryposts";
        static final String FILTER_NAME_POSTS = "filternameposts";
        static final String DELETE_POST = "deletepost";
        static final String DELETE_SELECTED_POST = "deleteselectedpost";
        static final String ADD_SELECTED_CATEGORY_POST = "addselectedcategorypost";
        static final String REMOVE_CATEGORY_POST = "removecategorypost";
        static final String PUBLISH_POST = "publishpost";
        static final String PUBLISH_POSTS = "draftpost";
        static final String CHANGE_VERSION_POST = "changeversionpost";
        static final String CHANGE_VERSION_UPLOAD = "changeversionupload";
        static final String CHANGE_VERSION_TEMPLATE = "changeversiontemplate";
    }

    /**
     * Defines constants used in a MVC portlet pattern.
     * Events are an extension of MVC portlet pattern to support AJAX calls.
     * They indicates which action and render to server in a serveResource() portlet phase.
     */
    interface EVENTS {
        static final String SHOW_CATEGORIES_CHILDREN = "showcategorieschildren";
        static final String DOWNLOAD_UPLOAD = "downloadupload";
        static final String SHOW_POST_UPLOADS = "showpostuploads";
        static final String SHOW_POST_ACLS = "showpostacls";
        static final String ADD_ACL_POST = "addaclpost";
        static final String REMOVE_ACL_POST = "removeaclpost";
        static final String SHOW_CATEGORY_ACLS = "showcategoryacls";
        static final String ADD_ACL_CATEGORY = "addaclcategory";
        static final String REMOVE_ACL_CATEGORY = "removeaclcategory";
        static final String SHOW_UPLOAD_ACLS = "showuploadacls";
        static final String ADD_ACL_UPLOAD = "addaclupload";
        static final String REMOVE_ACL_UPLOAD = "removeaclupload";
        static final String UPDATE_CONTENT_POST = "updatecontentpost";
        static final String SHOW_POST_COMMENTS = "showpostcomments";
        static final String ADD_COMMENT_POST = "addcommentpost";
        static final String UPDATE_COMMENTS_POST = "updatecommentspost";
        static final String UPDATE_STATUS_COMMENT_POST = "updatestatuscommentpost";
        static final String SHOW_POST_RELATIONSHIPS = "showpostrelationships";
        static final String ADD_RELATIONSHIP_POST = "addrelationshippost";
        static final String REMOVE_RELATIONSHIP_POST = "removerelationshippost";
        static final String SHOW_TEMPLATE_RELATIONSHIPS = "showtemplaterelationships";
        static final String ADD_RELATIONSHIP_TEMPLATE = "addrelationshiptemplate";
        static final String REMOVE_RELATIONSHIP_TEMPLATE = "removerelationshiptemplate";
        static final String UNLOCK_POST = "unlockpost";
        static final String UNLOCK_CATEGORY = "unlockcategory";
        static final String UNLOCK_UPLOAD = "unlockupload";
        static final String UNLOCK_TEMPLATE = "unlocktemplate";
        static final String SHOW_LOCKS = "showlocks";
        static final String REMOVE_LOCK = "removelock";
    }

    /**
     * Defines constants in ContentPortlet for configuration use cases.
     */
    interface CONFIG {
        interface ACTIONS {
            static final String SAVE_CONFIGURATION = "changetemplate";
        }

        interface VIEWS {
        }

        interface EVENTS {
            static final String CHANGE_CHOOSE_CONTENT = "changechoosecontent";
            static final String NEW_CONTENT_ATTACHED = "newcontentattached";
            static final String DELETE_CONTENT_ATTACHED = "deletecontentattached";
        }
    }

    /**
     * Defines constants in ContentPortlet for render use cases
     */
    interface CONTENT {
        interface ACTIONS {
            static final String INLINE_EDITOR = "inlineeditor";
        }
    }

    /**
     * Defines constants to define suffix variables for content navigation inside ContentPortlet.
     */
    interface SUFFIX {
        static final String POST = (System.getProperty("wcm.parameters.post") == null ? "post" : System.getProperty("wcm.parameters.post"));
        static final String CATEGORY = (System.getProperty("wcm.parameters.category") == null ? "cat" : System.getProperty("wcm.parameters.category"));
        static final String ID = (System.getProperty("wcm.parameters.id") == null ? "id" : System.getProperty("wcm.parameters.id"));
        static final String NAME = (System.getProperty("wcm.parameters.name") == null ? "name" : System.getProperty("wcm.parameters.name"));
    }

    /**
     * Defines constants for type of Relationship objects
     */
    interface RELATIONSHIP {
        static final Character POST = 'P';
        static final Character TEMPLATE = 'T';
    }

    /**
     * Defines constants for type of Locks
     */
    interface LOCK {
        static final Character POST = 'P';
        static final Character CATEGORY = 'C';
        static final Character UPLOAD = 'U';
        static final Character TEMPLATE = 'T';
    }

    /**
     * Defines configuration for automatic tasks inside WCM
     */
    interface TIMEOUTS {
        /**
         * Defines scheduler in minutes for Timer service
         */
        static final int TIMER = 5;
        /**
         * Defines max minutes a lock is valid
         */
        static final int LOCKS = (System.getProperty("wcm.timeout.locks") == null ? 20 : new Integer(System.getProperty("wcm.timeout.locks")));
    }

    /**
     * Defines constant for debug mode
     */
    static final boolean DEBUG = (System.getProperty("wcm.debug") == null || "false".equals(System.getProperty("wcm.debug"))? false : true);
}