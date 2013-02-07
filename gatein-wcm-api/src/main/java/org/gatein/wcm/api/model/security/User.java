package org.gatein.wcm.api.model.security;

import java.util.Date;

public interface User {

	   /**
	    * This method should return the username of the user. The username should be
	    * unique and the user database should not have 2 user record with the same
	    * username
	    * 
	    * @return
	    */
	   public String getUserName();
	   
	   /**
	    * @return This method return the password of the user account
	    */
	   public String getPassword();

	   /**
	    * This method is used to change the user account password.
	    * 
	    * @param s
	    */
	   public void setPassword(String s);
	   
	   /**
	    * @return This method return the first name of the user
	    */
	   public String getFirstName();

	   /**
	    * @param s the new first name
	    */
	   public void setFirstName(String s);

	   /**
	    * @return The last name of the user
	    */
	   public String getLastName();

	   /**
	    * @param s The new last name of the user
	    */
	   public void setLastName(String s);	   
	   
	   /**
	    * @return The email address of the user
	    */
	   public String getEmail();

	   /**
	    * @param s The new user email address
	    */
	   public void setEmail(String s);

	   /**
	    * @return The date that the user register or create the account
	    */
	   public Date getCreatedDate();	   

	   /**
	    * @return Return the last time that the user access the account
	    */
	   public Date getLastLoginTime();	   
	   
	   /**
	    * @return return the display name
	    */
	   public String getDisplayName();

	   /**
	    * @param displayName The name that should show in the display name
	    */
	   public void setDisplayName(String displayName);	   

	   /**
	    * @return the id of organization the user belongs to or null if not
	    *         applicable
	    */
	   String getOrganizationId();

	   /**
	    * sets the prganizationId
	    */
	   void setOrganizationId(String organizationId);	   
	   
}
