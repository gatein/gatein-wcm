package org.gatein.wcm.impl.model;

import org.gatein.wcm.api.model.metadata.Property;

public class WcmProperty implements Property {

    String name;
    String value;

    protected WcmProperty() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setValue(String value) {
        this.value = value;
    }
}
