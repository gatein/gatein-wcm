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
<%@ page import="org.gatein.wcm.domain.UserWcm" %>
<%@include file="imports.jsp"%>
<div class="wcm-notaccess">
<%
    if (renderRequest.getAttribute("userWcm").equals("anonymous")) {
%>
        <div class="wcm-notaccess inner">
            <h2><span class="glyphicon glyphicon-warning-sign wcm-grey icon-big-size"></span> ${rsc.getString('notaccess.anonymous_msg')}</h2>
            <p>${rsc.getString('notaccess.login_msg')}</p>
        </div>
<%
    } else {
        userWcm = (UserWcm)renderRequest.getAttribute("userWcm");
%>
        <div class="wcm-notaccess inner">
            <h2><span class="glyphicon glyphicon-warning-sign wcm-grey icon-big-size"></span> ${rsc.getString('notaccess.user')} <span class="wcm-red"> <%= userWcm.getUsername() %></span> ${rsc.getString('notaccess.norights_msg')}</h2>
            <p>${rsc.getString('notaccess.contact_msg')}</p>
        </div>
<%
    }
%>
</div>