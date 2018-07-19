package com.indigo.om.outbound.api;

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
import java.util.Set;
import org.w3c.dom.Document; 

public class Indg_HandleBatchPick implements YCDhandleShortageForBatchLineUE{
	private static YFCLogCategory cat = YFCLogCategory.instance(Indg_HandleBatchPick.class);
	  
	  public Document handleShortageForBatchLine(YFSEnvironment env, Document inXML)
	    throws YFSUserExitException
	  {
		System.out.println("hbdsbfjsjgnknhk"+inXML);
	    YFCElement inputElem = YFCDocument.getDocumentFor(inXML).getDocumentElement();
	    String shortageReason = inputElem.getChildElement("Item").getAttribute("ShortageReason");
	    System.out.println("bcsjbjg"+shortageReason);
	    String batch_key = inputElem.getAttribute("StoreBatchKey");
	    
	    YFCElement shipmentLines = inputElem.getChildElement("ShipmentLines");
	    if (YFCObject.equals(shortageReason, "damaged") || (YFCObject.equals(shortageReason, "shortage "))) {
	      handleDamagedItems(env, inputElem, batch_key, shipmentLines);
	    } else if (YFCObject.equals(shortageReason, "picklater")) {
	      handlePickLater(env, inputElem, batch_key, shipmentLines);
	    }
	    return YFCDocument.createDocument("Success").getDocument();
	  }
	  
	  private void handlePickLater(YFSEnvironment env, YFCElement inputElem, String batch_key, YFCElement shipmentLines)
	  {
	    YFCIterable<YFCElement> itr = shipmentLines.getChildren("ShipmentLine");
	    Map<String, YFCElement> changeShipmentInputMap = new HashMap();
	    while (itr.hasNext())
	    {
	      YFCElement changeShipmentInput = null;
	      YFCElement oShipmentLine = (YFCElement)itr.next();
	      String shipmentKey = oShipmentLine.getAttribute("ShipmentKey");
	      String shipmentLineKey = oShipmentLine.getAttribute("ShipmentLineKey");
	      if (changeShipmentInputMap.containsKey(shipmentKey))
	      {
	        changeShipmentInput = (YFCElement)changeShipmentInputMap.get(shipmentKey);
	        setChangeShipmentInputForPickLater(changeShipmentInput, shipmentLineKey);
	      }
	      else
	      {
	        changeShipmentInput = prepareChangeShipmentInputForPickLater(env, shipmentKey);
	        setChangeShipmentInputForPickLater(changeShipmentInput, shipmentLineKey);
	        changeShipmentInputMap.put(shipmentKey, changeShipmentInput);
	      }
	    }
	    if (changeShipmentInputMap.size() > 0) {
	      callChangeShipmentForAllShipments(env, changeShipmentInputMap);
	    }
	  }
	  
