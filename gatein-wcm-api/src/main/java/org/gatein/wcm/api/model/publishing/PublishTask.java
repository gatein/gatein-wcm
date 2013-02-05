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
package org.gatein.wcm.api.model.publishing;

import java.util.Date;

import org.exoplatform.services.organization.User;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.security.Principal;

/**
 * 
 * Represents a publishing tasks. <br>
 * It's created when a user wants to publish a content and trigger a publishing flow. <br>
 * Defines: <br>
 * <li> what is the publishing state of the content.
 * <li> who are the Principals granted to resolve this task.
 * <li> who are the User from Principals who takes this tasks.
 * <li> lifecycle of the task.
 * 
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class PublishTask {

	public enum TaskStatus {
		PENDING, ACCEPTED, REJECTED
	}
	
	Content content;
	PublishFlow flow;
	PublishPoint from;
	PublishPoint to;
	Principal assigned;
	
	User executeBy;
	
	Date created;
	Date finished;
	
	String comments;
	
	
}
