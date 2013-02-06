package org.gatein.wcm.api.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.Source;

import org.apache.log4j.Logger;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.JcrPath;

public class JcrContentImpl implements ContentAPI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(JcrContentImpl.class);

	public String getContent(String key, String locale) {
		String absPath = JcrContentHierarchy
				.getAbsoluteContentPath(key, locale);
		Session session = null;
		try {
			Repository repository = JcrRepository.lookup();
			session = repository.login();
			if (session.nodeExists(absPath)) {
				Node fileNode = session.getNode(absPath);
				if (fileNode != null && fileNode.hasNode(JcrConstants.JCR_CONTENT)) {
					Node contentNode = fileNode.getNode(JcrConstants.JCR_CONTENT);
					Content result = toContent(key, locale, contentNode);
					return result.getContent();
				}
			}
		} catch (LoginException e) {
			throw new RuntimeException(e);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
		return null;
	}

	public void setContent(String key, String locale, String content) {
		String relPath = JcrContentHierarchy
				.getRelativeContentPath(key, locale);
		Session session = null;
		try {
			Repository repository = JcrRepository.lookup();
			session = repository.login();
			if (content == null) {
				String absPath = JcrContentHierarchy.getAbsoluteContentPath(
						key, locale);
				if (session.nodeExists(absPath)) {
					session.removeItem(absPath);
				}
			} else {
				JcrContentHierarchy hierarchy = new JcrContentHierarchy(session);
				Node fileNode = hierarchy.getOrCreateFile(hierarchy.getContentRootNode(), relPath);
				Node resourceNode = fileNode.hasNode(JcrConstants.JCR_CONTENT) ? fileNode
						.getNode(JcrConstants.JCR_CONTENT) : fileNode.addNode(
						JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);

				resourceNode.setProperty(Property.JCR_ENCODING,
						WcmConstants.UTF_8);
				resourceNode.setProperty(JcrConstants.JCR_MIME_TYPE,
						WcmConstants.APPLICATION_XML_XHTML);
				resourceNode.setProperty("jcr:lastModified", session.getValueFactory()
						.createValue(Calendar.getInstance()));
				InputStream in = new ByteArrayInputStream(
						content.getBytes(WcmConstants.UTF_8));
				resourceNode.setProperty(JcrConstants.JCR_DATA, session
						.getValueFactory().createBinary(in));
			}
			session.save();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}

	public List<Content> getContent() {
		final String parentPath = JcrContentHierarchy.CONTENT_ROOT_PATH;
		Session session = null;
		try {
			Repository repository = JcrRepository.lookup();
			session = repository.login();
			javax.jcr.query.qom.QueryObjectModelFactory factory = session.getWorkspace().getQueryManager().getQOMFactory();

			// Create the parts of a query object ...
			final String RESOURCE = "resource";
			Source selector = factory.selector(NodeType.NT_RESOURCE, RESOURCE);
			Constraint constraints = factory.descendantNode(RESOURCE, parentPath);
			Column[] columns = new Column[] {};
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
				List<Content> list = new ArrayList<Content>(cnt < 0 ? 64 : (int) cnt);
				while (iter.hasNext()) {
					Node node = iter.nextRow().getNode();
					String relativePath = new JcrPath(node.getPath()).asDescendantOf(parentPath);
					String locale = JcrContentHierarchy.getLocale(relativePath);
				    list.add(toContent(relativePath, locale, node));
				}
				return list;
			}
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}

	public void removeContent(String key, String locale) {
		setContent(key, locale, null);
	}

	private static Content toContent(String relativePath, String locale, Node node) throws ValueFormatException, PathNotFoundException, RepositoryException, IOException {
		Content result = new Content();
		result.setKey(relativePath);
		result.setLocale(locale);

		Binary bin = node.getProperty(JcrConstants.JCR_DATA).getBinary();
		InputStream in = bin.getStream();
		Reader r = new InputStreamReader(in, node.getProperty(Property.JCR_ENCODING).getString());
		int len = (int) bin.getSize();
		StringBuilder sb = new StringBuilder(len + len / 5);
		char[] buffer = new char[1024];
		int cnt = 0;
		while ((cnt = r.read(buffer)) >= 0) {
			sb.append(buffer, 0, cnt);
		}
		result.setContent(sb.toString());
		return result;
	}

}