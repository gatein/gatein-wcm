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

package org.gatein.wcm.services.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmAuthorizationException;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.WcmLockException;
import org.gatein.wcm.domain.*;
import org.gatein.wcm.portlet.util.ParseDates;
import org.gatein.wcm.services.WcmService;
import org.gatein.wcm.services.impl.export.ExportBuilder;
import org.gatein.wcm.services.impl.export.ImportParser;

/**
 * Implementation of WcmService public API.
 * EJB3 Stateless to manage transactions inside container and pooling.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
@Stateless
public class WcmServiceImpl implements WcmService {

    private static final Logger log = Logger.getLogger(WcmServiceImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    /**
     * @see WcmService#create(org.gatein.wcm.domain.Category, org.gatein.wcm.domain.UserWcm)
     */
	@Override
	public void create(Category cat, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (cat == null || user == null) return;
		if (cat.getParent() == null) {
			for (String group : user.getWriteGroups()) {
				Acl write = new Acl(group, Wcm.ACL.WRITE);
				write.setCategory(cat);
				cat.add(write);
			}			
		} else {
			if (user.canWrite(cat.getParent())) {
                Set<Acl> parentAcls = cat.getParent().getAcls();
				for (Acl parentAcl : parentAcls) {
					Acl childAcl = new Acl(parentAcl.getPrincipal(), parentAcl.getPermission());
					childAcl.setCategory(cat);
					cat.add(childAcl);
				}
			} else {
				throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + cat.getParent());
			}
		}
		try {
			em.persist(cat);			
		} catch (Exception e) {
			throw new WcmException(e);
		}			
	}

    /**
     * @see WcmService#update(org.gatein.wcm.domain.Category, org.gatein.wcm.domain.UserWcm)
     */
	@Override
	public void update(Category cat, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (cat == null  || user == null) return;
		if (!user.canWrite(cat)) {
			throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + cat);
		}
		try {
			em.merge(cat);
		} catch (Exception e) {
			throw new WcmException(e);
		}			
	}
	
	private void deleteOnCascade(Category cat, UserWcm user) throws Exception {
		List<Category> children = findChildren(cat, user);
		if (children != null && children.size() > 0) {
			for (Category child : children) {
                if (user.canWrite(child)) {
                    deleteOnCascade(child, user);
                } else {
                    // If I can WRITE in parent but NOT in child, I will detach child from parent
                    child.setParent(null);
                    em.merge(child);
                }
			}
		}
		em.remove(cat);
	}

    /**
     * @see WcmService#deleteCategory(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void deleteCategory(Long id, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (id == null || user == null) return;
        Category cat = em.find(Category.class, id);
        if (!user.canWrite(cat)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + cat);
        }
        try {
            deleteOnCascade(cat, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findCategories(org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Category> findCategories(UserWcm user) throws WcmException {
        if (user == null) return null;
        try {
            List<Category> result = em.createNamedQuery("listAllCategories", Category.class)
                    .getResultList();
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    private List aclFilter(List col, UserWcm user) {
		if (col == null || user == null) return null;
		List filtered = new ArrayList();		
		for (Object o : col) {
			if (o instanceof Category) {
				Category c = ((Category)o);
                if (user.canRead(c)) filtered.add(c);
            } else if (o instanceof Post) {
				Post p = ((Post)o);
                if (user.canRead(p)) filtered.add(p);
            } else if (o instanceof Upload) {
                Upload u = ((Upload)o);
                if (user.canRead(u)) filtered.add(u);
            }
		}
		return filtered;
	}

    private List<Category> categoryFilter(List<Category> categories, Character type) {
        if (categories == null) return null;
        if (type == null) return categories;
        List<Category> filtered = new ArrayList<Category>();
        for (Category c: categories) {
            if (c.getType() == type) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    private List statusFilter(List col, Character status) {
        if (col == null) return null;
        List filtered = new ArrayList();
        for (Object o : col) {
            if (o instanceof Post) {
                if (((Post)o).getPostStatus().equals(status)) {
                    filtered.add(o);
                }
            }
        }
        return filtered;
    }

    private List<Post> localeFilter(List<Post> list, String locale) throws Exception {
        if (list == null) return list;
        if (locale == null || locale.length() == 0) return list;
        List<Post> filtered = new ArrayList<Post>();
        for (Post p : list) {
            if (p.getLocale() == null || p.getLocale().equals(locale)) {
                filtered.add(p);
            } else {
                RelationshipPK pk = new RelationshipPK();
                pk.setOriginId(p.getId());
                pk.setKey(locale);
                pk.setType(Wcm.RELATIONSHIP.POST);
                Relationship relationship = em.find(Relationship.class, pk);
                if (relationship != null) {
                    Post pr = em.find(Post.class, relationship.getAliasId());
                    if (pr != null) {
                        filtered.add(pr);
                    } else {
                        filtered.add(p);
                    }
                } else {
                    filtered.add(p);
                }
            }
        }
        return filtered;
    }

    /**
     * @see WcmService#findChildren(org.gatein.wcm.domain.Category, org.gatein.wcm.domain.UserWcm)
     */
	@Override
	public List<Category> findChildren(Category cat, UserWcm user) throws WcmException {
		if (cat == null || cat.getId() == null) return null;
		try {
			List<Category> result = em.createNamedQuery("listCategoriesChildren", Category.class)
					.setParameter("id", cat.getId())
					.getResultList();
            result = aclFilter(result, user);
            for (Category c : result) {
                List<Category> children = findChildren(c, user);
                if (children != null) {
                    c.setNumChildren(children.size());
                }
            }
			return result;
		} catch (Exception e) {
			throw new WcmException(e);
		}
	}

    /**
     * @see WcmService#findChildren(org.gatein.wcm.domain.Category, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Category> findChildren(Long id, UserWcm user) throws WcmException {
        if (id == null) return null;
        try {
            List<Category> result = em.createNamedQuery("listCategoriesChildren", Category.class)
                    .setParameter("id", id)
                    .getResultList();
            result = aclFilter(result, user);
            for (Category c : result) {
                List<Category> children = findChildren(c, user);
                if (children != null) {
                    c.setNumChildren(children.size());
                }
            }
            return result;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findCategories(Character, org.gatein.wcm.domain.UserWcm)
     */
	@Override
	public List<Category> findCategories(Character type, UserWcm user)
			throws WcmException {
		if (user == null) return null;
		try {
			List<Category> result = em.createNamedQuery("listCategoriesType", Category.class)
					.setParameter("type", type)
					.getResultList();
			return aclFilter(result, user);
		} catch (Exception e) {
			throw new WcmException(e);
		}
	}

    /**
     * @see WcmService#findRootCategories(org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Category> findRootCategories(UserWcm user) throws WcmException {
        if (user == null) return null;
        try {
            List<Category> result = em.createNamedQuery("listRootCategories", Category.class)
                    .getResultList();
            result = aclFilter(result, user);
            for (Category c : result) {
                List<Category> children = findChildren(c, user);
                if (children != null) {
                    c.setNumChildren(children.size());
                }
            }
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findChildren(String, Character, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Category> findChildren(String path, Character type, UserWcm user) throws WcmException {
        List<Category> children = null;
        if (user == null) return null;
        if (path == null || path.length() == 0) path = "/";
        try {
            String name = child(path);
            List<Category> parents = null;
            Category parent = null;
            if (name != null && name.length() > 0) {
                parents = em.createNamedQuery("listCategoriesName", Category.class)
                            .setParameter("name", name)
                            .getResultList();
                if (parents != null && parents.size() > 1) {
                    for (Category c : parents) {
                        if (hasPath(c, path)) {
                            parent = c;
                        }
                    }
                } else if (parents != null && parents.size() == 1) {
                    parent = parents.get(0);
                }
                if (parent != null) {
                    children = findChildren(parent, user);
                    children = aclFilter(children, user);
                    if (type == null) {
                        return children;
                    } else {
                        children = categoryFilter(children, type);
                    }
                }
            } else {
                children = findRootCategories(user);
                children = categoryFilter(children, type);
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
        return children;
    }

    /**
     * @see WcmService#findCategory(String, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public Category findCategory(String path, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (path == null || path.length() == 0) path = "/";
        Category output = null;
        try {
            String name = child(path);
            List<Category> candidates = null;
            if (name != null && name.length() > 0) {
                candidates = em.createNamedQuery("listCategoriesName", Category.class)
                        .setParameter("name", name)
                        .getResultList();
                if (candidates != null && candidates.size() > 1) {
                    for (Category c : candidates) {
                        if (hasPath(c, path)) {
                            output = c;
                        }
                    }
                } else if (candidates != null && candidates.size() == 1) {
                    output = candidates.get(0);
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
        return output;
    }

    private boolean hasPath(Category c, String path) {
        if (path == null || path.length() == 0) return false;
        if (c.getName().equals(child(path))) {
            if (c.getParent() == null && parent(path).length() == 0) {
                return true;
            } else {
                return hasPath(c.getParent(), parent(path));
            }
        } else {
            return false;
        }
    }

    /**
     * @see WcmService#findCategory(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public Category findCategory(Long id, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (id == null) return null;
        try {
            Category cat = em.find(Category.class, id);
            if (user.canRead(cat))
                return cat;
            else
                return null;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#create(org.gatein.wcm.domain.Post, org.gatein.wcm.domain.UserWcm)
     */
    @Override
	public void create(Post post, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (post == null || user == null) return;
		post.setAuthor(user.getUsername());
		for (String group : user.getWriteGroups()) {
			Acl write = new Acl(group, Wcm.ACL.WRITE);
			write.setPost(post);
			post.add(write);
		}					
		try {
			em.persist(post);			
		} catch (Exception e) {
			throw new WcmException(e);
		}			
	}

    /**
     * @see WcmService#add(org.gatein.wcm.domain.Post, org.gatein.wcm.domain.Category, org.gatein.wcm.domain.UserWcm)
     */
    @Override
	public void add(Post post, Category cat, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (post == null || cat == null || user == null) return;
		if (!user.canWrite(cat)) {
			throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + cat);
		}
        if (!user.canWrite(post)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Post " + post);
        }
		// cat.add(post); // Category -> Posts is lazy, findPosts(Category) is used for that instead to browse into the relationship
		post.add(cat);
		try {
			post = em.merge(post);
			cat = em.merge(cat);
            if (!cat.getPosts().contains(post))
                cat.getPosts().add(post);
            // We add category parents to post relationship
            Category parent = cat.getParent();
            while (parent != null) {
                if (!parent.getPosts().contains(post)) {
                    parent.getPosts().add(post);
                    post.add(parent);
                }
                parent = parent.getParent();
            }
            em.flush();
		} catch (Exception e) {
			throw new WcmException(e);
		}			
	}

    /**
     * @see WcmService#update(org.gatein.wcm.domain.Post, org.gatein.wcm.domain.UserWcm)
     */
	@Override
	public void update(Post post, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (post == null  || user == null) return;
		if (!user.canWrite(post)) {
			throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Post " + post);
		}
		try {
            Long nVersion = (Long)em.createNamedQuery("maxPostVersion")
                    .setParameter("postid", post.getId())
                    .getResultList()
                    .get(0);
            if (nVersion == null || nVersion < post.getVersion()) {
                Post postOrig = em.find(Post.class, post.getId());
                PostHistory postVersion = createVersion(postOrig, postOrig.getVersion());
                em.persist(postVersion);
            }

            post.setAuthor(user.getUsername());
			post.setModified(Calendar.getInstance());
            Long nextVersion = Math.max((nVersion == null ? 0 : nVersion) + 1, post.getVersion() + 1);
			post.setVersion(nextVersion);
			em.merge(post);
		} catch (Exception e) {
			throw new WcmException(e);
		}			
	}

    /**
     * @see WcmService#deletePost(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void deletePost(Long id, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (id == null || user == null) return;
        Post post = em.getReference(Post.class, id);
        if (!user.canWrite(post)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Post " + post);
        }
        try {
            for (Category cat : post.getCategories()) {
                cat.remove(post);
                post.remove(cat);
                em.merge(cat);
            }
            Long nVersion = (Long)em.createNamedQuery("maxPostVersion")
                    .setParameter("postid", post.getId())
                    .getResultList()
                    .get(0);
            PostHistory postVersion = createVersion(post, nVersion == null ? 0 : nVersion + 1);
            postVersion.setDeleted(Calendar.getInstance());
            em.persist(postVersion);
            em.remove(post);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findPost(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public Post findPost(Long id, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (id == null) return null;
        try {
            Post p = em.find(Post.class, id);
            if (p != null && user.canRead(p))
                return p;
            else
                return null;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findPost(Long, String, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public Post findPost(Long id, String locale, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (id == null) return null;
        try {
            Post p = em.find(Post.class, id);
            if (p != null && p.getLocale() != null && !p.getLocale().equals(locale)) {
                RelationshipPK pk = new RelationshipPK();
                pk.setOriginId(id);
                pk.setKey(locale);
                pk.setType(Wcm.RELATIONSHIP.POST);
                Relationship relationship = em.find(Relationship.class, pk);
                if (relationship != null) {
                    Post pr = em.find(Post.class, relationship.getAliasId());
                    if (pr != null) {
                        p = pr;
                    }
                }
            }
            if (p != null && user.canRead(p))
                return p;
            else
                return null;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findPosts(org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Post> findPosts(UserWcm user) throws WcmException {
        if (user == null) return null;
        try {
            List<Post> result = em.createNamedQuery("listAllPosts", Post.class)
                    .getResultList();
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findPosts(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Post> findPosts(Long categoryId, UserWcm user) throws WcmException {

        if (user == null) return null;
        if (categoryId == null) return null;
        try {
            Category cat = em.find(Category.class, categoryId);
            if (cat == null) return null;
            List<Post> result = new ArrayList<Post>(cat.getPosts());
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findPosts(Long, Character, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Post> findPosts(Long categoryId, Character status, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (categoryId == null) return null;
        if (status == null) return null;
        try {
            Category cat = em.find(Category.class, categoryId);
            if (cat == null) return null;
            List<Post> result = new ArrayList<Post>(cat.getPosts());
            result = statusFilter(result, status);
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findPosts(Long, String, Character, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Post> findPosts(Long categoryId, String locale, Character status, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (categoryId == null) return null;
        if (status == null) return null;
        try {
            Category cat = em.find(Category.class, categoryId);
            if (cat == null) return null;
            List<Post> result = new ArrayList<Post>(cat.getPosts());
            result = statusFilter(result, status);
            result = localeFilter(result, locale);
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findPosts(String, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Post> findPosts(String filterName, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (filterName == null) return null;
        try {
            List<Post> result = em.createNamedQuery("listPostsName", Post.class)
                    .setParameter("title", "%" + filterName.toUpperCase() + "%")
                    .getResultList();
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#versionsPost(Long, UserWcm)
     */
    @Override
    public List<Long> versionsPost(Long postId, UserWcm user) throws WcmException {
        if (postId == null) return null;
        try {
            List<Long> result = null;
            Post post = findPost(postId, user);
            if (post != null) {
                result = em.createNamedQuery("versionsPost")
                    .setParameter("postid", postId)
                    .getResultList();
                if (!result.contains(post.getVersion())) {
                    result.add(0, post.getVersion());
                }
            }
            return result;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#changeVersionPost(Long, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void changeVersionPost(Long postId, Long version, UserWcm user) throws WcmException {
        if (postId == null || version == null || user == null) return;
        try {
            Post post = findPost(postId, user);
            if (post != null && user.canWrite(post)) {
                if (!post.getVersion().equals(version)) {
                    List<Long> versions = em.createNamedQuery("versionsPost")
                            .setParameter("postid", postId)
                            .getResultList();
                    if (versions != null && !versions.contains(post.getVersion())) {
                        PostHistory postHistoryCurrent = createVersion(post, post.getVersion());
                        em.persist(postHistoryCurrent);
                    }
                    PostHistoryPK key = new PostHistoryPK();
                    key.setId(postId);
                    key.setVersion(version);
                    PostHistory postH = em.find(PostHistory.class, key);
                    if (postH != null) {
                        post.setTitle(postH.getTitle());
                        post.setExcerpt(postH.getExcerpt());
                        post.setContent(postH.getContent());
                        post.setVersion(postH.getVersion());
                        post.setAuthor(user.getUsername());
                        post.setModified(Calendar.getInstance());
                        post.setLocale(postH.getLocale());
                        post.setPostStatus(postH.getPostStatus());
                        em.merge(post);
                    }
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#remove(org.gatein.wcm.domain.Acl, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void remove(Acl acl, UserWcm user) throws WcmException {
        if (acl == null) return;
        if (user == null) return;
        try {
            if (acl.getPost() != null) {
                Post p = em.find(Post.class, acl.getPost().getId());
                if (!user.canWrite(p)) {
                    throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Post " + p);
                }
                Acl pAcl = em.find(Acl.class, acl.getId());
                p.getAcls().remove(pAcl);
                pAcl.setPost(null);
                em.remove(pAcl);
            } else if (acl.getUpload() != null) {
                Upload u = em.find(Upload.class, acl.getUpload().getId());
                if (!user.canWrite(u)) {
                    throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Upload " + u);
                }
                Acl pAcl = em.find(Acl.class, acl.getId());
                u.getAcls().remove(pAcl);
                pAcl.setUpload(null);
                em.remove(pAcl);
            } else if (acl.getCategory() != null) {
                Category c = em.find(Category.class, acl.getCategory().getId());
                if (!user.canWrite(c)) {
                    throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + c);
                }
                Acl pAcl = em.find(Acl.class, acl.getId());
                c.getAcls().remove(pAcl);
                pAcl.setCategory(null);
                em.remove(pAcl);
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#remove(org.gatein.wcm.domain.Comment, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void remove(Comment c, UserWcm user) throws WcmException {
        if (c == null) return;
        if (c.getId() == null) return;
        if (c.getPost() == null) return;
        if (user == null) return;
        try {
            Post p = em.find(Post.class, c.getPost().getId());
            if (user.canWrite(p)) {
                    Comment delete = em.find(Comment.class, c.getId());
                    if (delete != null) {
                        delete.setPost(null);
                        p.getComments().remove(delete);
                        em.remove(delete);
                    }
            } else {
                throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Post " + p);
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    private PostHistory createVersion(Post post, Long nVersion) {
		if (post == null || post.getId() == null || post.getVersion() == null) return null;		
		PostHistory copy = new PostHistory();
		copy.setAuthor(post.getAuthor());
		copy.setContent(post.getContent());
		copy.setCreated(post.getCreated());
		copy.setExcerpt(post.getExcerpt());
		copy.setId(post.getId());
		copy.setLocale(post.getLocale());
		copy.setModified(post.getModified());
		copy.setPostStatus(post.getPostStatus());
		copy.setTitle(post.getTitle());
		copy.setVersion(nVersion);
		return copy;		
	}

    /**
     * @see WcmService#add(org.gatein.wcm.domain.Post, org.gatein.wcm.domain.Comment)
     */
    @Override
	public void add(Post post, Comment comment) throws WcmAuthorizationException, WcmException {
		if (post == null || comment == null || post.getId() == null) return;
		if (post.getCommentsStatus().equals(Wcm.COMMENTS.NO_COMMENTS)) {
			throw new WcmAuthorizationException("Post: " + post + " has not COMMENTS enabled ");
		}
		comment.setPost(post);
		try {
			comment.setPost(post);
			em.persist(comment);
			em.merge(post);
		} catch (Exception e) {
			throw new WcmException(e);
		}		
	}

    /**
     * @see WcmService#remove(org.gatein.wcm.domain.Post, org.gatein.wcm.domain.Comment, org.gatein.wcm.domain.UserWcm)
     */
    @Override
	public void remove(Post post, Comment comment, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (post == null || comment == null || post.getId() == null || comment.getId() == null) return;
		if (!post.getComments().contains(comment)) return;
		if (!user.canWrite(post)) {
			throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Post " + post);
		}				
		comment.setPost(null);
		post.getComments().remove(comment);		
		try {
			comment = em.find(Comment.class, comment.getId());
			em.remove(comment);
			em.merge(post);
		} catch (Exception e) {
			throw new WcmException(e);
		}		
	}

    /**
     * @see WcmService#removePostCategory(Long, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void removePostCategory(Long postId, Long catId, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (postId == null || catId == null || user == null) return;
        Post post = em.find(Post.class, postId);
        if (!user.canWrite(post)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Post " + post);
        }
        try {
            Category cat = em.find(Category.class, catId);
            cat.getPosts().remove(post);
            post.getCategories().remove(cat);
            em.merge(cat);
            em.merge(post);
            // Remove Category's children
            List<Category> children = findChildren(cat, user);
            while (post.getCategories().size() > 0 && children.size() > 0) {
                for (Iterator<Category> iterator = children.iterator(); iterator.hasNext();) {
                    Category child = iterator.next();
                    if (post.getCategories().contains(child)) {
                        post.getCategories().remove(child);
                        child.getPosts().remove(post);
                        iterator.remove();
                        List<Category> childrenChild = findChildren(child, user);
                        for (Category cChild : childrenChild) {
                            children.add(cChild);
                        }
                    }
                }
            }
            em.flush();
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#create(org.gatein.wcm.domain.Upload, java.io.InputStream, org.gatein.wcm.domain.UserWcm)
     */
    @Override
	public void create(Upload upload, InputStream is, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (upload == null || user == null) return;
		upload.setUser(user.getUsername());
		for (String group : user.getWriteGroups()) {
			Acl write = new Acl(group, Wcm.ACL.WRITE);
			write.setUpload(upload);
			upload.add(write);
		}					
		try {
			String storedName = UUID.randomUUID().toString();
			copyFile(is, storedName);
			upload.setStoredName(storedName);
			upload.setUser(user.getUsername());
			em.persist(upload);			
		} catch (Exception e) {
			throw new WcmException(e);
		}	
		
	}
	
	private void copyFile(InputStream is, String storedFile) throws Exception {
		if (is == null || storedFile == null) return;
		
		String dirPath;
		String fullPath;
		
		if (System.getProperty(Wcm.UPLOADS.FOLDER) == null) {
			dirPath = Wcm.UPLOADS.DEFAULT;
		} else {
			dirPath = System.getProperty(Wcm.UPLOADS.FOLDER);
		}
		File dir = new File(dirPath);
		if (!dir.exists() && !dir.mkdir()) {
			throw new WcmException("Cannot create dir: " + Wcm.UPLOADS.FOLDER);
		}		
		fullPath = dirPath + File.separator + storedFile;
		
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		
		input = new BufferedInputStream(is);
		output = new BufferedOutputStream(new FileOutputStream(fullPath));
		byte[] buffer = new byte[Wcm.UPLOADS.LENGTH_BUFFER];
	    for (int length = 0; (length = input.read(buffer)) > 0;) {
	        output.write(buffer, 0, length);
	    }
	    input.close();
	    output.flush();
	    output.close();
	}

    /**
     * @see WcmService#update(org.gatein.wcm.domain.Upload, java.io.InputStream, org.gatein.wcm.domain.UserWcm)
     */
    @Override
	public void update(Upload upload, InputStream is, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (upload == null || is == null || user == null || upload.getId() == null) return;
		if (!user.canWrite(upload)) {
			throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Upload " + upload);
		}	
		try {
            Long nVersion = (Long)em.createNamedQuery("maxUploadVersion")
                    .setParameter("uploadid", upload.getId())
                    .getResultList()
                    .get(0);
            if (nVersion == null || nVersion < upload.getVersion()) {
                Upload uploadOrig = em.find(Upload.class, upload.getId());
                UploadHistory uploadVersion = createVersion(uploadOrig, uploadOrig.getVersion());
                em.persist(uploadVersion);
            }


			// upload = em.find(Upload.class, upload.getId());
			String storedName = UUID.randomUUID().toString();
			copyFile(is, storedName);
			upload.setStoredName(storedName);

            Long nextVersion = Math.max((nVersion == null ? 0 : nVersion) + 1, upload.getVersion() + 1);
			upload.setVersion(nextVersion);
			upload.setUser(user.getUsername());
            upload.setModified(Calendar.getInstance());
			em.merge(upload);				
		} catch (Exception e) {
			throw new WcmException(e);
		}		
	}
	
	private UploadHistory createVersion(Upload upload, Long nVersion) {
		if (upload == null) return null;
		UploadHistory version = new UploadHistory();
		version.setCreated(upload.getCreated());
		version.setDescription(upload.getDescription());
		version.setFileName(upload.getFileName());
		version.setId(upload.getId());
		version.setMimeType(upload.getMimeType());
		version.setModified(upload.getModified());
		version.setStoredName(upload.getStoredName());
		version.setUser(upload.getUser());
		version.setVersion(nVersion);
		return version;
	}

    /**
     * @see WcmService#update(org.gatein.wcm.domain.Upload, org.gatein.wcm.domain.UserWcm)
     */
    @Override
	public void update(Upload upload, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (upload == null || user == null || upload.getId() == null) return;
		if (!user.canWrite(upload)) {
			throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Upload " + upload);
		}	
		try {
            Long nVersion = (Long)em.createNamedQuery("maxUploadVersion")
                    .setParameter("uploadid", upload.getId())
                    .getResultList()
                    .get(0);
            if (nVersion == null || nVersion < upload.getVersion()) {
                Upload uploadOrig = em.find(Upload.class, upload.getId());
                UploadHistory uploadVersion = createVersion(uploadOrig, uploadOrig.getVersion());
                em.persist(uploadVersion);
            }

            Long nextVersion = Math.max((nVersion == null ? 0 : nVersion) + 1, upload.getVersion() + 1);
            upload.setVersion(nextVersion);
            upload.setUser(user.getUsername());
            upload.setModified(Calendar.getInstance());
            em.merge(upload);
		} catch (Exception e) {
			throw new WcmException(e);
		}	
	}

    /**
     * @see WcmService#delete(org.gatein.wcm.domain.Upload, org.gatein.wcm.domain.UserWcm)
     */
    @Override
	public void delete(Upload upload, UserWcm user)
			throws WcmAuthorizationException, WcmException {
		if (upload == null || user == null || upload.getId() == null) return;
		if (!user.canWrite(upload)) {
			throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Upload " + upload);
		}			
		try {
            upload = em.getReference(Upload.class, upload.getId());
            Long nVersion = (Long)em.createNamedQuery("maxUploadVersion")
                    .setParameter("uploadid", upload.getId())
                    .getResultList()
                    .get(0);
            UploadHistory version = createVersion(upload, nVersion == null ? 0 : nVersion + 1);
			version.setDeleted(Calendar.getInstance());
			em.persist(version);
            for (Category c : upload.getCategories()) {
                c.getUploads().remove(upload);
            }
			em.remove(upload);
		} catch (Exception e) {
			throw new WcmException(e);
		}
	}

    /**
     * @see WcmService#deleteUpload(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void deleteUpload(Long id, UserWcm user) throws WcmAuthorizationException, WcmException {
        Upload upload = findUpload(id, user);
        delete(upload, user);
    }

    /**
     * @see WcmService#findUploads(org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Upload> findUploads(UserWcm user) throws WcmException {
        if (user == null) return null;
        try {
            List<Upload> result = em.createNamedQuery("listAllUploads", Upload.class)
                    .getResultList();
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findUploads(String, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Upload> findUploads(String filterName, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (filterName == null) return null;
        try {
            List<Upload> result = em.createNamedQuery("listUploadsFileName", Upload.class)
                    .setParameter("fileName", "%" + filterName.toUpperCase() + "%")
                    .setParameter("description", "%" + filterName.toUpperCase() + "%")
                    .getResultList();
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#add(org.gatein.wcm.domain.Upload, org.gatein.wcm.domain.Category, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void add(Upload upload, Category cat, UserWcm user)
            throws WcmAuthorizationException, WcmException {
        if (upload == null || cat == null || user == null) return;
        if (!user.canWrite(cat)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + cat);
        }
        if (!user.canWrite(upload)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Upload " + upload);
        }
        upload.add(cat);
        try {
            upload = em.merge(upload);
            cat = em.merge(cat);
            if (!cat.getUploads().contains(upload))
                cat.getUploads().add(upload);
            // We add category parents to upload relationship
            Category parent = cat.getParent();
            while (parent != null) {
                if (!parent.getUploads().contains(upload)) {
                    parent.getUploads().add(upload);
                    upload.getCategories().add(parent);
                }
                parent = parent.getParent();
            }
            em.flush();
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#removeUploadCategory(Long, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void removeUploadCategory(Long uploadId, Long catId, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (uploadId == null || catId == null || user == null) return;
        Upload upload = em.find(Upload.class, uploadId);
        if (!user.canWrite(upload)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Upload " + upload);
        }
        try {
            Category cat = em.find(Category.class, catId);
            cat.getUploads().remove(upload);
            upload.getCategories().remove(cat);
            em.merge(cat);
            em.merge(upload);
            // Remove Category's children
            List<Category> children = findChildren(cat, user);
            while (upload.getCategories().size() > 0 && children.size() > 0) {
                for (Iterator<Category> iterator = children.iterator(); iterator.hasNext();) {
                    Category child = iterator.next();
                    if (upload.getCategories().contains(child)) {
                        upload.getCategories().remove(child);
                        child.getUploads().remove(upload);
                        iterator.remove();
                        List<Category> childrenChild = findChildren(child, user);
                        for (Category cChild : childrenChild) {
                            children.add(cChild);
                        }
                    }
                }
            }
            em.flush();
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findUpload(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public Upload findUpload(Long id, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (id == null) return null;
        try {
            Upload u = em.find(Upload.class, id);
            if (u!=null && user.canRead(u))
                return u;
            else
                return null;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findUploads(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Upload> findUploads(Long categoryId, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (user == null) return null;
        try {
            Category cat = em.find(Category.class, categoryId);
            if (cat == null) return null;
            List<Upload> result = new ArrayList<Upload>(cat.getUploads());
            return aclFilter(result, user);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#versionsUpload(Long, UserWcm)
     */
    @Override
    public List<Long> versionsUpload(Long uploadId, UserWcm user) throws WcmException {
        if (uploadId == null) return null;
        try {
            List<Long> result = null;
            Upload upload = findUpload(uploadId, user);
            if (upload != null) {
                result = em.createNamedQuery("versionsUpload")
                        .setParameter("uploadid", uploadId)
                        .getResultList();
                if (!result.contains(upload.getVersion())) {
                    result.add(0, upload.getVersion());
                }
            }
            return result;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#changeVersionUpload(Long, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void changeVersionUpload(Long uploadId, Long version, UserWcm user) throws WcmException {
        if (uploadId == null || version == null || user == null) return;
        try {
            Upload upload = findUpload(uploadId, user);
            if (upload != null && user.canWrite(upload)) {
                if (!upload.getVersion().equals(version)) {
                    List<Long> versions = em.createNamedQuery("versionsUpload")
                            .setParameter("uploadid", uploadId)
                            .getResultList();
                    if (versions != null && !versions.contains(upload.getVersion())) {
                        UploadHistory uploadHistoryCurrent = createVersion(upload, upload.getVersion());
                        em.persist(uploadHistoryCurrent);
                    }
                    UploadHistoryPK key = new UploadHistoryPK();
                    key.setId(uploadId);
                    key.setVersion(version);
                    UploadHistory uploadH = em.find(UploadHistory.class, key);
                    if (uploadH != null) {
                        upload.setFileName(uploadH.getFileName());
                        upload.setDescription(uploadH.getDescription());
                        upload.setMimeType(uploadH.getMimeType());
                        upload.setVersion(uploadH.getVersion());
                        upload.setStoredName(uploadH.getStoredName());
                        upload.setUser(user.getUsername());
                        upload.setModified(Calendar.getInstance());
                        em.merge(upload);
                    }
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#create(org.gatein.wcm.domain.Template, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void create(Template temp, UserWcm user)
            throws WcmAuthorizationException, WcmException {
        if (temp == null || user == null) return;
        if (!user.isManager()) {
            throw new WcmAuthorizationException("User: " + user + " has not ADMIN rights to WRITE Template ");
        }
        try {
            em.persist(temp);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findTemplates(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Template> findTemplates(Long categoryId, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (user == null) return null;
        try {
            Category cat = em.find(Category.class, categoryId);
            if (cat == null) return null;
            List<Template> result = new ArrayList<Template>(cat.getTemplates());
            // return aclFilter(result, user);
            return result; // In this version we don't have ACL on Template entities
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findTemplates(org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Template> findTemplates(UserWcm user) throws WcmException {
        if (user == null) return null;
        try {
            List<Template> result = em.createNamedQuery("listAllTemplates", Template.class)
                    .getResultList();
            // return aclFilter(result, user);
            return result; // In this version we don't have ACL on Template entities
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findTemplates(String, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Template> findTemplates(String filterName, UserWcm user) throws WcmException {
        if (user == null) return null;
        try {
            List<Template> result = em.createNamedQuery("listTemplatesName", Template.class)
                    .setParameter("name", "%" + filterName.toUpperCase() + "%")
                    .getResultList();
            // return aclFilter(result, user);
            return result; // In this version we don't have ACL on Template entities
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findTemplate(Long, org.gatein.wcm.domain.UserWcm)
     */
    public Template findTemplate(Long id, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (id == null) return null;
        try {
            Template t = em.find(Template.class, id);
            // In this version we don't have ACL on Template entities
            return t;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findTemplate(Long, String, org.gatein.wcm.domain.UserWcm)
     */
    public Template findTemplate(Long id, String locale, UserWcm user) throws WcmException {
        if (user == null) return null;
        if (id == null) return null;
        try {
            Template t = em.find(Template.class, id);
            if (t != null && t.getLocale() != null && !t.getLocale().equals(locale)) {
                RelationshipPK pk = new RelationshipPK();
                pk.setOriginId(id);
                pk.setKey(locale);
                pk.setType(Wcm.RELATIONSHIP.TEMPLATE);
                Relationship relationship = em.find(Relationship.class, pk);
                if (relationship != null) {
                    Template tr = em.find(Template.class, relationship.getAliasId());
                    if (tr != null) {
                        t = tr;
                    }
                }
            }
            // In this version we don't have ACL on Template entities
            return t;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }


    /**
     * @see WcmService#add(org.gatein.wcm.domain.Template, org.gatein.wcm.domain.Category, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void add(Template template, Category cat, UserWcm user)
            throws WcmAuthorizationException, WcmException {
        if (template == null || cat == null || user == null) return;
        if (!user.canWrite(cat)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + cat);
        }
        try {
            template.getCategories().add(cat);
            template = em.merge(template);
            cat = em.merge(cat);
            if (!cat.getTemplates().contains(template))
                cat.getTemplates().add(template);
            // We add category parents to post relationship
            Category parent = cat.getParent();
            while (parent != null) {
                if (!parent.getTemplates().contains(template)) {
                    parent.getTemplates().add(template);
                    template.getCategories().add(parent);
                }
                parent = parent.getParent();
            }
            em.flush();
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#deleteTemplate(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void deleteTemplate(Long id, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (id == null || user == null) return;
        if (!user.isManager()) {
            throw new WcmAuthorizationException("User: " + user + " has not ADMIN rights to WRITE Template ");
        }
        try {
            Template template = em.getReference(Template.class, id);
            // Template object has not versioning functionality
            for (Category c : template.getCategories()) {
                c.getTemplates().remove(template);
            }
            Long nVersion = (Long)em.createNamedQuery("maxTemplateVersion")
                    .setParameter("templateid", template.getId())
                    .getResultList()
                    .get(0);
            TemplateHistory templateVersion = createVersion(template, nVersion == null ? 0 : nVersion + 1);
            templateVersion.setDeleted(Calendar.getInstance());
            em.persist(templateVersion);
            em.remove(template);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#removeTemplateCategory(Long, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void removeTemplateCategory(Long templateId, Long catId, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (templateId == null || catId == null || user == null) return;
        Template template = em.find(Template.class, templateId);
        Category cat = em.find(Category.class, catId);
        if (!user.canWrite(cat)) {
            throw new WcmAuthorizationException("User: " + user + " has not WRITE rights on Category " + cat);
        }
        try {
            cat.getTemplates().remove(template);
            template.getCategories().remove(cat);
            em.merge(cat);
            em.merge(template);
            // Remove Category's children
            List<Category> children = findChildren(cat, user);
            while (template.getCategories().size() > 0 && children.size() > 0) {
                for (Iterator<Category> iterator = children.iterator(); iterator.hasNext();) {
                    Category child = iterator.next();
                    if (template.getCategories().contains(child)) {
                        template.getCategories().remove(child);
                        child.getTemplates().remove(template);
                        iterator.remove();
                        List<Category> childrenChild = findChildren(child, user);
                        for (Category cChild : childrenChild) {
                            children.add(cChild);
                        }
                    }
                }
            }
            em.flush();
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#update(org.gatein.wcm.domain.Template, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void update(Template template, UserWcm user)
            throws WcmAuthorizationException, WcmException {
        if (template == null || user == null || template.getId() == null) return;
        if (!user.isManager()) {
            throw new WcmAuthorizationException("User: " + user + " has not ADMIN rights to WRITE Template ");
        }
        try {
            Long nVersion = (Long)em.createNamedQuery("maxTemplateVersion")
                    .setParameter("templateid", template.getId())
                    .getResultList()
                    .get(0);
            if (nVersion == null || nVersion < template.getVersion()) {
                Template templateOrig = em.find(Template.class, template.getId());
                TemplateHistory templateVersion = createVersion(templateOrig, templateOrig.getVersion());
                em.persist(templateVersion);
            }

            Long nextVersion = Math.max((nVersion == null ? 0 : nVersion) + 1, template.getVersion() + 1);
            template.setVersion(nextVersion);
            template.setUser(user.getUsername());
            template.setModified(Calendar.getInstance());
            em.merge(template);
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    private TemplateHistory createVersion(Template template, Long nVersion) {
        if (template == null) return null;
        TemplateHistory version = new TemplateHistory();
        version.setName(template.getName());
        version.setCreated(template.getCreated());
        version.setModified(template.getModified());
        version.setUser(template.getUser());
        version.setVersion(nVersion);
        version.setContent(template.getContent());
        version.setLocale(template.getLocale());
        version.setId(template.getId());
        return version;
    }


    /**
     * @see WcmService#versionsTemplate(Long, UserWcm)
     */
    @Override
    public List<Long> versionsTemplate(Long templateId, UserWcm user) throws WcmException {
        if (templateId == null) return null;
        try {
            List<Long> result = null;
            Template template = findTemplate(templateId, user);
            if (template != null) {
                result = em.createNamedQuery("versionsTemplate")
                        .setParameter("templateid", templateId)
                        .getResultList();
                if (!result.contains(template.getVersion())) {
                    result.add(0, template.getVersion());
                }
            }
            return result;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#changeVersionTemplate(Long, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void changeVersionTemplate(Long templateId, Long version, UserWcm user) throws WcmException {
        if (templateId == null || version == null || user == null) return;
        try {
            Template template = findTemplate(templateId, user);
            if (template != null) {
                if (!template.getVersion().equals(version)) {
                    List<Long> versions = em.createNamedQuery("versionsTemplate")
                            .setParameter("templateid", templateId)
                            .getResultList();
                    if (versions != null && !versions.contains(template.getVersion())) {
                        TemplateHistory templateHistoryCurrent = createVersion(template, template.getVersion());
                        em.persist(templateHistoryCurrent);
                    }
                    TemplateHistoryPK key = new TemplateHistoryPK();
                    key.setId(templateId);
                    key.setVersion(version);
                    TemplateHistory templateH = em.find(TemplateHistory.class, key);
                    if (templateH != null) {
                        template.setName(templateH.getName());
                        template.setLocale(templateH.getLocale());
                        template.setContent(templateH.getContent());
                        template.setVersion(templateH.getVersion());
                        template.setCreated(templateH.getCreated());
                        template.setModified(Calendar.getInstance());
                        em.merge(template);
                    }
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#createRelationshipPost(Long, String, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void createRelationshipPost(Long originId, String key, Long targetId, UserWcm user) throws WcmException {
        if (originId == null || key == null || targetId == null || user == null) return;
        Post post = findPost(originId, user);
        try {
            if (post != null && user.canWrite(post)) {
                RelationshipPK pk = new RelationshipPK();
                pk.setOriginId(originId);
                pk.setKey(key);
                Relationship existing = em.find(Relationship.class, pk);
                if (existing == null) {
                    Relationship newRelationship = new Relationship();
                    newRelationship.setOriginId(originId);
                    newRelationship.setKey(key);
                    newRelationship.setAliasId(targetId);
                    newRelationship.setType(Wcm.RELATIONSHIP.POST);
                    em.persist(newRelationship);
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#removeRelationshipPost(Long, String, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void removeRelationshipPost(Long originId, String key, UserWcm user) throws WcmException {
        if (originId == null || key == null || user == null) return;
        Post post = findPost(originId, user);
        try {
            if (post != null && user.canWrite(post)) {
                RelationshipPK pk = new RelationshipPK();
                pk.setOriginId(originId);
                pk.setKey(key);
                pk.setType(Wcm.RELATIONSHIP.POST);
                Relationship existing = em.find(Relationship.class, pk);
                if (existing != null) {
                    em.remove(existing);
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findRelationshipsPost(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Relationship> findRelationshipsPost(Long postId, UserWcm user) throws WcmException {
        if (postId == null || user == null) return null;
        Post post = findPost(postId, user);
        if (post != null) {
            List<Relationship> relations = em.createNamedQuery("listRelationships")
                    .setParameter("originId", postId)
                    .setParameter("type", Wcm.RELATIONSHIP.POST)
                    .getResultList();
            return relations;
        }
        return null;
    }

    /**
     * @see WcmService#findPostsRelationshipPost(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Post> findPostsRelationshipPost(Long postId, UserWcm user) throws WcmException {
        if (postId == null || user == null) return null;
        Post post = findPost(postId, user);
        if (post != null) {
            List<Post> postsRelations = em.createNamedQuery("listPostsRelationships")
                    .setParameter("originId", postId)
                    .setParameter("type", Wcm.RELATIONSHIP.POST)
                    .getResultList();
            return postsRelations;
        }
        return null;
    }

    /**
     * @see WcmService#createRelationshipTemplate(Long, String, Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void createRelationshipTemplate(Long originId, String key, Long targetId, UserWcm user) throws WcmException {
        if (originId == null || key == null || targetId == null || user == null) return;
        Template template = findTemplate(originId, user);
        try {
            if (template != null && user.isManager()) {
                RelationshipPK pk = new RelationshipPK();
                pk.setOriginId(originId);
                pk.setKey(key);
                pk.setType(Wcm.RELATIONSHIP.TEMPLATE);
                Relationship existing = em.find(Relationship.class, pk);
                if (existing == null) {
                    Relationship newRelationship = new Relationship();
                    newRelationship.setOriginId(originId);
                    newRelationship.setKey(key);
                    newRelationship.setAliasId(targetId);
                    newRelationship.setType(Wcm.RELATIONSHIP.TEMPLATE);
                    em.persist(newRelationship);
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#removeRelationshipTemplate(Long, String, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void removeRelationshipTemplate(Long originId, String key, UserWcm user) throws WcmException {
        if (originId == null || key == null || user == null) return;
        Template template = findTemplate(originId, user);
        try {
            if (template != null && user.isManager()) {
                RelationshipPK pk = new RelationshipPK();
                pk.setOriginId(originId);
                pk.setKey(key);
                pk.setType(Wcm.RELATIONSHIP.TEMPLATE);
                Relationship existing = em.find(Relationship.class, pk);
                if (existing != null) {
                    em.remove(existing);
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findRelationshipsTemplate(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Relationship> findRelationshipsTemplate(Long templateId, UserWcm user) throws WcmException {
        if (templateId == null || user == null) return null;
        Template template = findTemplate(templateId, user);
        if (template != null) {
            List<Relationship> relations = em.createNamedQuery("listRelationships")
                    .setParameter("originId", templateId)
                    .setParameter("type", Wcm.RELATIONSHIP.TEMPLATE)
                    .getResultList();
            return relations;
        }
        return null;
    }

    /**
     * @see WcmService#findTemplatesRelationshipTemplate(Long, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Template> findTemplatesRelationshipTemplate(Long templateId, UserWcm user) throws WcmException {
        if (templateId == null || user == null) return null;
        Template template = findTemplate(templateId, user);
        if (template != null) {
            List<Template> templatesRelations = em.createNamedQuery("listTemplatesRelationships")
                    .setParameter("originId", templateId)
                    .setParameter("type", Wcm.RELATIONSHIP.TEMPLATE)
                    .getResultList();
            return templatesRelations;
        }
        return null;
    }

    /**
     * @see WcmService#lock(Long, Character, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void lock(Long originId, Character type, UserWcm user) throws WcmLockException, WcmException {
        if (originId == null || type == null|| user == null) {
            throw new WcmException("Illegal lock() invocation");
        }
        try {
            LockPK pk = new LockPK();
            pk.setOriginId(originId);
            pk.setType(type);
            Lock lock = em.find(Lock.class, pk);
            if (lock != null && !lock.getUsername().equals(user.getUsername())) {
                String msg = "Lock for ";
                if (type.equals(Wcm.LOCK.POST)) {
                    msg += " Post ID " + originId;
                } else if (type.equals(Wcm.LOCK.CATEGORY)) {
                    msg += " Category ID " + originId;
                } else if (type.equals(Wcm.LOCK.UPLOAD)) {
                    msg += " Upload ID " + originId;
                } else if (type.equals(Wcm.LOCK.TEMPLATE)) {
                    msg += " Template ID " + originId;
                }
                msg += " by user: " + lock.getUsername() + " at " + ParseDates.parse(lock.getCreated());
                throw new WcmLockException(msg);
            }
            if (lock == null) {
                lock = new Lock();
                lock.setOriginId(originId);
                lock.setType(type);
                lock.setUsername(user.getUsername());
                lock.setCreated(Calendar.getInstance());
                em.persist(lock);
            }
        } catch (WcmLockException e) {
            throw new WcmLockException(e.getMessage());
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#unlock(Long, Character, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void unlock(Long originId, Character type, UserWcm user) throws WcmLockException, WcmException {
        if (originId == null || type == null|| user == null) {
            throw new WcmException("Illegal unlock() invocation");
        }
        try {
            LockPK pk = new LockPK();
            pk.setOriginId(originId);
            pk.setType(type);
            Lock lock = em.find(Lock.class, pk);
            if (lock != null) {
                if (lock.getUsername().equals(user.getUsername())) {
                    em.remove(lock);
                } else {
                    // This exception can be raised if an admin or scheduler deletes a lock and user tries to unlock a different one
                    throw new WcmLockException("Lock only can be unlocked by admin or user: " + lock.getUsername());
                }
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#removeLock(Long, Character, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public void removeLock(Long originId, Character type, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (originId == null || type == null|| user == null) {
            throw new WcmException("Illegal unlock() invocation");
        }
        if (!user.isManager()) {
            throw new WcmAuthorizationException("RemoveLock() is an operation for managers.");
        }
        try {
            LockPK pk = new LockPK();
            pk.setOriginId(originId);
            pk.setType(type);
            Lock lock = em.find(Lock.class, pk);
            if (lock != null) {
                em.remove(lock);
            }
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findLocks(org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public List<Lock> findLocks(UserWcm user) throws WcmAuthorizationException, WcmException {
        if (user == null) {
            throw new WcmException("Illegal findLocks() invocation");
        }
        if (!user.isManager()) {
            throw new WcmAuthorizationException("findLocks() is an operation for managers.");
        }
        try {
            List<Lock> result = em.createNamedQuery("listLocks")
                    .getResultList();
            return result;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#findLocksObjects(java.util.List, org.gatein.wcm.domain.UserWcm)
     */
    @Override
    public Map<Long, Object> findLocksObjects(List<Lock> locks, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (user == null) {
            throw new WcmException("Illegal findLocks() invocation");
        }
        if (!user.isManager()) {
            throw new WcmAuthorizationException("findLocksObjects() is an operation for managers.");
        }
        if (locks == null) return null;
        try {
            Map<Long, Object> result = new HashMap<Long, Object>();
            for (Lock l : locks) {
                if (l.getType().equals(Wcm.LOCK.POST)) {
                    Post post = em.find(Post.class, l.getOriginId());
                    result.put(l.getOriginId(), post);
                } else if (l.getType().equals(Wcm.LOCK.CATEGORY)) {
                    Category cat = em.find(Category.class, l.getOriginId());
                    result.put(l.getOriginId(), cat);
                } else if (l.getType().equals(Wcm.LOCK.UPLOAD)) {
                    Upload upload = em.find(Upload.class, l.getOriginId());
                    result.put(l.getOriginId(), upload);
                } else if (l.getType().equals(Wcm.LOCK.TEMPLATE)) {
                    Template template = em.find(Template.class, l.getOriginId());
                    result.put(l.getOriginId(), template);
                }
            }
            return result;
        } catch (Exception e) {
            throw new WcmException(e);
        }
    }

    /**
     * @see WcmService#exportRepository(org.gatein.wcm.domain.UserWcm)
     */
    public String exportRepository(UserWcm user) throws WcmAuthorizationException, WcmException {
        if (user == null) {
            throw new WcmException("Illegal exportRepository() invocation");
        }
        if (!user.isManager()) {
            throw new WcmAuthorizationException("exportRepository() is an operation for managers.");
        }
        // Zip Name
        String now = ParseDates.parseNow();
        String zipName = System.getProperty(Wcm.UPLOADS.TMP_DIR) + "/gatein-wcm-export-" + now + ".zip";
        ZipOutputStream zos = null;
        FileInputStream in = null;

        try {
            zos = new ZipOutputStream(new FileOutputStream(zipName));

            // Perform export
            ExportBuilder builder = new ExportBuilder();

            List<Category> rCategories = em.createNamedQuery("listAllCategories", Category.class).getResultList();
            List<Comment> rComments = em.createNamedQuery("listAllComments", Comment.class).getResultList();
            List<Post> rPosts = em.createNamedQuery("listAllPosts", Post.class).getResultList();
            List<PostHistory> rPostsHistory = em.createNamedQuery("listAllPostHistory", PostHistory.class).getResultList();
            List<Relationship> rRelationships = em.createNamedQuery("listAllRelationships", Relationship.class).getResultList();
            List<Acl> rAcls = em.createNamedQuery("listAllAcls", Acl.class).getResultList();
            List<Template> rTemplates = em.createNamedQuery("listAllTemplates", Template.class).getResultList();
            List<TemplateHistory> rTemplatesHistory = em.createNamedQuery("listAllTemplateHistory", TemplateHistory.class).getResultList();
            List<Upload> rUploads = em.createNamedQuery("listAllUploads", Upload.class).getResultList();
            List<UploadHistory> rUploadsHistory = em.createNamedQuery("listAllUploadHistory", UploadHistory.class).getResultList();

            StringBuilder export =builder
                    .add(rCategories)
                   .add(rComments)
                   .add(rPosts)
                   .add(rPostsHistory)
                   .add(rRelationships)
                   .add(rAcls)
                   .add(rTemplates)
                   .add(rTemplatesHistory)
                   .add(rUploads)
                   .add(rUploadsHistory)
                   .build();

            // Copy export file.
            ZipEntry zipEntry = new ZipEntry("gatein-wcm.xml");
            zos.putNextEntry(zipEntry);
            IOUtils.write(export, zos, "UTF-8");
            // Copy uploads folder
            File dirUploads = new File(System.getProperty(Wcm.UPLOADS.FOLDER));
            if (dirUploads != null) {
                File[] uploads = dirUploads.listFiles();
                for (int i=0; i < uploads.length; i++) {
                    in = new FileInputStream(uploads[i]);
                    zipEntry = new ZipEntry("uploads/" + uploads[i].getName());
                    zos.putNextEntry(zipEntry);
                    IOUtils.copy(in, zos);
                    in.close();
                }
            }
            zos.close();
        } catch (Exception e) {
            log.warning("Error exporting file. " + e.getMessage());
            e.printStackTrace();
            throw new WcmException(e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(zos);
        }

        return zipName;
    }

    public void importRepository(InputStream importFile, Character strategy, UserWcm user) throws WcmAuthorizationException, WcmException {
        if (user == null) {
            throw new WcmException("Illegal importRepository() invocation");
        }
        if (!user.isManager()) {
            throw new WcmAuthorizationException("importRepository() is an operation for managers.");
        }
        if (importFile == null) {
            throw new WcmException("InputStream cannot be null");
        }

        // Target folder for uploads
        String targetFolder;
        if (System.getProperty(Wcm.UPLOADS.FOLDER) == null) {
            targetFolder = System.getProperty(Wcm.UPLOADS.DEFAULT) + "/wcm/uploads";
        } else {
            targetFolder = System.getProperty(Wcm.UPLOADS.FOLDER);
        }

        // Unzip in a temporal location
        String temporalFolder = System.getProperty(Wcm.UPLOADS.TMP_DIR) + "/import-" + UUID.randomUUID();
        File output = new File(temporalFolder);
        if (!output.exists()) {
            output.mkdirs();
        }
        try {
            unzip(new ZipInputStream(importFile), output);
        } catch (Exception e) {
            throw new WcmException("Error extracting file " + e.getMessage());
        }

        // Parse gatein-wcm.xml file
        String gateinWcm = temporalFolder + "/gatein-wcm.xml";
        File gateinWcmFile = new File(gateinWcm);
        if (!gateinWcmFile.exists()) {
            throw new WcmException("Not gatein-wcm.xml file found in .zip");
        }

        String uploadsFolder = temporalFolder + "/uploads";
        File uploadsFolderFile = new File(uploadsFolder);
        if (!uploadsFolderFile.exists()) {
            throw new WcmException("Not uploads folder found in .zip");
        }

        ImportParser importParser;
        try {
            importParser = new ImportParser(temporalFolder);
        } catch (Exception e) {
            throw new WcmException("Error initializing parser " + e.getMessage());
        }

        try {
            importParser.parse();
        } catch (Exception e) {
            throw new WcmException("Error parsing import file: " + e.getMessage());
        }

        // Update collections
        // Copy uploads images into data location
        if (strategy.equals(Wcm.IMPORT.STRATEGY.NEW)) {
            try {
                deleteAllRepository();
            } catch (Exception e) {
                throw new WcmException("Error deleting all repository " + e.getMessage());
            }
        }
        try {
            Map<Long, Long> mCategories = updateCategories(importParser.getCategories(), strategy);
            Map<Long, Long> mComments = updateComments(importParser.getComments(), strategy);
            Map<Long, Long> mPosts = updatePosts(importParser.getPosts(), strategy);
            Map<Long, Long> mTemplates = updateTemplates(importParser.getTemplates(), strategy);
            Map<Long, Long> mUploads = updateUploads(importParser.getUploads(), strategy, uploadsFolder, targetFolder);
            Map<Long, Long> mAcls = updateSecurity(importParser.getAcls(), strategy);

            updatePostsHistory(importParser.getPostsHistory(), strategy, mPosts);
            updateTemplatesHistory(importParser.getTemplatesHistory(), strategy, mTemplates);
            updateUploadsHistory(importParser.getUploadsHistory(), strategy, uploadsFolder, targetFolder, mUploads);

            updateRelationships(importParser.getRelationships(), strategy, mPosts, mTemplates);
            updateCategoriesParent(importParser.getCategoriesParent(), mCategories);
            updateCategoriesPosts(importParser.getCategoriesPosts(), mCategories, mPosts);
            updateCategoriesTemplates(importParser.getCategoriesTemplates(), mCategories, mTemplates);
            updateCategoriesUploads(importParser.getCategoriesUploads(), mCategories, mUploads);

            updateCommentsPost(importParser.getCommentsPost(), mComments, mPosts);

            updateAclCategories(importParser.getAclsCategories(), mAcls, mCategories);
            updateAclPosts(importParser.getAclsPosts(), mAcls, mPosts);
            updateAclUploads(importParser.getAclsUploads(), mAcls, mUploads);

            updateUploadsLinks(mUploads);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WcmException("Error upading repository " + e.getMessage());
        }

    }

    /*
        Deletes all repository for a new and clean import.
     */
    private void deleteAllRepository() throws Exception {
        String[] queries = {
            "deleteAllAcls",
            "deleteAllCategories",
            "deleteAllComments",
            "deleteAllPosts",
            "deleteAllPostHistory",
            "deleteAllRelationships",
            "deleteAllTemplates",
            "deleteAllTemplateHistory",
            "deleteAllUploads",
            "deleteAllUploadHistory"
        };

        for (String query : queries) {
            try {
                em.createNamedQuery(query).executeUpdate();
            } catch (Exception e) {
                log.warning("Error executing query: " + query);
                throw e;
            }
        }

        em.clear();

        // Delete uploads
        String uploads = System.getProperty(Wcm.UPLOADS.FOLDER);
        File folder = new File(uploads);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File f : files) {
                f.delete();
            }
        }
    }

    private Map<Long, Long> updateCategories(List<Category> categories,
                                        Character strategy)
            throws Exception {
        Map<Long, Long> mCategories = new HashMap<Long, Long>();

        for (Category newC : categories) {
            Category oldC = em.find(Category.class, newC.getId());
            if (oldC == null) {
                Long importId, newId;

                importId = newC.getId();
                newC = em.merge(newC);
                newId = newC.getId();
                // We are trying to import a previous detached object and EM can return a new ID
                // We need to maintain a table with new IDs to update references in next objects
                mCategories.put(importId, newId);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldC.setName(newC.getName());
                    oldC.setType(newC.getType());

                    em.merge(oldC);
                    // Existing object with same ID, but we will add it to the table to use a similiar process for processing
                    mCategories.put(oldC.getId(), oldC.getId());
                }
            }
        }

        return mCategories;
    }

    private Map<Long, Long> updateComments(List<Comment> comments,
                                      Character strategy)
            throws Exception {
        Map<Long, Long> mComments = new HashMap<Long, Long>();

        for (Comment newC : comments) {
            Comment oldC = em.find(Comment.class, newC.getId());
            if (oldC == null) {
                Long importId, newId;

                importId = newC.getId();
                newC = em.merge(newC);
                newId = newC.getId();

                mComments.put(importId, newId);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldC.setAuthor(newC.getAuthor());
                    oldC.setAuthorEmail(newC.getAuthorEmail());
                    oldC.setAuthorUrl(newC.getAuthorUrl());
                    oldC.setContent(newC.getContent());
                    oldC.setCreated(newC.getCreated());
                    oldC.setStatus(newC.getStatus());

                    em.merge(oldC);
                    mComments.put(oldC.getId(), oldC.getId());
                }
            }
        }

        return mComments;
    }

    private Map<Long, Long> updatePosts(List<Post> posts,
                                   Character strategy)
            throws Exception {
        Map<Long, Long> mPosts = new HashMap<Long, Long>();

        for (Post newP : posts) {
            Post oldP = em.find(Post.class, newP.getId());
            if (oldP == null) {
                Long importId, newId;

                importId = newP.getId();
                newP = em.merge(newP);
                newId = newP.getId();

                mPosts.put(importId, newId);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldP.setAuthor(newP.getAuthor());
                    oldP.setContent(newP.getContent());
                    oldP.setCreated(newP.getCreated());
                    oldP.setCommentsStatus(newP.getCommentsStatus());
                    oldP.setExcerpt(newP.getExcerpt());
                    oldP.setLocale(newP.getLocale());
                    oldP.setModified(newP.getModified());
                    oldP.setPostStatus(newP.getPostStatus());
                    oldP.setTitle(newP.getTitle());
                    oldP.setVersion(newP.getVersion());

                    em.merge(oldP);
                    mPosts.put(oldP.getId(), oldP.getId());
                }
            }
        }

        return mPosts;
    }

    private List<PostHistoryPK> updatePostsHistory(List<PostHistory> posts,
                                          Character strategy,
                                          Map<Long, Long> mPosts)
            throws Exception {
        List<PostHistoryPK> mPostsHistory = new ArrayList<PostHistoryPK>();

        for (PostHistory newP : posts) {
            // Update ID reference
            Long newId = mPosts.get(newP.getId());
            if (newId == null) newId = newP.getId();
            newP.setId(newId);

            PostHistoryPK pk = new PostHistoryPK();
            pk.setId(newP.getId());
            pk.setVersion(newP.getVersion());
            PostHistory oldP = em.find(PostHistory.class, pk);
            if (oldP == null) {
                em.persist(newP);
                mPostsHistory.add(pk);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldP.setAuthor(newP.getAuthor());
                    oldP.setContent(newP.getContent());
                    oldP.setCreated(newP.getCreated());
                    oldP.setDeleted(newP.getDeleted());
                    oldP.setExcerpt(newP.getExcerpt());
                    oldP.setLocale(newP.getLocale());
                    oldP.setModified(newP.getModified());
                    oldP.setPostStatus(newP.getPostStatus());
                    oldP.setTitle(newP.getTitle());
                    oldP.setVersion(newP.getVersion());

                    em.merge(oldP);
                    mPostsHistory.add(pk);
                }
            }
        }

        return mPostsHistory;
    }

    private List<RelationshipPK> updateRelationships(List<Relationship> relationships,
                                           Character strategy,
                                           Map<Long, Long> mPosts,
                                           Map<Long, Long> mTemplates)
            throws Exception {
        List<RelationshipPK> mRelationships = new ArrayList<RelationshipPK>();

        for (Relationship newR : relationships) {
            if (newR.getType().equals(Wcm.RELATIONSHIP.POST)) {
                newR.setOriginId(mPosts.get(newR.getOriginId()));
                if (newR.getAliasId() != null) {
                    newR.setAliasId(mPosts.get(newR.getAliasId()));
                }
            } else {
                newR.setOriginId(mTemplates.get(newR.getOriginId()));
                if (newR.getAliasId() != null) {
                    newR.setAliasId(mTemplates.get(newR.getAliasId()));
                }
            }

            RelationshipPK pk = new RelationshipPK();
            pk.setKey(newR.getKey());
            pk.setOriginId(newR.getOriginId());
            pk.setType(newR.getType());
            Relationship oldR = em.find(Relationship.class, pk);
            if (oldR == null) {
                em.persist(newR);
                mRelationships.add(pk);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldR.setKey(newR.getKey());
                    oldR.setType(newR.getType());
                    oldR.setAliasId(newR.getAliasId());
                    oldR.setOriginId(newR.getOriginId());

                    em.merge(oldR);
                    mRelationships.add(pk);
                }
            }
        }

        return mRelationships;
    }

    private Map<Long, Long> updateSecurity(List<Acl> acls,
                                      Character strategy)
            throws Exception {
        Map<Long, Long> mAcls = new HashMap<Long, Long>();

        for (Acl newA : acls) {
            Acl oldA = em.find(Acl.class, newA.getId());
            if (oldA == null) {
                Long importId, newId;

                importId = newA.getId();
                newA = em.merge(newA);
                newId = newA.getId();

                mAcls.put(importId, newId);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldA.setPrincipal(newA.getPrincipal());
                    oldA.setPermission(newA.getPermission());

                    em.merge(oldA);
                    mAcls.put(oldA.getId(), oldA.getId());
                }
            }
        }
        return mAcls;
    }

    private Map<Long, Long> updateTemplates(List<Template> templates,
                                       Character strategy)
            throws Exception {
        Map<Long, Long> mTemplates = new HashMap<Long, Long>();

        for (Template newT : templates) {
            Template oldT = em.find(Template.class, newT.getId());
            if (oldT == null) {
                Long importId, newId;

                importId = newT.getId();
                newT = em.merge(newT);
                newId = newT.getId();

                mTemplates.put(importId, newId);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldT.setCreated(newT.getCreated());
                    oldT.setContent(newT.getContent());
                    oldT.setLocale(newT.getLocale());
                    oldT.setModified(newT.getModified());
                    oldT.setName(newT.getName());
                    oldT.setUser(newT.getUser());
                    oldT.setVersion(newT.getVersion());

                    em.merge(oldT);
                    mTemplates.put(oldT.getId(), oldT.getId());
                }
            }
        }

        return mTemplates;
    }

    private List<TemplateHistoryPK> updateTemplatesHistory(List<TemplateHistory> templates,
                                              Character strategy,
                                              Map<Long, Long> mTemplates)
            throws Exception {
        List<TemplateHistoryPK> mTemplatesHistory = new ArrayList<TemplateHistoryPK>();

        for (TemplateHistory newT : templates) {
            // Update ID reference
            Long newId = mTemplates.get(newT.getId());
            if (newId == null) newId = newT.getId();
            newT.setId(newId);

            TemplateHistoryPK pk = new TemplateHistoryPK();
            pk.setId(newT.getId());
            pk.setVersion(newT.getVersion());
            TemplateHistory oldT = em.find(TemplateHistory.class, pk);
            if (oldT == null) {
                em.persist(newT);
                mTemplatesHistory.add(pk);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldT.setCreated(newT.getCreated());
                    oldT.setContent(newT.getContent());
                    oldT.setDeleted(newT.getDeleted());
                    oldT.setLocale(newT.getLocale());
                    oldT.setModified(newT.getModified());
                    oldT.setName(newT.getName());
                    oldT.setUser(newT.getUser());
                    oldT.setVersion(newT.getVersion());

                    em.merge(oldT);
                    mTemplatesHistory.add(pk);
                }
            }
        }

        return mTemplatesHistory;
    }

    private Map<Long, Long> updateUploads(List<Upload> uploads,
                                     Character strategy,
                                     String uploadsFolder,
                                     String targetFolder)
            throws Exception {
        Map<Long, Long> mUploads = new HashMap<Long, Long>();

        for (Upload newU : uploads) {
            Upload oldU = em.find(Upload.class, newU.getId());
            String tempPath = uploadsFolder + "/" + newU.getStoredName();
            String newPath = targetFolder + "/" + newU.getStoredName();
            if (oldU == null) {
                Long importId, newId;

                importId = newU.getId();
                newU = em.merge(newU);
                newId = newU.getId();

                mUploads.put(importId, newId);
                moveFile(tempPath, newPath);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldU.setCreated(newU.getCreated());
                    oldU.setDescription(newU.getDescription());
                    oldU.setFileName(newU.getFileName());
                    oldU.setMimeType(newU.getMimeType());
                    oldU.setModified(newU.getModified());
                    oldU.setStoredName(newU.getStoredName());
                    oldU.setUser(newU.getUser());
                    oldU.setVersion(newU.getVersion());

                    em.merge(oldU);
                    mUploads.put(oldU.getId(), oldU.getId());
                    moveFile(tempPath, newPath);
                }
            }
        }

        return mUploads;
    }

    private List<UploadHistoryPK> updateUploadsHistory(List<UploadHistory> uploads,
                                            Character strategy,
                                            String uploadsFolder,
                                            String targetFolder,
                                            Map<Long, Long> mUploads)
            throws Exception {
        List<UploadHistoryPK> mUploadsHistory = new ArrayList<UploadHistoryPK>();

        for (UploadHistory newU : uploads) {
            // Update ID reference
            Long newId = mUploads.get(newU.getId());
            if (newId == null) newId = newU.getId();
            newU.setId(newId);

            UploadHistoryPK pk = new UploadHistoryPK();
            pk.setId(newU.getId());
            pk.setVersion(newU.getVersion());
            UploadHistory oldU = em.find(UploadHistory.class, pk);
            String tempPath = uploadsFolder + "/" + newU.getStoredName();
            String newPath = targetFolder + "/" + newU.getStoredName();
            if (oldU == null) {
                em.persist(newU);
                mUploadsHistory.add(pk);
                moveFile(tempPath, newPath);
            } else {
                if (!strategy.equals(Wcm.IMPORT.STRATEGY.UPDATE)) {
                    oldU.setCreated(newU.getCreated());
                    oldU.setDeleted(newU.getDeleted());
                    oldU.setDescription(newU.getDescription());
                    oldU.setFileName(newU.getFileName());
                    oldU.setMimeType(newU.getMimeType());
                    oldU.setModified(newU.getModified());
                    oldU.setStoredName(newU.getStoredName());
                    oldU.setUser(newU.getUser());
                    oldU.setVersion(newU.getVersion());

                    em.merge(oldU);
                    mUploadsHistory.add(pk);
                    moveFile(tempPath, newPath);
                }
            }
        }

        return mUploadsHistory;
    }

    private void updateCategoriesParent(Map<Long, Long> categoriesParent,
                                        Map<Long,Long> categoriesModified)
            throws Exception {
        if (categoriesParent == null || categoriesModified == null)
            throw new IllegalArgumentException();

        Set<Long> categories = categoriesParent.keySet();

        for (Long catId : categories) {
            Long parentId = categoriesParent.get(catId);
            if (parentId != null && categoriesModified.get(catId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                catId = categoriesModified.get(catId);
                parentId = categoriesModified.get(parentId);
                // Validates if current state is correct
                Category cat = em.find(Category.class, catId);
                if (cat.getParent() == null || !cat.getParent().getId().equals(parentId)) {
                    Category parent = em.find(Category.class, parentId);
                    cat.setParent(parent);
                    em.merge(cat);
                }
            }
        }
    }

    private void updateCategoriesPosts(List<ImportParser.CategoryPost> categoriesPosts,
                                       Map<Long, Long> categoriesModified,
                                       Map<Long, Long> postsModified)
            throws Exception {
        if (categoriesPosts == null || categoriesModified == null || postsModified == null)
            throw new IllegalArgumentException();

        for (ImportParser.CategoryPost catPost : categoriesPosts) {
            Long catId = catPost.category;
            Long postId = catPost.post;
            if (categoriesModified.get(catId) != null || postsModified.get(postId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                postId = postsModified.get(postId);
                catId = categoriesModified.get(catId);

                Post post = em.find(Post.class, postId);
                Category cat = em.find(Category.class, catId);
                if (!cat.getPosts().contains(post)) {
                    post.getCategories().add(cat);
                    cat.getPosts().add(post);
                    em.merge(post);
                    em.merge(cat);
                }
            }
        }
    }

    private void updateCategoriesTemplates(List<ImportParser.CategoryTemplate> categoriesTemplates,
                                           Map<Long, Long> categoriesModified,
                                           Map<Long, Long> templatesModified)
            throws Exception {
        if (categoriesTemplates == null || categoriesModified == null || templatesModified == null)
            throw new IllegalArgumentException();

        for (ImportParser.CategoryTemplate catTemplate : categoriesTemplates) {
            Long catId = catTemplate.category;
            Long tempId = catTemplate.template;
            if (categoriesModified.get(catId) != null || templatesModified.get(tempId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                tempId = templatesModified.get(tempId);
                catId = categoriesModified.get(catId);

                Template temp = em.find(Template.class, tempId);
                Category cat = em.find(Category.class, catId);
                if (!cat.getTemplates().contains(temp)) {
                    temp.getCategories().add(cat);
                    cat.getTemplates().add(temp);
                    em.merge(temp);
                    em.merge(cat);
                }
            }
        }

    }

    private void updateCategoriesUploads(List<ImportParser.CategoryUpload> categoriesUploads,
                                         Map<Long, Long> categoriesModified,
                                         Map<Long, Long> uploadsModified)
            throws Exception {
        if (categoriesUploads == null || categoriesModified == null || uploadsModified == null)
            throw new IllegalArgumentException();

        for (ImportParser.CategoryUpload catUpload : categoriesUploads) {
            Long catId = catUpload.category;
            Long upId = catUpload.upload;
            if (categoriesModified.get(catId) != null || uploadsModified.get(upId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                upId = uploadsModified.get(upId);
                catId = categoriesModified.get(catId);

                Upload upload = em.find(Upload.class, upId);
                Category cat = em.find(Category.class, catId);
                if (!cat.getUploads().contains(upload)) {
                    upload.getCategories().add(cat);
                    cat.getUploads().add(upload);
                    em.merge(upload);
                    em.merge(cat);
                }
            }
        }

    }

    private void updateCommentsPost(Map<Long, Long> commentsPost,
                                    Map<Long, Long> commentsModified,
                                    Map<Long, Long> postsModified)
            throws Exception {
        if (commentsPost == null || commentsModified == null || postsModified == null)
            throw new IllegalArgumentException();

        Set<Long> keys = commentsPost.keySet();
        for (Long commentId : keys) {
            Long postId = commentsPost.get(commentId);
            if (commentsModified.get(commentId) != null|| postsModified.get(postId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                commentId = commentsModified.get(commentId);
                postId = postsModified.get(postId);

                Post post = em.find(Post.class, postId);
                Comment comment = em.find(Comment.class, commentId);
                if (!post.getComments().contains(comment)) {
                    post.getComments().add(comment);
                    comment.setPost(post);
                    em.merge(post);
                    em.merge(comment);
                }
            }
        }
    }

    private void updateAclCategories(Map<Long, Long> aclCategories,
                                     Map<Long, Long> aclsModified,
                                     Map<Long, Long> categoriesModified)
            throws Exception {
        if (aclCategories == null || aclsModified == null || categoriesModified == null)
            throw new IllegalArgumentException();

        Set<Long> keys = aclCategories.keySet();
        for (Long aclId : keys) {
            Long categoryId = aclCategories.get(aclId);
            if (aclsModified.get(aclId) != null || categoriesModified.get(categoryId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                aclId = aclsModified.get(aclId);
                categoryId = categoriesModified.get(categoryId);

                Acl acl = em.find(Acl.class, aclId);
                Category category = em.find(Category.class, categoryId);
                if (!category.getAcls().contains(acl)) {
                    category.getAcls().add(acl);
                    acl.setCategory(category);
                    em.merge(category);
                    em.merge(acl);
                }
            }
        }

    }

    private void updateAclPosts(Map<Long, Long> aclPosts,
                                Map<Long, Long> aclsModified,
                                Map<Long, Long> postsModified)
            throws Exception {
        if (aclPosts == null || aclsModified == null || postsModified == null)
            throw new IllegalArgumentException();

        Set<Long> keys = aclPosts.keySet();
        for (Long aclId : keys) {
            Long postId = aclPosts.get(aclId);
            if (aclsModified.get(aclId) != null || postsModified.get(postId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                aclId = aclsModified.get(aclId);
                postId = postsModified.get(postId);

                Acl acl = em.find(Acl.class, aclId);
                Post post = em.find(Post.class, postId);
                if (!post.getAcls().contains(acl)) {
                    post.getAcls().add(acl);
                    acl.setPost(post);
                    em.merge(post);
                    em.merge(acl);
                }
            }
        }
    }

    private void updateAclUploads(Map<Long, Long> aclUploads,
                                  Map<Long, Long> aclsModified,
                                  Map<Long, Long> uploadsModified)
            throws Exception {
        if (aclUploads == null || aclsModified == null || uploadsModified == null)
            throw new IllegalArgumentException();

        Set<Long> keys = aclUploads.keySet();
        for (Long aclId : keys) {
            Long uploadId = aclUploads.get(aclId);
            if (aclsModified.get(aclId) != null || uploadsModified.get(uploadId) != null) {
                // Convert old IDs to new IDs to link objects with parents
                aclId = aclsModified.get(aclId);
                uploadId = uploadsModified.get(uploadId);

                Acl acl = em.find(Acl.class, aclId);
                Upload upload = em.find(Upload.class, uploadId);
                if (!upload.getAcls().contains(acl)) {
                    upload.getAcls().add(acl);
                    acl.setUpload(upload);
                    em.merge(upload);
                    em.merge(acl);
                }
            }
        }
    }

    private void updateUploadsLinks(Map<Long, Long> mUploads)
            throws Exception {
        if (mUploads == null || mUploads.size() == 0) {
            return;
        }
        // Update Posts
        List<Post> rPosts = em.createNamedQuery("listAllPosts", Post.class).getResultList();
        for (Post p : rPosts) {
            if (updateUploadsLinks(p, mUploads)) {
                em.merge(p);
            }
        }
        // Update PostsHistory
        List<PostHistory> rPostsHistory = em.createNamedQuery("listAllPostHistory", PostHistory.class).getResultList();
        for (PostHistory p : rPostsHistory) {
            if (updateUploadsLinks(p, mUploads)) {
                em.merge(p);
            }
        }
        // Update Templates
        List<Template> rTemplates = em.createNamedQuery("listAllTemplates", Template.class).getResultList();
        for (Template t : rTemplates) {
            if (updateUploadsLinks(t, mUploads)) {
                em.merge(t);
            }
        }
        // Update TemplatesHistory
        List<TemplateHistory> rTemplatesHistory = em.createNamedQuery("listAllTemplateHistory", TemplateHistory.class).getResultList();
        for (TemplateHistory t : rTemplatesHistory) {
            if (updateUploadsLinks(t, mUploads)) {
                em.merge(t);
            }
        }
    }

    private void moveFile(String oldPath, String newPath) {
        try {
            File oldF = new File(oldPath);
            File newF = new File(newPath);
            if (!newF.exists()) {
                FileUtils.moveFile(oldF, newF);
            }
        } catch (Exception e) {
            log.warning("Error trying to move upload: " + oldPath + " to " + newPath + ". Msg: " + e.getMessage()) ;
        }
    }

    @Schedule(hour="*", minute = "*/" + Wcm.TIMEOUTS.TIMER)
    void checkUnlocks() {
        try {
            List<Lock> lockList = em.createNamedQuery("listLocks")
                    .getResultList();
            for (Lock l : lockList) {
                Calendar created = (Calendar)l.getCreated().clone();
                created.add(Calendar.MINUTE, Wcm.TIMEOUTS.LOCKS);
                Calendar now = Calendar.getInstance();
                if (now.after(created)) {
                    log.info("Timeout for lock: " + l);
                    em.remove(l);
                }
            }
        } catch (Exception e) {
            log.warning("Error querying/deleting locks");
            e.printStackTrace();
        }
    }

    /*
     *  Aux functions to extract path for categories
     */
    private String child(String path) {
        if (path == null || path.length() == 0) return path;
        if (path.indexOf("/") == -1) return path;
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private String parent(String path) {
        if (path == null || path.length() == 0) return path;
        if (path.indexOf("/") == -1) return "";
        return path.substring(0, path.lastIndexOf("/"));
    }

    /*
     * Aux function to unzip in a folder
     */
    private void unzip(ZipInputStream zis, File output) throws IOException {
        if (zis == null || output == null) return;
        ZipEntry entry;
        OutputStream os = null;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = new File(output, entry.getName());
                if (entry.isDirectory()) {
                    if (!entryFile.exists()) {
                        entryFile.mkdirs();
                    }
                } else {
                    if (entryFile.getParentFile() != null && !entryFile.getParentFile().exists()) {
                        entryFile.getParentFile().mkdirs();
                    }
                    if (!entryFile.exists()) {
                        entryFile.createNewFile();
                    }
                    os = new FileOutputStream(entryFile);
                    IOUtils.copy(zis, os);
                    os.close();
                }
            }
            zis.close();
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(os);
        }
    }

    private boolean updateUploadsLinks(Object o, Map<Long, Long> mUploads) {
        if (o instanceof Post) {
            Post p = (Post)o;
            String modified = changeLinks(p.getContent(), mUploads);
            if (modified != null) {
                p.setContent(modified);
                return true;
            } else {
                return false;
            }
        } else if (o instanceof PostHistory) {
            PostHistory p = (PostHistory)o;
            String modified = changeLinks(p.getContent(), mUploads);
            if (modified != null) {
                p.setContent(modified);
                return true;
            } else {
                return false;
            }
        } else if (o instanceof Template) {
            Template t = (Template)o;
            String modified = changeLinks(t.getContent(), mUploads);
            if (modified != null) {
                t.setContent(modified);
                return true;
            } else {
                return false;
            }
        } else if (o instanceof TemplateHistory) {
            TemplateHistory t = (TemplateHistory)o;
            String modified = changeLinks(t.getContent(), mUploads);
            if (modified != null) {
                t.setContent(modified);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private String changeLinks(String doc, Map<Long, Long> mUploads) {
        if (doc == null || doc.length() == 0) {
            return doc;
        }

        int length = doc.length();
        int i = 0;
        int startToken = -1;
        int finishToken = -1;
        boolean finish = false;
        boolean modified = false;
        StringBuilder output = new StringBuilder();

        while (!finish) {
            boolean found = false;
            Character ch = doc.charAt(i);
            // Checks if there is a pattern under present position
            if (ch.equals('r') &&
                ((i+1) < length) && doc.charAt(i+1) == 's' &&
                ((i+2) < length) && doc.charAt(i+2) == '/' &&
                ((i+3) < length) && doc.charAt(i+3) == 'u' &&
                ((i+4) < length) && doc.charAt(i+4) == '/') {
                // Search end of token
                boolean numbers = true;
                startToken = i;
                finishToken = i + 5;
                while (numbers) {
                    if (finishToken < length &&
                        doc.charAt(finishToken) >= '0' &&
                        doc.charAt(finishToken) <= '9') {
                        finishToken++;
                    } else {
                        numbers = false;
                    }
                }
                if (finishToken < length) {
                    String token = doc.substring(startToken, finishToken);
                    int index = -1;
                    try {
                        index = new Integer(token.substring(5, token.length()));
                    } catch (Exception e) {
                        // Not a number
                    }
                    if (index > -1) {
                        Long newIndex = mUploads.get(new Long(index));
                        if (newIndex != null) {
                            output.append("rs/u/" + newIndex);
                            found = true;
                            modified = true;
                            i = finishToken - 1; // To read end quotes
                        }
                    }
                }
            }
            if (!found) {
                output.append(ch);
            }
            i++;
            if (i >= length) {
                finish = true;
            }
        }

        if (modified) {
            return output.toString();
        } else {
            return null;
        }
    }
}