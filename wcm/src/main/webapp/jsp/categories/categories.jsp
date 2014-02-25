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
<%@ page import="org.gatein.wcm.domain.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="static org.gatein.wcm.Wcm.*" %>
<%@ page import="java.util.Set" %>
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>

<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/categories/categories.js") %>"></script>
<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>

    <form id="${n}deleteCategoryForm" method="post" action="${deleteCategoryAction}">
        <input type="hidden" id="${n}deleteCategoryId" name="deletedCategoryId" />
    </form>

    <ul class="wcm-categories">
        <%
            List<Category> list = (List<Category>)renderRequest.getAttribute("list");
            if (list != null) {
                for (Category c : list) {
                    boolean canWrite = userWcm.canWrite(c);
                    String typeIcon = "bookmark";
                    String type = "Category";
                    String color = "green";
                    if (c.getType() == CATEGORIES.FOLDER) {
                        typeIcon = "folder-open";
                        type = "Folder";
                        color = "blue";
                    }
                    if (c.getType() == CATEGORIES.TAG) {
                        typeIcon = "tag";
                        type = "Tag";
                        color = "red";
                    }
         %>
        <li>
            <div id="${n}categoryId<%= c.getId()%>">
                <div class="wcm-category-title"><span class="glyphicon glyphicon-<%= typeIcon %> margin-right wcm-<%= color%> wcm-category-icon"></span> <%= c.getName() %>&nbsp;<span class="wcm-category-type">(<%= type %>)</span></div>
                <div class="wcm-category-actions"><% if (canWrite) { %><a href="<%= editCategoryView %>&editid=<%= c.getId() %>">Edit</a> | <a href="javascript:deleteCategory('${n}', <%= c.getId() %>)">Delete</a> | <a href="javascript:;" onclick="javascript:showSingleAclCategory('${n}', this.id, '${showCategoryAclsEvent}', '<%= c.getId() %>', '${categoriesView}');" id="${n}addAcl<%= c.getId() %>">Security</a> | <% } %>
                    <%
                        if (c.getNumChildren() > 0) {
                    %>
                     <a href="javascript:showChildrenCategories('${n}', '${showCategoriesChildrenEvent}', <%= c.getId() %>);" id="${n}linkCatId<%= c.getId() %>">Show Children(<%= c.getNumChildren() %>)</a> |
                    <%
                        }
                    %>
                     <a href="${filterCategoryPostsAction}&filterCategoryId=<%= c.getId()%>" title="Show Posts"><span class="glyphicon glyphicon-file margin-right margin-left-cat"></span></a>
                    | <a href="${filterCategoryUploadsAction}&filterCategoryId=<%= c.getId()%>" title="Show Uploads"><span class="glyphicon glyphicon-picture margin-right margin-left-cat"></span></a>
                    <% if (isManager) { %>| <a href="${filterCategoryTemplatesAction}&filterCategoryId=<%= c.getId()%>" title="Show Templates"><span class="glyphicon glyphicon-th margin-right margin-left-cat"></span></a>  <% } %>
                    </div>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</div>

<input type="hidden" id="${n}aclCategoryId" name="aclCategoryId" />
<div id="${n}categories-acls" class="wcm-popup-categories wcm-dialog">
    <div id="${n}categories-acls-title" class="wcm-dialog-title">ACLs</div>
    <a href="#" id="${n}close-categories-acls" class="wcm-dialog-close"><span> </span></a>
    <div class="wcm-dialog-body">
        <div class="wcm-acl-new">
            New ACL:

            <div class="wcm-acl-type"><select id="${n}aclType" name="aclType" class="wcm-input wcm-acl-type-input">
                <option value="<%= Wcm.ACL.WRITE %>">WRITE</option>
                <option value="<%= Wcm.ACL.NONE%>">NONE</option>
            </select></div>

            <div class="wcm-acl-group"><select id="${n}aclWcmGroup" name="aclWcmGroup" class="wcm-input wcm-acl-group-input">
                <option value="<%= Wcm.GROUPS.ALL%>">ALL</option>
                <%
                    Set<String> wcmGroups = (Set<String>)request.getAttribute("wcmGroups");
                    if (wcmGroups != null) {
                        for (String groupId : wcmGroups) {
                %>
                <option value="<%= groupId %>"><%= groupId %></option>
                <%
                        }
                    }
                %>
            </select></div>
            <a href="javascript:;" onclick="addAcl('${n}', '${addAclCategoryEvent}')" id="${n}addAclButton" class="button" title="Add ACL"><span class="glyphicon glyphicon-plus"></span></a>
        </div>

        <div class="wcm-acl-attached" id="${n}categories-acls-attached">
        </div>

    </div>
</div>