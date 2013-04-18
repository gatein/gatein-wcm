package org.gatein.wcm.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.content.WcmBinaryObject;
import org.gatein.wcm.api.model.content.WcmFolder;
import org.gatein.wcm.api.model.content.WcmObject;
import org.gatein.wcm.api.model.content.WcmTextObject;

public class WcmFolderImpl extends WcmObjectImpl implements WcmFolder {

    List<WcmObject> children;
    List<WcmFolder> subfolders;
    List<WcmTextObject> textContent;
    List<WcmBinaryObject> binaryContent;

    @Override
    public List<WcmObject> getChildren() {
        return children;
    }

    @Override
    public List<WcmFolder> getSubfolders() {
        if (children == null) return null;
        if (subfolders == null) {
            subfolders = new ArrayList<WcmFolder>();
            for (WcmObject o : children) {
                if (o instanceof WcmFolder)
                    subfolders.add((WcmFolder)o);
            }
        }
        return subfolders;
    }

    @Override
    public List<WcmTextObject> getTextContent() {
        if (children == null) return null;
        if (textContent == null) {
            textContent = new ArrayList<WcmTextObject>();
            for (WcmObject o : children) {
                if (o instanceof WcmTextObject)
                    textContent.add((WcmTextObject)o);
            }
        }
        return textContent;
    }

    @Override
    public List<WcmBinaryObject> getBinaryContent() {
        if (children == null) return null;
        if (binaryContent == null) {
            binaryContent = new ArrayList<WcmBinaryObject>();
            for (WcmObject o : children) {
                if (o instanceof WcmBinaryObject)
                    binaryContent.add((WcmBinaryObject)o);
            }
        }
        return binaryContent;
    }

    // Protected methods

    protected WcmFolderImpl() {

    }

    protected void setChildren(List<WcmObject> children) {
        this.children = children;
    }

}