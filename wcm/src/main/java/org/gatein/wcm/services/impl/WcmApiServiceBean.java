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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.api.domain.Category;
import org.gatein.wcm.api.domain.Comment;
import org.gatein.wcm.api.domain.Post;
import org.gatein.wcm.api.domain.Upload;
import org.gatein.wcm.domain.UserWcm;
import org.gatein.wcm.portlet.util.ViewMetadata;
import org.gatein.wcm.services.PortalService;
import org.gatein.wcm.services.WcmService;
import org.gatein.wcm.api.services.WcmApiService;

@Stateless
public class WcmApiServiceBean implements WcmApiService {

    private static final Logger log = Logger.getLogger(WcmApiServiceBean.class.getName());
    private static final String ANONYMOUS = "anonymous";

    @Inject
    WcmService wcm;

    @Inject
    PortalService portal;

    /**
     * @see WcmApiService#findRootCategories(String)
     */
    @Override
    public List<Category> findRootCategories(String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Category> listCategories = wcm.findRootCategories(userWcm);
            return convertCategories(listCategories);
        } catch (Exception e) {
            log.warning("Error querying root categories for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findChildren(org.gatein.wcm.api.domain.Category, String)
     */
    @Override
    public List<Category> findChildren(Category cat, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Category> listCategories = wcm.findChildren(cat.getId(), userWcm);
            return convertCategories(listCategories);
        } catch (Exception e) {
            log.warning("Error querying children categories for cat " + cat + " and user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findCategory(String, String)
     */
    @Override
    public Category findCategory(String path, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            org.gatein.wcm.domain.Category cat = wcm.findCategory(path, userWcm);
            return convert(cat);
        } catch (Exception e) {
            log.warning("Error querying children categories for path " + path + " and user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findPosts(String)
     */
    @Override
    public List<Post> findPosts(String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Post> listPosts = wcm.findPosts(userWcm);
            return convertPosts(listPosts);
        } catch (Exception e) {
            log.warning("Error querying posts for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findPosts(Long, String)
     */
    @Override
    public List<Post> findPosts(Long categoryId, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Post> listPosts = wcm.findPosts(categoryId, userWcm);
            return convertPosts(listPosts);
        } catch (Exception e) {
            log.warning("Error querying posts for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findPosts(String, String)
     */
    @Override
    public List<Post> findPosts(String filterName, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Post> listPosts = wcm.findPosts(filterName, userWcm);
            return convertPosts(listPosts);
        } catch (Exception e) {
            log.warning("Error querying posts for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findPosts(Long, Character, String)
     */
    @Override
    public List<Post> findPosts(Long categoryId, Character status, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Post> listPosts = wcm.findPosts(categoryId, status, userWcm);
            return convertPosts(listPosts);
        } catch (Exception e) {
            log.warning("Error querying posts for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findPosts(Long, Character, String)
     */
    @Override
    public List<Post> findPosts(Long categoryId, String locale, Character status, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Post> listPosts = wcm.findPosts(categoryId, locale, status, userWcm);
            return convertPosts(listPosts);
        } catch (Exception e) {
            log.warning("Error querying posts for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findPost(Long, String)
     */
    @Override
    public Post findPost(Long id, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            org.gatein.wcm.domain.Post post = wcm.findPost(id, userWcm);
            return convert(post);
        } catch (Exception e) {
            log.warning("Error querying posts for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findPost(Long, String, String)
     */
    @Override
    public Post findPost(Long id, String locale, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            org.gatein.wcm.domain.Post post = wcm.findPost(id, userWcm);
            return convert(post);
        } catch (Exception e) {
            log.warning("Error querying posts for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findUploads(String)
     */
    @Override
    public List<Upload> findUploads(String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Upload> listUploads = wcm.findUploads(userWcm);
            return convertUploads(listUploads);
        } catch (Exception e) {
            log.warning("Error querying uploads for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findUploads(String, String)
     */
    @Override
    public List<Upload> findUploads(String filterName, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Upload> listUploads = wcm.findUploads(filterName, userWcm);
            return convertUploads(listUploads);
        } catch (Exception e) {
            log.warning("Error querying uploads for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findUploads(String, String)
     */
    @Override
    public List<Upload> findUploads(Long categoryId, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            List<org.gatein.wcm.domain.Upload> listUploads = wcm.findUploads(categoryId, userWcm);
            return convertUploads(listUploads);
        } catch (Exception e) {
            log.warning("Error querying uploads for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * @see WcmApiService#findUploads(String, String)
     */
    @Override
    public Upload findUpload(Long id, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            org.gatein.wcm.domain.Upload upload = wcm.findUpload(id, userWcm);
            return convert(upload);
        } catch (Exception e) {
            log.warning("Error querying uploads for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void add(Long postId, Comment comment, String user) throws Exception {
        UserWcm userWcm = null;
        if (user == null || (user != null && ANONYMOUS.equals(user))) {
            userWcm = new UserWcm(ANONYMOUS);
        } else {
            userWcm = portal.getPortalUser(user);
        }
        try {
            org.gatein.wcm.domain.Post post = wcm.findPost(new Long(postId), userWcm);
            org.gatein.wcm.domain.Comment c = new org.gatein.wcm.domain.Comment();
            c.setContent(comment.getContent());
            if (userWcm == null || userWcm.getUsername().equals("anonymous")) {
                c.setAuthor(comment.getAuthor().equals("")?"anonymous":comment.getAuthor());
                c.setAuthorEmail(comment.getAuthorEmail());
                c.setAuthorUrl(comment.getAuthorUrl());
            } else {
                c.setAuthor(userWcm.getUsername());
            }
            c.setPost(post);
            if (post.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS)) {
                c.setStatus(Wcm.COMMENT.PUBLIC);
            } else {
                c.setStatus(Wcm.COMMENT.PUBLIC);
            }
            wcm.add(post, c);
        } catch (Exception e) {
            log.warning("Error querying uploads for user " + user);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Auxiliar functions to convert classes between org.gatein.wcm.domain.* and org.gatein.wcm.api.domain.*
     */
    private List<Category> convertCategories(List<org.gatein.wcm.domain.Category> listCategories) {
        if (listCategories == null) return null;
        ArrayList<Category> out = new ArrayList<Category>();
        for (org.gatein.wcm.domain.Category c : listCategories) {
            out.add(convert(c));
        }
        return out;
    }

    private List<Post> convertPosts(List<org.gatein.wcm.domain.Post> listPosts) {
        if (listPosts == null) return null;
        ArrayList<Post> out = new ArrayList<Post>();
        for (org.gatein.wcm.domain.Post p : listPosts) {
            out.add(convert(p));
        }
        return out;
    }

    private List<Upload> convertUploads(List<org.gatein.wcm.domain.Upload> listUploads) {
        if (listUploads == null) return null;
        ArrayList<Upload> out = new ArrayList<Upload>();
        for (org.gatein.wcm.domain.Upload u : listUploads) {
            out.add(convert(u));
        }
        return out;
    }


    private Category convert(org.gatein.wcm.domain.Category cat) {
        if (cat == null) return null;
        Category out = new Category();
        out.setId(cat.getId());
        out.setName(cat.getName());
        out.setType(cat.getType());
        out.setNumChildren(cat.getNumChildren());
        out.setPath(ViewMetadata.categoryTitle(cat));
        return out;
    }

    private Comment convert(org.gatein.wcm.domain.Comment comment) {
        if (comment == null) return null;
        Comment out = new Comment();
        out.setId(comment.getId());
        out.setCreated(comment.getCreated());
        out.setAuthor(comment.getAuthor());
        out.setAuthorEmail(comment.getAuthorEmail());
        out.setAuthorUrl(comment.getAuthorUrl());
        out.setStatus(comment.getStatus());
        out.setPostId(comment.getPost() != null ? comment.getPost().getId() : null);
        out.setContent(comment.getContent());
        return out;
    }

    private Post convert(org.gatein.wcm.domain.Post post) {
        if (post == null) return null;
        Post out = new Post();
        out.setContent(post.getContent());
        out.setCreated(post.getCreated());
        out.setAuthor(post.getAuthor());
        out.setCommentsStatus(post.getCommentsStatus());
        out.setExcerpt(post.getExcerpt());
        out.setId(post.getId());
        out.setTitle(post.getTitle());
        out.setVersion(post.getVersion());
        out.setLocale(post.getLocale());
        out.setPostStatus(post.getPostStatus());
        out.setModified(post.getModified());
        if (post.getComments() != null) {
            for (org.gatein.wcm.domain.Comment com : post.getComments()) {
                out.getComments().add(convert(com));
            }
        }
        if (post.getCategories() != null) {
            for (org.gatein.wcm.domain.Category cat : post.getCategories()) {
                out.getCategories().add(convert(cat));
            }
        }
        return out;
    }

    private Upload convert(org.gatein.wcm.domain.Upload upload) {
        if (upload == null) return null;
        Upload out = new Upload();
        out.setStoredName(upload.getStoredName());
        out.setCreated(upload.getCreated());
        out.setId(upload.getId());
        out.setVersion(upload.getVersion());
        out.setDescription(upload.getDescription());
        out.setMimeType(upload.getMimeType());
        out.setModified(upload.getModified());
        out.setFileName(upload.getFileName());
        out.setUser(upload.getUser());
        if (upload.getCategories() != null) {
            for (org.gatein.wcm.domain.Category cat : upload.getCategories()) {
                out.getCategories().add(convert(cat));
            }
        }
        return out;
    }
}
