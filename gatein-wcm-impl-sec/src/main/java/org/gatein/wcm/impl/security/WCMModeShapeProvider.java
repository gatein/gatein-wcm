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
package org.gatein.wcm.impl.security;

import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.jboss.logging.Logger;
import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthenticationProvider;

/**
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMModeShapeProvider implements AuthenticationProvider {

    private static final Logger log = Logger.getLogger(WCMModeShapeProvider.class);

    public ExecutionContext authenticate(Credentials credentials, String repositoryName, String workspaceName,
            ExecutionContext repositoryContext, Map<String, Object> sessionAttributes) {

        try {
            if (credentials instanceof SimpleCredentials) {
                SimpleCredentials sCredentials = (SimpleCredentials) credentials;
                return repositoryContext.with(new WCMSecurityContext(sCredentials));
            }

        } catch (LoginException e) {
            log.debug(e.toString(), e);
            return null;
        }
        return null;
    }
}
