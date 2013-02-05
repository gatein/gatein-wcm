package org.gatein.wcm.api.images;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.gatein.wcm.api.content.JcrContentHierarchy;
import org.gatein.wcm.api.content.JcrRepository;
import org.modeshape.jcr.api.JcrConstants;

public class JcrImagesImpl implements ImagesAPI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(JcrImagesImpl.class);

	public Image getImage(String key) {
		String absPath = JcrContentHierarchy.getAbsoluteImagePath(key);
		Session session = null;
		try {
			Repository repository = JcrRepository.lookup();
			session = repository.login();
			if (session.nodeExists(absPath)) {
				Node fileNode = session.getNode(absPath);
				if (fileNode != null && fileNode.hasNode(JcrConstants.JCR_CONTENT)) {
					Node contentNode = fileNode.getNode(JcrConstants.JCR_CONTENT);
					Binary bin = contentNode.getProperty(JcrConstants.JCR_DATA)
							.getBinary();
					InputStream in = bin.getStream();
					int len = (int) bin.getSize();
					byte[] buffer = new byte[len];
					int offset = 0;
					int cnt = 0;
					while ((cnt = in.read(buffer, offset, len - offset)) >= 0) {
						offset += cnt;
					}

					Image result = new Image();
					result.setContent(buffer);
					result.setContentType(contentNode.getProperty(
							JcrConstants.JCR_MIME_TYPE).getString());
					result.setName(key);
					result.setSize(bin.getSize());
					return result;
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

	public void setImage(String relPath, FileItem file) {
		Session session = null;
		try {
			Repository repository = JcrRepository.lookup();
			session = repository.login();
			if (file == null || file.getSize() == 0) {
				String absPath = JcrContentHierarchy
						.getAbsoluteImagePath(relPath);
				if (session.nodeExists(absPath)) {
					session.removeItem(absPath);
				}
			} else {
				JcrContentHierarchy hierarchy = new JcrContentHierarchy(session);
				Node fileNode = hierarchy.getOrCreateFile(hierarchy.getContentRootNode(), relPath);
				Node resourceNode = fileNode.hasNode(JcrConstants.JCR_CONTENT) ? fileNode
						.getNode(JcrConstants.JCR_CONTENT) : fileNode.addNode(
						JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);

				resourceNode.setProperty(JcrConstants.JCR_MIME_TYPE,
						file.getContentType());
				resourceNode.setProperty("jcr:lastModified", session.getValueFactory()
						.createValue(Calendar.getInstance()));
				InputStream in = file.getInputStream();
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}

	public void removeImage(String relPath) {
		setImage(relPath, null);
	}

	public List<String> getImages() {
		Session session = null;
		try {
			Repository repository = JcrRepository.lookup();
			session = repository.login();
			return new JcrContentHierarchy(session).listResources(JcrContentHierarchy.IMAGE_ROOT_PATH);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}

}
