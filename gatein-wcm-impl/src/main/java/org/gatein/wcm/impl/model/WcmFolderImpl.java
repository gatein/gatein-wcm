package org.gatein.wcm.impl.model;

import java.util.List;

import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmFolder;

public class WcmFolderImpl extends WcmObjectImpl implements WcmFolder {

    List<WcmObject> children;

    @Override
    public List<WcmObject> getChildren() {
        return children;
    }

    // Protected methods

    protected WcmFolderImpl() {

    }

    protected void setChildren(List<WcmObject> children) {
        this.children = children;
    }

}