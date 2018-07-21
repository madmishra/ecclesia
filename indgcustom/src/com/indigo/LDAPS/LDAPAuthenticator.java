package com.indigo.LDAPS;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
// import com.bridge.sterling.utils.LoggerUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.util.YFSAuthenticator;
import com.yantra.yfs.japi.util.YFSUseInternalAuthException;

public class LDAPAuthenticator implements YFSAuthenticator{
//    private static YFCLogCategory logger = YFCLogCategory.instance(POSAuthenticator.class);
    
	public static void main(String arg[]) throws Exception {
		LDAPAuthenticator obj = new LDAPAuthenticator();
		obj.authenticate("admin", "password");
	}
	
    public Map<String, String> authenticate(String sLoginID, String sPassword) throws Exception {
//      LoggerUtil.startComponentLog(logger, this.getClass().getName(),
//				"authenticate","");
    	
//    	LoggerUtil.verboseLog("Authentication user",logger,sLoginID);    	
    	
    	if(isVoid(sLoginID)) {
			YFCDocument errorDoc = YFCDocument.createDocument("Errors");
	        YFCElement eleErrors = errorDoc.getDocumentElement();
	        YFCElement eleError = errorDoc.createElement("Error");
	        eleError.setAttribute("ErrorCode", "Error");
	        eleError.setAttribute("ErrorDescription", "LoginId is mandatory");
	        eleErrors.appendChild(eleError);
	        throw new YFSException(errorDoc.toString());
    	}
    	
    	if(isVoid(sPassword)) {
			YFCDocument errorDoc = YFCDocument.createDocument("Errors");
	        YFCElement eleErrors = errorDoc.getDocumentElement();
	        YFCElement eleError = errorDoc.createElement("Error");
	        eleError.setAttribute("ErrorCode", "Error");
	        eleError.setAttribute("ErrorDescription", "Password is mandatory");
	        eleErrors.appendChild(eleError);
	        throw new YFSException(errorDoc.toString());
    	}
    	
    	
    	if(sLoginID.equalsIgnoreCase("admin")) {
//    		LoggerUtil.verboseLog("Go for Sterling authentication", logger,"");
			throw new YFSUseInternalAuthException();
    	}
    	
    	
    	INDGUserDetailsStruct userDetailStruct = new INDGUserDetailsStruct();
    	
    	//get current environment example - LDAPAuthenticator, UAT, PROD
    	userDetailStruct.setEnvironment(YFSSystem.getProperty("ldap.env"));
    	userDetailStruct.setUserId(sLoginID);
    	userDetailStruct.setPassword(sPassword);
    	
		getDirectoryContextInternal(userDetailStruct);
       		
        return null;
    }
    
    private void getDirectoryContextInternal(INDGUserDetailsStruct userDetailStruct) throws Exception{
//    	LoggerUtil.startComponentLog(logger, this.getClass().getName(),"getDirectoryContextInternal", "");
    	String sLoginID = userDetailStruct.getUserId();
    	String sPassword = userDetailStruct.getPassword();
        try{
        	
        	userDetailStruct.setLdapFactory(YFSSystem.getProperty("yfs.security.ldap.factory"));
        	userDetailStruct.setLdapURL1(YFSSystem.getProperty("ldap.url.netti1"));
        	userDetailStruct.setLdapDN(YFSSystem.getProperty("ldap.dn.netti"));
        //	userDetailStruct.setLdapCN(YFSSystem.getProperty("ldap.cn.netti"));
        //	userDetailStruct.setGroupDN(YFSSystem.getProperty("ldap.group.dn.netti"));

       		String strCredentials =  sLoginID+"@"+userDetailStruct.getLdapDN();
        	System.out.println("LDAP URL " + userDetailStruct.getLdapURL1());
        	System.out.println("LDAP Credentials " + strCredentials);
        	System.out.println("LDAP factory " + userDetailStruct.getLdapFactory());
        	
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, userDetailStruct.getLdapFactory());
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, strCredentials);
            env.put(Context.SECURITY_CREDENTIALS, sPassword);
            env.put(Context.PROVIDER_URL, userDetailStruct.getLdapURL1());
            
            
            DirContext ctx = new InitialLdapContext(env, null);
            userDetailStruct.setDirectoryContext(ctx);
            System.out.println("Connected to Internal AD1 "+userDetailStruct.getLdapURL1());
            
            
        }catch (AuthenticationException aex){
        	System.out.println("Authentication exception for user " + sLoginID);
			
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
		catch (CommunicationException nex){
			System.out.println("Connection error while connecting to AD1 will try AD2 now "+ userDetailStruct.getLdapURL1());
			
			}
        finally {
        	System.out.println(this.getClass().getName() + "getDirectoryContextInternal" + "");
        	}
		}
    	
    private boolean isVoid(String strVal) {
        if (strVal == null || "".equals(strVal.trim())) {
            return true;
        }
        return false;
    }
}
