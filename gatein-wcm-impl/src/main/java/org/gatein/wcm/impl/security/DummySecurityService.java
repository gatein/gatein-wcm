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

import org.gatein.wcm.api.model.security.WCMUser;
import org.gatein.wcm.api.services.WCMSecurityService;
import org.gatein.wcm.api.services.exceptions.WCMContentSecurityException;
import org.gatein.wcm.impl.model.WCMContentFactory;
import org.jboss.logging.Logger;

/**
 *
 * Dummy Service facade to simulate an interaction with the Portal's authentication system.
 * <p>
 * In this case we are going to create the following users for testing:
 * <p>
 *  <li>User/password: admin/admin    Roles:   admin             <br>
 *  <li>User/password: user1/gtn      Roles:   readwrite,europe    <br>
 *  <li>User/password: user2/gtn      Roles:   europe            <br>
 *  <li>User/password: user3/gtn      Roles:   readwrite,america   <br>
 *  <li>User/password: user4/gtn      Roles:   america           <br>
 * <p>
 * ModeShape needs "admin" or "readwrite" roles to create a writable session. <br>
 * Other roles are used by WCM for fine grained ACL. <br>
 * <p>
 * This class will be replaced for another that connects to underlaying security subsystem.
 * <p>
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class DummySecurityService implements WCMSecurityService {

    private static final Logger log = Logger.getLogger(DummySecurityService.class);

    public WCMUser authenticate(String idUser, String password) throws WCMContentSecurityException {

        log.debug("Authenticating user... " + idUser);

        WCMUser user = null;

        if ("admin".equals(idUser)) {
            if (!"admin".equals(password))
                throw new WCMContentSecurityException("Bad password for user " + idUser);
            String[] roles = {"admin"};
            user = WCMContentFactory.createUserInstance("admin", roles);
            user.setPassword(password);
        }

        if ("user1".equals(idUser)) {
            if (!"gtn".equals(password))
                throw new WCMContentSecurityException("Bad password for user " + idUser);
            String[] roles = {"readwrite", "europe"};
            user = WCMContentFactory.createUserInstance("user1", roles);
            user.setPassword(password);
        }

        if ("user2".equals(idUser)) {
            if (!"gtn".equals(password))
                throw new WCMContentSecurityException("Bad password for user " + idUser);
            String[] roles = {"europe"};
            user = WCMContentFactory.createUserInstance("user2", roles);
            user.setPassword(password);
        }

        if ("user3".equals(idUser)) {
            if (!"gtn".equals(password))
                throw new WCMContentSecurityException("Bad password for user " + idUser);
            String[] roles = {"readwrite", "america"};
            user = WCMContentFactory.createUserInstance("user3", roles);
            user.setPassword(password);
        }

        if ("user4".equals(idUser)) {
            if (!"gtn".equals(password))
                throw new WCMContentSecurityException("Bad password for user " + idUser);
            String[] roles = {"america"};
            user = WCMContentFactory.createUserInstance("user4", roles);
            user.setPassword(password);
        }

        if (user == null)
            throw new WCMContentSecurityException("User " + idUser + " doesn't found on DummySecurityService");
        return user;
    }
}