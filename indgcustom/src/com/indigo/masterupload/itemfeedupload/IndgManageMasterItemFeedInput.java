package com.indigo.masterupload.itemfeedupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * This service gets Item Feed from the file and drops
 * it into queue. 
 * 
 * @author BSG109
 *
 */
public class IndgManageMasterItemFeedInput extends AbstractCustomApi {

  private static final String ITEM_LOAD_Q = "Indg_ItemFeedQ";
  private static final String INDIGO_CA = "Indigo_CA";
  private static final String EACH = "EACH";
  
  /**
   * This is the starting point of the class
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    
    YFCElement inEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfsItrator = inEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle : yfsItrator) {
      YFCDocument itemListDoc = YFCDocument.createDocument(XMLLiterals.ITEM_LIST);
      YFCElement itemListEle = itemListDoc.getDocumentElement();
      itemEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, INDIGO_CA);
      itemEle.setAttribute(XMLLiterals.UNIT_OF_MEASURE, EACH);
      itemListEle.importNode(itemEle);
      invokeYantraService(ITEM_LOAD_Q, itemListDoc);
    }
    return inXml;
  }
}