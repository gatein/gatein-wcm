package org.gatein.wcm.api.model.publishing;

import java.util.List;

import org.gatein.wcm.api.model.security.Principal;

public class PublishPoint {
	
	Integer order;
	String description;
	PublishPoint next;
	PublishPoint back;
	
	List<Principal> granted;
	
}
