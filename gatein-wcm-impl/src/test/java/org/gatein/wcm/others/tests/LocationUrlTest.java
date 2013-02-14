package org.gatein.wcm.others.tests;

import junit.framework.Assert;

import org.junit.Test;

public class LocationUrlTest {


    public String parent(String location) {

        String[] locs = location.split("/");

        if (locs.length > 1) {
            StringBuffer sb = new StringBuffer(location.length());
            for (int i=1; i < (locs.length -1); i++) {
                sb.append("/" + locs[i]);
            }
            return sb.toString();
        }

        return location;
    }

    @Test
    public void check_parent() {

        String loc = "/a/b/c/d";

        System.out.println ( parent(loc) );
        Assert.assertEquals(parent(loc), "/a/b/c");

        loc = "/";

        System.out.println ( parent(loc) );
        Assert.assertEquals(parent(loc), "/");

    }

}
