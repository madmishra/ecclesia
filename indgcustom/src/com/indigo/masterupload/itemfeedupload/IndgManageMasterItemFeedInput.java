package com.indigo.masterupload.itemfeedupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * 
 * @author BSG109
 *
 */
public class IndgManageMasterItemFeedInput extends AbstractCustomApi {

  private static final String DEFAULT_UNIT_OF_MEASURE = "EACH";
  private static final String ORGANIZATION_CODE = "Indigo_CA";
  private static final String ITEM_UPLOAD_Q="ITEM_UPLOAD_Q";

  /**
   * This is the invoke point of the Server. This Method splits
   * each Item Element into separate Document
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    YFCElement inputEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfcItrator= inputEle.getChildren(XMLLiterals.ITEM);
    for(YFCElement itemEle: yfcItrator){
      itemEle.setAttribute(XMLLiterals.UNIT_OF_MEASURE, DEFAULT_UNIT_OF_MEASURE);
      itemEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, ORGANIZATION_CODE);
      YFCDocument itemFeedXml = YFCDocument.createDocument(XMLLiterals.ITEM_LIST);
      itemFeedXml.getDocumentElement().importNode(itemEle);
      invokeYantraService(getProperty(ITEM_UPLOAD_Q),itemFeedXml);
    }
    return inXml;
  }
}
