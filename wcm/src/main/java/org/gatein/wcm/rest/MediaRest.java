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

package org.gatein.wcm.rest;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.domain.Upload;
import org.gatein.wcm.domain.UserWcm;
import org.gatein.wcm.services.WcmService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.File;
import java.util.logging.Logger;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

/**
 *   REST point to serve uploaded files
 *
 *   @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Path("/u")
public class MediaRest {
    private static final Logger log = Logger.getLogger(MediaRest.class.getName());

    @Inject
    private WcmService wcm;

    /**
     * @param id Upload's id
     * @return Upload
     * @see Wcm.UPLOADS
     */
    @GET
    @Path("/{id:\\d*}")
    @Produces("*/*")
    public Response getUpload(@PathParam("id") Long id) {
        UserWcm userWcm = new UserWcm("anonymous");
        File output = null;
        try {
            Upload upload = wcm.findUpload(new Long(id), userWcm);
            if (upload != null) {
                String dirPath = null;
                String fullPath = null;
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
                output = new File(fullPath);

                CacheControl cc = new CacheControl();
                cc.setMaxAge(Wcm.UPLOADS.CACHE.MAX_AGE);
                cc.setPrivate(false);

                return Response.ok(output, upload.getMimeType()).cacheControl(cc).build();
            } else {
                log.warning("Upload ID: " + id + " not found.");
            }
        } catch (WcmException e) {
            log.warning("Error accessing upload.");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error downloading file.");
            e.printStackTrace();
        }
        return Response.noContent().build();
    }

}
