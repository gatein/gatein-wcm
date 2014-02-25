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

/* Templates Actions functions
 ============================= */

function showSingleCategoriesTemplate(namespace, link, templateId) {
    require(["SHARED/jquery"], function($) {
        // Show popup
        var id = "#" + namespace + "templates-categories";
        var linkid = "#" + link; // namespace included
        $(id).css('top',  $(linkid).offset().top - $(document).scrollTop());
        $(id).css('left', $(linkid).offset().left);
        $(id).fadeIn(100);

        var buttonid = "#" + namespace + "assign-single-category";
        var selectCatId = "#" + namespace + "selectAddCategory";

        var formId  = "#" + namespace + "addCategoryTemplateForm";
        var formCatId = "#" + namespace + "categoryId";
        var formTemplateId = "#" + namespace + "templateId";

        $(buttonid).click(function(e) {
            e.preventDefault();
            $(formTemplateId).val(templateId);
            $(formCatId).val( $(selectCatId).val() );
            $(formId).submit();
        });

        var closeid = "#" + namespace + "close-posts-categories";
        $(closeid).click(function (e) {
            e.preventDefault();
            $(id).hide();
        });
    });
}

function showFilterCategoriesTemplates(namespace) {
    require(["SHARED/jquery"], function($) {
        var formId = "#" + namespace + "filterCategoryTemplatesForm";
        var filterId = "#" + namespace + "filterCategoryId";
        var selectId = "#" + namespace + "selectFilterCategory";

        $(filterId).val( $(selectId).val() );
        $(formId).submit();
    });
}

function showFilterCategoriesById(namespace, id) {
    require(["SHARED/jquery"], function($) {
        var formId = "#" + namespace + "filterCategoryTemplatesForm";
        var filterId = "#" + namespace + "filterCategoryId";

        $(filterId).val( id );
        $(formId).submit();
    });
}


function deleteTemplate(namespace, templateid) {
    require(["SHARED/jquery"], function($) {
        if (confirm("Confirm delete Template ID [" + templateid + "] ?")) {
            $("#" + namespace + "deleteTemplateId").val(templateid);
            $("#" + namespace + "deleteTemplateForm").submit();
        }
    });
}

function selectTemplate(namespace, templateid) {
    require(["SHARED/jquery"], function($) {
        var listTemplateId = "#" + namespace + "listTemplateId";
        var currentList = $(listTemplateId).val();

        // Check if uploadid exists
        var found = false;
        var array = currentList.split(",");
        var output = "";
        for (var i = 0; i<array.length; i++) {
            if (array[i].localeCompare(templateid) == 0) {
                found = true;
            } else {
                output = output + array[i];
                if (output.length > 0) {
                    output = output + ",";
                }
            }
        }
        if (!found) {
            output = output + templateid;
        } else {
            if (output.length > 0) {
                output = output.substr(0, output.length -1);
            }
        }
        $(listTemplateId).val(output);
    });
}

function selectAllTemplates(namespace) {
    require(["SHARED/jquery"], function($) {
        var selectAllId = "#" + namespace + "selectAll";
        var templatesId = "#" + namespace + "templates";
        var listTemplateId = "#" + namespace + "listTemplateId";
        var output = "";

        if ($(selectAllId).is(":checked")) {
            $(templatesId).find("input").each(function() {
                $(this).prop("checked", true);
                output = output + $(this).val() + ",";
            });
        } else {
            $(templatesId).find("input").each(function() {
                $(this).prop("checked", false)
            });
        }

        if (output.length > 0) {
            output = output.substr(0, output.length - 1);
        }
        $(listTemplateId).val(output);
    });
}

function deleteSelectedTemplates(namespace) {
    require(["SHARED/jquery"], function($) {
        var listTemplateId = "#" + namespace + "listTemplateId"
        var selectedId = "#" + namespace + "deleteSelectedListId";
        var deleteSelectedFormId = "#" + namespace + "deleteSelectedTemplateForm";
        if ($(listTemplateId).val().length > 0) {
            if (confirm("Confirm delete Template Selected ID [" + $(listTemplateId).val() + "] ?")) {
                $(selectedId).val( $(listTemplateId).val() );
                $(deleteSelectedFormId).submit();
            }
        }
    });
}

function showSelectedCategoriesTemplates(namespace, link) {
    require(["SHARED/jquery"], function($) {
        var listTemplateId = "#" + namespace + "listTemplateId";
        if ($(listTemplateId).val().length > 0) {
            // Show popup
            var id = "#" + namespace + "templates-categories";
            var linkid = "#" + link; // namespace included
            $(id).css('top',  $(linkid).offset().top);
            $(id).css('left', $(linkid).offset().left);
            $(id).fadeIn(100);

            var buttonid = "#" + namespace + "assign-single-category";
            var selectCatId = "#" + namespace + "selectAddCategory";

            var formId  = "#" + namespace + "addSelectedCategoryTemplateForm";
            var formTemplateId = "#" + namespace + "addSelectedCategoryTemplateListId";
            var formCatId = "#" + namespace + "addCategoryTemplateId";

            $(buttonid).click(function(e) {
                e.preventDefault();
                $(formTemplateId).val($(listTemplateId).val());
                $(formCatId).val( $(selectCatId).val() );
                $(formId).submit();
            });

            var closeid = "#" + namespace + "close-posts-categories";
            $(closeid).click(function (e) {
                e.preventDefault();
                $(id).hide();
            });
        }
    });
}

