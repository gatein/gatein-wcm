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
<%@ page import="java.util.List" %>
<%@ page import="org.gatein.wcm.domain.Category" %>
<%@ page import="static org.gatein.wcm.Wcm.*" %>
<%@ page import="org.gatein.wcm.domain.UserWcm" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@include file="../urls.jsp"%>
<%
    /*
        UserWcm of request
     */
    Object o = request.getAttribute("userWcm");
    UserWcm userWcm = null;
    boolean isManager = false;
    boolean canWrite = false;
    if (o instanceof UserWcm) {
        userWcm = (UserWcm)o;
        isManager = userWcm.isManager();
    }
    String namespace = (String)request.getAttribute("namespace");
    String parentid = (String)request.getAttribute("parentid");
    String showchildrenhref = (String)request.getAttribute("showchildrenhref");
%>
<ul id="<%= namespace %>listChildrenId<%= parentid %>" class="wcm-categories">
<%
    List<Category> list = (List<Category>)request.getAttribute("categories");
    if (list != null) {
        for (Category c : list) {
            if (userWcm != null)
                canWrite = userWcm.canWrite(c);
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
        <div id="<%= namespace %>categoryId<%= c.getId()%>">
            <div class="wcm-category-title"><span class="glyphicon glyphicon-<%= typeIcon %> margin-right wcm-<%= color%>"></span> <%= c.getName() %> <span class="wcm-category-type">(<%= type %>)</span></div>
            <div class="wcm-category-actions"><% if (canWrite) { %><a href="<%= editCategoryView %>&editid=<%= c.getId() %>">Edit</a> | <a href="javascript:deleteCategory('<%= namespace %>', <%= c.getId() %>)">Delete</a> | <a href="javascript:;" onclick="javascript:showSingleAclCategory('<%= namespace %>', this.id, '${showCategoryAclsEvent}', '<%= c.getId() %>', '${categoriesView}');" id="${n}addAcl<%= c.getId() %>">Security</a> | <% } %>
                <%
                    if (c.getNumChildren() > 0) {
                %>
                 <a href="javascript:showChildrenCategories('<%= namespace %>', '<%= showCategoriesChildrenEvent %>', <%= c.getId() %>);" id="<%= namespace %>linkCatId<%= c.getId() %>">Show Children(<%= c.getNumChildren() %>)</a> |
                <%
                    }
                %>
                 <a href="${filterCategoryPostsAction}&filterCategoryId=<%= c.getId()%>" title="Show Posts"><span class="glyphicon glyphicon-file margin-right margin-left-cat"></span></a>
                | <a href="${filterCategoryUploadsAction}&filterCategoryId=<%= c.getId()%>" title="Show Uploads"><span class="glyphicon glyphicon-picture margin-right margin-left-cat"></span></a>
                <% if (isManager) { %>| <a href="${filterCategoryTemplatesAction}&filterCategoryId=<%= c.getId()%>" title="Show Templates"><span class="glyphicon glyphicon-th margin-right margin-left-cat"></span></a><% } %>
            </div>
        </div>
    </li>
<%
        }
    }
%>
</ul>