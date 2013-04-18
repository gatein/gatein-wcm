package org.gatein.wcm.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.content.WcmBinaryObject;
import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmFolder;
import org.gatein.wcm.api.model.content.WcmTextObject;

public class TreeContent {

    public static final String ICONLEAF_FOLDER = "folder_green.png";
    public static final String ICONLEAF_TEXT = "html.png";
    public static final String ICONLEAF_BINARY = "images.png";

    public enum Type {
        TYPE_TEXT, TYPE_FOLDER, TYPE_BINARY, NO_TYPE
    };

    private WcmObject content;

    // TextContent temp properties
    private String text;

    public TreeContent(WcmObject content) {
        this.content = content;
    }

    public WcmObject getContent() {
        return content;
    }

    public void setContent(WcmObject content) {
        this.content = content;
    }

    public List<TreeContent> getNodes() {
        if (content instanceof WcmFolder) {
            WcmFolder f = (WcmFolder)content;
            if (f.getChildren() == null) return null;
            if (f.getChildren().size() == 0) return null;
            ArrayList<TreeContent> children = new ArrayList<TreeContent>();
            for (WcmObject c : f.getChildren())
                children.add(new TreeContent(c));
            return children;
        }
        return null;
    }

    public Type getType() {
        if (content instanceof WcmFolder)
            return Type.TYPE_FOLDER;
        if (content instanceof WcmTextObject)
            return Type.TYPE_TEXT;
        if (content instanceof WcmBinaryObject)
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

    public WcmFolder getFolder() {
        if (getType() == Type.TYPE_FOLDER)
            return (WcmFolder)content;
        return null;
    }

    public WcmTextObject getTextContent() {
        if (getType() == Type.TYPE_TEXT)
            return (WcmTextObject)content;
        return null;
    }

    public WcmBinaryObject getBinaryContent() {
        if (getType() == Type.TYPE_BINARY)
            return (WcmBinaryObject)content;
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
