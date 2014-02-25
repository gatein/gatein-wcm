<%
/*
 * JBoss, a division of Red Hat
 * Copyright 2012, Red Hat Middleware, LLC, and individual
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
%>
<%@ page import="org.gatein.wcm.Wcm" %>
<% // View URLs %>
<portlet:actionURL var="postsView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.POSTS %>" />
</portlet:actionURL>
<portlet:actionURL var="categoriesView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.CATEGORIES %>" />
</portlet:actionURL>
<portlet:actionURL var="uploadsView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.UPLOADS %>" />
</portlet:actionURL>
<portlet:actionURL var="templatesView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.TEMPLATES %>" />
</portlet:actionURL>
<portlet:actionURL var="newPostView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.NEW_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="newCategoryView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.NEW_CATEGORY %>" />
</portlet:actionURL>
<portlet:actionURL var="newUploadView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.NEW_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="newTemplateView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.NEW_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="managerView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.MANAGER %>" />
</portlet:actionURL>
<portlet:actionURL var="editCategoryView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.EDIT_CATEGORY %>" />
    <portlet:param name="action" value="lock" />
</portlet:actionURL>
<portlet:actionURL var="editUploadView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.EDIT_UPLOAD %>" />
    <portlet:param name="action" value="lock" />
</portlet:actionURL>
<portlet:actionURL var="editTemplateView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.EDIT_TEMPLATE %>" />
    <portlet:param name="action" value="lock" />
</portlet:actionURL>
<portlet:actionURL var="editPostView">
    <portlet:param name="view" value="<%= Wcm.VIEWS.EDIT_POST %>" />
    <portlet:param name="action" value="lock" />
</portlet:actionURL>
<% // Actions URLs %>
<portlet:actionURL var="postsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.POSTS %>" />
</portlet:actionURL>
<portlet:actionURL var="newPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.NEW_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="editPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.EDIT_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="deletePostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.DELETE_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="publishPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.PUBLISH_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="publishPostsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.PUBLISH_POSTS %>" />
</portlet:actionURL>
<portlet:actionURL var="removeCategoryPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.REMOVE_CATEGORY_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="deleteSelectedPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.DELETE_SELECTED_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="addSelectedCategoryPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.ADD_SELECTED_CATEGORY_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="filterCategoryPostsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.FILTER_CATEGORY_POSTS %>" />
</portlet:actionURL>
<portlet:actionURL var="filterNamePostsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.FILTER_NAME_POSTS %>" />
</portlet:actionURL>
<portlet:actionURL var="rightPostsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.RIGHT_POSTS %>" />
</portlet:actionURL>
<portlet:actionURL var="leftPostsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.LEFT_POSTS %>" />
</portlet:actionURL>
<portlet:actionURL var="addCategoryPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.ADD_CATEGORY_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="changeVersionPostAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.CHANGE_VERSION_POST %>" />
</portlet:actionURL>
<portlet:actionURL var="newCategoryAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.NEW_CATEGORY %>" />
</portlet:actionURL>
<portlet:actionURL var="deleteCategoryAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.DELETE_CATEGORY %>" />
</portlet:actionURL>
<portlet:actionURL var="editCategoryAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.EDIT_CATEGORY %>" />
</portlet:actionURL>
<portlet:actionURL var="newUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.NEW_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="rightUploadsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.RIGHT_UPLOADS %>" />
</portlet:actionURL>
<portlet:actionURL var="leftUploadsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.LEFT_UPLOADS %>" />
</portlet:actionURL>
<portlet:actionURL var="editUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.EDIT_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="addCategoryUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.ADD_CATEGORY_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="filterCategoryUploadsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.FILTER_CATEGORY_UPLOADS %>" />
</portlet:actionURL>
<portlet:actionURL var="filterNameUploadsAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.FILTER_NAME_UPLOADS %>" />
</portlet:actionURL>
<portlet:actionURL var="deleteUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.DELETE_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="deleteSelectedUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.DELETE_SELECTED_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="addSelectedCategoryUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.ADD_SELECTED_CATEGORY_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="removeCategoryUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.REMOVE_CATEGORY_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="changeVersionUploadAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.CHANGE_VERSION_UPLOAD %>" />
</portlet:actionURL>
<portlet:actionURL var="newTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.NEW_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="deleteSelectedTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.DELETE_SELECTED_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="addSelectedCategoryTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.ADD_SELECTED_CATEGORY_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="filterCategoryTemplatesAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.FILTER_CATEGORY_TEMPLATES %>" />
</portlet:actionURL>
<portlet:actionURL var="filterNameTemplatesAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.FILTER_NAME_TEMPLATES %>" />
</portlet:actionURL>
<portlet:actionURL var="rightTemplatesAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.RIGHT_TEMPLATES %>" />
</portlet:actionURL>
<portlet:actionURL var="leftTemplatesAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.LEFT_TEMPLATES %>" />
</portlet:actionURL>
<portlet:actionURL var="addCategoryTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.ADD_CATEGORY_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="removeCategoryTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.REMOVE_CATEGORY_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="editTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.EDIT_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="deleteTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.DELETE_TEMPLATE %>" />
</portlet:actionURL>
<portlet:actionURL var="changeVersionTemplateAction">
    <portlet:param name="action" value="<%= Wcm.ACTIONS.CHANGE_VERSION_TEMPLATE %>" />
</portlet:actionURL>
<% // Event URL %>
<portlet:resourceURL var="showCategoriesChildrenEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_CATEGORIES_CHILDREN %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showPostUploadsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_POST_UPLOADS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showPostAclsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_POST_ACLS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="addAclPostEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.ADD_ACL_POST %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showCategoryAclsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_CATEGORY_ACLS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="addAclCategoryEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.ADD_ACL_CATEGORY %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showUploadAclsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_UPLOAD_ACLS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="addAclUploadEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.ADD_ACL_UPLOAD %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showPostCommentsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_POST_COMMENTS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="addCommentPostEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.ADD_COMMENT_POST %>" />
</portlet:resourceURL>
<portlet:resourceURL var="changeCommentsPostEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UPDATE_COMMENTS_POST %>" />
</portlet:resourceURL>
<portlet:resourceURL var="changeStatusCommentPostEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UPDATE_STATUS_COMMENT_POST %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showPostRelationshipsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_POST_RELATIONSHIPS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showTemplateRelationshipsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_TEMPLATE_RELATIONSHIPS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="unlockPostEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UNLOCK_POST %>" />
</portlet:resourceURL>
<portlet:resourceURL var="unlockCategoryEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UNLOCK_CATEGORY %>" />
</portlet:resourceURL>
<portlet:resourceURL var="unlockUploadEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UNLOCK_UPLOAD %>" />
</portlet:resourceURL>
<portlet:resourceURL var="unlockTemplateEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UNLOCK_TEMPLATE %>" />
</portlet:resourceURL>
<portlet:resourceURL var="showLocksEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_LOCKS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="removeLockEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.REMOVE_LOCK %>" />
</portlet:resourceURL>












