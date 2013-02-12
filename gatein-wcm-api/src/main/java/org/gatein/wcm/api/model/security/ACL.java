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

import java.util.List;

/**
 * 
 * A Content can have several Access Control Entries grouped in an Access Control List . <br>
 * ACL objects linked to a Folder object apply by default to all their children. <br>
 * A children of a Folder can have their own ACL. <br>
 * Priorities of ACL: <li>self content (if defined). <li>parent folder (if defined). <li>default ACL for root folder (if
 * defined). <br>
 * <br>
 * 
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 * 
 */
public interface ACL {

    /**
     * 
     * @return This method return
     */
    public String getId();

    public String getDescription();

    public List<ACE> getAces();

}
