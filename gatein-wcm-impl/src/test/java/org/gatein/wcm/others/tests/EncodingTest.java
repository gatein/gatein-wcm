package org.gatein.wcm.others.tests;

import junit.framework.Assert;

import org.junit.Test;

public class EncodingTest {

    @Test
    public void encodingTest() {

        try {
            String text = "Camión región áéíóú test";
            String text2 = new String( text.getBytes("UTF8"), "UTF8" );

            System.out.println(text);
            System.out.println(text2);
        } catch (Exception e) {
            Assert.fail( e.getMessage() );
        }
        Assert.assertTrue(true);
    }
}
