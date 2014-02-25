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
<%@ page import="org.gatein.wcm.portlet.util.ViewMetadata" %>
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>

<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/templates/template.js") %>"></script>
<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>
    <%
        List<Category> listCategories = (List<Category>)portletSession.getAttribute("categories");
    %>
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

    <form id="${n}newTemplateForm" method="post" action="${newTemplateAction}">
    <div class="wcm-newpost-title"><input id="${n}templateName" name="templateName" class="wcm-input" value="Template Name" onfocus="if (this.value == 'Template Name') this.value=''" onblur="if (this.value == '') this.value='Template Name'"/></div>
    <div class="wcm-newtemplate">
        <span class="glyphicon glyphicon-globe margin-right margin-top"></span>
        Locale: <div class="wcm-newtemplate-locale"><input id="${n}templateLocale" name="templateLocale" class="wcm-input" value="<%= renderRequest.getLocale().getLanguage() %>"/></div>
        <a href="javascript:saveNewTemplate('${n}');" class="button" title="Save Template">Save Template</a>
    </div>
    <script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/ckeditor/ckeditor.js") %>"></script>
    <input id="${n}urlShowPostUploadsEvent" type="hidden" value="${showPostUploadsEvent}" />
    <script>
        CKEDITOR.on( 'instanceCreated', function( event ) {
            var editor = event.editor;
            editor.portalnamespace='${n}';
            editor.config.enterMode = CKEDITOR.ENTER_BR;
        });
    </script>
    <textarea class="ckeditor" id="${n}templateContent" name="templateContent"></textarea>
    </form>
</div>