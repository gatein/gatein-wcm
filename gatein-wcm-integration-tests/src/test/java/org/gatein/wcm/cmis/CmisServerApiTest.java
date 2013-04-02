/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.gatein.wcm.cmis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.jcr.JcrTypeManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
@RunWith(Arquillian.class)
public class CmisServerApiTest {


    //private static final Logger log = Logger.getLogger(CmisServerApiTest.class);

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, CmisServerApiTest.class.getSimpleName() + ".war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Test
    public void shouldAccessRootFolder() throws Exception {
        Session session = CmisSessionFactory.getInstance().createSession("admin", "admin");
        Folder root = session.getRootFolder();
        Assert.assertNotNull(root);
    }

    @Test
    public void createAndDeleteFolder() throws Exception {
        Session session = CmisSessionFactory.getInstance().createSession("admin", "admin");
        Folder root = session.getRootFolder();

        /* ensure a valid initial state */
        Assert.assertFalse(root.getChildren().getHasMoreItems());

        try {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, JcrTypeManager.FOLDER_TYPE_ID);
            properties.put(PropertyIds.NAME, "f1");
            Folder f1 = root.createFolder(properties);
            Assert.assertNotNull(f1);

            CmisObject repoF1 = root.getChildren().iterator().next();
            if (!(repoF1 instanceof Folder)) {
                Assert.fail("repoF1 expected to be an instance of "+ Folder.class.getName() + " but was "+ repoF1.getClass().getName());
            }
        } finally {
            /* cleanup */
            deleteAll(root);
        }

    }

    private static void deleteAll(Folder root) {
        for (CmisObject i : root.getChildren()) {
            i.delete(true);
        }
        Assert.assertFalse(root.getChildren().getHasMoreItems());
    }

// Commented our because of https://issues.apache.org/jira/browse/CMIS-618 https://issues.jboss.org/browse/MODE-1749
//    @Test
//    public void createAndDeleteDocument() throws Exception {
//
//        final String docName = "d1.txt";
//
//        Session session = CmisSessionFactory.getInstance().createSession("admin", "admin");
//        Folder root = session.getRootFolder();
//
//        /* ensure a valid initial state */
//        Assert.assertFalse(root.getChildren().getHasMoreItems());
//
//        try {
//            byte[] content = "Hello World!".getBytes();
//            InputStream stream = new ByteArrayInputStream(content);
//            ContentStream contentStream = new ContentStreamImpl(docName, BigInteger.valueOf(content.length), "text/plain", stream);
//
//            Map<String, Object> properties = new HashMap<String, Object>();
//            properties.put(PropertyIds.OBJECT_TYPE_ID, JcrTypeManager.DOCUMENT_TYPE_ID);
//            properties.put(PropertyIds.NAME, docName);
//            Document d1 = root.createDocument(properties, contentStream, VersioningState.MAJOR);
//            Assert.assertNotNull(d1);
//
//            CmisObject repoD1 = root.getChildren().iterator().next();
//            if (!(repoD1 instanceof Document)) {
//                Assert.fail("repoD1 expected to be an instance of "+ Document.class.getName() + " but was "+ repoD1.getClass().getName());
//            }
//        } finally {
//            /* cleanup */
//            deleteAll(root);
//        }
//
//    }

}