function showFilterNameTemplates(e, namespace) {
    require(["SHARED/jquery"], function($) {
        if (typeof e == 'undefined' && window.event) { e = window.event; }
        if (e.keyCode == 13) {
            var formId = "#" + namespace + "filterNameTemplatesForm";
            var filterId = "#" + namespace + "filterName";
            var inputId = "#" + namespace + "inputFilterName";

            $(filterId).val( $(inputId).val() );
            $(formId).submit();
        }
    });
}

function showRelationshipsTemplate(namespace, link, href, templateId, hrefClose) {
    require(["SHARED/jquery"], function($) {
        // Show popup
        var id = "#" + namespace + "templates-relationships";
        var linkid = "#" + link; // namespace included
        var relationshipsId = "#" + namespace + "template-relationships-list";
        var selecfilterid = "#" + namespace + "selectFilterCategoryRelationships";
        var inputfilterid = "#" + namespace + "inputFilterNameRelationships";

        // Open comments in the center
        var w = (($(window).width() - 980)/2);
        if (w < 0) w = 100;
        else w = w + 100;
        $(id).css('top',  200 - $(document).scrollTop());    // Fixed at the beginning of
        $(id).css('left', w - $(document).scrollLeft());
        $(id).fadeIn(100);

        $(inputfilterid).val('Filter By Name');
        $(selecfilterid)[0].selectedIndex = 0;

        // Events
        $(selecfilterid).change(function () {
            $.ajax({
                type: "POST",
                url: href + "&templateid=" + templateId + "&namespace=" + namespace + "&filterCategoryId=" + $(selecfilterid).val(),
                cache: false,
                dataType: "text",
                success: function(data)
                {
                    // Write Comment data
                    $(relationshipsId).html(data);
                },
                error: function(XMLHttpRequest, textStatus, errorThrown)
                {
                    alert("Problem accessing showRelationshipsTemplate()");
                }
            });
        });
        $(inputfilterid).keypress(function(event) {
            if (event.which == 13) {
                $.ajax({
                    type: "POST",
                    url: href + "&templateid=" + templateId + "&namespace=" + namespace + "&filterName=" + $(inputfilterid).val(),
                    cache: false,
                    dataType: "text",
                    success: function(data)
                    {
                        // Write Comment data
                        $(relationshipsId).html(data);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown)
                    {
                        alert("Problem accessing showRelationshipsTemplate()");
                    }
                });
            }
        });

        $.ajax({
            type: "POST",
            url: href + "&templateid=" + templateId + "&namespace=" + namespace,
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write Comment data
                $(relationshipsId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing showRelationshipsTemplate()");
            }
        });


        var closeid = "#" + namespace + "close-templates-relationships";
        $(closeid).click(function (e) {
            e.preventDefault();
            $(id).hide();
            window.location.assign(hrefClose);
        });
    });
}

function addRelationshipTemplate(namespace, href, templateid, targetid) {
    require(["SHARED/jquery"], function($) {
        var relationshipsId = "#" + namespace + "template-relationships-list";
        var key = $("#" + namespace + "inputNewKey" + targetid).val();
        var selecfilterid = "#" + namespace + "selectFilterCategoryRelationships";
        var inputfilterid = "#" + namespace + "inputFilterNameRelationships";
        $.ajax({
            type: "POST",
            url: href + "&templateid=" + templateid + "&namespace=" + namespace + "&filterCategoryId=" + $(selecfilterid).val() + "&filterName=" + $(inputfilterid).val(),
            data: {
                key: key,
                targetid: targetid
            },
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write Comment data
                $(relationshipsId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing addRelationshipTemplate()");
            }
        });
    });
}

function removeRelationshipTemplate(namespace, href, templateid, key) {
    require(["SHARED/jquery"], function($) {
        var relationshipsId = "#" + namespace + "template-relationships-list";
        var selecfilterid = "#" + namespace + "selectFilterCategoryRelationships";
        var inputfilterid = "#" + namespace + "inputFilterNameRelationships";
        $.ajax({
            type: "POST",
            url: href + "&templateid=" + templateid + "&namespace=" + namespace + "&filterCategoryId=" + $(selecfilterid).val() + "&filterName=" + $(inputfilterid).val(),
            data: {
                key: key
            },
            cache: false,
            dataType: "text",
            success: function(data)
            {
                // Write Comment data
                $(relationshipsId).html(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown)
            {
                alert("Problem accessing removeRelationshipTemplate()");
            }
        });
    });
}


