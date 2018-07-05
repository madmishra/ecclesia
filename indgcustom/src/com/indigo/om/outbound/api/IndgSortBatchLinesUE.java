package com.indigo.om.outbound.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

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
	
	private static final String HASH_VAL = "#";
	
	/**
	 * 
	 * This method is the invoke point of the service.
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		String inputDocString = inXml.toString();
	    YFCDocument docSortByItemID = YFCDocument.getDocumentFor(inputDocString);
		sortDocByDeptCodeItemId(docSortByItemID, inXml);
		return docSortByItemID;
	}

	/**
	 * This method fetches the departmentCode and itemId from the
	 * elements of input doc and stores it in the list and sorts the
	 * list.
	 * 
	 * @param docSortByItemID
	 * @param inXml
	 */
	
	private void sortDocByDeptCodeItemId(YFCDocument docSortByItemID, YFCDocument inXml) {
		YFCIterable<YFCElement> yfsItrator = docSortByItemID.getDocumentElement().getChildElement(XMLLiterals.STORE_BATCH_LINES)
				.getChildren();
		List<String> batchList = new ArrayList<>();
		for(YFCElement storeBatchLine: yfsItrator) {
			String departmentId = storeBatchLine.getAttribute(XMLLiterals.DEPARTMENT_CODE);
			String itemID = storeBatchLine.getAttribute(XMLLiterals.ITEM_ID);
			if((!XmlUtils.isVoid(departmentId)) && (!XmlUtils.isVoid(itemID))) {
				String deptItemId = departmentId.concat(HASH_VAL).concat(itemID);
				batchList.add(deptItemId);
				YFCNode parent = storeBatchLine.getParentNode();
				parent.removeChild(storeBatchLine);
			}
		}
		Collections.sort(batchList);
		afterSortRearrangedEle(docSortByItemID, batchList, inXml);
	}
	
	/**
	 * This method iterates through the sorted list and rearranges 
	 * the input document and returns it.
	 * 
	 * @param docdocSortByItemID
	 * @param batchList
	 * @param inXml
	 */
	
	private void afterSortRearrangedEle(YFCDocument docdocSortByItemID, List<String> batchList, YFCDocument inXml) {
		YFCIterable<YFCElement> yfsItrator = inXml.getDocumentElement().getChildElement(XMLLiterals.STORE_BATCH_LINES)
				.getChildren(XMLLiterals.STORE_BATCH_LINE);
		for (int i = 0; i < batchList.size(); i++) {
			String deptItemID = batchList.get(i);
			String[] segments = deptItemID.split(HASH_VAL);
			String department = segments[0];
			String itemId = segments[1];
			for(YFCElement batchLineEle : yfsItrator) {
				if((department.equals(batchLineEle.getAttribute(XMLLiterals.DEPARTMENT_CODE)) &&
						(itemId.equals(batchLineEle.getAttribute(XMLLiterals.ITEM_ID))))){
					YFCElement storeBatchEle = docdocSortByItemID.getDocumentElement().getChildElement(XMLLiterals.STORE_BATCH_LINES);
					storeBatchEle.importNode(batchLineEle);
				}
			}
		}
	}
}
