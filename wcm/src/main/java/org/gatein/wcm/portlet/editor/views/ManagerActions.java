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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.io.IOUtils;
import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.domain.Lock;
import org.gatein.wcm.domain.UserWcm;
import org.gatein.wcm.services.PortalService;
import org.gatein.wcm.services.WcmService;

/**
 * Actions for Manager area of EditorPortlet
 *
 * @see org.gatein.wcm.Wcm.VIEWS
 * @see org.gatein.wcm.Wcm.ACTIONS
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ManagerActions {
    private static final Logger log = Logger.getLogger(ManagerActions.class.getName());

    @Inject
    private WcmService wcm;

    @Inject
    private PortalService portal;

    public String eventShowLocks(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        try {
            // List of locks
            List<Lock> locks = wcm.findLocks(userWcm);
            Map<Long, Object> locksObjects = wcm.findLocksObjects(locks, userWcm);
            request.setAttribute("locks", locks);
            request.setAttribute("locksObjects", locksObjects);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error locks");
            e.printStackTrace();
        }
        return "/jsp/manager/managerLocks.jsp";
    }

    public String eventRemoveLock(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String lockid = request.getParameter("lockid");
        String locktype = request.getParameter("locktype");
        try {
            // Remove lock
            wcm.removeLock(new Long(lockid), new Character(locktype.charAt(0)), userWcm);
            // List of locks
            List<Lock> locks = wcm.findLocks(userWcm);
            Map<Long, Object> locksObjects = wcm.findLocksObjects(locks, userWcm);
            request.setAttribute("locks", locks);
            request.setAttribute("locksObjects", locksObjects);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error locks");
            e.printStackTrace();
        }
        return "/jsp/manager/managerLocks.jsp";
    }

    public String eventExport(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        FileInputStream in = null;
        OutputStream out = null;
        try {
            log.info("WCM Export: STARTED");

            String zipName = wcm.exportRepository(userWcm);
            File zip = new File(zipName);

            response.setContentType("application/zip");
            response.setProperty("Content-disposition", "attachment; filename=" + zip.getName());

            out = response.getPortletOutputStream();
            if (zip.exists()) {
                in = new FileInputStream(zip);
                IOUtils.copy(in, out);
            } else {
                throw new Exception("File name: " + zipName + " doesn't exist");
            }

            out.flush();
            out.close();
            in.close();

            log.info("WCM Export: FINISHED");
        } catch (Exception e) {
            log.warning("Error generating export file. " + e.getMessage());
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        // Return null as this specific event will generate a .zip response
        return null;
    }

    public String actionNewImport(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        log.info("WCM Import: STARTED");

        String tmpDir = System.getProperty(Wcm.UPLOADS.TMP_DIR);
        FileItemFactory factory = new DiskFileItemFactory(Wcm.UPLOADS.MAX_FILE_SIZE, new File(tmpDir));
        PortletFileUpload importUpload = new PortletFileUpload(factory);
        try {
            List<FileItem> items = importUpload.parseRequest(request);
            FileItem file = null;
            String importStrategy = String.valueOf(Wcm.IMPORT.STRATEGY.NEW);
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    file = item;
                } else {
                    importStrategy = item.getString();
                }
            }
            // .zip validation
            if (file != null && file.getContentType() != null && !"application/zip".equalsIgnoreCase(file.getContentType())) {
                throw new WcmException("File: " + file.getName() + " is not application/zip.");
            }
            //
            log.info("Importing ... " + file.getName() + " with strategy " + importStrategy);
            wcm.importRepository(file.getInputStream(), importStrategy.charAt(0), userWcm);
        } catch(Exception e) {
            log.warning("Error importing file");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error importing file: " + e.toString());
        }

        log.info("WCM Import: FINISHED");

        return Wcm.VIEWS.MANAGER;
    }
}
