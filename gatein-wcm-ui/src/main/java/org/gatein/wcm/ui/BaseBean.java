package org.gatein.wcm.ui;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class BaseBean {

    public String getLocale() {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
    }

    public void msg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
    }
}
