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
package org.gatein.wcm.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMFolder;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;
import org.gatein.wcm.api.model.metadata.WCMCategory;
import org.gatein.wcm.api.model.metadata.WCMComment;
import org.gatein.wcm.api.model.security.WCMPermissionType;
import org.gatein.wcm.api.model.security.WCMPrincipalType;
import org.gatein.wcm.api.services.WCMContentService;
import org.gatein.wcm.api.services.WCMRepositoryService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WCMContentServiceTest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "WCMContentServiceTest.war")
                .addAsResource(new File("src/test/resources/GateIn-UserGuide-v3.5.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
                .addAsResource(new File("src/test/resources/jbossportletbridge.pdf"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));
    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WCMRepositoryService repos;
    WCMContentService cs;

    @Test
    public void createTextDocument() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text1 = cs.createTextDocument("text1", "en", "/", "This is a content");

        Assert.assertFalse(text1 == null);
        Assert.assertEquals("text1", text1.getId());
        Assert.assertEquals("/", text1.getParentPath());
        Assert.assertEquals("/text1", text1.getPath());
        Assert.assertEquals("en", text1.getLocale());
        Assert.assertEquals("This is a content", text1.getContent());
        Assert.assertEquals("admin", text1.getCreatedBy());

        cs.deleteContent("/text1");
        cs.closeSession();
    }

    @Test
    public void createTextDocumentSameIdSeveralLocales() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text2 = cs.createTextDocument("text2", "en", "/", "This is a content");

        Assert.assertFalse(text2 == null);
        Assert.assertEquals("text2", text2.getId());
        Assert.assertEquals("/", text2.getParentPath());
        Assert.assertEquals("/text2", text2.getPath());
        Assert.assertEquals("en", text2.getLocale());
        Assert.assertEquals("This is a content", text2.getContent());
        Assert.assertEquals("admin", text2.getCreatedBy());

        WCMTextDocument text2_es = cs.createTextDocument("text2", "es", "/", "Este es un contenido");

        Assert.assertFalse(text2_es == null);
        Assert.assertEquals("text2__es", text2_es.getId());
        Assert.assertEquals("/", text2_es.getParentPath());
        Assert.assertEquals("/text2__es", text2_es.getPath());
        Assert.assertEquals("es", text2_es.getLocale());
        Assert.assertEquals("Este es un contenido", text2_es.getContent());
        Assert.assertEquals("admin", text2_es.getCreatedBy());

        WCMTextDocument text2_fr = cs.createTextDocument("text2", "fr", "/", "C'est un contenu");

        Assert.assertFalse(text2_fr == null);
        Assert.assertEquals("text2__fr", text2_fr.getId());
        Assert.assertEquals("/", text2_fr.getParentPath());
        Assert.assertEquals("/text2__fr", text2_fr.getPath());
        Assert.assertEquals("fr", text2_fr.getLocale());
        Assert.assertEquals("C'est un contenu", text2_fr.getContent());
        Assert.assertEquals("admin", text2_fr.getCreatedBy());

        cs.deleteContent("/text2");
        cs.deleteContent("/text2__es");
        cs.deleteContent("/text2__fr");
        cs.closeSession();
    }

    @Test
    public void createTextDocumentFailsIfSameIdAndSameLocale() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text3 = cs.createTextDocument("text3", "en", "/", "This is a content");

        Assert.assertFalse(text3 == null);
        Assert.assertEquals("text3", text3.getId());
        Assert.assertEquals("/", text3.getParentPath());
        Assert.assertEquals("/text3", text3.getPath());
        Assert.assertEquals("en", text3.getLocale());
        Assert.assertEquals("This is a content", text3.getContent());
        Assert.assertEquals("admin", text3.getCreatedBy());

        boolean fail = false;
        try {
            cs.createTextDocument("text3", "en", "/", "This is a repeated content");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/text3");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createTextDocumentFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createTextDocument("dummy", "en", "/thispathdoentexist", "This is a dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createTextDocumentFailsIfUserDoesntHaveRights() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createTextDocument("dummy", "en", "/thispathdoentexist", "This is a dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createTextDocumentFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createTextDocument("dummy", "en", "/thispathdoentexist", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createTextDocument("dummy", "en", null, "Test");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createTextDocument("dummy", null, "/thispathdoentexist", "Test");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createTextDocument(null, "en", "/thispathdoentexist", "Test");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createTextDocumentDefaultLocale() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text4 = cs.createTextDocument("text4", "/", "This is a content");

        Assert.assertFalse(text4 == null);
        Assert.assertEquals("text4", text4.getId());
        Assert.assertEquals("/", text4.getParentPath());
        Assert.assertEquals("/text4", text4.getPath());
        Assert.assertEquals(repos.getDefaultLocale(), text4.getLocale());
        Assert.assertEquals("This is a content", text4.getContent());
        Assert.assertEquals("admin", text4.getCreatedBy());

        cs.deleteContent("/text4");
        cs.closeSession();
    }

    @Test
    public void createFolder() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMFolder folder1 = cs.createFolder("folder1", "/");

        Assert.assertFalse(folder1 == null);
        Assert.assertEquals("folder1", folder1.getId());
        Assert.assertEquals("/", folder1.getParentPath());
        Assert.assertEquals("/folder1", folder1.getPath());
        Assert.assertEquals("admin", folder1.getCreatedBy());

        WCMFolder folder2 = cs.createFolder("folder2", "/folder1");

        Assert.assertFalse(folder2 == null);
        Assert.assertEquals("folder2", folder2.getId());
        Assert.assertEquals("/folder1", folder2.getParentPath());
        Assert.assertEquals("/folder1/folder2", folder2.getPath());
        Assert.assertEquals("admin", folder2.getCreatedBy());

        cs.deleteContent("/folder1");
        cs.closeSession();
    }

    @Test
    public void cantCreateContentUnderNotFolder() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("text5", "/", "This is a content");

        boolean fail = false;

        try {
            cs.createTextDocument("dummy", "/text5", "Dummy text");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createFolder("dummy", "/text5");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/text5");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createFolderFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createFolder("dummy", "/thispathdoentexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createFolderFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createFolder("dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createFolder(null, "/thispathdoentexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createFolderFailsIfUserDoesntHaveRights() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createFolder("dummy", "/thispathdoentexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createBinaryDocument() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMBinaryDocument binary1 = cs.createBinaryDocument("binary1", "en", "/", mimeType, size, fileName, content);

        Assert.assertFalse(binary1 == null);
        Assert.assertEquals("binary1", binary1.getId());
        Assert.assertEquals("/", binary1.getParentPath());
        Assert.assertEquals("/binary1", binary1.getPath());
        Assert.assertEquals("en", binary1.getLocale());
        Assert.assertEquals("admin", binary1.getCreatedBy());
        Assert.assertEquals(fileName, binary1.getFileName());
        Assert.assertEquals(size, binary1.getSize());
        Assert.assertEquals(mimeType, binary1.getMimeType());
        Assert.assertFalse(binary1.getContent() == null);

        byte[] file = toByteArray(binary1.getContent());
        Assert.assertEquals(binary1.getSize(), file.length);

        cs.deleteContent("/binary1");
        cs.closeSession();
    }

    @Test
    public void createBinaryDocumentDefaultLocale() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMBinaryDocument binary2 = cs.createBinaryDocument("binary2", "/", mimeType, size, fileName, content);

        Assert.assertFalse(binary2 == null);
        Assert.assertEquals("binary2", binary2.getId());
        Assert.assertEquals("/", binary2.getParentPath());
        Assert.assertEquals("/binary2", binary2.getPath());
        Assert.assertEquals(repos.getDefaultLocale(), binary2.getLocale());
        Assert.assertEquals("admin", binary2.getCreatedBy());
        Assert.assertEquals(fileName, binary2.getFileName());
        Assert.assertEquals(size, binary2.getSize());
        Assert.assertEquals(mimeType, binary2.getMimeType());
        Assert.assertFalse(binary2.getContent() == null);

        byte[] file = toByteArray(binary2.getContent());
        Assert.assertEquals(binary2.getSize(), file.length);

        cs.deleteContent("/binary2");
        cs.closeSession();
    }

    @Test
    public void createBinaryDocumentFailsIfNullArguments() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createBinaryDocument("dummy", "/thispathdoesntexist", mimeType, size, fileName, null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createBinaryDocument("dummy", "/thispathdoesntexist", mimeType, size, null, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createBinaryDocument("dummy", "/thispathdoesntexist", mimeType, 0, fileName, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createBinaryDocument("dummy", "/thispathdoesntexist", null, size, fileName, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createBinaryDocument("dummy", null, mimeType, size, fileName, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createBinaryDocument(null, "/thispathdoesntexist", mimeType, size, fileName, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createBinaryDocumentFailsIfPathDoesntExist() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createBinaryDocument("dummy", "/thispathdoesntexist", mimeType, size, fileName, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createBinaryDocumentFailsIfSameIdAndSameLocale() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMBinaryDocument binary3 = cs.createBinaryDocument("binary3", "en", "/", mimeType, size, fileName, content);

        Assert.assertFalse(binary3 == null);
        Assert.assertEquals("binary3", binary3.getId());
        Assert.assertEquals("/", binary3.getParentPath());
        Assert.assertEquals("/binary3", binary3.getPath());
        Assert.assertEquals("en", binary3.getLocale());
        Assert.assertEquals("admin", binary3.getCreatedBy());
        Assert.assertEquals(fileName, binary3.getFileName());
        Assert.assertEquals(size, binary3.getSize());
        Assert.assertEquals(mimeType, binary3.getMimeType());
        Assert.assertFalse(binary3.getContent() == null);

        byte[] file = toByteArray(binary3.getContent());
        Assert.assertEquals(binary3.getSize(), file.length);

        boolean fail = false;
        try {
            cs.createBinaryDocument("binary3", "en", "/", mimeType, size, fileName, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/binary3");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createBinaryDocumentSameIdSeveralLocales() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMBinaryDocument binary4 = cs.createBinaryDocument("binary4", "en", "/", mimeType, size, fileName, content);

        Assert.assertFalse(binary4 == null);
        Assert.assertEquals("binary4", binary4.getId());
        Assert.assertEquals("/", binary4.getParentPath());
        Assert.assertEquals("/binary4", binary4.getPath());
        Assert.assertEquals("en", binary4.getLocale());
        Assert.assertEquals("admin", binary4.getCreatedBy());
        Assert.assertEquals(fileName, binary4.getFileName());
        Assert.assertEquals(size, binary4.getSize());
        Assert.assertEquals(mimeType, binary4.getMimeType());
        Assert.assertFalse(binary4.getContent() == null);

        byte[] file = toByteArray(binary4.getContent());
        Assert.assertEquals(binary4.getSize(), file.length);

        // To re-read the same InputStream
        content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");

        WCMBinaryDocument binary4_es = cs.createBinaryDocument("binary4", "es", "/", mimeType, size, fileName, content);

        Assert.assertFalse(binary4_es == null);
        Assert.assertEquals("binary4__es", binary4_es.getId());
        Assert.assertEquals("/", binary4_es.getParentPath());
        Assert.assertEquals("/binary4__es", binary4_es.getPath());
        Assert.assertEquals("es", binary4_es.getLocale());
        Assert.assertEquals("admin", binary4_es.getCreatedBy());
        Assert.assertEquals(fileName, binary4_es.getFileName());
        Assert.assertEquals(size, binary4_es.getSize());
        Assert.assertEquals(mimeType, binary4_es.getMimeType());
        Assert.assertFalse(binary4_es.getContent() == null);

        byte[] file_es = toByteArray(binary4_es.getContent());
        Assert.assertEquals(binary4_es.getSize(), file_es.length);

        // To re-read the same InputStream
        content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");

        WCMBinaryDocument binary4_fr = cs.createBinaryDocument("binary4", "fr", "/", mimeType, size, fileName, content);

        Assert.assertFalse(binary4_fr == null);
        Assert.assertEquals("binary4__fr", binary4_fr.getId());
        Assert.assertEquals("/", binary4_fr.getParentPath());
        Assert.assertEquals("/binary4__fr", binary4_fr.getPath());
        Assert.assertEquals("fr", binary4_fr.getLocale());
        Assert.assertEquals("admin", binary4_fr.getCreatedBy());
        Assert.assertEquals(fileName, binary4_fr.getFileName());
        Assert.assertEquals(size, binary4_fr.getSize());
        Assert.assertEquals(mimeType, binary4_fr.getMimeType());
        Assert.assertFalse(binary4_fr.getContent() == null);

        byte[] file_fr = toByteArray(binary4_fr.getContent());
        Assert.assertEquals(binary4_fr.getSize(), file_fr.length);

        cs.deleteContent("/binary4");
        cs.deleteContent("/binary4__es");
        cs.deleteContent("/binary4__fr");
        cs.closeSession();
    }

    @Test
    public void createBinaryDocumentFailsIfUserDoesntHaveRights() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createBinaryDocument("dummy", "en", "/thispathdoentexist", mimeType, size, fileName, content);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getContentWithTextDocuments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text6 = cs.createTextDocument("text6", "en", "/", "This is a content");
        WCMObject obj = cs.getContent("/text6");

        Assert.assertFalse(text6 == null);
        Assert.assertTrue(text6.equals(obj));

        cs.deleteContent("/text6");
        cs.closeSession();
    }

    @Test
    public void getContentWithBinaryDocuments() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMBinaryDocument binary5 = cs.createBinaryDocument("binary5", "/", mimeType, size, fileName, content);
        WCMObject obj = cs.getContent("/binary5");

        Assert.assertFalse(binary5 == null);
        Assert.assertTrue(binary5.equals(obj));

        cs.deleteContent("/binary5");
        cs.closeSession();
    }

    @Test
    public void getContentWithFolders() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMFolder folder3 = cs.createFolder("folder3", "/");
        WCMObject obj = cs.getContent("/folder3");

        Assert.assertFalse(folder3 == null);
        Assert.assertTrue(folder3.equals(obj));

        cs.deleteContent("/folder3");
        cs.closeSession();
    }

    @Test
    public void getContentWithSubfolders() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMFolder folder4 = cs.createFolder("folder4", "/");
        WCMFolder folder41 = cs.createFolder("folder41", folder4.getPath());
        WCMTextDocument text411 = cs.createTextDocument("text411", folder41.getPath(), "This is text 411");
        WCMBinaryDocument binary411 = cs.createBinaryDocument("binary411", folder41.getPath(), mimeType, size, fileName,
                content);

        // folder4 and folder41 are not updated so we need to use getContent() for children
        WCMObject obj = cs.getContent("/folder4");

        Assert.assertFalse(obj == null);
        Assert.assertTrue(obj instanceof WCMFolder);
        WCMFolder folder = (WCMFolder) obj;
        Assert.assertEquals("folder4", folder.getId());
        Assert.assertFalse(folder.getChildren() == null);
        Assert.assertEquals(1, folder.getChildren().size());
        Assert.assertTrue(folder.getChildren().get(0) instanceof WCMFolder);
        WCMFolder subFolder = (WCMFolder) folder.getChildren().get(0);
        Assert.assertFalse(subFolder.getBinaryDocuments() == null);
        Assert.assertFalse(subFolder.getTextDocuments() == null);
        Assert.assertEquals(1, subFolder.getBinaryDocuments().size());
        Assert.assertEquals(1, subFolder.getTextDocuments().size());
        WCMTextDocument textObj = subFolder.getTextDocuments().get(0);
        WCMBinaryDocument binaryObj = subFolder.getBinaryDocuments().get(0);
        Assert.assertTrue(text411.equals(textObj));
        Assert.assertTrue(binary411.equals(binaryObj));

        cs.deleteContent("/folder4");
        cs.closeSession();
    }

    @Test
    public void createRelationship() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("rel1", "en", "/", "This is a content for a relationship");
        cs.createTextDocument("rel2", "es", "/", "Este es otro contenido para una relación");
        cs.createContentRelation("/rel1", "/rel2", "es");
        WCMObject obj = cs.getContent("/rel1", "es");
        Assert.assertFalse(obj == null);
        Assert.assertTrue(obj instanceof WCMTextDocument);
        WCMTextDocument text = (WCMTextDocument) obj;
        Assert.assertEquals("rel2", text.getId());
        Assert.assertEquals("/", text.getParentPath());
        Assert.assertEquals("/rel2", text.getPath());
        Assert.assertEquals("Este es otro contenido para una relación", text.getContent());
        Assert.assertEquals("es", text.getLocale());

        cs.deleteContent("/rel1");
        cs.deleteContent("/rel2");
        cs.closeSession();
    }

    @Test
    public void createRelationshipWithFolders() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createFolder("es", "/");
        cs.createFolder("en", "/");
        cs.createFolder("noticias", "/es");
        cs.createFolder("news", "/en");
        cs.createTextDocument("not1", "es", "/es/noticias", "Esta es la noticia 1");
        cs.createTextDocument("not2", "es", "/es/noticias", "Esta es la noticia 2");
        cs.createTextDocument("not3", "es", "/es/noticias", "Esta es la noticia 3");
        cs.createTextDocument("new1", "en", "/en/news", "This is news 1");
        cs.createTextDocument("new2", "en", "/en/news", "This is news 2");
        cs.createTextDocument("new3", "en", "/en/news", "This is news 3");
        cs.createContentRelation("/es", "/en", "en");
        WCMObject obj = cs.getContent("/es", "en");
        Assert.assertFalse(obj == null);
        Assert.assertTrue(obj instanceof WCMFolder);
        WCMFolder folder = (WCMFolder) obj;
        Assert.assertEquals("en", folder.getId());
        Assert.assertEquals(1, folder.getSubfolders().size());
        Assert.assertEquals(3, folder.getSubfolders().get(0).getTextDocuments().size());

        cs.deleteContent("/es");
        cs.deleteContent("/en");
        cs.closeSession();
    }

    @Test
    public void createRelationshipFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createContentRelation("/dummy1", "/dummy2", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentRelation("/dummy1", null, "dummykey");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentRelation(null, "/dummy2", "dummykey");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createRelationshipFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("fail", "es", "/", "Esta es un contenido de test");

        boolean fail = false;
        try {
            cs.createContentRelation("/fail", "/thispathdoesntexist", "test");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentRelation("/thispathdoesntexist", "/fail", "test");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/fail");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateTextDocument() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text = cs.createTextDocument("update1", "es", "/", "Esta es un contenido de test");
        text.setLocale("en");
        text.setContent("This is an update for a content");
        text = cs.updateTextDocument("/update1", text);

        Assert.assertFalse(text == null);
        Assert.assertEquals("update1", text.getId());
        Assert.assertEquals("/", text.getParentPath());
        Assert.assertEquals("/update1", text.getPath());
        Assert.assertEquals("en", text.getLocale());
        Assert.assertEquals("This is an update for a content", text.getContent());

        cs.deleteContent("/update1");
        cs.closeSession();
    }

    @Test
    public void updateTextDocumentFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text = cs.createTextDocument("update2", "es", "/", "Esta es un contenido de test");
        text.setLocale("en");
        text.setContent("This is an update for a content");

        boolean fail = false;
        try {
            cs.updateTextDocument(null, text);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.updateTextDocument("/update2", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            text.setLocale(null);
            cs.updateTextDocument("/update2", text);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
            text.setLocale("en");
        }

        try {
            text.setContent(null);
            cs.updateTextDocument("/update2", text);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
            text.setContent("This is an update for a content");
        }

        cs.deleteContent("/update2");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateTextDocumentFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument text = cs.createTextDocument("update3", "es", "/", "Esta es un contenido de test");
        text.setLocale("en");
        text.setContent("This is an update for a content");

        boolean fail = false;
        try {
            cs.updateTextDocument("/thispathdoesntexist", text);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/update3");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateTextDocumentWithRelationships() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("update4es", "es", "/", "Este es un contenido de test");
        WCMTextDocument updated = cs.createTextDocument("update4en", "en", "/", "This is a test content");
        cs.createContentRelation("/update4es", "/update4en", "en");

        updated.setContent("This is an UPDATE of update4en");
        updated = cs.updateTextDocument("/update4en", updated);

        WCMObject obj = cs.getContent("/update4es", "en");

        Assert.assertFalse(obj == null);
        Assert.assertTrue(obj.equals(updated));

        cs.deleteContent("/update4es");
        cs.deleteContent("/update4en");
        cs.closeSession();
    }

    @Test
    public void updateContentName() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument doc = cs.createTextDocument("testupdate1", "/", "This is a test content");
        Assert.assertEquals("testupdate1", doc.getId());
        Assert.assertEquals("/testupdate1", doc.getPath());

        doc = (WCMTextDocument)cs.updateContentName("/testupdate1", "newnamemodified");
        Assert.assertEquals("newnamemodified", doc.getId());
        Assert.assertEquals("/newnamemodified", doc.getPath());
        Assert.assertEquals("This is a test content", doc.getContent());

        cs.deleteContent("/newnamemodified");
        cs.closeSession();
    }

    @Test
    public void updateContentNameWithFolders() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMFolder f = cs.createFolder("testupdate2", "/");
        cs.createFolder("subfolder1", "/testupdate2");
        cs.createFolder("subfolder2", "/testupdate2");
        Assert.assertEquals("testupdate2", f.getId());
        Assert.assertEquals("/testupdate2", f.getPath());

        f = (WCMFolder)cs.updateContentName("/testupdate2", "newnamemodified2");
        Assert.assertEquals("newnamemodified2", f.getId());
        Assert.assertEquals("/newnamemodified2", f.getPath());

        cs.deleteContent("/newnamemodified2");
        cs.closeSession();
    }

    @Test
    public void updateContentNameWithBinaryDocuments() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMBinaryDocument bin = cs.createBinaryDocument("testupdate3", "/", mimeType, size, fileName, content);
        Assert.assertEquals("testupdate3", bin.getId());
        Assert.assertEquals("/testupdate3", bin.getPath());
        Assert.assertEquals(size, bin.getSize());

        bin = (WCMBinaryDocument)cs.updateContentName("/testupdate3", "newnamemodified3");
        Assert.assertEquals("newnamemodified3", bin.getId());
        Assert.assertEquals("/newnamemodified3", bin.getPath());
        Assert.assertEquals(size, bin.getSize());

        cs.deleteContent("/newnamemodified3");
        cs.closeSession();
    }

    @Test
    public void updateContentNameWithMetadata() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument doc, doc2;

        cs.createTextDocument("testupdate4", "/", "This is a test content");
        cs.createContentAce("/testupdate4", "admin", WCMPrincipalType.USER, WCMPermissionType.ALL);
        cs.createContentComment("/testupdate4", "This is a test comment");
        cs.createTextDocument("testupdate4", "es", "/", "Este es un contenido relacionado");
        doc = (WCMTextDocument)cs.createContentProperty("/testupdate4", "test", "test property");
        doc2 = (WCMTextDocument)cs.getContent("/testupdate4", "es");

        Assert.assertEquals("testupdate4", doc.getId());
        Assert.assertEquals("/testupdate4", doc.getPath());
        Assert.assertEquals("This is a test content", doc.getContent());
        Assert.assertEquals(1, doc.getAcl().getAces().size());
        Assert.assertEquals(1, doc.getComments().size());
        Assert.assertEquals("test property", doc.getProperties().get("test"));
        Assert.assertEquals("Este es un contenido relacionado", doc2.getContent());

        doc = (WCMTextDocument)cs.updateContentName("/testupdate4", "newnamemodified4");
        doc2 = (WCMTextDocument)cs.getContent("/newnamemodified4", "es");
        Assert.assertEquals("newnamemodified4", doc.getId());
        Assert.assertEquals("/newnamemodified4", doc.getPath());
        Assert.assertEquals("This is a test content", doc.getContent());
        Assert.assertEquals(1, doc.getAcl().getAces().size());
        Assert.assertEquals(1, doc.getComments().size());
        Assert.assertEquals("test property", doc.getProperties().get("test"));
        Assert.assertEquals("Este es un contenido relacionado", doc2.getContent());

        cs.deleteContent(doc.getPath());
        cs.deleteContent(doc2.getPath());
        cs.closeSession();
    }

    @Test
    public void updateContentNameFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.updateContentName("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.updateContentName(null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateContentNameFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.updateContentName("/thispathdoesntexist", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateContentPath() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument doc;

        cs.createFolder("updatefolder1", "/");
        doc = cs.createTextDocument("testupdate5", "/", "This is a test content");
        Assert.assertEquals("testupdate5", doc.getId());
        Assert.assertEquals("/testupdate5", doc.getPath());
        Assert.assertEquals("This is a test content", doc.getContent());

        doc = (WCMTextDocument)cs.updateContentPath("/testupdate5", "/updatefolder1");
        Assert.assertEquals("testupdate5", doc.getId());
        Assert.assertEquals("/updatefolder1/testupdate5", doc.getPath());
        Assert.assertEquals("This is a test content", doc.getContent());

        cs.deleteContent("/updatefolder1");
        cs.closeSession();
    }

    @Test
    public void updateContentPathWithFolders() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMFolder f;

        cs.createFolder("updatefolder2", "/");
        cs.createFolder("sub1", "/updatefolder2");
        cs.createFolder("sub2", "/updatefolder2");
        cs.createFolder("sub3", "/updatefolder2");
        cs.updateContentPath("/updatefolder2/sub2", "/updatefolder2/sub1");
        f = (WCMFolder) cs.updateContentPath("/updatefolder2/sub3", "/updatefolder2/sub1/sub2");

        Assert.assertEquals("sub3", f.getId());
        Assert.assertEquals("/updatefolder2/sub1/sub2/sub3", f.getPath());

        cs.deleteContent("/updatefolder2");
        cs.closeSession();
    }

    @Test
    public void updateContentPathWithBinaryDocuments() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");

        WCMBinaryDocument bin = cs.createBinaryDocument("testupdate6", "/", mimeType, size, fileName, content);
        Assert.assertEquals("testupdate6", bin.getId());
        Assert.assertEquals("/testupdate6", bin.getPath());
        Assert.assertEquals(size, bin.getSize());

        cs.createFolder("updatefolder3", "/");
        cs.createFolder("sub1", "/updatefolder3");

        bin = (WCMBinaryDocument)cs.updateContentPath("/testupdate6", "/updatefolder3/sub1");
        Assert.assertEquals("testupdate6", bin.getId());
        Assert.assertEquals("/updatefolder3/sub1/testupdate6", bin.getPath());
        Assert.assertEquals(size, bin.getSize());

        cs.deleteContent("/updatefolder3");
        cs.closeSession();
    }

    @Test
    public void updateContentPathWithMetadata() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument doc, doc2;

        cs.createTextDocument("testupdate7", "/", "This is a test content");
        cs.createContentAce("/testupdate7", "admin", WCMPrincipalType.USER, WCMPermissionType.ALL);
        cs.createContentComment("/testupdate7", "This is a test comment");
        cs.createTextDocument("testupdate7", "es", "/", "Este es un contenido relacionado");
        doc = (WCMTextDocument)cs.createContentProperty("/testupdate7", "test", "test property");
        doc2 = (WCMTextDocument)cs.getContent("/testupdate7", "es");

        Assert.assertEquals("testupdate7", doc.getId());
        Assert.assertEquals("/testupdate7", doc.getPath());
        Assert.assertEquals("This is a test content", doc.getContent());
        Assert.assertEquals(1, doc.getAcl().getAces().size());
        Assert.assertEquals(1, doc.getComments().size());
        Assert.assertEquals("test property", doc.getProperties().get("test"));
        Assert.assertEquals("Este es un contenido relacionado", doc2.getContent());

        cs.createFolder("subfolder7", "/");
        doc = (WCMTextDocument)cs.updateContentPath("/testupdate7", "/subfolder7");
        doc2 = (WCMTextDocument)cs.getContent("/subfolder7/testupdate7", "es");
        Assert.assertEquals("testupdate7", doc.getId());
        Assert.assertEquals("/subfolder7/testupdate7", doc.getPath());
        Assert.assertEquals("This is a test content", doc.getContent());
        Assert.assertEquals(1, doc.getAcl().getAces().size());
        Assert.assertEquals(1, doc.getComments().size());
        Assert.assertEquals("test property", doc.getProperties().get("test"));
        Assert.assertEquals("Este es un contenido relacionado", doc2.getContent());

        cs.deleteContent("/subfolder7");
        cs.deleteContent(doc2.getPath());
        cs.closeSession();
    }

    @Test
    public void updateContentPathFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.updateContentPath("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.updateContentPath(null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateContentPathFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.updateContentPath("/thispathdoesntexist", "/thispathdoesntalsoexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    // @Test
    // TODO Update this test once ACL API is ready
    // By default "lucas" user has not READ rights on default root repository
    public void updateTextDocumentFailsIfUserDoesntHaveRights() throws Exception {
        cs = repos.createContentSession("admin", "admin");
        cs.createTextDocument("update5", "es", "/", "Este es un contenido de test");
        cs.closeSession();

        cs = repos.createContentSession("admin", "admin");
        WCMTextDocument text = (WCMTextDocument) cs.getContent("/update5");
        text.setContent("Esto es una ACTUALIZACIÓN del contenido");

        boolean fail = false;
        try {
            cs.updateTextDocument("/update5", text);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/update5");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateBinaryDocument() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");
        WCMBinaryDocument update6 = cs.createBinaryDocument("update6", "en", "/", mimeType, size, fileName, content);

        content = (FileInputStream) Thread.currentThread().getContextClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
        size = content.available();
        fileName = "wcm-whiteboard.jpg";
        mimeType = "image/jpeg";

        update6.setContent(content);
        update6.setSize(size);
        update6.setFileName(fileName);
        update6.setMimeType(mimeType);
        update6 = cs.updateBinaryDocument("/update6", update6);

        Assert.assertFalse(update6 == null);
        Assert.assertEquals("update6", update6.getId());
        Assert.assertEquals("/", update6.getParentPath());
        Assert.assertEquals("/update6", update6.getPath());
        Assert.assertEquals("en", update6.getLocale());
        Assert.assertEquals("admin", update6.getCreatedBy());
        Assert.assertEquals(fileName, update6.getFileName());
        Assert.assertEquals(size, update6.getSize());
        Assert.assertEquals(mimeType, update6.getMimeType());
        Assert.assertFalse(update6.getContent() == null);

        byte[] file = toByteArray(update6.getContent());
        Assert.assertEquals(update6.getSize(), file.length);

        cs.deleteContent("/update6");
        cs.closeSession();
    }

    @Test
    public void updateBinaryDocumentFailsIfNullArguments() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");
        WCMBinaryDocument update7 = cs.createBinaryDocument("update7", "en", "/", mimeType, size, fileName, content);

        content = (FileInputStream) Thread.currentThread().getContextClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
        size = content.available();
        fileName = "wcm-whiteboard.jpg";
        mimeType = "image/jpeg";

        update7.setLocale("es");
        update7.setContent(content);
        update7.setSize(size);
        update7.setFileName(fileName);
        update7.setMimeType(mimeType);

        boolean fail = false;
        try {
            cs.updateBinaryDocument(null, update7);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.updateBinaryDocument("/update7", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            update7.setLocale(null);
            cs.updateBinaryDocument("/update7", update7);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
            update7.setLocale("es");
        }

        try {
            update7.setContent(null);
            cs.updateBinaryDocument("/update7", update7);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
            update7.setContent(content);
        }

        try {
            update7.setSize(0);
            cs.updateBinaryDocument("/update7", update7);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
            update7.setSize(size);
        }

        try {
            update7.setFileName(null);
            cs.updateBinaryDocument("/update7", update7);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
            update7.setFileName(fileName);
        }

        try {
            update7.setMimeType(null);
            cs.updateBinaryDocument("/update7", update7);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
            update7.setMimeType(mimeType);
        }

        cs.deleteContent("/update7");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateBinaryDocumentFailsIfPathDoesntExist() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");
        WCMBinaryDocument update8 = cs.createBinaryDocument("update8", "en", "/", mimeType, size, fileName, content);

        content = (FileInputStream) Thread.currentThread().getContextClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
        size = content.available();
        fileName = "wcm-whiteboard.jpg";
        mimeType = "image/jpeg";

        update8.setLocale("es");
        update8.setContent(content);
        update8.setSize(size);
        update8.setFileName(fileName);
        update8.setMimeType(mimeType);

        boolean fail = false;
        try {
            cs.updateBinaryDocument("/thispathdoesntexist", update8);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/update8");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    // @Test
    // TODO Update this test once ACL API is ready
    // By default "lucas" user has not READ rights on default root repository
    public void updateBinaryDocumentFailsIfUserDoesntHaveRights() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        cs = repos.createContentSession("admin", "admin");
        WCMBinaryDocument update9 = cs.createBinaryDocument("update9", "en", "/", mimeType, size, fileName, content);
        cs.closeSession();

        cs = repos.createContentSession("admin", "admin");

        content = (FileInputStream) Thread.currentThread().getContextClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
        size = content.available();
        fileName = "wcm-whiteboard.jpg";
        mimeType = "image/jpeg";

        update9.setLocale("es");
        update9.setContent(content);
        update9.setSize(size);
        update9.setFileName(fileName);
        update9.setMimeType(mimeType);

        boolean fail = false;
        try {
            cs.updateBinaryDocument("/thispathdoesntexist", update9);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/update9");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateBinaryDocumentWithRelationships() throws Exception {
        FileInputStream content = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/GateIn-UserGuide-v3.5.pdf");
        long size = content.available();
        String fileName = "GateIn-UserGuide-v3.5.pdf";
        String mimeType = "application/pdf";

        FileInputStream content2 = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/jbossportletbridge.pdf");
        long size2 = content2.available();
        String fileName2 = "jbossportletbridge.pdf";
        String mimeType2 = "application/pdf";

        FileInputStream content3 = (FileInputStream) Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/wcm-whiteboard.jpg");
        long size3 = content3.available();
        String fileName3 = "wcm-whiteboard.jpg";
        String mimeType3 = "image/jpeg";

        cs = repos.createContentSession("admin", "admin");
        cs.createBinaryDocument("update10en", "en", "/", mimeType, size, fileName, content);
        WCMBinaryDocument update10es = cs.createBinaryDocument("update10es", "es", "/", mimeType2, size2, fileName2, content2);
        cs.createContentRelation("/update10en", "/update10es", "es");

        update10es.setContent(content3);
        update10es.setSize(size3);
        update10es.setFileName(fileName3);
        update10es.setMimeType(mimeType3);

        WCMBinaryDocument bin = cs.updateBinaryDocument("/update10es", update10es);
        WCMObject obj = cs.getContent("/update10en", "es");

        Assert.assertTrue(obj.equals(bin));

        byte[] file = toByteArray(((WCMBinaryDocument) obj).getContent());
        Assert.assertEquals(bin.getSize(), file.length);

        cs.deleteContent("/update10es");
        cs.deleteContent("/update10en");
        cs.closeSession();
    }

    @Test
    public void deleteContent() throws Exception {
        cs = repos.createContentSession("admin", "admin");
        cs.createFolder("todelete1", "/");
        cs.getContent("/todelete1");
        String parent = cs.deleteContent("/todelete1");
        Assert.assertEquals("/", parent);

        boolean fail = false;
        try {
            cs.getContent("/todelete1");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteContentFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getContent(null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteContentFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getContent("/thispathdoesntexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createCategory() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMCategory c = cs.createCategory("world1", "en", "World category", "/");

        Assert.assertFalse(c == null);
        Assert.assertEquals("world1", c.getId());
        Assert.assertEquals("World category", c.getDescription().get("en"));
        Assert.assertEquals(c.getParentPath(), "/");
        Assert.assertEquals("/world1", c.getPath());

        cs.deleteCategory("/world1");
        cs.closeSession();
    }

    @Test
    public void createCategoryFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createCategory("world", "en", "World category", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createCategory("world", "en", null, "/");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createCategory("world", null, "World category", "/");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createCategory(null, "en", "World category", "/");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createCategoryDefaultLocale() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMCategory c = cs.createCategory("world2", "World category", "/");

        Assert.assertFalse(c == null);
        Assert.assertEquals("world2", c.getId());
        Assert.assertEquals("World category", c.getDescription().get(repos.getDefaultLocale()));
        Assert.assertEquals(c.getParentPath(), "/");
        Assert.assertEquals("/world2", c.getPath());

        cs.deleteCategory("/world2");
        cs.closeSession();
    }

    @Test
    public void createCategoryWithSubfolders() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createCategory("world3", "en", "World category", "/");
        WCMCategory c1 = cs.createCategory("spain", "en", "Spain category", "/world3");
        WCMCategory c2 = cs.createCategory("france", "en", "France category", "/world3");

        Assert.assertFalse(c1 == null);
        Assert.assertEquals("spain", c1.getId());
        Assert.assertEquals("Spain category", c1.getDescription().get("en"));
        Assert.assertEquals(c1.getParentPath(), "/world3");
        Assert.assertEquals("/world3/spain", c1.getPath());

        Assert.assertFalse(c2 == null);
        Assert.assertEquals("france", c2.getId());
        Assert.assertEquals("France category", c2.getDescription().get("en"));
        Assert.assertEquals(c2.getParentPath(), "/world3");
        Assert.assertEquals("/world3/france", c2.getPath());

        cs.deleteCategory("/world3");
        cs.closeSession();
    }

    @Test
    public void createCategoryWithSameIdMultipleLocales() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMCategory c1 = cs.createCategory("world4", "en", "World category", "/");
        WCMCategory c2 = cs.createCategory("world4", "es", "Categoria nueva", "/");
        WCMCategory c3 = cs.createCategory("world4", "fr", "nouvelle catégorie", "/");

        Assert.assertFalse(c1 == null);
        Assert.assertEquals("world4", c1.getId());
        Assert.assertEquals("World category", c1.getDescription().get("en"));
        Assert.assertEquals(c1.getParentPath(), "/");
        Assert.assertEquals("/world4", c1.getPath());

        Assert.assertFalse(c2 == null);
        Assert.assertEquals("world4", c2.getId());
        Assert.assertEquals("Categoria nueva", c2.getDescription().get("es"));
        Assert.assertEquals("/", c2.getParentPath());
        Assert.assertEquals("/world4", c2.getPath());

        Assert.assertFalse(c3 == null);
        Assert.assertEquals("world4", c3.getId());
        Assert.assertEquals("nouvelle catégorie", c3.getDescription().get("fr"));
        Assert.assertEquals("/", c3.getParentPath());
        Assert.assertEquals("/world4", c3.getPath());

        cs.deleteCategory("/world4");
        cs.closeSession();
    }

    @Test
    public void createCategoryFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createCategory("world", "en", "World category", "/thispathdoesntexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createCategoryFailsIfSameIdAndSameLocale() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createCategory("world5", "en", "World category", "/");

        boolean fail = false;
        try {
            cs.createCategory("world5", "en", "World category", "/");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteCategory("/world5");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateCategoryDescription() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMCategory c = cs.createCategory("world6", "en", "World category", "/");
        c = cs.updateCategoryDescription("/world6", "en", "World category UPDATED");

        Assert.assertFalse(c == null);
        Assert.assertEquals("world6", c.getId());
        Assert.assertEquals("World category UPDATED", c.getDescription().get("en"));
        Assert.assertEquals(c.getParentPath(), "/");
        Assert.assertEquals("/world6", c.getPath());

        cs.deleteCategory("/world6");
        cs.closeSession();
    }

    @Test
    public void updateCategoryDescriptionDefaultLocale() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMCategory c = cs.createCategory("world7", "World category", "/");
        c = cs.updateCategoryDescription("/world7", "World category UPDATED");

        Assert.assertFalse(c == null);
        Assert.assertEquals("world7", c.getId());
        Assert.assertEquals("World category UPDATED", c.getDescription().get(repos.getDefaultLocale()));
        Assert.assertEquals(c.getParentPath(), "/");
        Assert.assertEquals("/world7", c.getPath());

        cs.deleteCategory("/world7");
        cs.closeSession();
    }

    @Test
    public void updateCategoryPath() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createCategory("world8", "Test category", "/");
        cs.createCategory("europe", "Europe categories", "/world8");
        cs.createCategory("asia", "Asia categories", "/world8");
        cs.createCategory("spain", "Spain categories", "/world8/asia");
        cs.updateCategoryPath("/world8/asia/spain", "/world8/europe");
        WCMCategory c = cs.updateCategoryDescription("/world8/europe/spain", "Spain categories UPDATED");

        Assert.assertFalse(c == null);
        Assert.assertEquals("spain", c.getId());
        Assert.assertEquals("Spain categories UPDATED", c.getDescription().get(repos.getDefaultLocale()));
        Assert.assertEquals(c.getParentPath(), "/world8/europe");
        Assert.assertEquals("/world8/europe/spain", c.getPath());

        cs.deleteCategory("/world8");
        cs.closeSession();
    }

    @Test
    public void getCategories() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createCategory("europe", "Europe categories", "/");
        cs.createCategory("asia", "Asia categories", "/");
        cs.createCategory("america", "America categories", "/");

        List<WCMCategory> cats = cs.getCategories("/");

        Assert.assertFalse(cats == null);
        Assert.assertEquals(3, cats.size());

        cs.deleteCategory("/europe");
        cs.deleteCategory("/asia");
        cs.deleteCategory("/america");
        cs.closeSession();
    }

    @Test
    public void getCategoriesFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getCategories(null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getCategoriesFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getCategories("/thispathdoesntexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getCategoriesWithSubfolders() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createCategory("world9", "World categories", "/");
        cs.createCategory("europe", "Europe categories", "/world9");
        cs.createCategory("asia", "Asia categories", "/world9");
        cs.createCategory("america", "America categories", "/world9");

        List<WCMCategory> cats = cs.getCategories("/world9");

        Assert.assertFalse(cats == null);
        Assert.assertEquals(3, cats.size());

        cs.deleteCategory("/world9");
        cs.closeSession();
    }

    @Test
    public void getCategory() throws Exception {
        cs = repos.createContentSession("admin", "admin");
        cs.createCategory("world10", "World category description", "/");
        cs.createCategory("europe", "Europe category description", "/world10");

        WCMCategory c = cs.getCategory("/world10");

        Assert.assertFalse(c == null);
        Assert.assertEquals("world10", c.getId());
        Assert.assertEquals("World category description", c.getDescription().get(repos.getDefaultLocale()));
        Assert.assertEquals("/", c.getParentPath());
        Assert.assertEquals("/world10", c.getPath());

        c = cs.getCategory("/world10/europe");

        Assert.assertFalse(c == null);
        Assert.assertEquals("europe", c.getId());
        Assert.assertEquals("Europe category description", c.getDescription().get(repos.getDefaultLocale()));
        Assert.assertEquals("/world10", c.getParentPath());
        Assert.assertEquals("/world10/europe", c.getPath());

        cs.deleteCategory("/world10");
        cs.closeSession();
    }

    @Test
    public void getCategoryFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getCategory(null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getCategoryFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getCategory("/thispathdoesntexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void putContentCategory() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("testcat", "/", "This is a test of attached category to a content");
        cs.createCategory("world11", "World category", "/");
        cs.createCategory("sports1", "Sports category", "/");
        cs.putContentCategory("/testcat", "/world11");
        cs.putContentCategory("/testcat", "/sports1");
        List<WCMCategory> list = cs.getContentCategories("/testcat");
        Assert.assertEquals(2, list.size());

        cs.removeContentCategory("/testcat", "/world11");
        cs.removeContentCategory("/testcat", "/sports1");
        cs.deleteCategory("/world11");
        cs.deleteCategory("/sports1");
        cs.deleteContent("/testcat");
        cs.closeSession();
    }

    @Test
    public void deleteCategory() throws Exception {
        cs = repos.createContentSession("admin", "admin");
        cs.createCategory("world12", "en", "World category", "/");
        cs.createCategory("world12", "fr", "classe mondiale", "/");
        cs.createCategory("world12", "es", "Categoría mundo", "/");

        cs.deleteCategory("/world12", "en");
        WCMCategory cat = cs.getCategory("/world12");
        Assert.assertEquals(2, cat.getDescription().size());

        cs.deleteCategory("/world12");
        cs.closeSession();
    }

    @Test
    public void deleteCategoryFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.deleteCategory("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.deleteCategory(null, "en");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteCategoryFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.deleteCategory("/thispathdoesntexist", "es");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteCategoryFailsIfCategoryHasReferences() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createCategory("testcategory", "Test Category", "/");
        cs.createTextDocument("testcat1", "/", "This is test content for category 1");
        cs.createTextDocument("testcat2", "/", "This is test content for category 2");
        cs.createTextDocument("testcat3", "/", "This is test content for category 3");

        cs.putContentCategory("/testcat1", "/testcategory");
        cs.putContentCategory("/testcat2", "/testcategory");
        cs.putContentCategory("/testcat3", "/testcategory");

        boolean fail = false;
        try {
            cs.deleteCategory("/testcategory");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/testcat1");

        try {
            cs.deleteCategory("/testcategory");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/testcat2");

        try {
            cs.deleteCategory("/testcategory");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/testcat3");

        cs.deleteCategory("/testcategory");

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getContentFilteredByCategories() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        /*
         * Sites In both locales {es, en}
         *
         * /site1/new1
         *
         * /site1/new2
         *
         * /site1/new3
         *
         * /site1/new4
         *
         * /site2/new5
         *
         * /site2/new6
         *
         * /site2/new7
         *
         * /site2/new8
         *
         * /site3/new9
         *
         * /site3/new10
         *
         * /site3/new11
         *
         * /site3/new12
         *
         * Categories:
         *
         * /news/national --> new1, new5
         *
         * /news/sports --> new2, new6
         *
         * /countries/spain --> new3, new7
         *
         * /countries/france --> new4, new8
         *
         * Queries:
         *
         * /news --> new1, new2, new5, new6 :: filtered by location "/"
         *
         * /countries -> new3, new4, new7, new8 :: filtered by location "/"
         *
         * /news --> new1, new2 :: filtered by location "/site1"
         *
         * /news --> new5, new6 :: filtered by location "/site2"
         *
         */

        // Creating test content
        for (int i = 1; i <= 3; i++) {
            cs.createFolder("site" + i, "/");
        }
        int news = 1;
        for (int site = 1; site <= 3; site++) {
            for (int i = 1; i <= 4; i++) {
                cs.createTextDocument("new" + news, "es", "/site" + site, "Esta es la noticia " + news);
                cs.createTextDocument("new" + news, "en", "/site" + site, "This is the news " + news);
                news++;
            }
        }

        cs.createCategory("news", "es", "Noticias", "/");
        cs.createCategory("news", "en", "News", "/");
        cs.createCategory("countries", "es", "Paises", "/");
        cs.createCategory("countries", "en", "Countries", "/");
        cs.createCategory("national", "es", "Nacional", "/news");
        cs.createCategory("national", "en", "National", "/news");
        cs.createCategory("sports", "es", "Deportes", "/news");
        cs.createCategory("sports", "en", "Sports", "/news");
        cs.createCategory("spain", "es", "España", "/countries");
        cs.createCategory("spain", "en", "Spain", "/countries");
        cs.createCategory("france", "es", "Francia", "/countries");
        cs.createCategory("france", "en", "France", "/countries");

        // Assign categories in content

        cs.putContentCategory("/site1/new1", "/news/national");
        cs.putContentCategory("/site2/new5", "/news/national");
        cs.putContentCategory("/site1/new2", "/news/sports");
        cs.putContentCategory("/site2/new6", "/news/sports");

        cs.putContentCategory("/site1/new3", "/countries/spain");
        cs.putContentCategory("/site2/new7", "/countries/spain");
        cs.putContentCategory("/site1/new4", "/countries/france");
        cs.putContentCategory("/site2/new8", "/countries/france");

        /*
         * Categories:
         *
         * /news/national --> new1, new5
         *
         * /news/sports --> new2, new6
         *
         * /countries/spain --> new3, new7
         *
         * /countries/france --> new4, new8
         *
         * Queries:
         *
         * /news --> new1, new2, new5, new6 :: filtered by location "/"
         *
         * /countries -> new3, new4, new7, new8 :: filtered by location "/"
         *
         * /news --> new1, new2 :: filtered by location "/site1"
         *
         * /news --> new5, new6 :: filtered by location "/site2"
         *
         */

        List<WCMCategory> cats;
        List<WCMObject> result;

        cats = cs.getCategories("/news");
        result = cs.getContent(cats, "/site1");
        ArrayList<String> ids = new ArrayList<String>();
        for (WCMObject r : result) {
            ids.add(r.getId());
        }
        Assert.assertTrue(ids.contains("new1"));
        Assert.assertTrue(ids.contains("new2"));
        Assert.assertTrue(!ids.contains("new5"));
        Assert.assertTrue(!ids.contains("new6"));

        result = cs.getContent(cats, "/site2");
        ids = new ArrayList<String>();
        for (WCMObject r : result) {
            ids.add(r.getId());
        }
        Assert.assertTrue(ids.contains("new5"));
        Assert.assertTrue(ids.contains("new6"));
        Assert.assertTrue(!ids.contains("new1"));
        Assert.assertTrue(!ids.contains("new2"));

        result = cs.getContent(cats, "/");
        ids = new ArrayList<String>();
        for (WCMObject r : result) {
            ids.add(r.getId());
        }
        Assert.assertTrue(ids.contains("new1"));
        Assert.assertTrue(ids.contains("new2"));
        Assert.assertTrue(ids.contains("new5"));
        Assert.assertTrue(ids.contains("new6"));
        Assert.assertTrue(!ids.contains("new3"));
        Assert.assertTrue(!ids.contains("new4"));
        Assert.assertTrue(!ids.contains("new7"));
        Assert.assertTrue(!ids.contains("new8"));

        cats = cs.getCategories("/countries");
        result = cs.getContent(cats, "/");
        ids = new ArrayList<String>();
        for (WCMObject r : result) {
            ids.add(r.getId());
        }
        Assert.assertTrue(ids.contains("new3"));
        Assert.assertTrue(ids.contains("new4"));
        Assert.assertTrue(ids.contains("new7"));
        Assert.assertTrue(ids.contains("new8"));
        Assert.assertTrue(!ids.contains("new1"));
        Assert.assertTrue(!ids.contains("new2"));
        Assert.assertTrue(!ids.contains("new5"));
        Assert.assertTrue(!ids.contains("new6"));

        // Cleaning test
        cs.deleteContent("/site1");
        cs.deleteContent("/site2");
        cs.deleteContent("/site3");
        cs.deleteCategory("/news");
        cs.deleteCategory("/countries");

        cs.closeSession();
    }

    @Test
    public void createComment() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("testcomment", "/", "This is a text with comments");
        WCMTextDocument c = (WCMTextDocument)cs.createContentComment("/testcomment", "This is comment 1");
        Assert.assertEquals("This is comment 1", c.getComments().get(0).getComment());

        c = (WCMTextDocument)cs.createContentComment("/testcomment", "This is comment 2");
        Assert.assertEquals(2, c.getComments().size());

        cs.deleteContent("/testcomment");
        cs.closeSession();
    }

    @Test
    public void createCommentFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createContentComment("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentComment(null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createCommentFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.closeSession();

        boolean fail = false;
        try {
            cs.createContentComment("/thispathdoesntexist", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteComment() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("testcomment2", "/", "This is a text with comments");
        WCMTextDocument d = (WCMTextDocument)cs.createContentComment("/testcomment2", "This is comment 1");
        WCMComment c = d.getComments().get(0);

        Assert.assertEquals("This is comment 1", c.getComment());
        cs.deleteContentComment("/testcomment2", c.getId());

        d = (WCMTextDocument)cs.createContentComment("/testcomment2", "This is comment 2");
        Assert.assertEquals(1, d.getComments().size());
        c = d.getComments().get(0);
        Assert.assertEquals("This is comment 2", c.getComment());

        cs.deleteContent("/testcomment2");
        cs.closeSession();
    }

    @Test
    public void createContentProperty() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("testproperty1", "/", "This is a text with properties");
        cs.createContentProperty("/testproperty1", "title", "This is the title");
        cs.createContentProperty("/testproperty1", "description", "This is a description");
        WCMTextDocument obj = (WCMTextDocument) cs.createContentProperty("/testproperty1", "kpi", "10");

        Assert.assertEquals("This is the title", obj.getProperties().get("title"));
        Assert.assertEquals("This is a description", obj.getProperties().get("description"));
        Assert.assertEquals("10", obj.getProperties().get("kpi"));
        Assert.assertTrue(obj.getProperties().get("thisdoesntexist") == null);

        cs.deleteContent("/testproperty1");
        cs.closeSession();
    }

    @Test
    public void createContentPropertyFailsfIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createContentProperty("/dummy", "dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentProperty("/dummy", null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentProperty(null, "dummy", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createContentPropertyFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createContentProperty("/thispathdoesntexist", "dummy", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateContentProperty() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("testproperty2", "/", "This is a text with properties");
        cs.createContentProperty("/testproperty2", "title", "This is the title");
        cs.createContentProperty("/testproperty2", "description", "This is a description");
        cs.createContentProperty("/testproperty2", "kpi", "10");
        WCMTextDocument obj = (WCMTextDocument) cs.updateContentProperty("/testproperty2", "description", "This is an UPDATED description");

        Assert.assertEquals("This is the title", obj.getProperties().get("title"));
        Assert.assertEquals("This is an UPDATED description", obj.getProperties().get("description"));
        Assert.assertEquals("10", obj.getProperties().get("kpi"));
        Assert.assertTrue(obj.getProperties().get("thisdoesntexist") == null);

        cs.deleteContent("/testproperty2");
        cs.closeSession();
    }

    @Test
    public void updateContentPropertyFailsfIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.updateContentProperty("/dummy", "dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.updateContentProperty("/dummy", null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.updateContentProperty(null, "dummy", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void updateContentPropertyFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.updateContentProperty("/thispathdoesntexist", "dummy", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteContentProperty() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createTextDocument("testproperty3", "/", "This is a text with properties");
        cs.createContentProperty("/testproperty3", "title", "This is the title");
        cs.createContentProperty("/testproperty3", "description", "This is a description");
        WCMTextDocument obj = (WCMTextDocument) cs.createContentProperty("/testproperty3", "kpi", "10");

        Assert.assertEquals("This is the title", obj.getProperties().get("title"));
        Assert.assertEquals("This is a description", obj.getProperties().get("description"));
        Assert.assertEquals("10", obj.getProperties().get("kpi"));
        obj = (WCMTextDocument) cs.deleteContentProperty("/testproperty3", "kpi");

        Assert.assertFalse(obj.getProperties().get("kpi") != null);

        cs.deleteContent("/testproperty3");
        cs.closeSession();
    }

    @Test
    public void deleteContentPropertyFailsfIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.deleteContentProperty("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.deleteContentProperty(null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createContentAce() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createFolder("createacl", "/");

        WCMObject c = cs.createContentAce("/createacl", "guess", WCMPrincipalType.USER, WCMPermissionType.READ);
        Assert.assertEquals("guess", c.getAcl().getAces().get(0).getPrincipal().getId());
        Assert.assertEquals(WCMPrincipalType.USER, c.getAcl().getAces().get(0).getPrincipal().getType());
        Assert.assertEquals(WCMPermissionType.READ, c.getAcl().getAces().get(0).getPermission());

        cs.deleteContent("/createacl");
        cs.closeSession();
    }

    @Test
    public void createContentAceFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createContentAce("/dummy", "guess", WCMPrincipalType.USER, null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentAce("/dummy", "guess", null, WCMPermissionType.READ);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentAce("/dummy", null, WCMPrincipalType.USER, WCMPermissionType.READ);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.createContentAce(null, "guess", WCMPrincipalType.USER, WCMPermissionType.READ);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void createContentAceFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.createContentAce("/thispathdoesntexist", "guess", WCMPrincipalType.USER, WCMPermissionType.READ);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteContentAce() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createFolder("deleteacl", "/");

        WCMObject c = cs.createContentAce("/deleteacl", "guess", WCMPrincipalType.USER, WCMPermissionType.READ);
        Assert.assertEquals("guess", c.getAcl().getAces().get(0).getPrincipal().getId());
        Assert.assertEquals(WCMPrincipalType.USER, c.getAcl().getAces().get(0).getPrincipal().getType());
        Assert.assertEquals(WCMPermissionType.READ, c.getAcl().getAces().get(0).getPermission());
        c = cs.deleteContentAce("/deleteacl", "guess");

        // It should take the ACL from their parent
        Assert.assertFalse(c.getAcl() == null);
        Assert.assertEquals(c.getParentPath(), c.getAcl().getId());

        cs.deleteContent("/deleteacl");
        cs.closeSession();
    }

    @Test
    public void deleteContentAceFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.deleteContentAce("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.deleteContentAce(null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteContentAceFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.deleteContentAce("/thispathdoesntexist", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getVersions() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        WCMTextDocument doc = cs.createTextDocument("testversions", "/", "This is Version 1");

        Assert.assertEquals(1, cs.getVersions("/testversions").size());

        doc.setContent("This is Version 2");
        doc = cs.updateTextDocument("/testversions", doc);

        Assert.assertEquals(2, cs.getVersions("/testversions").size());

        doc.setContent("This is Version 3");
        doc = cs.updateTextDocument("/testversions", doc);

        Assert.assertEquals(3, cs.getVersions("/testversions").size());

        doc.setContent("This is Version 4");
        doc = cs.updateTextDocument("/testversions", doc);

        Assert.assertEquals(4, cs.getVersions("/testversions").size());

        cs.deleteContent("/testversions");
        cs.closeSession();
    }

    @Test
    public void getVersionsFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getVersions(null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getVersionsFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.getVersions("/thispathdoesntexist");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void getVersionsNullWithFolder() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createFolder("testversions2", "/");

        Assert.assertEquals(null, cs.getVersions("/testversions2"));

        cs.deleteContent("/testversions2");
        cs.closeSession();
    }

    @Test
    public void restore() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        // Version 1.0
        WCMTextDocument doc = cs.createTextDocument("testversions3", "/", "This is Version 1");

        // Version 1.1
        doc.setContent("This is Version 2");
        doc = cs.updateTextDocument("/testversions3", doc);

        // Version 1.2
        doc.setContent("This is Version 3");
        doc = cs.updateTextDocument("/testversions3", doc);

        // Version 1.3
        doc.setContent("This is Version 4");
        doc = cs.updateTextDocument("/testversions3", doc);

        cs.restore("/testversions3", "1.0");
        doc = (WCMTextDocument) cs.getContent("/testversions3");
        Assert.assertEquals("This is Version 1", doc.getContent());

        cs.restore("/testversions3", "1.1");
        doc = (WCMTextDocument) cs.getContent("/testversions3");
        Assert.assertEquals("This is Version 2", doc.getContent());

        cs.restore("/testversions3", "1.2");
        doc = (WCMTextDocument) cs.getContent("/testversions3");
        Assert.assertEquals("This is Version 3", doc.getContent());

        cs.restore("/testversions3", "1.3");
        doc = (WCMTextDocument) cs.getContent("/testversions3");
        Assert.assertEquals("This is Version 4", doc.getContent());

        cs.deleteContent("/testversions3");
        cs.closeSession();
    }

    @Test
    public void restoreFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.restore("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.restore(null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void restoreFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.restore("/thispathdoesntexist", "1.0");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void restoreIsTransparentForFolders() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        cs.createFolder("testversions4", "/");
        boolean fail = false;
        try {
            cs.restore("/testversions4", "1.0");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.deleteContent("/testversions4");
        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteContentVersion() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        // Version 1.0
        WCMTextDocument doc = cs.createTextDocument("testversions5", "/", "This is Version 1");

        // Version 1.1
        doc.setContent("This is Version 2");
        doc = cs.updateTextDocument("/testversions5", doc);

        // Version 1.2
        doc.setContent("This is Version 3");
        doc = cs.updateTextDocument("/testversions5", doc);

        // Version 1.3
        doc.setContent("This is Version 4");
        doc = cs.updateTextDocument("/testversions5", doc);
        Assert.assertEquals(4, cs.getVersions("/testversions5").size());

        cs.deleteContentVersion("/testversions5", "1.2");
        Assert.assertEquals(3, cs.getVersions("/testversions5").size());

        cs.deleteContentVersion("/testversions5", "1.1");
        Assert.assertEquals(2, cs.getVersions("/testversions5").size());

        cs.deleteContentVersion("/testversions5", "1.0");
        Assert.assertEquals(1, cs.getVersions("/testversions5").size());

        cs.deleteContent("/testversions5");
        cs.closeSession();
    }

    @Test
    public void deleteContentVersionFailsIfNullArguments() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.deleteContentVersion("/dummy", null);
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        try {
            cs.deleteContentVersion(null, "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    @Test
    public void deleteContentVersionFailsIfPathDoesntExist() throws Exception {
        cs = repos.createContentSession("admin", "admin");

        boolean fail = false;
        try {
            cs.deleteContentVersion("/thispathdoesntexist", "dummy");
        } catch (Exception e) {
            // Expected to fail...
            fail = true;
        }

        cs.closeSession();

        if (!fail)
            Assert.assertFalse(true);
    }

    /**
     * Transform an InputStream in a byte[] buffer.
     *
     * @param is - InputStream
     * @return byte[] buffer with raw InputStream's content
     */
    private byte[] toByteArray(InputStream is) {
        byte[] data = new byte[16384];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.close();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}