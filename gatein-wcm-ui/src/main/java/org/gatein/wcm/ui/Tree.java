package org.gatein.wcm.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.ui.model.TreeContent;
import org.jboss.logging.Logger;

@ManagedBean
@RequestScoped
public class Tree extends BaseBean {
    private static final Logger log = Logger.getLogger("org.gatein.wcm.ui");

    @ManagedProperty(value="#{connect}")
    private Connect connect;

    private List<TreeContent> root;

    private String description;

    public Tree() {
        log.info("Tree: creating new class");
        description = new java.util.Date().toString();
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public String getDescription() {
        if (connect != null) {
            log.info("Connected: " + connect.isConnected());
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TreeContent> getRoot() {
        if (root == null) queryRoot();
        return root;
    }

    // Logic

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;


    private void queryRoot() {
        if (connect == null || !connect.isConnected()) return;
        try {
            ContentService cs = repos.createContentSession(connect.getRepository(), connect.getWorkspace(), connect.getUser(), connect.getPassword());
            Content rootContent = cs.getContent("/", getLocale());
            TreeContent r = new TreeContent(rootContent);
            root = new ArrayList<TreeContent>();
            root.add(r);
        } catch (ContentException e) {
            msg("Cannot get root content from " + connect.getRepository() + "/" + connect.getWorkspace());
            log.info("Cannot get root content from " + connect.getRepository() + "/" + connect.getWorkspace());
        } catch (ContentIOException e) {
            msg("Cannot connect with repository " + connect.getRepository());
            log.error(e.getMessage(), e);
            connect.setConnected(false);
        } catch (ContentSecurityException e) {
            msg("User " + connect.getUser() + " incorrect for " + connect.getRepository() + "/" + connect.getWorkspace() + "");
            log.warn("ContentSecurityException for " + connect.getUser() + " repository " + connect.getRepository() + " workspace " + connect.getWorkspace() + ". Msg: " + e.getMessage());
            connect.setConnected(false);
        }
    }


}
