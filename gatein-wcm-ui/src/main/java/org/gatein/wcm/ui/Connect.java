package org.gatein.wcm.ui;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.gatein.wcm.api.services.WcmRepositoryService;
import org.gatein.wcm.api.services.exceptions.WcmContentIOException;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;
import org.jboss.logging.Logger;

@ManagedBean
@SessionScoped
public class Connect extends BaseBean implements Serializable {
    private static final long serialVersionUID = 3166659291670634657L;

    private static final Logger log = Logger.getLogger(Connect.class);

    private String repository;
    private String workspace;
    private String user;
    private String password;
    private boolean connected;

    public Connect() {
        repository = "sample";
        workspace = "default";
        user = "admin";
        password = "admin";
        connected = false;
    }

    public String getRepository() {
        return repository;
    }
    public void setRepository(String repository) {
        this.repository = repository;
    }
    public String getWorkspace() {
        return workspace;
    }
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isConnected() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    // Logic

    @Resource(mappedName = "java:jboss/gatein-wcm")
    WcmRepositoryService repos;

    // Connect to repository
    public void connect(ActionEvent event) {
        log.debug(event);
        log.info("Connecting to repository/workspace: " + repository + "/" + workspace + " with user/password: " + user + "/" + password);
        try {
            repos.createContentSession(repository, workspace, user, password);
            connected = true;
            msg("Connection OK");
        } catch (WcmContentIOException ex) {
            msg("Cannot connect with repository " + repository);
            log.error(ex.getMessage(), ex);
            connected = false;
        } catch (WcmContentSecurityException ex) {
            msg("User " + user + " incorrect for " + repository + "/" + workspace + "");
            log.warn("ContentSecurityException for " + user + " repository " + repository + " workspace " + workspace + ". Msg: " + ex.getMessage());
            connected = false;
        }
    }

    // Disconnect. Cleaning session and redirect.
    public String disconnect() throws IOException {
        log.info("Disconnect ...");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "wcm";
    }


}