	  private void handleDamagedItems(YFSEnvironment env, YFCElement inputElem, String batch_key, YFCElement shipmentLines)
	  {
	    ArrayList<String> cancellableShipments = new ArrayList();
	    Map<String, YFCElement> changeShipmentInputMap = new HashMap();
	    ArrayList<String> shipmentKeyList = new ArrayList();
	    Map<String, YFCElement> shipmentDetailsMap = new HashMap();
	    
	    YFCIterable<YFCElement> itr = shipmentLines.getChildren("ShipmentLine");
	    while (itr.hasNext())
	    {
	      YFCElement changeShipmentWithBackorderInput = null;
	      YFCElement oShipmentLine = (YFCElement)itr.next();
	      YFCElement oShipment = oShipmentLine.getChildElement("Shipment");
	      String shipmentKey = oShipmentLine.getAttribute("ShipmentKey");
	      shipmentDetailsMap.put(shipmentKey, oShipment);
	      String shipmentLineKey = oShipmentLine.getAttribute("ShipmentLineKey");
	      double shortageQuantity = oShipmentLine.getDoubleAttribute("Quantity") - oShipmentLine.getDoubleAttribute("BackroomPickedQuantity");
	      if (shortageQuantity > 0.0D)
	      {
	        double newQuantity = oShipmentLine.getDoubleAttribute("Quantity") - shortageQuantity;
	        double shipmentLineShortQty = oShipmentLine.getDoubleAttribute("OriginalQuantity") - newQuantity;
	        if (YFCDoubleUtils.equal(newQuantity, 0.0D))
	        {
	          if (!cancellableShipments.contains(shipmentKey)) {
	            cancellableShipments.add(shipmentKey);
	          }
	        }
	        else if (!shipmentKeyList.contains(shipmentKey)) {
	          shipmentKeyList.add(shipmentKey);
	        }
	        if (changeShipmentInputMap.containsKey(shipmentKey))
	        {
	          changeShipmentWithBackorderInput = (YFCElement)changeShipmentInputMap.get(shipmentKey);
	          setChangeShipmentWithBackorderInput(changeShipmentWithBackorderInput, shipmentLineKey, newQuantity, shipmentLineShortQty);
	        }
	        else
	        {
	          changeShipmentWithBackorderInput = prepareChangeShipmentInput(env, shipmentKey);
	          System.out.println("jngfnhkjmkdm"+changeShipmentWithBackorderInput);
	          setChangeShipmentWithBackorderInput(changeShipmentWithBackorderInput, shipmentLineKey, newQuantity, shipmentLineShortQty);
	          changeShipmentInputMap.put(shipmentKey, changeShipmentWithBackorderInput);
	        }
	      }
	    }
	    if (changeShipmentInputMap.size() > 0) {
	      callChangeShipmentForAllShipments(env, changeShipmentInputMap);
	    }
	    Iterator<String> it;
	    if (cancellableShipments.size() > 0) {
	      for (it = cancellableShipments.iterator(); it.hasNext();)
	      {
	        String shipmentKey = (String)it.next();
	        boolean shipmentCancelled = checkShipmentForCancellation(env, shipmentKey, batch_key);
	        if ((!shipmentCancelled) && 
	          (!shipmentKeyList.contains(shipmentKey))) {
	          shipmentKeyList.add(shipmentKey);
	        }
	      }
	    }
	    for (Iterator<String> iterator = shipmentKeyList.iterator(); iterator.hasNext();)
	    {
	      String shKey = (String)iterator.next();
	      YFCElement shipmentDetailsElem = (YFCElement)shipmentDetailsMap.get(shKey);
	      callChangeShipmentStatus(env, shKey, shipmentDetailsElem, batch_key, inputElem.getAttribute("AssignedToUserId"));
	    }
	  }
	  
	  private void callChangeShipmentStatus(YFSEnvironment env, String shipmentKey, YFCElement shipmentDetails, String batch_key, String userId)
	  {
	    String status = shipmentDetails.getAttribute("Status");
	    System.out.println("hdbjsbjhnkj"+status);
	    if (status.contains("1100.70.06.10"))
	    {
	      YFCDocument changeShipmentStatusInputDoc = YFCDocument.createDocument("Shipment");
	      YFCElement changeShipmentStatusInput = changeShipmentStatusInputDoc.getDocumentElement();
	      changeShipmentStatusInput.setAttribute("BaseDropStatus", "1100.70.06.20");
	      changeShipmentStatusInput.setAttribute("ShipmentKey", shipmentKey);
	      changeShipmentStatusInput.setAttribute("TransactionId", "YCD_BACKROOM_PICK_IN_PROGRESS");
	      
	      YFCDocument changeShipmentStatusTempateDoc = YFCDocument.createDocument("Shipment");
	      YFCElement changeShipmentStatusTempateElem = changeShipmentStatusTempateDoc.getDocumentElement();
	      changeShipmentStatusTempateElem.setAttribute("ShipmentKey", "");
	      if (cat.isDebugEnabled()) {
	        cat.debug("Input to changeShipmentStatus:" + changeShipmentStatusInputDoc);
	        System.out.println("fdjgsnjhnj"+changeShipmentStatusInputDoc);
	      }
	      YFCElement apiElem = YFCDocument.createDocument("API").getDocumentElement();
	      apiElem.setAttribute("Name", "changeShipmentStatus");
	      APIManager.getInstance().invokeAPI(env, apiElem, changeShipmentStatusInput, changeShipmentStatusTempateElem);
	      
	      YFCDocument changeShipmentInputDoc = YFCDocument.createDocument("Shipment");
	      YFCElement changeShipmentInput = changeShipmentInputDoc.getDocumentElement();
	      changeShipmentInput.setAttribute("ShipmentKey", shipmentKey);
	      changeShipmentInput.setAttribute("AssignedToUserId", userId);
	      System.out.println("vhdbfhzbngxnkfdg"+changeShipmentInputDoc);
	      YFCDocument changeShipmentTemplateDoc = YFCDocument.createDocument("Shipment");
	      YFCElement changeShipmentTemplateElem = changeShipmentTemplateDoc.getDocumentElement();
	      changeShipmentStatusTempateElem.setAttribute("ShipmentKey", "");
	      System.out.println("bjdbgjnhk"+changeShipmentTemplateDoc);
	      if (cat.isDebugEnabled()) {
	        cat.debug("Input to changeShipment:" + changeShipmentInputDoc);
	      }
	      YFCElement apiElement = YFCDocument.createDocument("API").getDocumentElement();
	      apiElement.setAttribute("Name", "changeShipment");
	      APIManager.getInstance().invokeAPI(env, apiElement, changeShipmentInput, changeShipmentTemplateElem);
	    }
	  }
	  
