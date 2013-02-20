package org.gatein.wcm.others.tests;

import junit.framework.Assert;

import org.junit.Test;

public class StringTest {

    @Test
    public void extract_text() {
        String name = "__pepe";
        String out = name.substring(2);

        Assert.assertEquals("pepe", out);

        Assert.assertEquals(true, name.startsWith("__"));

    }

    @Test
    public void last_id() {
        String test1 = "/folder1";
        String test2 = "/folder1/id1";
        String test3 = "/folder1/id1/id2";
        String test4 = "/folder1/id1/id2/id3";

        Assert.assertEquals("folder1", test1.substring(test1.lastIndexOf("/") + 1));
        Assert.assertEquals("id1", test2.substring(test2.lastIndexOf("/") + 1));
        Assert.assertEquals("id2", test3.substring(test3.lastIndexOf("/") + 1));
        Assert.assertEquals("id3", test4.substring(test4.lastIndexOf("/") + 1));
    }

    @Test
    public void parent_location() {
        String test1 = "/folder1";
        String test2 = "/folder1/id1";
        String test3 = "/folder1/id1/id2";
        String test4 = "/folder1/id1/id2/id3";

        Assert.assertEquals("/", test1.substring(0,test1.lastIndexOf("/")+1));
        Assert.assertEquals("/folder1/", test2.substring(0,test2.lastIndexOf("/")+1));
        Assert.assertEquals("/folder1/id1/", test3.substring(0,test3.lastIndexOf("/")+1));
        Assert.assertEquals("/folder1/id1/id2/", test4.substring(0,test4.lastIndexOf("/")+1));
    }

}
