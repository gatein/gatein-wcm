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

/**
 *
 * Text content representation. <br />
 * In WCM a Text content is an small text with a high maintenance by writers. <br />
 * For example: an article, news, entry blog, etc. <br />
 * WCM UI will process text content searching for specific WCM metatags. <br />
 * <br />
 * Large text files that are not supposed to be processed by WCM UI, as a .css/.js <br />
 * are better represented with {@link WcmBinaryObject}. <br />
 *
 * BinaryContent objects represent files that are served by WCM UI without preprocessing. <br />
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WcmTextObject extends WcmObject {

    /**
    *
    * @return This method returns the version of the content.
    */
   String getVersion();

    /**
     *
     * @return This method returns text of the content.
     */
    String getContent();

}
