package com.indigo.masterupload.itemfeedupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.masterupload.categoryupload.IndgCategoryMasterUpload;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSException;

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
  /**
   * This is the starting point of the class
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) throws YFSException {
    invokeYantraApi(XMLLiterals.MANAGE_ITEM, inXml);
    createCategoryItem(inXml);
    return inXml;
  }
  
  /**
   * This method calls modifyCategoryItem for creating categoryFeed
   * 
   * @param inXml
   */
  private void createCategoryItem(YFCDocument inXml){
    YFCElement itemEle = inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM);
    String categoryId = IndgManageItemFeed.getCategoryID(itemEle);
    YFCDocument categoryList = getCategoryList(categoryId, organizationCode);
    if(categoryList.getDocumentElement().hasChildNodes()) {
      String categoryPath = XPathUtil.getXpathAttribute(categoryList, 
          "/CategoryList/Category/@CategoryPath");
      invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, 
          IndgManageItemFeed.formInputDocForModifyCategoryItem(itemEle.getAttribute(XMLLiterals.ITEM_ID),CREATE_ACTION,categoryPath,organizationCode));
    }
  }
  
  /**
   * This method calls getCategoryList API
   * @param categoryId
   * @param org
   * @return
   */
   private YFCDocument getCategoryList(String categoryId, String org){
     return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
         IndgCategoryMasterUpload.formInputXmlForGetCategoryList(categoryId,org),
           IndgCategoryMasterUpload.formTemplateXmlForgetCategoryList());
   }
}
