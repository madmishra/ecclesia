package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import java.util.Iterator;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * Custom API to consume the StoreBatchLinesUE input messages and 
 * sorts the StoreBatchLine(s) element by departmentCode followed 
 * by itemId internally in DeptCode in the document and returns
 * the sorted document.
 * 
 * @author BSG168
 *
 */

public class IndgSortBatchLinesUE extends AbstractCustomApi{
	
	/**
	 * 
	 * This method is the invoke point of the service.
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		String inputDocString = inXml.toString();
		YFCDocument docSortByItemID = YFCDocument.getDocumentFor(inputDocString);
	    sortNew(docSortByItemID);
		return docSortByItemID;
	}

	/**
	 * This method sorts the elements by departmentCode and internally by 
	 * ItemType
	 * @param inDoc
	 */
	
	private void sortNew(YFCDocument inDoc) {
		YFCElement rootElem = inDoc.getDocumentElement();
		YFCElement storeBatchLines= rootElem.getChildElement(XMLLiterals.STORE_BATCH_LINES);
		for(Iterator<YFCElement> itr=storeBatchLines.getChildren().iterator();itr.hasNext();) {
			YFCElement batchLine = itr.next();
			YFCElement itemDetailsElem = batchLine.getChildElement(XMLLiterals.ITEM_DETAILS,true);
			YFCElement primaryInfoElem = itemDetailsElem.getChildElement(XMLLiterals.PRIMARY_INFORMATION);
			if(primaryInfoElem.hasAttribute(XMLLiterals.ITEM_TYPE)) {
				batchLine.setAttribute(XMLLiterals.ITEM_TYPE, primaryInfoElem.getAttribute(XMLLiterals.ITEM_TYPE));
			}
		}
		String [] attrNames = new String[]{XMLLiterals.DEPARTMENT_CODE,XMLLiterals.ITEM_TYPE};
		storeBatchLines.sortChildren(attrNames);
	}
}
