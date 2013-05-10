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
package org.gatein.wcm.impl.model;

import java.io.InputStream;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;

/**
 * @see {@link WCMBinaryDocument}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMBinaryDocumentImpl extends WCMObjectImpl implements WCMBinaryDocument {

    protected String locale;
    protected String version;
    protected String mimeType;
    protected long size;
    protected String fileName;
    protected InputStream content;
    protected String encoding;

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public InputStream getContentAsInputStream() {
        return content;
    }

    // Protected methods

    protected WCMBinaryDocumentImpl() {
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    void setSize(long size) {
        this.size = size;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    /**
     * Note that {@link #content} is not considered to compute the hash code.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((encoding == null) ? 0 : encoding.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    /**
     * Note that {@link #content} is not considered to compare.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WCMBinaryDocumentImpl other = (WCMBinaryDocumentImpl) obj;
        if (encoding == null) {
            if (other.encoding != null)
                return false;
        } else if (!encoding.equals(other.encoding))
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        if (mimeType == null) {
            if (other.mimeType != null)
                return false;
        } else if (!mimeType.equals(other.mimeType))
            return false;
        if (size != other.size)
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    /**
     * @see org.gatein.wcm.api.model.content.WCMBinaryDocument#getEncoding()
     */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /**
     * @see org.gatein.wcm.api.model.content.WCMBinaryDocument#setEncoding(java.lang.String)
     */
    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}