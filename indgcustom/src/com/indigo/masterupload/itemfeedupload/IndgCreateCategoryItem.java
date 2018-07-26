package com.indigo.masterupload.itemfeedupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.masterupload.categoryupload.IndgCategoryMasterUpload;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author  @author BSG109
 *
 */

public class IndgCreateCategoryItem extends AbstractCustomApi {
	
	String organizationCode="Indigo_CA"; 
	private static final String CREATE_ACTION = "Create";
	private static final String DEFAULT_CATEGORY_PATH = "DefaultCategoryPath";
	private static final String EMPTY_STRING = "";
	private static final String CATEGORY_ALERT_FLOW="CATEGORY_ALERT_FLOW";
	
	/**
	 * This is the starting point of the class
	 * 
	 */
	  
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		createCategoryItem(inXml);
		return inXml;
	}
	  
	/**
	 * This method calls modifyCategoryItem for creating categoryFeed
	 * 
	 * @param inXml
	 */
	  
	private void createCategoryItem(YFCDocument inXml) {
		System.out.println("----INDG_CREATECATEGORY INPUT----"+inXml);
		YFCElement itemEle = inXml.getDocumentElement().getChildElement(XMLLiterals.ITEM);
	    String categoryId = IndgManageItemFeed.getCategoryID(itemEle);
	    System.out.println("----INDG_CREATECATEGORY categoryId----"+categoryId);
	    if(!XmlUtils.isVoid(categoryId)) {
	    	YFCDocument categoryList = getCategoryList(categoryId, organizationCode);
	    	System.out.println("----INDG_CREATECATEGORY categoryList----"+categoryList);
	    	if(categoryList.getDocumentElement().hasChildNodes()) {
	    		String categoryPath = XPathUtil.getXpathAttribute(categoryList, "/CategoryList/Category/@CategoryPath");
	    		System.out.println("INVOKE MODIFY_CATEGORY_ITEM");
	    		invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, 
	    				IndgManageItemFeed.getInputDocForModifyCategoryItem(itemEle.getAttribute(XMLLiterals.ITEM_ID),
	    						CREATE_ACTION,categoryPath,organizationCode));
	        	} else {
	        		System.out.println("----CATEGORY_ALERT_FLOW------");
	        		invokeYantraService(getProperty(CATEGORY_ALERT_FLOW), inXml);
	        	}
	    	} else {
	    		System.out.println("INVOKE ELSE MODIFY_CATEGORY_ITEM ");
	    		invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, 
	    				IndgManageItemFeed.getInputDocForModifyCategoryItem(itemEle.getAttribute(XMLLiterals.ITEM_ID),
	    						CREATE_ACTION,getProperty(DEFAULT_CATEGORY_PATH),organizationCode));
	    	}
	    setItemType(itemEle);
	}
	  
	  /**
	   * This method calls getCategoryList API
	   * @param categoryId
	   * @param org
	   * @return
	   */
	  
	   private YFCDocument getCategoryList(String categoryId, String org) {
	     return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
	         IndgCategoryMasterUpload.getInputXmlForGetCategoryList(categoryId,org,EMPTY_STRING),
	           IndgCategoryMasterUpload.formTemplateXmlForgetCategoryList());
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
	     System.out.println("------itemEle---"+itemEle);
	   }
	  
	  
}
