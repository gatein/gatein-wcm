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

import java.util.List;

import org.gatein.wcm.api.model.publishing.PublishTask;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.api.services.exceptions.PublishException;

/**
 * 
 * Defines administration API for publishing services. <br>
 * 
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 * 
 */
public interface PublishService {

    public List<PublishTask> getTodoList() throws ContentIOException, ContentSecurityException;

    public PublishTask resolveTask(PublishTask task, PublishTask.Status status, String comments) throws PublishException,
            ContentIOException, ContentSecurityException;

}
