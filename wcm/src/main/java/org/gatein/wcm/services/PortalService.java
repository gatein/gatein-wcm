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

package org.gatein.wcm.services;

import org.gatein.wcm.WcmException;
import org.gatein.wcm.domain.UserWcm;

import java.util.Set;

/**
 * Public API for GateIn users services.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public interface PortalService {

    /**
     * @param user logged into GateIn
     * @return a UserWcm associated to user with wcm groups
     * @see org.gatein.wcm.Wcm.GROUPS
     * @throws WcmException
     */
    UserWcm getPortalUser(String user) throws WcmException;

    /**
     * @return groups defined under Wcm.GROUPS
     * @see org.gatein.wcm.Wcm.GROUPS
     * @throws WcmException
     */
    Set<String> getWcmGroups() throws WcmException;
}
