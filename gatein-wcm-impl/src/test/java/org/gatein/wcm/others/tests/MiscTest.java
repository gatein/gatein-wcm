package org.gatein.wcm.others.tests;

import junit.framework.Assert;
import org.junit.Test;

public class MiscTest {

    @Test
    public void test_parameters() {
        String param1 = null;
        String param2 = "";

        Assert.assertTrue( param1 == null );
        Assert.assertTrue( "".equals( param2 ) );
        Assert.assertTrue( param1 == null || "".equals( param1 ) );
    }

}
