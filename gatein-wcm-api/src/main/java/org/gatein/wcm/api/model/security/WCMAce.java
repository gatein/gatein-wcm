/*
 * JBoss, a division of Red Hat
 * Copyright 2013, Red Hat Middleware, LLC, and individual
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

package org.gatein.wcm.api.model.security;

/**
 *
 * Access Control Entry for a Content.
 * <p>
 * Defines permission type associated to a Principal. <br>
 * Principal can be an USER or a ROLE. <br>
 * <p>
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WCMAce {

    /**
     *
     * @return This method returns principal associated to this permission.
     */
    WCMPrincipal getPrincipal();

    /**
     *
     * @return This method returns permission associated to this principal.
     */
    WCMPermissionType getPermission();
}