	  private boolean checkShipmentForCancellation(YFSEnvironment env, String shipmentKey, String batchKey)
	  {
	    cat.beginTimer("callGetShipmentLineList");
	    
	    YFCDocument getShipmentLineListInputDoc = YFCDocument.createDocument("ShipmentLine");
	    YFCElement getShipmentLineListElem = getShipmentLineListInputDoc.getDocumentElement();
	    getShipmentLineListElem.setAttribute("ShipmentKey", shipmentKey);
	    
	    YFCDocument getShipmentLineListTemplateDoc = YFCDocument.createDocument("ShipmentLines");
	    YFCElement getShipmentLineListTemplate = getShipmentLineListTemplateDoc.getDocumentElement();
	    getShipmentLineListTemplate.setAttribute("TotalNumberOfRecords", "");
	    if (cat.isDebugEnabled())
	    {
	      cat.debug("Input to getShipmentLineList:" + getShipmentLineListElem);
	      cat.debug("Template to getShipmentLineList:" + getShipmentLineListTemplateDoc);
	    }
	    YFCElement apiElem = YFCDocument.createDocument("API").getDocumentElement();
	    apiElem.setAttribute("Name", "getShipmentLineList");
	    YFCElement getShipmentLineListOutElem = APIManager.getInstance().invokeAPI(env, apiElem, getShipmentLineListElem, getShipmentLineListTemplate);
	    if (cat.isDebugEnabled()) {
	      cat.debug("Output from getShipmentLineList:" + getShipmentLineListOutElem);
	    }
	    int noOfShipmentLines = getShipmentLineListOutElem.getIntAttribute("TotalNumberOfRecords");
	    getShipmentLineListElem.setAttribute("Quantity", "0");
	    YFCElement getShipmentLineListOutputElem = APIManager.getInstance().invokeAPI(env, apiElem, getShipmentLineListElem, getShipmentLineListTemplate);
	    int totalNumberOfRecords = getShipmentLineListOutputElem.getIntAttribute("TotalNumberOfRecords");
	    if (totalNumberOfRecords == noOfShipmentLines)
	    {
	      YFCElement changeShipmentInput = YFCDocument.createDocument("Shipment").getDocumentElement();
	      changeShipmentInput.setAttribute("Action", "Cancel");
	      changeShipmentInput.setAttribute("ShipmentKey", shipmentKey);
	      System.out.println("fjhjgh"+changeShipmentInput);
	      YFCDocument changeShipmentOutputDoc = YFCDocument.createDocument("Shipment");
	      YFCElement changeShipmentOutputElem = changeShipmentOutputDoc.getDocumentElement();
	      changeShipmentOutputElem.setAttribute("Status", "");
	      System.out.println("fjdngnhk"+changeShipmentOutputDoc);
	      YFCElement apiElement = YFCDocument.createDocument("API").getDocumentElement();
	      apiElement.setAttribute("Name", "changeShipment");
	      YFCElement changeShipmentForCancelOutElem = APIManager.getInstance().invokeAPI(env, apiElement, changeShipmentInput, changeShipmentOutputElem);
	      if (cat.isDebugEnabled()) {
	        cat.debug("Output from changeShipment:" + changeShipmentForCancelOutElem);
	      }
	      return true;
	    }
	    return false;
	  }
	  
