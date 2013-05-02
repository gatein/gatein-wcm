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
import java.util.List;

import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.security.WCMPrincipal;
import org.gatein.wcm.api.model.security.WCMUser;

/**
 *
 * Represents a publishing tasks. <br>
 * It's created when a user wants to publish a content and trigger a publishing flow. <br>
 * Defines: <br>
 * <li>what is the publishing state of the content. <li>who are the Principals granted to resolve this task. <li>who are the
 * User from Principals who takes this tasks. <li>lifecycle of the task.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WCMPublishTask {

    enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    /**
     *
     * @return This method returns status of task completion
     */
    Status getStatus();

    /**
     *
     * @return This method returns the content object to publish.
     */
    WCMObject getContent();

    /**
     *
     * @return This method returns the list of roles associated to this task. <br>
     *         It invokes to this.getContent().getPublishingRoles().
     */
    List<WCMPrincipal> getPublishingRoles();

    /**
     *
     * @return This method returns the user assigned to resolve this publishing task.
     */
    WCMUser getExecuteBy();

    /**
     *
     * @return This method returns date when this task were created.
     */
    Date getCreated();

    /**
     *
     * @return This method returns date when this task were finished.
     */
    Date getFinished();

    /**
     *
     * @return This method returns comments of the task.
     */
    String getComment();

}
