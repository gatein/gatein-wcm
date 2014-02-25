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
import org.gatein.wcm.portlet.util.ParseDates;

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

    /*
        <wcm-list> / <wcm-param-list> tag processing
     */
    public String tagWcmList(String tagWcmList, String initialTemplate, List<Post> listPosts, Map<String, String> params, UserWcm userWcm) {
        String processedTemplate = "";
        String tag = extractTag(tagWcmList, initialTemplate);
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
            String outputList = "<";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            if (properties.containsKey("id")) {
                outputList += " id=\"" + properties.get("id") + "\"";
            }
            if (properties.containsKey("class")) {
                outputList += " class=\"" + properties.get("class") + "\"";
            }

            outputList += " >";
            if (listPosts != null) {
                for (int i = from; i < to; i++) {
                    Post p = listPosts.get(i);
                    if (size > 1) outputList += "<li>";
                    outputList += combine(inside, p, i, false, userWcm);
                    if (size > 1) outputList += "</li>";
                }
            }
            outputList += "</";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            outputList += ">";
            processedTemplate = initialTemplate.replace(tag, outputList);
        } else if (tag != null && listPosts == null) {
            processedTemplate = initialTemplate.replace(tag, "<div></div>");
        }
        return processedTemplate;
    }

    /*
        <wcm-file-list> tag processing
     */
    public String tagWcmFileList(String tagWcmFileList, String initialTemplate, List<Upload> listUploads, Map<String, String> params, UserWcm userWcm) {
        String processedTemplate = "";
        String tag = extractTag(tagWcmFileList, initialTemplate);
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
            String outputList = "<";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            if (properties.containsKey("id")) {
                outputList += " id=\"" + properties.get("id") + "\"";
            }
            if (properties.containsKey("class")) {
                outputList += " class=\"" + properties.get("class") + "\"";
            }

            outputList += " >";
            if (listUploads != null) {
                for (int i = from; i < to; i++) {
                    Upload u = listUploads.get(i);
                    if (size > 1) outputList += "<li>";
                    outputList += combineUpload(inside, u, i);
                    if (size > 1) outputList += "</li>";
                }
            }
            outputList += "</";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            outputList += ">";
            processedTemplate = initialTemplate.replace(tag, outputList);
        } else if (tag != null && listUploads == null) {
            processedTemplate = initialTemplate.replace(tag, "<div></div>");
        }
        return processedTemplate;
    }

    /*
        <wcm-cat-list> tag processing
     */
    public String tagWcmCatList(String tagWcmList, String initialTemplate, List<Category> listCategories, Map<String, String> params) {
        String processedTemplate = "";
        String tag = extractTag(tagWcmList, initialTemplate);
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
            String outputList = "<";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            if (properties.containsKey("id")) {
                outputList += " id=\"" + properties.get("id") + "\"";
            }
            if (properties.containsKey("class")) {
                outputList += " class=\"" + properties.get("class") + "\"";
            }

            outputList += " >";
            if (listCategories != null) {
                for (int i = from; i < to; i++) {
                    Category c = listCategories.get(i);
                    if (size > 1) outputList += "<li>";
                    outputList += combineCategory(inside, c, i);
                    if (size > 1) outputList += "</li>";
                }
            }
            outputList += "</";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            outputList += ">";
            processedTemplate = initialTemplate.replace(tag, outputList);
        } else if (tag != null && listCategories == null) {
            processedTemplate = initialTemplate.replace(tag, "<div></div>");
        }
        return processedTemplate;
    }

    /*
        <wcm-single> / <wcm-param-single> tag processing
     */
    public String tagWcmSingle(String tagWcmSingle, String initialTemplate, Post post, Map<String, String> params, boolean canWrite, UserWcm userWcm) {
        String processedTemplate = null;
        String tag = extractTag(tagWcmSingle, initialTemplate);
        this.urlParams = params;

        if (tag != null) {
            String inside = insideTag(tagWcmSingle, tag);
            String outputSingle = combine(inside, post, 0, canWrite, userWcm);
            processedTemplate = initialTemplate.replace(tag, outputSingle);
        } else {
            processedTemplate = initialTemplate.replace(tag, "<div></div>");
        }
        return processedTemplate;
    }

    /*
        <wcm-param-name> tag processing
     */
    public String tagWcmParamName(String tagWcmParamName, String initialTemplate, Category cat) {
        String processedTemplate = initialTemplate;
        String tag = extractTag(tagWcmParamName, initialTemplate);

        if (tag != null) {
            String output = "";
            if (cat != null && cat.getName() != null) {
                output = cat.getName();
            }
            processedTemplate = initialTemplate.replace(tag, output);
        }

        return processedTemplate;
    }

    /*
        Combine in-line tags with Post object
     */
    public String combine(String template, Post post, int iteration, boolean canWrite, UserWcm userWcm) {
        if (post == null) return "";
        boolean foundTag = false;
        String output = template;
        while (!foundTag) {
            if (hasTag("wcm-categories", output)) {
                output = tagWcmCategories(output, post);
            } else if (hasTag("wcm-link", output)) {
                output = tagWcmLink(output, post);
            } else if (hasTag("wcm-img", output)) {
                output = tagWcmImg(output, post, canWrite);
            } else if (hasTag("wcm-title", output)) {
                output = tagWcmTitle(output, post, canWrite);
            } else if (hasTag("wcm-excerpt", output)) {
                output = tagWcmExcerpt(output, post, canWrite);
            } else if (hasTag("wcm-iter", output)) {
                output = tagWcmIter(output, iteration);
            } else if (hasTag("wcm-created", output)) {
                output = tagWcmCreated(output, post);
            } else if (hasTag("wcm-author", output)) {
                output = tagWcmAuthor(output, post);
            } else if (hasTag("wcm-content", output)) {
                output = tagWcmContent(output, post, canWrite);
            } else if (hasTag("wcm-comments", output)) {
                output = tagWcmComments(output, post);
            } else if (hasTag("wcm-form-comments", output)) {
                output = tagWcmFormComments(output, post, userWcm);
            } else {
                foundTag = true;
            }
        }
        return output;
    }

    /*
        Combine in-line tags with Upload object
     */
    public String combineUpload(String template, Upload upload, int iteration) {
        if (upload == null) return "";
        boolean foundTag = false;
        String output = template;
        while (!foundTag) {
            if (hasTag("wcm-link", output)) {
                output = tagWcmLink(output, upload);
            } else if (hasTag("wcm-filename", output)) {
                output = tagWcmFileName(output, upload);
            } else if (hasTag("wcm-iter", output)) {
                output = tagWcmIter(output, iteration);
            } else if (hasTag("wcm-created", output)) {
                output = tagWcmCreated(output, upload);
            } else if (hasTag("wcm-author", output)) {
                output = tagWcmAuthor(output, upload);
            } else if (hasTag("wcm-mimetype", output)) {
                output = tagWcmMimeType(output, upload);
            } else if (hasTag("wcm-description", output)) {
                output = tagWcmDescription(output, upload);
            } else {
                foundTag = true;
            }
        }
        return output;
    }

    /*
        Combine in-line tags with Category object
     */
    public String combineCategory(String template, Category category, int iteration) {
        if (category == null) return "";
        boolean foundTag = false;
        String output = template;
        while (!foundTag) {
            if (hasTag("wcm-link", output)) {
                output = tagWcmLink(output, category);
            } else if (hasTag("wcm-cat-name", output)) {
                output = tagWcmCatName(output, category);
            } else if (hasTag("wcm-iter", output)) {
                output = tagWcmIter(output, iteration);
            } else if (hasTag("wcm-cat-type", output)) {
                output = tagWcmCatType(output, category);
            } else {
                foundTag = true;
            }
        }
        return output;
    }

    /*
        <wcm-link> tag processing
     */
    public String tagWcmLink(String template, Post post) {
        String tag = extractTag("wcm-link", template);
        String inside = insideTag("wcm-link", template);
        Map<String, String> properties = propertiesTag(tag);

        String postUrl = Wcm.SUFFIX.POST + "/" + Wcm.SUFFIX.ID + "/" + post.getId();

        // By default wcm-link add a post id to href
        if (properties.containsKey("index") && properties.get("index").equals("disable")) {
            postUrl="";
        }
        String output = "<a";
        if (properties.containsKey("href")) {
            output += " href=\"" + properties.get("href") + (!"".equals(postUrl)?"/" + postUrl:"") + "\"";
        } else {
            String page = urlParams.get("page");
            if (urlParams.containsKey("post") || urlParams.containsKey("category")) {
                page = "../../../" + page;
            }
            output += " href=\"" + page + (!"".equals(postUrl)?"/" + postUrl:"") + "\"";
        }
        if (properties.containsKey("class")) {
            output += " class=\"" + properties.get("class") + "\"";
        }
        output += ">";
        output += inside;
        output += "</a>";
        return template.replace(tag, output);
    }

    public String tagWcmLink(String template, Category category) {
        String tag = extractTag("wcm-link", template);
        String inside = insideTag("wcm-link", template);
        Map<String, String> properties = propertiesTag(tag);

        String categoryUrl = Wcm.SUFFIX.CATEGORY + "/" + Wcm.SUFFIX.ID + "/" + category.getId();

        // By default wcm-link add a post id to href
        if (properties.containsKey("index") && properties.get("index").equals("disable")) {
            categoryUrl="";
        }
        String output = "<a";
        if (properties.containsKey("href")) {
            output += " href=\"" + properties.get("href") + (!"".equals(categoryUrl)?"/" + categoryUrl:"") + "\"";
        } else {
            String page = urlParams.get("page");
            if (urlParams.containsKey("post") || urlParams.containsKey("category")) {
                page = "../../../" + page;
            }
            output += " href=\"" + page + (!"".equals(categoryUrl)?"/" + categoryUrl:"") + "\"";
        }
        if (properties.containsKey("class")) {
            output += " class=\"" + properties.get("class") + "\"";
        }
        output += ">";
        output += inside;
        output += "</a>";
        return template.replace(tag, output);
    }

    public String tagWcmLink(String template, Upload upload) {
        String tag = extractTag("wcm-link", template);
        String inside = insideTag("wcm-link", template);
        Map<String, String> properties = propertiesTag(tag);

        String uploadUrl = "/wcm/rs/u/" + upload.getId();

        String output = "<a";
        if (properties.containsKey("target")) {
            output += " target=\"" + properties.get("target") + "\"";
        }
        if (properties.containsKey("class")) {
            output += " class=\"" + properties.get("class") + "\"";
        }
        output += " href=\"" + uploadUrl + "\" ";
        output += ">";
        output += inside;
        output += "</a>";
        return template.replace(tag, output);
    }


    /*
        <wcm-img> tag processing
     */
    public String tagWcmImg(String template, Post post, boolean canWrite) {
        String tag = extractTag("wcm-img", template);
        String inside = insideTag("wcm-img", template);
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
        return template.replace(tag, output);
    }

    /*
        <wcm-title> tag processing
     */
    public String tagWcmTitle(String template, Post post, boolean canWrite) {
        String tag = extractTag("wcm-title", template);
        String inside = insideTag("wcm-title", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < post.getTitle().length())
                output = substringWord(post.getTitle(), max) + " ...";
            else
                output = post.getTitle();
        } else {
            output = post.getTitle();
        }
        // Editing tags
        if (canWrite) {
            output = "<p contenteditable=\"true\" class=\"wcm-content-edit\" data-post-id=\"" + post.getId() + "\" data-post-attr=\"title\">" + output + "</p>";
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-excerpt> tag processing
     */
    public String tagWcmExcerpt(String template, Post post, boolean canWrite) {
        String tag = extractTag("wcm-excerpt", template);
        String inside = insideTag("wcm-excerpt", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < post.getExcerpt().length())
                output = substringWord(post.getExcerpt(), max) + " ...";
            else
                output = post.getExcerpt();
        } else {
            output = post.getExcerpt();
        }
        // Editing tags
        if (canWrite) {
            output = "<p contenteditable=\"true\" data-post-id=\"" + post.getId() + "\" data-post-attr=\"excerpt\">" + output + "</p>";
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-iter> tag processing
     */
    public String tagWcmIter(String template, int iteration) {
        String tag = extractTag("wcm-iter", template);
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
                return template.replace(tag, inside);
            }
        } else if (properties.containsKey("par")) {
            if ("true".equals(properties.get("par")) && (i%2 == 0)) {
                return template.replace(tag, inside);
            }
        }
        return template.replace(tag, "");
    }

    /*
        <wcm-created> tag processing
     */
    public String tagWcmCreated(String template, Object object) {
        String tag = extractTag("wcm-created", template);
        String inside = insideTag("wcm-created", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("format")) {
            try {
                SimpleDateFormat custom = new SimpleDateFormat(properties.get("format"));
                if (object instanceof Post) {
                    output = custom.format(((Post)object).getCreated().getTime());
                } else if (object instanceof Upload) {
                    output = custom.format(((Upload)object).getCreated().getTime());
                }

            } catch (Exception e) {
                log.warning("Error parsing date with format " + properties.get("format"));
            }
            output = "";
        } else {
            if (object instanceof Post) {
                output = ParseDates.parse(((Post)object).getCreated());
            } else if (object instanceof Upload) {
                output = ParseDates.parse(((Upload)object).getCreated());
            }
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-author> tag processing
     */
    public String tagWcmAuthor(String template, Object object) {
        String tag = extractTag("wcm-author", template);
        String inside = insideTag("wcm-author", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (object instanceof Post) {
            output = ((Post)object).getAuthor();
        } else if (object instanceof Upload) {
            output = ((Upload)object).getUser();
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-content> tag processing
        skipImages is a list of indexes of images in the content.
        We can combine <wcm-content skipimages="0"> if we want to use <wcm-img index="0"> and I don't want to repeat the same image
     */
    public String tagWcmContent(String template, Post post, boolean canWrite) {
        String tag = extractTag("wcm-content", template);
        String inside = insideTag("wcm-content", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("skipimages")) {
            try {
                output = post.getContent();
                String[] skipImages = properties.get("skipimages").split(",");
                for (int i=0; i<skipImages.length; i++) {
                    int iImage = new Integer(skipImages[i]).intValue();
                    String image = extractImg(output, iImage, false);
                    String imageNotVisible = notVisibleImg(image);
                    output = output.replace(image, imageNotVisible);
                }
            } catch (Exception e) {
                log.warning("Error parsing content with skipImages " + properties.get("skipImages"));
            }
        } else {
            output = post.getContent();
        }
        // Editing tags
        if (canWrite) {
            output = "<div contenteditable=\"true\" data-post-id=\"" + post.getId() + "\" data-post-attr=\"content\">" + output + "</div>";
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-filename> tag processing
     */
    public String tagWcmFileName(String template, Upload upload) {
        String tag = extractTag("wcm-filename", template);
        String inside = insideTag("wcm-filename", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < upload.getFileName().length())
                output = substringWord(upload.getFileName(), max) + " ...";
            else
                output = upload.getFileName();
        } else {
            output = upload.getFileName();
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-mimetype> tag processing
     */
    public String tagWcmMimeType(String template, Upload upload) {
        String tag = extractTag("wcm-mimetype", template);
        String inside = insideTag("wcm-mimetype", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < upload.getMimeType().length())
                output = substringWord(upload.getMimeType(), max) + " ...";
            else
                output = upload.getMimeType();
        } else {
            output = upload.getMimeType();
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-description> tag processing
     */
    public String tagWcmDescription(String template, Upload upload) {
        String tag = extractTag("wcm-description", template);
        String inside = insideTag("wcm-description", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < upload.getDescription().length())
                output = substringWord(upload.getDescription(), max) + " ...";
            else
                output = upload.getDescription();
        } else {
            output = upload.getDescription();
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-cat-name> tag processing
     */
    public String tagWcmCatName(String template, Category category) {
        String tag = extractTag("wcm-cat-name", template);
        String inside = insideTag("wcm-cat-name", template);
        Map<String, String> properties = propertiesTag(tag);
        String output = "";
        if (properties.containsKey("max-length")) {
            int max = 100;
            try {
                max = new Integer(properties.get("max-length")).intValue();
            } catch (Exception e) {
                // Default value if exception happens
            }
            if (max < category.getName().length())
                output = substringWord(category.getName(), max) + " ...";
            else
                output = category.getName();
        } else {
            output = category.getName();
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-cat-type> tag processing
     */
    public String tagWcmCatType(String template, Category category) {
        String tag = extractTag("wcm-cat-type", template);
        String output = "";
        if (category.getType().equals(Wcm.CATEGORIES.FOLDER)) {
            output = "Folder";
        } else if (category.getType().equals(Wcm.CATEGORIES.CATEGORY)) {
            output = "Category";
        } else if (category.getType().equals(Wcm.CATEGORIES.TAG)) {
            output = "Tag";
        }
        return template.replace(tag, output);
    }

    /*
        <wcm-categories> tag processing
     */
    public String tagWcmCategories(String template, Post post) {
        String processedTemplate = "";
        String tag = extractTag("wcm-categories", template);
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
            String outputList = "<";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            if (properties.containsKey("id")) {
                outputList += " id=\"" + properties.get("id") + "\"";
            }
            if (properties.containsKey("class")) {
                outputList += " class=\"" + properties.get("class") + "\"";
            }

            outputList += " >";
            if (setCategories != null) {
                Category[] categories = setCategories.toArray(new Category[setCategories.size()]);
                for (int i = from; i < to; i++) {
                    Category c = categories[i];
                    if (size > 1) outputList += "<li>";
                    outputList += combineCategory(inside, c, i);
                    if (size > 1) outputList += "</li>";
                }
            }
            outputList += "</";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            outputList += ">";
            processedTemplate = template.replace(tag, outputList);
        } else if (tag != null && setCategories == null) {
            processedTemplate = template.replace(tag, "<div></div>");
        }
        return processedTemplate;
    }

    /*
        <wcm-comments> tag processing
     */
    public String tagWcmComments(String template, Post post) {
        String processedTemplate = "";
        String tag = extractTag("wcm-comments", template);
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
            String outputList = "<";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            if (properties.containsKey("id")) {
                outputList += " id=\"" + properties.get("id") + "\"";
            }
            if (properties.containsKey("class")) {
                outputList += " class=\"" + properties.get("class") + "\"";
            }

            outputList += " >";
            if (setComments != null) {
                Comment[] comments = setComments.toArray(new Comment[setComments.size()]);
                for (int i = from; i < to; i++) {
                    Comment c = comments[i];
                    if (c.getStatus().equals(Wcm.COMMENT.PUBLIC)) {
                        if (size > 1) outputList += "<li>";
                        outputList += combineComment(inside, c, i);
                        if (size > 1) outputList += "</li>";
                    }
                }
            }
            outputList += "</";
            if (size == 1) {
                outputList += "div";
            } else {
                outputList += "ul";
            }
            outputList += ">";
            processedTemplate = template.replace(tag, outputList);
        } else if (tag != null && setComments == null) {
            processedTemplate = template.replace(tag, "<div></div>");
        }
        return processedTemplate;
    }

    /*
        Combine in-line tags with Comment object
     */
    public String combineComment(String template, Comment comment, int iteration) {
        if (comment == null) return "";
        boolean foundTag = false;
        String output = template;
        while (!foundTag) {
            if (hasTag("wcm-comment-content", output)) {
                output = tagWcmCommentContent(output, comment);
            } else if (hasTag("wcm-comment-author", output)) {
                output = tagWcmCommentAuthor(output, comment);
            } else if (hasTag("wcm-iter", output)) {
                output = tagWcmIter(output, iteration);
            } else if (hasTag("wcm-comment-created", output)) {
                output = tagWcmCommentCreated(output, comment);
            } else {
                foundTag = true;
            }
        }
        return output;
    }

    /*
        <wcm-comment-content> tag processing
     */
    public String tagWcmCommentContent(String template, Comment comment) {
        String tag = extractTag("wcm-comment-content", template);
        String output = "";
        if (comment.getContent() != null)
            output = comment.getContent();
        return template.replace(tag, output);
    }

    /*
        <wcm-comment-created> tag processing
     */
    public String tagWcmCommentCreated(String template, Comment comment) {
        String tag = extractTag("wcm-comment-created", template);
        String output = "";
        if (comment.getCreated() != null)
            output = ParseDates.parse(comment.getCreated());
        return template.replace(tag, output);
    }

    /*
        <wcm-comment-author> tag processing
     */
    public String tagWcmCommentAuthor(String template, Comment comment) {
        String tag = extractTag("wcm-comment-author", template);
        String output = "";
        if (comment.getAuthor() != null)
            output = comment.getAuthor();
        return template.replace(tag, output);
    }

    /*
        <wcm-form-comments> tag processing
     */
    public String tagWcmFormComments(String template, Post post, UserWcm userWcm) {
        String tag = extractTag("wcm-form-comments", template);
        Map<String, String> properties = propertiesTag(tag);

        String type = properties.containsKey("type") ? properties.get("type") : null;
        if (type != null && type.equals("anonymous") && !post.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS)) {
            return template.replace(tag, "");
        }
        if (type != null && type.equals("logged") && !post.getCommentsStatus().equals(Wcm.COMMENTS.LOGGED)) {
            return template.replace(tag, "");
        }

        boolean noComments = post.getCommentsStatus().equals(Wcm.COMMENTS.NO_COMMENTS);
        boolean postAnonymous = post.getCommentsStatus().equals(Wcm.COMMENTS.ANONYMOUS);
        boolean userAnonymous = userWcm.getUsername().equals("anonymous");

        if (noComments) {
            return template.replace(tag, "");
        } else {
            if (!postAnonymous && userAnonymous) {
                return template.replace(tag, "");
            } else {
                String inside = insideTag("wcm-form-comments", tag);
                String output = combineCommentForm(inside, post, userWcm);
                return template.replace(tag, output);
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
        boolean userAnonymous = userWcm.getUsername().equals("anonymous");
        String output = template;
        while (!foundTag) {
            if (hasTag("wcm-form-content", output)) {
                output = tagWcmCommentsFormContent(output);
            } else if (postAnonymous && hasTag("wcm-form-author", output)) {
                output = tagWcmCommentsFormAuthor(output);
            } else if (postAnonymous && hasTag("wcm-form-email", output)) {
                output = tagWcmCommentsFormEmail(output);
            } else if (postAnonymous && hasTag("wcm-form-url", output)) {
                output = tagWcmCommentsFormUrl(output);
            } else if (hasTag("wcm-form-button",  output)) {
                output = tagWcmCommentsFormButton(output, post);
            } else {
                foundTag = true;
            }
        }
        return output;
    }

    /*
        <wcm-form-content> tag processing
     */
    public String tagWcmCommentsFormContent(String template) {
        String tag = extractTag("wcm-form-content", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        String output = "<textarea id=\"" + this.namespace + "-content\"" + inputClass + "></textarea>";
        return template.replace(tag, output);
    }

    /*
        <wcm-form-author> tag processing
     */
    public String tagWcmCommentsFormAuthor(String template) {
        String tag = extractTag("wcm-form-author", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        String output = "<input id=\"" + this.namespace + "-author\"" + inputClass + " />";
        return template.replace(tag, output);
    }

    /*
        <wcm-form-email> tag processing
     */
    public String tagWcmCommentsFormEmail(String template) {
        String tag = extractTag("wcm-form-email", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        String output = "<input id=\"" + this.namespace + "-email\"" + inputClass + " />";
        return template.replace(tag, output);
    }

    /*
        <wcm-form-url> tag processing
     */
    public String tagWcmCommentsFormUrl(String template) {
        String tag = extractTag("wcm-form-url", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        String output = "<input id=\"" + this.namespace + "-url\"" + inputClass + " />";
        return template.replace(tag, output);
    }

    /*
        <wcm-form-button> tag processing
     */
    public String tagWcmCommentsFormButton(String template, Post post) {
        String tag = extractTag("wcm-form-button", template);
        String inside = insideTag("wcm-form-button", template);
        Map<String, String> properties = propertiesTag(tag);
        String inputClass = "";
        if (properties.containsKey("class")) {
            inputClass = " class=\"" + properties.get("class") + "\"";
        }
        String output = "<a id=\"" + this.namespace + "-addComment\" " + inputClass + " href=\"javascript:;\" onclick=\"wcmAddComment('" + this.namespace + "', '" + post.getId() + "');\">" + inside + "</a>";
        return template.replace(tag, output);
    }

    /*
        Aux functions to manipulate tags
     */
    public boolean hasTag(String tag, String template) {
        if (template == null || template.equals("")) return false;
        return (template.indexOf("<" + tag) > -1);
    }

    public String extractTag(String tag, String template) {
        String output = "";
        int i = template.indexOf("<" + tag);
        if (i != -1) {
            int j = template.indexOf(">", i);
            // Check if we are in <tag /> or <tag></tag>
            if (j > 0 && template.charAt(j-1) == '/') {
                output = template.substring(i, j + 1);
            } else {
                j = template.indexOf("</" + tag + ">", i);
                if (j>-1) {
                    output = template.substring(i, j + ("</" + tag + ">").length());
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

    public Map<String, String> propertiesTag(String template) {
        Map<String, String> output = new HashMap<String, String>();
        if (template != null) {
            int start = template.indexOf(" ");
            int i = start;
            int j = 0;
            while (j != -1) {
                // Look property name
                j = template.indexOf("=", i);
                if (j != -1) {
                    String name = template.substring(i, j).trim();
                    i = template.indexOf("\"", j);
                    j = template.indexOf("\"", i+1);
                    String value = template.substring(i+1, j);
                    i = j+1;
                    output.put(name, value);
                }
            }
        }
        return output;
    }

    public String insideTag(String tag, String template) {
        String output = "";
        int i = template.indexOf("<" + tag);
        i = template.indexOf(">", i);
        int j = template.indexOf("</" + tag + ">", i);
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
        } else if (newData.indexOf("wcm-skip") != -1) {
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
        if (html == null || "".equals(html)) return html;
        if (index > html.length()) return html;
        int i = html.indexOf(" ", index);
        if (i == -1) i = index - 1;
        return html.substring(0, i);
    }

}
