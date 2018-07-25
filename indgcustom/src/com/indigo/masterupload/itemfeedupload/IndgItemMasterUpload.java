package com.indigo.masterupload.itemfeedupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.masterupload.categoryupload.IndgCategoryMasterUpload;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * Custom API for Initial Item Master Upload. Item will also be
 * added to ItemCategory based in Input
 * 
 * @author BSG109
 *
 */
public class IndgItemMasterUpload extends AbstractCustomApi {

  String organizationCode="Indigo_CA";
  
  
  private static final String CREATE_ACTION = "Create";
  private static final String DEFAULT_ATP_RULE = "DEFAULT";
  private static final String NODE_LEVEL_INV_MONITOR_RULE = "DEFAULT_RTAM_RULE";
  private static final String DEFAULT_CATEGORY_PATH = "/IndigoInStoreCategory/L1-Books";
  private static final String EMPTY_STRING = "";
  private static final String CATEGORY_ALERT_FLOW="Indg_AlertForCategoryItem";
  
  /**
   * This is the starting point of the class
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    YFCElement invEle = inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM)
      .createChild(XMLLiterals.INVENTORY_PARAMETERS);
    invEle.setAttribute(XMLLiterals.ATP_RULE, DEFAULT_ATP_RULE);
    invEle.setAttribute(XMLLiterals.NODE_LEVEL_INVENTORY_MONITOR_RULE, NODE_LEVEL_INV_MONITOR_RULE);
    invokeYantraApi(XMLLiterals.MANAGE_ITEM, inXml);
    createCategoryItem(inXml);
    return inXml;
  }
  
  /**
   * This method calls modifyCategoryItem for creating categoryFeed
   * 
   * @param inXml
   */
  private void createCategoryItem(YFCDocument inXml) {
    YFCElement itemEle = inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM);
    String categoryId = IndgManageItemFeed.getCategoryID(itemEle);
    if(!XmlUtils.isVoid(categoryId)) {
      YFCDocument categoryList = getCategoryList(categoryId, organizationCode);
      if(categoryList.getDocumentElement().hasChildNodes()) {
        String categoryPath = XPathUtil.getXpathAttribute(categoryList, 
          "/CategoryList/Category/@CategoryPath");
        invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, 
          IndgManageItemFeed.getInputDocForModifyCategoryItem(itemEle.getAttribute(XMLLiterals.ITEM_ID)
              ,CREATE_ACTION,categoryPath,organizationCode));
        } else {
        invokeYantraService(getProperty(CATEGORY_ALERT_FLOW), inXml);
      }
    } else {
      invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, 
          IndgManageItemFeed.getInputDocForModifyCategoryItem(itemEle.getAttribute(XMLLiterals.ITEM_ID)
              ,CREATE_ACTION,getProperty(DEFAULT_CATEGORY_PATH),organizationCode));
    }
    setItemType(itemEle);
  }
  
  /**
   * This method calls getCategoryList API
   * @param categoryId
   * @param org
   * @return
   */
   private YFCDocument getCategoryList(String categoryId, String org) {
     return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
         IndgCategoryMasterUpload.getInputXmlForGetCategoryList(categoryId,org,EMPTY_STRING),
           IndgCategoryMasterUpload.formTemplateXmlForgetCategoryList());
   }
   
   
   /**
    * 
    * This method sets ItemType from commodity code attribute
    * from the item element. 
    * 
    * @param itemEle
    */
   public static void setItemType(YFCElement itemEle) {
     if(!XmlUtils.isVoid(itemEle.getChildElement(XMLLiterals.CLASSIFICATION_CODES))) {
       String categoryID = itemEle.getChildElement(XMLLiterals.CLASSIFICATION_CODES)
           .getAttribute(XMLLiterals.COMMODITY_CODE,EMPTY_STRING);
       if(!XmlUtils.isVoid(categoryID)) {
         itemEle.getChildElement(XMLLiterals.PRIMARY_INFORMATION)
         .setAttribute(XMLLiterals.ITEM_TYPE,categoryID);
       }
     }
   }
}