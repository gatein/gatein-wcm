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
<%@ page import="org.gatein.wcm.domain.Category" %>
<%@ page import="org.gatein.wcm.domain.Post" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<portlet:defineObjects />
<c:set var="n"><portlet:namespace/></c:set>
<ul>
    <%
        List<Object> listAttachedContent = (List<Object>)resourceRequest.getAttribute("listAttachedContent");
        if (listAttachedContent != null) {
            for (Object o : listAttachedContent) {
                if (o instanceof Category) {
                    Category c = (Category)o;
        %>
        <li id="${n}attached<%= c.getId()%>_C"><span class="glyphicon glyphicon-tags margin-right margin-top"></span> <%= c.getName() %> <a href="#" onclick="deleteAttachment${n}('<%= c.getId()%>_C');"><span class="glyphicon glyphicon-remove middle"></span></a></li>
        <%
                } else {
                    Post p = (Post)o;
        %>
        <li id="${n}attached<%= p.getId()%>_P"><span class="glyphicon glyphicon-file margin-right margin-top"></span> <%= p.getTitle() %> <a href="#" onclick="deleteAttachment${n}('<%= p.getId()%>_P');"><span class="glyphicon glyphicon-remove middle"></span></a></li>
        <%
                }
            }
        }
    %>
</ul>