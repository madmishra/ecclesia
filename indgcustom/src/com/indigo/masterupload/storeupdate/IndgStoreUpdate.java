package com.indigo.masterupload.storeupdate;

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
 * Custom API to manage Stores to create and de-flag organizations
 * based on Input document and the shipNodes that already exist.
 * If organization preset in Input and but not in the system 
 * createOrganizationHierarchy will be called to create. If the 
 * organization does not exist in InputDoc and but exist in System 
 * then the ActiveFlag will be changed. If organization exist in both
 * system and input doc the manageOrganizationHierarchy will be called
 * update the system stores.
 *
 */

public class IndgStoreUpdate extends AbstractCustomApi{
	
	private static final String EMPTY_STRING = "";
	private static final String INACTIVATE_FLAG = "N";
	private static final String NODE = "NODE";
	
	/**	 
	 * This method is the invoke point of the service.
	 *
	 */

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCDocument shipNodeListApiOp = getShipNodeList();
		Collection<String> uncommonShipNodeList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(inXml, shipNodeListApiOp, 
				XMLLiterals.ORGANIZATION_CODE,XMLLiterals.ORGANIZATION, XMLLiterals.SHIP_NODE_CODE,XMLLiterals.SHIPNODE);
		Collection<String> organizationCodeList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(shipNodeListApiOp, inXml, 
				XMLLiterals.SHIP_NODE_CODE,XMLLiterals.SHIPNODE,XMLLiterals.ORGANIZATION_CODE,XMLLiterals.ORGANIZATION);
		changeStatusOfExtraNodes(uncommonShipNodeList, shipNodeListApiOp);
		createNewNodesInInputXml(organizationCodeList,inXml);
		
		return inXml;
	}
	
	/**
	 * This method forms the Input XML for getShipNodeListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument formInputXmlForGetStoreList() {
	    YFCDocument getStoreListDoc = YFCDocument.createDocument(XMLLiterals.SHIPNODE);
	    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.OWNERKEY, EMPTY_STRING);
	    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, EMPTY_STRING);
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
	    YFCElement organizationEle = shipNodeEle.createChild(XMLLiterals.ORGANIZATION);
	    organizationEle.setAttribute(XMLLiterals.CAPACITYORGCODE, EMPTY_STRING);
	    organizationEle.setAttribute(XMLLiterals.PMENTERPRISEKEY, EMPTY_STRING);
	    
	    return getShipNodeListTemp;
	 }
	
	/**
	 * This method calls getShipNodeList API
	 * 
	 * @return
	 */
		
	public YFCDocument getShipNodeList(){
	    return  invokeYantraApi(XMLLiterals.GET_SHIP_NODE_LIST, formInputXmlForGetStoreList(),formTemplateXmlForgetShipNodeList());
	 }
	
	/**
	 * This method iterates the uncommonShipNode List and changes
	 * the ActiveFlag status to 'N'.
	 * 
	 * @param organizationList
	 * @param shipNodeListApiOp
	 */
	
	private void changeStatusOfExtraNodes(Collection<String> uncommonShipNodeList, YFCDocument shipNodeListApiOp) {
	    for(String value:uncommonShipNodeList) {
	    	YFCElement shipNodeEle = XPathUtil.getXPathElement(shipNodeListApiOp, "/ShipNodeList/ShipNode[@ShipNode = \""+value+"\"]");
	    	if(!XmlUtils.isVoid(shipNodeEle)) {
	    		String flag = shipNodeEle.getAttribute(XMLLiterals.ACTIVATEFLAG);
	    		if(!flag.equals(INACTIVATE_FLAG)) {
	    			
	    			YFCDocument inputDocForManageOrgAPI = YFCDocument.createDocument(XMLLiterals.ORGANIZATION);
	    			inputDocForManageOrgAPI.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, value);
	    			YFCElement nodeEle = inputDocForManageOrgAPI.getDocumentElement().createChild((XMLLiterals.NODE));
	    			nodeEle.setAttribute(XMLLiterals.ACTIVATEFLAG, INACTIVATE_FLAG);
	    			invokeYantraApi(XMLLiterals.MANAGE_ORGANIZATION_HIERARCHY, inputDocForManageOrgAPI);
	    		}
	    	}
	    }
	 }
	
	/**
	 * This method iterates the Collection List and forms a
	 * document to pass through the CreateOrganizationHierarchy API
	 * and removes that particular element from the XML which is 
	 * passed to another method where other organizations will be 
	 * modified.
	 * 
	 * @param organizationList2
	 * @param inXml
	 */
	
	private void createNewNodesInInputXml(Collection<String> organizationList2, YFCDocument inXml) {
	    for(String organizationId:organizationList2) {
	      YFCElement organizationEle = XPathUtil.getXPathElement(inXml, "/StoreList/Organization[@OrganizationCode = \""+organizationId+"\"]");
	      if(!XmlUtils.isVoid(organizationEle)) {
	    	  
	    	  String sInpOrgCodeEle = organizationEle.toString();
	    	  YFCDocument docCreateOrgInput = YFCDocument.getDocumentFor(sInpOrgCodeEle);
	    	  YFCElement organization = docCreateOrgInput.getDocumentElement();
	    	  YFCElement orgRoleEle = organization.createChild(XMLLiterals.ORG_ROLE_LIST);
	    	  YFCElement orgRole = orgRoleEle.createChild(XMLLiterals.ORG_ROLE);
	    	  orgRole.setAttribute(XMLLiterals.ROLE_KEY, NODE);
	    	  
	    	  invokeYantraApi(XMLLiterals.CREATE_ORGANIZATION_HIERARCHY, docCreateOrgInput);
	    	  
	    	  YFCNode parent = organizationEle.getParentNode();
	    	  parent.removeChild(organizationEle);
	      }
	    }
	    modifyExistingNodes(inXml);
	 }
	
	/**
	 * This method passes through the remaining Organization elements
	 * in the XML document and calls the manageOrganizationHierarchy API.
	 * 
	 */
	
	private void modifyExistingNodes(YFCDocument inXml){
		if(!XmlUtils.isVoid(inXml)) {
			YFCElement inEle = inXml.getDocumentElement();
			YFCIterable<YFCElement> organizationEle = inEle.getChildren(XMLLiterals.ORGANIZATION);
			for(YFCElement element : organizationEle) {
			
				String inputString = element.toString();
				YFCDocument remainingNodesforModify = YFCDocument.getDocumentFor(inputString);
				invokeYantraApi(XMLLiterals.MODIFY_ORGANIZATION_HIERARCHY, remainingNodesforModify);
				}
			}
		}
}
