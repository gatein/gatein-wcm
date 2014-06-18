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
package org.gatein.wcm.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.activation.FileDataSource;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for some utils functions for file manipulation
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class FileAuxTest {

    @Test
    public void testIsText() throws Exception {
        URL testReplaceFile = getClass().getResource("/test-detection");
        String path = testReplaceFile.getPath();
        File f = new File(path);
        assertTrue(FileAux.isText(f));
    }

    @Test
    public void testReplaceAllInFile() throws Exception {
        URL testReplaceFile = getClass().getResource("/test-replace-file.txt");

        String input = testReplaceFile.getPath();
        String output = input + "." + UUID.randomUUID().toString();

        Map<Long, Long> substitutes = new HashMap<Long, Long>();
        substitutes.put(704l, 999l);

        boolean result = FileAux.changeLinksOnFile(new File(input), new File(output), substitutes);
        assertTrue(result);

        String tmp = output + "." + UUID.randomUUID().toString();
        result = FileAux.changeLinksOnFile(new File(output), new File(tmp), substitutes);
        assertFalse(result);
    }
}
