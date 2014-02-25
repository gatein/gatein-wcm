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
<%@ page import="org.gatein.wcm.domain.Post" %>
<%@ page import="org.gatein.wcm.domain.Category" %>
<%@ page import="org.gatein.wcm.portlet.util.ParseDates" %>
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>

<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>
    <%@include file="postsActions.jsp"%>

    <form id="${n}deletePostForm" method="post" action="${deletePostAction}">
        <input type="hidden" id="${n}deletePostId" name="deletePostId" />
    </form>
    <input type="hidden" id="${n}listPostId" name="listPostId" />
    <form id="${n}publishPostForm" method="post" action="${publishPostAction}">
        <input type="hidden" id="${n}publishPostId" name="publishPostId" />
        <input type="hidden" id="${n}publishState" name="publishState" />
    </form>
    <table class="wcm-posts" id="${n}posts">
        <%
            List<Post> listPosts = (List<Post>)renderRequest.getAttribute("list");
            if (listPosts != null) {
                for (Post p : listPosts) {
                    boolean canWrite = userWcm.canWrite(p);
        %>
        <tr class="row-post">
            <td class="row-checkbox">
                <div class="wcm-checkbox margin-left">
                    <% if (canWrite) { %>
                    <input type="checkbox" value="<%= p.getId() %>" id="${n}selectRow<%= p.getId() %>" name="" onchange="selectPost('${n}', '<%= p.getId() %>')" />
                    <label for="${n}selectRow<%= p.getId() %>"></label>
                    <% } %>
                </div>
            </td>
            <td class="row-type"><span class="glyphicon glyphicon-file margin-right"></span></td>
            <td>
                <div>
                    <div class="wcm-post-title"><a href="${editPostView}&editid=<%= p.getId() %>"><%= p.getTitle() %></a></div>
                    <div class="wcm-post-categories"><%
                        for (Category cU : p.getCategories()) {
                            if (userWcm.canRead(cU)) {
                    %>[<a href="javascript:showFilterCategoriesById('${n}', '<%= cU.getId() %>');"><%= cU.getName() %></a><% if (canWrite) { %> <a href="${removeCategoryPostAction}&catid=<%= cU.getId() %>&postid=<%= p.getId() %>" class="wcm-delete-category"><span class="glyphicon glyphicon-remove middle"></span></a><% } %>] <%
                            }
                        }
                    %>
                    </div>
                    <div class="wcm-post-actions"><% if (canWrite) { %><a href="${editPostView}&editid=<%= p.getId() %>">Edit</a> | <a href="javascript:deletePost('${n}', <%= p.getId() %>)">Delete</a> | <a href="javascript:;" onclick="javascript:showSingleCategoriesPost('${n}', this.id, '<%= p.getId() %>');" id="${n}addCategory<%= p.getId() %>">Category</a> | <a href="javascript:;" onclick="javascript:showSingleAclPost('${n}', this.id, '${showPostAclsEvent}', '<%= p.getId() %>', '${postsView}');" id="${n}addAcl<%= p.getId() %>">Security</a> | <a href="javascript:;" onclick="javascript:showRelationshipsPost('${n}', this.id, '${showPostRelationshipsEvent}', '<%= p.getId() %>', '${postsView}');" id="${n}addRelationShip<%= p.getId() %>">Relationships</a> | <% } %> <a href="javascript:;" onclick="javascript:showCommentsPost('${n}', this.id, '${showPostCommentsEvent}', '<%= p.getId() %>', '${postsView}');" id="${n}comments<%= p.getId() %>">Comments(<%= p.getComments().size() %>)</a></div>
                </div>
            </td>
            <td class="row-author"><%= p.getAuthor() %></td>
            <%
                String statusColor = "red";
                String status = "Draft";
                String statusThumb = "down";
                String nextStatus = Wcm.POSTS.PUBLISHED.toString();
                if (p.getPostStatus().equals(Wcm.POSTS.PUBLISHED)) {
                    statusColor = "green";
                    status = "Published";
                    statusThumb = "up";
                    nextStatus = Wcm.POSTS.DRAFT.toString();
                }
            %>
            <td class="row-status"><% if (canWrite) { %><a href="javascript:publishPost('${n}', '<%= p.getId() %>', '<%= nextStatus %>');"><% } %><span class="glyphicon glyphicon-thumbs-<%= statusThumb %> wcm-<%=statusColor %> middle"> <%= status %></span><% if (canWrite) { %></a><% } %> </td>
            <td class="row-timestamp"><%= ParseDates.parse(p.getModified()) %></td>
        </tr>
        <%
                }
            }
        %>
    </table>

</div>
