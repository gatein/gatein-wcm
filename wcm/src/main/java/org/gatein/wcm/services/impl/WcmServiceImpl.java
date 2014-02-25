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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmAuthorizationException;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.WcmLockException;
import org.gatein.wcm.domain.*;
import org.gatein.wcm.portlet.util.ParseDates;
import org.gatein.wcm.services.WcmService;

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
        if (locale == null || "".equals(locale)) return list;
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
        if (path == null || "".equals(path)) path = "/";
        try {
            String name = child(path);
            List<Category> parents = null;
            Category parent = null;
            if (name != null && !"".equals(name)) {
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
        if (path == null || "".equals(path)) path = "/";
        Category output = null;
        try {
            String name = child(path);
            List<Category> candidates = null;
            if (name != null && !"".equals(name)) {
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
        if (path == null || "".equals(path)) return false;
        if (c.getName().equals(child(path))) {
            if (c.getParent() == null && "".equals(parent(path))) {
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
            if (p.getLocale() != null && !p.getLocale().equals(locale)) {
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
            if (t.getLocale() != null && !t.getLocale().equals(locale)) {
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
        if (path == null || "".equals(path)) return path;
        if (path.indexOf("/") == -1) return path;
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private String parent(String path) {
        if (path == null || "".equals(path)) return path;
        if (path.indexOf("/") == -1) return "";
        return path.substring(0, path.lastIndexOf("/"));
    }
}