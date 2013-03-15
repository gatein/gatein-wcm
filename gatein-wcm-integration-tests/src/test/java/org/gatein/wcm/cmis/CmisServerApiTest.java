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

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.cmis.JcrServiceFactory;

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

    private Session session;

    @Before
    public void setUp() {

        SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.PASSWORD, "admin");

        // connection settings
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.LOCAL.value());
        parameter.put(SessionParameter.LOCAL_FACTORY, JcrServiceFactory.class.getName());
        parameter.put(SessionParameter.REPOSITORY_ID, "artifacts:default");
        // create session
        session = factory.createSession(parameter);
    }

    @Test
    public void shouldAccessRootFolder() throws Exception {
        Folder root = session.getRootFolder();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, "f55");
        //System.out.println("Root: " + root);
        root.createFolder(properties);
    }

}
