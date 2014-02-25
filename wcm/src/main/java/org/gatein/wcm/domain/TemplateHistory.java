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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TemplateHistory will store versioning of wcm templates.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Entity
@Table(name = "wcm_templates_history")
@IdClass(TemplateHistoryPK.class)
@Cacheable
@NamedQueries({
        @NamedQuery(name = "maxTemplateVersion", query = "select max(th.version) from TemplateHistory th where th.id = :templateid"),
        @NamedQuery(name = "versionsTemplate", query = "select th.version from TemplateHistory th where th.id = :templateid order by th.version desc")
})
final public class TemplateHistory implements Serializable {

	private Long id;
    private Long version;
	private String name;
	private String content;
	private String locale;
    private Calendar created;
    private Calendar modified;
    private Calendar deleted;
    private String user;

    public TemplateHistory() {
        this.created = Calendar.getInstance();
        this.modified = (Calendar)this.created.clone();
    }

    @Id
	@Column(name = "template_id")	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    @Id
    @Column(name = "template_version")
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }

	@Column(name = "template_name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Lob
	@Column(name = "template_content")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Column(name = "template_locale")
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}

    @Column(name = "template_created")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreated() {
        return created;
    }
    public void setCreated(Calendar created) {
        this.created = created;
    }

    @Column(name = "template_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getModified() {
        return modified;
    }
    public void setModified(Calendar modified) {
        this.modified = modified;
    }

    @Column(name = "template_deleted")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getDeleted() {
        return deleted;
    }
    public void setDeleted(Calendar deleted) {
        this.deleted = deleted;
    }

    @Column(name = "template_user")
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    @Override
	public String toString() {
		return "TemplateHistory [id=" + id + ", name=" + name
				+ ", content=" + content + ", locale=" + locale + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TemplateHistory other = (TemplateHistory) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}