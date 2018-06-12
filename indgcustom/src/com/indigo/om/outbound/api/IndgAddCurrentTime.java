package com.indigo.om.outbound.api;

import java.text.SimpleDateFormat;
/**
 * 
 * 
 * @author BSG170
 *
 */
import java.util.Date;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgAddCurrentTime extends AbstractCustomApi {
	 /**
     * This is the invoke point of the Service
     * @throws  
     * 
     */
  
    @Override
  public YFCDocument invoke(YFCDocument docInXml) {
    	String sLastUpdatedTime=setTime();
		return capacityDocument(docInXml,sLastUpdatedTime);
    }
    private static String setTime()
	{
		Date date = new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm:ssXXX");
		String dateString1=sdf1.format(date);
		String dateString2=sdf2.format(date);
		return dateString1+"T"+dateString2;
	}
	private static YFCDocument capacityDocument(YFCDocument docInXml,String sLastUpdatedTime)
	{
		YFCElement eleinXml=docInXml.getDocumentElement().getChildElement(XMLLiterals.NODE_CAPACITY);
		YFCElement eleNode=eleinXml.getChildElement(XMLLiterals.DATES);
		if(eleNode.hasChildNodes()){
			YFCIterable<YFCElement> eleDate = eleNode.getChildren();
			 for(YFCElement eleDateLines:eleDate) {
				 eleDateLines.setAttribute(XMLLiterals.LAST_UPDATED_TIME, sLastUpdatedTime);
			 }
		}
		return docInXml;
	}
}
