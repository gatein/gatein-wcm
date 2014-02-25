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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Acl defines rights for content manipulation.
 * Acl can be linked to Posts, Categories or Uploads objects.
 * Acl is linked with a GateIn's group defined in principal
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Entity
@Table(name = "wcm_security_acl")
@Cacheable
final public class Acl implements Serializable {

	private Long id;
	private String principal;
	private Character permission;
	private Category category;
	private Post post;
	private Upload upload;
	
	public Acl() { }
	
	public Acl(String principal, Character permission) {
		this.principal = principal;
		this.permission = permission;
	}
	
	@Id @GeneratedValue
	@Column(name = "acl_id")		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    /**
     * @return GateIn's group with this Acl refers to
     */
	@Column(name = "acl_principal")
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}

    /**
     * @see org.gatein.wcm.Wcm.ACL
     * @return permission defined in this Acl.
     */
	@Column(name = "acl_type")
	public Character getPermission() {
		return permission;
	}
	public void setPermission(Character permission) {
		this.permission = permission;
	}

    /**
     * @see Category
     * @return category attached to this Acl, It can be null.
     */
	@ManyToOne
	@JoinColumn(name = "acl_category_id")	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {		
		this.category = category;
	}

    /**
     * @see Post
     * @return post attached to this Acl, It can be null.
     */
	@ManyToOne
	@JoinColumn(name = "acl_post_id")		
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}

    /**
     * @see Upload
     * @return upload attached to this Acl, It can be null.
     */
    @ManyToOne
	@JoinColumn(name = "acl_upload_id")
	public Upload getUpload() {
		return upload;
	}

	public void setUpload(Upload upload) {
		this.upload = upload;
	}

	@Override
	public String toString() {
		return "Acl [id=" + id + ", principal=" + principal + ", permission="
				+ permission + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((post == null) ? 0 : post.hashCode());
		result = prime * result
				+ ((principal == null) ? 0 : principal.hashCode());
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
		Acl other = (Acl) obj;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		if (principal == null) {
			if (other.principal != null)
				return false;
		} else if (!principal.equals(other.principal))
			return false;
		return true;
	}
	
}