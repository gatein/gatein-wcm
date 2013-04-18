package org.gatein.wcm.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmFolder;
import org.gatein.wcm.api.services.WcmContentService;
import org.gatein.wcm.api.services.WcmRepositoryService;
import org.gatein.wcm.api.services.exceptions.WcmContentException;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.gatein.wcm.ui.model.TreeContent;
import org.jboss.logging.Logger;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

@ManagedBean
@RequestScoped
public class Tree extends BaseBean {
    private static final Logger log = Logger.getLogger(Tree.class);

    @ManagedProperty(value="#{connect}")
    private Connect connect;

    @ManagedProperty(value="#{panel}")
    private Panel panel;

    private List<TreeContent> root;

    private Date ping;

    public Tree() {
        ping = new java.util.Date();
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public Date getPing() {
        return ping;
    }

    public void setPing(Date ping) {
        this.ping = ping;
    }

    public List<TreeContent> getRoot() {
        if (root == null) queryRoot();
        return root;
    }

    public Panel getPanel() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }

    // Logic

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WcmRepositoryService repos;


    private void queryRoot() {
        if (connect == null || !connect.isConnected()) return;
        try {
            WcmContentService cs = repos.createContentSession(connect.getRepository(), connect.getWorkspace(), connect.getUser(), connect.getPassword());
            WcmObject rootContent = cs.getContent("/", getLocale());
            root = new ArrayList<TreeContent>();
            // We don't show root node in the UI, only children
            if (rootContent instanceof WcmFolder) {
                WcmFolder f = (WcmFolder)rootContent;
                for (WcmObject c : f.getChildren()) {
                    root.add(new TreeContent(c));
                }
            }
        } catch (WcmContentException e) {
            msg("Cannot get root content from " + connect.getRepository() + "/" + connect.getWorkspace());
            log.info("Cannot get root content from " + connect.getRepository() + "/" + connect.getWorkspace());
        } catch (WcmContentIOException e) {
            msg("Cannot connect with repository " + connect.getRepository());
            log.error(e.getMessage(), e);
            connect.setConnected(false);
        } catch (WcmContentSecurityException e) {
            msg("User " + connect.getUser() + " incorrect for " + connect.getRepository() + "/" + connect.getWorkspace() + "");
            log.warn("ContentSecurityException for " + connect.getUser() + " repository " + connect.getRepository() + " workspace " + connect.getWorkspace() + ". Msg: " + e.getMessage());
            connect.setConnected(false);
        }
    }

    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();
        tree.setRowKey(currentSelectionKey);

        TreeContent currentSelection = (TreeContent)tree.getRowData();
        panel.setSelected(currentSelection);
    }

}