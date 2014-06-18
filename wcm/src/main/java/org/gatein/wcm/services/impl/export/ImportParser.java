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
package org.gatein.wcm.services.impl.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gatein.wcm.domain.Acl;
import org.gatein.wcm.domain.Category;
import org.gatein.wcm.domain.Comment;
import org.gatein.wcm.domain.Post;
import org.gatein.wcm.domain.PostHistory;
import org.gatein.wcm.domain.Relationship;
import org.gatein.wcm.domain.Template;
import org.gatein.wcm.domain.TemplateHistory;
import org.gatein.wcm.domain.Upload;
import org.gatein.wcm.domain.UploadHistory;
import org.gatein.wcm.util.ParseDates;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility class for parser a previosly exported xml into org.gatein.wcm.domain.* collections.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ImportParser {

    private static final Logger log = Logger.getLogger(ImportParser.class.getName());

    private String importFolder;

    private SAXParserFactory factory;
    private SAXParser parser;
    private File gateinFile;
    private ImportHandler handler;

    private List<Category> categories;
    private Map<Long, Long> categoriesParent;
    private List<CategoryPost> categoriesPosts;
    private List<CategoryTemplate> categoriesTemplates;
    private List<CategoryUpload> categoriesUploads;

    private List<Comment> comments;
    private Map<Long, Long> commentsPost;

    private List<Post> posts;

    private List<PostHistory> postsHistory;

    private List<Relationship> relationships;

    private List<Acl> acls;
    private Map<Long, Long> aclsCategories;
    private Map<Long, Long> aclsPosts;
    private Map<Long, Long> aclsUploads;

    private List<Template> templates;

    private List<TemplateHistory> templatesHistory;

    private List<Upload> uploads;

    private List<UploadHistory> uploadsHistory;

    public ImportParser(String importFolder)
        throws Exception
    {
        if (importFolder == null || importFolder.length() == 0) {
            throw new IllegalArgumentException("ImportFolder cannot be null or empty");
        }

        this.importFolder = importFolder;
        factory = SAXParserFactory.newInstance();

        // Validates if gatein-wcm.xml exists
        gateinFile = new File(importFolder + "/gatein-wcm.xml");
        if (!gateinFile.exists()) {
            throw new IllegalArgumentException("File: " + importFolder + "/gatein-wcm.xml not found.");
        }

        parser = factory.newSAXParser();
        handler = new ImportHandler();
    }

    public void parse() throws IOException, SAXException{
        parser.parse(gateinFile, handler);
        log.info("Import task finished...");
    }

    public static class CategoryPost {
        public Long category;
        public Long post;
    }

    public static class CategoryTemplate {
        public Long category;
        public Long template;
    }

    public static class CategoryUpload {
        public Long category;
        public Long upload;
    }

    class ImportHandler extends DefaultHandler {

        static final String CATEGORIES = "categories";
        static final String CATEGORY = "category";
        static final String ID = "id";
        static final String NAME = "name";
        static final String TYPE = "type";
        static final String PARENT = "parent";
        static final String CATEGORIES_POSTS = "categories-posts";
        static final String CATEGORIES_TEMPLATES = "categories-templates";
        static final String CATEGORIES_UPLOADS = "categories-uploads";
        static final String CATEGORY_POST = "category-post";
        static final String CATEGORY_TEMPLATE = "category-template";
        static final String CATEGORY_UPLOAD = "category-upload";
        static final String POST = "post";
        static final String TEMPLATE = "template";
        static final String UPLOAD = "upload";
        static final String COMMENTS = "comments";
        static final String COMMENT = "comment";
        static final String AUTHOR = "author";
        static final String EMAIL = "email";
        static final String URL = "url";
        static final String CONTENT = "content";
        static final String CREATED = "created";
        static final String STATUS = "status";
        static final String POSTS = "posts";
        static final String EXCERPT = "excerpt";
        static final String LOCALE = "locale";
        static final String MODIFIED = "modified";
        static final String TITLE = "title";
        static final String VERSION = "version";
        static final String POSTS_HISTORY = "posts-history";
        static final String DELETED = "deleted";
        static final String RELATIONSHIPS = "relationships";
        static final String RELATIONSHIP = "relationship";
        static final String KEY = "key";
        static final String ORIGIN = "origin";
        static final String ALIAS = "alias";
        static final String SECURITY = "security";
        static final String ACL = "acl";
        static final String PERMISSION = "permission";
        static final String PRINCIPAL = "principal";
        static final String TEMPLATES = "templates";
        static final String TEMPLATES_HISTORY = "templates-history";
        static final String USER = "user";
        static final String UPLOADS = "uploads";
        static final String UPLOADS_HISTORY = "uploads-history";
        static final String DESCRIPTION = "description";
        static final String FILENAME = "filename";
        static final String MIMETYPE = "mimetype";
        static final String STORED = "stored";
        static final String COMMENT_STATUS = "comment-status";

        private boolean bCategories, bCategory, bId, bName, bType, bParent, bCategoriesPosts, bCategoryPost,
                bCategoriesTemplates, bCategoryTemplate, bCategoriesUploads, bCategoryUpload, bPost, bTemplate, bUpload,
                bComments, bComment, bAuthor, bEmail, bUrl, bContent, bCreated, bStatus, bPosts, bExcerpt, bLocale, bModified,
                bTitle, bVersion, bPostsHistory, bDeleted, bRelationShips, bRelationShip, bKey, bOrigin, bAlias, bSecurity,
                bAcl, bPermission, bPrincipal, bTemplates, bTemplatesHistory, bUser, bUploads, bUploadsHistory, bDescription,
                bFileName, bMimeType, bStored, bCommentStatus;

        private StringBuilder content;

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (CATEGORIES.equals(qName)) {
                bCategories = true;
                categories = new ArrayList<Category>();
                categoriesParent = new HashMap<Long, Long>();
            } else if (CATEGORY.equals(qName)) {
                bCategory = true;
                if (bCategories) {
                    categories.add(new Category());
                }
            } else if (ID.equals(qName)) {
                bId = true;
            } else if (NAME.equals(qName)) {
                bName = true;
            } else if (TYPE.equals(qName)) {
                bType = true;
            } else if (PARENT.equals(qName)) {
                bParent = true;
            } else if (CATEGORIES_POSTS.equals(qName)) {
                bCategoriesPosts = true;
                categoriesPosts = new ArrayList<CategoryPost>();
            } else if (CATEGORY_POST.equals(qName)) {
                bCategoryPost = true;
                if (bCategoriesPosts) {
                    categoriesPosts.add(new CategoryPost());
                }
            } else if (CATEGORIES_TEMPLATES.equals(qName)) {
                bCategoriesTemplates = true;
                categoriesTemplates = new ArrayList<CategoryTemplate>();
            } else if (CATEGORY_TEMPLATE.equals(qName)) {
                bCategoryTemplate = true;
                if (bCategoriesTemplates) {
                    categoriesTemplates.add(new CategoryTemplate());
                }
            } else if (CATEGORIES_UPLOADS.equals(qName)) {
                bCategoriesUploads = true;
                categoriesUploads = new ArrayList<CategoryUpload>();
            } else if (CATEGORY_UPLOAD.equals(qName)) {
                bCategoryUpload = true;
                if (bCategoriesUploads && bCategoryUpload) {
                    categoriesUploads.add(new CategoryUpload());
                }
            } else if (POST.equals(qName)) {
                bPost = true;
                if (bPosts) {
                    posts.add(new Post());
                } else if (bPostsHistory) {
                    postsHistory.add(new PostHistory());
                }
            } else if (TEMPLATE.equals(qName)) {
                bTemplate = true;
                if (bTemplates) {
                    templates.add(new Template());
                } else if (bTemplatesHistory) {
                    templatesHistory.add(new TemplateHistory());
                }
            } else if (UPLOAD.equals(qName)) {
                bUpload = true;
                if (bUploads) {
                    uploads.add(new Upload());
                } else if (bUploadsHistory) {
                    uploadsHistory.add(new UploadHistory());
                }
            } else if (COMMENTS.equals(qName)) {
                bComments = true;
                comments = new ArrayList<Comment>();
                commentsPost = new HashMap<Long, Long>();
            } else if (COMMENT.equals(qName)) {
                bComment = true;
                if (bComments) {
                    comments.add(new Comment());
                }
            } else if (AUTHOR.equals(qName)) {
                bAuthor = true;
            } else if (EMAIL.equals(qName)) {
                bEmail = true;
            } else if (URL.equals(qName)) {
                bUrl = true;
            } else if (CONTENT.equals(qName)) {
                bContent = true;
            } else if (CREATED.equals(qName)) {
                bCreated = true;
            } else if (STATUS.equals(qName)) {
                bStatus = true;
            } else if (POSTS.equals(qName)) {
                bPosts = true;
                posts = new ArrayList<Post>();
            } else if (EXCERPT.equals(qName)) {
                bExcerpt = true;
            } else if (LOCALE.equals(qName)) {
                bLocale = true;
            } else if (MODIFIED.equals(qName)) {
                bModified = true;
            } else if (TITLE.equals(qName)) {
                bTitle = true;
            } else if (VERSION.equals(qName)) {
                bVersion = true;
            } else if (POSTS_HISTORY.equals(qName)) {
                bPostsHistory = true;
                postsHistory = new ArrayList<PostHistory>();
            } else if (DELETED.equals(qName)) {
                bDeleted = true;
            } else if (RELATIONSHIPS.equals(qName)) {
                bRelationShips = true;
                relationships = new ArrayList<Relationship>();
            } else if (RELATIONSHIP.equals(qName)) {
                bRelationShip = true;
            } else if (KEY.equals(qName)) {
                bKey = true;
            } else if (ORIGIN.equals(qName)) {
                bOrigin = true;
            } else if (ALIAS.equals(qName)) {
                bAlias = true;
            } else if (SECURITY.equals(qName)) {
                bSecurity = true;
                acls = new ArrayList<Acl>();
                aclsCategories = new HashMap<Long, Long>();
                aclsPosts = new HashMap<Long, Long>();
                aclsUploads = new HashMap<Long, Long>();
            } else if (ACL.equals(qName)) {
                bAcl = true;
                if (bSecurity) {
                    acls.add(new Acl());
                }
            } else if (PERMISSION.equals(qName)) {
                bPermission = true;
            } else if (PRINCIPAL.equals(qName)) {
                bPrincipal = true;
            } else if (TEMPLATES.equals(qName)) {
                bTemplates = true;
                templates = new ArrayList<Template>();
            } else if (TEMPLATES_HISTORY.equals(qName)) {
                bTemplatesHistory = true;
                templatesHistory = new ArrayList<TemplateHistory>();
            } else if (USER.equals(qName)) {
                bUser = true;
            } else if (UPLOADS.equals(qName)) {
                bUploads = true;
                uploads = new ArrayList<Upload>();
            } else if (UPLOADS_HISTORY.equals(qName)) {
                bUploadsHistory = true;
                uploadsHistory = new ArrayList<UploadHistory>();
            } else if (DESCRIPTION.equals(qName)) {
                bDescription = true;
            } else if (FILENAME.equals(qName)) {
                bFileName = true;
            } else if (MIMETYPE.equals(qName)) {
                bMimeType = true;
            } else if (STORED.equals(qName)) {
                bStored = true;
            } else if (COMMENT_STATUS.equals(qName)) {
                bCommentStatus = true;
            }

            content = new StringBuilder();
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            processElement(content.toString());

            if (CATEGORIES.equals(qName)) {
                bCategories = false;
            } else if (CATEGORY.equals(qName)) {
                bCategory = false;
            } else if (ID.equals(qName)) {
                bId = false;
            } else if (NAME.equals(qName)) {
                bName = false;
            } else if (TYPE.equals(qName)) {
                bType = false;
            } else if (PARENT.equals(qName)) {
                bParent = false;
            } else if (CATEGORIES_POSTS.equals(qName)) {
                bCategoriesPosts = false;
            } else if (CATEGORY_POST.equals(qName)) {
                bCategoryPost = false;
            } else if (CATEGORIES_TEMPLATES.equals(qName)) {
                bCategoriesTemplates = false;
            } else if (CATEGORY_TEMPLATE.equals(qName)) {
                bCategoryTemplate = false;
            } else if (CATEGORIES_UPLOADS.equals(qName)) {
                bCategoriesUploads = false;
            } else if (CATEGORY_UPLOAD.equals(qName)) {
                bCategoryUpload = false;
            } else if (POST.equals(qName)) {
                bPost = false;
            } else if (TEMPLATE.equals(qName)) {
                bTemplate = false;
            } else if (UPLOAD.equals(qName)) {
                bUpload = false;
            } else if (COMMENTS.equals(qName)) {
                bComments = false;
            } else if (COMMENT.equals(qName)) {
                bComment = false;
            } else if (AUTHOR.equals(qName)) {
                bAuthor = false;
            } else if (EMAIL.equals(qName)) {
                bEmail = false;
            } else if (URL.equals(qName)) {
                bUrl = false;
            } else if (CONTENT.equals(qName)) {
                bContent = false;
            } else if (CREATED.equals(qName)) {
                bCreated = false;
            } else if (STATUS.equals(qName)) {
                bStatus = false;
            } else if (POSTS.equals(qName)) {
                bPosts = false;
            } else if (EXCERPT.equals(qName)) {
                bExcerpt = false;
            } else if (LOCALE.equals(qName)) {
                bLocale = false;
            } else if (MODIFIED.equals(qName)) {
                bModified = false;
            } else if (TITLE.equals(qName)) {
                bTitle = false;
            } else if (VERSION.equals(qName)) {
                bVersion = false;
            } else if (POSTS_HISTORY.equals(qName)) {
                bPostsHistory = false;
            } else if (DELETED.equals(qName)) {
                bDeleted = false;
            } else if (RELATIONSHIPS.equals(qName)) {
                bRelationShips = false;
            } else if (RELATIONSHIP.equals(qName)) {
                bRelationShip = false;
            } else if (KEY.equals(qName)) {
                bKey = false;
            } else if (ORIGIN.equals(qName)) {
                bOrigin = false;
            } else if (ALIAS.equals(qName)) {
                bAlias = false;
            } else if (SECURITY.equals(qName)) {
                bSecurity = false;
            } else if (ACL.equals(qName)) {
                bAcl = false;
            } else if (PERMISSION.equals(qName)) {
                bPermission = false;
            } else if (PRINCIPAL.equals(qName)) {
                bPrincipal = false;
            } else if (TEMPLATES.equals(qName)) {
                bTemplates = false;
            } else if (TEMPLATES_HISTORY.equals(qName)) {
                bTemplatesHistory = false;
            } else if (USER.equals(qName)) {
                bUser = false;
            } else if (UPLOADS.equals(qName)) {
                bUploads = false;
            } else if (UPLOADS_HISTORY.equals(qName)) {
                bUploadsHistory = false;
            } else if (DESCRIPTION.equals(qName)) {
                bDescription = false;
            } else if (FILENAME.equals(qName)) {
                bFileName = false;
            } else if (MIMETYPE.equals(qName)) {
                bMimeType = false;
            } else if (STORED.equals(qName)) {
                bStored = false;
            } else if (COMMENT_STATUS.equals(qName)) {
                bCommentStatus = false;
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            String value = new String(ch, start, length).trim();
            content.append(value);
        }

        public void processElement(String value) {
            if (bCategories && bCategory && categories.size() > 0) {
                Category c = categories.get(categories.size() - 1);
                if (bId && value.length() > 0) {
                    c.setId(new Long(value));
                } else if (bName) {
                    c.setName(value);
                } else if (bType && value.length() > 0) {
                    c.setType(value.charAt(0));
                } else if (bParent && c.getId() != null && value.length() > 0) {
                    categoriesParent.put(c.getId(), new Long(value));
                }
            } else if (bCategoriesPosts && bCategoryPost && categoriesPosts.size() > 0 && value.length() > 0) {
                CategoryPost categoryPost = categoriesPosts.get(categoriesPosts.size() - 1);
                if (bCategory) {
                    categoryPost.category = new Long(value);
                } else if (bPost) {
                    categoryPost.post = new Long(value);
                }
            } else if (bCategoriesTemplates && bCategoryTemplate && categoriesTemplates.size() > 0 && value.length() > 0) {
                CategoryTemplate categoryTemplate = categoriesTemplates.get(categoriesTemplates.size() - 1);
                if (bCategory) {
                    categoryTemplate.category = new Long(value);
                } else if (bTemplate) {
                    categoryTemplate.template = new Long(value);
                }
            } else if (bCategoriesUploads && bCategoryUpload && categoriesUploads.size() > 0 && value.length() > 0) {
                CategoryUpload categoryUpload = categoriesUploads.get(categoriesUploads.size() - 1);
                if (bCategory) {
                    categoryUpload.category = new Long(value);
                } else if (bUpload) {
                    categoryUpload.upload = new Long(value);
                }
            } else if (bComments && bComment && comments.size() > 0) {
                Comment c = comments.get(comments.size() - 1);
                if (bId && value.length() > 0) {
                    c.setId(new Long(value));
                } else if (bAuthor) {
                    c.setAuthor(value);
                } else if (bEmail) {
                    c.setAuthorEmail(value);
                } else if (bUrl) {
                    c.setAuthorEmail(value);
                } else if (bContent) {
                    c.setContent(value);
                } else if (bCreated && value.length() > 0) {
                    c.setCreated(ParseDates.parseFromFile(value));
                } else if (bStatus && value.length() > 0) {
                    c.setStatus(value.charAt(0));
                } else if (bPost && c.getId() != null && value.length() > 0) {
                    commentsPost.put(c.getId(), new Long(value));
                }
            } else if (bPosts && bPost && posts.size() > 0) {
                Post p = posts.get(posts.size() - 1);
                if (bId && value.length() > 0) {
                    p.setId(new Long(value));
                } else if (bAuthor) {
                    p.setAuthor(value);
                } else if (bStatus && value.length() > 0) {
                    p.setPostStatus(value.charAt(0));
                } else if (bContent) {
                    p.setContent(value);
                } else if (bCreated && value.length() > 0) {
                    p.setCreated(ParseDates.parseFromFile(value));
                } else if (bExcerpt) {
                    p.setExcerpt(value);
                } else if (bLocale) {
                    p.setLocale(value);
                } else if (bModified && value.length() > 0) {
                    p.setModified(ParseDates.parseFromFile(value));
                } else if (bStatus && value.length() > 0) {
                    p.setPostStatus(value.charAt(0));
                } else if (bTitle) {
                    p.setTitle(value);
                } else if (bVersion && value.length() > 0) {
                    p.setVersion(new Long(value));
                } else if (bCommentStatus && value.length() > 0) {
                    p.setCommentsStatus(value.charAt(0));
                }
            } else if (bPostsHistory && bPost && postsHistory.size() > 0) {
                PostHistory p = postsHistory.get(postsHistory.size() - 1);
                if (bId && value.length() > 0) {
                    p.setId(new Long(value));
                } else if (bAuthor) {
                    p.setAuthor(value);
                } else if (bStatus && value.length() > 0) {
                    p.setPostStatus(value.charAt(0));
                } else if (bContent) {
                    p.setContent(value);
                } else if (bCreated && value.length() > 0) {
                    p.setCreated(ParseDates.parseFromFile(value));
                } else if (bExcerpt) {
                    p.setExcerpt(value);
                } else if (bLocale) {
                    p.setLocale(value);
                } else if (bModified && value.length() > 0) {
                    p.setModified(ParseDates.parseFromFile(value));
                } else if (bStatus && value.length() > 0) {
                    p.setPostStatus(value.charAt(0));
                } else if (bTitle) {
                    p.setTitle(value);
                } else if (bVersion && value.length() > 0) {
                    p.setVersion(new Long(value));
                } else if (bDeleted && value.length() > 0) {
                    p.setDeleted(ParseDates.parseFromFile(value));
                }
            } else if (bRelationShips && bRelationShip && relationships.size() > 0) {
                Relationship r = relationships.get(relationships.size() - 1);
                if (bKey) {
                    r.setKey(value);
                } else if (bOrigin && value.length() > 0) {
                    r.setOriginId(new Long(value));
                } else if (bAlias && value.length() > 0) {
                    r.setAliasId(new Long(value));
                } else if (bType && value.length() > 0) {
                    r.setType(value.charAt(0));
                }
            } else if (bSecurity && bAcl && acls.size() > 0) {
                Acl a = acls.get(acls.size() - 1);
                if (bId && value.length() > 0) {
                    a.setId(new Long(value));
                } else if (bPermission && value.length() > 0) {
                    a.setPermission(value.charAt(0));
                } else if (bPrincipal) {
                    a.setPrincipal(value);
                } else if (bCategory && a.getId() != null && value.length() > 0) {
                    aclsCategories.put(a.getId(), new Long(value));
                } else if (bPost && a.getId() != null && value.length() > 0) {
                    aclsPosts.put(a.getId(), new Long(value));
                } else if (bUpload && a.getId() != null && value.length() > 0) {
                    aclsUploads.put(a.getId(), new Long(value));
                }
            } else if (bTemplates && bTemplate && templates.size() > 0) {
                Template t = templates.get(templates.size() - 1);
                if (bId && value.length() > 0) {
                    t.setId(new Long(value));
                } else if (bContent) {
                    t.setContent(value);
                } else if (bCreated && value.length() > 0) {
                    t.setCreated(ParseDates.parseFromFile(value));
                } else if (bLocale) {
                    t.setLocale(value);
                } else if (bModified && value.length() > 0) {
                    t.setModified(ParseDates.parseFromFile(value));
                } else if (bName) {
                    t.setName(value);
                } else if (bUser) {
                    t.setUser(value);
                } else if (bVersion && value.length() > 0) {
                    t.setVersion(new Long(value));
                }
            } else if (bTemplatesHistory && bTemplate && templatesHistory.size() > 0) {
                TemplateHistory t = templatesHistory.get(templatesHistory.size() -1);
                if (bId && value.length() > 0) {
                    t.setId(new Long(value));
                } else if (bContent) {
                    t.setContent(value);
                } else if (bCreated && value.length() > 0) {
                    t.setCreated(ParseDates.parseFromFile(value));
                } else if (bLocale) {
                    t.setLocale(value);
                } else if (bModified && value.length() > 0) {
                    t.setModified(ParseDates.parseFromFile(value));
                } else if (bName) {
                    t.setName(value);
                } else if (bUser) {
                    t.setUser(value);
                } else if (bVersion && value.length() > 0) {
                    t.setVersion(new Long(value));
                } else if (bDeleted && value.length() > 0) {
                    t.setDeleted(ParseDates.parseFromFile(value));
                }
            } else if (bUploads && bUpload && uploads.size() > 0) {
                Upload u = uploads.get(uploads.size() - 1);
                if (bId && value.length() > 0) {
                    u.setId(new Long(value));
                } else if (bVersion) {
                    u.setVersion(new Long(value));
                } else if (bCreated && value.length() > 0) {
                    u.setCreated(ParseDates.parseFromFile(value));
                } else if (bDescription) {
                    u.setDescription(value);
                } else if (bFileName) {
                    u.setFileName(value);
                } else if (bMimeType) {
                    u.setMimeType(value);
                } else if (bStored) {
                    u.setStoredName(value);
                } else if (bModified && value.length() > 0) {
                    u.setModified(ParseDates.parseFromFile(value));
                } else if (bUser) {
                    u.setUser(value);
                }
            } else if (bUploadsHistory && bUpload && uploadsHistory.size() > 0) {
                UploadHistory u = uploadsHistory.get(uploadsHistory.size() - 1);
                if (bId && value.length() > 0) {
                    u.setId(new Long(value));
                } else if (bVersion && value.length() > 0) {
                    u.setVersion(new Long(value));
                } else if (bCreated && value.length() > 0) {
                    u.setCreated(ParseDates.parseFromFile(value));
                } else if (bDescription) {
                    u.setDescription(value);
                } else if (bFileName) {
                    u.setFileName(value);
                } else if (bMimeType) {
                    u.setMimeType(value);
                } else if (bStored) {
                    u.setStoredName(value);
                } else if (bModified && value.length() > 0) {
                    u.setModified(ParseDates.parseFromFile(value));
                } else if (bUser) {
                    u.setUser(value);
                } else if (bDeleted && value.length() > 0) {
                    u.setDeleted(ParseDates.parseFromFile(value));
                }
            }
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Map<Long, Long> getCategoriesParent() {
        return categoriesParent;
    }

    public List<CategoryPost> getCategoriesPosts() {
        return categoriesPosts;
    }

    public List<CategoryTemplate> getCategoriesTemplates() {
        return categoriesTemplates;
    }

    public List<CategoryUpload> getCategoriesUploads() {
        return categoriesUploads;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Map<Long, Long> getCommentsPost() {
        return commentsPost;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<PostHistory> getPostsHistory() {
        return postsHistory;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public List<Acl> getAcls() {
        return acls;
    }

    public Map<Long, Long> getAclsCategories() {
        return aclsCategories;
    }

    public Map<Long, Long> getAclsPosts() {
        return aclsPosts;
    }

    public Map<Long, Long> getAclsUploads() {
        return aclsUploads;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public List<TemplateHistory> getTemplatesHistory() {
        return templatesHistory;
    }

    public List<Upload> getUploads() {
        return uploads;
    }

    public List<UploadHistory> getUploadsHistory() {
        return uploadsHistory;
    }
}
