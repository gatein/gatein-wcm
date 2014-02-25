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

package org.gatein.wcm.api.util;

/**
 * Helper class for WCM content manipulation.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class WcmUtils {

    /**
     * @param html code from Post's content
     * @param index of the image to extract
     * @return src of the <img> tag to extract
     */
    public static String extractSrcImg(String html, int index) {
        if (html == null) return "";
        int found = 0;
        int i = 0;
        int j = 0;
        String output = "";
        while (found != -1) {
            j = html.indexOf("<img", i);
            if (j != -1) {
                if (found == index) {
                    i = html.indexOf(">", j);
                    output = html.substring(j, i+1);
                    found = -1;
                } else {
                    i = j+1;
                    found++;
                }
            } else {
                found = -1;
            }
        }
        if (!"".equals(output)) {
            i = output.indexOf("src=");
            j = output.indexOf(" ", i);
            output = output.substring(i + 5, j - 1);
        }
        return output;
    }

    /**
     * @param html code from Post's content
     * @param index of the image to extract
     * @return <img> tag to extract
     */
    public static String extractImg(String html, int index) {
        if (html == null) return "<img>";
        int found = 0;
        int i = 0;
        int j = 0;
        String output = "<img>";
        while (found != -1) {
            j = html.indexOf("<img", i);
            if (j != -1) {
                if (found == index) {
                    i = html.indexOf(">", j);
                    output = html.substring(j, i+1);
                    found = -1;
                } else {
                    i = j+1;
                    found++;
                }
            } else {
                found = -1;
            }
        }
        return output;
    }

}
