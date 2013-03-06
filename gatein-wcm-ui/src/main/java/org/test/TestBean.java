package org.test;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.jboss.logging.Logger;

@ManagedBean
@SessionScoped
public class TestBean {
    private static final Logger log = Logger.getLogger("org.test");

    String user;
    String password;

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

    public void test(ActionEvent event) {

        log.info("TEST: " + user + " " + password);


    }


}
