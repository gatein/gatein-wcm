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
package org.gatein.wcm.api.model.content;

import java.io.InputStream;

/**
 * 
 * File representation. <br />
 * Implementation should define where to storage resource (memory, file, both). <br />
 * This is transparent for end-user that access to the resource via InputStream. <br />
 * 
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class Resource extends Content {

	private static final long serialVersionUID = 2L;
	
	String contentType;
	Long size;
	String fileName;
	InputStream content;	
	
}
