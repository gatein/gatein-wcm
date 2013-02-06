package org.gatein.wcm.api.content;

public class ContentFactory {

	public static ContentAPI getContent() {
		return new JcrContentImpl();
	}

}
