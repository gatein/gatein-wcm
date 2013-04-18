package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.content.WcmTextObject;

public class WcmTextObjectImpl extends WcmObjectImpl implements WcmTextObject {

    String version;
    String content;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getContent() {
        return content;
    }

    // Protected methods

    protected WcmTextObjectImpl() {
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    protected void setContent(String content) {
        this.content = content;
    }

}
