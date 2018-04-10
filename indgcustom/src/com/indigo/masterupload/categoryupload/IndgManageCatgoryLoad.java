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
 * Custom API to manage Category to Publish and UN-Publish category
 * based on input Document and Category already exist.If category
 * preset in Input and but not in the system ManageAPI will be 
 * called to create.If the category not exist in InputDoc and but 
 * exist in System then Category will be unpublished.If category exist
 * in both system and input doc published to queue for ManageCategory.
 *
 */

public class IndgManageCatgoryLoad extends AbstractCustomApi {
  
  private static final String EMPTY_STRING = "";
  private static final String MANAGE_CATEGORY_UPLOADQ_FLOW = "Indg_CategoryFeed_Q";
  private static final String UN_PUBLISH_STATUS = "2000";
  private static final String ACTION_DELETE = "Delete";
  
  
  private String organizationCode = "";
  
  
  /**
   *  This method is invoke point of the service
   *  This calls manageDeltaLoadForDeletion method to
   *  get the list of Category that need to be un published 
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml)  {
   setOrganizationCode(inXml);
   YFCDocument categoryListApiOp = getCategoryList(EMPTY_STRING, organizationCode);
   Collection<String> unpublishCategoryIDList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion
       (inXml, categoryListApiOp,XMLLiterals.CATEGORY_ID, XMLLiterals.CATEGORY);
   addInputDocToManageCategory(inXml.getDocumentElement(),categoryListApiOp);
   manageUnPublishCategory(unpublishCategoryIDList,categoryListApiOp);
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
   private void addInputDocToManageCategory(YFCElement inputEle,YFCDocument categoryListApiOp){
     YFCIterable<YFCElement> yfsItator = inputEle.getChildren(XMLLiterals.CATEGORY);
     for(YFCElement categoryEle: yfsItator) {
       manageDeleteCategory(categoryEle.getAttribute(XMLLiterals.CATEGORY_ID),
           categoryEle.getAttribute(XMLLiterals.CATEGORY_PATH),categoryListApiOp);
       callManageCategoryQService(categoryEle);
     }
   }
   
   /**
    * This method calls custom server to drop Document 
    * to Queue
    * 
    * @param yfcInputDoc
    */
   private void callManageCategoryQService(YFCElement inEle) {
     YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
     inputDocForService.getDocumentElement().importNode(inEle);
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
      YFCElement inEle = XPathUtil.getXPathElement(categoryListAPIDoc, "/CategoryList/Category[@CategoryID=\""+categoryId+"\"]");
      if(!XmlUtils.isVoid(inEle)) {
        inEle.setAttribute(XMLLiterals.STATUS, UN_PUBLISH_STATUS);
        callManageCategoryQService(inEle);
      }
    }
   }
   
  /**
   * This method calls getCategoryList API and 
   * @param categoryId
   * @param org
   * @return
   */
   public YFCDocument getCategoryList(String categoryId, String org){
     return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
         IndgCategoryMasterUpload.formInputXmlForGetCategoryList(categoryId,org),IndgCategoryMasterUpload.formTemplateXmlForgetCategoryList());
   }
   
   /**
    * This method validate if the Path matches with the Input and system.
    * If not deletes the category from System and creates new one from
    * the input
    * 
    * @param categoryId
    * @param categoryPath
    * @param categoryListApiOp
    */
   public void manageDeleteCategory(String categoryId,String categoryPath, YFCDocument categoryListApiOp) {
     YFCElement categoryEle = XPathUtil.getXPathElement(categoryListApiOp,
         "/CategoryList/Category[@CategoryID=\""+categoryId+"\"]");
     if(!XmlUtils.isVoid(categoryEle) && !categoryPath.equals(categoryEle.getAttribute(XMLLiterals.CATEGORY_PATH))) {
       categoryEle.setAttribute(XMLLiterals.ACTION, ACTION_DELETE);
       callManageCategoryQService(categoryEle);
     }
   }
}
