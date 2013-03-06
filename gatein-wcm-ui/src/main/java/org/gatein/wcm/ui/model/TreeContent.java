package org.gatein.wcm.ui.model;

import java.util.List;

import org.gatein.wcm.api.model.content.Content;

public class TreeContent {

    private Content content;

    protected TreeContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public List<TreeContent> getNodes() {
        return null;
    }

}
