package com.indigo.masterupload.itemfeedupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
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
    String inputItemID = itemEle.getAttribute(itemEle.getAttribute(XMLLiterals.ITEM_ID));
    YFCDocument itemListOp = getItemList(inputItemID);
    if(itemListOp.getDocumentElement().hasChildNodes()) {
      String opSyncTS = itemListOp.getDocumentElement().getChildElement(XMLLiterals.ITEM)
          .getAttribute(XMLLiterals.SYNC_TS);
      if(!itemEle.getAttribute(XMLLiterals.SYNC_TS).equals(opSyncTS)){
        
        invokeYantraApi(XMLLiterals.MANAGE_ITEM, inXml);
        return;
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
        inputXml, formTemplateForGetItemList());
  }
  
  /**
   * 
   * This method forms template XML for getItemList
   * 
   * @return
   */
  public static YFCDocument formTemplateForGetItemList() {
    YFCDocument inXml = YFCDocument.createDocument(XMLLiterals.ITEM_LIST);
    YFCElement inEle = inXml.getDocumentElement().createChild(XMLLiterals.ITEM);
    inEle.setAttribute(XMLLiterals.ITEM_ID, EMPTY_STRING);
    inEle.setAttribute(XMLLiterals.SYNC_TS, EMPTY_STRING);
    inEle.createChild(XMLLiterals.CATEGORY_LIST).createChild(XMLLiterals.CATEGORY)
      .setAttribute(XMLLiterals.CATEGORY_ID,EMPTY_STRING);
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
      String orgCode = getProperty(XMLLiterals.ORGANIZATION_CODE);
      YFCDocument categoryList = getCategoryList(inputCategoryID,
          orgCode);
      categoryPath = getProperty(DEFAULT_CATEGORY_PATH);
      if(!categoryList.getDocumentElement().hasChildNodes()) { 
        createCategory(inputCategoryID,orgCode);
      } else {
        categoryPath = XPathUtil.getXpathAttribute(categoryList, "/CategoryList/Category/@CategoryPath");
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
         IndgCategoryMasterUpload.formInputXmlForGetCategoryList(categoryId,org),
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
     invokeYantraApi(XMLLiterals.CREATE_CATEGORY, 
         IndgCategoryMasterUpload.formInputXmlForCreateCategory(categoryId,
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
     if(action.equals(CREATE_ACTION)){
       manageCategory(itemEle);
     }
     invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, 
         formInputDocForModifyCategoryItem(itemEle.getAttribute(XMLLiterals.ITEM_ID),action,categoryPath,getProperty(XMLLiterals.ORGANIZATION_CODE)));
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
   public static YFCDocument formInputDocForModifyCategoryItem(String itemID,String action,String catPath,String orgCode){
     YFCDocument modifyCategory = YFCDocument.createDocument(XMLLiterals.MODIFY_CATEGORY_ITEMS);
     YFCElement modifyCategoryEle = modifyCategory.getDocumentElement();
     modifyCategoryEle.setAttribute(XMLLiterals.CALLING_ORGANIZATION_CODE, EMPTY_STRING);
     YFCElement categoryEle = modifyCategoryEle.createChild(XMLLiterals.CATEGORY);
     categoryEle.setAttribute(XMLLiterals.CATEGORY_PATH, catPath);
     categoryEle.setAttribute(XMLLiterals.ORGANIZATION_CODE,orgCode);
     YFCElement categoryItem = categoryEle.createChild(XMLLiterals.CATEGORY_ITEM_LIST)
         .createChild(XMLLiterals.CATEGORY_ITEM);
     categoryItem.setAttribute(XMLLiterals.ACTION, action);
     categoryItem.setAttribute(XMLLiterals.ITEM_ID, itemID);
     categoryItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, DEFAULT_UNIT_OF_MEASURE);
     return modifyCategory;
   }
   
   
   
   
  
}