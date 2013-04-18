package org.gatein.wcm.api.services;

import org.gatein.wcm.api.model.security.WcmUser;
import org.gatein.wcm.api.services.exceptions.WcmContentSecurityException;

public interface WcmSecurityService {

    WcmUser authenticate(String idUser, String password) throws WcmContentSecurityException;

    boolean hasRole(WcmUser user, String role) throws WcmContentSecurityException;

}
