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

package org.gatein.wcm.api.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.Source;
import javax.jcr.version.VersionException;

import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.JcrPath;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public class JcrContentHierarchy {

	public static final String CONTENT_ROOT_PATH = "/content";
	public static final String IMAGE_ROOT_PATH = "/img";
	private final Session session;

	/**
	 * @param session
	 */
	public JcrContentHierarchy(Session session) {
		super();
		this.session = session;
	}

	public void ensureInitialized() throws ItemExistsException,
			PathNotFoundException, NoSuchNodeTypeException, LockException,
			VersionException, ConstraintViolationException, RepositoryException {
		if (!session.nodeExists(CONTENT_ROOT_PATH)) {
			session.getRootNode().addNode(CONTENT_ROOT_PATH,
					JcrConstants.NT_FOLDER);
		}
		if (!session.nodeExists(IMAGE_ROOT_PATH)) {
			session.getRootNode().addNode(IMAGE_ROOT_PATH,
					JcrConstants.NT_FOLDER);
		}
	}

	public Node getContentRootNode() throws PathNotFoundException,
			RepositoryException {
		return session.getNode(CONTENT_ROOT_PATH);
	}

	public List<String> listResources(String parentPath) throws PathNotFoundException, RepositoryException {
		javax.jcr.query.qom.QueryObjectModelFactory factory = session.getWorkspace().getQueryManager().getQOMFactory();

		// Create the parts of a query object ...
		final String RESOURCE = "resource";
		Source selector = factory.selector(NodeType.NT_RESOURCE, RESOURCE);
		Constraint constraints = factory.descendantNode(RESOURCE, parentPath);
		Column[] columns = new Column[] {factory.column(RESOURCE, Property.JCR_PATH, Property.JCR_PATH)};
		Ordering[] orderings = new Ordering[] {factory.ascending(factory.propertyValue(RESOURCE, Property.JCR_PATH))};
		Query query = factory.createQuery(selector, constraints, orderings, columns);
		QueryResult result = query.execute();
		RowIterator iter = result.getRows();

		long cnt = iter.getSize();
		if (cnt == 0) {
			return Collections.emptyList();
		}
		else {
			/* cnt < 0 means JCR does not know - we hardcode 64 in that case */
			List<String> list = new ArrayList<String>(cnt < 0 ? 64 : (int) cnt);
			while (iter.hasNext()) {
				Row r = iter.nextRow();
				String path = r.getValue(Property.JCR_PATH).getString();
				/* return only the parent-relative path */
			    list.add(new JcrPath(path).asDescendantOf(parentPath));
			}
			return list;
		}
	}

	public Node getImageRootNode() throws PathNotFoundException,
			RepositoryException {
		return session.getNode(IMAGE_ROOT_PATH);
	}

	/**
	 * @param path
	 *            Path relative to #getContentRootNode()
	 * @return
	 * @throws RepositoryException
	 */
	public Node getOrCreateFolder(Node parent, String name)
			throws RepositoryException {
		if (parent.hasNode(name)) {
			return parent.getNode(name);
		} else {
			Node result = null;
			try {
				result = parent.addNode(name, JcrConstants.NT_FOLDER);
				session.save();
				return result;
			} catch (ItemExistsException e) {
				session.refresh(false);
				return parent.getNode(name);
			}
		}
	}

	/**
	 * @param path
	 *            Path relative to #getContentRootNode()
	 * @return
	 * @throws RepositoryException
	 */
	public Node getOrCreateFile(Node parent, String path)
			throws RepositoryException {
		if (parent.hasNode(path)) {
			return parent.getNode(path);
		} else {
			JcrPath jcrPath = new JcrPath(path);
			JcrPath parentPath = jcrPath.getParentPath();

			Node parentNode = parent;
			for (String segment : parentPath) {
				parentNode = getOrCreateFolder(parentNode, segment);
			}
			return parentNode.addNode(jcrPath.getLastSegment(), JcrConstants.NT_FILE);
		}
	}

	public static String getAbsoluteContentPath(String path, String locale) {
		return new StringBuilder(CONTENT_ROOT_PATH.length() + locale.length()
				+ path.length() + 2 /* number of delimiters */)
				.append(CONTENT_ROOT_PATH).append(JcrPath.DELIMITER)
				.append(locale).append(JcrPath.DELIMITER).append(path)
				.toString();
	}

	public static String getAbsoluteImagePath(String path) {
		return new StringBuilder(IMAGE_ROOT_PATH.length() + path.length()
				+ 1 /* number of delimiters */).append(CONTENT_ROOT_PATH)
				.append(JcrPath.DELIMITER).append(path).toString();
	}

	public static String getRelativeContentPath(String path, String locale) {
		return new StringBuilder(locale.length() + path.length() + 1 /*
																	 * number of
																	 * delimiters
																	 */
		).append(locale).append(JcrPath.DELIMITER).append(path).toString();
	}

	/**
	 * @param relativePath
	 * @return
	 */
	public static String getLocale(String relativePath) {
		return new JcrPath(relativePath).getFirstSegment();
	}

}
