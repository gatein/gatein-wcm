package org.gatein.wcm.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.gatein.wcm.api.model.security.ACE;
import org.gatein.wcm.api.model.security.ACL;

public class WCMACL implements ACL {

    String id;
    String description;
    ArrayList<ACE> aces;

    protected WCMACL(String id, String description) {
        this.id = id;
        this.description = description;
        aces = new ArrayList<ACE>();
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ACE> getAces() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return "WCMACL [id=" + id + ", description=" + description + ", aces=" + aces + "]";
    }

}
