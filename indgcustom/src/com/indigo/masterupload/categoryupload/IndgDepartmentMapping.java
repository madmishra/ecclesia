package com.indigo.masterupload.categoryupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author BSG168	
 * 
 * Custom API to manage Department and DepartmentGroup mapping
 * based on the input document. The input document will have
 * the list of Departments associated with DepartmentGroups.
 * Code will perform the department-mapping for all the active
 * ShipNodes.
 *
 */

public class IndgDepartmentMapping extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final String MANAGE = "Manage";
	private static final String WSC_DEPART_GROUP = "WSC_DEPART_GROUP";
	private static final String VALUE = "Y";

	/**
	 * This method is invoke point of the service.
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument docGetShipNodeList = getShipNodeListApiOp();
		docGetAttributesFromInXml(inXml, docGetShipNodeList);
		
		return null;
	}
	
	/**
	 * This method forms the Input XML for getShipNodeListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument formInputXmlForGetStoreList() {
	    YFCDocument getStoreListDoc = YFCDocument.createDocument(XMLLiterals.SHIPNODE);
	    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
	    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.ACTIVATEFLAG, VALUE);
	    return getStoreListDoc;
	 }
	
	/**
	 * This method forms template for the getShipNodeList
	 * 
	 * @return
	 */
	
	public YFCDocument formTemplateXmlForgetShipNodeList() {
	    YFCDocument getShipNodeListTemp = YFCDocument.createDocument(XMLLiterals.SHIPNODE_LIST);
	    YFCElement shipNodeEle = getShipNodeListTemp.getDocumentElement().createChild(XMLLiterals.SHIPNODE);
	    shipNodeEle.setAttribute(XMLLiterals.SHIP_NODE_CODE, EMPTY_STRING);
	    shipNodeEle.setAttribute(XMLLiterals.ACTIVATEFLAG, EMPTY_STRING);
	    return getShipNodeListTemp;
	 }
	
	/**
	 * This method calls getShipNodeList API
	 * 
	 * @return
	 */
	
	public YFCDocument getShipNodeListApiOp(){
	    return  invokeYantraApi(XMLLiterals.GET_SHIP_NODE_LIST, formInputXmlForGetStoreList(),formTemplateXmlForgetShipNodeList());
	 }
	
	/**
	 * This code traverses through the input doc and fetches the 
	 * DepartmentGroups and associated Departments.
	 * 
	 * @param inXml
	 * @param docGetShipNodeList
	 */
	
	private void docGetAttributesFromInXml(YFCDocument inXml, YFCDocument docGetShipNodeList) {
		YFCElement deptGroupListEle = inXml.getDocumentElement();
	    YFCIterable<YFCElement> deptGroup = deptGroupListEle.getChildren(XMLLiterals.DEPARTMENT_GROUP);
	    for(YFCElement L1 : deptGroup) {
	    	String groupName = L1.getAttribute(XMLLiterals.GROUP_NAME);
	    	if(XmlUtils.isVoid(L1.getChildElement(XMLLiterals.DEPARTMENT))) {
	    		docCommonCodeNoDept(groupName, docGetShipNodeList);
	    	}
	    	YFCIterable<YFCElement> department = L1.getChildren(XMLLiterals.DEPARTMENT);
	    	for(YFCElement L11 : department) {
	    		String deptName = L11.getAttribute(XMLLiterals.DEPARTMENT_NAME);
	    		docManageCommonCodeInp(groupName, deptName, docGetShipNodeList);
	    	}
	    }
	}
	
	/**
	 * This code forms the manageCommonCode API input when there are 
	 * no associated Departments with DepartmentGroup.
	 * 
	 * @param groupName
	 * @param docGetShipNodeList
	 */
	
	private void docCommonCodeNoDept(String groupName, YFCDocument docGetShipNodeList) {
		YFCDocument commonCode = YFCDocument.createDocument(XMLLiterals.COMMON_CODE);
		commonCode.getDocumentElement().setAttribute(XMLLiterals.ACTION, MANAGE);	
		commonCode.getDocumentElement().setAttribute(XMLLiterals.CODE_TYPE, WSC_DEPART_GROUP);
		commonCode.getDocumentElement().setAttribute(XMLLiterals.CODE_SHORT_DESCRIPTION, groupName);
		commonCode.getDocumentElement().setAttribute(XMLLiterals.CODE_VALUE, groupName);
		docSetShipNodeToInp(commonCode, docGetShipNodeList);
	}
	
	/**
	 * This code forms the manageCommonCode API input when there are 
	 * associated Departments with DepartmentGroup.
	 * 
	 * @param groupName
	 * @param deptName
	 * @param docGetShipNodeList
	 */
	
	private void docManageCommonCodeInp(String groupName, String deptName, YFCDocument docGetShipNodeList) {
		YFCDocument commonCode = YFCDocument.createDocument(XMLLiterals.COMMON_CODE);
		commonCode.getDocumentElement().setAttribute(XMLLiterals.ACTION, MANAGE);	
		commonCode.getDocumentElement().setAttribute(XMLLiterals.CODE_TYPE, WSC_DEPART_GROUP);
		commonCode.getDocumentElement().setAttribute(XMLLiterals.CODE_SHORT_DESCRIPTION, groupName);
		commonCode.getDocumentElement().setAttribute(XMLLiterals.CODE_VALUE, groupName);
		YFCElement commonCodeAttributeList = commonCode.getDocumentElement().createChild(XMLLiterals.COMMON_CODE_ATTRIBUTE_LIST);
		commonCodeAttributeList.setAttribute(XMLLiterals.RESET, VALUE);
		YFCElement commonCodeAttribute = commonCodeAttributeList.createChild(XMLLiterals.COMMON_CODE_ATTRIBUTE);
		commonCodeAttribute.setAttribute(XMLLiterals.NAME, deptName);
		commonCodeAttribute.setAttribute(XMLLiterals.VALUE, VALUE);
		docSetShipNodeToInp(commonCode, docGetShipNodeList);
	}
	
	/**
	 * This code sets the value of OrganizationCode in the 
	 * manageCommonCode API input and sends the input to the API.
	 * 
	 * @param commonCode
	 * @param docGetShipNodeList
	 */
	
	private void docSetShipNodeToInp(YFCDocument commonCode, YFCDocument docGetShipNodeList) {
		YFCElement shipNodeListEle = docGetShipNodeList.getDocumentElement();
	    YFCIterable<YFCElement> yfsItrator = shipNodeListEle.getChildren(XMLLiterals.SHIPNODE);
	    for(YFCElement shipNodeEle : yfsItrator) {
	    	String shipNode = shipNodeEle.getAttribute(XMLLiterals.SHIP_NODE_CODE);
	    	commonCode.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, shipNode);
	    	invokeYantraApi(XMLLiterals.MANAGE_COMMON_CODE_API, commonCode);
	    }
	}
}

