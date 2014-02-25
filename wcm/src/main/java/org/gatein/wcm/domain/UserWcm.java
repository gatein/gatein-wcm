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
import java.util.HashSet;
import java.util.Set;

import org.gatein.wcm.Wcm;

/**
 * User representacion.
 * Users are managed in GateIn Portal.
 * Authorization is managed in GateIn Portal using groups under an specific path.
 * For example:
 * 	/wcm/group1
 *  /wcm/group2
 *  /wcm/group3
 *  
 *  All groups under /wcm/* path will be used to authorization inside wcm system.
 *  It's responsability to user to popule only these groups.
 *  
 * This class is not persisted due we don't need to maintain two models of users, it's used as a helper for some functions.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
final public class UserWcm implements Serializable {

	private String username;
	private Set<String> groups = new HashSet<String>();
    private Set<String> writeGroups = new HashSet<String>();
    private boolean manager = false;
	
	public UserWcm() { }
	
	public UserWcm(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void add(String group) {
		if (group == null) return;
		if (groups.contains(group)) return;
		groups.add(group);
        // We are not using memberships
        // One exception is "/wcm" group, that is considering as a simple mark that a user can access to lw wcm editor
        if (!group.equals(Wcm.GROUPS.WCM)) {
            writeGroups.add(group);
        }
	}
	
	public void remove(String group) {
		if (group == null) return;
		groups.remove(group);
	}

    /*
        Validate if group if one of the children of /wcm
     */
    public boolean checkWcmGroup(String group) {
        if (group == null) return false;
        if ("".equals(group)) return false;
        return writeGroups.contains(group);
    }

    public Set<String> getWriteGroups() {
        return writeGroups;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    /*
        A User can write if:

            Explicit Wcm.ACL.WRITE Acl present attached to a group under /wcm root group
     */
    public boolean canWrite(Object o) {
        if (o == null) return false;
        Set<Acl> acls = null;
        if (o instanceof Post) {
            acls = ((Post)o).getAcls();
        } else if (o instanceof Category) {
            acls = ((Category)o).getAcls();
        } else if (o instanceof Upload) {
            acls = ((Upload)o).getAcls();
        } else {
            return false;
        }
        if (acls == null) return false;
        for (Acl acl : acls) {
            if (acl.getPrincipal().equals(Wcm.GROUPS.ALL) && acl.getPermission().equals(Wcm.ACL.WRITE)) {
                return true;
            } else if (this.checkWcmGroup(acl.getPrincipal()) && acl.getPermission().equals(Wcm.ACL.WRITE)) {
                return true;
            }
        }
        return false;
    }

    /*
        A User can read if:

            Explicit Wcm.ACL.WRITE Acl present attached to a group under /wcm root group
            Not Explicit Wcm.ACL.NONE Acl present to a group under /wcm root group
     */
    public boolean canRead(Object o) {
        if (o == null) return false;
        Set<Acl> acls;
        if (o instanceof Post) {
            acls = ((Post)o).getAcls();
        } else if (o instanceof Category) {
            acls = ((Category)o).getAcls();
        } else if (o instanceof Upload) {
            acls = ((Upload)o).getAcls();
        } else {
            return true;
        }
        if (acls == null) return true;
        for (Acl acl : acls) {
            if (acl.getPrincipal().equals(Wcm.GROUPS.ALL) && acl.getPermission().equals(Wcm.ACL.WRITE)) {
                return true;
            } else if (this.checkWcmGroup(acl.getPrincipal()) && acl.getPermission().equals(Wcm.ACL.WRITE)) {
                return true;
            }
            if (acl.getPrincipal().equals(Wcm.GROUPS.ALL) && acl.getPermission().equals(Wcm.ACL.NONE)) {
                return false;
            } else if (this.checkWcmGroup(acl.getPrincipal()) && acl.getPermission().equals(Wcm.ACL.NONE)) {
                return false;
            }
        }
        return true;
    }

    @Override
	public String toString() {
		return "UserWcm [username=" + username + ", groups=" + groups + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		UserWcm other = (UserWcm) obj;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
