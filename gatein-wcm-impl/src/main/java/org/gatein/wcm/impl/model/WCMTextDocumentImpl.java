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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.gatein.wcm.api.model.content.WCMTextDocument;
import org.gatein.wcm.impl.util.ConvertibleByteArrayOutputStream;

/**
 * @see {@link WCMTextDocument}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMTextDocumentImpl extends WCMBinaryDocumentImpl implements WCMTextDocument {

    /**
     * Sets {@link WCMBinaryDocumentImpl#size} properly.
     *
     * @see org.gatein.wcm.api.model.content.WCMTextDocument#getContentAsString()
     */
    @Override
    public String getContentAsString() {
        if (getSize() == 0) {
            return null;
        } else {
            /* Using getSize() for StringBuilder capacity is actually worst case */
            StringBuilder sb = new StringBuilder((int) getSize());
            Reader r = getContentAsReader();
            char[] buffer = new char[WCMConstants.BUFFER_SIZE];
            int len = 0;
            try {
                while ((len = r.read(buffer)) >= 0) {
                    sb.append(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return sb.toString();
        }
    }

    // Protected methods

    protected WCMTextDocumentImpl() {
    }

    public void setContent(String content) {
        if (content == null) {
            super.setContent((InputStream) null);
        } else {
            setContent(new StringReader(content));
        }
    }

    /**
     * @see org.gatein.wcm.api.model.content.WCMTextDocument#getContentAsReader()
     */
    @Override
    public Reader getContentAsReader() {
        String encoding = getEncoding();
        if (encoding == null) {
            throw new IllegalStateException("Cannot create a " + Reader.class.getName() + " out of the underlying "
                    + InputStream.class.getName() + " given that encoding is null.");
        } else {
            InputStream in = getContentAsInputStream();
            try {
                return new InputStreamReader(in, encoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Sets {@link WCMBinaryDocumentImpl#size} properly.
     *
     * @see org.gatein.wcm.api.model.content.WCMTextDocument#setContent(java.io.Reader)
     */
    @Override
    public void setContent(Reader reader) {
        if (reader == null) {
            super.setContent((InputStream) null);
        } else {
            String encoding = getEncoding();
            if (encoding == null) {
                this.encoding = WCMConstants.DEFAULT_ENCODING;
            }

            ConvertibleByteArrayOutputStream out = null;
            OutputStreamWriter w = null;
            try {
                out = new ConvertibleByteArrayOutputStream();
                w = new OutputStreamWriter(out, encoding);
                char[] buffer = new char[WCMConstants.BUFFER_SIZE];
                int len = 0;
                while ((len = reader.read(buffer)) >= 0) {
                    w.write(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (w != null) {
                    try {
                        w.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            setContent(out.createInputStream());
        }
    }

}
