package com.indigo.masterupload.categoryupload;

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
 * @author BSG109
 * 
 * Custom API to manage Category to Publish and UN-Publish 
 * category based on input Document and Category already 
 * exist.If category preset in Input and but not in the system 
 * ManageAPI will be called to create.If the category not exist
 * in InputDoc and but exist in System then Category will be 
 * unpublished.If category exist in both system and input 
 * doc publish to queue for ManageCategory
 * 
 *
 */

public class IndgManageCatgoryLoad extends AbstractCustomApi {
  
  private static final String EMPTY_STRING = "";
  private static final String MANAGE_CATEGORY_UPLOADQ_FLOW = "Indg_CategoryFeed_Q";
  private static final String UN_PUBLISH_STATUS = "2000";
  
  
  private String organizationCode = "";
  
  
  /**
   *  This method is the invoke point of the service
   *  This calls manageDeltaLoadForDeletion method to
   *  get the list of Category that need to be un published 
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml)  {
   setOrganizationCode(inXml);
   YFCDocument categoryListApiOp = getCategoryList(EMPTY_STRING, organizationCode);
   Collection<String> unpublishCategoryList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion
       (inXml, categoryListApiOp,XMLLiterals.CATEGORY_ID, XMLLiterals.CATEGORY);
   addInputDocToManageCategory(inXml.getDocumentElement());
   manageUnPublishCategory(unpublishCategoryList,categoryListApiOp);
   return inXml;
  }
 
  /**
   * This method gets Organization code from Input Doc
   * 
   * @param inXml
   */
   private void setOrganizationCode(YFCDocument inXml){
     organizationCode = XPathUtil.getXpathAttribute(inXml, "/CategoryList/Category/@OrganizationCode");
   }
   
   /**
    * This forms Input Document for ManageCategory and calls
    * service to add the Document into Queue
    * 
    * @param inputEle
    */
   private void addInputDocToManageCategory(YFCElement inputEle){
     YFCIterable<YFCElement> yfsItator = inputEle.getChildren(XMLLiterals.CATEGORY);
     for(YFCElement categoryEle: yfsItator) {
       YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
       inputDocForService.getDocumentElement().importNode(categoryEle);
       callManageCategoryQ(inputDocForService);
     }
   }
   
   /**
    * This method calls custom server to drop Document 
    * to Queue
    * 
    * @param yfcInputDoc
    */
   private void callManageCategoryQ(YFCDocument inputDocForService) {
     invokeYantraService(MANAGE_CATEGORY_UPLOADQ_FLOW, inputDocForService);
   }
   
   /**
    * 
    * This method iterates the Collection List and append
    * Status 2000 to un-publish the category.
    * 
    * @param categoryList
    * @param categoryListAPIDoc
    */
   private void manageUnPublishCategory(Collection<String> categoryList, YFCDocument categoryListAPIDoc) {
    for(String categoryId:categoryList) {
      YFCElement inEle = XPathUtil.getXPathElement(categoryListAPIDoc, "/CategoryList/Category[@CategoryID = "+categoryId+"]");
      if(!XmlUtils.isVoid(inEle)) {
        inEle.setAttribute(XMLLiterals.STATUS, UN_PUBLISH_STATUS);
        YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
        inputDocForService.getDocumentElement().importNode(inEle);
        callManageCategoryQ(inputDocForService);
      }
    }
   }
   
   /**
    * 
    * This method forms the Input XML for getCategoryListApi
    * 
    * @param categoryId
    * @param org
    * @return
    */
   private YFCDocument formInputXmlForGetCategoryList(String categoryId,String org) {
     YFCDocument getCategoryListDoc = YFCDocument.createDocument(XMLLiterals.CATEGORY);
     getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.CATEGORY_ID, categoryId);
     getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.CATEGORY_KEY, categoryId);
     getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, org);
     return getCategoryListDoc;
   }
   
   
   /**
    * This method forms templates for the getCateforyList 
    * 
    * @return
    */
   private YFCDocument formTemplateXmlForgetCategoryList() {
     YFCDocument getCategoryListTemp = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
     YFCElement categoryEle = getCategoryListTemp.getDocumentElement().createChild(XMLLiterals.CATEGORY);
     categoryEle.setAttribute(XMLLiterals.CATEGORY_ID, EMPTY_STRING);
     categoryEle.setAttribute(XMLLiterals.CATEGORY_PATH, EMPTY_STRING);
     return getCategoryListTemp;
   }
   
  /**
   * This method calls getCategoryList API and 
   * @param categoryId
   * @param org
   * @return
   */
   public YFCDocument getCategoryList(String categoryId, String org){
     return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
         formInputXmlForGetCategoryList(categoryId,org),formTemplateXmlForgetCategoryList());
   }
   
   
}
