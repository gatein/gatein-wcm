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
package org.gatein.wcm.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.security.WCMAce;
import org.gatein.wcm.api.model.security.WCMAcl;

/**
 * @see {@link WCMAcl}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMAclImpl implements WCMAcl {

    String id;
    String description;
    ArrayList<WCMAce> aces;

    protected WCMAclImpl(String id, String description) {
        this.id = id;
        this.description = description;
        aces = new ArrayList<WCMAce>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<WCMAce> getAces() {
        return aces;
    }

    @Override
    public String toString() {
        return "WCMACL [id=" + id + ", description=" + description + ", aces=" + aces + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aces == null) ? 0 : aces.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WCMAclImpl other = (WCMAclImpl) obj;
        if (aces == null) {
            if (other.aces != null)
                return false;
        } else if (!aces.equals(other.aces))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
