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
package org.gatein.wcm.api.services.exceptions;

/**
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ContentSecurityException extends Exception {

    private static final long serialVersionUID = 2113146312414252797L;

    public ContentSecurityException(String msg) {
        super(msg);
    }

    /**
     * @param message
     * @param cause
     */
    public ContentSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ContentSecurityException(Throwable cause) {
        super(cause);
    }

}
