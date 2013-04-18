package org.gatein.wcm.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.security.WcmAce;
import org.gatein.wcm.api.model.security.WcmAcl;

public class WcmAclImpl implements WcmAcl {

    String id;
    String description;
    ArrayList<WcmAce> aces;

    protected WcmAclImpl(String id, String description) {
        this.id = id;
        this.description = description;
        aces = new ArrayList<WcmAce>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<WcmAce> getAces() {
        return aces;
    }

    @Override
    public String toString() {
        return "WCMACL [id=" + id + ", description=" + description + ", aces=" + aces + "]";
    }

}
