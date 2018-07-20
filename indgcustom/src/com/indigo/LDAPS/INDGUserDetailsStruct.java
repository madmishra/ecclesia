package com.indigo.LDAPS;


import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;

public class INDGUserDetailsStruct {
	
	public String userId="";
	public String password="";
	public boolean isExternalUser=false;
	public String userName="";
	public String email="";
	public String distinguishedName="";
	public String userSAMAccountName="";
	public List<String> groupList = new ArrayList<String>();
	public List<String> ADgroupList = new ArrayList<String>();
	public String userNode="";
	public String userEnterprise="";
	public String environment="";
	public String ldapCN="";
	public String ldapDN="";
	public String userDN="";
	public String userIPAddress="";
	public DirContext directoryContext=null;
	public String ldapFactory="";
	public String ldapURL1="";
	public String ldapURL2="";
	public String groupDN="";
	public String country="";

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
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
		return ADgroupList;
	}
	public void setADGroup(List<String> ADgroupList) {
		this.ADgroupList = ADgroupList;
	}
	public void addADGroup(String ADstrGroup) {
		getADGroupList().add(ADstrGroup);
	}

}
