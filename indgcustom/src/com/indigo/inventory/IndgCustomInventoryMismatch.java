package com.indigo.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.LoggerUtil;
import com.yantra.yfc.date.YDate;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfc.util.YFCDoubleUtils;
import com.yantra.yfs.japi.YFSException;

public class IndgCustomInventoryMismatch extends AbstractCustomApi{
    
	private static YFCLogCategory logger = YFCLogCategory.instance(IndgCustomInventoryMismatch.class);
	private String _sShipNode = "";
	private String _sOrgCode = "";
	private String _sUOM = "";
	private String _sProductClass = "";
	private String _sSupplyType = "";
	private static YFCDate _sShipByDate = YDate.HIGH_DATE;
	
    public IndgCustomInventoryMismatch() {
    }
    
    public YFCDocument invoke(YFCDocument inpDoc){
		LoggerUtil.startComponentLog(logger, this.getClass().getName(),	"invoke", inpDoc);
		try {
	    	YFCElement rootElem = inpDoc.getDocumentElement();
	    	init(rootElem);  
	    	Map inSupplyMap = new HashMap();
	    	Map dbSupplyMap = new HashMap();
	   		loadInventorySupply(rootElem, inSupplyMap);
	   		logger.debug("Total item entries in input :" +inSupplyMap.size());
	   		loadInventorySupply(_sShipNode, dbSupplyMap);
	   		logger.debug("Total item entries in supply :" +dbSupplyMap.size());
	   		YFCDocument outDoc = loadMismatch(inSupplyMap, dbSupplyMap);    	
	   		LoggerUtil.endComponentLog(logger, this.getClass().getName(),"invoke", outDoc);
	   		return outDoc;	    	
		}catch (Exception ex) {
   			throw new YFSException(ex.getMessage());
   		}		 
    }
    
    
	private void init(YFCElement rootElem) {
    	YFCElement shipNodeElem = rootElem.getFirstChildElement();
    	_sShipNode = shipNodeElem.getAttribute("ShipNode");
    	YFCElement itemElem = shipNodeElem.getFirstChildElement();
    	_sOrgCode = itemElem.getAttribute("InventoryOrganizationCode");
    	_sUOM = itemElem.getAttribute("UnitOfMeasure");
    	_sProductClass = itemElem.getAttribute("ProductClass");
    	_sSupplyType = itemElem.getChildElement("SupplyDetails",true).getAttribute("SupplyType");
	}

	private YFCDocument loadMismatch(Map inSupplMap, Map dbSupplyMap) {
		logger.beginTimer("loadMismatch");
		YFCDocument doc = YFCDocument.createDocument("Items");
		YFCElement root = doc.getDocumentElement();
		for(Iterator oIter1 = inSupplMap.keySet().iterator(); oIter1.hasNext();){
			String sItemId = (String)oIter1.next();
			double dExpectedQty = (Double)inSupplMap.get(sItemId);
			double dActualQty = 0;
			if(dbSupplyMap.containsKey(sItemId)){
				dActualQty = (Double)dbSupplyMap.get(sItemId);
				logger.debug("Removing item " + sItemId + " from DB map");
				dbSupplyMap.remove(sItemId);
			}
			double dDiffQty = dExpectedQty - dActualQty;
			if(YFCDoubleUtils.equal(dDiffQty, 0)) {
				logger.debug("Ignoring item " + sItemId + " as it has no mismatch" );
				continue;
			}
			YFCElement item = root.createChild("Item");
			logger.debug("Creating a record for item :" +sItemId);
			item.setAttribute("ShipNode", _sShipNode);
			item.setAttribute("ItemID", sItemId);
			item.setAttribute("ProductClass", _sProductClass);
			item.setAttribute("UnitOfMeasure", _sUOM);
			item.setAttribute("InventoryOrganizationCode",_sOrgCode);
			item.setAttribute("SupplyType", _sSupplyType);
			item.setAttribute("ShipByDate", _sShipByDate);
			item.setAttribute("Quantity", dDiffQty);
			item.setAttribute("ExpectedQuantity", dExpectedQty);
			item.setAttribute("ActualQuantity", dActualQty);
		}
		for(Iterator oIter2 = dbSupplyMap.keySet().iterator(); oIter2.hasNext();){
			String sItemId = (String)oIter2.next();			
			double dActualQty = (Double)dbSupplyMap.get(sItemId);
			double dExpectedQty = 0;
			double dDiffQty = dExpectedQty - dActualQty;
			YFCElement item = root.createChild("Item");
			logger.debug("Creating a record for item :" +sItemId);
			item.setAttribute("ShipNode", _sShipNode);
			item.setAttribute("ItemID", sItemId);
			item.setAttribute("ProductClass", _sProductClass);
			item.setAttribute("UnitOfMeasure", _sUOM);
			item.setAttribute("InventoryOrganizationCode",_sOrgCode);
			item.setAttribute("SupplyType", _sSupplyType);
			item.setAttribute("ShipByDate", _sShipByDate);
			item.setAttribute("Quantity", dDiffQty);
			item.setAttribute("ExpectedQuantity", dExpectedQty);
			item.setAttribute("ActualQuantity", dActualQty);
		}
		logger.endTimer("loadMismatch");
		return doc;		
	}
	
	

