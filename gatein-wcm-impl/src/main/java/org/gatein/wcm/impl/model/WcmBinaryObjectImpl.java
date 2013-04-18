package org.gatein.wcm.impl.model;

import java.io.InputStream;

import org.gatein.wcm.api.model.content.WcmBinaryObject;

public class WcmBinaryObjectImpl extends WcmObjectImpl implements WcmBinaryObject {

    String version;
    String contentType;
    long size;
    String fileName;
    InputStream content;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getMimeType() {
        return contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public InputStream getContent() {
        return content;
    }

    // Protected methods

    protected WcmBinaryObjectImpl() {
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected void setSize(Long size) {
        this.size = size;
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected void setContent(InputStream content) {
        this.content = content;
    }

}