package org.gatein.wcm.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.content.BinaryContent;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.content.Folder;
import org.gatein.wcm.api.model.content.TextContent;

public class TreeContent {

    public final String ICONLEAF_FOLDER = "folder_green.png";
    public final String ICONLEAF_TEXT = "html.png";
    public final String ICONLEAF_BINARY = "images.png";

    private Content content;

    public TreeContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public List<TreeContent> getNodes() {
        if (content instanceof Folder) {
            Folder f = (Folder)content;
            if (f.getChildren() == null) return null;
            if (f.getChildren().size() == 0) return null;
            ArrayList<TreeContent> children = new ArrayList<TreeContent>();
            for (Content c : f.getChildren())
                children.add(new TreeContent(c));
            return children;
        }
        return null;
    }

    public String getIconLeaf() {
        if (content instanceof Folder) {
            return ICONLEAF_FOLDER;
        }
        if (content instanceof TextContent) {
            return ICONLEAF_TEXT;
        }
        if (content instanceof BinaryContent) {
            return ICONLEAF_BINARY;
        }
        return "user_delete.png";
    }

}
