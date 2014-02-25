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
<%@ page import="org.gatein.wcm.portlet.util.ViewMetadata" %>
<%@ page import="org.gatein.wcm.domain.Post" %>
<%@ page import="org.gatein.wcm.domain.Comment" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.gatein.wcm.portlet.util.ParseDates" %>
<%@ page import="org.gatein.wcm.domain.UserWcm" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@include file="../urls.jsp"%>
<%
    String n = (String)request.getAttribute("namespace");
    Post p = (Post)request.getAttribute("post");
    UserWcm userWcm = (UserWcm)request.getAttribute("userWcm");

    boolean canWrite = userWcm.canWrite(p);

    if (p != null) {
%>
<div class="wcm-comments" >
    <ul>
    <%
        if (p.getComments() != null) {
            for (Comment c : p.getComments()) {
    %>
        <li>
            <%
                String statusClass = "";
                if (c.getStatus().equals(Wcm.COMMENT.REJECTED)) {
                    statusClass = "wcm-orange";
                }
            %>
            <div class="wcm-comment-a"><span class="glyphicon glyphicon-comment <%= statusClass %>"></span></div>
            <div class="wcm-comment-b <%= statusClass %>"><%= c.getContent() %></div>
            <div class="wcm-comment-c">
                <div class="wcm-comment-date"><%= ParseDates.parse(c.getCreated()) %></div>
                <div class="wcm-comment-user"><span class="glyphicon glyphicon-user"></span> <%= c.getAuthor() %></div>
                <%
                    if (canWrite) {
                %>
                <div class="wcm-comment-actions">
                    <%
                        if (c.getStatus().equals(Wcm.COMMENT.PUBLIC)) {
                    %>
                    <span class="glyphicon glyphicon-eye-close margin-top"></span> <a href="javascript:;" onclick="changeStatusComment('<%= n %>', '<%= changeStatusCommentPostEvent%>', '<%= p.getId() %>', '<%= c.getId()%>', '<%= Wcm.COMMENT.REJECTED %>');">Reject</a>
                    <%
                        }
                    %>
                    <%
                        if (c.getStatus().equals(Wcm.COMMENT.REJECTED)) {
                    %>
                    <span class="glyphicon glyphicon-eye-open margin-top"></span> <a href="javascript:;" onclick="changeStatusComment('<%= n %>', '<%= changeStatusCommentPostEvent%>', '<%= p.getId() %>', '<%= c.getId()%>', '<%= Wcm.COMMENT.PUBLIC %>');">Public</a>
                    <%
                        }
                    %>
                    <span class="glyphicon glyphicon-remove margin-top"></span> <a href="javascript:;" onclick="changeStatusComment('<%= n %>', '<%= changeStatusCommentPostEvent%>', '<%= p.getId() %>', '<%= c.getId()%>', '<%= Wcm.COMMENT.DELETED %>');">Delete</a>
                </div>
                <%
                    }
                %>
            </div>
            <div class="clear"></div>
        </li>
    <%
            }
        }
    %>
    </ul>
</div>
<%
        boolean canComment = false;
        if (p.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS)) canComment = true;
        if (p.getCommentsStatus().equals(Wcm.COMMENTS.LOGGED) && !userWcm.getUsername().equals("anonymous")) canComment = true;
%>
<div class="wcm-newcomment">
    <% if (canComment) { %>
    <div class="wcm-comment-a">
        <span class="glyphicon glyphicon-comment"></span>
    </div>
    <div class="wcm-comment-b">
        <textarea id="<%= n %>newComment" name="newComment" class="wcm-input wcm-newcomment-comment" onfocus="if (this.value == 'New Comment') this.value=''" onblur="if (this.value == '') this.value='New Comment'">New Comment</textarea>

    </div>
    <% } %>
    <div class="wcm-comment-c">
        <% if (!p.getCommentsStatus().equals(Wcm.COMMENTS.NO_COMMENTS)) { %>
        <a href="javascript:;" onclick="addComment('<%= n %>', '<%= addCommentPostEvent%>', '<%= p.getId() %>');" id="<%= n %>addCommentButton" class="button" title="Add Comment"><span class="glyphicon glyphicon-comment"></span></a>
        <% } %>
        <% if (canWrite) { %>
        <div class="wcm-newtemplate-type wcm-comment-type">
            <select id="<%= n %>postCommentsStatus" name="postCommentsStatus" class="wcm-input wcm-comment-select" onchange="changeCommentsPost('<%= n %>', '<%= changeCommentsPostEvent%>', '<%= p.getId() %>');">
                <option value="<%= Wcm.COMMENTS.ANONYMOUS%>" <% if (p.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS)) { %>selected <% } %> >Anonymous</option>
                <option value="<%= Wcm.COMMENTS.LOGGED%>" <% if (p.getCommentsStatus().equals(Wcm.COMMENTS.LOGGED)) { %>selected <% } %>>Logged</option>
                <option value="<%= Wcm.COMMENTS.NO_COMMENTS%>" <% if (p.getCommentsStatus().equals(Wcm.COMMENTS.NO_COMMENTS)) { %>selected <% } %>>No Comments</option>
            </select>
        </div>
        <% } %>
    </div>
    <div class="clear"></div>
</div>
<%
    }
%>