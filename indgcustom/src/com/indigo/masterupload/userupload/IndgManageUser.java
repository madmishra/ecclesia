package com.indigo.masterupload.userupload;

import java.util.Collection;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.utils.IndgManageDeltaLoadUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

/**
 * 
 * @author BGS168
 * 
 * Custom API manages users to create, delete and modify users
 * based on the input XML document and the users list that consists
 * of users already existing in the system. The users will be dropped 
 * inside the queue and will go ahead to the respective APIs.
 *
 */

public class IndgManageUser extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final String CREATE = "Create";
	private static final String MODIFY = "Modify";
	private static final String INACTIVE = "InActive";
	private static final String USER_MANAGER_SERVER = "Indg_UserFeed_IntoQ";
	private static final String INACTIVATE_FLAG = "N";
	private static final String FLAG = "Y";
	
	/**	 
	 * This method is the invoke point of the service.
	 *
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml){
		
		 YFCDocument userListApiOp = getUserList();
		 Collection<String> extraUserList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(inXml, userListApiOp, 
					XMLLiterals.LOGIN_ID,XMLLiterals.USER);
		 Collection<String> inputUserList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(userListApiOp, inXml, 
					XMLLiterals.LOGIN_ID,XMLLiterals.USER);
		 changeStatusForExtraUser(extraUserList,userListApiOp);
		 createNewUserFromInputXml(inputUserList,inXml);
		 return inXml;
	}
	
	/**
	 * This method forms the Input XML for getUserListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument formInputXmlForGetUserList() {
	    YFCDocument getUserListDoc = YFCDocument.createDocument(XMLLiterals.USER);
	    getUserListDoc.getDocumentElement().setAttribute(XMLLiterals.LOGIN_ID, EMPTY_STRING);
	    getUserListDoc.getDocumentElement().setAttribute(XMLLiterals.DISPLAY_USER_ID, EMPTY_STRING);
	    getUserListDoc.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
	    return getUserListDoc;
	  }
	
	/**
	  * This method forms template for the getUserList
	  * 
	  * @return
	  */
	
	public YFCDocument formTemplateXmlForgetUserList() {
	    YFCDocument getUserListTemp = YFCDocument.createDocument(XMLLiterals.USER_LIST);
	    YFCElement userEle = getUserListTemp.getDocumentElement().createChild(XMLLiterals.USER);
	    userEle.setAttribute(XMLLiterals.LOGIN_ID, EMPTY_STRING);
	    userEle.setAttribute(XMLLiterals.ENTERPRISE_CODE, EMPTY_STRING);
	    userEle.setAttribute(XMLLiterals.DISPLAY_USER_ID, EMPTY_STRING);
	    userEle.setAttribute(XMLLiterals.ACTIVATE_FLAG, EMPTY_STRING);
	    return getUserListTemp;
	  }
	
	/**
	 * This method calls getUserList API
	 * 
	 * @return
	 */
	
	public YFCDocument getUserList(){
	    return  invokeYantraApi(XMLLiterals.GET_USER_LIST, formInputXmlForGetUserList(),formTemplateXmlForgetUserList());
	 }
	
	/**
	 * This method iterates the list containing the extra users which are
	 * already present in the system and makes them InActive.
	 * 
	 * @param extraUserList
	 */
	
	private void changeStatusForExtraUser(Collection<String> extraUserList, YFCDocument userListApiOp) {
	    for(String value:extraUserList) {
	    	YFCElement userEle = XPathUtil.getXPathElement(userListApiOp, "/UserList/User[@Loginid = \""+value+"\"]");
	    	if(!XmlUtils.isVoid(userEle)) {
	    		String flag = userEle.getAttribute(XMLLiterals.ACTIVATE_FLAG);
	    		if(!INACTIVATE_FLAG.equals(flag)) {
	    			
	    			YFCDocument inputDocForManageUserAPI = YFCDocument.createDocument(XMLLiterals.USER);
	    			inputDocForManageUserAPI.getDocumentElement().setAttribute(XMLLiterals.LOGIN_ID, value);
	    			inputDocForManageUserAPI.getDocumentElement().setAttribute(XMLLiterals.ACTIVATE_FLAG, INACTIVATE_FLAG);
	    			inputDocForManageUserAPI.getDocumentElement().setAttribute(XMLLiterals.ACTION, INACTIVE);
	    			callUserUpdateQueue(inputDocForManageUserAPI);
	    		}
	    	}
	    }
	}
	
	/**
	 * This method will iterate through the inputUserList which contains 
	 * the list of users that needs to be created. Then it creates a 
	 * document with the users and the service is called.
	 * 
	 * @param inputUserList
	 * @param inXml
	 */
	
	private void createNewUserFromInputXml(Collection<String> inputUserList, YFCDocument inXml) {
	    for(String loginId:inputUserList) {
	      YFCElement inEle = XPathUtil.getXPathElement(inXml, "/UserList/User[@Loginid = \""+loginId+"\"]");
	      if(!XmlUtils.isVoid(inEle)) {
	    	  
	    	  String inpEleString = inEle.toString();
	    	  YFCDocument inputDocForService = YFCDocument.getDocumentFor(inpEleString);
	    	  inputDocForService.getDocumentElement().setAttribute(XMLLiterals.ACTION, CREATE);
	    	  inputDocForService.getDocumentElement().setAttribute(XMLLiterals.ACTIVATE_FLAG, FLAG);
	    	  callUserUpdateQueue(inputDocForService);
	    	  
	    	  YFCNode parent = inEle.getParentNode();
	    	  parent.removeChild(inEle);
	      }
	    }
	    modifyExistingUsers(inXml);
	 }
	
	/**
	 * This method accepts the inXml which only has the list of users that 
	 * needs to be modified.
	 * 
	 * @param inXml
	 */
	
	private void modifyExistingUsers(YFCDocument inXml){
		if(!XmlUtils.isVoid(inXml)) {
			YFCElement inEle = inXml.getDocumentElement();
			YFCIterable<YFCElement> userEle = inEle.getChildren(XMLLiterals.USER);
			for(YFCElement element : userEle) {
			
				String inputString = element.toString();
				YFCDocument existingUserforModify = YFCDocument.getDocumentFor(inputString);
				existingUserforModify.getDocumentElement().setAttribute(XMLLiterals.ACTION, MODIFY);
				callUserUpdateQueue(existingUserforModify);
				}
			}
		}
	
	/**
	 * This method calls custom server to drop messages into queue
	 * 
	 * @param inputDocForService
	 */
	
	private void callUserUpdateQueue(YFCDocument doc) {
	     invokeYantraService(USER_MANAGER_SERVER, doc);
	   }
}
