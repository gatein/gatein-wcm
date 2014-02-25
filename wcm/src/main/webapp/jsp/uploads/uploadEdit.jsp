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
<%@ page import="java.util.List" %>
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>

<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/uploads/upload.js") %>"></script>
<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>

    <%
        Upload u = (Upload) renderRequest.getAttribute("edit");
        if (u != null) {
    %>
    <script>
        checkExit('${n}', '<%= u.getId() %>', '<%= unlockUploadEvent %>&event=<%= Wcm.EVENTS.UNLOCK_UPLOAD %>');
    </script>
    <portlet:resourceURL var="downloadUploadEvent">
        <portlet:param name="event" value="<%= Wcm.EVENTS.DOWNLOAD_UPLOAD %>" />
        <portlet:param name="uploadid" value="<%= u.getId().toString() %>" />
    </portlet:resourceURL>

    <form id="${n}changeVersionUploadForm" method="post" action="${changeVersionUploadAction}">
        <input type="hidden" id="${n}uploadVersionId" name="uploadVersionId" value="<%= u.getId() %>" />
        <input type="hidden" id="${n}uploadVersion" name="uploadVersion" value="-1" />
    </form>
    <form id="${n}editUploadForm" method="post" enctype="multipart/form-data" action="${editUploadAction}">
        <input type="hidden" id="${n}editUploadId" name="editUploadId" value="<%= u.getId() %>"/>
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-pushpin margin-right margin-top"></span>
            <label>Filename: </label>
            <div class="wcm-editupload-name"><%= u.getFileName() %></div>
            <a href="${downloadUploadEvent}" target="_blank" class="button" title="Download">&nbsp;&nbsp;Download&nbsp;&nbsp;</a>
        </div>
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-paperclip margin-right margin-top"></span>
            <a href="javascript:showUploadFile('${n}');" class="button" title="Upload file">Upload file</a>
            <input type="file" id="${n}uploadFile" name="uploadFile" class="wcm-newupload-file" onchange="setUploadModified()" />
            <div class="wcm-newupload-name" id="${n}uploadFileName"></div>
            <a href="javascript:saveUpdateUpload('${n}');" class="button" title="Save Upload">Save Upload</a>
        </div>
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-font margin-right margin-top"></span>
            <label for="${n}uploadFileDescription">Description: </label>
            <div class="wcm-newupload-description"><input id="${n}uploadFileDescription" name="uploadFileDescription" class="wcm-input margin-left-cat"  value="<%= u.getDescription() %>" onchange="setUploadModified()" /></div>
        </div>
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-sort margin-right margin-top"></span>
            <label for="${n}uploadVersions">Version: </label>
            <div class="wcm-newupload-versions">
                <select id="${n}uploadVersions" name="uploadVersions" class="wcm-input" onchange="changeVersionUpload('${n}');">
                    <%
                        List<Long> versions = (List<Long>)request.getAttribute("versions");
                        if (versions != null) {
                            if (!versions.contains(u.getVersion())) {
                    %>
                    <option value="<%= u.getVersion()%>" selected><%= u.getVersion()%></option>
                    <%
                        }
                        for (Long version: versions) {
                    %>
                    <option value="<%= version %>" <% if (u.getVersion().equals(version)) { %> selected <% } %>><%= version %></option>
                    <%
                            }
                        }
                    %>
                </select>
            </div>
        </div>
    </form>
    <%
        }
    %>
</div>