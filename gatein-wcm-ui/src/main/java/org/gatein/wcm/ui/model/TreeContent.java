package org.gatein.wcm.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.content.BinaryContent;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.content.Folder;
import org.gatein.wcm.api.model.content.TextContent;

public class TreeContent {

    public static final String ICONLEAF_FOLDER = "folder_green.png";
    public static final String ICONLEAF_TEXT = "html.png";
    public static final String ICONLEAF_BINARY = "images.png";

    public enum Type {
        TYPE_TEXT, TYPE_FOLDER, TYPE_BINARY, NO_TYPE
    };

    private Content content;

    // TextContent temp properties
    private String text;

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

    public Type getType() {
        if (content instanceof Folder)
            return Type.TYPE_FOLDER;
        if (content instanceof TextContent)
            return Type.TYPE_TEXT;
        if (content instanceof BinaryContent)
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

    public Folder getFolder() {
        if (getType() == Type.TYPE_FOLDER)
            return (Folder)content;
        return null;
    }

    public TextContent getTextContent() {
        if (getType() == Type.TYPE_TEXT)
            return (TextContent)content;
        return null;
    }

    public BinaryContent getBinaryContent() {
        if (getType() == Type.TYPE_BINARY)
            return (BinaryContent)content;
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
