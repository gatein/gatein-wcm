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
<%@ page import="java.util.Set" %>
<%@ page import="org.gatein.wcm.portlet.util.ParseDates" %>
<%@ page import="org.gatein.wcm.domain.UserWcm" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gatein.wcm.domain.Relationship" %>
<%@ page import="org.gatein.wcm.domain.Template" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@include file="../urls.jsp"%>
<portlet:resourceURL var="addTemplateRelationshipEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.ADD_RELATIONSHIP_TEMPLATE %>" />
</portlet:resourceURL>
<portlet:resourceURL var="removeTemplateRelationshipEvent">
    <portlet:param name="event" value="<%= Wcm.EVENTS.REMOVE_RELATIONSHIP_TEMPLATE %>" />
</portlet:resourceURL>
<%
    String n = (String)request.getAttribute("namespace");
    Template template = (Template)request.getAttribute("template");
    List<Template> templates = (List)request.getAttribute("templates");

    if (templates != null) {
%>
<div class="wcm-relationships" >
    <ul>
        <%
            for (Template t : templates) {
        %>
        <li>
            <div class="wcm-relationship-newtitle left margin-top"><span class="glyphicon glyphicon-th margin-right margin-top"></span> <%= t.getName() %> <span class="glyphicon glyphicon-globe margin-left-locale margin-top"></span> <%= t.getLocale() %></div>
            <div class="wcm-relationship-newkey left margin-top"><span class="glyphicon glyphicon-hand-right margin-right"></span></div>
            <div class="wcm-relationship-input left margin-left">
                <input id="<%= n %>inputNewKey<%= t.getId() %>" class="wcm-input margin-left-cat" />
            </div>
            <div class="wcm-relationship-newkey left margin-top margin-left">
                <a href="javascript:;" onclick="addRelationshipTemplate('<%= n%>', '<%= addTemplateRelationshipEvent%>', '<%= template.getId() %>', '<%= t.getId() %>')" title="Add new relationship"><span class="glyphicon glyphicon-plus margin-right"></span></a>
            </div>
            <div class="clear"></div>
        </li>
        <%
            }
        %>
    </ul>
</div>
<% } %>
<div class="wcm-relationships-selected" >
    <span class="glyphicon glyphicon-random margin-top margin-right"></span> Relationships defined:
    <ul>
        <%
            List<Relationship> relations = (List<Relationship>)request.getAttribute("relations");
            List<Template> templatesRelations = (List<Template>)request.getAttribute("templatesRelations");
            if (relations != null && templatesRelations != null && relations.size() == templatesRelations.size()) {
                for (int i=0; i<relations.size(); i++) {
                    Relationship r = relations.get(i);
                    Template tr = templatesRelations.get(i);
         %>
        <li>
            <div class="wcm-relationship-origin left margin-top left"><span class="glyphicon glyphicon-th margin-right margin-top"></span> <%= template.getName() %> <span class="glyphicon glyphicon-globe margin-left-locale margin-top"></span> <%= template.getLocale() %></div>
            <div class="wcm-relationship-newkey left margin-top margin-left">
                <a href="javascript:;" onclick="removeRelationshipTemplate('<%= n%>', '<%= removeTemplateRelationshipEvent%>', '<%= template.getId() %>', '<%= r.getKey() %>')" title="Remove relationship"><span class="glyphicon glyphicon-minus margin-right"></span></a>
            </div>
            <div class="clear"></div>
            <div class="wcm-relationship-key left margin-top"><span class="glyphicon glyphicon-hand-right margin-right margin-top"></span> <%= r.getKey() %></div>
            <div class="wcm-relationship-newkey left margin-top"><span class="glyphicon glyphicon-chevron-right margin-right margin-top"></span></div>
            <div class="wcm-relationship-newtitle left margin-top left"><span class="glyphicon glyphicon-th margin-right margin-top"></span> <%= tr.getName() %> <span class="glyphicon glyphicon-globe margin-left-locale margin-top"></span> <%= tr.getLocale() %></div>
            <div class="clear"></div>
        </li>
        <%
                }
            }
        %>
    </ul>
</div>