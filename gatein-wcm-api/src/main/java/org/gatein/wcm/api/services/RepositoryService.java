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
package org.gatein.wcm.api.services;

import org.exoplatform.services.organization.User;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;

/**
 * 
 * Repository is an abstraction layer of content storage. <br>
 *
 * 
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface RepositoryService {

	/**
	 * 
	 * Opens a content sessions and returns a content API. <br>
	 * 
	 * @param idRepository - Repository id.
	 * @param user - User of the content session.
	 * @return ContentService API for given repository id.
	 * @throws ContentIOException if any IO related problem with repository.
	 * @throws ContentSecurityException if user has not been granted to create Session on given repository. 
	 */	
	public ContentService createContentSession(String idRepository, User user)
		throws ContentIOException, ContentSecurityException;
	
	
	public PublishService createPublishSession(String idRepository, User user)
		throws ContentIOException, ContentSecurityException;
			
}
