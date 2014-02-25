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
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="org.gatein.wcm.Wcm" %>
<portlet:defineObjects />
<%
    /*
        Loading CSS resources
     */
%>
<link rel="stylesheet" href="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/css/wcm.css") %>" />
<link rel="stylesheet" href="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/css/wcm-content.css") %>" />
<link rel="stylesheet" href="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/css/bootstrap-glyphicons.css") %>" />

<%
    /*
        JS events for imports.jsp page
     */
%>
<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/imports.js") %>"></script>
<%
    /*
        Global variable for namespace in all jsp objects
     */
%>
<c:set var="n"><portlet:namespace/></c:set>

<%@ page import="java.util.Locale"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page trimDirectiveWhitespaces="true"%>

<%
    /*
        Global variable for resource bundle
     */
%>
<c:set var="rsc" value="${portletConfig.getResourceBundle(renderRequest.locale)}" />
<%
    /*
        UserWcm of request
     */
    Object o = renderRequest.getAttribute("userWcm");
    UserWcm userWcm = null;
    boolean isManager = false;
    if (o instanceof UserWcm) {
        userWcm = (UserWcm)o;
        isManager = userWcm.isManager();
    }
%>
<%
    /*
        Global layer for modal window containing warning messages
     */
%>
<div id="${n}msg-dialog" class="wcm-modal-window wcm-dialog">
    <div id="${n}msg-dialog-title" class="wcm-dialog-title"></div>
    <a href="#" id="${n}close-msg-dialog" class="wcm-dialog-close"><span> </span></a>
    <div id="${n}msg-dialog-body" class="wcm-dialog-body"></div>
</div>
<div id="${n}msg-mask" class="wcm-modal-mask"></div>
<%
    String errorWcm = request.getParameter("errorWcm");
    if (request.getParameter("errorWcm") != null) {
%>
<script>
    showMsg('${n}', '<%= errorWcm %>', 'Warning');
</script>
<%
    }
%>