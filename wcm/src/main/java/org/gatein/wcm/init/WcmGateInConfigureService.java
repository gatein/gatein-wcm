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

package org.gatein.wcm.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.application.registry.impl.ApplicationRegistryServiceImpl;
import org.exoplatform.application.registry.impl.CategoryDefinition;
import org.exoplatform.application.registry.impl.ContentDefinition;
import org.exoplatform.application.registry.impl.ContentRegistry;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Properties;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.management.operations.page.PageUtils;
import org.exoplatform.portal.mop.navigation.NavigationContext;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.mop.navigation.NavigationState;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.exoplatform.portal.mop.navigation.NodeModel;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageState;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.federation.FederatingPortletInvoker;
import org.gatein.wcm.Wcm;

/**
 * Accesses to GateIn Services to register Wcm application
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class WcmGateInConfigureService {
    private static final Logger log = Logger.getLogger(WcmGateInConfigureService.class.getName());

    private static final String EMPTY_TEMPLATE = "empty";

    public static void initGateInConfiguration() {
        log.info("Started - Installing Wcm app into GateIn.");
        configureWcmGroups();
        configureWcmPortlets();
        configureWcmNavigation();
        log.info("Finished - Installed Wcm app into GateIn.");
    }

    public static void configureWcmGroups() {
        ExoContainer manager = ExoContainerContext.getCurrentContainer();
        OrganizationService orgService = (OrganizationService) PortalContainer
                .getInstance()
                .getComponentInstanceOfType(OrganizationService.class);

        String wcmGroupName = Wcm.GROUPS.WCM;
        if (wcmGroupName == null || wcmGroupName.length() == 0 || wcmGroupName.charAt(0) != '/') {
            log.warning("Invalid Wcm group: " + wcmGroupName + ". Please, review wcm.properties");
            return;
        }

        Group wcmGroup;
        Group editorGroup = null;
        User root;
        MembershipType membershipManager = null;
        try {
            RequestLifeCycle.begin(manager);
            wcmGroup = orgService.getGroupHandler().findGroupById(wcmGroupName);

            // Creates a WCM group only if it doesn't exist

            if (wcmGroup == null) {
                log.info("Configuring Wcm groups into GateIn...");

                wcmGroup = orgService.getGroupHandler().createGroupInstance();
                wcmGroup.setGroupName(wcmGroupName.substring(1));
                wcmGroup.setDescription("GateIn WCM");
                orgService.getGroupHandler().addChild(null, wcmGroup, true);
                wcmGroup = orgService.getGroupHandler().findGroupById(wcmGroupName);

                // Creates a default editor group

                String wcmEditorName = Wcm.GROUPS.EDITOR;
                if (wcmEditorName != null
                        && wcmEditorName.length() > 0
                        && wcmEditorName.lastIndexOf('/') > -1
                        && wcmEditorName.substring(wcmEditorName.lastIndexOf('/')).length() > 0) {
                    editorGroup = orgService.getGroupHandler().createGroupInstance();
                    editorGroup.setGroupName(wcmEditorName.substring(wcmEditorName.lastIndexOf('/') + 1));
                    orgService.getGroupHandler().addChild(wcmGroup, editorGroup, true);
                    editorGroup = orgService.getGroupHandler().findGroupById(wcmEditorName);
                }

                // Grant root user in wcm groups

                root = orgService.getUserHandler().findUserByName("root");
                membershipManager = orgService.getMembershipTypeHandler().findMembershipType(Wcm.GROUPS.MANAGER);
                if (root != null && membershipManager != null) {
                    orgService.getMembershipHandler().linkMembership(root, wcmGroup, membershipManager, true);
                    orgService.getMembershipHandler().linkMembership(root, editorGroup, membershipManager, true);
                }
            }

        } catch (Exception e) {
            log.warning("Error accesing GateIn groups. Msg: " + e.getMessage());
        } finally {
            RequestLifeCycle.end();
        }
    }

    public static void configureWcmPortlets() {
        ExoContainer manager = ExoContainerContext.getCurrentContainer();
        ApplicationRegistryService appRegistryService = (ApplicationRegistryService)manager.getComponentInstanceOfType(ApplicationRegistryService.class);

        if (appRegistryService != null) {
            try {
                RequestLifeCycle.begin(manager);

                // Search for wcm category

                List<ApplicationCategory> categories = appRegistryService.getApplicationCategories();
                boolean catFound = false;
                for (ApplicationCategory cat : categories) {
                    if (cat.getName() != null && cat.getName().equals(Wcm.APPS.CATEGORY)) {
                        catFound = true;
                        break;
                    }
                }

                // Creates wcm category if doesn't exist

                if (!catFound) {
                    log.info("Configuring Wcm portlets into GateIn...");

                    // Search for wcm portlets to add them into category

                    FederatingPortletInvoker portletInvoker = (FederatingPortletInvoker) manager
                            .getComponentInstance(FederatingPortletInvoker.class);
                    Set<Portlet> portlets = null;
                    try {
                        portlets = portletInvoker.getLocalPortlets();
                    } catch (PortletInvokerException e) {
                        log.warning("Error trying to get GateIn portlets. Msg: " + e.getMessage());
                    }

                    Portlet wcmContentPortlet = null;
                    Portlet wcmEditorPortlet = null;
                    if (portlets != null) {
                        for (Portlet p : portlets) {
                            if (p.getInfo() != null
                                    && p.getInfo().getApplicationName().equals(Wcm.APPS.NAME)
                                    && p.getInfo().getName().equals(Wcm.APPS.CONTENT)) {
                                wcmContentPortlet = p;
                            } else if (p.getInfo() != null
                                    && p.getInfo().getApplicationName().equals(Wcm.APPS.NAME)
                                    && p.getInfo().getName().equals(Wcm.APPS.EDITOR)) {
                                wcmEditorPortlet = p;
                            }
                        }
                    }

                    // Create new category

                    ApplicationCategory wcmCat = new ApplicationCategory();
                    wcmCat.setName(Wcm.APPS.CATEGORY);
                    List accessPermissions = new ArrayList();
                    accessPermissions.add(Wcm.GROUPS.ALL + ":" + Wcm.GROUPS.WCM);
                    wcmCat.setAccessPermissions(accessPermissions);
                    appRegistryService.save(wcmCat);

                    // Importing applications

                    ApplicationRegistryServiceImpl appRegistryServiceImpl = (ApplicationRegistryServiceImpl)appRegistryService;
                    ContentRegistry registry = appRegistryServiceImpl.getContentRegistry();

                    CategoryDefinition catDefinition = registry.getCategory(Wcm.APPS.CATEGORY);
                    if (catDefinition != null) {

                        // Adding WCM Content Portlet
                        PortletInfo info = wcmContentPortlet.getInfo();
                        String displayName = Wcm.APPS.CONTENT_DISPLAY;
                        String contentId = info.getApplicationName() + "/" + info.getName();

                        ContentDefinition wcmContentContent = catDefinition.createContent(displayName,
                                org.exoplatform.portal.pom.spi.portlet.Portlet.CONTENT_TYPE,
                                contentId);
                        wcmContentContent.setAccessPermissions(accessPermissions);
                        wcmContentContent.setDisplayName(displayName);

                        // Adding WCM Editor Portlet
                        info = wcmEditorPortlet.getInfo();
                        displayName = Wcm.APPS.EDITOR_DISPLAY;
                        contentId = info.getApplicationName() + "/" + info.getName();

                        ContentDefinition wcmEditorContent = catDefinition.createContent(displayName,
                                org.exoplatform.portal.pom.spi.portlet.Portlet.CONTENT_TYPE,
                                contentId);
                        wcmEditorContent.setAccessPermissions(accessPermissions);
                        wcmEditorContent.setDisplayName(displayName);
                    }
                }

            } catch (Exception e) {
                log.warning("Error trying to get GateIn categories. Msg: " + e.getMessage());
            } finally {
                RequestLifeCycle.end();
            }
        }
    }

    public static void configureWcmNavigation() {
        ExoContainer manager = ExoContainerContext.getCurrentContainer();

        NavigationService navigationService = (NavigationService)manager.getComponentInstanceOfType(NavigationService.class);
        if (navigationService != null) {
            try {
                RequestLifeCycle.begin(manager);

                NavigationContext navigation = navigationService.loadNavigation(SiteKey.group(Wcm.GROUPS.WCM));

                // Creates new wcm navigation it doesn't exist

                if (navigation == null) {
                    log.info("Configuring Wcm navigation into GateIn...");

                    // Adding new navigation for WCM group

                    DataStorage dataService = (DataStorage) manager.getComponentInstanceOfType(DataStorage.class);
                    UserPortalConfigService configService = (UserPortalConfigService) manager.getComponentInstanceOfType(UserPortalConfigService.class);

                    if (dataService != null) {
                        PortalConfig portalConfig = dataService.getPortalConfig("group", Wcm.GROUPS.WCM);
                        if (portalConfig == null) {
                            configService.createGroupSite(Wcm.GROUPS.WCM);
                        }
                    }
                    navigationService.saveNavigation(new NavigationContext(SiteKey.group(Wcm.GROUPS.WCM), new NavigationState(0)));

                    // Adding new WCM Editor page

                    String[] accessPermissions = new String[] { Wcm.GROUPS.ALL + ":" + Wcm.GROUPS.WCM };
                    String editPermissions = Wcm.GROUPS.MANAGER + ":" + Wcm.GROUPS.WCM;

                    Page wcmEditorPage = configService.createPageTemplate(EMPTY_TEMPLATE, SiteType.GROUP.getName(), Wcm.GROUPS.WCM);
                    wcmEditorPage.setName(Wcm.APPS.NAME);
                    wcmEditorPage.setTitle(Wcm.APPS.EDITOR_DISPLAY);
                    wcmEditorPage.setAccessPermissions(accessPermissions);
                    wcmEditorPage.setEditPermission(editPermissions);

                    // Create Application for WCM Editor

                    TransientApplicationState tState = new TransientApplicationState(Wcm.APPS.NAME + "/" + Wcm.APPS.EDITOR);
                    org.exoplatform.portal.config.model.Application wcmEditorApp =
                            new org.exoplatform
                                    .portal
                                    .config
                                    .model
                                    .Application<org.exoplatform.portal.pom.spi.portlet.Portlet>(ApplicationType.PORTLET);
                    wcmEditorApp.setStorageName(null);
                    wcmEditorApp.setShowApplicationState(true);
                    wcmEditorApp.setState(tState);
                    wcmEditorApp.setDescription(Wcm.APPS.EDITOR_DISPLAY);
                    wcmEditorApp.setShowInfoBar(false);
                    wcmEditorApp.setShowApplicationState(false);
                    wcmEditorApp.setShowApplicationMode(false);
                    wcmEditorApp.setProperties(new Properties());
                    wcmEditorApp.setModifiable(true);
                    wcmEditorApp.setAccessPermissions(accessPermissions);

                    wcmEditorPage.getChildren().add(wcmEditorApp);

                    PageState wcmEditorPageState = PageUtils.toPageState(wcmEditorPage);

                    configService.getPageService().savePage(new PageContext(wcmEditorPage.getPageKey(), wcmEditorPageState));
                    dataService.save(wcmEditorPage);

                    // Adding new navigation node

                    navigation = navigationService.loadNavigation(SiteKey.group(Wcm.GROUPS.WCM));

                    String wcmNodeName = Wcm.APPS.EDITOR_DISPLAY;

                    NodeContext rootNode = navigationService.loadNode(NodeModel.SELF_MODEL, navigation, Scope.ALL, null);
                    NodeContext childNode = rootNode.add(0, wcmNodeName);
                    childNode.setName(wcmNodeName);

                    NodeState nodeState = NodeState.INITIAL
                            .builder()
                            .pageRef(wcmEditorPage.getPageKey())
                            .build();
                    childNode.setState(nodeState);

                    navigationService.saveNode(childNode, null);
                    navigationService.saveNavigation(navigation);
                }
            } catch (Exception e) {
                log.warning("Error trying to get GateIn navigation. Msg: " + e.getMessage());
            } finally {
                RequestLifeCycle.end();
            }
        }
    }
}
