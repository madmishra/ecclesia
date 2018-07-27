package com.indigo.masterupload.itemfeedupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
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
  private static final String DEFAULT_ATP_RULE = "DEFAULT";
  private static final String NODE_LEVEL_INV_MONITOR_RULE = "DEFAULT_RTAM_RULE";
  private static final String CATEGORY_ITEM_CREATION_REQ = "CATEGORY_ITEM_CREATION_REQ";
  private static final String INDG_CATEGORY_ITEM_Q = "INDG_CATEGORY_ITEM_Q";
  private static final String EMPTY_STRING = "";
  private static final String YES = "Y";
  
  /**
   * This is the starting point of the class
   * 
   */
  
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    YFCElement itemEle = inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM)
      .createChild(XMLLiterals.INVENTORY_PARAMETERS);
    itemEle.setAttribute(XMLLiterals.ATP_RULE, DEFAULT_ATP_RULE);
    itemEle.setAttribute(XMLLiterals.NODE_LEVEL_INVENTORY_MONITOR_RULE, NODE_LEVEL_INV_MONITOR_RULE);
    setItemType(itemEle);
    invokeYantraApi(XMLLiterals.MANAGE_ITEM, inXml);
    if(getProperty(CATEGORY_ITEM_CREATION_REQ).equals(YES))
    	{
    		invokeYantraService(INDG_CATEGORY_ITEM_Q, inXml);
    	}
    return inXml;
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