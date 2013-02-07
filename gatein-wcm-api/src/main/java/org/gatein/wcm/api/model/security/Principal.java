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
 * Principal represents a user or group. <br>
 * Groups can define memberships. <br>
 * Groups can use wildcards for memberships only. <br>
 * <br>
 * Examples of Principal: <br>
 * <li> Principal[id = "user1234", type = PrincipalType.USER] - (Simple user)
 * <li> Principal[id = "group1234", type = PrincipalType.GROUP] - (Simple group)
 * <li> Principal[id = "group1234:roleA", type = PrincipalType.GROUP] - (Members of group1234 with role roleA)
 * <li> Principal[id = "*:roleA", type = PrincipalType.GROUP] - (Members of any group with role roleA) <br>
 * <br>
 * 
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface Principal {

	/**
	 * 
	 * @return This method returns ID of the principal. <br>
	 * A Principal can represent an user or a group, or a group+membership.
	 */
	public String getId();

	/**
	 * 
	 * @return This method returns Type of principal (user or group).
	 */
	public PrincipalType getType();
		
	public enum PrincipalType {
		USER, GROUP;
	}
	
}
