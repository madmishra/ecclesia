package com.indigo.LDAPS;


import java.util.Hashtable;
import java.util.Map;

import javax.naming.AuthenticationException;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.util.YFSAuthenticator;
import com.yantra.yfs.japi.util.YFSUseInternalAuthException;

public class LDAPAuthenticator implements YFSAuthenticator {
	private static final String ERROR_CODE = "ErrorCode";
    private static final  String ERRORS= "Errors";
    private static final String ERROR_DESC= "ErrorDescription";
    private static final  String ERROR= "Error";


	public Map<String, String> authenticate(String sLoginID, String sPassword) throws Exception {

		if (isVoid(sLoginID)) {
			YFCDocument errorDoc = YFCDocument.createDocument(ERRORS);
			YFCElement eleErrors = errorDoc.getDocumentElement();
			YFCElement eleError = errorDoc.createElement(ERROR);
			eleError.setAttribute(ERROR_CODE, ERROR);
			eleError.setAttribute(ERROR_DESC, "LoginId is mandatory");
			eleErrors.appendChild(eleError);
			throw new YFSException(errorDoc.toString());
		}

		if (isVoid(sPassword)) {
			YFCDocument errorDoc = YFCDocument.createDocument(ERRORS);
			YFCElement eleErrors = errorDoc.getDocumentElement();
			YFCElement eleError = errorDoc.createElement(ERROR);
			eleError.setAttribute(ERROR_CODE, ERROR);
			eleError.setAttribute(ERROR_DESC, "Password is mandatory");
			eleErrors.appendChild(eleError);
			throw new YFSException(errorDoc.toString());
		}

		
			INDGUserDetailsStruct userDetailStruct = new INDGUserDetailsStruct();

			userDetailStruct.setUserId(sLoginID);
			userDetailStruct.setPassword(sPassword);
			getDirectoryContextExternal(userDetailStruct);


		return null;

	}

	private void getDirectoryContextExternal(INDGUserDetailsStruct userDetailStruct) throws Exception {
		String sLoginID = userDetailStruct.getUserId();
		String sPassword = userDetailStruct.getPassword();
		
		try {
			userDetailStruct.setLdapFactory(YFSSystem.getProperty("yfs.security.ldap.factory"));
			userDetailStruct.setLdapURL1(YFSSystem.getProperty("ldap.url.netti1"));
			userDetailStruct.setLdapDN(YFSSystem.getProperty("ldap.dn.netti"));

            String strCredentials = "CN="+sLoginID+","+userDetailStruct.getLdapDN();
			
            Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, userDetailStruct.getLdapFactory());
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, strCredentials);
			env.put(Context.SECURITY_CREDENTIALS, sPassword);
			env.put(Context.PROVIDER_URL, userDetailStruct.getLdapURL1());
			
			DirContext ctx = new InitialLdapContext(env, null);
			userDetailStruct.setDirectoryContext(ctx);
		
			if (!getUserSAMAccountName(userDetailStruct, userDetailStruct.getDirectoryContext()).equals("Y"))
			{
				userDetailStruct.getDirectoryContext().close();
				YFCDocument errorDoc = YFCDocument.createDocument(ERRORS);
				YFCElement eleErrors = errorDoc.getDocumentElement();
				YFCElement eleError = errorDoc.createElement(ERROR);
				eleError.setAttribute(ERROR_CODE, ERROR);
				eleError.setAttribute(ERROR_DESC, "User doesn't belongs to the specified group");
				eleErrors.appendChild(eleError);
				throw new YFSException(errorDoc.toString());
			}
			
			

		} catch (AuthenticationException aex) {

			String strErrorMessage = aex.getMessage();
			String strLdapErrorCode = strErrorMessage.substring(strErrorMessage.indexOf("data") + 5, strErrorMessage.indexOf("data") + 8);

			if (strLdapErrorCode.equals("532") || strLdapErrorCode.equals("773")) {
				YFCDocument errorDoc = YFCDocument.createDocument(ERRORS);
				YFCElement eleErrors = errorDoc.getDocumentElement();
				YFCElement eleError = errorDoc.createElement(ERROR);
				eleError.setAttribute(ERROR_CODE, ERROR);
				eleError.setAttribute(ERROR_DESC, "Password Expired, Please reset!");
				eleErrors.appendChild(eleError);
				throw new YFSException(errorDoc.toString());
			} else {
				throw new YFSUseInternalAuthException();
			}

		} 
		
	}

	private String getUserSAMAccountName(INDGUserDetailsStruct userDetailStruct, DirContext ctx) {
		String sIsBelongsToGroup = "N";
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String[] attrIDs = { "sAMAccountName", "sn", "givenname", "mail", "distinguishedName", "memberOf", "c" };
			constraints.setReturningAttributes(attrIDs);
			NamingEnumeration<SearchResult> answer = ctx.search(userDetailStruct.getLdapCN(), "userPrincipalName=" + userDetailStruct.getUserId(), constraints);
			if (answer.hasMore()) {
				Attributes attrs = ((SearchResult) answer.next()).getAttributes();
				userDetailStruct.setUserSAMAccountName(attrs.get("sAMAccountName").get().toString());
				userDetailStruct.setUserName(attrs.get("givenname").get().toString() + " " + attrs.get("sn").get().toString());
				userDetailStruct.setEmail(attrs.get("mail").get().toString());
				userDetailStruct.setUserDN(attrs.get("distinguishedName").get().toString());

				if (attrs.get("c") != null) {
					userDetailStruct.setCountry(attrs.get("c").get().toString());
				}

				for (int i = 0; i < attrs.get("memberOf").size(); i++) {
					String groupName = attrs.get("memberOf").get(i).toString();
					if (groupName.startsWith("CN=Sterling")) {
						sIsBelongsToGroup = "Y";
						
						userDetailStruct.addADGroup(groupName.substring(4, 20));
						
					}
					
				}
			} else {
				YFCDocument errorDoc = YFCDocument.createDocument(ERRORS);
				YFCElement eleErrors = errorDoc.getDocumentElement();
				YFCElement eleError = errorDoc.createElement(ERROR);
				eleError.setAttribute(ERROR_CODE, ERROR);
				eleError.setAttribute(ERROR_DESC, "User account not found in external AD");
				eleErrors.appendChild(eleError);
				throw new YFSException(errorDoc.toString());
				
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sIsBelongsToGroup;
	}

	

	 private boolean isVoid(String strVal) {
	      return (strVal == null || "".equals(strVal.trim()));
	}
}
