/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
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

package org.gatein.wcm.portlet.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Utility class to parse date types.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ParseDates {

    private static final Logger log = Logger.getLogger(ParseDates.class.getName());

    static SimpleDateFormat today = new SimpleDateFormat("HH:mm:ss z");
    static SimpleDateFormat other = new SimpleDateFormat("d MMM yyyy");

    public static String parse(Calendar cal) {
        if (cal == null) return "";
        try {
            Calendar now = Calendar.getInstance();
            boolean sameDay = now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
            String output = null;
            if (sameDay) {
                output = today.format(cal.getTime());
            } else {
                output = other.format(cal.getTime());
            }
            return output;
        } catch (Exception e) {
            log.warning("Error parsing cal: " + cal + ". Error: " + e.toString());
            e.printStackTrace();
        }
        return "";
    }
}
