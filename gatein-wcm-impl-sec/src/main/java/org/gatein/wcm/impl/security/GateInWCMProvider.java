package org.gatein.wcm.impl.security;

import java.util.Locale;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.modeshape.common.i18n.I18nResource;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.security.AuthenticationProvider;
import org.modeshape.jcr.security.SecurityContext;

public class GateInWCMProvider implements AuthenticationProvider {

    private static final Logger LOGGER = Logger.getLogger(GateInWCMProvider.class);
	
	public ExecutionContext authenticate(Credentials credentials, String repositoryName,
			String workspaceName, ExecutionContext repositoryContext, Map<String, Object> sessionAttributes) {
		
		try {		
			if (credentials instanceof SimpleCredentials) {
				SimpleCredentials sCredentials = (SimpleCredentials)credentials;
				return repositoryContext.with( new GateInSecurityContext( sCredentials ) );
			}				
		
		} catch (LoginException e) {
			LOGGER.warn( new GateInWCMProvider.LogMsg( e.toString() ), e.toString() );
			return null;
		}
		return null;
	}


	protected static class GateInSecurityContext implements SecurityContext {

		SimpleCredentials sCredentials = null;
		
		protected GateInSecurityContext( Credentials credentials ) 
			throws LoginException
		{			
			// TODO Expecting SimpleCredentials
			sCredentials = (SimpleCredentials)credentials;
			LOGGER.info(new GateInWCMProvider.LogMsg("Getting security credentials "), sCredentials.getUserID());	
			
			if (!sCredentials.getUserID().equals( new String( sCredentials.getPassword() ) )) {
				throw new LoginException("GateInSecurityContext: user should be equals as password !! ");
			}
		}
		
		public String getUserName() {			
			return ( sCredentials != null ? sCredentials.getUserID() : null );
		}

		public boolean hasRole(String arg0) {
			
			// TODO Hardcoding for testing
			// Only roles:
			// admin or guest
			if (arg0 == null) return false;
			if (sCredentials == null) return false;
			if ("admin".equals( arg0 )) return true;
			if ("guest".equals( arg0 )) return true;
			
			return false;			
		}

		public boolean isAnonymous() {
			return false;
		}

		public void logout() {
			sCredentials = null;			
		}
		
	}
	
	public static class LogMsg implements I18nResource {

		String msg;
		
		public LogMsg(String msg) {
			this.msg = msg;
		}
		
		public String text(Object... arguments) {
			return msg;
		}

		public String text(Locale locale, Object... arguments) {
			return msg;
		}
		
	}
	
	
}
