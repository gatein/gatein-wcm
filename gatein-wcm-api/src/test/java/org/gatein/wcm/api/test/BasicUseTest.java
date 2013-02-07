package org.gatein.wcm.api.test;

import org.exoplatform.services.organization.User;
import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
import org.gatein.wcm.api.services.exceptions.ContentException;
import org.gatein.wcm.api.services.exceptions.ContentIOException;
import org.gatein.wcm.api.services.exceptions.ContentSecurityException;
import org.junit.Assert;
import org.junit.Test;

public class BasicUseTest {
	
	@SuppressWarnings({ "null", "unused" })
	@Test
	public void basic_api_use_example() {

		System.out.println("Basic test example");
			
		User user = null;		
		RepositoryService repository = null;
		
		try {
			
			// RepositoryService should be instantiated from a factory or server service.
			
			// Creating a session
			ContentService contentService = repository.createContentSession("website1", user);
			
			// Creating content			
			String html = "<div><h1>Main Content</h1></div>";						
			
			Content main_content = contentService.createTextContent("main", "es", "/", html, "UTF8");
			
			// Closing a session
			contentService.closeSession();
						
		
		} catch (ContentException e) {
			
		} catch (ContentIOException e) {
			
		} catch (ContentSecurityException e) {
			
		} catch (NullPointerException e) {
			
		} 
		
		Assert.assertTrue(true);
	}
	
}
