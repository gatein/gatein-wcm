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

import java.util.List;
import java.util.Set;

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
import org.gatein.wcm.portlet.util.ParseDates;

/**
 * Utility class for convert org.gatein.wcm.domain.* collections into a xml format used for export.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class ExportBuilder {
    StringBuilder categories;
    StringBuilder categoriesPosts;
    StringBuilder categoriesTemplates;
    StringBuilder categoriesUploads;
    StringBuilder comments;
    StringBuilder posts;
    StringBuilder postsHistory;
    StringBuilder relationships;
    StringBuilder acls;
    StringBuilder templates;
    StringBuilder templatesHistory;
    StringBuilder uploads;
    StringBuilder uploadsHistory;

    StringBuilder export;

    public ExportBuilder() {
        categories = new StringBuilder();
        categoriesPosts = new StringBuilder();
        categoriesTemplates = new StringBuilder();
        categoriesUploads = new StringBuilder();
        comments = new StringBuilder();
        posts = new StringBuilder();
        postsHistory = new StringBuilder();
        relationships = new StringBuilder();
        acls = new StringBuilder();
        templates = new StringBuilder();
        templatesHistory = new StringBuilder();
        export = new StringBuilder();
        uploads = new StringBuilder();
        uploadsHistory = new StringBuilder();
    }

    public ExportBuilder add(List<?> list) {
        if (list != null && list.size() > 0) {
            for (Object o : list) {
                if (o instanceof Category) {
                    add((Category)o);
                } else if (o instanceof Comment) {
                    add((Comment)o);
                } else if (o instanceof Post) {
                    add((Post)o);
                } else if (o instanceof PostHistory) {
                    add((PostHistory)o);
                } else if (o instanceof Relationship) {
                    add((Relationship)o);
                } else if (o instanceof Acl) {
                    add((Acl)o);
                } else if (o instanceof Template) {
                    add((Template)o);
                } else if (o instanceof TemplateHistory) {
                    add((TemplateHistory)o);
                } else if (o instanceof Upload) {
                    add((Upload)o);
                } else if (o instanceof UploadHistory) {
                    add((UploadHistory)o);
                }
            }
        }
        return this;
    }

    private void add(Category c) {
        categories.append("<category>")
                  .append("\n")
                        .append("<id>").append(c.getId()).append("</id>")
                        .append("\n")
                        .append("<name>").append(c.getName()).append("</name>")
                        .append("\n")
                        .append("<type>").append(c.getType()).append("</type>")
                        .append("\n")
                        .append("<parent>").append(c.getParent() == null ? "" : c.getParent().getId()).append("</parent>")
                        .append("\n")
                  .append("</category>")
                  .append("\n");

        Set<Post> posts = c.getPosts();
        if (posts != null && posts.size() > 0) {
            for (Post p : posts) {
                categoriesPosts.append("<category-post>")
                               .append("\n")
                                    .append("<category>").append(c.getId()).append("</category>")
                                    .append("<post>").append(p.getId()).append("</post>")
                               .append("</category-post>")
                               .append("\n");
            }
        }

        Set<Template> templates = c.getTemplates();
        if (templates != null && templates.size() > 0) {
            for (Template t : templates) {
                categoriesTemplates.append("<category-template>")
                                   .append("\n")
                                        .append("<category>").append(c.getId()).append("</category>")
                                        .append("<template>").append(t.getId()).append("</template>")
                                   .append("</category-template>")
                                   .append("\n");
            }
        }

        Set<Upload> uploads = c.getUploads();
        if (uploads != null && uploads.size() > 0) {
            for (Upload u : uploads) {
                categoriesUploads.append("<category-upload>")
                        .append("\n")
                        .append("<category>").append(c.getId()).append("</category>")
                        .append("<upload>").append(u.getId()).append("</upload>")
                        .append("</category-upload>")
                        .append("\n");
            }
        }
    }

    private void add(Comment c) {
        comments.append("<comment>")
                .append("\n")
                    .append("<id>").append(c.getId()).append("</id>")
                    .append("\n")
                    .append("<author>").append(c.getAuthor()).append("</author>")
                    .append("\n")
                    .append("<email>").append(c.getAuthorEmail()).append("</email>")
                    .append("\n")
                    .append("<url>").append(c.getAuthorUrl()).append("</url>")
                    .append("\n")
                    .append("<content>").append(c.getContent()).append("</content>")
                    .append("\n")
                    .append("<created>").append(ParseDates.parseForFile(c.getCreated())).append("</created>")
                    .append("\n")
                    .append("<status>").append(c.getStatus()).append("</status>")
                    .append("\n")
                    .append("<post>").append(c.getPost().getId()).append("</post>")
                    .append("\n")
                .append("</comment>")
                .append("\n");
    }

    private void add(Post p) {
        posts.append("<post>")
             .append("\n")
                .append("<id>").append(p.getId()).append("</id>")
                .append("\n")
                .append("<author>").append(p.getAuthor()).append("</author>")
                .append("\n")
                .append("<status>").append(p.getPostStatus()).append("</status>")
                .append("\n")
                .append("<content>").append("<![CDATA[").append(p.getContent()).append("]]>").append("</content>")
                .append("\n")
                .append("<created>").append(ParseDates.parseForFile(p.getCreated())).append("</created>")
                .append("\n")
                .append("<excerpt>").append(p.getExcerpt()).append("</excerpt>")
                .append("\n")
                .append("<locale>").append(p.getLocale()).append("</locale>")
                .append("\n")
                .append("<modified>").append(ParseDates.parseForFile(p.getModified())).append("</modified>")
                .append("\n")
                .append("<status>").append(p.getPostStatus()).append("</status>")
                .append("\n")
                .append("<title>").append(p.getTitle()).append("</title>")
                .append("\n")
                .append("<version>").append(p.getVersion()).append("</version>")
                .append("\n")
             .append("</post>")
             .append("\n");
    }

    private void add(PostHistory p) {
        postsHistory.append("<post>")
                .append("\n")
                .append("<id>").append(p.getId()).append("</id>")
                .append("\n")
                .append("<version>").append(p.getVersion()).append("</version>")
                .append("\n")
                .append("<author>").append(p.getAuthor()).append("</author>")
                .append("\n")
                .append("<status>").append(p.getPostStatus()).append("</status>")
                .append("\n")
                .append("<content>").append("<![CDATA[").append(p.getContent()).append("]]>").append("</content>")
                .append("\n")
                .append("<created>").append(ParseDates.parseForFile(p.getCreated())).append("</created>")
                .append("\n")
                .append("<deleted>").append(ParseDates.parseForFile(p.getDeleted())).append("</deleted>")
                .append("\n")
                .append("<excerpt>").append(p.getExcerpt()).append("</excerpt>")
                .append("\n")
                .append("<locale>").append(p.getLocale()).append("</locale>")
                .append("\n")
                .append("<modified>").append(ParseDates.parseForFile(p.getModified())).append("</modified>")
                .append("\n")
                .append("<status>").append(p.getPostStatus()).append("</status>")
                .append("\n")
                .append("<title>").append(p.getTitle()).append("</title>")
                .append("\n")
                .append("<version>").append(p.getVersion()).append("</version>")
                .append("\n")
                .append("</post>")
                .append("\n");
    }

    private void add(Relationship r) {
        relationships.append("<relationship>")
                .append("\n")
                .append("<key>").append(r.getKey()).append("</key>")
                .append("\n")
                .append("<origin>").append(r.getOriginId()).append("</origin>")
                .append("\n")
                .append("<alias>").append(r.getAliasId()).append("</alias>")
                .append("\n")
                .append("<type>").append(r.getType()).append("</type>")
                .append("\n")
                .append("</relationship>")
                .append("\n");
    }

    private void add(Acl a) {
        acls.append("<acl>")
                .append("\n")
                .append("<id>").append(a.getId()).append("</id>")
                .append("\n")
                .append("<permission>").append(a.getPermission()).append("</permission>")
                .append("\n")
                .append("<principal>").append(a.getPrincipal()).append("</principal>")
                .append("\n")
                .append("<category>").append(a.getCategory() == null ? "":a.getCategory().getId()).append("</category>")
                .append("\n")
                .append("<post>").append(a.getPost() == null ? "":a.getPost().getId()).append("</post>")
                .append("\n")
                .append("<upload>").append(a.getUpload() == null ? "":a.getUpload().getId()).append("</upload>")
                .append("\n")
                .append("</acl>")
                .append("\n");
    }

    private void add(Template t) {
        templates.append("<template>")
                .append("\n")
                .append("<id>").append(t.getId()).append("</id>")
                .append("\n")
                .append("<content>").append("<![CDATA[").append(t.getContent()).append("]]>").append("</content>")
                .append("\n")
                .append("<created>").append(ParseDates.parseForFile(t.getCreated())).append("</created>")
                .append("\n")
                .append("<locale>").append(t.getLocale()).append("</locale>")
                .append("\n")
                .append("<modified>").append(ParseDates.parseForFile(t.getModified())).append("</modified>")
                .append("\n")
                .append("<name>").append(t.getName()).append("</name>")
                .append("\n")
                .append("<user>").append(t.getUser()).append("</user>")
                .append("\n")
                .append("<version>").append(t.getVersion()).append("</version>")
                .append("\n")
                .append("</template>")
                .append("\n");
    }

    private void add(TemplateHistory t) {
        templatesHistory.append("<template>")
                .append("\n")
                .append("<id>").append(t.getId()).append("</id>")
                .append("\n")
                .append("<version>").append(t.getVersion()).append("</version>")
                .append("\n")
                .append("<content>").append("<![CDATA[").append(t.getContent()).append("]]>").append("</content>")
                .append("\n")
                .append("<created>").append(ParseDates.parseForFile(t.getCreated())).append("</created>")
                .append("\n")
                .append("<locale>").append(t.getLocale()).append("</locale>")
                .append("\n")
                .append("<modified>").append(ParseDates.parseForFile(t.getModified())).append("</modified>")
                .append("\n")
                .append("<name>").append(t.getName()).append("</name>")
                .append("\n")
                .append("<user>").append(t.getUser()).append("</user>")
                .append("\n")
                .append("<deleted>").append(ParseDates.parseForFile(t.getDeleted())).append("</deleted>")
                .append("\n")
                .append("</template>")
                .append("\n");
    }

    private void add(Upload u) {
        uploads.append("<upload>")
                .append("\n")
                .append("<id>").append(u.getId()).append("</id>")
                .append("\n")
                .append("<created>").append(ParseDates.parseForFile(u.getCreated())).append("</created>")
                .append("\n")
                .append("<description>").append(u.getDescription()).append("</description>")
                .append("\n")
                .append("<filename>").append(u.getFileName()).append("</filename>")
                .append("\n")
                .append("<mimetype>").append(u.getMimeType()).append("</mimetype>")
                .append("\n")
                .append("<modified>").append(ParseDates.parseForFile(u.getModified())).append("</modified>")
                .append("\n")
                .append("<stored>").append(u.getStoredName()).append("</stored>")
                .append("\n")
                .append("<user>").append(u.getUser()).append("</user>")
                .append("\n")
                .append("<version>").append(u.getVersion()).append("</version>")
                .append("\n")
                .append("</upload>")
                .append("\n");
    }

    private void add(UploadHistory u) {
        uploadsHistory.append("<upload>")
                .append("\n")
                .append("<id>").append(u.getId()).append("</id>")
                .append("\n")
                .append("<version>").append(u.getVersion()).append("</version>")
                .append("\n")
                .append("<created>").append(ParseDates.parseForFile(u.getCreated())).append("</created>")
                .append("\n")
                .append("<deleted>").append(ParseDates.parseForFile(u.getDeleted())).append("</deleted>")
                .append("\n")
                .append("<description>").append(u.getDescription()).append("</description>")
                .append("\n")
                .append("<filename>").append(u.getFileName()).append("</filename>")
                .append("\n")
                .append("<mimetype>").append(u.getMimeType()).append("</mimetype>")
                .append("\n")
                .append("<modified>").append(ParseDates.parseForFile(u.getModified())).append("</modified>")
                .append("\n")
                .append("<stored>").append(u.getStoredName()).append("</stored>")
                .append("\n")
                .append("<user>").append(u.getUser()).append("</user>")
                .append("\n")
                .append("</upload>")
                .append("\n");
    }

    public StringBuilder build() {
        export.append("<gatein-wcm>")
              .append("\n")
                .append("<categories>")
                .append("\n")
                .append(categories)
                .append("</categories>")
                .append("\n")
                .append("<categories-posts>")
                .append("\n")
                .append(categoriesPosts)
                .append("</categories-posts>")
                .append("\n")
                .append("<categories-templates>")
                .append("\n")
                .append(categoriesTemplates)
                .append("</categories-templates>")
                .append("\n")
                .append("<categories-uploads>")
                .append("\n")
                .append(categoriesUploads)
                .append("</categories-uploads>")
                .append("\n")
                .append("<comments>")
                .append("\n")
                .append(comments)
                .append("</comments>")
                .append("\n")
                .append("<posts>")
                .append("\n")
                .append(posts)
                .append("</posts>")
                .append("\n")
                .append("<posts-history>")
                .append("\n")
                .append(postsHistory)
                .append("</posts-history>")
                .append("\n")
                .append("<relationships>")
                .append("\n")
                .append(relationships)
                .append("</relationships>")
                .append("\n")
                .append("<security>")
                .append("\n")
                .append(acls)
                .append("</security>")
                .append("\n")
                .append("<templates>")
                .append("\n")
                .append(templates)
                .append("</templates>")
                .append("\n")
                .append("<templates-history>")
                .append("\n")
                .append(templatesHistory)
                .append("</templates-history>")
                .append("\n")
                .append("<uploads>")
                .append("\n")
                .append(uploads)
                .append("</uploads>")
                .append("\n")
                .append("<uploads-history>")
                .append("\n")
                .append(uploadsHistory)
                .append("</uploads-history>")
                .append("\n")
                .append("</gatein-wcm>")
              .append("\n");

        return export;
    }
}
