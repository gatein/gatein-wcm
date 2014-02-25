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
<%@ page import="org.gatein.wcm.Wcm" %>

<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/templates/templatesActions.js") %>"></script>
<div class="wcm-posts-actions">
    <div class="wcm-checkbox left">
        <input type="checkbox" value="0" id="${n}selectAll" name="selectAll" onchange="selectAllTemplates('${n}')" />
        <label for="${n}selectAll" title="Select All"></label>
    </div>

    <form id="${n}deleteSelectedTemplateForm" method="post" action="${deleteSelectedTemplateAction}">
        <input type="hidden" id="${n}deleteSelectedListId" name="deleteSelectedListId" />
    </form>
    <div class="left margin-left">
        <a href="javascript:deleteSelectedTemplates('${n}');" class="button" title="Delete selected"><span class="glyphicon glyphicon-trash"></span></a>
    </div>

    <form id="${n}addSelectedCategoryTemplateForm" method="post" action="${addSelectedCategoryTemplateAction}">
        <input type="hidden" id="${n}addSelectedCategoryTemplateListId" name="addSelectedCategoryTemplateListId" />
        <input type="hidden" id="${n}addCategoryTemplateId" name="addCategoryTemplateId" />
    </form>
    <div class="margin-center">
        <a href="#" onclick="showSelectedCategoriesTemplates('${n}', this.id)" class="button" title="Assign Category" id="${n}assign-category"><span class="glyphicon glyphicon-tag"></span></a>
    </div>

    <%  ViewMetadata metadata = (ViewMetadata)portletSession.getAttribute("metadata");
        List<Category> listCategories = (List<Category>)request.getAttribute("categories");
    %>
    <form id="${n}filterCategoryTemplatesForm" method="post" action="${filterCategoryTemplatesAction}">
        <input type="hidden" id="${n}filterCategoryId" name="filterCategoryId" />
    </form>
    <form id="${n}filterNameTemplatesForm" method="post" action="${filterNameTemplatesAction}">
        <input type="hidden" id="${n}filterName" name="filterName"  />
    </form>
    <div class="wcm-select margin-left">
        <select id="${n}selectFilterCategory" class="wcm-input" onchange="showFilterCategoriesTemplates('${n}')">
            <option value="-1">All categories</option>
            <%
                if (listCategories != null) {
                    for (Category c : listCategories) {
            %>
            <option value="<%= c.getId()%>" <% if (metadata != null && metadata.isFilterCategory() && metadata.getCategoryId().equals(c.getId())) { %> selected <% } %> > <%= ViewMetadata.categoryTitle(c) %></option>
            <%
                    }
                }
            %>
        </select>
    </div>
    <div class="wcm-post-filtername margin-left">
        <%
            String filterName = "Filter By Name";
            if (metadata != null && metadata.isFilterName()) filterName = metadata.getName();
        %>
        <input id="${n}inputFilterName" class="wcm-input margin-left-cat" value="<%= filterName %>" onfocus="if (this.value == 'Filter By Name') this.value=''" onblur="if (this.value == '') this.value='Filter By Name'" onkeypress="showFilterNameTemplates(event, '${n}')" />
    </div>
    <%
        if (metadata != null && ((metadata.getToIndex() + 1) < metadata.getTotalIndex())) {
    %>
    <a href="${rightTemplatesAction}" class="button right" title="Older entries"><span class="glyphicon glyphicon-chevron-right"></span></a>
    <%
        }
        if (metadata != null && ((metadata.getFromIndex() +1) >= Wcm.VIEWS.MAX_PER_PAGE)) {
    %>
    <a href="${leftTemplatesAction}" class="button right" title="Newer entries"><span class="glyphicon glyphicon-chevron-left"></span></a>
    <%
        }
        if (metadata != null) {
    %>
    <div class="wcm-pagination right"><%= metadata.getFromIndex()+1 %>-<%= metadata.getToIndex()+1 %> of <%= metadata.getTotalIndex() %></div>
    <%
        }
    %>
</div>
<form id="${n}addCategoryTemplateForm" method="post" action="${addCategoryTemplateAction}">
    <input type="hidden" id="${n}categoryId" name="categoryId" />
    <input type="hidden" id="${n}templateId" name="templateId" />
</form>

<div id="${n}templates-categories" class="wcm-popup-categories wcm-dialog">
    <div class="wcm-dialog-title">Select category to Add</div>
    <a href="#" id="${n}close-posts-categories" class="wcm-dialog-close"><span> </span></a>
    <div class="wcm-dialog-body">
        <div class="wcm-select left">
            <select id="${n}selectAddCategory" class="wcm-input">
                <%
                    if (listCategories != null) {
                        for (Category c : listCategories) {
                %>
                <option value="<%= c.getId()%>"><%= ViewMetadata.categoryTitle(c) %></option>
                <%
                        }
                    }
                %>
            </select>
        </div>
        <a href="#" class="button right" title="Assign Category" id="${n}assign-single-category"><span class="glyphicon glyphicon-tag"></span></a>
        <div class="clear"></div>
    </div>
</div>

<div id="${n}templates-relationships" class="wcm-popup-categories wcm-dialog">
    <div id="${n}templates-relationships-title" class="wcm-dialog-title">Relationships</div>
    <a href="#" id="${n}close-templates-relationships" class="wcm-dialog-close"><span> </span></a>
    <div class="wcm-dialog-body wcm-relationships-select">
        <div class="wcm-select left margin-right">
            <select id="${n}selectFilterCategoryRelationships" class="wcm-input">
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
        <div class="wcm-post-filtername left">
            <%
                filterName = "Filter By Name";
            %>
            <input id="${n}inputFilterNameRelationships" class="wcm-input margin-left-cat" value="<%= filterName %>" onfocus="if (this.value == 'Filter By Name') this.value=''" onblur="if (this.value == '') this.value='Filter By Name'" />
        </div>
        <div class="clear"></div>
    </div>
    <div class="wcm-dialog-body" id="${n}template-relationships-list">

    </div>
</div>