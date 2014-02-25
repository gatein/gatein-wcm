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

package org.gatein.wcm.api;

import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.gatein.wcm.api.services.WcmApiService;

/**
 * Helper class to retrieve WcmApiService interface.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class WcmApi {

    private static final Logger log = Logger.getLogger(WcmApi.class.getName());
    private static final String WCM_API_JNDI_NAME = "java:global/wcm/WcmApiServiceBean!org.gatein.wcm.api.services.WcmApiService";

    /**
     * @return Instance of proxy to WcmApiService
     * @throws Exception
     */
    public static WcmApiService getInstance() throws Exception {
        WcmApiService instance = null;
        try {
            Context ctx = new InitialContext();
            instance = (WcmApiService)ctx.lookup(WCM_API_JNDI_NAME);
            return instance;
        } catch (Exception e) {
            log.warning(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
