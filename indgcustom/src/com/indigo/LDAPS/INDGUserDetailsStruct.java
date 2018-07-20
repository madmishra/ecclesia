package com.indigo.LDAPS
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;

public class POSUserDetailsStruct {
	
	 String userId="";
	 String passwrd="";
	 boolean isExternalUser=false;
	 String userName="";
	 String email="";
	 String distinguishedName="";
	 String userSAMAccountName="";
	 List<String> groupList = new ArrayList<>();
	 List<String> aDgroupList = new ArrayList<>();
	 String userNode="";
	 String userEnterprise="";
	 String environment="";
	 String ldapCN="";
	 String ldapDN="";
	 String userDN="";
	 String userIPAddress="";
	 DirContext directoryContext=null;
	 String ldapFactory="";
	 String ldapURL1="";
	 String ldapURL2="";
	 String groupDN="";
	 String country="";

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getGroupDN() {
		return groupDN;
	}
	public void setGroupDN(String groupDN) {
		this.groupDN = groupDN;
	}
	public String getLdapURL1() {
		return ldapURL1;
	}
	public void setLdapURL1(String ldapURL1) {
		this.ldapURL1 = ldapURL1;
	}
	public String getLdapURL2() {
		return ldapURL2;
	}
	public void setLdapURL2(String ldapURL2) {
		this.ldapURL2 = ldapURL2;
	}
	public String getLdapFactory() {
		return ldapFactory;
	}
	public void setLdapFactory(String ldapFactory) {
		this.ldapFactory = ldapFactory;
	}
	public DirContext getDirectoryContext() {
		return directoryContext;
	}
	public void setDirectoryContext(DirContext directoryContext) {
		this.directoryContext = directoryContext;
	}
	public String getUserNode() {
		return userNode;
	}
	public void setUserNode(String userNode) {
		this.userNode = userNode;
	}
	public String getUserEnterprise() {
		return userEnterprise;
	}
	public void setUserEnterprise(String userEnterprise) {
		this.userEnterprise = userEnterprise;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getLdapCN() {
		return ldapCN;
	}
	public void setLdapCN(String ldapCN) {
		this.ldapCN = ldapCN;
	}
	public String getLdapDN() {
		return ldapDN;
	}
	public void setLdapDN(String ldapDN) {
		this.ldapDN = ldapDN;
	}
	public String getUserDN() {
		return userDN;
	}
	public void setUserDN(String userDN) {
		this.userDN = userDN;
	}
	public String getUserIPAddress() {
		return userIPAddress;
	}
	public void setUserIPAddress(String userIPAddress) {
		this.userIPAddress = userIPAddress;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return passwrd;
	}
	public void setPassword(String passwrd) {
		this.passwrd = passwrd;
	}
	public boolean isExternalUser() {
		return isExternalUser;
	}
	public void setExternalUser(boolean isExternalUser) {
		this.isExternalUser = isExternalUser;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDistinguishedName() {
		return distinguishedName;
	}
	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}
	public String getUserSAMAccountName() {
		return userSAMAccountName;
	}
	public void setUserSAMAccountName(String userSAMAccountName) {
		this.userSAMAccountName = userSAMAccountName;
	}
	public List<String> getGroupList() {
		return groupList;
	}
	public void setGroup(List<String> groupList) {
		this.groupList = groupList;
	}
	public void addGroup(String strGroup) {
		getGroupList().add(strGroup);
	}
	
	public List<String> getADGroupList() {
		return aDgroupList;
	}
	public void setADGroup(List<String> aDgroupList) {
		this.aDgroupList = aDgroupList;
	}
	public void addADGroup(String aDstrGroup) {
		getADGroupList().add(aDstrGroup);
	}

}
