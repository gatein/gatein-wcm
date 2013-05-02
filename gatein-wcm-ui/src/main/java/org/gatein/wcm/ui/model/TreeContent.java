package org.gatein.wcm.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMFolder;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;

public class TreeContent {

    public static final String ICONLEAF_FOLDER = "folder_green.png";
    public static final String ICONLEAF_TEXT = "html.png";
    public static final String ICONLEAF_BINARY = "images.png";

    public enum Type {
        TYPE_TEXT, TYPE_FOLDER, TYPE_BINARY, NO_TYPE
    };

    private WCMObject content;

    // TextContent temp properties
    private String text;

    public TreeContent(WCMObject content) {
        this.content = content;
    }

    public WCMObject getContent() {
        return content;
    }

    public void setContent(WCMObject content) {
        this.content = content;
    }

    public List<TreeContent> getNodes() {
        if (content instanceof WCMFolder) {
            WCMFolder f = (WCMFolder)content;
            if (f.getChildren() == null) return null;
            if (f.getChildren().size() == 0) return null;
            ArrayList<TreeContent> children = new ArrayList<TreeContent>();
            for (WCMObject c : f.getChildren())
                children.add(new TreeContent(c));
            return children;
        }
        return null;
    }

    public Type getType() {
        if (content instanceof WCMFolder)
            return Type.TYPE_FOLDER;
        if (content instanceof WCMTextDocument)
            return Type.TYPE_TEXT;
        if (content instanceof WCMBinaryDocument)
            return Type.TYPE_BINARY;
        return Type.NO_TYPE;
    }

    public String getIconLeaf() {

        switch (getType()) {
            case TYPE_FOLDER:
                return ICONLEAF_FOLDER;
            case TYPE_TEXT:
                return ICONLEAF_TEXT;
            case TYPE_BINARY:
                return ICONLEAF_BINARY;
            default:
                break;
        }
        return "user_delete.png";
    }

    public WCMFolder getFolder() {
        if (getType() == Type.TYPE_FOLDER)
            return (WCMFolder)content;
        return null;
    }

    public WCMTextDocument getTextContent() {
        if (getType() == Type.TYPE_TEXT)
            return (WCMTextDocument)content;
        return null;
    }

    public WCMBinaryDocument getBinaryContent() {
        if (getType() == Type.TYPE_BINARY)
            return (WCMBinaryDocument)content;
        return null;
    }

    public String getText() {
        if (text == null && getTextContent() != null) {
            text = getTextContent().getContent();
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
