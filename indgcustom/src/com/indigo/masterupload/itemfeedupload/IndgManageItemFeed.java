package com.indigo.masterupload.itemfeedupload;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.masterupload.categoryupload.IndgCategoryMasterUpload;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * Custom API to manage Item Feed and Item Delta feed.
 * 
 * @author BSG109
 *
 */
public class IndgManageItemFeed extends AbstractCustomApi{

  private static final String EMPTY_STRING = "";
  private static final String CATEGORY_DOMAIN = "CategoryDomain";
  private static final String DEFAULT_CATEGORY_PATH = "DefaultCategoryPath";
  private static final String CREATE_ACTION = "Create";
  private static final String DEFAULT_UNIT_OF_MEASURE = "EACH";
  private static final String DELETE_ACTION = "Delete";
  private static final String ORGANIZATION_CODE = "Indigo_CA";
  private String inputCategoryID=""; 
  private String categoryPath = "";
  
  

  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    YFCElement inputEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfcItrator= inputEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle: yfcItrator){
      manageItem(itemEle,inXml);
    }
    return inXml;
  }
  
  /**
   * This method is the starting for the Manage Item
   * Operation for Initial Load and Delta Load
   * 
   * @param inXml
   */
  private void manageItem(YFCElement itemEle,YFCDocument inXml) {
    String inputItemID = itemEle.getAttribute(XMLLiterals.ITEM_ID);
    YFCDocument itemListOp = getItemList(inputItemID);
    if(itemListOp.getDocumentElement().hasChildNodes()) {
      String opSyncTS = itemListOp.getDocumentElement().getChildElement(XMLLiterals.ITEM)
          .getAttribute(XMLLiterals.SYNC_TS);
      if(validateItemUpdate(itemEle.getAttribute(XMLLiterals.SYNC_TS),opSyncTS)){
        if(!isItemAssignedToCategory(itemListOp.getDocumentElement().getChildElement(XMLLiterals.ITEM))){
          modifyCategoryItem(itemEle,CREATE_ACTION);
        } else {
          validateCategoryItempath(itemEle,itemListOp);
        }
        invokeYantraApi(XMLLiterals.MANAGE_ITEM, inXml);
      }
    } else {
      invokeYantraApi(XMLLiterals.MANAGE_ITEM, inXml);
      modifyCategoryItem(itemEle,CREATE_ACTION);
    }
  }
  
  /**
   * 
   * This method calls getItemList API
   * @param itemID
   * @return
   */
  private YFCDocument getItemList(String itemID) {
    YFCDocument inputXml = YFCDocument.createDocument(XMLLiterals.ITEM);
    inputXml.getDocumentElement().setAttribute(XMLLiterals.ITEM_ID, itemID);
    return invokeYantraApi(XMLLiterals.GET_ITEM_LIST_API, 
        inputXml, getTemplateForGetItemList());
  }
  
  /**
   * 
   * This method forms template XML for getItemList
   * 
   * @return
   */
  public static YFCDocument getTemplateForGetItemList() {
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.ITEM_LIST);
    YFCElement inEle = inXml.getDocumentElement().createChild(XMLLiterals.ITEM);
    inEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
    inEle.setAttribute(XMLLiterals.SYNC_TS, EMPTY_STRING);
    inEle.setAttribute(XMLLiterals.PRODUCT_LINE, EMPTY_STRING);
    inEle.createChild(XMLLiterals.CLASSIFICATION_CODES)
      .setAttribute(XMLLiterals.COMMODITY_CODE, EMPTY_STRING);
    YFCElement categoryEle = inEle.createChild(XMLLiterals.CATEGORY_LIST).createChild(XMLLiterals.CATEGORY);
    categoryEle.setAttribute(XMLLiterals.CATEGORY_ID,EMPTY_STRING);
    categoryEle.setAttribute(XMLLiterals.CATEGORY_PATH,EMPTY_STRING);
    return inXml;
  }
  
  /**
   * 
   * This method validates and creates CategoryItem for the Category
   * Mentioned in the input. Creates Dummy Category if  category is
   * not available in the system
   * @param inXml
   */
  private void manageCategory(YFCElement itemEle) {
    inputCategoryID = getCategoryID(itemEle);
    if(!XmlUtils.isVoid(inputCategoryID)){
      YFCDocument categoryList = getCategoryList(inputCategoryID,
          ORGANIZATION_CODE);
      if(categoryList.getDocumentElement().hasChildNodes()) {
        categoryPath = XPathUtil.getXpathAttribute(categoryList, "/CategoryList/Category/@CategoryPath");
      }else{
        categoryPath = getProperty(DEFAULT_CATEGORY_PATH);
        createCategory(inputCategoryID,ORGANIZATION_CODE);
      }
    }
  }

  /**
   * 
   * This method is to get CategoryID from Input Item Element
   * @param itemEle
   * @return
   */
  public static String getCategoryID(YFCElement itemEle){
    String categoryID = EMPTY_STRING;
    if(!XmlUtils.isVoid(itemEle.getChildElement(XMLLiterals.CLASSIFICATION_CODES))) {
        categoryID = itemEle.getChildElement(XMLLiterals.CLASSIFICATION_CODES)
            .getAttribute(XMLLiterals.COMMODITY_CODE);
        if(XmlUtils.isVoid(categoryID)) {
          categoryID = itemEle.getAttribute(XMLLiterals.PRODUCT_LINE);
        }
    }
    return categoryID;
  }
  
  
  /**
   * This method calls getCategoryList API and 
   * @param categoryId
   * @param org
   * @return
   */
   public YFCDocument getCategoryList(String categoryId, String org){
     return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
         IndgCategoryMasterUpload.getInputXmlForGetCategoryList(categoryId,org,EMPTY_STRING),
           IndgCategoryMasterUpload.formTemplateXmlForgetCategoryList());
   }
   
   /**
    * This method call createCategoryAPI from passed input
    * 
    * @param orgCode
    * @param categoryId
    */
   public void createCategory(String categoryId,String orgCode) {
     String categoryDomain = getProperty(CATEGORY_DOMAIN);
     categoryPath = categoryPath+"/"+categoryId;
     invokeYantraApi(XMLLiterals.CREATE_CATEGORY, 
         IndgCategoryMasterUpload.getInputXmlForCreateCategory(categoryId,
             categoryDomain,categoryPath,orgCode));
   }
   
   
  /**
   * 
   * 
   * @param action
   * @param itemEle
   * 
   */
   public void modifyCategoryItem(YFCElement itemEle,String action){
     if(CREATE_ACTION.equals(action)) {
       manageCategory(itemEle);
     }
     invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, 
         getInputDocForModifyCategoryItem(itemEle.getAttribute(XMLLiterals.ITEM_ID),action,
             categoryPath,ORGANIZATION_CODE));
   }
   
   /**
    * 
    * 
    * @param action
    * @param itemID
    * @param catPath
    * @param orgCode
    * @return
    */
   public static YFCDocument getInputDocForModifyCategoryItem(String itemID,String action,String catPath,String orgCode){
     YFCDocument modifyCategory = YFCDocument.createDocument(XMLLiterals.MODIFY_CATEGORY_ITEMS);
     YFCElement modifyCategoryEle = modifyCategory.getDocumentElement();
     modifyCategoryEle.setAttribute(XMLLiterals.CALLING_ORGANIZATION_CODE, orgCode);
     YFCElement categoryEle = modifyCategoryEle.createChild(XMLLiterals.CATEGORY);
     categoryEle.setAttribute(XMLLiterals.CATEGORY_PATH, catPath);
     categoryEle.setAttribute(XMLLiterals.ORGANIZATION_CODE,orgCode);
     YFCElement categoryItem = categoryEle.createChild(XMLLiterals.CATEGORY_ITEM_LIST)
         .createChild(XMLLiterals.CATEGORY_ITEM);
     categoryItem.setAttribute(XMLLiterals.ACTION, action);
     categoryItem.setAttribute(XMLLiterals.ITEM_ID, itemID);
     categoryItem.setAttribute(XMLLiterals.ORGANIZATION_CODE,orgCode);
     categoryItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, DEFAULT_UNIT_OF_MEASURE);
     return modifyCategory;
   }
   
   /**
    * This method validates if the Item is assigned to 
    * any Category
    * 
    * @param itemEle
    * @return
    */
   private boolean isItemAssignedToCategory(YFCElement itemEle) {
     if(itemEle.getChildElement(XMLLiterals.CATEGORY_LIST).hasChildNodes()) {
       return true;
     }
     return false;
   }
   
   /**
    * This method validates and manage categoryItem
    * 
    * @param inItemEle
    * @param opListItemEle
    */
   private void validateCategoryItempath(YFCElement inItemEle, YFCDocument opListItemDoc) {
    
     String ipItemCategoryId = getCategoryID(inItemEle);
     YFCElement opListItemEle = opListItemDoc.getDocumentElement()
         .getChildElement(XMLLiterals.ITEM);
     String opListCategoryId =  XPathUtil.getXpathAttribute(opListItemDoc, "//Category/@CategoryID");
     categoryPath =  XPathUtil.getXpathAttribute(opListItemDoc, "//Category/@CategoryPath");
     if(!ipItemCategoryId.equals(opListCategoryId)) {
       modifyCategoryItem(opListItemEle,DELETE_ACTION);
       modifyCategoryItem(inItemEle,CREATE_ACTION);
     }
   }
   
   
   /**
    * 
    * 
    * 
    * @param inputSyncTS
    * @param outputSyncTS
    * @return
    */
   private boolean validateItemUpdate(String inputSyncTS, String outputSyncTS) {
       try{
         if(XmlUtils.isVoid(inputSyncTS) && XmlUtils.isVoid(outputSyncTS)){
          String synctsIn = inputSyncTS.substring(0,10)+" "+inputSyncTS.substring(11,19);
          String synctsOp = outputSyncTS.substring(0,10)+" "+outputSyncTS.substring(11,19);
          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
          Date date1 = format.parse(synctsIn);
          Date date2 = format.parse(synctsOp);
          long difference = date2.getTime() - date1.getTime();
          if(difference < 0) {
            return true;
          }
         }
       }
       catch(Exception exp) {
       throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_SQL_EXP, exp);
     }
     return false;
   }
}