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

import org.exoplatform.services.organization.Membership;
import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.domain.UserWcm;
import org.gatein.wcm.services.PortalService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Group;

/**
 * Implementation of PortalService public API.
 * Injected as plain object.
 * No transactions needed.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Stateless
public class PortalServiceImpl implements PortalService {

    private static final Logger log = Logger.getLogger(PortalServiceImpl.class.getName());

    /*
        Read operation, no need to enlist this transaction in the wcm transaction context
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserWcm getPortalUser(String user) throws WcmException {
        if (user == null) return null;
        OrganizationService os = (OrganizationService)PortalContainer
                .getInstance()
                .getComponentInstanceOfType(OrganizationService.class);
        if (os == null) {
            throw new WcmException("Cannot retrieve OrganizationService from PortalContainer.");
        }
        UserWcm userWcm = new UserWcm(user);
        try {
            Collection groups = os.getGroupHandler().findGroupsOfUser(user);
            for (Object o : groups) {
                Group g = (Group)o;
                // We only return wcm groups
                if (g.getId().startsWith(Wcm.GROUPS.WCM)) {
                    userWcm.add(g.getId());
                    if (g.getId().equals(Wcm.GROUPS.WCM)) {
                        try {
                            Membership m = os.getMembershipHandler().findMembershipByUserGroupAndType(user, g.getId(), Wcm.GROUPS.MANAGER);
                            if (m != null) userWcm.setManager(true);
                        } catch (Exception e) {
                            log.warning("Error querying user");
                            e.printStackTrace();
                        }
                    }
                }
            }
            // If user has not valid group we assign a default group
            if (userWcm.getWriteGroups().isEmpty()) {
                userWcm.add(Wcm.GROUPS.LOST);
                log.warning("User: " + userWcm + " has not a valid writable group. Assigning to default group: " + Wcm.GROUPS.LOST);
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
        return userWcm;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Set<String> getWcmGroups() throws WcmException {
        Set<String> groups = new HashSet<String>();
        try {
            OrganizationService os = (OrganizationService)PortalContainer
                    .getInstance()
                    .getComponentInstanceOfType(OrganizationService.class);

            Group wcmGroup = os.getGroupHandler().findGroupById(Wcm.GROUPS.WCM);
            Collection children = os.getGroupHandler().findGroups(wcmGroup);
            Iterator iChildren = children.iterator();
            while (iChildren.hasNext()) {
                Group g = (Group)iChildren.next();
                groups.add(g.getId());
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
        return groups;
    }
}
