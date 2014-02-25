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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * It represents a relationship between Posts, Uploads or Templates.
 * A relation is defined by a key.
 * For example:
 *  Post1 + key("en") = Post2
 *  Post2 is the english version of Post1.
 *  First use case is to use with localization relationships but it's a generic class to use it in more scenarios.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Entity
@Table(name = "wcm_relationships")
@IdClass(RelationshipPK.class)
@Cacheable
@NamedQueries({
        @NamedQuery(name = "listRelationships", query = "select r from Relationship r where r.originId = :originId and r.type = :type order by r.originId, r.key"),
        @NamedQuery(name = "listPostsRelationships", query = "select p from Relationship r, Post p where p.id = r.aliasId and r.originId = :originId and r.type = :type order by r.originId, r.key"),
        @NamedQuery(name = "listTemplatesRelationships", query = "select t from Relationship r, Template t where t.id = r.aliasId and r.originId = :originId and r.type = :type order by r.originId, r.key")
})
final public class Relationship implements Serializable {
	
	private Long originId;
	private String key;
    private Character type;
	private Long aliasId;

    @Id
    @Column(name = "relationship_origin_id")
    public Long getOriginId() {
        return originId;
    }
    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    @Id
    @Column(name = "relationship_key")
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    @Id
    @Column(name = "relationship_type")
    public Character getType() {
        return type;
    }
    public void setType(Character type) {
        this.type = type;
    }

    @Column(name = "relationship_alias_id")
    public Long getAliasId() {
        return aliasId;
    }
    public void setAliasId(Long aliasId) {
        this.aliasId = aliasId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relationship that = (Relationship) o;

        if (!aliasId.equals(that.aliasId)) return false;
        if (!key.equals(that.key)) return false;
        if (!originId.equals(that.originId)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = originId.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + aliasId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "originId=" + originId +
                ", key='" + key + '\'' +
                ", type=" + type +
                ", aliasId=" + aliasId +
                '}';
    }
}