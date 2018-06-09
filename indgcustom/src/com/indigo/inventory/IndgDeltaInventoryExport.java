package com.indigo.inventory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.date.YDate;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;


/**
 * This custom class convert the RTAM message to
 * requested changes
 * 
 * 
 * @author BSG109
 *
 */
public class IndgDeltaInventoryExport extends AbstractCustomApi{

  
  private static final String INDG_DELTA_EXPORT_Q = "INDG_DELTA_EXPORT_Q";

  /**
   * This is the invoke point of the service
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    YFCElement inEle = inXml.getDocumentElement();
    YFCIterable<YFCElement> yfsItr = inEle.getChildren(XMLLiterals.AVAILABILITY_CHANGE);
    for(YFCElement availabilityEle : yfsItr) {
      if(!XmlUtils.isVoid(availabilityEle.getAttribute(XMLLiterals.NODE))) {
        String inputString = availabilityEle.toString();
        YFCDocument invExp = YFCDocument.getDocumentFor(inputString);
        YTimestamp ts = availabilityEle.getYTimestampAttribute("OnhandAvailableDate");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		System.out.println(format.format(ts)+"Date"+ts);
        String onHnadAvaDate = format.format(ts);
        availabilityEle.setAttribute("OnhandAvailableDate", onHnadAvaDate);
        invokeYantraService(getProperty(INDG_DELTA_EXPORT_Q), invExp);
      }
    }
    return inXml;
  } 

}
