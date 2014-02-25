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

package org.gatein.wcm.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * It represents a Lock of an editable item (Post, Category, Upload or Template) by a user.
 * It's managed by administrator.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Entity
@Table(name = "wcm_locks")
@IdClass(LockPK.class)
@Cacheable
@NamedQueries({
        @NamedQuery(name = "listLocks", query = "select l from Lock l order by l.created")
})
final public class Lock implements Serializable {
	
	private Long originId;
    private Character type;
	private String username;
    private Calendar created;

    @Id
    @Column(name = "lock_origin_id")
    public Long getOriginId() {
        return originId;
    }
    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    @Id
    @Column(name = "lock_type")
    public Character getType() {
        return type;
    }
    public void setType(Character type) {
        this.type = type;
    }

    @Column(name = "lock_username")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "lock_created")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreated() {
        return created;
    }
    public void setCreated(Calendar created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lock lock = (Lock) o;

        if (created != null ? !created.equals(lock.created) : lock.created != null) return false;
        if (originId != null ? !originId.equals(lock.originId) : lock.originId != null) return false;
        if (type != null ? !type.equals(lock.type) : lock.type != null) return false;
        if (username != null ? !username.equals(lock.username) : lock.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = originId != null ? originId.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Lock{" +
                "originId=" + originId +
                ", type=" + type +
                ", username='" + username + '\'' +
                ", created=" + created +
                '}';
    }
}