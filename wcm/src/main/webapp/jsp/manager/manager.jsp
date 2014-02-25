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
<%@include file="../imports.jsp"%>
<%@include file="../urls.jsp"%>
<script type="text/javascript" src="<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/js/manager/manager.js") %>"></script>

<div id="${n}manager-locks" class="wcm-popup-categories wcm-dialog">
    <div id="${n}manager-locks-title" class="wcm-dialog-title">Locks</div>
    <a href="#" id="${n}close-manager-locks" class="wcm-dialog-close"><span> </span></a>
    <div class="wcm-dialog-body" id="${n}manager-locks-list">

    </div>
</div>

<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>

    <div class="wcm-manager-actions">
        <div class="wcm-manager-locks">
            <span class="glyphicon glyphicon-lock margin-right margin-top"></span> <a href="javascript:;" onclick="showLocks('${n}', '${showLocksEvent}', '${managerView}');">Locks</a>
        </div>
    </div>

</div>
