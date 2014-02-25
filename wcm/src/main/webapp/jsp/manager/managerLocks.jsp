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
<%@ page import="org.gatein.wcm.domain.Post" %>
<%@ page import="org.gatein.wcm.portlet.util.ParseDates" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gatein.wcm.domain.Lock" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.gatein.wcm.domain.Category" %>
<%@ page import="org.gatein.wcm.domain.Upload" %>
<%@ page import="org.gatein.wcm.domain.Template" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@include file="../urls.jsp"%>
<%
    String n = (String)request.getAttribute("namespace");
    List<Lock> locks = (List)request.getAttribute("locks");
    Map<Long, Object> locksObjects = (Map)request.getAttribute("locksObjects");

    if (locks != null && locksObjects != null) {
%>
<div class="wcm-manager-body-locks" >
    <ul>
        <%
            for (Lock l : locks) {
                Object o = locksObjects.get(l.getOriginId());
                Post p = null;
                Category c = null;
                Upload u = null;
                Template t = null;
                if (o instanceof Post) {
                    p = (Post)o;
                } else if (o instanceof Category) {
                    c = (Category)o;
                } else if (o instanceof Upload) {
                    u = (Upload)o;
                } else if (o instanceof Template) {
                    t = (Template)o;
                }
        %>
        <li>
            <div class="wcm-lock-post left margin-top">
                <% if (p != null) { %>
                <span class="glyphicon glyphicon-file margin-right margin-top"></span>
                <%= p.getTitle() %>
                <% } %>
                <% if (c != null) { %>
                <span class="glyphicon glyphicon-tags margin-right margin-top"></span>
                <%= c.getName() %>
                <% } %>
                <% if (u != null) { %>
                <span class="glyphicon glyphicon-picture margin-right margin-top"></span>
                <%= u.getFileName() %>
                <% } %>
                <% if (t != null) { %>
                <span class="glyphicon glyphicon-th margin-right margin-top"></span>
                <%= t.getName()%>
                <% } %>
            </div>
            <div class="wcm-lock-user left margin-top"><span class="glyphicon glyphicon-user margin-right margin-top"></span> <%= l.getUsername() %></div>
            <div class="wcm-lock-created left margin-top"><span class="glyphicon glyphicon-time margin-right margin-top"></span> <%= ParseDates.parse(l.getCreated()) %></div>
            <div class="wcm-lock-action left margin-top">
                <span class="glyphicon glyphicon-share-alt margin-right margin-top"></span> <a href="javascript:;" onclick="removeLock('<%= n %>', '<%= removeLockEvent %>', '<%= l.getOriginId() %>', '<%= l.getType() %>')" title="Remove lock">Unlock</a>
            </div>
            <div class="clear"></div>
        </li>
        <%
            }
        %>
    </ul>
</div>
<% } %>