	  private YFCElement prepareChangeShipmentInput(YFSEnvironment env, String shipmentKey)
	  {
	    YFCElement changeShipmentWithBackorderInput = YFCDocument.createDocument("Shipment").getDocumentElement();
	    changeShipmentWithBackorderInput.setAttribute("Action", "Modify");
	    changeShipmentWithBackorderInput.setAttribute("ShipmentKey", shipmentKey);
	    changeShipmentWithBackorderInput.setAttribute("BackOrderRemovedQuantity", "Y");
	    return changeShipmentWithBackorderInput;
	  }
	  
	  private void setChangeShipmentWithBackorderInput(YFCElement changeShipmentWithBackorderInput, String shipmentLineKey, double quantity, double shortageQuantity)
	  {
	    YFCElement csShipmentLines = changeShipmentWithBackorderInput.getChildElement("ShipmentLines", true);
	    YFCElement csShipmentLine = YCDUtils.getLineInXml(csShipmentLines, "ShipmentLine", "ShipmentLineKey", shipmentLineKey, true);
	    csShipmentLine.setDoubleAttribute("Quantity", quantity);
	    csShipmentLine.setDoubleAttribute("ShortageQty", shortageQuantity);
	  }
	  
	  private void callChangeShipmentForAllShipments(YFSEnvironment env, Map changeShipmentInputMap)
	  {
	    YFCDocument changeShipmentTemplateDoc = YFCDocument.createDocument("Shipment");
	    YFCElement changeShipmentTemplate = changeShipmentTemplateDoc.getDocumentElement();
	    changeShipmentTemplate.setAttribute("ShipmentKey", "");
	    
	    Iterator it = changeShipmentInputMap.keySet().iterator();
	    while (it.hasNext())
	    {
	    	System.out.println("huejhyiji"+it);
	      YFCElement changeShipmentInput = (YFCElement)changeShipmentInputMap.get(it.next());
	      YFCElement apiElem = YFCDocument.createDocument("API").getDocumentElement();
	      apiElem.setAttribute("Name", "changeShipment");
	      APIManager.getInstance().invokeAPI(env, apiElem, changeShipmentInput, changeShipmentTemplate);
	    }
	  }
	  
	  private YFCElement prepareChangeShipmentInputForPickLater(YFSEnvironment env, String shipmentKey)
	  {
	    YFCElement changeShipmentInput = YFCDocument.createDocument("Shipment").getDocumentElement();
	    changeShipmentInput.setAttribute("Action", "Modify");
	    changeShipmentInput.setAttribute("ShipmentKey", shipmentKey);
	    changeShipmentInput.setAttribute("HoldLocation", "");
	    System.out.println("hgfrnjrh"+changeShipmentInput);
	    
	    return changeShipmentInput;
	  }
	  
	  private void setChangeShipmentInputForPickLater(YFCElement changeShipmentInput, String shipmentLineKey)
	  {
	    YFCElement csShipmentLines = changeShipmentInput.getChildElement("ShipmentLines", true);
	    YFCElement csShipmentLine = YCDUtils.getLineInXml(csShipmentLines, "ShipmentLine", "ShipmentLineKey", shipmentLineKey, true);
	    csShipmentLine.setAttribute("StoreBatchKey", "");
	    csShipmentLine.setDoubleAttribute("StagedQuantity", 0.0D);
	    csShipmentLine.setDoubleAttribute("BackroomPickedQuantity", 0.0D);
	    csShipmentLine.setAttribute("BatchPickPriority", "");
	    System.out.println("hfdjkhgkh"+csShipmentLine);
	  }
}

