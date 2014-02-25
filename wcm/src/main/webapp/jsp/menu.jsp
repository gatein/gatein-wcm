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
<%@ page import="org.gatein.wcm.domain.UserWcm" %>
<%
        String view = renderRequest.getParameter("view");
        if (view == null) view = Wcm.VIEWS.POSTS;


%>
<div class="wcm-header">
    <ul>
        <li <% if (view.equals(Wcm.VIEWS.POSTS) || view.equals(Wcm.VIEWS.NEW_POST) || view.equals(Wcm.VIEWS.EDIT_POST)) { %>class="selected" <% } %>><a href="${postsView}">Posts</a></li>
        <li <% if (view.equals(Wcm.VIEWS.CATEGORIES) || view.equals(Wcm.VIEWS.NEW_CATEGORY) || view.equals(Wcm.VIEWS.EDIT_CATEGORY)) { %>class="selected" <% } %>><a href="${categoriesView}">Categories</a></li>
        <li <% if (view.equals(Wcm.VIEWS.UPLOADS) || view.equals(Wcm.VIEWS.NEW_UPLOAD) || view.equals(Wcm.VIEWS.EDIT_UPLOAD)) { %>class="selected" <% } %>><a href="${uploadsView}">Uploads</a></li>
        <%
            if (isManager) {
        %>
        <li <% if (view.equals(Wcm.VIEWS.TEMPLATES) || view.equals(Wcm.VIEWS.NEW_TEMPLATE) || view.equals(Wcm.VIEWS.EDIT_TEMPLATE)) { %>class="selected" <% } %>><a href="${templatesView}">Templates</a></li>
        <li <% if (view.equals(Wcm.VIEWS.MANAGER)) { %>class="selected right-menu" <% } else { %> class="right-menu" <% } %>><a href="${managerView}">Manager</a></li>
        <%
            }
        %>
    </ul>
</div>