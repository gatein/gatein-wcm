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
<%@ page import="org.gatein.wcm.domain.Upload" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@include file="../urls.jsp"%>

<%
    String n = (String)request.getAttribute("namespace");
%>
<ul>
<%
    List<Upload> uploads = (List<Upload>)request.getAttribute("uploads");
    if (uploads != null) {
        for (Upload u : uploads) {
%>
    <portlet:resourceURL var="downloadUploadEvent">
        <portlet:param name="event" value="<%= Wcm.EVENTS.DOWNLOAD_UPLOAD %>" />
        <portlet:param name="uploadid" value="<%= u.getId().toString() %>" />
    </portlet:resourceURL>
    <li data-id="<%= u.getId() %>" data-mimetype="<%= (u.getMimeType() != null ? u.getMimeType():"") %>" data-filename="<%= u.getFileName() %>">
        <%= u.getFileName() %>
        <span class="wcm-blue"><%=(u.getDescription()!=null && !u.getDescription().equals("")?"(" + u.getDescription() + ")":"")%></span>
        <a href="#" id="${n}upload<%= u.getId() %>" <% if (u.getMimeType() != null && u.getMimeType().startsWith("image")) { %>onmouseover="showPreview('<%= n %>', this.id, '${downloadUploadEvent}');" onmouseout="hidePreview('<%= n %>', this.id, '${downloadUploadEvent}');" <% } %>><span class="wcm-upload-mimetype"><%= u.getMimeType() %></span></a> <% if (u.getMimeType() != null && u.getMimeType().startsWith("image")) { %><span class="wcm-upload-mimetype glyphicon glyphicon-eye-open margin-top"></span><% } %>
    </li>
<%
        }
    }
%>
</ul>