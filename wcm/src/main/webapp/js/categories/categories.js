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

/* Categories functions
 ======================= */

function showChildrenCategories(namespace, href, parentid) {
    require(["SHARED/jquery"], function($) {
        var catId = "#" + namespace + "categoryId" + parentid;
        var linkCatId = "#" + namespace + "linkCatId" + parentid;

        $.ajax({
            type: "POST",
            url: href + "&parentid=" + parentid + "&namespace=" + namespace,
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write children list
                $(catId).after(data);
                // Backup references to toggle children
                var listChildrenId = "#" + namespace + "listChildrenId" + parentid;
                $(listChildrenId).attr("data-parent-href", $(linkCatId).attr("href"));
                $(listChildrenId).attr("data-parent-link-id", $(linkCatId).attr("id"));
                $(listChildrenId).attr("data-parent-link", $(linkCatId).text());

                // Change show/hide link
                $(linkCatId).attr("href", "javascript:hideChildrenCategories('" + namespace + "listChildrenId" + parentid + "');");
                $(linkCatId).text("Hide Children");
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing showChildrenCategories()");
            }
        });
    });
}

function hideChildrenCategories(id) {
    require(["SHARED/jquery"], function($) {
        var thisId= "#" + id;
        var linkParentId = "#" + $(thisId).attr("data-parent-link-id");
        $(linkParentId).attr("href", $(thisId).attr("data-parent-href"));
        $(linkParentId).text($(thisId).attr("data-parent-link"));
        $(thisId).remove();
    });
}

function deleteCategory(namespace, catid) {
    require(["SHARED/jquery"], function($) {
        var formId = "#" + namespace + "deleteCategoryForm";
        var inputCatId = "#" + namespace + "deleteCategoryId";

        if (confirm("Confirm delete Category ID [" + catid + "] ?")) {
            $("#" + namespace + "deleteCategoryId").val(catid);
            $("#" + namespace + "deleteCategoryForm").submit();
        }
    });
}

function showSingleAclCategory(namespace, link, href, categoryId, hrefClose) {
    require(["SHARED/jquery"], function($) {
        // Show popup
        var id = "#" + namespace + "categories-acls";
        var linkid = "#" + link; // namespace included
        var aclId = "#" + namespace + "categories-acls-attached";
        var aclCategoryId = "#" + namespace + "aclCategoryId";
        $(id).css('top',  $(linkid).offset().top - $(document).scrollTop());
        $(id).css('left', $(linkid).offset().left);
        $(id).fadeIn(100);

        $(aclCategoryId).val( categoryId );

        $.ajax({
            type: "POST",
            url: href + "&categoryid=" + categoryId + "&namespace=" + namespace,
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write ACL data
                $(aclId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing showSingleAclCategory()");
            }
        });

        var closeid = "#" + namespace + "close-categories-acls";
        $(closeid).click(function (e) {
            e.preventDefault();
            $(id).hide();
            window.location.assign(hrefClose);
        });
    });
}

function addAcl(namespace, href) {
    var aclTypeId = "#" + namespace + "aclType";
    var aclWcmGroupId = "#" + namespace + "aclWcmGroup";
    var aclCategoryId = "#" + namespace + "aclCategoryId";
    var aclId = "#" + namespace + "categories-acls-attached";

    require(["SHARED/jquery"], function($) {

        $.ajax({
            type: "POST",
            url: href + "&aclcategoryid=" + $(aclCategoryId).val() + "&namespace=" + namespace + "&acltype=" + $(aclTypeId).val() + "&aclwcmgroup=" + $(aclWcmGroupId).val(),
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write ACL data
                $(aclId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing addAcl()");
            }
        });

    });
}

function deleteAcl(namespace, href, aclAclId, aclCategoryId) {
    var aclId = "#" + namespace + "categories-acls-attached";

    require(["SHARED/jquery"], function($) {

        $.ajax({
            type: "POST",
            url: href + "&aclid=" + aclAclId + "&namespace=" + namespace + "&aclcategoryid=" + aclCategoryId,
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write ACL data
                $(aclId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing deleteAcl()");
            }
        });

    });
}
