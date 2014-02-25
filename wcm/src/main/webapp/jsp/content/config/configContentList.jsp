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
<%@ page import="org.gatein.wcm.domain.Post" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
    String newTypeContent = (String)resourceRequest.getAttribute("newTypeContent");
    if (newTypeContent != null && newTypeContent.equals("C")) {
        List<Category> list = (List<Category>)resourceRequest.getAttribute("chooseContent");
        for (Category c : list) {
%>
<option value="<%= c.getId() %>"><%= c.getName() %></option>
<%
        }
    }
    if (newTypeContent != null && newTypeContent.equals("P")) {
        List<Post> list = (List<Post>)resourceRequest.getAttribute("chooseContent");
        for (Post p : list) {
%>
<option value="<%= p.getId() %>"><%= p.getTitle() %></option>
<%
        }
    }
%>