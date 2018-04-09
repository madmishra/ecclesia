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

public class IndgStoreUpdates extends AbstractCustomApi {
	
	private String organizationCode = "";
	private String shipNode = "";
	private static final String EMPTY_STRING = "";

	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		   setOrganizationCode(inXml);
		   YFCDocument shipNodeListApiOp = getShipNodeList(shipNode);
		   Collection<String> uncommonShipNodeList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion(inXml, shipNodeListApiOp, 
				   XMLLiterals.ORGANIZATION_CODE,XMLLiterals.ORGANIZATION,XMLLiterals.SHIP_NODE_CODE,XMLLiterals.SHIPNODE);
		   addInputDocToManageOrganizationHierarchy(inXml.getDocumentElement());
		   manageExtraNodesInApiDoc(uncommonShipNodeList,shipNodeListApiOp);
		   return inXml;
	}
	
	//get organizationCode from Input Doc
	private void setOrganizationCode(YFCDocument inXml){
		organizationCode = XPathUtil.getXpathAttribute(inXml, "/StoreList/Organization/@OrganizationCode");
	   }
	
	//calls the getShipNodeList API and return API Input Doc
	 public YFCDocument getShipNodeList(String shipno){
	     return  invokeYantraApi(XMLLiterals.GET_SHIP_NODE_LIST, formInputXmlForGetStoreList(shipno),formTemplateXmlForgetShipNodeList());
	   }
	 
	 
	 public YFCDocument formInputXmlForGetStoreList(String shipno) {
		    YFCDocument getStoreListDoc = YFCDocument.createDocument(XMLLiterals.SHIPNODE);
		    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.OWNERKEY, XMLLiterals.INDIGO_CA);
		    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODE, shipno);
		    getStoreListDoc.getDocumentElement().setAttribute(XMLLiterals.SHIPNODEKEY, shipno);
		    return getStoreListDoc;
		  }
		
		public YFCDocument formTemplateXmlForgetShipNodeList() {
		    YFCDocument getShipNodeListTemp = YFCDocument.createDocument(XMLLiterals.SHIPNODE_LIST);
		    YFCElement shipNodeEle = getShipNodeListTemp.getDocumentElement().createChild(XMLLiterals.SHIPNODE);
		    shipNodeEle.setAttribute(XMLLiterals.SHIP_NODE_CODE, EMPTY_STRING);
		    YFCElement organizationEle = shipNodeEle.createChild(XMLLiterals.ORGANIZATION);
		    organizationEle.setAttribute(XMLLiterals.CAPACITYORGCODE, XMLLiterals.INDIGO_CA);
		    organizationEle.setAttribute(XMLLiterals.PMENTERPRISEKEY, XMLLiterals.INDIGO_CA);
		    return getShipNodeListTemp;
		  }
		
		//form input doc for ManageOrganizationHierarchy and Calls service to drop into queue
		private void addInputDocToManageOrganizationHierarchy(YFCElement inputEle){
		     YFCIterable<YFCElement> yfsItator = inputEle.getChildren(XMLLiterals.ORGANIZATION);
		     for(YFCElement organizationEle: yfsItator) {
		       YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.STORE_LIST);
		       inputDocForService.getDocumentElement().importNode(organizationEle);
		       callOrganizationUpdate_Q(inputDocForService);
		     }
		   }
		
		//Call custom Server
		private void callOrganizationUpdate_Q(YFCDocument inputDocForService) {
		     invokeYantraService(OrganizationServer, inputDocForService);
		   }
		
		//Iterates and delete the extra node in API input doc
		private void manageExtraNodesInApiDoc(Collection<String> organizationList, YFCDocument shipNodeListAPIDoc) {
		    for(String organizationId:organizationList) {
		      YFCElement inEle = XPathUtil.getXPathElement(shipNodeListAPIDoc, "/StoreList/Organization[@OrganizationKey = "+organizationId+"]");
		      if(!XmlUtils.isVoid(inEle)) {
		        inEle.setAttribute(XMLLiterals.ACTION, XMLLiterals.DELETE);
		        YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.STORE_LIST);
		        inputDocForService.getDocumentElement().importNode(inEle);
		        callOrganizationUpdate_Q(inputDocForService);
		      }
		    }
		}

}
