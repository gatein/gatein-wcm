package org.gatein.wcm.ui;

import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.gatein.wcm.api.services.RepositoryService;
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
        return root;
    }

    // Logic

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;



}
