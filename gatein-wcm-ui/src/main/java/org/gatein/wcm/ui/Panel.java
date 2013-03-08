package org.gatein.wcm.ui;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.gatein.wcm.ui.model.TreeContent;
import org.jboss.logging.Logger;

@ManagedBean
@SessionScoped
public class Panel extends BaseBean implements Serializable {
    private static final long serialVersionUID = 3385985697794449952L;

    private static final Logger log = Logger.getLogger("org.gatein.wcm.ui.panel");

    private String view;
    private TreeContent selected;

    public Panel() {
        log.debug("Creating a panel");
        view = "";
    }

    public String getView() {
        if (selected != null) {
            switch (selected.getType()) {
                case TYPE_FOLDER:
                    view = "folder";
                    break;
                case TYPE_BINARY:
                    view = "binary";
                    break;
                case TYPE_TEXT:
                    view = "text";
                    break;
                default:
                    break;
            }
        }
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public TreeContent getSelected() {
        return selected;
    }

    public void setSelected(TreeContent selected) {
        this.selected = selected;
    }

}