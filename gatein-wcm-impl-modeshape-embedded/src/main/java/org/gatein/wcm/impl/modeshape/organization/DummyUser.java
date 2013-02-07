package org.gatein.wcm.impl.modeshape.organization;

import java.util.Date;

import org.exoplatform.services.organization.User;

public class DummyUser implements User {

	String userName;
	String password;
	String firstName;
	String lastName;
	String fullName;
	String email;
	Date createdDate;
	Date lastLoginTime;
	String displayName;
	String organizationId;
	
	@Override
	public String getUserName() {
		return this.userName;
	}

	@Override
	public void setUserName(String s) {
		this.userName = s;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String s) {
		this.password = s;
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public void setFirstName(String s) {
		this.firstName = s;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public void setLastName(String s) {
		this.lastName = s;
	}

	@Override
	public String getFullName() {
		return this.fullName;
	}

	@Override
	public void setFullName(String s) {
		this.fullName = s;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public void setEmail(String s) {
		this.email = s;
	}

	@Override
	public Date getCreatedDate() {
		return this.getCreatedDate();
	}

	@Override
	public void setCreatedDate(Date t) {
		this.createdDate = t;
	}

	@Override
	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}

	@Override
	public void setLastLoginTime(Date t) {
		this.lastLoginTime = t;
	}

	@Override
	public String getDisplayName() {		
		return this.displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getOrganizationId() {
		return this.organizationId;
	}

	@Override
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	// Tests users factories
	public User getTestUser1() {
		return null;
	}
	
	
	
}
