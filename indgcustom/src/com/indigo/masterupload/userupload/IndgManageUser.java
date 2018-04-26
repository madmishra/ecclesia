package com.indigo.masterupload.userupload;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
	private static final String USERGROUPMAPPING = "USER_GROUP_MAPPING";
	
	/**	 
	 * This method is the invoke point of the service.
	 *
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml){
		
		 YFCDocument userListApiOp = getUserList();
		 YFCDocument commonCodeListApiOp = getCommonCodeList();
		 Collection<String> extraUserList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(inXml, userListApiOp, 
					XMLLiterals.LOGIN_ID,XMLLiterals.USER);
		 Collection<String> inputUserList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(userListApiOp, inXml, 
					XMLLiterals.LOGIN_ID,XMLLiterals.USER);
		 changeStatusForExtraUser(extraUserList,userListApiOp);
		 createNewUserFromInputXml(inputUserList,inXml,commonCodeListApiOp);
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
	 * This method forms the Input XML for getCommonCodeListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument getCommonCodeListInDoc() {
	    YFCDocument getCodeListDoc = YFCDocument.createDocument(XMLLiterals.COMMON_CODE);
	    getCodeListDoc.getDocumentElement().setAttribute(XMLLiterals.CODE_TYPE, USERGROUPMAPPING);
	    return getCodeListDoc;
	  }
	
	/**
	 * This method forms template for the getCommonCodeList
	 * 
	 * @return
	 */
	
	public YFCDocument getCommonCodeListTemplateDoc() {
	    YFCDocument getCommonCodeListTemp = YFCDocument.createDocument(XMLLiterals.COMMON_CODE_LIST);
	    YFCElement codeEle = getCommonCodeListTemp.getDocumentElement().createChild(XMLLiterals.COMMON_CODE);
	    codeEle.setAttribute(XMLLiterals.CODE_NAME, EMPTY_STRING);
	    codeEle.setAttribute(XMLLiterals.CODE_VALUE, EMPTY_STRING);
	    codeEle.setAttribute(XMLLiterals.CODE_LONG_DESCRIPTION, EMPTY_STRING);
	    codeEle.setAttribute(XMLLiterals.CODE_SHORT_DESCRIPTION, EMPTY_STRING);
	    return getCommonCodeListTemp;
	  }
	
	/**
	 * This method calls getCommonCodeList API
	 * 
	 * @return
	 */
	
	public YFCDocument getCommonCodeList(){
	    return  invokeYantraApi(XMLLiterals.GET_COMMON_CODE_LIST, getCommonCodeListInDoc(),
	    		getCommonCodeListTemplateDoc());
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
	
	private void createNewUserFromInputXml(Collection<String> inputUserList, YFCDocument inXml, YFCDocument commonCodeListApiOp) {
	    for(String loginId:inputUserList) {
	      YFCElement userEle = XPathUtil.getXPathElement(inXml, "/UserList/User[@Loginid = \""+loginId+"\"]");
	      if(!XmlUtils.isVoid(userEle)) {
	    	  
	    	  String inpEleString = userEle.toString();
	    	  YFCDocument inputDoctoCreateUser = YFCDocument.getDocumentFor(inpEleString);
	    	  inputDoctoCreateUser.getDocumentElement().setAttribute(XMLLiterals.ACTION, CREATE);
	    	  inputDoctoCreateUser.getDocumentElement().setAttribute(XMLLiterals.ACTIVATE_FLAG, FLAG);
	    	  
	    	  createUserGroupInputDoc(inputDoctoCreateUser, userEle, commonCodeListApiOp);
	    	  
	    	  callUserUpdateQueue(inputDoctoCreateUser);
	    	  
	    	  YFCNode parent = userEle.getParentNode();
	    	  parent.removeChild(userEle);
	      }
	    }
	    
	    modifyExistingUsers(inXml);
	 }
	
	/**
	 * This method will iterate through the list of CodeValue and verify
	 * if any Sterling- group is present or not. If present, then it adds
	 * the queue, menuId and Group value to user puts the document in 
	 * the queue.
	 * 
	 * @param inputDoctoCreateUser
	 * @param userEle
	 * @param commonCodeListApiOp
	 */
	
	private void createUserGroupInputDoc(YFCDocument inputDoctoCreateUser, YFCElement userEle, YFCDocument commonCodeListApiOp) {
		
		 String memberOfValue = userEle.getAttribute(XMLLiterals.MEMBER_OF);
	    	if(!XmlUtils.isVoid(memberOfValue)) {
	    		
	    		List<String> memberList = Arrays.asList(memberOfValue.split(","));
	    		for(String sterlingValue : memberList) {
	    			
	    			if(sterlingValue.contains(XMLLiterals.STERLING)) {
	    				String membergroupVal = sterlingValue;
	    					
	    				YFCElement commonCodeList = commonCodeListApiOp.getDocumentElement();
	    				YFCIterable<YFCElement> commonCodeEle = commonCodeList.getChildren(XMLLiterals.COMMON_CODE);
	    				
	    				for(YFCElement codeList :commonCodeEle) {
	    					String commonCodeVal = codeList.getAttribute(XMLLiterals.CODE_VALUE);
	    						if(commonCodeVal.equals(membergroupVal.trim())) {
	    							
	    							String sterlingGroup = codeList.getAttribute(XMLLiterals.CODE_NAME);
	    							String queue = codeList.getAttribute(XMLLiterals.CODE_LONG_DESCRIPTION);
	    							String menuId = codeList.getAttribute(XMLLiterals.CODE_SHORT_DESCRIPTION);
	    							
	    							inputDoctoCreateUser.getDocumentElement().setAttribute(XMLLiterals.MENU_ID, menuId);
	    							YFCElement queueSubsList = inputDoctoCreateUser.getDocumentElement()
	    									.createChild(XMLLiterals.QUEUE_SUBSCRIPTION_LIST);
	    							YFCElement queueSubs = queueSubsList.createChild(XMLLiterals.QUEUE_SUBSCRIPTION);
	    							queueSubs.setAttribute(XMLLiterals.QUEUE_KEY, queue);
	    						
	    							YFCElement userGroupList = inputDoctoCreateUser.getDocumentElement().createChild(XMLLiterals.USER_GROUP_LISTS);
	    							YFCElement userGroup = userGroupList.createChild(XMLLiterals.USER_GROUP_LIST);
	    							userGroup.setAttribute(XMLLiterals.USER_GROUP_ID, sterlingGroup);
	    						}
	    				}
	    			}
	    		}
	    	}
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
