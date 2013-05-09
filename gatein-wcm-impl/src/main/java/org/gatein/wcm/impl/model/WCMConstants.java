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

import java.util.Arrays;
import java.util.List;

/**
 * Reserved entries used for mapping WCM metadata on top of a JCR.
 * <p>
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMConstants {

    public static final List<String> RESERVED_ENTRIES = Arrays.asList("jcr:system", "__acl", "__wcmstatus", "__wcmroles", "__comments", "__categories", "__properties" , "__relationships");

    /**
     * Size used when reading from or writing to binary or character streams.
     */
    public static final int BUFFER_SIZE = 128;

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String MIME_TEXT_HTML = "text/html";
    public static final String DEFAULT_MIME_TYPE = MIME_TEXT_HTML;

}
