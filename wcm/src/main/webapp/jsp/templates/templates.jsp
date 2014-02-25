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
<%@ page import="org.gatein.wcm.domain.Template" %>
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>

<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>
    <%@include file="templatesActions.jsp"%>

    <form id="${n}deleteTemplateForm" method="post" action="${deleteTemplateAction}">
        <input type="hidden" id="${n}deleteTemplateId" name="deleteTemplateId" />
    </form>
    <input type="hidden" id="${n}listTemplateId" name="listTemplateId" />
    <table class="wcm-posts" id="${n}templates">
        <%
            List<Template> listTemplates = (List<Template>)renderRequest.getAttribute("list");
            if (listTemplates != null) {
                for (Template t : listTemplates) {
        %>
        <tr class="row-post">
            <td class="row-checkbox">
                <div class="wcm-checkbox margin-left">
                    <input type="checkbox" value="<%= t.getId() %>" id="${n}selectRow<%= t.getId() %>" name="" onchange="selectTemplate('${n}', '<%= t.getId() %>')" />
                    <label for="${n}selectRow<%= t.getId() %>"></label>
                </div>
            </td>
            <td class="row-type"><span class="glyphicon glyphicon-th margin-right"></span></td>
            <td>
                <div>
                    <div class="wcm-post-title"><a href="${editTemplateView}&editid=<%= t.getId() %>"><%= t.getName() %></a></div>
                    <div class="wcm-post-categories"><%
                        for (Category cU : t.getCategories()) {
                    %>[<a href="javascript:showFilterCategoriesById('${n}', '<%= cU.getId() %>');"><%= cU.getName() %></a> <a href="${removeCategoryTemplateAction}&catid=<%= cU.getId() %>&templateid=<%= t.getId() %>" class="wcm-delete-category"><span class="glyphicon glyphicon-remove middle"></span></a>] <%
                        }
                    %>
                    </div>
                    <div class="wcm-post-actions"><a href="${editTemplateView}&editid=<%= t.getId() %>">Edit</a> | <a href="javascript:deleteTemplate('${n}', <%= t.getId() %>)">Delete</a> | <a href="javascript:;" onclick="javascript:showSingleCategoriesTemplate('${n}', this.id, '<%= t.getId() %>');" id="${n}addCategory<%= t.getId() %>" >Category</a> | <a href="javascript:;" onclick="javascript:showRelationshipsTemplate('${n}', this.id, '${showTemplateRelationshipsEvent}', '<%= t.getId() %>', '${templatesView}');" id="${n}addRelationShip<%= t.getId() %>">Relationships</a></div>
                </div>
            </td>
            <td class="row-author"><%= t.getUser() %></td>
            <td class="row-timestamp"><%= ParseDates.parse(t.getModified()) %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>
