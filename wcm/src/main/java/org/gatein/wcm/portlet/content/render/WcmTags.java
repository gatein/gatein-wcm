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

package org.gatein.wcm.portlet.content.render;

import org.gatein.wcm.Wcm;
import org.gatein.wcm.domain.*;
import org.gatein.wcm.util.ParseDates;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Wcm custom tags processing.
 *
 * @author <a href="mailto:lponce@redhat.com">Lucas Ponce</a>
 */
public class WcmTags {
    private static final Logger log = Logger.getLogger(WcmTags.class.getName());

    private Map<String, String> urlParams;
    private String namespace;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * <wcm-list> / <wcm-param-list> tag processing
     *
     * Searchs for <wcm-list> or <wcm-param-list> tags inside a template and replaces it with a combined list of posts.
     *
     * @param tagWcmList Tag to search, it should be "wcm-list" or "wcm-param-list" text.
     * @param initialTemplate Template where to search and replace.
     * @param listPosts List of posts to combine in the tag.
     * @param params Optional parameters to include in the tag processing.
     * @param userWcm User processing template.
     */
    public void tagWcmList(String tagWcmList, StringBuilder initialTemplate, List<Post> listPosts, Map<String, String> params, UserWcm userWcm) {
        StringBuilder tag = extractTag(tagWcmList, initialTemplate);
        this.urlParams = params;

        if (tag != null && listPosts != null) {
            String inside = insideTag(tagWcmList, tag);
            Map<String, String> properties = propertiesTag(tag);
            int from = 0;
            int to = listPosts.size();
            if (properties.containsKey("from")) {
                String value = properties.get("from");
                if (value.equals("first")) {
                    from = 0;
                } else if (value.equals("last")) {
                    from = listPosts.size();
                } else {
                    try {
                        from = new Integer(value).intValue();
                        if (from < 0) from = 0;
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            if (properties.containsKey("to")) {
                String value = properties.get("to");
                if (value.equals("first")) {
                    to = 0;
                } else if (value.equals("last")) {
                    to = listPosts.size();
                } else {
                    try {
                        to = new Integer(value).intValue();
                        if (to > listPosts.size()) to = listPosts.size();
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            int size = to - from;
            StringBuilder outputList = new StringBuilder("<");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            if (properties.containsKey("id")) {
                outputList.append(" id=\"").append(properties.get("id")).append("\"");
            }
            if (properties.containsKey("class")) {
                outputList.append(" class=\"").append(properties.get("class")).append("\"");
            }

            outputList.append(" >");
            if (listPosts != null) {
                for (int i = from; i < to; i++) {
                    Post p = listPosts.get(i);
                    if (size > 1) outputList.append("<li>");
                    outputList.append(combine(inside, p, i, false, userWcm));
                    if (size > 1) outputList.append("</li>");
                }
            }
            outputList.append("</");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            outputList.append(">");
            replaceAll(initialTemplate, tag, outputList);
        } else if (tag != null && listPosts == null) {
            replaceAll(initialTemplate, tag, "<div></div>");
        }
    }

    /**
     * <wcm-file-list> tag processing
     *
     * Searchs for <wcm-file-list> tags inside a template and replaces it with a combined list of uploads.
     *
     * @param tagWcmFileList Tag to search, it should be "wcm-file-list" text.
     * @param initialTemplate Template where to search and replace.
     * @param listUploads List of uploads to combine in the tag.
     * @param params Optional parameters to include in the tag processing.
     * @param userWcm User processing template.
     */
    public void tagWcmFileList(String tagWcmFileList, StringBuilder initialTemplate, List<Upload> listUploads, Map<String, String> params, UserWcm userWcm) {
        StringBuilder tag = extractTag(tagWcmFileList, initialTemplate);
        this.urlParams = params;

        if (tag != null && listUploads != null) {
            String inside = insideTag(tagWcmFileList, tag);
            Map<String, String> properties = propertiesTag(tag);
            int from = 0;
            int to = listUploads.size();
            if (properties.containsKey("from")) {
                String value = properties.get("from");
                if (value.equals("first")) {
                    from = 0;
                } else if (value.equals("last")) {
                    from = listUploads.size();
                } else {
                    try {
                        from = new Integer(value).intValue();
                        if (from < 0) from = 0;
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            if (properties.containsKey("to")) {
                String value = properties.get("to");
                if (value.equals("first")) {
                    to = 0;
                } else if (value.equals("last")) {
                    to = listUploads.size();
                } else {
                    try {
                        to = new Integer(value).intValue();
                        if (to > listUploads.size()) to = listUploads.size();
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            int size = to - from;
            StringBuilder outputList = new StringBuilder("<");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            if (properties.containsKey("id")) {
                outputList.append(" id=\"").append(properties.get("id")).append("\"");
            }
            if (properties.containsKey("class")) {
                outputList.append(" class=\"").append(properties.get("class")).append("\"");
            }

            outputList.append(" >");
            if (listUploads != null) {
                for (int i = from; i < to; i++) {
                    Upload u = listUploads.get(i);
                    if (size > 1) outputList.append("<li>");
                    outputList.append(combineUpload(inside, u, i));
                    if (size > 1) outputList.append("</li>");
                }
            }
            outputList.append("</");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            outputList.append(">");
            replaceAll(initialTemplate, tag, outputList.toString());
        } else if (tag != null && listUploads == null) {
            replaceAll(initialTemplate, tag, "<div></div>");
        }
    }

    /**
     * <wcm-cat-list> tag processing
     *
     * Searchs for <wcm-cat-list> tags inside a template and replaces it with a combined list of categories.
     *
     * @param tagWcmList Tag to search, it should be "wcm-cat-list" text.
     * @param initialTemplate Template where to search and replace.
     * @param listCategories List of categories to combine in the tag.
     * @param params Optional parameters to include in the tag processing.
     */
    public void tagWcmCatList(String tagWcmList, StringBuilder initialTemplate, List<Category> listCategories, Map<String, String> params) {
        StringBuilder tag = extractTag(tagWcmList, initialTemplate);
        this.urlParams = params;

        if (tag != null && listCategories != null) {
            String inside = insideTag(tagWcmList, tag);
            Map<String, String> properties = propertiesTag(tag);
            int from = 0;
            int to = listCategories.size();
            if (properties.containsKey("from")) {
                String value = properties.get("from");
                if (value.equals("first")) {
                    from = 0;
                } else if (value.equals("last")) {
                    from = listCategories.size();
                } else {
                    try {
                        from = new Integer(value).intValue();
                        if (from < 0) from = 0;
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            if (properties.containsKey("to")) {
                String value = properties.get("to");
                if (value.equals("first")) {
                    to = 0;
                } else if (value.equals("last")) {
                    to = listCategories.size();
                } else {
                    try {
                        to = new Integer(value).intValue();
                        if (to > listCategories.size()) to = listCategories.size();
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            int size = to - from;
            StringBuilder outputList = new StringBuilder("<");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            if (properties.containsKey("id")) {
                outputList.append(" id=\"").append(properties.get("id")).append("\"");
            }
            if (properties.containsKey("class")) {
                outputList.append(" class=\"").append(properties.get("class")).append("\"");
            }

            outputList.append(" >");
            if (listCategories != null) {
                for (int i = from; i < to; i++) {
                    Category c = listCategories.get(i);
                    if (size > 1) outputList.append("<li>");
                    outputList.append(combineCategory(inside, c, i));
                    if (size > 1) outputList.append("</li>");
                }
            }
            outputList.append("</");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            outputList.append(">");
            replaceAll(initialTemplate, tag, outputList.toString());
        } else if (tag != null && listCategories == null) {
            replaceAll(initialTemplate, tag, "<div></div>");
        }
    }

    /**
     * <wcm-single> / <wcm-param-single> tag processing
     *
     * Searchs for <wcm-single> or <wcm-param-single> tags inside a template and replaces it with a single post.
     *
     * @param tagWcmSingle Tag to search, it should be "wcm-single" or "wcm-param-single" text.
     * @param initialTemplate Template where to search and replace.
     * @param post Post to combine in the tag.
     * @param params Optional parameters to include in the tag processing.
     * @param canWrite Indicates if this post is rendered in edit mode.
     */
    public void tagWcmSingle(String tagWcmSingle, StringBuilder initialTemplate, Post post, Map<String, String> params, boolean canWrite, UserWcm userWcm) {
        StringBuilder tag = extractTag(tagWcmSingle, initialTemplate);
        this.urlParams = params;

        if (tag != null) {
            String inside = insideTag(tagWcmSingle, tag);
            String outputSingle = combine(inside, post, 0, canWrite, userWcm);
            replaceAll(initialTemplate, tag, outputSingle);
        } else {
            replaceAll(initialTemplate, tag, "<div></div>");
        }
    }

    /**
     * <wcm-param-name> tag processing
     *
     * Searchs for <wcm-param-name> tags inside a template and replaces it with a single category.
     *
     * @param tagWcmParamName Tag to search, it should be "wcm-param-name" text.
     * @param initialTemplate Template where to search and replace.
     * @param cat Category to combine in the tag.
     */
    public void tagWcmParamName(String tagWcmParamName, StringBuilder initialTemplate, Category cat) {
        StringBuilder tag = extractTag(tagWcmParamName, initialTemplate);
        if (tag != null) {
            String output = "";
            if (cat != null && cat.getName() != null) {
                output = cat.getName();
            }
            replaceAll(initialTemplate, tag, output);
        }
    }

    /*
        Combine in-line tags with Post object
     */
    public String combine(String template, Post post, int iteration, boolean canWrite, UserWcm userWcm) {
        if (post == null) return "";
        boolean foundTag = false;
        StringBuilder output = new StringBuilder(template);
        while (!foundTag) {
            if (hasTag("wcm-categories", output)) {
                tagWcmCategories(output, post);
            } else if (hasTag("wcm-link", output)) {
                tagWcmLink(output, post);
            } else if (hasTag("wcm-img", output)) {
                tagWcmImg(output, post, canWrite);
            } else if (hasTag("wcm-title", output)) {
                tagWcmTitle(output, post, canWrite);
            } else if (hasTag("wcm-excerpt", output)) {
                tagWcmExcerpt(output, post, canWrite);
            } else if (hasTag("wcm-iter", output)) {
                tagWcmIter(output, iteration);
            } else if (hasTag("wcm-created", output)) {
                tagWcmCreated(output, post);
            } else if (hasTag("wcm-author", output)) {
                tagWcmAuthor(output, post);
            } else if (hasTag("wcm-content", output)) {
                tagWcmContent(output, post, canWrite);
            } else if (hasTag("wcm-comments", output)) {
                tagWcmComments(output, post);
            } else if (hasTag("wcm-form-comments", output)) {
                tagWcmFormComments(output, post, userWcm);
            } else {
                foundTag = true;
            }
        }
        return output.toString();
    }

    /*
        Combine in-line tags with Upload object
     */
    public String combineUpload(String template, Upload upload, int iteration) {
        if (upload == null) return "";
        boolean foundTag = false;
        StringBuilder output = new StringBuilder(template);
        while (!foundTag) {
            if (hasTag("wcm-link", output)) {
                tagWcmLink(output, upload);
            } else if (hasTag("wcm-filename", output)) {
                tagWcmFileName(output, upload);
            } else if (hasTag("wcm-iter", output)) {
                tagWcmIter(output, iteration);
            } else if (hasTag("wcm-created", output)) {
                tagWcmCreated(output, upload);
            } else if (hasTag("wcm-author", output)) {
                tagWcmAuthor(output, upload);
            } else if (hasTag("wcm-mimetype", output)) {
                tagWcmMimeType(output, upload);
            } else if (hasTag("wcm-description", output)) {
                tagWcmDescription(output, upload);
            } else {
                foundTag = true;
            }
        }
        return output.toString();
    }

    /*
        Combine in-line tags with Category object
     */
    public String combineCategory(String template, Category category, int iteration) {
        if (category == null) return "";
        boolean foundTag = false;
        StringBuilder output = new StringBuilder(template);
        while (!foundTag) {
            if (hasTag("wcm-link", output)) {
                tagWcmLink(output, category);
            } else if (hasTag("wcm-cat-name", output)) {
                tagWcmCatName(output, category);
            } else if (hasTag("wcm-iter", output)) {
                tagWcmIter(output, iteration);
            } else if (hasTag("wcm-cat-type", output)) {
                tagWcmCatType(output, category);
            } else {
                foundTag = true;
            }
        }
        return output.toString();
    }

    /*
        <wcm-link> tag processing
     */
    public void tagWcmLink(StringBuilder template, Post post) {
        StringBuilder tag = extractTag("wcm-link", template);
        String inside = insideTag("wcm-link", template);
        Map<String, String> properties = propertiesTag(tag);

        String postUrl = Wcm.SUFFIX.POST + "/" + Wcm.SUFFIX.ID + "/" + post.getId();

        // By default wcm-link add a post id to href
        if (properties.containsKey("index") && properties.get("index").equals("disable")) {
            postUrl="";
        }
        StringBuilder output = new StringBuilder("<a");
        if (properties.containsKey("href")) {
            output.append(" href=\"").append(properties.get("href")).append((postUrl.length()>0?"/" + postUrl:"")).append("\"");
        } else {
            String page = urlParams.get("page");
            if (urlParams.containsKey("post") || urlParams.containsKey("category")) {
                page = "../../../" + page;
            }
            output.append(" href=\"").append(page).append((postUrl.length()>0?"/" + postUrl:"")).append("\"");
        }
        if (properties.containsKey("class")) {
            output.append(" class=\"").append(properties.get("class")).append("\"");
        }
        output.append(">");
        output.append(inside);
        output.append("</a>");
        replaceAll(template, tag, output);
    }

    public void tagWcmLink(StringBuilder template, Category category) {
        StringBuilder tag = extractTag("wcm-link", template);
        String inside = insideTag("wcm-link", template);
        Map<String, String> properties = propertiesTag(tag);

        String categoryUrl = Wcm.SUFFIX.CATEGORY + "/" + Wcm.SUFFIX.ID + "/" + category.getId();

        // By default wcm-link add a post id to href
        if (properties.containsKey("index") && properties.get("index").equals("disable")) {
            categoryUrl="";
        }
        StringBuilder output = new StringBuilder("<a");
        if (properties.containsKey("href")) {
            output.append(" href=\"").append(properties.get("href")).append((categoryUrl.length()>0?"/" + categoryUrl:"")).append("\"");
        } else {
            String page = urlParams.get("page");
            if (urlParams.containsKey("post") || urlParams.containsKey("category")) {
                page = "../../../" + page;
            }
            output.append(" href=\"").append(page).append((!"".equals(categoryUrl)?"/" + categoryUrl:"")).append("\"");
        }
        if (properties.containsKey("class")) {
            output.append(" class=\"").append(properties.get("class")).append("\"");
        }
        output.append(">");
        output.append(inside);
        output.append("</a>");
        replaceAll(template, tag, output);
    }

    public void tagWcmLink(StringBuilder template, Upload upload) {
        StringBuilder tag = extractTag("wcm-link", template);
        String inside = insideTag("wcm-link", template);
        Map<String, String> properties = propertiesTag(tag);

        String uploadUrl = "/wcm/rs/u/" + upload.getId();

        StringBuilder output = new StringBuilder("<a");
        if (properties.containsKey("target")) {
            output.append(" target=\"").append(properties.get("target")).append("\"");
        }
        if (properties.containsKey("class")) {
            output.append(" class=\"").append(properties.get("class")).append("\"");
        }
        output.append(" href=\"").append(uploadUrl).append("\" ");
        output.append(">");
        output.append(inside);
        output.append("</a>");
        replaceAll(template, tag, output);
    }


    /*
        <wcm-img> tag processing
     */
    public void tagWcmImg(StringBuilder template, Post post, boolean canWrite) {
        StringBuilder tag = extractTag("wcm-img", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        int index = 0;
        if (properties.containsKey("index")) {
            try {
                index = new Integer(properties.get("index")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
        }
        output = extractImg(post.getContent(), index, true);
        // Check style
        if (properties.containsKey("class")) {
            String cssClass = properties.get("class");
            // Reset class in img extracted
            output = output.replaceAll("class=\"[0-9a-zA-Z_-]*\"", "");
            output = "<img class=\"" + cssClass + "\" " + output.substring(4);
        }
        // Cleaning hard code style
        // output = output.replaceAll("style=\"[0-9a-zA-Z :;,-]*\"", "");
        // Editing tags
        if (canWrite) {
            output = "<p contenteditable=\"true\" class=\"wcm-content-edit\" data-post-id=\"" + post.getId() + "\" data-post-attr=\"image\">" + output + "</p>";
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-title> tag processing
     */
    public void tagWcmTitle(StringBuilder template, Post post, boolean canWrite) {
        StringBuilder tag = extractTag("wcm-title", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < post.getTitle().length())
                output.append(substringWord(post.getTitle(), max)).append(" ...");
            else
                output.append(post.getTitle());
        } else {
            output.append(post.getTitle());
        }
        // Editing tags
        if (canWrite) {
            output = new StringBuilder("<p contenteditable=\"true\" class=\"wcm-content-edit\" data-post-id=\"")
                    .append(post.getId())
                    .append("\" data-post-attr=\"title\">")
                    .append(output)
                    .append("</p>");
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-excerpt> tag processing
     */
    public void tagWcmExcerpt(StringBuilder template, Post post, boolean canWrite) {
        StringBuilder tag = extractTag("wcm-excerpt", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < post.getExcerpt().length())
                output.append(substringWord(post.getExcerpt(), max)).append(" ...");
            else
                output.append(post.getExcerpt());
        } else {
            output.append(post.getExcerpt());
        }
        // Editing tags
        if (canWrite) {
            output = new StringBuilder("<p contenteditable=\"true\" data-post-id=\"")
                    .append(post.getId())
                    .append("\" data-post-attr=\"excerpt\">")
                    .append(output)
                    .append("</p>");
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-iter> tag processing
     */
    public void tagWcmIter(StringBuilder template, int iteration) {
        StringBuilder tag = extractTag("wcm-iter", template);
        String inside = insideTag("wcm-iter", template);
        Map<String, String> properties = propertiesTag(tag);
        int i = -1;
        if (properties.containsKey("i")) {
            try {
                i = new Integer(properties.get("i")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (iteration == i) {
                replaceAll(template, tag, inside);
                return;
            }
        } else if (properties.containsKey("par")) {
            if ("true".equals(properties.get("par")) && (i%2 == 0)) {
                replaceAll(template, tag, inside);
                return;
            }
        }
        replaceAll(template, tag, "");
    }

    /*
        <wcm-created> tag processing
     */
    public void tagWcmCreated(StringBuilder template, Object object) {
        StringBuilder tag = extractTag("wcm-created", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("format")) {
            try {
                SimpleDateFormat custom = new SimpleDateFormat(properties.get("format"));
                if (object instanceof Post) {
                    output.append(custom.format(((Post)object).getCreated().getTime()));
                } else if (object instanceof Upload) {
                    output.append(custom.format(((Upload)object).getCreated().getTime()));
                }

            } catch (Exception e) {
                log.warning("Error parsing date with format " + properties.get("format"));
                output = new StringBuilder();
            }
        } else {
            if (object instanceof Post) {
                output.append(ParseDates.parse(((Post)object).getCreated()));
            } else if (object instanceof Upload) {
                output.append(ParseDates.parse(((Upload)object).getCreated()));
            }
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-author> tag processing
     */
    public void tagWcmAuthor(StringBuilder template, Object object) {
        StringBuilder tag = extractTag("wcm-author", template);
        String output = "";
        if (object instanceof Post) {
            output = ((Post)object).getAuthor();
        } else if (object instanceof Upload) {
            output = ((Upload)object).getUser();
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-content> tag processing
        skipImages is a list of indexes of images in the content.
        We can combine <wcm-content skipimages="0"> if we want to use <wcm-img index="0"> and I don't want to repeat the same image
     */
    public void tagWcmContent(StringBuilder template, Post post, boolean canWrite) {
        StringBuilder tag = extractTag("wcm-content", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("skipimages")) {
            try {
                output.append(post.getContent());
                String[] skipImages = properties.get("skipimages").split(",");
                for (int i=0; i<skipImages.length; i++) {
                    int iImage = new Integer(skipImages[i]).intValue();
                    String image = extractImg(output.toString(), iImage, false);
                    String imageNotVisible = notVisibleImg(image);
                    replaceAll(output, image, imageNotVisible);
                }
            } catch (Exception e) {
                log.warning("Error parsing content with skipImages " + properties.get("skipImages"));
            }
        } else {
            output.append(post.getContent());
        }
        // Editing tags
        if (canWrite) {
            output = new StringBuilder("<div contenteditable=\"true\" data-post-id=\"")
                    .append(post.getId())
                    .append("\" data-post-attr=\"content\">")
                    .append(output)
                    .append("</div>");
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-filename> tag processing
     */
    public void tagWcmFileName(StringBuilder template, Upload upload) {
        StringBuilder tag = extractTag("wcm-filename", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < upload.getFileName().length())
                output.append(substringWord(upload.getFileName(), max)).append(" ...");
            else
                output.append(upload.getFileName());
        } else {
            output.append(upload.getFileName());
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-mimetype> tag processing
     */
    public void tagWcmMimeType(StringBuilder template, Upload upload) {
        StringBuilder tag = extractTag("wcm-mimetype", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < upload.getMimeType().length())
                output.append(substringWord(upload.getMimeType(), max)).append(" ...");
            else
                output.append(upload.getMimeType());
        } else {
            output.append(upload.getMimeType());
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-description> tag processing
     */
    public void tagWcmDescription(StringBuilder template, Upload upload) {
        StringBuilder tag = extractTag("wcm-description", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < upload.getDescription().length())
                output.append(substringWord(upload.getDescription(), max)).append(" ...");
            else
                output.append(upload.getDescription());
        } else {
            output.append(upload.getDescription());
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-cat-name> tag processing
     */
    public void tagWcmCatName(StringBuilder template, Category category) {
        StringBuilder tag = extractTag("wcm-cat-name", template);
        Map<String, String> properties = propertiesTag(tag);
        StringBuilder output = new StringBuilder();
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < category.getName().length())
                output.append(substringWord(category.getName(), max)).append(" ...");
            else
                output.append(category.getName());
        } else {
            output.append(category.getName());
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-cat-type> tag processing
     */
    public void tagWcmCatType(StringBuilder template, Category category) {
        StringBuilder tag = extractTag("wcm-cat-type", template);
        String output = "";
        if (category.getType().equals(Wcm.CATEGORIES.FOLDER)) {
            output = "Folder";
        } else if (category.getType().equals(Wcm.CATEGORIES.CATEGORY)) {
            output = "Category";
        } else if (category.getType().equals(Wcm.CATEGORIES.TAG)) {
            output = "Tag";
        }
        replaceAll(template, tag, output);
    }

    /*
        <wcm-categories> tag processing
     */
    public void tagWcmCategories(StringBuilder template, Post post) {
        StringBuilder tag = extractTag("wcm-categories", template);
        Set<Category> setCategories = post.getCategories();
        if (tag != null && setCategories != null) {
            String inside = insideTag("wcm-categories", tag);
            Map<String, String> properties = propertiesTag(tag);

            String type = properties.containsKey("type") ? properties.get("type") : "all";
            if (type.equals("category")) {
                setCategories = categoryFilter(setCategories, Wcm.CATEGORIES.CATEGORY);
            } else if (type.equals("folder")) {
                setCategories = categoryFilter(setCategories, Wcm.CATEGORIES.FOLDER);
            } else if (type.equals("tag")) {
                setCategories = categoryFilter(setCategories, Wcm.CATEGORIES.TAG);
            }

            int from = 0;
            int to = setCategories.size();
            if (properties.containsKey("from")) {
                String value = properties.get("from");
                if (value.equals("first")) {
                    from = 0;
                } else if (value.equals("last")) {
                    from = setCategories.size();
                } else {
                    try {
                        from = new Integer(value).intValue();
                        if (from < 0) from = 0;
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            if (properties.containsKey("to")) {
                String value = properties.get("to");
                if (value.equals("first")) {
                    to = 0;
                } else if (value.equals("last")) {
                    to = setCategories.size();
                } else {
                    try {
                        to = new Integer(value).intValue();
                        if (to > setCategories.size()) to = setCategories.size();
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            int size = to - from;
            StringBuilder outputList = new StringBuilder("<");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            if (properties.containsKey("id")) {
                outputList.append(" id=\"").append(properties.get("id")).append("\"");
            }
            if (properties.containsKey("class")) {
                outputList.append(" class=\"").append(properties.get("class")).append("\"");
            }

            outputList.append(" >");
            if (setCategories != null) {
                Category[] categories = setCategories.toArray(new Category[setCategories.size()]);
                for (int i = from; i < to; i++) {
                    Category c = categories[i];
                    if (size > 1) outputList.append("<li>");
                    outputList.append(combineCategory(inside, c, i));
                    if (size > 1) outputList.append("</li>");
                }
            }
            outputList.append("</");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            outputList.append(">");
            replaceAll(template, tag, outputList);
        } else if (tag != null && setCategories == null) {
            replaceAll(template, tag, "<div></div>");
        }
    }

    /*
        <wcm-comments> tag processing
     */
    public void tagWcmComments(StringBuilder template, Post post) {
        StringBuilder tag = extractTag("wcm-comments", template);
        Set<Comment> setComments = post.getComments();
        if (tag != null && setComments != null && !post.getCommentsStatus().equals(Wcm.COMMENTS.NO_COMMENTS)) {
            String inside = insideTag("wcm-comments", tag);
            Map<String, String> properties = propertiesTag(tag);

            int from = 0;
            int to = setComments.size();
            if (properties.containsKey("from")) {
                String value = properties.get("from");
                if (value.equals("first")) {
                    from = 0;
                } else if (value.equals("last")) {
                    from = setComments.size();
                } else {
                    try {
                        from = new Integer(value).intValue();
                        if (from < 0) from = 0;
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            if (properties.containsKey("to")) {
                String value = properties.get("to");
                if (value.equals("first")) {
                    to = 0;
                } else if (value.equals("last")) {
                    to = setComments.size();
                } else {
                    try {
                        to = new Integer(value).intValue();
                        if (to > setComments.size()) to = setComments.size();
                    } catch (Exception e) {
                        // Default value if exception happens
                    }
                }
            }
            int size = to - from;
            StringBuilder outputList = new StringBuilder("<");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            if (properties.containsKey("id")) {
                outputList.append(" id=\"").append(properties.get("id")).append("\"");
            }
            if (properties.containsKey("class")) {
                outputList.append(" class=\"").append(properties.get("class")).append("\"");
            }

            outputList.append(" >");
            if (setComments != null) {
                Comment[] comments = setComments.toArray(new Comment[setComments.size()]);
                for (int i = from; i < to; i++) {
                    Comment c = comments[i];
                    if (c.getStatus().equals(Wcm.COMMENT.PUBLIC)) {
                        if (size > 1) outputList.append("<li>");
                        outputList.append(combineComment(inside, c, i));
                        if (size > 1) outputList.append("</li>");
                    }
                }
            }
            outputList.append("</");
            if (size == 1) {
                outputList.append("div");
            } else {
                outputList.append("ul");
            }
            outputList.append(">");
            replaceAll(template, tag, outputList);
        } else if (tag != null && setComments == null) {
            replaceAll(template, tag,  "<div></div>");
        }
    }

    /*
        Combine in-line tags with Comment object
     */
    public String combineComment(String template, Comment comment, int iteration) {
        if (comment == null) return "";
        boolean foundTag = false;
        StringBuilder output = new StringBuilder(template);
        while (!foundTag) {
            if (hasTag("wcm-comment-content", output)) {
                tagWcmCommentContent(output, comment);
            } else if (hasTag("wcm-comment-author", output)) {
                tagWcmCommentAuthor(output, comment);
            } else if (hasTag("wcm-iter", output)) {
                tagWcmIter(output, iteration);
            } else if (hasTag("wcm-comment-created", output)) {
                tagWcmCommentCreated(output, comment);
            } else {
                foundTag = true;
            }
        }
        return output.toString();
    }

    /*
        <wcm-comment-content> tag processing
     */
    public void tagWcmCommentContent(StringBuilder template, Comment comment) {
        StringBuilder tag = extractTag("wcm-comment-content", template);
        String output = "";
        if (comment.getContent() != null)
            output = comment.getContent();
        replaceAll(template, tag, output);
    }

    /*
        <wcm-comment-created> tag processing
     */
    public void tagWcmCommentCreated(StringBuilder template, Comment comment) {
        StringBuilder tag = extractTag("wcm-comment-created", template);
        String output = "";
        if (comment.getCreated() != null)
            output = ParseDates.parse(comment.getCreated());
        replaceAll(template, tag, output);
    }

    /*
        <wcm-comment-author> tag processing
     */
    public void tagWcmCommentAuthor(StringBuilder template, Comment comment) {
        StringBuilder tag = extractTag("wcm-comment-author", template);
        String output = "";
        if (comment.getAuthor() != null)
            output = comment.getAuthor();
        replaceAll(template, tag, output);
    }

    /*
        <wcm-form-comments> tag processing
     */
    public void tagWcmFormComments(StringBuilder template, Post post, UserWcm userWcm) {
        StringBuilder tag = extractTag("wcm-form-comments", template);
        Map<String, String> properties = propertiesTag(tag);

        String type = properties.containsKey("type") ? properties.get("type") : null;
        if (type != null && type.equals("anonymous") && !post.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS)) {
            replaceAll(template, tag, "");
            return;
        }
        if (type != null && type.equals("logged") && !post.getCommentsStatus().equals(Wcm.COMMENTS.LOGGED)) {
            replaceAll(template, tag, "");
            return;
        }

        boolean noComments = post.getCommentsStatus().equals(Wcm.COMMENTS.NO_COMMENTS);
        boolean postAnonymous = post.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS);
        boolean userAnonymous = userWcm.getUsername().equals("anonymous");

        if (noComments) {
            replaceAll(template, tag, "");
            return;
        } else {
            if (!postAnonymous && userAnonymous) {
                replaceAll(template, tag, "");
                return;
            } else {
                String inside = insideTag("wcm-form-comments", tag);
                String output = combineCommentForm(inside, post, userWcm);
                replaceAll(template, tag, output);
                return;
            }
        }
    }

    /*
        Combine in-line tags with Comment object
     */
    public String combineCommentForm(String template, Post post, UserWcm userWcm) {
        if (post == null) return "";
        boolean foundTag = false;
        boolean postAnonymous = post.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS);
        StringBuilder output = new StringBuilder(template);
        while (!foundTag) {
            if (hasTag("wcm-form-content", output)) {
                tagWcmCommentsFormContent(output);
            } else if (postAnonymous && hasTag("wcm-form-author", output)) {
                tagWcmCommentsFormAuthor(output);
            } else if (postAnonymous && hasTag("wcm-form-email", output)) {
                tagWcmCommentsFormEmail(output);
            } else if (postAnonymous && hasTag("wcm-form-url", output)) {
                tagWcmCommentsFormUrl(output);
            } else if (hasTag("wcm-form-button",  output)) {
                tagWcmCommentsFormButton(output, post);
            } else {
                foundTag = true;
            }
        }
        return output.toString();
    }

    /*
        <wcm-form-content> tag processing
     */
    public void tagWcmCommentsFormContent(StringBuilder template) {
        StringBuilder tag = extractTag("wcm-form-content", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        StringBuilder output = new StringBuilder("<textarea id=\"")
                                .append(this.namespace)
                                .append("-content\"")
                                .append(inputClass)
                                .append("></textarea>");
        replaceAll(template, tag, output);
    }

    /*
        <wcm-form-author> tag processing
     */
    public void tagWcmCommentsFormAuthor(StringBuilder template) {
        StringBuilder tag = extractTag("wcm-form-author", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        StringBuilder output = new StringBuilder("<input id=\"")
                                .append(this.namespace)
                                .append("-author\"")
                                .append(inputClass)
                                .append(" />");
        replaceAll(template, tag, output);
    }

    /*
        <wcm-form-email> tag processing
     */
    public void tagWcmCommentsFormEmail(StringBuilder template) {
        StringBuilder tag = extractTag("wcm-form-email", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        StringBuilder output = new StringBuilder("<input id=\"")
                                .append(this.namespace)
                                .append("-email\"")
                                .append(inputClass)
                                .append(" />");
        replaceAll(template, tag, output);
    }

    /*
        <wcm-form-url> tag processing
     */
    public void tagWcmCommentsFormUrl(StringBuilder template) {
        StringBuilder tag = extractTag("wcm-form-url", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        StringBuilder output = new StringBuilder("<input id=\"")
                                .append(this.namespace)
                                .append("-url\"")
                                .append(inputClass)
                                .append(" />");
        replaceAll(template, tag, output);
    }

    /*
        <wcm-form-button> tag processing
     */
    public void tagWcmCommentsFormButton(StringBuilder template, Post post) {
        StringBuilder tag = extractTag("wcm-form-button", template);
        String inside = insideTag("wcm-form-button", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        StringBuilder output = new StringBuilder("<a id=\"")
                                .append(this.namespace)
                                .append("-addComment\" ")
                                .append(inputClass)
                                .append(" href=\"javascript:;\" onclick=\"wcmAddComment('")
                                .append(this.namespace)
                                .append("', '")
                                .append(post.getId())
                                .append("');\">")
                                .append(inside)
                                .append("</a>");
        replaceAll(template, tag, output);
    }

    /*
        Aux functions to manipulate tags
     */

    /*
        Checks if a specific tag is present in a template
     */
    public boolean hasTag(String tagName, StringBuilder template) {
        if (template == null || template.length() == 0) return false;
        return (template.indexOf("<" + tagName) > -1);
    }

    /*
        Extracts a tag from a template
     */
    public StringBuilder extractTag(String tagName, StringBuilder template) {
        StringBuilder output = new StringBuilder();
        int i = template.indexOf("<" + tagName);
        if (i != -1) {
            int j = template.indexOf(">", i);
            // Check if we are in <tag /> or <tag></tag>
            if (j > 0 && template.charAt(j-1) == '/') {
                output.append(template.substring(i, j + 1));
            } else {
                j = template.indexOf("</" + tagName + ">", i);
                if (j>-1) {
                    output.append(template.substring(i, j + ("</" + tagName + ">").length()));
                }
            }
        }
        return output;
    }

    /*
        Extracts <img> tag without styling only img source.
     */
    public String extractImg(String html, int index, boolean skipStyles) {
        if (html == null) return "<img>";
        int found = 0;
        int i = 0;
        int j = 0;
        String output = "<img>";
        while (found != -1) {
            j = html.indexOf("<img", i);
            if (j != -1) {
                if (found == index) {
                    i = html.indexOf(">", j);
                    output = html.substring(j, i+1);
                    found = -1;
                } else {
                    i = j+1;
                    found++;
                }
            } else {
                found = -1;
            }
        }
        // <img src="" class="" style="">
        // Extracts only src to build a new one without styling
        if (skipStyles)
        {
            String src="";
            i = output.indexOf("src=");
            if (i > -1) {
                j = output.indexOf(" ", i);
                src = output.substring(i, j);
                output = "<img " + src + ">";
            }
        }
        return output;
    }

    /*
        Extracts properties from a previously extracted tag
     */
    public Map<String, String> propertiesTag(StringBuilder tag) {
        Map<String, String> output = new HashMap<String, String>();
        if (tag != null) {
            int start = tag.indexOf(" ");
            int i = start;
            int j = 0;
            while (j != -1) {
                // Look property name
                j = tag.indexOf("=", i);
                if (j != -1) {
                    String name = tag.substring(i, j).trim();
                    i = tag.indexOf("\"", j);
                    if (i < 0) {
                        i = tag.indexOf("'", j);
                    }
                    j = tag.indexOf("\"", i+1);
                    if (j < 0) {
                        j = tag.indexOf("'", i+1);
                    }
                    String value = tag.substring(i+1, j);
                    i = j+1;
                    output.put(name, value);
                }
            }
        }
        return output;
    }

    /*
        Returns inner content of a tag
     */
    public String insideTag(String tagName, StringBuilder template) {
        String output = "";
        int i = template.indexOf("<" + tagName);
        i = template.indexOf(">", i);
        int j = template.indexOf("</" + tagName + ">", i);
        if (i>-1 && j>-1) {
            output = template.substring(i+1, j);
        }
        return output;
    }

    // Rules to replace content in inline editor
    // Covers specific cases as image extraction or cleaning whitespaces or newlines
    public String replace(String target, String oldData, String newData) {
        if (target == null || oldData == null || newData == null) return null;

        if (oldData.indexOf("class=\"wcm-skip\"") != -1) {
            oldData = oldData.replaceAll("class=\"wcm-skip\"", "");
        } else if (oldData.indexOf("wcm-skip") != -1) {
            oldData = oldData.replaceAll("wcm-skip", "");
        }

        if (newData.indexOf("class=\"wcm-skip\"") != -1) {
            newData = newData.replaceAll("class=\"wcm-skip\"", "");
        } else if (newData.indexOf("wcm-skip") != -1) {
            newData = newData.replaceAll("wcm-skip", "");
        }

        // Specific treatment for images
        if (oldData.startsWith("<img") && newData.startsWith("<img")) {
            int i = oldData.indexOf("src=");
            int j = oldData.indexOf(" ", i);
            String oldSrc = oldData.substring(i, j);
            i = newData.indexOf("src=");
            j = newData.indexOf(" ", j);
            String newSrc = newData.substring(i, j);
            oldData = oldSrc;
            newData = newSrc;
        }

        target = target.replace(oldData, newData);
        return target;
    }

    public String replace(String newData) {
        if (newData == null) return null;

        if (newData.indexOf("class=\"wcm-skip\"") != -1) {
            newData = newData.replaceAll("class=\"wcm-skip\"", "");
        } else if (newData.indexOf("wcm-skip") != -1) {
            newData = newData.replaceAll("wcm-skip", "");
        }
        return newData;
    }

    // Replace aux method for StringBuilder arguments
    public void replaceAll(StringBuilder target, String oldData, String newData) {
        if (target == null || oldData == null || newData == null) return;
        int i = target.indexOf(oldData);
        while (i > -1) {
            target.replace(i, i + oldData.length(), newData);
            i = target.indexOf(oldData);
        }
    }

    public void replaceAll(StringBuilder target, StringBuilder oldData, StringBuilder newData) {
        if (target == null || oldData == null || newData == null) return;
        String strOldData = oldData.toString();
        int i = target.indexOf(strOldData);
        while (i > -1) {
            target.replace(i, i + oldData.length(), newData.toString());
            i = target.indexOf(strOldData);
        }
    }

    public void replaceAll(StringBuilder target, StringBuilder oldData, String newData) {
        if (target == null || oldData == null || newData == null) return;
        String strOldData = oldData.toString();
        int i = target.indexOf(strOldData);
        while (i > -1) {
            target.replace(i, i + oldData.length(), newData);
            i = target.indexOf(strOldData);
        }
    }

    public String notVisibleImg(String img) {
        if (img.indexOf("class") > -1) {
            img = img.replaceAll("class=[\"''](.*)[\"']", "class=\"$1 wcm-skip\"");
        } else {
            img = "<img class=\"wcm-skip\" " + img.trim().substring(4);
        }
        return img;
    }

    private Set<Category> categoryFilter(Set<Category> categories, Character type) {
        if (categories == null) return null;
        if (type == null) return categories;
        Set<Category> filtered = new HashSet<Category>();
        for (Category c: categories) {
            if (c.getType() == type) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    private String substringWord(String html, int index) {
        if (html == null || html.length() == 0) return html;
        if (index > html.length()) return html;
        int i = html.indexOf(" ", index);
        if (i == -1) i = index - 1;
        return html.substring(0, i);
    }

}
