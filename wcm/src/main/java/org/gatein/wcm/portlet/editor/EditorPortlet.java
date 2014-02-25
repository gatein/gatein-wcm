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

package org.gatein.wcm.portlet.editor;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.domain.UserWcm;
import org.gatein.wcm.portlet.editor.views.CategoriesActions;
import org.gatein.wcm.portlet.editor.views.ManagerActions;
import org.gatein.wcm.portlet.editor.views.PostsActions;
import org.gatein.wcm.portlet.editor.views.TemplatesActions;
import org.gatein.wcm.portlet.editor.views.UploadsActions;
import org.gatein.wcm.services.PortalService;

import javax.inject.Inject;
import javax.portlet.*;
import java.io.*;
import java.util.logging.Logger;

/**
 * GateIn WCM Editor Portlet.
 * Implements a specific MVC pattern for portlets.
 *
 * @see Wcm.VIEWS
 * @see Wcm.ACTIONS
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class EditorPortlet extends GenericPortlet {
    private static final Logger log = Logger.getLogger(EditorPortlet.class.getName());

    @Inject
    private PortalService portal;

    @Inject
    private UploadsActions uploads;

    @Inject
    private CategoriesActions categories;

    @Inject
    private TemplatesActions templates;

    @Inject
    private PostsActions posts;

    @Inject
    private ManagerActions manager;

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        final String view = request.getParameter("view");
        String url = null;

        UserWcm userWcm = null;
        // Check user
        if (request.getUserPrincipal() == null || request.getUserPrincipal().getName() == null) {
            url = "/jsp/notaccess.jsp";
            request.setAttribute("userWcm", "anonymous");
        } else {
            try {
                String user = request.getUserPrincipal().getName();
                userWcm = portal.getPortalUser(user);
                if (!userWcm.getGroups().contains(Wcm.GROUPS.WCM)) {
                    url = "/jsp/notaccess.jsp";
                }
                request.setAttribute("userWcm", userWcm);
            } catch (WcmException e) {
                log.warning("Cannot access Portal User interface.");
                e.printStackTrace();
            }
        }

        // UserWcm is validated, checking controller variable on MVC pattern
        if (url == null) {
            if (view == null) {
                // First access shows default post list
                if (request.getPortletSession().getAttribute("list") == null) {
                    posts.viewPosts(request, response, userWcm);
                }
                url = "/jsp/posts/posts.jsp";
            } else {
                // Redirect to view
                if (view.equals(Wcm.VIEWS.POSTS))  {
                    // Query list of posts
                    posts.viewPosts(request, response, userWcm);
                    url = "/jsp/posts/posts.jsp";
                } else if (view.equals(Wcm.VIEWS.NEW_POST)) {
                    // Query categories for post editor
                    posts.viewNewPost(request, response, userWcm);
                    url = "/jsp/posts/post.jsp";
                } else if (view.equals(Wcm.VIEWS.CATEGORIES))  {
                    // Query list of categories
                    categories.viewCategories(request, response, userWcm);
                    url = "/jsp/categories/categories.jsp";
                } else if (view.equals(Wcm.VIEWS.NEW_CATEGORY)) {
                    // New category
                    categories.viewNewCategory(request, response, userWcm);
                    url = "/jsp/categories/category.jsp";
                } else if (view.equals(Wcm.VIEWS.UPLOADS))  {
                    // Query list of uploads
                    uploads.viewUploads(request, response, userWcm);
                    url = "/jsp/uploads/uploads.jsp";
                } else if (view.equals(Wcm.VIEWS.NEW_UPLOAD)) {
                    url = "/jsp/uploads/upload.jsp";
                } else if (view.equals(Wcm.VIEWS.TEMPLATES))  {
                    // Query list of templates
                    templates.viewTemplates(request, response, userWcm);
                    url = "/jsp/templates/templates.jsp";
                } else if (view.equals(Wcm.VIEWS.NEW_TEMPLATE)) {
                    // Query list of categories
                    templates.viewNewTemplate(request, response, userWcm);
                    url = "/jsp/templates/template.jsp";
                } else if (view.equals(Wcm.VIEWS.EDIT_CATEGORY)) {
                    // Query edit category
                    categories.viewEditCategory(request, response, userWcm);
                    url = "/jsp/categories/categoryEdit.jsp";
                } else if (view.equals(Wcm.VIEWS.EDIT_UPLOAD)) {
                    // Query edit upload
                    uploads.viewEditUpload(request, response, userWcm);
                    url = "/jsp/uploads/uploadEdit.jsp";
                } else if (view.equals(Wcm.VIEWS.EDIT_TEMPLATE)) {
                    // Query edit template
                    templates.viewEditTemplate(request, response, userWcm);
                    url = "/jsp/templates/templateEdit.jsp";
                } else if (view.equals(Wcm.VIEWS.EDIT_POST)) {
                    // Query edit post
                    posts.viewEditPost(request, response, userWcm);
                    url = "/jsp/posts/postEdit.jsp";
                } else if (view.equals(Wcm.VIEWS.MANAGER)) {
                    url = "/jsp/manager/manager.jsp";
                } else {
                   // View parameter wrong. Default view.
                    url = "/jsp/posts/posts.jsp";
                }
            }
        }

        PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(url);
        prd.include(request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        // Get user
        UserWcm userWcm = null;
        try {
            String user = (request.getUserPrincipal() != null?request.getUserPrincipal().getName():null);
            userWcm = portal.getPortalUser(user);
        } catch (WcmException e) {
            log.warning("Cannot access Portal User interface.");
            e.printStackTrace();
        }

        // Get parameters
        String action = request.getParameter("action");
        String view = request.getParameter("view");

        // Views
        // Transfer parameters from action to render phase
        if (view!=null) {
            if (view.equals(Wcm.VIEWS.EDIT_CATEGORY) ||
                view.equals(Wcm.VIEWS.EDIT_UPLOAD) ||
                view.equals(Wcm.VIEWS.EDIT_TEMPLATE) ||
                view.equals(Wcm.VIEWS.EDIT_POST)) {

                // Locks
                if (view.equals(Wcm.VIEWS.EDIT_POST) && action.equals("lock")) {
                    view = posts.actionLockPost(request, response, userWcm);
                }
                if (view.equals(Wcm.VIEWS.EDIT_CATEGORY) && action.equals("lock")) {
                    view = categories.actionLockCategory(request, response, userWcm);
                }
                if (view.equals(Wcm.VIEWS.EDIT_UPLOAD) && action.equals("lock")) {
                    view = uploads.actionLockUpload(request, response, userWcm);
                }
                if (view.equals(Wcm.VIEWS.EDIT_TEMPLATE) && action.equals("lock")) {
                    view = templates.actionLockTemplate(request, response, userWcm);
                }

                response.setRenderParameter("editid", request.getParameter("editid"));
            }
            if (request.getParameter("errorWcm") != null)
                response.setRenderParameter("errorWcm", request.getParameter("errorWcm"));
        }

        // Actions
        if (action != null) {
            if (action.equals(Wcm.ACTIONS.NEW_CATEGORY)) {
                // New category action
                view = categories.actionNewCategory(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.DELETE_CATEGORY)) {
                // Delete category action
                view = categories.actionDeleteCategory(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.EDIT_CATEGORY)) {
                // Edit category action
                view = categories.actionEditCategory(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.NEW_UPLOAD)) {
                // New upload action
                view = uploads.actionNewUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.RIGHT_UPLOADS)) {
                // Right page actions uploads
                view = uploads.actionRightUploads(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.LEFT_UPLOADS)) {
                // Left page actions uploads
                view = uploads.actionLeftUploads(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.EDIT_UPLOAD)) {
                // Edit upload action
                view = uploads.actionEditUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.ADD_CATEGORY_UPLOAD)) {
                // Add category upload action
                view = uploads.actionAddCategoryUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.FILTER_CATEGORY_UPLOADS)) {
                // Filter category uploads action
                view = uploads.actionFilterCategoryUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.FILTER_NAME_UPLOADS)) {
                // Filter name uploads action
                view = uploads.actionFilterNameUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.DELETE_UPLOAD)) {
                // Delete upload action
                view = uploads.actionDeleteUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.DELETE_SELECTED_UPLOAD)) {
                // Delete selected upload action
                view = uploads.actionDeleteSelectedUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.ADD_SELECTED_CATEGORY_UPLOAD))  {
                // Add selected category upload action
                view = uploads.actionAddSelectedCategoryUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.REMOVE_CATEGORY_UPLOAD))  {
                // Remove category upload action
                view = uploads.actionRemoveCategoryUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.CHANGE_VERSION_UPLOAD)) {
                // Change version upload action
                view = uploads.actionChangeVersionUpload(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.NEW_TEMPLATE)) {
                // New template action
                view = templates.actionNewTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.RIGHT_TEMPLATES)) {
                // Right page actions templates
                view = templates.actionRightTemplates(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.LEFT_TEMPLATES)) {
                // Left page actions templates
                view = templates.actionLeftTemplates(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.EDIT_TEMPLATE)) {
                // Edit template action
                view = templates.actionEditTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.CHANGE_VERSION_TEMPLATE)) {
                // Change version template action
                view = templates.actionChangeVersionTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.ADD_CATEGORY_TEMPLATE)) {
                // Add category template action
                view = templates.actionAddCategoryTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.FILTER_CATEGORY_TEMPLATES)) {
                // Filter category templates action
                view = templates.actionFilterCategoryTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.FILTER_NAME_TEMPLATES)) {
                // Filter name templates action
                view = templates.actionFilterNameTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.DELETE_TEMPLATE)) {
                // Delete template action
                view = templates.actionDeleteTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.DELETE_SELECTED_TEMPLATE)) {
                // Delete selected
                view = templates.actionDeleteSelectedTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.ADD_SELECTED_CATEGORY_TEMPLATE)) {
                // Add selected category template action
                view = templates.actionAddSelectedCategoryTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.REMOVE_CATEGORY_TEMPLATE)) {
                // Remove category template action
                view = templates.actionRemoveCategoryTemplate(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.NEW_POST)) {
                // New post action
                view = posts.actionNewPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.RIGHT_POSTS)) {
                // Right page actions posts
                view = posts.actionRightPosts(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.LEFT_POSTS)) {
                // Left page actions posts
                view = posts.actionLeftPosts(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.EDIT_POST)) {
                // Edit post action
                view = posts.actionEditPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.CHANGE_VERSION_POST)) {
                // Change version post action
                view = posts.actionChangeVersionPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.ADD_CATEGORY_POST)) {
                // Add category post action
                view = posts.actionAddCategoryPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.FILTER_CATEGORY_POSTS)) {
                // Filter category posts action
                view = posts.actionFilterCategoryPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.FILTER_NAME_POSTS)) {
                // Filter name posts action
                view = posts.actionFilterNamePost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.DELETE_POST)) {
                // Delete post action
                view = posts.actionDeletePost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.DELETE_SELECTED_POST)) {
                // Delete selected post action
                view = posts.actionDeleteSelectedPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.ADD_SELECTED_CATEGORY_POST))  {
                // Add selected category post action
                view = posts.actionAddSelectedCategoryPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.REMOVE_CATEGORY_POST))  {
                // Remove category post action
                view = posts.actionRemoveCategoryPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.PUBLISH_POST))  {
                // Publish post action
                view = posts.actionPublishPost(request, response, userWcm);
            } else if (action.equals(Wcm.ACTIONS.PUBLISH_POSTS))  {
                // Publish posts action
                view = posts.actionPublishPosts(request, response, userWcm);
            } else {
                // View parameter doesn't modified by actions
            }
        }
        response.setRenderParameter("view", view);
    }

    /*
        It will be used to handle ajax calls
     */
    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

        // Get user
        UserWcm userWcm = null;
        try {
            String user = (request.getUserPrincipal() != null?request.getUserPrincipal().getName():null);
            userWcm = portal.getPortalUser(user);
            request.setAttribute("userWcm", userWcm);
        } catch (WcmException e) {
            log.warning("Cannot access Portal User interface.");
            e.printStackTrace();
        }

        String event = request.getParameter("event");
        if (event != null) {
            String url = null;
            if (event.equals(Wcm.EVENTS.SHOW_CATEGORIES_CHILDREN)) {
                // Show children categories
                url = categories.eventShowCategoriesChildren(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.DOWNLOAD_UPLOAD)) {
                // Show download attached of Upload object
                uploads.eventDownloadUpload(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_POST_UPLOADS)) {
                // Show list uploads
                url = posts.eventShowPostUploads(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_POST_ACLS)) {
                // Show list acls
                url = posts.eventShowPostAcls(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.ADD_ACL_POST)) {
                // Add acl post action
                url = posts.eventAddAclPost(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.REMOVE_ACL_POST)) {
                // Remove acl post action
                url = posts.eventRemoveAclPost(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_CATEGORY_ACLS)) {
                // Show list acls
                url = categories.eventShowCategoryAcls(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.ADD_ACL_CATEGORY)) {
                // Add acl category action
                url = categories.eventAddAclCategory(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.REMOVE_ACL_CATEGORY)) {
                // Remove acl category action
                url = categories.eventRemoveAclCategory(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_UPLOAD_ACLS)) {
                // Show list acls
                url = uploads.eventShowUploadAcls(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.ADD_ACL_UPLOAD)) {
                // Add acl upload action
                url = uploads.eventAddAclUpload(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.REMOVE_ACL_UPLOAD)) {
                // Remove acl upload action
                url = uploads.eventRemoveAclUpload(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_POST_COMMENTS)) {
                // Show comments post
                url = posts.eventShowPostComments(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.ADD_COMMENT_POST)) {
                // Add Comment Post action
                url = posts.eventAddCommentPost(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.UPDATE_COMMENTS_POST)) {
                // Update Comments state in Post
                url = posts.eventUpdateCommentsPost(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.UPDATE_STATUS_COMMENT_POST)) {
                // Update Comment's status inside Post
                url = posts.eventUpdateCommentStatusPost(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_POST_RELATIONSHIPS)) {
                // Show relationships post
                url = posts.eventShowPostRelationships(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.ADD_RELATIONSHIP_POST)) {
                // Add relationship post
                url = posts.eventAddPostRelationship(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.REMOVE_RELATIONSHIP_POST)) {
                // Add relationship post
                url = posts.eventRemovePostRelationship(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_TEMPLATE_RELATIONSHIPS)) {
                // Show relationships template
                url = templates.eventShowTemplateRelationships(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.ADD_RELATIONSHIP_TEMPLATE)) {
                // Add relationship template
                url = templates.eventAddTemplateRelationship(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.REMOVE_RELATIONSHIP_TEMPLATE)) {
                // Add relationship template
                url = templates.eventRemoveTemplateRelationship(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.UNLOCK_POST)) {
                // Unlock post when user leaves page
                posts.eventUnlockPost(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.UNLOCK_CATEGORY)) {
                // Unlock category when user leaves page
                categories.eventUnlockCategory(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.UNLOCK_UPLOAD)) {
                // Unlock upload when user leaves page
                uploads.eventUnlockUpload(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.UNLOCK_TEMPLATE)) {
                // Unlock template when user leaves page
                templates.eventUnlockTemplate(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.SHOW_LOCKS)) {
                // Show list of locks
                url = manager.eventShowLocks(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.REMOVE_LOCK)) {
                // Remove lock
                url = manager.eventRemoveLock(request, response, userWcm);
            } else {
                // No default view.
            }
            if (url != null) {
                PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(url);
                prd.include(request, response);
            }
        }
    }
}
