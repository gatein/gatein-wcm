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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Utility class to manipulate files and paths.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class FileAux {

    private static final Logger log = Logger.getLogger(FileAux.class.getName());

    private static final MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
    {
        mimeTypesMap.addMimeTypes("text");
    }

    private static Magic parser = new Magic();
    /*
     *  Aux functions to extract path for categories
     */
    public static String child(String path) {
        if (path == null || path.length() == 0) return path;
        if (path.indexOf("/") == -1) return path;
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String parent(String path) {
        if (path == null || path.length() == 0) return path;
        if (path.indexOf("/") == -1) return "";
        return path.substring(0, path.lastIndexOf("/"));
    }

    /*
     * Aux function to unzip in a folder
     */
    public static void unzip(ZipInputStream zis, File output) throws IOException {
        if (zis == null || output == null) return;
        ZipEntry entry;
        OutputStream os = null;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = new File(output, entry.getName());
                if (entry.isDirectory()) {
                    if (!entryFile.exists()) {
                        entryFile.mkdirs();
                    }
                } else {
                    if (entryFile.getParentFile() != null && !entryFile.getParentFile().exists()) {
                        entryFile.getParentFile().mkdirs();
                    }
                    if (!entryFile.exists()) {
                        entryFile.createNewFile();
                    }
                    os = new FileOutputStream(entryFile);
                    IOUtils.copy(zis, os);
                    os.close();
                }
            }
            zis.close();
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(os);
        }
    }

    public static void moveFile(String oldPath, String newPath) {
        try {
            File oldF = new File(oldPath);
            File newF = new File(newPath);
            if (!newF.exists()) {
                FileUtils.moveFile(oldF, newF);
            }
        } catch (Exception e) {
            log.warning("Error trying to move upload: " + oldPath + " to " + newPath + ". Msg: " + e.getMessage()) ;
        }
    }

    public static boolean isText(File file) {
        if (file == null) return false;
        String mimeType = mimeTypesMap.getContentType(file);
        try {
            Logger.getLogger(parser.getClass().getName()).setLevel(Level.SEVERE);
            MagicMatch match = parser.getMagicMatch(file, true);
            mimeType = match.getMimeType();
        } catch (Exception e) {
            // jMimeMagic can't find extension, using standard
        }
        if (mimeType != null && mimeType.startsWith("text")) return true;
        return false;
    }

    public static String changeLinks(String doc, Map<Long, Long> mUploads) {
        if (doc == null || doc.length() == 0) {
            return null;
        }

        int length = doc.length();
        int i = 0;
        int startToken = -1;
        int finishToken = -1;
        boolean finish = false;
        boolean modified = false;
        StringBuilder output = new StringBuilder();

        while (!finish) {
            boolean found = false;
            Character ch = doc.charAt(i);
            // Checks if there is a pattern under present position
            if (ch.equals('r') &&
                    ((i+1) < length) && doc.charAt(i+1) == 's' &&
                    ((i+2) < length) && doc.charAt(i+2) == '/' &&
                    ((i+3) < length) && doc.charAt(i+3) == 'u' &&
                    ((i+4) < length) && doc.charAt(i+4) == '/') {
                // Search end of token
                boolean numbers = true;
                startToken = i;
                finishToken = i + 5;
                while (numbers) {
                    if (finishToken < length &&
                            doc.charAt(finishToken) >= '0' &&
                            doc.charAt(finishToken) <= '9') {
                        finishToken++;
                    } else {
                        numbers = false;
                    }
                }
                if (finishToken < length) {
                    String token = doc.substring(startToken, finishToken);
                    int index = -1;
                    try {
                        index = new Integer(token.substring(5, token.length()));
                    } catch (Exception e) {
                        // Not a number
                    }
                    if (index > -1) {
                        Long newIndex = mUploads.get(new Long(index));
                        if (newIndex != null) {
                            output.append("rs/u/" + newIndex);
                            found = true;
                            modified = true;
                            i = finishToken - 1; // To read end quotes
                        }
                    }
                }
            }
            if (!found) {
                output.append(ch);
            }
            i++;
            if (i >= length) {
                finish = true;
            }
        }

        if (modified) {
            return output.toString();
        } else {
            return null;
        }
    }


    public static boolean changeLinksOnFile(File input,
                                       File output,
                                       Map<Long, Long> mUploads) {
        if (input == null
                || output == null
                || mUploads.size() == 0)
            return false;

        FileReader reader;
        FileWriter writer;
        BufferedReader bReader;
        BufferedWriter bWriter;

        try {
            reader = new FileReader(input);
            writer = new FileWriter(output);
            bReader = new BufferedReader(reader);
            bWriter = new BufferedWriter(writer);
        } catch (FileNotFoundException e) {
            log.warning("File not found " + input + ". Msg: " + e.getMessage());
            return false;
        } catch (IOException e) {
            log.warning("Error writing file " + output + ". Msg: " + e.getMessage());
            return false;
        }

        boolean modified = false;

        try {
            String line;
            while ((line = bReader.readLine()) != null) {

                // Checking token
                String changed = changeLinks(line, mUploads);
                if (changed != null && changed.length() > 0) {
                    line = changed;
                    modified = true;
                }

                bWriter.append(line);
                bWriter.newLine();
            }
        } catch (IOException e) {
            log.warning("Error reading or writing on " + input + " or " + output + ". Msg: " + e.getMessage());
        } finally {
            try {
                bReader.close();
                bWriter.close();
            } catch (IOException ee) {
                // Silent on close
            }
        }

        return modified;
    }



}
