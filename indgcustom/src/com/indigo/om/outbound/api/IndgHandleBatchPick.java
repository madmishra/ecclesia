package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.XMLLiterals;
import com.yantra.pca.ycd.japi.ue.YCDhandleShortageForBatchLineUE;
import com.yantra.pca.ycd.utils.YCDUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.ui.backend.util.APIManager;
import com.yantra.yfc.util.YFCDoubleUtils;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document; 


public class IndgHandleBatchPick implements YCDhandleShortageForBatchLineUE {
	
	private static YFCLogCategory cat = YFCLogCategory.instance(IndgHandleBatchPick.class);
	private static final String SHORTAGE = "shortage";
	private static final String DAMAGED = "damaged";
	private static final String PICK_LATER = "picklater";
	private static final String SUCCESS = "Success";
	private static final String ASSIGNED_TO_USERID = "AssignedToUserId";
	private static final String STATUS_VAL = "1100.70.06.10";
	private static final String BASE_STATUS_VAL = "1100.70.06.20";
	private static final String TRANSACTION_VAL = "YCD_BACKROOM_PICK_IN_PROGRESS";
	private static final String CHANGE_SHIPMENT_STATUS = "changeShipmentStatus";
	private static final String CALL_ALERT = "callGetShipmentLineList";
	private static final String EMPTY_STRING = "";
	private static final String CANCEL = "Cancel";
	private static final String ZERO = "0";
	private static final String MODIFY = "Modify";
	private static final String YES = "Y";
	  
	public Document handleShortageForBatchLine(YFSEnvironment env, Document inXML) throws YFSUserExitException {
		YFCDocument docInput = YFCDocument.getDocumentFor(inXML);
	    YFCElement inputElem = YFCDocument.getDocumentFor(inXML).getDocumentElement();
	    String shortageReason = inputElem.getChildElement(XMLLiterals.ITEM).getAttribute(XMLLiterals.SHORTAGE_REASON);
	    YFCElement shipmentLines = inputElem.getChildElement(XMLLiterals.SHIPMENT_LINES);
	    if (YFCObject.equals(shortageReason, DAMAGED) || (YFCObject.equals(shortageReason, SHORTAGE))) {
	    	handleDamagedItems(env, inputElem, shipmentLines,shortageReason,docInput);
	    }
	    else if (YFCObject.equals(shortageReason, PICK_LATER)) {
	    	handlePickLater(env, shipmentLines, docInput);
	    }
	    return YFCDocument.createDocument(SUCCESS).getDocument();
	}
	  
