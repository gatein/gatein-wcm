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
package org.gatein.wcm.services.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.gatein.wcm.Wcm;
import org.junit.Test;

/**
 * Unit tests for Zip files manipulation
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ZipFilesTest {

    @Test
    public void simpleZipTest() throws Exception {

        FileInputStream in = null;
        ZipOutputStream out = null;
        try {
            String tmp = System.getProperty(Wcm.UPLOADS.TMP_DIR);
            PrintWriter writer = new PrintWriter(tmp + "/test.txt", "UTF-8");
            writer.println("First line");
            writer.println("Second line");
            writer.close();

            in = new FileInputStream(tmp + "/test.txt");
            out = new ZipOutputStream(new FileOutputStream(tmp + "/test.zip"));

            ZipEntry entry = new ZipEntry("tmp/test.txt");
            out.putNextEntry(entry);
            IOUtils.copy(in, out);

            in.close();
            out.close();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

}
