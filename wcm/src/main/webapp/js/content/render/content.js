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

function wcmAddComment(namespace, postid) {
    require(["SHARED/jquery"], function($) {
        var href = $("#" + namespace + "-addCommentPost").val();
        var content = "";
        if ($("#" + namespace + "-content").length > 0) {
            content = $("#" + namespace + "-content").val();
        }
        var author = "";
        if ($("#" + namespace + "-author").length > 0) {
            author = $("#" + namespace + "-author").val();
        }
        var email = "";
        if ($("#" + namespace + "-email").length > 0) {
            email = $("#" + namespace + "-email").val();
        }
        var url = "";
        if ($("#" + namespace + "-url").length > 0) {
            url = $("#" + namespace + "-url").val();
        }
        $.ajax({
            type: "POST",
            url: href + "&postid=" + postid,
            cache: false,
            dataType: "text",
            data: {
                content: content,
                author: author,
                email: email,
                url: url
            },
            success: function(data)
            {
                location.reload();
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing wcmAddComment()");
            }
        });
    });
}