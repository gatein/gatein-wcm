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
import org.gatein.wcm.api.model.security.WCMUser;

/**
 *
 * Content can participate in basic workflows that represents a publication process. <br />
 * Status class represent state of content inside a publication process. <br />
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WCMPublishStatus {

    public enum Status {
        DRAFT, PUBLISHED
    }

    /**
     *
     * @return This method returns Content associated with this Status.
     */
    WCMObject getContent();

    /**
     *
     * @return This method returns status of the content. <br>
     *         It will be one of this values: <li>PublishStatus.Status.DRAFT <li>PublishStatus.Status.PUBLISHED
     *
     */
    Status getStatus();

    /**
     *
     * @return This method returns date created of this status.
     */
    Date getCreated();

    /**
     *
     * @return This method returns User responsible of last status.
     */
    WCMUser getStatusBy();

    /**
     *
     * @return This method returns a list of publishing tasks representing history of publishing process.
     */
    List<WCMPublishTask> getHistory();

}
