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
    <div id="${n}manager-locks-title" class="wcm-dialog-title">${rsc.getString('manager.locks')}</div>
    <a href="#" id="${n}close-manager-locks" class="wcm-dialog-close"><span> </span></a>
    <div class="wcm-dialog-body" id="${n}manager-locks-list">

    </div>
</div>

<div id="${n}manager-import" class="wcm-popup-categories wcm-dialog">
    <div id="${n}manager-import-title" class="wcm-dialog-title">${rsc.getString('manager.import')}</div>
    <a href="#" id="${n}close-manager-import" class="wcm-dialog-close"><span> </span></a>

    <form id="${n}newImportForm" method="post" enctype="multipart/form-data" action="${newImportAction}">
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-paperclip margin-right margin-top"></span>
            <a href="javascript:showImportFile('${n}');" class="button" title="${rsc.getString('manager.import_file')}">${rsc.getString('manager.import_file')}</a>
            <input type="file" id="${n}importFile" name="importFile" class="wcm-newupload-file" />
            <div class="wcm-newupload-name" id="${n}importFileName"></div>
            <a href="javascript:saveNewImport('${n}', '${rsc.getString('manager.empty_import')}', '${rsc.getString('manager.import')}');" class="button" title="${rsc.getString('manager.save_import')}">${rsc.getString('manager.save_import')}</a>
        </div>
        <div class="wcm-newupload">
            <span class="glyphicon glyphicon-pushpin margin-right margin-top"></span>
            <label for="${n}importStrategy">${rsc.getString('manager.import_strategy')}</label>
            <div class="wcm-manager-strategy">
                <select id="${n}importStrategy" name="importStrategy" class="wcm-input">
                    <option value="<%= Wcm.IMPORT.STRATEGY.NEW%>" selected>${rsc.getString('manager.import_new')}</option>
                    <option value="<%= Wcm.IMPORT.STRATEGY.OVERWRITE%>">${rsc.getString('manager.import_overwrite')}</option>
                    <option value="<%= Wcm.IMPORT.STRATEGY.UPDATE%>">${rsc.getString('manager.import_update')}</option>
                </select>
            </div>
        </div>
    </form>

</div>

<div class="container">
    <%@include file="../menu.jsp"%>
    <%@include file="../submenu.jsp"%>

    <div class="wcm-manager-actions">
        <div class="wcm-manager-locks">
            <span class="glyphicon glyphicon-lock margin-right margin-top"></span> <a href="javascript:;" onclick="showLocks('${n}', '${showLocksEvent}', '${managerView}');">${rsc.getString('manager.locks')}</a>
        </div>
        <div class="wcm-manager-locks margin-top-block">
            <span class="glyphicon glyphicon-download margin-right margin-top "></span> <a href="${exportEvent}">${rsc.getString('manager.export')}</a>
        </div>
        <div class="wcm-manager-locks margin-top-block">
            <span class="glyphicon glyphicon-upload margin-right margin-top "></span> <a href="javascript:;" onclick="showImport('${n}', '${managerView}');">${rsc.getString('manager.import')}</a>
        </div>
    </div>

</div>
