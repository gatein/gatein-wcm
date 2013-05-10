/*
 * JBoss, a division of Red Hat
 * Copyright 2013, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.wcm.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.content.WCMBinaryDocument;
import org.gatein.wcm.api.model.content.WCMFolder;
import org.gatein.wcm.api.model.content.WCMObject;
import org.gatein.wcm.api.model.content.WCMTextDocument;

/**
 * @see {@link WCMFolder}
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 *
 */
public class WCMFolderImpl extends WCMObjectImpl implements WCMFolder {

    List<WCMObject> children;
    List<WCMFolder> subfolders;
    List<WCMTextDocument> textContent;
    List<WCMBinaryDocument> binaryContent;

    @Override
    public List<WCMObject> getChildren() {
        return children;
    }

    @Override
    public List<WCMFolder> getSubfolders() {
        if (children == null) return null;
        if (subfolders == null) {
            subfolders = new ArrayList<WCMFolder>();
            for (WCMObject o : children) {
                if (o instanceof WCMFolder)
                    subfolders.add((WCMFolder)o);
            }
        }
        return subfolders;
    }

    @Override
    public List<WCMTextDocument> getTextDocuments() {
        if (children == null) return null;
        if (textContent == null) {
            textContent = new ArrayList<WCMTextDocument>();
            for (WCMObject o : children) {
                if (o instanceof WCMTextDocument)
                    textContent.add((WCMTextDocument)o);
            }
        }
        return textContent;
    }

    @Override
    public List<WCMBinaryDocument> getBinaryDocuments() {
        if (children == null) return null;
        if (binaryContent == null) {
            binaryContent = new ArrayList<WCMBinaryDocument>();
            for (WCMObject o : children) {
                if (o instanceof WCMBinaryDocument && !(o instanceof WCMTextDocument))
                    binaryContent.add((WCMBinaryDocument)o);
            }
        }
        return binaryContent;
    }

    // Protected methods

    protected WCMFolderImpl() {

    }

    protected void setChildren(List<WCMObject> children) {
        this.children = children;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WCMFolderImpl other = (WCMFolderImpl) obj;
        if (children == null) {
            if (other.children != null)
                return false;
        } else if (!children.equals(other.children))
            return false;
        return true;
    }
}