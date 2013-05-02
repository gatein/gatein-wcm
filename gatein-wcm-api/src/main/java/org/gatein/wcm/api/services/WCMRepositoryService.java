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

import org.gatein.wcm.api.services.exceptions.WCMContentIOException;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;

/**
 *
 * Repository is an abstraction layer of content storage.
 * <p>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WCMRepositoryService {

    /**
     * Sets default repository name to open a WCM session.
     *
     * @param repository - Repository name
     */
    void setDefaultRepository(String repository);

    /**
     * Sets default workspace name to open a WCM session.
     * @param workspace
     */
    void setDefaultWorkspace(String workspace);

    /**
     * Return default repository name to open a WCM session.
     * @return Repository name
     */
    String getDefaultRepository();

    /**
     * Return default workspace name to open a WCM session.
     * @return
     */
    String getDefaultWorkspace();

    /**
    *
    * @return Return default locale of repository/workspace
    */
   String getDefaultLocale();

   /**
    * Sets default locale of repository/workspace
    *
    * @param locale - Locale of repository/workspace
    */
   void setDefaultLocale(String locale);

    /**
     *
     * Opens a content session and returns a ContentService API.
     * <p>
     *
     * @param user - User of the repository.
     * @param password - Password of the repository.
     * @return ContentService API for given repository id.
     * @throws WCMContentIOException if any IO related problem with repository.
     * @throws WCMContentSecurityException if user has not been granted to create Session on given repository.
     */
    WCMContentService createContentSession(String user, String password)
            throws WCMContentIOException, WCMContentSecurityException;

    /**
     * Opens a publishing session and returns a PublishService API.
     * <p>
     *
     * @param user
     * @param password
     * @return
     * @throws WCMContentIOException
     * @throws WCMContentSecurityException
     */
    WCMPublishService createPublishSession(String user, String password)
            throws WCMContentIOException, WCMContentSecurityException;

}
