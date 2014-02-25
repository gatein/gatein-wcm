/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
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

package org.gatein.wcm.portlet.editor.views;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.WcmLockException;
import org.gatein.wcm.domain.Category;
import org.gatein.wcm.domain.Relationship;
import org.gatein.wcm.domain.Template;
import org.gatein.wcm.domain.UserWcm;
import org.gatein.wcm.portlet.util.ViewMetadata;
import org.gatein.wcm.services.PortalService;
import org.gatein.wcm.services.WcmService;

import javax.inject.Inject;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Actions for Templates area of EditorPortlet
 *
 * @see Wcm.VIEWS
 * @see Wcm.ACTIONS
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class TemplatesActions {
    private static final Logger log = Logger.getLogger(TemplatesActions.class.getName());

    @Inject
    private WcmService wcm;

    @Inject
    private PortalService portal;

    public String actionNewTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String templateName = request.getParameter("templateName");
        String templateLocale = request.getParameter("templateLocale");
        String templateContent = request.getParameter("templateContent");
        try {
            Template newTemplate = new Template();
            newTemplate.setUser(userWcm.getUsername());
            newTemplate.setName(templateName);
            newTemplate.setLocale(templateLocale);
            newTemplate.setContent(templateContent);
            wcm.create(newTemplate, userWcm);
            return Wcm.VIEWS.TEMPLATES;
        } catch(Exception e) {
            log.warning("Error saving template");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error saving template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionRightTemplates(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) viewMetadata.rightPage();
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionLeftTemplates(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) viewMetadata.leftPage();
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionEditTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String templateEditId = request.getParameter("templateEditId");
        String templateName = request.getParameter("templateName");
        String templateLocale = request.getParameter("templateLocale");
        String templateContent = request.getParameter("templateContent");
        try {
            Template updateTemplate = wcm.findTemplate(new Long(templateEditId), userWcm);
            updateTemplate.setName(templateName);
            updateTemplate.setLocale(templateLocale);
            updateTemplate.setContent(templateContent);
            wcm.update(updateTemplate, userWcm);
            wcm.unlock(new Long(templateEditId), Wcm.LOCK.POST, userWcm);
            return Wcm.VIEWS.TEMPLATES;
        } catch(Exception e) {
            log.warning("Error uploading template");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error uploading template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionChangeVersionTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String templateVersionId = request.getParameter("templateVersionId");
        String templateVersion = request.getParameter("templateVersion");
        try {
            wcm.changeVersionTemplate(new Long(templateVersionId), new Long(templateVersion), userWcm);
            response.setRenderParameter("editid", templateVersionId);
            return Wcm.VIEWS.EDIT_TEMPLATE;
        } catch (Exception e) {
            log.warning("Error changing template version");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error changing template version " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionAddCategoryTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String categoryId = request.getParameter("categoryId");
        String templateId = request.getParameter("templateId");
        try {
            Category cat = wcm.findCategory(new Long(categoryId), userWcm);
            Template template = wcm.findTemplate(new Long(templateId), userWcm);
            wcm.add(template, cat, userWcm);
            return Wcm.VIEWS.TEMPLATES;
        } catch(Exception e) {
            log.warning("Error adding category to template.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionFilterCategoryTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String filterCategoryId = request.getParameter("filterCategoryId");
        Long filterId = new Long(filterCategoryId);
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) {
            // Reset viewMetadata if it comes from a different view
            if (viewMetadata.getViewType() != ViewMetadata.ViewType.TEMPLATES) {
                viewMetadata.setViewType(ViewMetadata.ViewType.TEMPLATES);
                viewMetadata.setFilterCategory(false);
            }
            if (!viewMetadata.isFilterCategory()) {
                viewMetadata.setCategoryId(filterId);
                viewMetadata.setFromIndex(0); // First search, reset pagination
                viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                viewMetadata.setFilterCategory(true);
                viewMetadata.setFilterName(false);
            } else {
                if (!viewMetadata.getCategoryId().equals(filterId)) {
                    if (filterId == -1) {
                        viewMetadata.setFilterCategory(false);
                    } else {
                        viewMetadata.setCategoryId(filterId);
                    }
                    viewMetadata.setFromIndex(0); // First search, reset pagination
                    viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                }
            }
        }
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionFilterNameTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String filterName = request.getParameter("filterName");
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) {
            // Reset viewMetadata if it comes from a different view
            if (viewMetadata.getViewType() != ViewMetadata.ViewType.TEMPLATES) {
                viewMetadata.setViewType(ViewMetadata.ViewType.TEMPLATES);
                viewMetadata.setFilterName(false);
            }
            if (!viewMetadata.isFilterName()) {
                viewMetadata.setName(filterName);
                viewMetadata.setFromIndex(0); // First search, reset pagination
                viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                viewMetadata.setFilterName(true);
                viewMetadata.setFilterCategory(false);
            } else {
                if (!viewMetadata.getName().equals(filterName)) {
                    if (filterName.equals("")) {
                        viewMetadata.setFilterName(false);
                    } else {
                        viewMetadata.setName(filterName);
                    }
                    viewMetadata.setFromIndex(0); // First search, reset pagination
                    viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                }
            }
        }
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionDeleteTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String deleteTemplateId = request.getParameter("deleteTemplateId");
        Long templateId = new Long(deleteTemplateId);
        try {
            wcm.lock(new Long(templateId), Wcm.LOCK.TEMPLATE, userWcm);
            wcm.deleteTemplate(templateId, userWcm);
            wcm.unlock(new Long(templateId), Wcm.LOCK.TEMPLATE, userWcm);
            return Wcm.VIEWS.TEMPLATES;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error deleting template.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error deleting template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionDeleteSelectedTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String deleteSelectedListId = request.getParameter("deleteSelectedListId");
        try {
            String[] templatesIds = deleteSelectedListId.split(",");
            for (String templateId : templatesIds) {
                wcm.lock(new Long(templateId), Wcm.LOCK.TEMPLATE, userWcm);
                wcm.deleteTemplate(new Long(templateId), userWcm);
                wcm.unlock(new Long(templateId), Wcm.LOCK.TEMPLATE, userWcm);
            }
            return Wcm.VIEWS.TEMPLATES;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error deleting template.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error deleting template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionAddSelectedCategoryTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String addSelectedCategoryTemplateListId = request.getParameter("addSelectedCategoryTemplateListId");
        String addCategoryTemplateId = request.getParameter("addCategoryTemplateId");
        try {
            Category cat = wcm.findCategory(new Long(addCategoryTemplateId), userWcm);
            String[] templatesIds = addSelectedCategoryTemplateListId.split(",");
            for (String templateId : templatesIds) {
                Template template = wcm.findTemplate(new Long(templateId), userWcm);
                wcm.add(template, cat, userWcm);
            }
            return Wcm.VIEWS.TEMPLATES;
        } catch(Exception e) {
            log.warning("Error adding category to template.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionRemoveCategoryTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String catId = request.getParameter("catid");
        String templateId = request.getParameter("templateid");
        try {
            wcm.removeTemplateCategory(new Long(templateId), new Long(catId), userWcm);
            return Wcm.VIEWS.TEMPLATES;
        } catch(Exception e) {
            log.warning("Error adding category to template.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public String actionLockTemplate(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            Template template = wcm.findTemplate(new Long(editId), userWcm);
            if (userWcm.isManager()) {
                wcm.lock(new Long(editId), Wcm.LOCK.TEMPLATE, userWcm);
            }
            return Wcm.VIEWS.EDIT_TEMPLATE;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        }  catch (Exception e) {
            log.warning("Error locking template.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error locking template " + e.toString());
        }
        return Wcm.VIEWS.TEMPLATES;
    }

    public void viewTemplates(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        // Check view metadata
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        try {
            // Categories
            List<Category> categories = wcm.findCategories(userWcm);
            request.setAttribute("categories", categories);

            // Wcm groups
            Set<String> wcmGroups = portal.getWcmGroups();
            request.setAttribute("wcmGroups", wcmGroups);

            // New default view
            if (viewMetadata == null || viewMetadata.getViewType() != ViewMetadata.ViewType.TEMPLATES) {
                List<Template> allTemplates = wcm.findTemplates(userWcm);
                if (allTemplates != null) {
                    viewMetadata = new ViewMetadata();
                    viewMetadata.setViewType(ViewMetadata.ViewType.TEMPLATES);
                    viewMetadata.setTotalIndex(allTemplates.size());
                    viewMetadata.resetPagination();
                    if (viewMetadata.getTotalIndex() > 0) {
                        List<Template> viewList = allTemplates.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                        request.setAttribute("list", viewList);
                        request.getPortletSession().setAttribute("metadata", viewMetadata);
                    } else {
                        request.setAttribute("list", null);
                        request.getPortletSession().setAttribute("metadata", viewMetadata);
                    }
                }
            } else {
                // Filter per category
                if (viewMetadata.isFilterCategory()) {
                    List<Template> filterTemplates = wcm.findTemplates(viewMetadata.getCategoryId(), userWcm);
                    if (filterTemplates != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.TEMPLATES);
                        viewMetadata.setTotalIndex(filterTemplates.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Template> viewList = filterTemplates.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                } else if (viewMetadata.isFilterName()) {
                    List<Template> filterTemplates = wcm.findTemplates(viewMetadata.getName(), userWcm);
                    if (filterTemplates != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.TEMPLATES);
                        viewMetadata.setTotalIndex(filterTemplates.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Template> viewList = filterTemplates.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                } else {
                    List<Template> allTemplates = wcm.findTemplates(userWcm);
                    if (allTemplates != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.TEMPLATES);
                        viewMetadata.setTotalIndex(allTemplates.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Template> viewList = allTemplates.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                }
            }
        } catch(WcmException e) {
            log.warning("Error accessing templates.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing templates: " + e.toString());
        }
    }

    public void viewEditTemplate(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            // Categories
            List<Category> categories = wcm.findCategories(userWcm);
            request.setAttribute("categories", categories);

            Template template = wcm.findTemplate(new Long(editId), userWcm);
            request.setAttribute("edit", template);

            // Template's versions
            List<Long> versions = wcm.versionsTemplate(new Long(editId), userWcm);
            request.setAttribute("versions", versions);
        } catch (WcmException e) {
            log.warning("Error accessing templates.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing templates: " + e.toString());
        }
    }

    public void viewNewTemplate(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        try {
            // Categories
            List<Category> categories = wcm.findCategories(userWcm);
            request.setAttribute("categories", categories);
        } catch (WcmException e) {
            log.warning("Error accessing templates.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing templates: " + e.toString());
        }
    }

    public String eventShowTemplateRelationships(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String templateId = request.getParameter("templateid");
        String filterCategoryId = request.getParameter("filterCategoryId");
        String filterName = request.getParameter("filterName");
        try {
            // List of select templates
            List<Template> templates = null;
            if (filterCategoryId != null && !"-1".equals(filterCategoryId)) {
                templates = wcm.findTemplates(new Long(filterCategoryId), userWcm);
            } else if (filterName != null && !"".equals(filterName)) {
                templates = wcm.findTemplates(filterName, userWcm);
            } else {
                templates = wcm.findTemplates(userWcm);
            }
            request.setAttribute("templates", templates);

            // List of relationships
            List<Relationship> relations = wcm.findRelationshipsTemplate(new Long(templateId), userWcm);
            request.setAttribute("relations", relations);

            List<Template> templatesRelations = wcm.findTemplatesRelationshipTemplate(new Long(templateId), userWcm);
            request.setAttribute("templatesRelations", templatesRelations);

            Template template = wcm.findTemplate(new Long(templateId), userWcm);
            request.setAttribute("template", template);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Template's Relationships");
            e.printStackTrace();
        }
        return "/jsp/templates/templatesRelationships.jsp";
    }

    public String eventAddTemplateRelationship(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String templateId = request.getParameter("templateid");
        String filterCategoryId = request.getParameter("filterCategoryId");
        String filterName = request.getParameter("filterName");
        String key = request.getParameter("key");
        String targetId = request.getParameter("targetid");
        try {
            // List of select templates
            List<Template> templates = null;
            if (filterCategoryId != null && !"-1".equals(filterCategoryId)) {
                templates = wcm.findTemplates(new Long(filterCategoryId), userWcm);
            } else if (filterName != null && !"".equals(filterName) && !"Filter By Name".equals(filterName)) {
                templates = wcm.findTemplates(filterName, userWcm);
            } else {
                templates = wcm.findTemplates(userWcm);
            }
            request.setAttribute("templates", templates);

            // Add relationShip
            wcm.createRelationshipTemplate(new Long(templateId), key, new Long(targetId), userWcm);

            // List of relationships
            List<Relationship> relations = wcm.findRelationshipsTemplate(new Long(templateId), userWcm);
            request.setAttribute("relations", relations);

            List<Template> templatesRelations = wcm.findTemplatesRelationshipTemplate(new Long(templateId), userWcm);
            request.setAttribute("templatesRelations", templatesRelations);

            Template template = wcm.findTemplate(new Long(templateId), userWcm);
            request.setAttribute("template", template);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Template's Relationships");
            e.printStackTrace();
        }
        return "/jsp/templates/templatesRelationships.jsp";
    }

    public String eventRemoveTemplateRelationship(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String templateId = request.getParameter("templateid");
        String filterCategoryId = request.getParameter("filterCategoryId");
        String filterName = request.getParameter("filterName");
        String key = request.getParameter("key");
        String targetId = request.getParameter("targetid");
        try {
            // List of select templates
            List<Template> templates = null;
            if (filterCategoryId != null && !"-1".equals(filterCategoryId)) {
                templates = wcm.findTemplates(new Long(filterCategoryId), userWcm);
            } else if (filterName != null && !"".equals(filterName) && !"Filter By Name".equals(filterName)) {
                templates = wcm.findTemplates(filterName, userWcm);
            } else {
                templates = wcm.findTemplates(userWcm);
            }
            request.setAttribute("templates", templates);

            // Add relationShip
            wcm.removeRelationshipTemplate(new Long(templateId), key, userWcm);

            // List of relationships
            List<Relationship> relations = wcm.findRelationshipsTemplate(new Long(templateId), userWcm);
            request.setAttribute("relations", relations);

            List<Template> templatesRelations = wcm.findTemplatesRelationshipTemplate(new Long(templateId), userWcm);
            request.setAttribute("templatesRelations", templatesRelations);

            Template template = wcm.findTemplate(new Long(templateId), userWcm);
            request.setAttribute("template", template);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Template's Relationships");
            e.printStackTrace();
        }
        return "/jsp/templates/templatesRelationships.jsp";
    }

    public void eventUnlockTemplate(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String templateId = request.getParameter("templateid");
        try {
            wcm.unlock(new Long(templateId), Wcm.LOCK.TEMPLATE, userWcm);
        } catch (WcmLockException e) {
            log.warning("Error unlocking Template. This case can be caused by concurrent hazard");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error unlocking Template");
            e.printStackTrace();
        }
    }

}
