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

/* Manager functions
 =================== */

function showLocks(namespace, href, hrefClose) {
    require(["SHARED/jquery"], function($) {
        // Show popup
        var id = "#" + namespace + "manager-locks";
        var locksId = "#" + namespace + "manager-locks-list";

        // Open comments in the center
        var w = (($(window).width() - 980)/2);
        if (w < 0) w = 100;
        else w = w + 100;
        $(id).css('top',  200 - $(document).scrollTop());    // Fixed at the beginning of
        $(id).css('left', w - $(document).scrollLeft());
        $(id).fadeIn(100);

        $.ajax({
            type: "POST",
            url: href + "&namespace=" + namespace,
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write Comment data
                $(locksId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing showLocks()");
            }
        });

        var closeid = "#" + namespace + "close-manager-locks";
        $(closeid).click(function (e) {
            e.preventDefault();
            $(id).hide();
            window.location.assign(hrefClose);
        });
    });
}

function removeLock(namespace, href, lockid, locktype) {
    require(["SHARED/jquery"], function($) {
        var locksId = "#" + namespace + "manager-locks-list";
        $.ajax({
            type: "POST",
            url: href + "&namespace=" + namespace,
            cache: false,
            dataType: "text",
            data: {
                lockid: lockid,
                locktype: locktype
            },
            success: function(data)
            {
                // Write Comment data
                $(locksId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing showLocks()");
            }
        });
    });
}