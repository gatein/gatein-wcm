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

/* Imports functions
==================== */

function showMsg(namespace, msg, title) {
    require(["SHARED/jquery"], function($) {
        var id = "#" + namespace + "msg-dialog";
        var maskid = "#" + namespace + "msg-mask";
        var closeid = "#" + namespace + "close-msg-dialog";
        var msgId = "#" + namespace + "msg-dialog-body";
        var titleId = "#" + namespace + "msg-dialog-title";

        // Setting title
        $(titleId).html(title);
        // Setting msg
        $(msgId).html(msg);
        // Setting mask
        var maskHeight = $(document).height();
        var maskWidth = $(window).width();
        $(maskid).css({'top':0, 'left': 0, 'width': maskWidth, 'height': maskHeight});
        $(maskid).fadeTo("fast", 0.3);
        // Setting dialog
        var winH = $(window).height();
        var winW = $(window).width();
        // $(id).css('top',  winH/2-$(id).height()/2);
        $(id).css('left', winW/2-$(id).width()/2);
        $(id).show();
        // Setting close button
        $(closeid).click(function (e) {
            e.preventDefault();
            $(maskid).hide();
            $(id).hide();
        });
    });
}


