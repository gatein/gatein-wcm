package org.gatein.wcm.api.services;

import org.gatein.wcm.api.model.security.User;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;

public interface SecurityService {

    public User authenticate(String idUser, String password) throws ContentSecurityException;

    public boolean hasRole(User user, String role) throws ContentSecurityException;

}
