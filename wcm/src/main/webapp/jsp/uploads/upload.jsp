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
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>

<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/uploads/upload.js") %>"></script>
<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>

    <form id="${n}newUploadForm" method="post" enctype="multipart/form-data" action="${newUploadAction}">
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-paperclip margin-right margin-top"></span>
            <a href="javascript:showUploadFile('${n}');" class="button" title="Upload file">Upload file</a>
            <input type="file" id="${n}uploadFile" name="uploadFile" class="wcm-newupload-file" />
            <div class="wcm-newupload-name" id="${n}uploadFileName"></div>
            <a href="javascript:saveNewUpload('${n}');" class="button" title="Save Upload">Save Upload</a>
        </div>
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-font margin-right margin-top"></span>
            <label for="${n}uploadFileDescription">Description: </label>
            <div class="wcm-newupload-description"><input id="${n}uploadFileDescription" name="uploadFileDescription" class="wcm-input margin-left-cat" /></div>
        </div>
    </form>
</div>