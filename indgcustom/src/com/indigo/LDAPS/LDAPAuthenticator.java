package com.indigo.LDAPS;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.util.YFSAuthenticator;
import com.yantra.yfs.japi.util.YFSUseInternalAuthException;

public class LDAPAuthenticato implements YFSAuthenticator{
    private static String errorCode = "ErrorCode";
    private static String errors= "Errors";
    private static String errordesc= "ErrorDescription";
    private static String error= "Error";
    public Map<String, String> authenticate(String sLoginID, String sPassword) throws Exception {    	
    	if(isVoid(sLoginID)) {
			YFCDocument errorDoc = YFCDocument.createDocument(errors);
	        YFCElement eleErrors = errorDoc.getDocumentElement();
	        YFCElement eleError = errorDoc.createElement(error);
	        eleError.setAttribute(errorCode, error);
	        eleError.setAttribute(errordesc, "LoginId is mandatory");
	        eleErrors.appendChild(eleError);
	        throw new YFSException(errorDoc.toString());
    	}
    	
    	if(isVoid(sPassword)) {
			YFCDocument errorDoc = YFCDocument.createDocument(errors);
	        YFCElement eleErrors = errorDoc.getDocumentElement();
	        YFCElement eleError = errorDoc.createElement(error);
	        eleError.setAttribute(errorCode, error);
	        eleError.setAttribute(errordesc, "Password is mandatory");
	        eleErrors.appendChild(eleError);
	        throw new YFSException(errorDoc.toString());
    	}
    	
    	INDGUserDetailsStruct userDetailStruct = new INDGUserDetailsStruct();
    	
    	userDetailStruct.setEnvironment(YFSSystem.getProperty("ldap.env"));
    	userDetailStruct.setUserId(sLoginID);
    	userDetailStruct.setPassword(sPassword);
    	
		getDirectoryContextInternal(userDetailStruct);
       		
        return null;
    }
    
    private void getDirectoryContextInternal(INDGUserDetailsStruct userDetailStruct) throws Exception{
   	String sLoginID = userDetailStruct.getUserId();
    	String sPassword = userDetailStruct.getPassword();
        try{
        	
        	userDetailStruct.setLdapFactory(YFSSystem.getProperty("yfs.security.ldap.factory"));
        	userDetailStruct.setLdapURL1(YFSSystem.getProperty("ldap.url.netti1"));
        	userDetailStruct.setLdapDN(YFSSystem.getProperty("ldap.dn.netti"));

       		String strCredentials =  "CN="+sLoginID+","+userDetailStruct.getLdapDN();
        	
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, userDetailStruct.getLdapFactory());
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, strCredentials);
            env.put(Context.SECURITY_CREDENTIALS, sPassword);
            env.put(Context.PROVIDER_URL, userDetailStruct.getLdapURL1());
            
            
            DirContext ctx = new InitialLdapContext(env, null);
            userDetailStruct.setDirectoryContext(ctx);
            
        }catch (AuthenticationException aex){
			
			String strErrorMessage = aex.getMessage();
			String strLdapErrorCode = strErrorMessage.substring(strErrorMessage.indexOf("data")+5, strErrorMessage.indexOf("data")+8);
			
			if(strLdapErrorCode.equals("532") || strLdapErrorCode.equals("773")) {
				YFCDocument errorDoc = YFCDocument.createDocument("Errors");
		        YFCElement eleErrors = errorDoc.getDocumentElement();
		        YFCElement eleError = errorDoc.createElement("Error");
		        eleError.setAttribute("ErrorCode", "Error");
		        eleError.setAttribute("ErrorDescription", "Password Expired, please reset!");
		        eleErrors.appendChild(eleError);
		        throw new YFSException(errorDoc.toString());
			}
			else {
				throw new YFSUseInternalAuthException();
			}
				
		}
	
		}
    	
    private boolean isVoid(String strVal) {
      return (strVal == null || "".equals(strVal.trim()));
    }
}