	private void handlePickLater(YFSEnvironment env, YFCElement shipmentLines, YFCDocument docInput) {
		YFCIterable<YFCElement> itr = shipmentLines.getChildren(XMLLiterals.SHIPMENT_LINE);
	    Map<String, YFCElement> changeShipmentInputMap = new HashMap<>();
	    while (itr.hasNext()) {
	    	YFCElement changeShipmentInput = null;
	    	YFCElement oShipmentLine = itr.next();
	    	String shipmentKey = oShipmentLine.getAttribute(XMLLiterals.SHIPMENT_KEY);
	    	String shipmentLineKey = oShipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_KEY);
	    	if (changeShipmentInputMap.containsKey(shipmentKey)) {
	    		changeShipmentInput = changeShipmentInputMap.get(shipmentKey);
	    		setChangeShipmentInputForPickLater(changeShipmentInput, shipmentLineKey);
	    	}
	    	else {
	    		changeShipmentInput = prepareChangeShipmentInputForPickLater(shipmentKey);
	    		setChangeShipmentInputForPickLater(changeShipmentInput, shipmentLineKey);
	    		changeShipmentInputMap.put(shipmentKey, changeShipmentInput);
	    	}
	    }
	    if (changeShipmentInputMap.size() > 0) {
	    	callChangeShipmentForAllShipments(env, changeShipmentInputMap, docInput);
	    }
	}
	  
	private void handleDamagedItems(YFSEnvironment env, YFCElement inputElem, YFCElement shipmentLines, String shortageReason, YFCDocument docInput) {
		ArrayList<String> cancellableShipments = new ArrayList<>();
	    Map<String, YFCElement> changeShipmentInputMap = new HashMap<>();
	    ArrayList<String> shipmentKeyList = new ArrayList<>();
	    Map<String, YFCElement> shipmentDetailsMap = new HashMap<>();
	    YFCIterable<YFCElement> itr = shipmentLines.getChildren(XMLLiterals.SHIPMENT_LINE);
	    while (itr.hasNext()) {
	    	YFCElement changeShipmentWithBackorderInput = null;
	    	YFCElement oShipmentLine = itr.next();
	    	YFCElement oShipment = oShipmentLine.getChildElement(XMLLiterals.SHIPMENT);
	    	String shipmentKey = oShipmentLine.getAttribute(XMLLiterals.SHIPMENT_KEY);
	    	shipmentDetailsMap.put(shipmentKey, oShipment);
	    	String shipmentLineKey = oShipmentLine.getAttribute(XMLLiterals.SHIPMENT_LINE_KEY);
	    	double shortageQuantity = oShipmentLine.getDoubleAttribute(XMLLiterals.QUANTITY) - oShipmentLine.getDoubleAttribute(XMLLiterals.BACKROOM_PICK_QUANTITY);
	    	if (shortageQuantity > 0.0D) {
	    		double newQuantity = oShipmentLine.getDoubleAttribute(XMLLiterals.QUANTITY) - shortageQuantity;
	    		double shipmentLineShortQty = oShipmentLine.getDoubleAttribute(XMLLiterals.ORIGINAL_QTY) - newQuantity;
	    		if (YFCDoubleUtils.equal(newQuantity, 0.0D)) {
	    			if (!cancellableShipments.contains(shipmentKey)) {
	    				cancellableShipments.add(shipmentKey);
	    			}
	    		}
	    		else if (!shipmentKeyList.contains(shipmentKey)) {
	    			shipmentKeyList.add(shipmentKey);
	    		}
	    		if (changeShipmentInputMap.containsKey(shipmentKey)) {
	    			changeShipmentWithBackorderInput = changeShipmentInputMap.get(shipmentKey);
	    			setChangeShipmentWithBackorderInput(changeShipmentWithBackorderInput, shipmentLineKey, newQuantity, shipmentLineShortQty);
	    		}
	    		else {
	    			changeShipmentWithBackorderInput = prepareChangeShipmentInput(shipmentKey);
	    			setChangeShipmentWithBackorderInput(changeShipmentWithBackorderInput, shipmentLineKey, newQuantity, shipmentLineShortQty);
	    			changeShipmentInputMap.put(shipmentKey, changeShipmentWithBackorderInput);
	    		}
	    	}
	    }
	    if (changeShipmentInputMap.size() > 0) {
	    	callChangeShipmentForAllShipments(env ,changeShipmentInputMap, docInput);
	    }
	    Iterator<String> it;
	    if (cancellableShipments.size() > 0) {
	    	for (it = cancellableShipments.iterator(); it.hasNext();) {
	    		String shipmentKey = it.next();
	    		boolean shipmentCancelled = checkShipmentForCancellation(env, shipmentKey);
	    		if ((!shipmentCancelled) && (!shipmentKeyList.contains(shipmentKey))) {
	    			shipmentKeyList.add(shipmentKey);
	    		}
	    	}
	    }
	    for (Iterator<String> iterator = shipmentKeyList.iterator(); iterator.hasNext();) {
	    	String shKey = iterator.next();
	    	YFCElement shipmentDetailsElem = shipmentDetailsMap.get(shKey);
	    	callChangeShipmentStatus(env, shKey, shipmentDetailsElem, inputElem.getAttribute(ASSIGNED_TO_USERID));
	    }
	}
	  
	private void callChangeShipmentStatus(YFSEnvironment env, String shipmentKey, YFCElement shipmentDetails, String userId) {
		String status = shipmentDetails.getAttribute(XMLLiterals.STATUS);
	    if (status.contains(STATUS_VAL)) {
	    	YFCDocument changeShipmentStatusInputDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    	YFCElement changeShipmentStatusInput = changeShipmentStatusInputDoc.getDocumentElement();
	    	changeShipmentStatusInput.setAttribute(XMLLiterals.BASE_DROP_STATUS, BASE_STATUS_VAL);
	    	changeShipmentStatusInput.setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentKey);
	    	changeShipmentStatusInput.setAttribute(XMLLiterals.TRANSACTION_ID, TRANSACTION_VAL);
	    	YFCDocument changeShipmentStatusTempateDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    	YFCElement changeShipmentStatusTempateElem = changeShipmentStatusTempateDoc.getDocumentElement();
	    	changeShipmentStatusTempateElem.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
	    	if (cat.isDebugEnabled()) {
	    		cat.debug("Input to changeShipmentStatus:" + changeShipmentStatusInputDoc);
	    	}
	    	YFCElement apiElem = YFCDocument.createDocument(XMLLiterals.API).getDocumentElement();
	    	apiElem.setAttribute(XMLLiterals.NAME, CHANGE_SHIPMENT_STATUS);
	    	APIManager.getInstance().invokeAPI(env, apiElem, changeShipmentStatusInput, changeShipmentStatusTempateElem);
	    	YFCDocument changeShipmentInputDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    	YFCElement changeShipmentInput = changeShipmentInputDoc.getDocumentElement();
	    	changeShipmentInput.setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentKey);
	    	changeShipmentInput.setAttribute(ASSIGNED_TO_USERID, userId);
	    	YFCDocument changeShipmentTemplateDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    	YFCElement changeShipmentTemplateElem = changeShipmentTemplateDoc.getDocumentElement();
	    	changeShipmentStatusTempateElem.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
	    	if (cat.isDebugEnabled()) {
	    		cat.debug("Input to changeShipment:" + changeShipmentInputDoc);
	    	}
	    	YFCElement apiElement = YFCDocument.createDocument(XMLLiterals.API).getDocumentElement();
	    	apiElement.setAttribute(XMLLiterals.NAME, XMLLiterals.CHANGE_SHIPMENT);
	    	APIManager.getInstance().invokeAPI(env, apiElement, changeShipmentInput, changeShipmentTemplateElem);
	    }
	}
	  
	private boolean checkShipmentForCancellation(YFSEnvironment env, String shipmentKey) {
		cat.beginTimer(CALL_ALERT);
	    YFCDocument getShipmentLineListInputDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINE);
	    YFCElement getShipmentLineListElem = getShipmentLineListInputDoc.getDocumentElement();
	    getShipmentLineListElem.setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentKey);
	    YFCDocument getShipmentLineListTemplateDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT_LINES);
	    YFCElement getShipmentLineListTemplate = getShipmentLineListTemplateDoc.getDocumentElement();
	    getShipmentLineListTemplate.setAttribute(XMLLiterals.TOTAL_NO_OF_RECORDS, EMPTY_STRING);
	    if (cat.isDebugEnabled()) {
	    	cat.debug("Input to getShipmentLineList:" + getShipmentLineListElem);
	    	cat.debug("Template to getShipmentLineList:" + getShipmentLineListTemplateDoc);
	    }
	    YFCElement apiElem = YFCDocument.createDocument(XMLLiterals.API).getDocumentElement();
	    apiElem.setAttribute(XMLLiterals.NAME, XMLLiterals.GET_SHIPMENT_LINE_LIST);
	    YFCElement getShipmentLineListOutElem = APIManager.getInstance().invokeAPI(env, apiElem, getShipmentLineListElem, getShipmentLineListTemplate);
	    if (cat.isDebugEnabled()) {
	    	cat.debug("Output from getShipmentLineList:" + getShipmentLineListOutElem);
	    }
	    int noOfShipmentLines = getShipmentLineListOutElem.getIntAttribute(XMLLiterals.TOTAL_NO_OF_RECORDS);
	    getShipmentLineListElem.setAttribute(XMLLiterals.QUANTITY, ZERO);
	    YFCElement getShipmentLineListOutputElem = APIManager.getInstance().invokeAPI(env, apiElem, getShipmentLineListElem, getShipmentLineListTemplate);
	    int totalNumberOfRecords = getShipmentLineListOutputElem.getIntAttribute(XMLLiterals.TOTAL_NO_OF_RECORDS);
	    if (totalNumberOfRecords == noOfShipmentLines) {
	    	YFCElement changeShipmentInput = YFCDocument.createDocument(XMLLiterals.SHIPMENT).getDocumentElement();
	    	changeShipmentInput.setAttribute(XMLLiterals.ACTION, CANCEL);
	    	changeShipmentInput.setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentKey);
	    	YFCDocument changeShipmentOutputDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    	YFCElement changeShipmentOutputElem = changeShipmentOutputDoc.getDocumentElement();
	    	changeShipmentOutputElem.setAttribute(XMLLiterals.STATUS, EMPTY_STRING);
	    	YFCElement apiElement = YFCDocument.createDocument(XMLLiterals.API).getDocumentElement();
	    	apiElement.setAttribute(XMLLiterals.NAME, XMLLiterals.CHANGE_SHIPMENT);
	    	YFCElement changeShipmentForCancelOutElem = APIManager.getInstance().invokeAPI(env, apiElement, changeShipmentInput, changeShipmentOutputElem);
	    	if (cat.isDebugEnabled()) {
	    		cat.debug("Output from changeShipment:" + changeShipmentForCancelOutElem);
	    	}
	    	return true;
	    }
	    return false;
	}
	  
	private YFCElement prepareChangeShipmentInput(String shipmentKey) {
	    YFCElement changeShipmentWithBackorderInput = YFCDocument.createDocument(XMLLiterals.SHIPMENT).getDocumentElement();
	    changeShipmentWithBackorderInput.setAttribute(XMLLiterals.ACTION, MODIFY);
	    changeShipmentWithBackorderInput.setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentKey);
	    changeShipmentWithBackorderInput.setAttribute(XMLLiterals.BACK_ORDER_REMOVED_QTY, YES);
	    return changeShipmentWithBackorderInput;
	}
	  
	private void setChangeShipmentWithBackorderInput(YFCElement changeShipmentWithBackorderInput, String shipmentLineKey, double quantity, double shortageQuantity) {
	    YFCElement csShipmentLines = changeShipmentWithBackorderInput.getChildElement(XMLLiterals.SHIPMENT_LINES, true);
	    YFCElement csShipmentLine = YCDUtils.getLineInXml(csShipmentLines, XMLLiterals.SHIPMENT_LINE, XMLLiterals.SHIPMENT_LINE_KEY, shipmentLineKey, true);
	    csShipmentLine.setDoubleAttribute(XMLLiterals.QUANTITY, quantity);
	    csShipmentLine.setDoubleAttribute(XMLLiterals.SHORTAGE_QTY, shortageQuantity);  
	}
	  
	private void callChangeShipmentForAllShipments(YFSEnvironment env, Map<String, YFCElement> changeShipmentInputMap, YFCDocument docInput) {
	    IndgCancelOrderInBatchPick oBatchPick = new IndgCancelOrderInBatchPick(env);
		oBatchPick.handleNodeInventory(docInput);
	    YFCDocument changeShipmentTemplateDoc = YFCDocument.createDocument(XMLLiterals.SHIPMENT);
	    YFCElement changeShipmentTemplate = changeShipmentTemplateDoc.getDocumentElement();
	    changeShipmentTemplate.setAttribute(XMLLiterals.SHIPMENT_KEY, EMPTY_STRING);
	    Iterator<String> it = changeShipmentInputMap.keySet().iterator();
	    while (it.hasNext()) {
	    	YFCElement changeShipmentInput = changeShipmentInputMap.get(it.next());
	    	YFCElement apiElem = YFCDocument.createDocument(XMLLiterals.API).getDocumentElement();
	    	apiElem.setAttribute(XMLLiterals.NAME, XMLLiterals.CHANGE_SHIPMENT);
	    	APIManager.getInstance().invokeAPI(env, apiElem, changeShipmentInput, changeShipmentTemplate);
	    	oBatchPick.invokeChangeOrder(docInput);
	    }
	}
	 
	private YFCElement prepareChangeShipmentInputForPickLater(String shipmentKey) {
		YFCElement changeShipmentInput = YFCDocument.createDocument(XMLLiterals.SHIPMENT).getDocumentElement();
	    changeShipmentInput.setAttribute(XMLLiterals.ACTION, MODIFY);
	    changeShipmentInput.setAttribute(XMLLiterals.SHIPMENT_KEY, shipmentKey);
	    changeShipmentInput.setAttribute(XMLLiterals.HOLD_LOCATION, EMPTY_STRING);
	    return changeShipmentInput;
	}
	  
	private void setChangeShipmentInputForPickLater(YFCElement changeShipmentInput, String shipmentLineKey) {
	    YFCElement csShipmentLines = changeShipmentInput.getChildElement(XMLLiterals.SHIPMENT_LINES, true);
	    YFCElement csShipmentLine = YCDUtils.getLineInXml(csShipmentLines, XMLLiterals.SHIPMENT_LINE, XMLLiterals.SHIPMENT_LINE_KEY, shipmentLineKey, true);
	    csShipmentLine.setAttribute(XMLLiterals.STORE_BATCH_KEY, EMPTY_STRING);
	    csShipmentLine.setDoubleAttribute(XMLLiterals.STAGED_QTANTITY, 0.0D);
	    csShipmentLine.setDoubleAttribute(XMLLiterals.BACKROOM_PICK_QUANTITY, 0.0D);
	    csShipmentLine.setAttribute(XMLLiterals.BATCH_PICK_PRIORITY, EMPTY_STRING);
	}
}
