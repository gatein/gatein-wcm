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

package org.gatein.wcm.portlet.editor.views;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.WcmException;
import org.gatein.wcm.WcmLockException;
import org.gatein.wcm.domain.*;
import org.gatein.wcm.portlet.util.ViewMetadata;
import org.gatein.wcm.services.PortalService;
import org.gatein.wcm.services.WcmService;

import javax.inject.Inject;
import javax.portlet.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  Actions for Posts area of EditorPortlet
 *
 * @see Wcm.VIEWS
 * @see Wcm.ACTIONS
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class PostsActions {
    private static final Logger log = Logger.getLogger(PostsActions.class.getName());

    @Inject
    private WcmService wcm;

    @Inject
    private PortalService portal;

    public String actionNewPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String postTitle = request.getParameter("postTitle");
        String postExcerpt = request.getParameter("postExcerpt");
        String postLocale = request.getParameter("postLocale");
        String postStatus = request.getParameter("postStatus");
        String postCommentsStatus = request.getParameter("postCommentsStatus");
        String postContent = request.getParameter("postContent");
        try {
            Post newPost = new Post();
            newPost.setTitle(postTitle);
            newPost.setExcerpt(postExcerpt);
            newPost.setLocale(postLocale);
            newPost.setPostStatus(postStatus.charAt(0));
            newPost.setCommentsStatus(postCommentsStatus.charAt(0));
            newPost.setContent(postContent);
            newPost.setAuthor(userWcm.getUsername());
            wcm.create(newPost, userWcm);
            return Wcm.VIEWS.POSTS;
        } catch(Exception e) {
            log.warning("Error saving post");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error saving post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionRightPosts(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) viewMetadata.rightPage();
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.POSTS;
    }

    public String actionLeftPosts(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) viewMetadata.leftPage();
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.POSTS;
    }

    public String actionEditPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String postEditId = request.getParameter("postEditId");
        String postTitle = request.getParameter("postTitle");
        String postExcerpt = request.getParameter("postExcerpt");
        String postLocale = request.getParameter("postLocale");
        String postStatus = request.getParameter("postStatus");
        String postCommentsStatus = request.getParameter("postCommentsStatus");
        String postContent = request.getParameter("postContent");
        try {
            Post updatePost = wcm.findPost(new Long(postEditId), userWcm);
            updatePost.setTitle(postTitle);
            updatePost.setExcerpt(postExcerpt);
            updatePost.setLocale(postLocale);
            updatePost.setPostStatus(postStatus.charAt(0));
            updatePost.setCommentsStatus(postCommentsStatus.charAt(0));
            updatePost.setContent(postContent);
            updatePost.setAuthor(userWcm.getUsername());
            wcm.update(updatePost, userWcm);
            wcm.unlock(new Long(postEditId), Wcm.LOCK.POST, userWcm);
            return Wcm.VIEWS.POSTS;
        } catch(Exception e) {
            log.warning("Error uploading post");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error uploading post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionChangeVersionPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String postVersionId = request.getParameter("postVersionId");
        String postVersion = request.getParameter("postVersion");
        try {
            wcm.changeVersionPost(new Long(postVersionId), new Long(postVersion), userWcm);
            response.setRenderParameter("editid", postVersionId);
            return Wcm.VIEWS.EDIT_POST;
        } catch (Exception e) {
            log.warning("Error changing post version");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error changing post version " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionAddCategoryPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String categoryId = request.getParameter("categoryId");
        String postId = request.getParameter("postId");
        try {
            Category cat = wcm.findCategory(new Long(categoryId), userWcm);
            Post post = wcm.findPost(new Long(postId), userWcm);
            wcm.lock(new Long(postId), Wcm.LOCK.POST, userWcm);
            wcm.add(post, cat, userWcm);
            wcm.unlock(new Long(postId), Wcm.LOCK.POST, userWcm);
            return Wcm.VIEWS.POSTS;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error adding category to post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionFilterCategoryPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String filterCategoryId = request.getParameter("filterCategoryId");
        Long filterId = new Long(filterCategoryId);
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) {
            // Reset viewMetadata if it comes from a different view
            if (viewMetadata.getViewType() != ViewMetadata.ViewType.POSTS) {
                viewMetadata.setViewType(ViewMetadata.ViewType.POSTS);
                viewMetadata.setFilterCategory(false);
            }
            if (!viewMetadata.isFilterCategory()) {
                viewMetadata.setCategoryId(filterId);
                viewMetadata.setFromIndex(0); // First search, reset pagination
                viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                viewMetadata.setFilterCategory(true);
                viewMetadata.setFilterName(false);
            } else {
                if (!viewMetadata.getCategoryId().equals(filterId)) {
                    if (filterId == -1) {
                        viewMetadata.setFilterCategory(false);
                    } else {
                        viewMetadata.setCategoryId(filterId);
                    }
                    viewMetadata.setFromIndex(0); // First search, reset pagination
                    viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                }
            }
        }
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.POSTS;
    }

    public String actionFilterNamePost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String filterName = request.getParameter("filterName");
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        if (viewMetadata != null) {
            // Reset viewMetadata if it comes from a different view
            if (viewMetadata.getViewType() != ViewMetadata.ViewType.POSTS) {
                viewMetadata.setViewType(ViewMetadata.ViewType.POSTS);
                viewMetadata.setFilterName(false);
            }
            if (!viewMetadata.isFilterName()) {
                viewMetadata.setName(filterName);
                viewMetadata.setFromIndex(0); // First search, reset pagination
                viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                viewMetadata.setFilterName(true);
                viewMetadata.setFilterCategory(false);
            } else {
                if (!viewMetadata.getName().equals(filterName)) {
                    if (filterName.equals("")) {
                        viewMetadata.setFilterName(false);
                    } else {
                        viewMetadata.setName(filterName);
                    }
                    viewMetadata.setFromIndex(0); // First search, reset pagination
                    viewMetadata.setToIndex(Wcm.VIEWS.MAX_PER_PAGE - 1);
                }
            }
        }
        request.getPortletSession().setAttribute("metadata", viewMetadata);
        return Wcm.VIEWS.POSTS;
    }

    public String actionDeletePost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String deletePostId = request.getParameter("deletePostId");
        Long postId = new Long(deletePostId);
        try {
            wcm.lock(new Long(postId), Wcm.LOCK.POST, userWcm);
            wcm.deletePost(postId, userWcm);
            wcm.unlock(new Long(postId), Wcm.LOCK.POST, userWcm);
            return Wcm.VIEWS.POSTS;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error deleting post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error deleting post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionDeleteSelectedPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String deleteSelectedListId = request.getParameter("deleteSelectedListId");
        try {
            String[] postsIds = deleteSelectedListId.split(",");
            for (String postId : postsIds) {
                wcm.lock(new Long(postId), Wcm.LOCK.POST, userWcm);
                wcm.deletePost(new Long(postId), userWcm);
                wcm.unlock(new Long(postId), Wcm.LOCK.POST, userWcm);
            }
            return Wcm.VIEWS.POSTS;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error deleting post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error deleting post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionAddSelectedCategoryPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String addSelectedCategoryPostListId = request.getParameter("addSelectedCategoryPostListId");
        String addCategoryPostId = request.getParameter("addCategoryPostId");
        try {
            Category cat = wcm.findCategory(new Long(addCategoryPostId), userWcm);
            String[] postsIds = addSelectedCategoryPostListId.split(",");
            for (String postId : postsIds) {
                Post post = wcm.findPost(new Long(postId), userWcm);
                wcm.lock(new Long(postId), Wcm.LOCK.POST, userWcm);
                wcm.add(post, cat, userWcm);
                wcm.unlock(new Long(postId), Wcm.LOCK.POST, userWcm);
            }
            return Wcm.VIEWS.POSTS;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        } catch(Exception e) {
            log.warning("Error adding category to post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionRemoveCategoryPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String catId = request.getParameter("catid");
        String postId = request.getParameter("postid");
        try {
            wcm.removePostCategory(new Long(postId), new Long(catId), userWcm);
            return Wcm.VIEWS.POSTS;
        } catch(Exception e) {
            log.warning("Error adding category to post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error adding category to post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionPublishPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String publishPostId = request.getParameter("publishPostId");
        String publishState = request.getParameter("publishState");
        try {
            Post post = wcm.findPost(new Long(publishPostId), userWcm);
            post.setPostStatus(publishState.charAt(0));
            wcm.update(post, userWcm);
            return Wcm.VIEWS.POSTS;
        } catch(Exception e) {
            log.warning("Error publishing post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error publishing post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionLockPost(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            Post post = wcm.findPost(new Long(editId), userWcm);
            if (userWcm.canWrite(post)) {
                wcm.lock(new Long(editId), Wcm.LOCK.POST, userWcm);
            }
            return Wcm.VIEWS.EDIT_POST;
        } catch (WcmLockException e) {
            response.setRenderParameter("errorWcm", e.getMessage());
        }  catch (Exception e) {
            log.warning("Error locking post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error locking post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public String actionPublishPosts(ActionRequest request, ActionResponse response, UserWcm userWcm) {
        String publishStates = request.getParameter("publishStates");
        String publishPostsId = request.getParameter("publishPostsId");
        try {
            String[] postsIds = publishPostsId.split(",");
            for (String postId : postsIds) {
                Post post = wcm.findPost(new Long(postId), userWcm);
                post.setPostStatus(publishStates.charAt(0));
                wcm.update(post, userWcm);
            }
            return Wcm.VIEWS.POSTS;
        } catch(Exception e) {
            log.warning("Error publishing post.");
            e.printStackTrace();
            response.setRenderParameter("errorWcm", "Error publishing post " + e.toString());
        }
        return Wcm.VIEWS.POSTS;
    }

    public void viewPosts(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        // Check view metadata
        ViewMetadata viewMetadata = (ViewMetadata)request.getPortletSession().getAttribute("metadata");
        try {
            // Categories
            List<Category> categories = wcm.findCategories(userWcm);
            request.setAttribute("categories", categories);

            // Wcm groups
            Set<String> wcmGroups = portal.getWcmGroups();
            request.setAttribute("wcmGroups", wcmGroups);

            // New default view
            if (viewMetadata == null || viewMetadata.getViewType() != ViewMetadata.ViewType.POSTS) {
                List<Post> allPosts = wcm.findPosts(userWcm);
                if (allPosts != null) {
                    viewMetadata = new ViewMetadata();
                    viewMetadata.setViewType(ViewMetadata.ViewType.POSTS);
                    viewMetadata.setTotalIndex(allPosts.size());
                    viewMetadata.resetPagination();
                    if (viewMetadata.getTotalIndex() > 0) {
                        List<Post> viewList = allPosts.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                        request.setAttribute("list", viewList);
                        request.getPortletSession().setAttribute("metadata", viewMetadata);
                    } else {
                        request.setAttribute("list", null);
                        request.getPortletSession().setAttribute("metadata", viewMetadata);
                    }
                }
            } else {
                // Filter per category
                if (viewMetadata.isFilterCategory()) {
                    List<Post> filterPosts = wcm.findPosts(viewMetadata.getCategoryId(), userWcm);
                    if (filterPosts != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.POSTS);
                        viewMetadata.setTotalIndex(filterPosts.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Post> viewList = filterPosts.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                } else if (viewMetadata.isFilterName()) {
                    List<Post> filterPosts = wcm.findPosts(viewMetadata.getName(), userWcm);
                    if (filterPosts != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.POSTS);
                        viewMetadata.setTotalIndex(filterPosts.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Post> viewList = filterPosts.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                } else {
                    List<Post> allPosts = wcm.findPosts(userWcm);
                    if (allPosts != null) {
                        viewMetadata.setViewType(ViewMetadata.ViewType.POSTS);
                        viewMetadata.setTotalIndex(allPosts.size());
                        viewMetadata.checkPagination();
                        if (viewMetadata.getTotalIndex() > 0) {
                            List<Post> viewList = allPosts.subList(viewMetadata.getFromIndex(), viewMetadata.getToIndex()+1);
                            request.setAttribute("list", viewList);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        } else {
                            request.setAttribute("list", null);
                            request.getPortletSession().setAttribute("metadata", viewMetadata);
                        }
                    }
                }
            }
        } catch(WcmException e) {
            log.warning("Error accessing posts.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing posts: " + e.toString());
        }
    }

    public void viewEditPost(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        String editId = request.getParameter("editid");
        try {
            // Categories
            List<Category> categories = wcm.findCategories(userWcm);
            request.setAttribute("categories", categories);

            // Post's versions
            List<Long> versions = wcm.versionsPost(new Long(editId), userWcm);
            request.setAttribute("versions", versions);

            Post post = wcm.findPost(new Long(editId), userWcm);
            request.setAttribute("edit", post);
        } catch (WcmException e) {
            log.warning("Error accessing posts.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing posts: " + e.toString());
        }
    }

    public void viewNewPost(RenderRequest request, RenderResponse response, UserWcm userWcm) {
        try {
            // Categories
            List<Category> categories = wcm.findCategories(userWcm);
            request.setAttribute("categories", categories);
        } catch (WcmException e) {
            log.warning("Error accessing posts.");
            e.printStackTrace();
            request.setAttribute("errorWcm", "Error accessing posts: " + e.toString());
        }
    }

    public String eventShowPostUploads(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String filterCategoryId = request.getParameter("filterCategoryId");
        String filterName = request.getParameter("filterName");
        try {
            List<Upload> uploads = null;
            if (filterCategoryId != null && !filterCategoryId.equals("") && !filterCategoryId.equals("-1")) {
                uploads = wcm.findUploads(new Long(filterCategoryId), userWcm);
            } else if (filterName != null && !filterName.equals("")) {
                uploads = wcm.findUploads(filterName, userWcm);
            } else {
                uploads = wcm.findUploads(userWcm);
            }
            request.setAttribute("uploads", uploads);
            request.setAttribute("namespace", namespace);
        } catch(WcmException e) {
            log.warning("Error accesing uploads.");
            e.printStackTrace();
        } catch(Exception e) {
            log.warning("Error parsing filterCategoryId: " + filterCategoryId);
            e.printStackTrace();
        }
        return "/jsp/posts/postUploads.jsp";
    }

    public String eventShowPostAcls(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        try {
            Post post = null;
            if (postId != null && !"".equals(postId) && namespace != null && !"".equals(namespace)) {
                post = wcm.findPost(new Long(postId), userWcm);
                if (!userWcm.canWrite(post)) post = null;
            }
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
            request.setAttribute("userWcm", userWcm);
        } catch(WcmException e) {
            log.warning("Error accesing post acls.");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error parsing postId: " + postId);
            e.printStackTrace();
        }
        return "/jsp/posts/postsAcls.jsp";
    }

    public String eventAddAclPost(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String aclPostId = request.getParameter("aclpostid");
        String aclType = request.getParameter("acltype");
        String aclWcmGroup = request.getParameter("aclwcmgroup");
        String namespace = request.getParameter("namespace");
        try {
            Post post = wcm.findPost(new Long(aclPostId), userWcm);
            if (post != null && aclType != null && aclWcmGroup != null && userWcm.canWrite(post)) {
                Acl newAcl = new Acl();
                if (aclType.equals(Wcm.ACL.WRITE.toString())) {
                    newAcl.setPermission(Wcm.ACL.WRITE);
                } else {
                    newAcl.setPermission(Wcm.ACL.NONE);
                }
                newAcl.setPrincipal(aclWcmGroup);

                // Check if exists
                boolean found = false;
                for (Acl acl : post.getAcls()) {
                    if (acl.getPermission().equals(newAcl.getPermission()) &&
                            acl.getPrincipal().equals(newAcl.getPrincipal())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    post.getAcls().add(newAcl);
                    newAcl.setPost(post);
                    wcm.update(post, userWcm);
                }
            } else {
                post = null;
            }
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error adding Acl to Post");
            e.printStackTrace();
        }
        return "/jsp/posts/postsAcls.jsp";
    }

    public String eventRemoveAclPost(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String aclPostId = request.getParameter("aclpostid");
        String aclId = request.getParameter("aclid");
        String namespace = request.getParameter("namespace");
        try {
            Post post = wcm.findPost(new Long(aclPostId), userWcm);
            if (post != null && aclId != null && userWcm.canWrite(post)) {
                // Check if exists
                Acl found = null;
                for (Acl acl : post.getAcls()) {
                    if (acl.getId().toString().equals(aclId)) {
                        found = acl;
                        break;
                    }
                }
                // Rules to remove:
                // - at least 1 WRITE ACL should exists
                if ((found.getPermission() == Wcm.ACL.NONE && post.getAcls().size() > 1) ||
                        (found.getPermission() == Wcm.ACL.WRITE && countAcl(post.getAcls(), Wcm.ACL.WRITE) > 1)) {
                    wcm.remove(found, userWcm);
                    post.getAcls().remove(found);
                    post = wcm.findPost(new Long(aclPostId), userWcm);
                    if (!userWcm.canWrite(post)) post = null;
                }
            } else {
                post = null;
            }
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);

        } catch (Exception e) {
            log.warning("Error removing Acl to Post");
            e.printStackTrace();
        }
        return "/jsp/posts/postsAcls.jsp";
    }

    public String eventShowPostComments(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        try {
            Post post = wcm.findPost(new Long(postId), userWcm);
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Post's Comments");
            e.printStackTrace();
        }
        return "/jsp/posts/postsComments.jsp";
    }

    public String eventAddCommentPost(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        String comment = request.getParameter("comment");
        try {
            Post post = wcm.findPost(new Long(postId), userWcm);
            Comment c = new Comment();
            c.setContent(comment);
            c.setAuthor(userWcm.getUsername());
            c.setPost(post);
            c.setStatus(Wcm.COMMENT.PUBLIC);
            wcm.add(post, c);
            post = wcm.findPost(post.getId(), userWcm);
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Post's Comments");
            e.printStackTrace();
        }
        return "/jsp/posts/postsComments.jsp";
    }

    public String eventUpdateCommentsPost(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        String postCommentsStatus = request.getParameter("postCommentsStatus");
        try {
            Post post = wcm.findPost(new Long(postId), userWcm);
            if (postCommentsStatus.equals(Wcm.COMMENTS.ANONYMOUS.toString())) {
                post.setCommentsStatus(Wcm.COMMENTS.ANONYMOUS);
            } else if (postCommentsStatus.equals(Wcm.COMMENTS.LOGGED.toString())) {
                post.setCommentsStatus(Wcm.COMMENTS.LOGGED);
            } else if (postCommentsStatus.equals(Wcm.COMMENTS.NO_COMMENTS.toString())) {
                post.setCommentsStatus(Wcm.COMMENTS.NO_COMMENTS);
            }
            wcm.update(post, userWcm);

            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error updating Post's Comments");
            e.printStackTrace();
        }
        return "/jsp/posts/postsComments.jsp";
    }

    public String eventUpdateCommentStatusPost(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        String commentId = request.getParameter("commentId");
        String status = request.getParameter("status");
        try {
            Post post = wcm.findPost(new Long(postId), userWcm);

            if (post.getComments() != null) {
                Comment cDeleted = null;
                for (Comment c : post.getComments()) {
                    if (c.getId().equals(new Long(commentId))) {
                        if (status.equals(Wcm.COMMENT.DELETED.toString())) {
                            cDeleted = c;
                        } else if (status.equals(Wcm.COMMENT.REJECTED.toString())) {
                            c.setStatus(Wcm.COMMENT.REJECTED);
                        } else if (status.equals(Wcm.COMMENT.PUBLIC.toString())) {
                            c.setStatus(Wcm.COMMENT.PUBLIC);
                        }
                    }
                }
                if (status.equals(Wcm.COMMENT.DELETED.toString())) {
                    wcm.remove(cDeleted, userWcm);
                } else {
                    wcm.update(post, userWcm);
                }
            }
            post = wcm.findPost(new Long(postId), userWcm);
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error updating Post's Comments");
            e.printStackTrace();
        }
        return "/jsp/posts/postsComments.jsp";
    }

    public String eventShowPostRelationships(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        String filterCategoryId = request.getParameter("filterCategoryId");
        String filterName = request.getParameter("filterName");
        try {
            // List of select posts
            List<Post> posts = null;
            if (filterCategoryId != null && !"-1".equals(filterCategoryId)) {
                posts = wcm.findPosts(new Long(filterCategoryId), userWcm);
            } else if (filterName != null && !"".equals(filterName)) {
                posts = wcm.findPosts(filterName, userWcm);
            } else {
                posts = wcm.findPosts(userWcm);
            }
            request.setAttribute("posts", posts);

            // List of relationships
            List<Relationship> relations = wcm.findRelationshipsPost(new Long(postId), userWcm);
            request.setAttribute("relations", relations);

            List<Post> postsRelations = wcm.findPostsRelationshipPost(new Long(postId), userWcm);
            request.setAttribute("postsRelations", postsRelations);

            Post post = wcm.findPost(new Long(postId), userWcm);
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Post's Relationships");
            e.printStackTrace();
        }
        return "/jsp/posts/postsRelationships.jsp";
    }

    public String eventAddPostRelationship(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        String filterCategoryId = request.getParameter("filterCategoryId");
        String filterName = request.getParameter("filterName");
        String key = request.getParameter("key");
        String targetId = request.getParameter("targetid");
        try {
            // List of select posts
            List<Post> posts = null;
            if (filterCategoryId != null && !"-1".equals(filterCategoryId)) {
                posts = wcm.findPosts(new Long(filterCategoryId), userWcm);
            } else if (filterName != null && !"".equals(filterName) && !"Filter By Name".equals(filterName)) {
                posts = wcm.findPosts(filterName, userWcm);
            } else {
                posts = wcm.findPosts(userWcm);
            }
            request.setAttribute("posts", posts);

            // Add relationShip
            wcm.createRelationshipPost(new Long(postId), key, new Long(targetId), userWcm);

            // List of relationships
            List<Relationship> relations = wcm.findRelationshipsPost(new Long(postId), userWcm);
            request.setAttribute("relations", relations);

            List<Post> postsRelations = wcm.findPostsRelationshipPost(new Long(postId), userWcm);
            request.setAttribute("postsRelations", postsRelations);

            Post post = wcm.findPost(new Long(postId), userWcm);
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Post's Relationships");
            e.printStackTrace();
        }
        return "/jsp/posts/postsRelationships.jsp";
    }

    public String eventRemovePostRelationship(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String namespace = request.getParameter("namespace");
        String postId = request.getParameter("postid");
        String filterCategoryId = request.getParameter("filterCategoryId");
        String filterName = request.getParameter("filterName");
        String key = request.getParameter("key");
        String targetId = request.getParameter("targetid");
        try {
            // List of select posts
            List<Post> posts = null;
            if (filterCategoryId != null && !"-1".equals(filterCategoryId)) {
                posts = wcm.findPosts(new Long(filterCategoryId), userWcm);
            } else if (filterName != null && !"".equals(filterName) && !"Filter By Name".equals(filterName)) {
                posts = wcm.findPosts(filterName, userWcm);
            } else {
                posts = wcm.findPosts(userWcm);
            }
            request.setAttribute("posts", posts);

            // Add relationShip
            wcm.removeRelationshipPost(new Long(postId), key, userWcm);

            // List of relationships
            List<Relationship> relations = wcm.findRelationshipsPost(new Long(postId), userWcm);
            request.setAttribute("relations", relations);

            List<Post> postsRelations = wcm.findPostsRelationshipPost(new Long(postId), userWcm);
            request.setAttribute("postsRelations", postsRelations);

            Post post = wcm.findPost(new Long(postId), userWcm);
            request.setAttribute("post", post);
            request.setAttribute("namespace", namespace);
        } catch (Exception e) {
            log.warning("Error querying Post's Relationships");
            e.printStackTrace();
        }
        return "/jsp/posts/postsRelationships.jsp";
    }

    public void eventUnlockPost(ResourceRequest request, ResourceResponse response, UserWcm userWcm) {
        String postId = request.getParameter("postid");
        try {
            wcm.unlock(new Long(postId), Wcm.LOCK.POST, userWcm);
        } catch (WcmLockException e) {
            log.warning("Error unlocking Post. This case can be caused by concurrent hazard");
            e.printStackTrace();
        } catch (Exception e) {
            log.warning("Error unlocking Post");
            e.printStackTrace();
        }
    }

    private int countAcl(Set<Acl> acl, Character type) {
        if (acl == null) return -1;
        int count = 0;
        for (Acl a : acl) if (a.getPermission() == type) count++;
        return count;
    }

}
