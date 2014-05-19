/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
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
package org.gatein.wcm.portlet.content.render;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for WcmTags
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class WcmTagsTest {

    WcmTags tags;

    @Before
    public void setup() {
        tags = new WcmTags();
    }

    @Test
    public void hasTagTest() {
        StringBuilder template = new StringBuilder("<wcm-list>Bla bla bla</wcm-list>");
        String tag = "wcm-list";
        String noTag = "wcm-other";

        assertTrue(tags.hasTag(tag, template));
        assertFalse(tags.hasTag(noTag, template));
    }

    @Test
    public void extractTagTest() {
        StringBuilder template1 = new StringBuilder("bla bla bla <wcm-list> This is my inner tag </wcm-list> bla bla bla");

        assertEquals("<wcm-list> This is my inner tag </wcm-list>", tags.extractTag("wcm-list", template1).toString());

        StringBuilder template2 = new StringBuilder("bla bla bla <wcm-list> This is my <wcm-img class=\"test\" /> inner tag </wcm-list> bla bla bla");

        assertEquals("<wcm-img class=\"test\" />", tags.extractTag("wcm-img", template2).toString());
    }

    @Test
    public void extractImgTest() {
        String html = "<html><img src=\"first.jpg\" class=\"test\" style=\"other style\" /> bla bla bla " +
                "<img src=\"second.jpg\" class=\"test\" style=\"other style\" /> bla bla bla " +
                "<img src=\"third.jpg\" class=\"test\" style=\"other style\" /> bla bla bla</html>";

        assertEquals("<img src=\"first.jpg\" class=\"test\" style=\"other style\" />", tags.extractImg(html, 0, false));
        assertEquals("<img src=\"second.jpg\" class=\"test\" style=\"other style\" />", tags.extractImg(html, 1, false));
        assertEquals("<img src=\"third.jpg\" class=\"test\" style=\"other style\" />", tags.extractImg(html, 2, false));

        assertEquals("<img src=\"first.jpg\">", tags.extractImg(html, 0, true));
        assertEquals("<img src=\"second.jpg\">", tags.extractImg(html, 1, true));
        assertEquals("<img src=\"third.jpg\">", tags.extractImg(html, 2, true));
    }

    @Test
    public void propertiesTagTest() {
        StringBuilder template = new StringBuilder("<wcm-list a=\"1\" b=\"2\" c=\"3\"></wcm-list>");

        Map<String, String> props = tags.propertiesTag(template);

        assertEquals("1", props.get("a"));
        assertEquals("2", props.get("b"));
        assertEquals("3", props.get("c"));

        StringBuilder template2 = new StringBuilder("<wcm-list a='1' b='2' c='3'></wcm-list>");

        Map<String, String> props2 = tags.propertiesTag(template2);

        assertEquals("1", props2.get("a"));
        assertEquals("2", props2.get("b"));
        assertEquals("3", props2.get("c"));
    }

    @Test
    public void insideTagTest() {
        StringBuilder template = new StringBuilder("<wcm-list>bla bla bla <wcm-test>this is a test</wcm-test> bla bla bla</wcm-list>");

        assertEquals("this is a test", tags.insideTag("wcm-test", template));
        assertEquals("bla bla bla <wcm-test>this is a test</wcm-test> bla bla bla", tags.insideTag("wcm-list", template));
    }

    @Test
    public void replaceTest() {
        String test1 = "bla bla bla this is old bla bla bla";
        String old1 = "this is old";
        String new1 = "for the new";

        assertEquals("bla bla bla for the new bla bla bla", tags.replace(test1, old1, new1));

        String test2 = "<html><img src=\"http://fake.com/image.jpg\" class=\"wcm-skip\"></html>";
        String old2 = "<img src=\"http://fake.com/image.jpg\" class=\"wcm-skip\">";
        String new2 = "<img src=\"http://fake.com/newImage.jpg\" class\"other-style wcm-skip\">";

        assertEquals("<html><img src=\"http://fake.com/newImage.jpg\" class=\"wcm-skip\"></html>", tags.replace(test2, old2, new2));
    }

    @Test
    public void replaceAllTest() {
        StringBuilder template = new StringBuilder("This is a test1 where we are going to change test1 for another test1 word");
        String oldData = "test1";
        String newData = "<new_more_longdata>";

        tags.replaceAll(template, oldData, newData);
        assertEquals("This is a <new_more_longdata> where we are going to change <new_more_longdata> for another <new_more_longdata> word", template.toString());
    }

}
