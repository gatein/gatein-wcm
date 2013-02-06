/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.modeshape.jcr.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public class JcrPathTest {

	@Test
	public void testJcrPath() {

		/* fails */
		try {
			new JcrPath(null);
			fail("Should have thrown IllegalArgumentException - null.");
		} catch (IllegalArgumentException expected) {
		}

		try {
			new JcrPath("");
			fail("Should have thrown IllegalArgumentException - \"\"");
		} catch (IllegalArgumentException expected) {
		}
		try {
			new JcrPath("/");
			fail("Should have thrown IllegalArgumentException - only /");
		} catch (IllegalArgumentException expected) {
		}
		try {
			new JcrPath("///");
			fail("Should have thrown IllegalArgumentException - only ///");
		} catch (IllegalArgumentException expected) {
		}

		JcrPath p = null;
		/* stripping initial and final delimiters */
		p = new JcrPath("/a/b/c");
		assertEquals("a/b/c", p.toString());
		p = new JcrPath("//a/b/c");
		assertEquals("a/b/c", p.toString());
		p = new JcrPath("a/b/c/");
		assertEquals("a/b/c", p.toString());
		p = new JcrPath("a/b/c//");
		assertEquals("a/b/c", p.toString());

		/* normal case */
		p = new JcrPath("a/b/c");
		assertEquals("a/b/c", p.toString());

		/* subsequent delimiters inside */
		p = new JcrPath("a/b///c");
		assertEquals("a/b///c", p.toString());

	}

	@Test
	public void testGetLastSegment() {
		JcrPath p = null;
		p = new JcrPath("/a/b/c");
		assertEquals("c", p.getLastSegment());
		p = new JcrPath("/a/b///c");
		assertEquals("c", p.getLastSegment());
	}

	@Test
	public void testGetFirstSegment() {
		JcrPath p = null;
		p = new JcrPath("/a/b/c");
		assertEquals("a", p.getFirstSegment());
		p = new JcrPath("/a///b/c");
		assertEquals("a", p.getFirstSegment());
	}

	@Test
	public void testGetParent() {
		JcrPath p = null;
		p = new JcrPath("/a/b/c");
		assertEquals("a/b", p.getParent());
		p = new JcrPath("/a///b/c");
		assertEquals("a///b", p.getParent());
		p = new JcrPath("/a/b///c");
		assertEquals("a/b", p.getParent());
	}

	@Test
	public void testIterator() {
		Iterator<String> it = null;
		it = new JcrPath("/a/b/c").iterator();
		Assert.assertArrayEquals(new String[] { "a", "b", "c" }, toArray(it));
		it = new JcrPath("/a//b/c").iterator();
		Assert.assertArrayEquals(new String[] { "a", "b", "c" }, toArray(it));
		it = new JcrPath("/a/b/c//").iterator();
		Assert.assertArrayEquals(new String[] { "a", "b", "c" }, toArray(it));
	}

	@Test
	public void testParent() {
		JcrPath p = null;
		p = new JcrPath("/a/b/c");
		Assert.assertArrayEquals(new String[] { "a/b/c", "a/b", "a" },
				toParentsArray(p));
		p = new JcrPath("/a//b/c");
		Assert.assertArrayEquals(new String[] { "a//b/c", "a//b", "a" },
				toParentsArray(p));
		p = new JcrPath("/a/b/c//");
		Assert.assertArrayEquals(new String[] { "a/b/c", "a/b", "a" },
				toParentsArray(p));
	}

	@Test
	public void testAsDescendant() {

		/* fails */
		try {
			new JcrPath("/a/b/c").asDescendantOf("a/b//c");
			fail("Should have thrown IllegalArgumentException - not an ancestor");
		} catch (IllegalArgumentException expected) {
		}
		try {
			new JcrPath("/a/b/c").asDescendantOf("a/b/d");
			fail("Should have thrown IllegalArgumentException - not an ancestor");
		} catch (IllegalArgumentException expected) {
		}

		try {
			new JcrPath("/a/b/c").asDescendantOf("a/b/c");
			fail("Should have thrown IllegalArgumentException - too long");
		} catch (IllegalArgumentException expected) {
		}
		try {
			new JcrPath("/a/b/c").asDescendantOf("a/b/c//");
			fail("Should have thrown IllegalArgumentException - too long");
		} catch (IllegalArgumentException expected) {
		}

		assertEquals("c", new JcrPath("/a/b/c").asDescendantOf("/a/b"));
		assertEquals("c", new JcrPath("/a/b/c").asDescendantOf("a/b"));
		assertEquals("c", new JcrPath("/a/b/c/").asDescendantOf("a/b"));
		assertEquals("c", new JcrPath("a/b//c").asDescendantOf("a/b"));
		assertEquals("b/c", new JcrPath("/a///b/c/").asDescendantOf("a"));
		assertEquals("b//c", new JcrPath("a/b//c").asDescendantOf("a"));
	}

	private static String[] toArray(Iterator<String> it) {
		List<String> result = new ArrayList<String>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result.toArray(new String[result.size()]);
	}

	private static String[] toParentsArray(JcrPath p) {
		List<String> result = new ArrayList<String>();
		while (p != null) {
			result.add(p.toString());
			p = p.getParentPath();
		}
		return result.toArray(new String[result.size()]);
	}

}
