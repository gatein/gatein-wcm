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

package org.gatein.wcm.portlet.content;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.domain.UserWcm;
import org.gatein.wcm.portlet.content.config.ConfigActions;
import org.gatein.wcm.portlet.content.render.RenderActions;
import org.gatein.wcm.services.PortalService;

import javax.inject.Inject;
import javax.portlet.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * GateIn WCM Content Portlet.
 * Show content rendered using templates
 *
 * @see Wcm.VIEWS
 * @see Wcm.ACTIONS
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ContentPortlet extends GenericPortlet {
    private static final Logger log = Logger.getLogger(ContentPortlet.class.getName());

    @Inject
    private PortalService portal;

    @Inject
    private ConfigActions config;

    @Inject
    private RenderActions render;

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        UserWcm userWcm = null;
        try {
            String user = (request.getUserPrincipal() != null?request.getUserPrincipal().getName():null);
            userWcm = portal.getPortalUser(user);
            if (userWcm == null) {
                userWcm = new UserWcm("anonymous");
            }
        } catch (WcmException e) {
            log.warning("Cannot access Portal User interface.");
            e.printStackTrace();
        }
        request.setAttribute("userWcm", userWcm);

        String profile = render.renderTemplate(request, response, userWcm);

        String url = "/jsp/content/render/contentRead.jsp";

        if (profile != null && profile.equals("editor")) {
            url = "/jsp/content/render/contentEdit.jsp";
        } else if (profile != null && profile.equals("write")) {
            url = "/jsp/content/render/contentWrite.jsp";
        }

        PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(url);
        prd.include(request, response);
    }

    @Override
    protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        UserWcm userWcm = null;
        try {
            String user = (request.getUserPrincipal() != null?request.getUserPrincipal().getName():null);
            userWcm = portal.getPortalUser(user);
        } catch (WcmException e) {
            log.warning("Cannot access Portal User interface.");
            e.printStackTrace();
        }

        String url = config.viewConfig(request, response, userWcm);

        PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(url);
        prd.include(request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        UserWcm userWcm = null;
        try {
            String user = (request.getUserPrincipal() != null?request.getUserPrincipal().getName():null);
            userWcm = portal.getPortalUser(user);
        } catch (WcmException e) {
            log.warning("Cannot access Portal User interface.");
            e.printStackTrace();
        }

        String action = request.getParameter("action");
        if (action != null) {
            if (action.equals(Wcm.CONFIG.ACTIONS.SAVE_CONFIGURATION)) {
                config.actionSaveConfig(request, response, userWcm);
            } else if (action.equals(Wcm.CONTENT.ACTIONS.INLINE_EDITOR)) {
              String activeEditor = request.getParameter("activeEditor");
              String editId = request.getParameter("editid");
              String lockMsg = render.checkLock(new Long(editId), userWcm);
              if (activeEditor != null && lockMsg == null) {
                request.getPortletSession().setAttribute("activeEditor", activeEditor);
              }
              request.getPortletSession().setAttribute("lockMsg", lockMsg);
            } else {
                // No default action
            }
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        UserWcm userWcm = null;
        try {
            String user = (request.getUserPrincipal() != null?request.getUserPrincipal().getName():null);
            userWcm = portal.getPortalUser(user);
            if (userWcm == null) {
                userWcm = new UserWcm("anonymous");
            }
        } catch (WcmException e) {
            log.warning("Cannot access Portal User interface.");
            e.printStackTrace();
        }

        String event = request.getParameter("event");
        if (event != null) {
            String url = null;
            if (event.equals(Wcm.CONFIG.EVENTS.CHANGE_CHOOSE_CONTENT)) {
                url = config.eventChangeChooseContent(request, response, userWcm);
            } else if (event.equals(Wcm.CONFIG.EVENTS.NEW_CONTENT_ATTACHED)) {
                url = config.eventNewContentAttached(request, response, userWcm);
            } else if (event.equals(Wcm.CONFIG.EVENTS.DELETE_CONTENT_ATTACHED)) {
                url = config.eventDeleteContentAttached(request, response, userWcm);
            } if (event.equals(Wcm.EVENTS.SHOW_POST_UPLOADS)) {
                // Show list uploads
                url = render.eventShowPostUploads(request, response, userWcm);
            } if (event.equals(Wcm.EVENTS.UPDATE_CONTENT_POST)) {
                // Updating content
                render.eventUpdateContent(request, response, userWcm);
            } if (event.equals(Wcm.EVENTS.ADD_COMMENT_POST)) {
              // Updating comment post
                render.eventAddCommentPost(request, response, userWcm);
            } else if (event.equals(Wcm.EVENTS.UNLOCK_POST)) {
                // Unlock post when user leaves page
                render.eventUnlockPost(request, response, userWcm);
            } else {
                // No default event
            }
            if (url != null) {
                PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(url);
                prd.include(request, response);
            }
        }
    }
}
