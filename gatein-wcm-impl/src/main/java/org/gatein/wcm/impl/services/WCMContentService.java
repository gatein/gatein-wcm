package org.gatein.wcm.impl.services;

import java.io.InputStream;
import java.util.List;

import javax.jcr.Session;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.model.security.ACE.PermissionType;
import org.gatein.wcm.api.model.security.Principal;
import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.gatein.wcm.api.services.exceptions.PublishException;

public class WCMContentService implements ContentService {

    Session jcrSession = null;
    User logged = null;

    public WCMContentService (Session session, User user) {

    }

    @Override
    public Content createTextContent(String id, String locale, String location, String html, String encoding)
            throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createFolder(String id, String locale, String location) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createBinaryContent(String id, String locale, String location, String contentType, Long size,
            String fileName, InputStream content) throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> getContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getContentLocales(String location) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content updateTextContent(String location, String locale, String html, String encoding) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content updateFolder(String location, String locale, String newLocation) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content updateBinaryContent(String location, String locale, String contentType, Long size, String fileName,
            InputStream content) throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String deleteContent(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category createCategory(String id, String locale, String description, String categoryLocation)
            throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category updateCategory(String categoryLocation, String locale, String description) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category updateCategory(String categoryLocation, String newLocation) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content addContentCategory(String location, Category category) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category deleteCategory(String categoryLocation) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> getContent(List<Category> categories, String location, String locale) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createContentComment(String location, String locale, String comment) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content deleteContentComment(String location, String locale, String idComment) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createContentProperty(String location, String name, String value) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content updateContentProperty(String location, String name, String value) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content deleteContentProperty(String location, String name) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content createContentACE(String location, Principal principal, PermissionType permission) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content deleteContentACE(String location, Principal principal) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getContentVersions(String location, String locale) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> checkOut(String location, String locale, Integer version) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content checkIn(Content content) throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> getContent(String location, String locale, Integer version) throws ContentException,
            ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String deleteContent(String location, String locale, Integer version) throws ContentException, ContentIOException,
            ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String deleteContet(String location) throws ContentException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content submitPublish(Content content) throws PublishException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content submitDraft(Content content) throws PublishException, ContentIOException, ContentSecurityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void closeSession() throws ContentException, ContentIOException {
        // TODO Auto-generated method stub

    }

}
