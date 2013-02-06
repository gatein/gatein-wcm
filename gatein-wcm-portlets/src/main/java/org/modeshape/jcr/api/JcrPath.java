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

import java.util.Iterator;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public class JcrPath implements Iterable<String> {

	protected static class TopDownIterator implements Iterator<String> {
		private JcrPath path;

		/**
		 * @param path
		 */
		public TopDownIterator(JcrPath path) {
			super();
			this.path = path;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return path != null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#next()
		 */
		@Override
		public String next() {
			if (path == null) {
				throw new IllegalStateException("No next element to return.");
			}
			String result = path.getFirstSegment();
			path = path.removeFirstSegmentPath();
			return result;
		}

		/**
		 * Throws {@link UnsupportedOperationException}.
		 *
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public static final char DELIMITER = '/';

	private final String path;

	public JcrPath(String path) {
		if (path == null) {
			throw new IllegalArgumentException(
					"The argument \"path\" may not be null");
		}
		if (path.length() == 0) {
			throw new IllegalArgumentException(
					"The argument \"path\" may not be an empty string");
		}

		/* skip initial delimiters */
		int start = 0;
		while (start < path.length() && path.charAt(start) == DELIMITER) {
			start++;
		}

		/* skip terminal delimiters */
		int end = path.length();
		while (end > start && path.charAt(end - 1) == DELIMITER) {
			end--;
		}
		if (start == end) {
			throw new IllegalArgumentException(
					"The argument \"path\" must contain characters other than '"
							+ DELIMITER + "'");
		}
		this.path = path.substring(start, end);
	}

	private JcrPath(String path, boolean dummy) {
		this.path = path;
	}

	public String asDescendantOf(JcrPath parent) {
		if (!path.startsWith(parent.path)) {
			throw new IllegalArgumentException("Cannot make descendant: '"
					+ parent.path + "' is not an ancestor of '" + path + "'.");
		}
		int start = parent.path.length();
		while (start < path.length() && path.charAt(start) == JcrPath.DELIMITER) {
			start++;
		}
		if (start >= path.length()) {
			throw new IllegalArgumentException("Cannot make descendant: '"
					+ parent.path + "'.length() >= '" + path + "'.length().");
		}
		return path.substring(start);
	}

	public String asDescendantOf(String parentPath) {
		return asDescendantOf(new JcrPath(parentPath));
	}

	public String getFirstSegment() {
		int end = path.indexOf(DELIMITER);
		if (end >= 0) {
			return path.substring(0, end);
		} else {
			return path;
		}
	}

	public String getLastSegment() {
		int start = path.lastIndexOf(DELIMITER);
		if (start >= 0) {
			return path.substring(start + 1);
		} else {
			return path;
		}
	}

	public String getParent() {
		if (path != null) {
			int end = path.lastIndexOf(DELIMITER);
			while (end > 0 && path.charAt(end - 1) == DELIMITER) {
				end--;
			}
			if (end > 0) {
				return path.substring(0, end);
			}
		}
		return null;
	}

	public JcrPath getParentPath() {
		String parent = getParent();
		if (parent != null) {
			return new JcrPath(parent, true);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		return new TopDownIterator(this);
	}

	public String removeFirstSegment() {
		if (path != null) {
			int start = path.indexOf(DELIMITER);
			if (start >= 0) {
				start++;
				/* skip subsequent delimiters */
				while (start < path.length() && path.charAt(start) == DELIMITER) {
					start++;
				}
				if (start < path.length()) {
					return path.substring(start);
				}
			}
		}
		return null;
	}

	public JcrPath removeFirstSegmentPath() {
		String parent = removeFirstSegment();
		if (parent != null) {
			return new JcrPath(parent, true);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return path;
	}

}
