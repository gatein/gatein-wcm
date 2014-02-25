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
import org.gatein.wcm.domain.*;
import org.gatein.wcm.services.PortalService;
import org.gatein.wcm.services.WcmService;

import javax.inject.Inject;
import javax.portlet.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Actions for Categories area of EditorPortlet
 *
 * @see Wcm.VIEWS
 * @see Wcm.ACTIONS
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class CategoriesActions {
    private static final Logger log = Logger.getLogger(CategoriesActions.class.getName());

    @Inject
    private WcmService wcm;

    @Inject
    private PortalService portal;

    public String actionNewCategory(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        // Get parameters
        String newCategoryName = request.getParameter("newCategoryName");
        String newCategoryType = request.getParameter("newCategoryType");
        String newCategoryParent = request.getParameter("newCategoryParent");

        Category newCategory = new Category(newCategoryName);
        if (newCategoryType.equals("Category")) {
            newCategory.setType(Wcm.CATEGORIES.CATEGORY);
        }
        if (newCategoryType.equals("Tag")) {
            newCategory.setType(Wcm.CATEGORIES.TAG);
        }
        if (newCategoryType.equals("Folder")) {
            newCategory.setType(Wcm.CATEGORIES.FOLDER);
        }
        if (!newCategoryParent.equals("-1")) {
            long parentId = -1;
            try {
                parentId = new Long(newCategoryParent).longValue();
            } catch (Exception ignored) { }
            if (parentId>-1) {
                try {
                    Category parentCategory = wcm.findCategory(parentId, userWcm);
                    newCategory.setParent(parentCategory);
                } catch (WcmException e) {
                    log.warning("Error getting parentCategory.");
                    e.printStackTrace();
                    response.setRenderParameter("errorWcm", "Error getting parentCategory " + e.toString());
                }
            }
        }
        try {
            wcm.create(newCategory, userWcm);
            return Wcm.VIEWS.CATEGORIES;
        } catch (Exception e) {
            log.warning("Error creating new category");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error creating new category " + e.toString());
        }
        return Wcm.VIEWS.CATEGORIES;
    }

    public String actionDeleteCategory(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String catId = request.getParameter("deletedCategoryId");
        try {
            wcm.lock(new Long(catId), Wcm.LOCK.CATEGORY, userWcm);
            wcm.deleteCategory(new Long(catId), userWcm);
            wcm.unlock(new Long(catId), Wcm.LOCK.CATEGORY, userWcm);
            return Wcm.VIEWS.CATEGORIES;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch (Exception e) {
            log.warning("Error deleting category");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error deleting category " + e.toString());
        }
        return Wcm.VIEWS.CATEGORIES;
    }

    public String actionEditCategory(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String catId = request.getParameter("editCategoryId");
        String newCategoryName = request.getParameter("newCategoryName");
        String newCategoryType = request.getParameter("newCategoryType");
        String newCategoryParent = request.getParameter("newCategoryParent");

        Category updateCategory = null;
        try {
            updateCategory = wcm.findCategory(new Long(catId), userWcm);
            updateCategory.setName(newCategoryName);
            if (newCategoryType.equals("Category")) {
                updateCategory.setType(Wcm.CATEGORIES.CATEGORY);
            }
            if (newCategoryType.equals("Tag")) {
                updateCategory.setType(Wcm.CATEGORIES.TAG);
            }
            if (newCategoryType.equals("Folder")) {
                updateCategory.setType(Wcm.CATEGORIES.FOLDER);
            }
            if (!newCategoryParent.equals("-1")) {
                long parentId = -1;
                try {
                    parentId = new Long(newCategoryParent).longValue();
                } catch (Exception ignored) { }
                if (parentId>-1 && parentId != new Long(catId)) {
                    try {
                        Category parentCategory = wcm.findCategory(parentId, userWcm);
                        updateCategory.setParent(parentCategory);
                    } catch (WcmException e) {
                        log.warning("Error getting parentCategory.");
                        e.printStackTrace();
                        response.setRenderParameter("errorWcm", "Error getting parentCategory " + e.toString());
                    }
                }
            } else {
                updateCategory.setParent(null);
            }
            wcm.update(updateCategory, userWcm);
            wcm.unlock(new Long(catId), Wcm.LOCK.CATEGORY, userWcm);
            return Wcm.VIEWS.CATEGORIES;
        } catch (Exception e) {
            log.warning("Error updating category");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error updating category " + e.toString());
        }
        return Wcm.VIEWS.CATEGORIES;
    }

    public String actionLockCategory(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            Category cat = wcm.findCategory(new Long(editId), userWcm);
            if (userWcm.canWrite(cat)) {
                wcm.lock(new Long(editId), Wcm.LOCK.CATEGORY, userWcm);
            }
            return Wcm.VIEWS.EDIT_CATEGORY;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        }  catch (Exception e) {
            log.warning("Error locking category.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error locking category " + e.toString());
        }
        return Wcm.VIEWS.CATEGORIES;
    }

    public void viewCategories(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        List<Category> categories = null;
        try {
            categories = wcm.findRootCategories(userWcm);
            // "list" variable will be global and it will be used to store all list from action phase to render phase
            // this way it can survive several invocation of third party portlets
            request.setAttribute("list", categories);

            // Wcm groups
            Set<String> wcmGroups = portal.getWcmGroups();
            request.setAttribute("wcmGroups", wcmGroups);
        } catch (WcmException e) {
            log.warning("Error accessing categories.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing categories: " + e.toString());
        }
    }

    public void viewNewCategory(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        List<Category> categories = null;
        try {
            categories = wcm.findCategories(Wcm.CATEGORIES.FOLDER, userWcm);
        } catch (WcmException e) {
            log.warning("Error accessing categories.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing categories: " + e.toString());
        }
        request.setAttribute("categories", categories);
    }

    public void viewEditCategory(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            Category category = wcm.findCategory(new Long(editId), userWcm);
            List<Category> categories = wcm.findCategories(Wcm.CATEGORIES.FOLDER, userWcm);
            request.getPortletSession().setAttribute("edit", category);
            request.getPortletSession().setAttribute("categories", categories);
        } catch (WcmException e) {
            log.warning("Error accessing categories.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing categories: " + e.toString());
        }
    }

    public String eventShowCategoriesChildren(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String parentid = request.getParameter("parentid");
        String namespace = request.getParameter("namespace");
        List<Category> categories = null;
        try {
            categories = wcm.findChildren(new Long(parentid), userWcm);
        } catch (WcmException e) {
            log.warning("Error accessing categories.");
            e.printStackTrace();
        }
        request.setAttribute("categories", categories);
        request.setAttribute("namespace", namespace);
        request.setAttribute("parentid", parentid);
        return "/jsp/categories/categoriesChildren.jsp";
    }

    public String eventShowCategoryAcls(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String categoryId = request.getParameter("categoryid");
        try {
            Category category = null;
            if (categoryId != null && !"".equals(categoryId) && namespace != null && !"".equals(namespace)) {
                category = wcm.findCategory(new Long(categoryId), userWcm);
                if (!userWcm.canWrite(category)) category = null;
            }
            request.setAttribute("category", category);
            request.setAttribute("namespace", namespace);
        } catch(WcmException e) {
            log.warning("Error accesing category acls.");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error parsing categoryId: " + categoryId);
            e.printStackTrace();
        }
        return "/jsp/categories/categoriesAcls.jsp";
    }

    public String eventAddAclCategory(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String aclCategoryId = request.getParameter("aclcategoryid");
        String aclType = request.getParameter("acltype");
        String aclWcmGroup = request.getParameter("aclwcmgroup");
        String namespace = request.getParameter("namespace");
        try {
            Category category = wcm.findCategory(new Long(aclCategoryId), userWcm);
            if (category != null && aclType != null && aclWcmGroup != null && userWcm.canWrite(category)) {
                Acl newAcl = new Acl();
                if (aclType.equals(Wcm.ACL.WRITE.toString())) {
                    newAcl.setPermission(Wcm.ACL.WRITE);
                } else {
                    newAcl.setPermission(Wcm.ACL.NONE);
                }
                newAcl.setPrincipal(aclWcmGroup);
                propagateAddAcl(category,newAcl, userWcm);
            } else {
                category = null;
            }
            request.setAttribute("category", category);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error adding Acl to Category");
            e.printStackTrace();
        }
        return "/jsp/categories/categoriesAcls.jsp";
    }

    private void propagateAddAcl(Category cat, Acl acl, UserWcm user) throws Exception {
        if (cat == null || acl == null || user == null) return;
        Acl clone = new Acl(acl.getPrincipal(), acl.getPermission());
        cat.getAcls().add(clone);
        clone.setCategory(cat);
        wcm.update(cat, user);
        propagateAddAclOnPosts(cat, acl, user);
        propagateAddAclOnUploads(cat, acl, user);
        List<Category> children = wcm.findChildren(cat, user);
        if (children != null && children.size() > 0) {
            for (Category c : children) {
                propagateAddAcl(c, acl, user);
            }
        }
    }

    private void propagateAddAclOnPosts(Category cat, Acl acl, UserWcm user) throws Exception {
        if (cat == null || acl == null || user == null) return;
        List<Post> posts = wcm.findPosts(cat.getId(), user);
        for (Post p : posts) {
            if (user.canWrite(p)) {
                Acl clone = new Acl(acl.getPrincipal(), acl.getPermission());
                if (!p.getAcls().contains(clone)) {
                    clone.setPost(p);
                    p.getAcls().add(clone);
                    wcm.update(p, user);
                }
            }
        }
    }

    private void propagateAddAclOnUploads(Category cat, Acl acl, UserWcm user) throws Exception {
        if (cat == null || acl == null || user == null) return;
        List<Upload> uploads = wcm.findUploads(cat.getId(), user);
        for (Upload u : uploads) {
            if (user.canWrite(u)) {
                Acl clone = new Acl(acl.getPrincipal(), acl.getPermission());
                if (!u.getAcls().contains(clone)) {
                    clone.setUpload(u);
                    u.getAcls().add(clone);
                    wcm.update(u, user);
                }
            }
        }
    }

    public String eventRemoveAclCategory(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String aclCategoryId = request.getParameter("aclcategoryid");
        String aclId = request.getParameter("aclid");
        String namespace = request.getParameter("namespace");
        try {
            Category category = wcm.findCategory(new Long(aclCategoryId), userWcm);
            if (category != null && aclId != null && userWcm.canWrite(category)) {
                // Check if exists
                Acl found = null;
                for (Acl acl : category.getAcls()) {
                    if (acl.getId().toString().equals(aclId)) {
                        found = acl;
                        break;
                    }
                }
                if (found != null) {
                    propagateRemoveAcl(category, found, userWcm);
                    category = wcm.findCategory(new Long(aclCategoryId), userWcm);
                    if (!userWcm.canWrite(category)) category = null;
                }
            } else {
                category = null;
            }
            request.setAttribute("category", category);
            request.setAttribute("namespace", namespace);

        } catch (Exception e) {
            log.warning("Error removing Acl to Category");
            e.printStackTrace();
        }
        return "/jsp/categories/categoriesAcls.jsp";
    }

    public void eventUnlockCategory(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String catId = request.getParameter("catid");
        try {
            wcm.unlock(new Long(catId), Wcm.LOCK.CATEGORY, userWcm);
        } catch (WcmLockException e) {
            log.warning("Error unlocking Category. This case can be caused by concurrent hazard");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error unlocking Category");
            e.printStackTrace();
        }
    }

    /*
        Rules to remove:
        - at least 1 WRITE ACL should exists
     */
    private void propagateRemoveAcl(Category cat, Acl acl, UserWcm user) throws Exception {
        if (cat == null || acl == null || user == null) return;
        // Check if exists
        Acl found = null;
        for (Acl a : cat.getAcls()) {
            if (a.getPermission().equals(acl.getPermission()) && a.getPrincipal().equals(acl.getPrincipal())) {
                found = a;
                break;
            }
        }

        if (found != null &&
                ((acl.getPermission() == Wcm.ACL.NONE && cat.getAcls().size() > 1) ||
                        (acl.getPermission() == Wcm.ACL.WRITE && countAcl(cat.getAcls(), Wcm.ACL.WRITE) > 1)
                )
                ) {
            wcm.remove(found, user);
            cat.getAcls().remove(found);
        }
        propagateRemoveAclOnPosts(cat, acl, user);
        propagateRemoveAclOnUploads(cat, acl, user);
        List<Category> children = wcm.findChildren(cat, user);
        if (children != null && children.size() > 0) {
            for (Category c : children) {
                propagateRemoveAcl(c, acl, user);
            }
        }
    }

    private void propagateRemoveAclOnPosts(Category cat, Acl acl, UserWcm user) throws Exception {
        if (cat == null || acl == null || user == null) return;
        List<Post> posts = wcm.findPosts(cat.getId(), user);
        for (Post p : posts) {
            if (user.canWrite(p)) {
                // Check if exists
                Acl found = null;
                for (Acl a : p.getAcls()) {
                    if (a.getPermission().equals(acl.getPermission()) && a.getPrincipal().equals(acl.getPrincipal())) {
                        found = a;
                        break;
                    }
                }
                // Rules to remove:
                // - at least 1 WRITE ACL should exists
                if ((found.getPermission() == Wcm.ACL.NONE && p.getAcls().size() > 1) ||
                        (found.getPermission() == Wcm.ACL.WRITE && countAcl(p.getAcls(), Wcm.ACL.WRITE) > 1)) {
                    wcm.remove(found, user);
                    p.getAcls().remove(found);
                }
            }
        }
    }

    private void propagateRemoveAclOnUploads(Category cat, Acl acl, UserWcm user) throws Exception {
        if (cat == null || acl == null || user == null) return;
        List<Upload> uploads = wcm.findUploads(cat.getId(), user);
        for (Upload u : uploads) {
            if (user.canWrite(u)) {
                // Check if exists
                Acl found = null;
                for (Acl a : u.getAcls()) {
                    if (a.getPermission().equals(acl.getPermission()) && a.getPrincipal().equals(acl.getPrincipal())) {
                        found = a;
                        break;
                    }
                }
                // Rules to remove:
                // - at least 1 WRITE ACL should exists
                if ((found.getPermission() == Wcm.ACL.NONE && u.getAcls().size() > 1) ||
                        (found.getPermission() == Wcm.ACL.WRITE && countAcl(u.getAcls(), Wcm.ACL.WRITE) > 1)) {
                    wcm.remove(found, user);
                    u.getAcls().remove(found);
                }
            }
        }
    }

    private int countAcl(Set <Acl> acl, Character type) {
        if (acl == null) return -1;
        int count = 0;
        for (Acl a : acl) if (a.getPermission() == type) count++;
        return count;
    }

}
