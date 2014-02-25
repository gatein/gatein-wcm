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
<%@ page import="org.gatein.wcm.domain.UserWcm" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gatein.wcm.portlet.util.ViewMetadata" %>
<%@include file="../../imports.jsp"%>
<portlet:resourceURL var="addCommentPost">
    <portlet:param name="event" value="<%= Wcm.EVENTS.ADD_COMMENT_POST %>" />
</portlet:resourceURL>
<portlet:resourceURL var="unlockPostEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UNLOCK_POST %>" />
</portlet:resourceURL>
<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/content/render/content.js") %>"></script>
<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/content/render/contentEditor.js") %>"></script>
<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/ckeditor/ckeditor.js") %>"></script>
<%
    List<Category> listCategories = (List<Category>)request.getAttribute("categories");
%>
<% String n = renderResponse.getNamespace(); %>
<input id="<%=n%>-addCommentPost" type="hidden" value="<%= addCommentPost %>" />
<div id="${n}uploads-preview" class="wcm-upload-preview">
    <img id="${n}uploads-preview-content" class="wcm-upload-image" src="" />
</div>
<div id="${n}post-select-upload" class="wcm-popup-categories wcm-dialog">
    <div class="wcm-dialog-title">Select upload to Add</div>
    <a href="#" id="${n}close-post-select-upload" class="wcm-dialog-close"><span> </span></a>
    <div class="wcm-dialog-body">
        <div class="wcm-select left">
            <select id="${n}selectFilterCategory" class="wcm-input">
                <option value="-1">All categories</option>
                <%
                    if (listCategories != null) {
                        for (Category c : listCategories) {
                %>
                <option value="<%= c.getId()%>"><%= ViewMetadata.categoryTitle(c) %> </option>
                <%
                        }
                    }
                %>
            </select>
        </div>
        <div class="wcm-post-filtername right">
            <%
                String filterName = "Filter By Name";
            %>
            <input id="${n}inputFilterName" class="wcm-input" value="<%= filterName %>" onfocus="if (this.value == 'Filter By Name') this.value=''" onblur="if (this.value == '') this.value='Filter By Name'" />
        </div>
        <div class="clear"></div>
        <div class="wcm-post-uploads" id="${n}listUploads">
        </div>
    </div>
</div>
<script>
    CKEDITOR.on( 'instanceCreated', function( event ) {
        var editor = event.editor;
        editor.portalnamespace='${n}';
        editor.on( 'focus', function( event ) {
            preSave('${n}', event.editor);

        });
        editor.on( 'blur', function( event ) {
            postSave('${n}', event.editor);
        });
        editor.on( 'configLoaded', function() {
            editor.config.removePlugins = 'stylescombo';
        });
    });
    checkExit('<%= n %>', '<%= request.getAttribute("editid") %>', '<%= unlockPostEvent %>&event=<%= Wcm.EVENTS.UNLOCK_POST %>');
</script>
<portlet:resourceURL var="showPostUploadsEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.SHOW_POST_UPLOADS %>" />
</portlet:resourceURL>
<portlet:resourceURL var="updateContentEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.UPDATE_CONTENT_POST %>" />
</portlet:resourceURL>
<input id="${n}urlShowPostUploadsEvent" type="hidden" value="${showPostUploadsEvent}" />
<input id="${n}urlUpdateContentEvent" type="hidden" value="${updateContentEvent}" />
<input id="${n}originalContent" type="hidden" value="" />
<input id="${n}postId" type="hidden" value="" />
<input id="${n}contentType" type="hidden" value="" />
<%
    String processedTemplate =  (String)renderRequest.getAttribute("processedTemplate");
    if (processedTemplate != null) {
%>
<%= processedTemplate %>
<%
    }
%>