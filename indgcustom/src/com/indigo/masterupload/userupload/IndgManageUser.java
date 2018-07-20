package com.indigo.masterupload.userupload;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.utils.IndgManageDeltaLoadUtil;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
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
	private static final String XPATH_USERLIST_API = "/UserList/User[@Loginid = \"";
	private static final String A = "0";
	private static final String B = "00";
	private static final String C = "000";
	private static final String ORGANIZATION_CODE_VAL = "Indigo_CA";
	private static final String HASH_VAL = "#";
	private static final String STORE = "Store ";
	private String localeCode = "";
	private static final String APPLICATION_VAL = "WSC";
	private static final String COMPONENT_VAL = "BATCH_SORT_METHOD";
	private static final String DEFINATION_VAL = "SORT_AFTER_PICK";
	private static final String SCREEN_NAME_VAL = "ALL";
	
	/**	 
	 * This method is the invoke point of the service.
	 *
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml){
		 checkStoreIdForUsers(inXml);
		 YFCDocument getOrganizationListApiOp = getOrganizationList();
		 localeCode = getOrganizationListApiOp.getDocumentElement().getChildElement(XMLLiterals.ORGANIZATION).
				 getAttribute(XMLLiterals.LOCALE_CODE2);
		 YFCDocument userListApiOp = getUserList();
		 YFCDocument commonCodeListApiOp = getCommonCodeList();
		 Collection<String> extraUserList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(inXml, userListApiOp, 
					XMLLiterals.LOGIN_ID,XMLLiterals.USER);
		 Collection<String> inputUserList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(userListApiOp, inXml, 
					XMLLiterals.LOGIN_ID,XMLLiterals.USER);
		 ifStoreExistForNewUser(inXml, inputUserList, getOrganizationListApiOp);
		 changeStatusForExtraUser(extraUserList,userListApiOp);
		 createNewUserFromInputXml(inputUserList,inXml,commonCodeListApiOp);
		 manageUIStateforNewUsers(inputUserList);
		 return inXml;
	}
	
	/**
	 * This method checks the Department attribute and appends zeros in the 
	 * storeId if it is not 4 digits long.
	 * 
	 * @param inXml
	 */
	
	private void checkStoreIdForUsers(YFCDocument inXml) {
		String sStoreIdVal = null;
		YFCIterable<YFCElement> yfsItrator = inXml.getDocumentElement().getChildren(XMLLiterals.USER);
 	    for(YFCElement userEle : yfsItrator) {
 	    	String sDepartment = userEle.getAttribute(XMLLiterals.DEPARTMENT);
 	    	if(!YFCObject.isVoid(sDepartment)) {
 	    		String[] segments = sDepartment.split(HASH_VAL);
 	    		String sStoreNo = segments[1];
 	    		String sStorename = segments[0];
 	    		if(sStoreNo.length()==3) {
 	    			sStoreIdVal = A.concat(sStoreNo);
 	    			userEle.setAttribute(XMLLiterals.ORGANIZATION_KEY, sStoreIdVal);
 			    	userEle.setAttribute(XMLLiterals.DEPARTMENT, sStorename.concat("#").concat(sStoreIdVal)); 			
 	    		}
 	    		else if(sStoreNo.length()==2) {
 	    			sStoreIdVal = B.concat(sStoreNo);
 	    			userEle.setAttribute(XMLLiterals.ORGANIZATION_KEY, sStoreIdVal);
 	    			userEle.setAttribute(XMLLiterals.DEPARTMENT, sStorename.concat("#").concat(sStoreIdVal)); 			
 	    		}
 	    		else if(sStoreNo.length()==1) {
 	    			sStoreIdVal = C.concat(sStoreNo);
 	    			userEle.setAttribute(XMLLiterals.ORGANIZATION_KEY, sStoreIdVal);
 	    			userEle.setAttribute(XMLLiterals.DEPARTMENT, sStorename.concat("#").concat(sStoreIdVal));
 	    		}
 	    		else
 	    			userEle.setAttribute(XMLLiterals.ORGANIZATION_KEY, sStoreNo);
 	    	}
 	    }
	}
	
	/**
	 * This method forms the Input XML for getOrganizationListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument inputXmlForGetOrganizationList() {
	    YFCDocument getOrganizationListDoc = YFCDocument.createDocument(XMLLiterals.USER);
	    getOrganizationListDoc.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, ORGANIZATION_CODE_VAL);
	    return getOrganizationListDoc;
	}
	
	/**
	  * This method forms template for the getOrganizationList
	  * 
	  * @return
	  */
	
	public YFCDocument templateXmlForgetOrganizationList() {
	    YFCDocument getOrganizationListTemp = YFCDocument.createDocument(XMLLiterals.ORGANIZATION_LIST);
	    YFCElement organizationEle = getOrganizationListTemp.getDocumentElement().createChild(XMLLiterals.ORGANIZATION);
	    organizationEle.setAttribute(XMLLiterals.LOCALE_CODE2, EMPTY_STRING);
	    organizationEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, EMPTY_STRING);
	    YFCElement relatedOrgListEle = organizationEle.createChild(XMLLiterals.RELATED_ORG_LIST);
	    YFCElement orgEnterpriseEle = relatedOrgListEle.createChild(XMLLiterals.ORG_ENTERPRISE);
	    orgEnterpriseEle.setAttribute(XMLLiterals.ENTERPRISE_ORGANIZATION_KEY, EMPTY_STRING);
	    orgEnterpriseEle.setAttribute(XMLLiterals.ORGANIZATION_KEY, EMPTY_STRING);
	    return getOrganizationListTemp;
	}
	
	/**
	 * This method calls getOrganizationList API
	 * @return
	 */
	
	public YFCDocument getOrganizationList(){
	    return invokeYantraApi(XMLLiterals.GET_ORGANIZATION_LIST, inputXmlForGetOrganizationList(),templateXmlForgetOrganizationList());
	}
	
	/**
	 * This method forms the Input XML for getUserListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument formInputXmlForGetUserList() {
	    YFCDocument getUserListDoc = YFCDocument.createDocument(XMLLiterals.USER);
	    getUserListDoc.getDocumentElement().setAttribute(XMLLiterals.ENTERPRISE_CODE, ORGANIZATION_CODE_VAL);
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
	    userEle.setAttribute(XMLLiterals.ORGANIZATION_KEY, EMPTY_STRING);
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
	    return  invokeYantraApi(XMLLiterals.GET_COMMON_CODE_LIST, getCommonCodeListInDoc(), getCommonCodeListTemplateDoc());
	 }
	
	/**
	 * This method checks if the Department attribute is present in the
	 * input XML or not. If not it sets the department as Indigo_CA.
	 * 
	 * @param inXml
	 * @param inputUserList
	 * @param getOrganizationListApiOp
	 */
	
	private void ifStoreExistForNewUser(YFCDocument inXml, Collection<String> inputUserList, YFCDocument getOrganizationListApiOp) {
		String deptVal = STORE.concat(HASH_VAL).concat(ORGANIZATION_CODE_VAL);
		for(String loginId : inputUserList) {
			YFCElement userEle = XPathUtil.getXPathElement(inXml, XPATH_USERLIST_API+loginId+"\"]");
			userEle.setAttribute(XMLLiterals.LOCALE_CODE, localeCode);
			String sDepartment = userEle.getAttribute(XMLLiterals.DEPARTMENT);
			if(!YFCObject.isVoid(sDepartment)) {
				String[] segments = sDepartment.split(HASH_VAL);
				String sStoreNo = segments[1];
				YFCElement orgEnterpriseEle = XPathUtil.getXPathElement(getOrganizationListApiOp, "/OrganizationList/Organization/"
 					+ "RelatedOrgList/OrgEnterprise[@OrganizationKey = \""+sStoreNo+"\"]");
				if(YFCObject.isVoid(orgEnterpriseEle)) {
					YFCNode parent = userEle.getParentNode();
					parent.removeChild(userEle);
				}
			}
			else {
				userEle.setAttribute(XMLLiterals.DEPARTMENT, deptVal);
				userEle.setAttribute(XMLLiterals.ORGANIZATION_KEY, ORGANIZATION_CODE_VAL);
			}
		}
	}
	
	/**
	 * This method iterates the list containing the extra users which are
	 * already present in the system and makes them InActive.
	 * 
	 * @param extraUserList
	 */
	
	private void changeStatusForExtraUser(Collection<String> extraUserList, YFCDocument userListApiOp) {
	    for(String loginId : extraUserList) {
	    	YFCElement userEle = XPathUtil.getXPathElement(userListApiOp, XPATH_USERLIST_API+loginId+"\"]");
	    	if(!YFCObject.isVoid(userEle)) {
	    		String flag = userEle.getAttribute(XMLLiterals.ACTIVATE_FLAG);
	    		if(!INACTIVATE_FLAG.equals(flag)) {
	    			YFCDocument inputDocForManageUserAPI = YFCDocument.createDocument(XMLLiterals.USER);
	    			inputDocForManageUserAPI.getDocumentElement().setAttribute(XMLLiterals.LOGIN_ID, loginId);
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
	    for(String loginId : inputUserList) {
	      YFCElement userEle = XPathUtil.getXPathElement(inXml, XPATH_USERLIST_API+loginId+"\"]");
	      if(!YFCObject.isVoid(userEle)) {
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
	    if(!YFCObject.isVoid(memberOfValue)) {		
	    	List<String> memberList = Arrays.asList(memberOfValue.split(","));
	    	for(String sterlingValue : memberList) {
	    		if(sterlingValue.contains(XMLLiterals.STERLING)) {
	    			String membergroupVal = sterlingValue;
	    			YFCElement commonCodeList = commonCodeListApiOp.getDocumentElement();
	    			YFCIterable<YFCElement> commonCodeEle = commonCodeList.getChildren(XMLLiterals.COMMON_CODE);
	    			for(YFCElement codeList :commonCodeEle) {
	    				String commonCodeVal = codeList.getAttribute(XMLLiterals.CODE_VALUE);
	    				if(commonCodeVal.equals(membergroupVal.trim())) {
	    					createUserGroupDocHeader(codeList, inputDoctoCreateUser);
   						}
	   				}
	   			}
	   		}
	    }
	}
	
	/**
	 * This method sets the attributes value in the document.
	 * 
	 * @param codeList
	 * @param inputDoctoCreateUser
	 */
	
	private void createUserGroupDocHeader(YFCElement codeList, YFCDocument inputDoctoCreateUser) {
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
	
	/**
	 * This method accepts the inXml which only has the list of users that 
	 * needs to be modified.
	 * 
	 * @param inXml
	 */
	
	private void modifyExistingUsers(YFCDocument inXml){
		if(!YFCObject.isVoid(inXml)) {
			YFCIterable<YFCElement> userEle = inXml.getDocumentElement().getChildren(XMLLiterals.USER);
			for(YFCElement element : userEle) {
				String sDepartment = element.getAttribute(XMLLiterals.DEPARTMENT);
				if(YFCObject.isVoid(sDepartment)) {
					String deptVal = STORE.concat(HASH_VAL).concat(ORGANIZATION_CODE_VAL);
					element.setAttribute(XMLLiterals.DEPARTMENT, deptVal);
					element.setAttribute(XMLLiterals.ORGANIZATION_KEY, ORGANIZATION_CODE_VAL);
				}
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
	
	/**
	 * This method forms the input for all users to send through
	 * manageUserUIState API.
	 * 
	 * @param inputUserList
	 */
	
	private void manageUIStateforNewUsers(Collection<String> inputUserList) {
		for(String loginId : inputUserList) {
			YFCDocument docUserUiState = YFCDocument.createDocument(XMLLiterals.USER_UI_STATE);
			docUserUiState.getDocumentElement().setAttribute(XMLLiterals.APPLICATION_NAME, APPLICATION_VAL);
			docUserUiState.getDocumentElement().setAttribute(XMLLiterals.COMPONENT_NAME, COMPONENT_VAL);
			docUserUiState.getDocumentElement().setAttribute(XMLLiterals.DEFINITION, DEFINATION_VAL);
			docUserUiState.getDocumentElement().setAttribute(XMLLiterals.LOGIN_ID, loginId);
			docUserUiState.getDocumentElement().setAttribute(XMLLiterals.SCREEN_NAME, SCREEN_NAME_VAL);
			invokeYantraApi(XMLLiterals.MANAGE_USER_UI_STATE, docUserUiState);
		}
	}
}
