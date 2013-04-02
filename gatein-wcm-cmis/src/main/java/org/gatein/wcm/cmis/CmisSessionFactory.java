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

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.modeshape.cmis.JcrServiceFactory;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public class CmisSessionFactory {

    private static final CmisSessionFactory INSTANCE = new CmisSessionFactory();

    public static CmisSessionFactory getInstance() {
        return INSTANCE;
    }

    public Session createSession(String user, String password) {
        SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
        Map<String, String> params = new HashMap<String, String>();

        params.put(SessionParameter.USER, user);
        params.put(SessionParameter.PASSWORD, password);
        params.put(SessionParameter.BINDING_TYPE, BindingType.LOCAL.value());
        params.put(SessionParameter.LOCAL_FACTORY, JcrServiceFactory.class.getName());
        params.put(SessionParameter.REPOSITORY_ID, "artifacts:default");

        return factory.createSession(params);
    }

}
