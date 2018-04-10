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

/**
 * 
 * @author BGS168
 * 
 * Custom API to manage Stores to create and remove organizations
 * based on Input document and the shipNodes that already exist.
 * If organization preset in Input and but not in the system 
 * manageOrganizationHierarchy will be called to create. If the 
 * organization does not exist in InputDoc and but exist in System 
 * then shipNode will be removed.If organization exist in both
 * system and input doc publish to queue for manageOrganizationHierarchy.
 *
 */

public class IndgStoreUpdates extends AbstractCustomApi {
	
	private static final String EMPTY_STRING = "";
	private static final String ORGANIZATION_SERVER= "OrganizationServer";
	
	/**	 
	 * This method is the invoke point of the service.
	 *  This calls manageDeltaLoadForDeletion method to
	 *  get the list of shipNodes that need to be removed.
	 * 
	 */	

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
	   YFCDocument shipNodeListApiOp = getShipNodeList();
	   Collection<String> uncommonShipNodeList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(inXml, shipNodeListApiOp, 
			   XMLLiterals.ORGANIZATION_CODE,XMLLiterals.ORGANIZATION,XMLLiterals.SHIP_NODE_CODE,XMLLiterals.SHIPNODE);
	   addInputDocToManageOrganizationHierarchy(inXml.getDocumentElement());
	   manageExtraNodesInApiDoc(uncommonShipNodeList,shipNodeListApiOp);
	   return inXml;
	}
	
	/**
	 * This method forms the Input XML for getShipNodeListAPI
	 * 
	 * @return
	 */
	
	public YFCDocument formInputXmlForGetStoreList() {
	    YFCDocument getStoreListDoc = YFCDocument.createDocument(XMLLiterals.SHIPNODE);
	    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.OWNERKEY, XMLLiterals.INDIGO_CA);
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
	    YFCElement organizationEle = shipNodeEle.createChild(XMLLiterals.ORGANIZATION);
	    organizationEle.setAttribute(XMLLiterals.CAPACITYORGCODE, XMLLiterals.INDIGO_CA);
	    organizationEle.setAttribute(XMLLiterals.PMENTERPRISEKEY, XMLLiterals.INDIGO_CA);
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
	 * This forms Input Document for manageOrganizationHierarchy 
	 * and calls service to add the Document into Queue
	 * 
	 * @param inputEle
	 */
	
	private void addInputDocToManageOrganizationHierarchy(YFCElement inputEle){
	     YFCIterable<YFCElement> yfsItator = inputEle.getChildren(XMLLiterals.ORGANIZATION);
	     for(YFCElement organizationEle: yfsItator) {
	       YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.STORE_LIST);
	       inputDocForService.getDocumentElement().importNode(organizationEle);
	       callOrganizationUpdateQ(inputDocForService);
	     }
	   }
	
	/**
	 * This method calls custom server to drop messages into queue
	 * 
	 * @param inputDocForService
	 */
	
	private void callOrganizationUpdateQ(YFCDocument inputDocForService) {
	     invokeYantraService(ORGANIZATION_SERVER, inputDocForService);
	   }
		
	/**
	 * This method iterates the Collection List and changes the Default
	 * Action of Create or Modify to Delete so to remove the extra
	 * shipNodes present in the system.
	 * 
	 * @param organizationList
	 * @param shipNodeListAPIDoc
	 */
	
	private void manageExtraNodesInApiDoc(Collection<String> organizationList, YFCDocument shipNodeListAPIDoc) {
	    for(String organizationId:organizationList) {
	      YFCElement inEle = XPathUtil.getXPathElement(shipNodeListAPIDoc, "/StoreList/Organization[@OrganizationCode = \""+organizationId+"\"]");
	      if(!XmlUtils.isVoid(inEle)) {
	        inEle.setAttribute(XMLLiterals.ACTION, XMLLiterals.DELETE);
	        YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.STORE_LIST);
	        inputDocForService.getDocumentElement().importNode(inEle);
	        callOrganizationUpdateQ(inputDocForService);
	      }
	    }
	}

}
