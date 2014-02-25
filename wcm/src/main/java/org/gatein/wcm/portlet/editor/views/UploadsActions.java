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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.WcmLockException;
import org.gatein.wcm.domain.*;
import org.gatein.wcm.portlet.util.ViewMetadata;
import org.gatein.wcm.services.PortalService;
import org.gatein.wcm.services.WcmService;

import javax.inject.Inject;
import javax.portlet.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Actions for Uploads area of EditorPortlet
 *
 * @see Wcm.VIEWS
 * @see Wcm.ACTIONS
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class UploadsActions {
    private static final Logger log = Logger.getLogger(UploadsActions.class.getName());

    @Inject
    private WcmService wcm;

    @Inject
    private PortalService portal;

    public String actionNewUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String tmpDir = System.getProperty(Wcm.UPLOADS.TMP_DIR);
        FileItemFactory factory = new DiskFileItemFactory(Wcm.UPLOADS.MAX_FILE_SIZE, new File(tmpDir));
        PortletFileUpload upload = new PortletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(request);
            FileItem file = null;
            String description = "";
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    file = item;
                } else {
                    description = item.getString();
                }
            }
            Upload newUpload = new Upload();
            newUpload.setFileName(file.getName());
            newUpload.setMimeType(file.getContentType());
            newUpload.setDescription(description);
            wcm.create(newUpload, file.getInputStream(), userWcm);
            return Wcm.VIEWS.UPLOADS;
        } catch(Exception e) {
            log.warning("Error uploading file");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error uploading file " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionRightUploads(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) viewMetadata.rightPage();
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionLeftUploads(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) viewMetadata.leftPage();
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionEditUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String tmpDir = System.getProperty(Wcm.UPLOADS.TMP_DIR);
        FileItemFactory factory = new DiskFileItemFactory(Wcm.UPLOADS.MAX_FILE_SIZE, new File(tmpDir));
        PortletFileUpload upload = new PortletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(request);
            FileItem file = null;
            String description = null;
            String editUploadId = null;
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    file = item;
                } else {
                    if (item.getFieldName().equals("uploadFileDescription")) {
                        description = item.getString();
                    } else if (item.getFieldName().equals("editUploadId")) {
                        editUploadId = item.getString();
                    }
                }
            }
            Upload updateUpload = wcm.findUpload(new Long(editUploadId), userWcm);
            if (updateUpload != null) {
                if (file != null && file.getSize() > 0 && !file.getName().equals("")) {
                    updateUpload.setFileName(file.getName());
                    updateUpload.setMimeType(file.getContentType());
                    updateUpload.setDescription(description);
                    wcm.unlock(new Long(editUploadId), Wcm.LOCK.UPLOAD, userWcm);
                    wcm.update(updateUpload, file.getInputStream(), userWcm);
                } else {
                    if (description != null) {
                        updateUpload.setDescription(description);
                        wcm.unlock(new Long(editUploadId), Wcm.LOCK.UPLOAD, userWcm);
                        wcm.update(updateUpload, userWcm);
                    }
                }
            }
            return Wcm.VIEWS.UPLOADS;
        } catch(Exception e) {
            log.warning("Error uploading file");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error uploading file " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionChangeVersionUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String uploadVersionId = request.getParameter("uploadVersionId");
        String uploadVersion = request.getParameter("uploadVersion");
        try {
            wcm.changeVersionUpload(new Long(uploadVersionId), new Long(uploadVersion), userWcm);
            response.setRenderParameter("editid", uploadVersionId);
            return Wcm.VIEWS.EDIT_UPLOAD;
        } catch (Exception e) {
            log.warning("Error changing upload version");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error changing upload version " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionAddCategoryUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String categoryId = request.getParameter("categoryId");
        String uploadId = request.getParameter("uploadId");
        try {
            Category cat = wcm.findCategory(new Long(categoryId), userWcm);
            Upload upload = wcm.findUpload(new Long(uploadId), userWcm);
            wcm.add(upload, cat, userWcm);
            return Wcm.VIEWS.UPLOADS;
        } catch(Exception e) {
            log.warning("Error adding category to upload.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to upload " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionFilterCategoryUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String filterCategoryId = request.getParameter("filterCategoryId");
        Long filterId = new Long(filterCategoryId);
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) {
            // Reset viewMetadata if it comes from a different view
            if (viewMetadata.getViewType() != ViewMetadata.ViewType.UPLOADS) {
                viewMetadata.setViewType(ViewMetadata.ViewType.UPLOADS);
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
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionFilterNameUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String filterName = request.getParameter("filterName");
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) {
            // Reset viewMetadata if it comes from a different view
            if (viewMetadata.getViewType() != ViewMetadata.ViewType.UPLOADS) {
                viewMetadata.setViewType(ViewMetadata.ViewType.UPLOADS);
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
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionDeleteUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String deleteUploadId = request.getParameter("deleteUploadId");
        Long uploadId = new Long(deleteUploadId);
        try {
            wcm.lock(new Long(uploadId), Wcm.LOCK.UPLOAD, userWcm);
            wcm.deleteUpload(uploadId, userWcm);
            wcm.unlock(new Long(uploadId), Wcm.LOCK.UPLOAD, userWcm);
            return Wcm.VIEWS.UPLOADS;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error deleting upload.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error deleting upload " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionDeleteSelectedUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String deleteSelectedListId = request.getParameter("deleteSelectedListId");
        try {
            String[] uploadIds = deleteSelectedListId.split(",");
            for (String uploadId : uploadIds) {
                wcm.lock(new Long(uploadId), Wcm.LOCK.UPLOAD, userWcm);
                wcm.deleteUpload(new Long(uploadId), userWcm);
                wcm.unlock(new Long(uploadId), Wcm.LOCK.UPLOAD, userWcm);
            }
            return Wcm.VIEWS.UPLOADS;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error deleting upload.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error deleting upload " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionAddSelectedCategoryUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String addSelectedCategoryUploadListId = request.getParameter("addSelectedCategoryUploadListId");
        String addCategoryUploadId = request.getParameter("addCategoryUploadId");
        try {
            Category cat = wcm.findCategory(new Long(addCategoryUploadId), userWcm);
            String[] uploadIds = addSelectedCategoryUploadListId.split(",");
            for (String uploadId : uploadIds) {
                Upload upload = wcm.findUpload(new Long(uploadId), userWcm);
                wcm.add(upload, cat, userWcm);
            }
            return Wcm.VIEWS.UPLOADS;
        } catch(Exception e) {
            log.warning("Error adding category to upload.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to upload " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionRemoveCategoryUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String catId = request.getParameter("catid");
        String uploadId = request.getParameter("uploadid");
        try {
            wcm.removeUploadCategory(new Long(uploadId), new Long(catId), userWcm);
            return Wcm.VIEWS.UPLOADS;
        } catch(Exception e) {
            log.warning("Error adding category to upload.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to upload " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public String actionLockUpload(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            Upload upload = wcm.findUpload(new Long(editId), userWcm);
            if (userWcm.canWrite(upload)) {
                wcm.lock(new Long(editId), Wcm.LOCK.UPLOAD, userWcm);
            }
            return Wcm.VIEWS.EDIT_UPLOAD;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        }  catch (Exception e) {
            log.warning("Error locking upload.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error locking upload " + e.toString());
        }
        return Wcm.VIEWS.UPLOADS;
    }

    public void viewUploads(RenderRequest request, RenderResponse response, UserWcm userWcm) {
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
            if (viewMetadata == null || viewMetadata.getViewType() != ViewMetadata.ViewType.UPLOADS) {
                List<Upload> allUploads = wcm.findUploads(userWcm);
                if (allUploads != null) {
                    viewMetadata = new ViewMetadata();
                    viewMetadata.setViewType(ViewMetadata.ViewType.UPLOADS);
                    viewMetadata.setTotalIndex(allUploads.size());
                    viewMetadata.resetPagination();
                    if (viewMetadata.getTotalIndex() > 0) {
                        List<Upload> viewList = allUploads.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
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
                    List<Upload> filterUploads = wcm.findUploads(viewMetadata.getCategoryId(), userWcm);
                    if (filterUploads != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.UPLOADS);
                        viewMetadata.setTotalIndex(filterUploads.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Upload> viewList = filterUploads.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                } else if (viewMetadata.isFilterName()) {
                    List<Upload> filterUploads = wcm.findUploads(viewMetadata.getName(), userWcm);
                    if (filterUploads != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.UPLOADS);
                        viewMetadata.setTotalIndex(filterUploads.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Upload> viewList = filterUploads.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                } else {
                    List<Upload> allUploads = wcm.findUploads(userWcm);
                    if (allUploads != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.UPLOADS);
                        viewMetadata.setTotalIndex(allUploads.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Upload> viewList = allUploads.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
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
            log.warning("Error accessing uploads.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing upoads: " + e.toString());
        }
    }

    public void viewEditUpload(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            Upload upload = wcm.findUpload(new Long(editId), userWcm);
            request.setAttribute("edit", upload);
            // Uploads's versions
            List<Long> versions = wcm.versionsUpload(new Long(editId), userWcm);
            request.setAttribute("versions", versions);
        } catch (WcmException e) {
            log.warning("Error accessing uploads.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing uploads: " + e.toString());
        }
    }

    public void eventDownloadUpload(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String uploadid = request.getParameter("uploadid");
        try {
            Upload upload = wcm.findUpload(new Long(uploadid), userWcm);
            if (upload != null) {
                download(upload, response);
            }
        } catch (WcmException e) {
            log.warning("Error accessing upload.");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error downloading file.");
            e.printStackTrace();
        }
    }

    public void download(Upload upload, ResourceResponse response) throws Exception {
        if (response == null || upload == null) return;

        String dirPath;
        String fullPath;

        if (System.getProperty(Wcm.UPLOADS.FOLDER) == null) {
            dirPath = System.getProperty(Wcm.UPLOADS.DEFAULT) + "/wcm/uploads";
        } else {
            dirPath = System.getProperty(Wcm.UPLOADS.FOLDER);
        }
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdir()) {
            throw new WcmException("Cannot read dir: " + Wcm.UPLOADS.FOLDER);
        }
        fullPath = dirPath + File.separator + upload.getStoredName();

        response.setContentType(upload.getMimeType());
        response.setProperty("Content-Disposition","inline; filename=\"" + upload.getFileName() + "\"");

        BufferedInputStream input = null;
        OutputStream output = response.getPortletOutputStream();

        input = new BufferedInputStream(new FileInputStream(fullPath));

        byte[] buffer = new byte[Wcm.UPLOADS.LENGTH_BUFFER];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            output.write(buffer, 0, length);
        }
        input.close();
        output.flush();
        output.close();
    }

    public String eventShowUploadAcls(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String uploadId = request.getParameter("uploadid");
        try {
            Upload upload = null;
            if (uploadId != null && !"".equals(uploadId) && namespace != null && !"".equals(namespace)) {
                upload = wcm.findUpload(new Long(uploadId), userWcm);
                if (!userWcm.canWrite(upload)) upload = null;
            }
            request.setAttribute("upload", upload);
            request.setAttribute("namespace", namespace);
        } catch(WcmException e) {
            log.warning("Error accesing upload acls.");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error parsing uploadId: " + uploadId);
            e.printStackTrace();
        }
        return "/jsp/uploads/uploadsAcls.jsp";
    }

    public String eventAddAclUpload(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String aclUploadId = request.getParameter("acluploadid");
        String aclType = request.getParameter("acltype");
        String aclWcmGroup = request.getParameter("aclwcmgroup");
        String namespace = request.getParameter("namespace");
        try {
            Upload upload = wcm.findUpload(new Long(aclUploadId), userWcm);
            if (upload != null && aclType != null && aclWcmGroup != null && userWcm.canWrite(upload)) {
                Acl newAcl = new Acl();
                if (aclType.equals(Wcm.ACL.WRITE.toString())) {
                    newAcl.setPermission(Wcm.ACL.WRITE);
                } else {
                    newAcl.setPermission(Wcm.ACL.NONE);
                }
                newAcl.setPrincipal(aclWcmGroup);

                // Check if exists
                boolean found = false;
                for (Acl acl : upload.getAcls()) {
                    if (acl.getPermission().equals(newAcl.getPermission()) &&
                            acl.getPrincipal().equals(newAcl.getPrincipal())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    upload.getAcls().add(newAcl);
                    newAcl.setUpload(upload);
                    wcm.update(upload, userWcm);
                }
            } else {
                upload = null;
            }
            request.setAttribute("upload", upload);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error adding Acl to Upload");
            e.printStackTrace();
        }
        return "/jsp/uploads/uploadsAcls.jsp";
    }

    public String eventRemoveAclUpload(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String aclUploadId = request.getParameter("acluploadid");
        String aclId = request.getParameter("aclid");
        String namespace = request.getParameter("namespace");
        try {
            Upload upload = wcm.findUpload(new Long(aclUploadId), userWcm);
            if (upload != null && aclId != null && userWcm.canWrite(upload)) {
                // Check if exists
                Acl found = null;
                for (Acl acl : upload.getAcls()) {
                    if (acl.getId().toString().equals(aclId)) {
                        found = acl;
                        break;
                    }
                }
                // Rules to remove:
                // - at least 1 WRITE ACL should exists
                if ((found.getPermission() == Wcm.ACL.NONE && upload.getAcls().size() > 1) ||
                        (found.getPermission() == Wcm.ACL.WRITE && countAcl(upload.getAcls(), Wcm.ACL.WRITE) > 1)) {
                    wcm.remove(found, userWcm);
                    upload.getAcls().remove(found);
                    upload = wcm.findUpload(new Long(aclUploadId), userWcm);
                    if (!userWcm.canWrite(upload)) upload = null;
                }
            } else {
                upload = null;
            }
            request.setAttribute("upload", upload);
            request.setAttribute("namespace", namespace);

        } catch (Exception e) {
            log.warning("Error removing Acl to Upload");
            e.printStackTrace();
        }
        return "/jsp/uploads/uploadsAcls.jsp";
    }

    public void eventUnlockUpload(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String uploadId = request.getParameter("uploadid");
        try {
            wcm.unlock(new Long(uploadId), Wcm.LOCK.UPLOAD, userWcm);
        } catch (WcmLockException e) {
            log.warning("Error unlocking Upload. This case can be caused by concurrent hazard");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error unlocking Upload");
            e.printStackTrace();
        }
    }

    private int countAcl(Set <Acl> acl, Character type) {
        if (acl == null) return -1;
        int count = 0;
        for (Acl a : acl) if (a.getPermission() == type) count++;
        return count;
    }

}
