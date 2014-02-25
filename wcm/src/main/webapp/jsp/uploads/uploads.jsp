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
<%@ page import="org.gatein.wcm.domain.Upload" %>
<%@ page import="org.gatein.wcm.portlet.util.ParseDates" %>
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>

<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>
    <%@include file="uploadsActions.jsp"%>

    <form id="${n}deleteUploadForm" method="post" action="${deleteUploadAction}">
        <input type="hidden" id="${n}deleteUploadId" name="deleteUploadId" />
    </form>
    <input type="hidden" id="${n}listUploadId" name="listUploadId" />
    <table class="wcm-posts" id="${n}uploads">
        <%
            List<Upload> listUploads = (List<Upload>)renderRequest.getAttribute("list");
            if (listUploads != null) {
                for (Upload u : listUploads) {
                    boolean canWrite = userWcm.canWrite(u);
         %>
        <portlet:resourceURL var="downloadUploadEvent">
            <portlet:param name="event" value="<%= Wcm.EVENTS.DOWNLOAD_UPLOAD %>" />
            <portlet:param name="uploadid" value="<%= u.getId().toString() %>" />
        </portlet:resourceURL>
        <tr class="row-post">
            <td class="row-checkbox">
                <div class="wcm-checkbox margin-left">
                    <% if (canWrite) { %>
                    <input type="checkbox" value="<%= u.getId() %>" id="${n}selectRow<%= u.getId() %>" name="" onchange="selectUpload('${n}', '<%= u.getId() %>')" />
                    <label for="${n}selectRow<%= u.getId() %>"></label>
                    <% } %>
                </div>
            </td>
            <td class="row-type"><span class="glyphicon glyphicon-picture margin-right"></span></td>
            <td>
                <div>
                    <div class="wcm-post-title"><a href="${downloadUploadEvent}" target="_blank"><%= u.getFileName() %>&nbsp;<span class="wcm-blue"><%= (u.getDescription().equals("")?"":"(" + u.getDescription() + ")") %></span></a> <a href="${downloadUploadEvent}" target="_blank" id="${n}preview<%= u.getId() %>" <% if (u.getMimeType() != null && u.getMimeType().startsWith("image")) { %>onmouseover="showPreview('${n}', this.id, '${downloadUploadEvent}');" onmouseout="hidePreview('${n}', this.id, '${downloadUploadEvent}');" <% } %>><span class="wcm-upload-mimetype"><%= u.getMimeType() %></span></a> <% if (u.getMimeType() != null && u.getMimeType().startsWith("image")) { %><span class="wcm-upload-mimetype glyphicon glyphicon-eye-open margin-top"></span><% } %></div>
                    <div class="wcm-post-categories"><%
                            for (Category cU : u.getCategories()) {
                                if (userWcm.canRead(cU)) {
                        %>[<a href="javascript:showFilterCategoriesById('${n}', '<%= cU.getId() %>');"><%= cU.getName() %></a><% if (canWrite) { %> <a href="${removeCategoryUploadAction}&catid=<%= cU.getId() %>&uploadid=<%= u.getId() %>" class="wcm-delete-category"><span class="glyphicon glyphicon-remove middle"></span></a><% } %>] <%
                                }
                            }
                        %>
                    </div>
                    <% if (canWrite) { %>
                    <div class="wcm-post-actions"><a href="${editUploadView}&editid=<%= u.getId() %>">Edit</a> | <a href="javascript:deleteUpload('${n}', <%= u.getId() %>)">Delete</a> | <a href="javascript:;" onclick="javascript:showSingleCategories('${n}', this.id, '<%= u.getId() %>');" id="${n}addCategory<%= u.getId() %>" >Category</a> | <a href="javascript:;" onclick="javascript:showSingleAclUpload('${n}', this.id, '${showUploadAclsEvent}', '<%= u.getId() %>', '${uploadsView}');" id="${n}addAcl<%= u.getId() %>">Security</a></div>
                    <% } %>
                </div>
            </td>
            <td class="row-author"><%= u.getUser() %></td>
            <td class="row-timestamp"><%= ParseDates.parse(u.getModified()) %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>