	private void loadInventorySupply(YFCElement rootElem, Map inSupplyMap) {
		YFCNodeList oList = rootElem.getElementsByTagName("Item");
		for(Iterator oIter = oList.iterator() ; oIter.hasNext() ;){
			YFCElement itemElem = (YFCElement)oIter.next();
			String sItemId = itemElem.getAttribute("ItemID");
			double dQuantity = itemElem.getChildElement("SupplyDetails").getDoubleAttribute("Quantity");
			loadSupplyMap(sItemId, dQuantity, inSupplyMap);			
		}
	}

	private void loadInventorySupply(String sShipNode, Map dbSupplyMap) throws Exception{	
		logger.beginTimer("loadInventorySupply");
		String sSQLQuery = "";
		PreparedStatement prepStmt = null;
		Connection con = null;
        ResultSet result = null;
        try {
        	sSQLQuery = getSQLQuery(sShipNode);
        	logger.debug("The query to fetch the supply details :" +sSQLQuery);
        	con = getDBConnection();
            prepStmt = con.prepareStatement(sSQLQuery);
            result = prepStmt.executeQuery();  
            loadSupplyMap(result, dbSupplyMap, Integer.MAX_VALUE);
        } catch(SQLException ex) {
        	throw ex;
        } finally {
	        close(result);
	        close(prepStmt);
	    }
        logger.endTimer("loadInventorySupply");
	}
	
	
	public void loadSupplyMap(ResultSet rs, Map supplyMap, int aMaxRows) throws SQLException { 
		int rnum = 0;
		while ((rnum < aMaxRows) && (rs.next())) {
			String sItemId = rs.getString("ITEM").trim();
			double dQuantity = rs.getDouble("TOTAL");
			loadSupplyMap(sItemId, dQuantity, supplyMap);
			rnum++;
		}
	}

	
	public void loadSupplyMap(String sItemId, double dQuantity, Map supplyMap) {		
		if(supplyMap.containsKey(sItemId)){
			double dOldQty = (Double)supplyMap.get(sItemId);
			dQuantity+=dOldQty;
		}
		logger.debug("Item ID :" +sItemId + " Quantity :" +dQuantity);
		supplyMap.put(sItemId, dQuantity);
	}

	
    private void close(ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) {
                // Consuming exception even if it cannot close
            }
        }
    }

    private void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // Consuming exception even if it cannot close
            }
        }
    }
	
	private String getSQLQuery(String sShipNode) {	
		StringBuffer strQuery = new StringBuffer("SELECT ITEM.ITEM_ID AS ITEM, SUM(SUPPLY.QUANTITY) AS TOTAL FROM YFS_INVENTORY_SUPPLY SUPPLY, YFS_INVENTORY_ITEM ITEM WHERE ");
		strQuery.append(" SUPPLY.SHIPNODE_KEY ='"+sShipNode+"'");
		strQuery.append(" AND ITEM.INVENTORY_ITEM_KEY = SUPPLY.INVENTORY_ITEM_KEY ");
		strQuery.append(" GROUP BY ITEM.ITEM_ID");
	    return strQuery.toString();
	}	
  }
