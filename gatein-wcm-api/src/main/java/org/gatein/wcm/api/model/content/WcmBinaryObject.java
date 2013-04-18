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
 * In WCM a BinaryContent is a file that are not pre-processed. <br />
 * It is useful to store images, large files, or text files that are not supposed to be preprocessed by WCM. <br />
 * For example: .pdf, .doc, .jpg, .png can be BinaryContent, <br />
 * also large text files as .css/.js that WCM only have to serve them as resource. <br />
 * <br/>
 * Text content representation that would have a high maintenance by writers should be represented as {@link WcmTextObject}
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public interface WcmBinaryObject extends WcmObject {

   /**
    *
    * @return This method returns the version of the content.
    */
   String getVersion();

    /**
     *
     * @return This method returns MimeType of file stored.
     */
    String getMimeType();

    /**
     *
     * @return This method returns size of file stored.
     */
    long getSize();

    /**
     *
     * @return This method returns name of file stored.
     */
    String getFileName();

    /**
     *
     * @return This method will return an InputStream of file stored.
     */
    InputStream getContent();

}
