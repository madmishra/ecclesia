package com.indigo.masterupload.categoryupload;

import java.util.ArrayList;
import java.util.List;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.masterupload.itemfeedupload.IndgManageItemFeed;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * This class uploads Master data for Category. It also
 * validate the path if Sub Category is created already if not
 * Sub category will be created and then add the Category 
 * 
 * @author BSG109
 *
 */

public class IndgCategoryMasterUpload extends AbstractCustomApi {

  private static final String EMPTY_STRING = "";
  private static final String FLAG_NO = "N";
  private String organizationCode = "Indigo_CA";
  private static final String BACK_SLASH = "/";
  private static final String FLAG_YES = "Y";
  private static final String CREATE_ACTION = "Create";
  private static final String DELETE_ACTION = "Delete";
  private static final String DEFAULT_UOM = "EACH";
  private static final String UNPUBLISH_STATUS = "2000";
  private static final String PUBLISH_STATUS = "3000";
  private static final String ORG_CODE = "Indigo_CA";
  List<String> itemIDList = new ArrayList<>();
  
  
  /**
   * This is the invoke point of the Service
   * 
   */
  
  @Override
  public YFCDocument invoke(YFCDocument inXml) {
    YFCElement categoryInEle = inXml.getDocumentElement().getChildElement(XMLLiterals.CATEGORY);
    if(!XmlUtils.isVoid(categoryInEle)) {
      categoryInEle.setAttribute(XMLLiterals.IS_CLASSIFICATION,FLAG_NO);
      categoryInEle.setAttribute(XMLLiterals.GET_UNPUBLISHED_CATEGORIES,FLAG_YES);
     if(UNPUBLISH_STATUS.equals(categoryInEle.getAttribute(XMLLiterals.STATUS))){
        invokeYantraApi(XMLLiterals.MANAGE_CATEGORY, inXml);
        return inXml;
      }
     organizationCode = categoryInEle.getAttribute(XMLLiterals.ORGANIZATION_CODE,ORG_CODE);
     YFCDocument categoryItemList = getCategoryList(categoryInEle
         .getAttribute(XMLLiterals.CATEGORY_ID),organizationCode,EMPTY_STRING);
     manageDeleteCategory(categoryInEle.getAttribute(XMLLiterals.CATEGORY_ID),
         categoryInEle.getAttribute(XMLLiterals.CATEGORY_PATH),categoryItemList);
     manageSubCategory(categoryInEle.getAttribute(XMLLiterals.CATEGORY_PATH),
    		 categoryInEle.getAttribute(XMLLiterals.CATEGORY_DOMAIN));
     invokeYantraApi(XMLLiterals.MANAGE_CATEGORY, inXml);
     //manageCategoryItem(categoryInEle);
    }
    return inXml;
  }
  
  /**
   * 
   * This method gets Category list from the Category
   * path Attribute in Input Document and creates category 
   * if not available in System.
   * 
   * @param categoryPath
   * @param categorydomain
   * @return
   */
  
  private void manageSubCategory(String categoryPath, String categorydomain) {
    String [] sCategoryList = categoryPath.split(BACK_SLASH);
    int iCategoryPathDepth = 0;
    String path = BACK_SLASH+categorydomain;
    for(String categoryId:  sCategoryList) {
      if(!XmlUtils.isVoid(categoryId) && iCategoryPathDepth > 1) {
        path = path+BACK_SLASH+categoryId;
        if(!getCategoryList(categoryId,organizationCode,path).getDocumentElement().hasChildNodes()) {
          createCategory(categoryId,categorydomain,path,organizationCode);
        }
      }
      if(iCategoryPathDepth == 3 && !isDepartmentExist(categoryId)
		  && FLAG_YES.equals(getProperty(IS_DEPT_MAPPING_REQ))){
        invokeYantraApi(XMLLiterals.MANAGE_ORGANIZATION_HIERARCHY,
		getInputDocForDepartmentCreation(categoryId));
      }
      iCategoryPathDepth++;
    }
  }
  
  /**
   * 
   * This method forms the Input XML for getCategoryListApi
   * 
   * @param categoryId
   * @param org
   * @param categoryPath
   * @return
   */
  
  public static YFCDocument getInputXmlForGetCategoryList(String categoryId,String org,String categoryPath) {
    YFCDocument getCategoryListDoc = YFCDocument.createDocument(XMLLiterals.CATEGORY);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.CATEGORY_ID, categoryId);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, org);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.CATEGORY_PATH, categoryPath);
    getCategoryListDoc.getDocumentElement()
      .setAttribute(XMLLiterals.GET_UN_PUBLISHED_CATEGORY, FLAG_YES);
    return getCategoryListDoc;
  }
  
  /**
   * This method forms templates for the getCateforyList 
   * 
   * @return
   */
  
  public static YFCDocument formTemplateXmlForgetCategoryList() {
    YFCDocument getCategoryListTemp = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
    YFCElement categoryEle = getCategoryListTemp.getDocumentElement().createChild(XMLLiterals.CATEGORY);
    categoryEle.setAttribute(XMLLiterals.CATEGORY_ID, EMPTY_STRING);
    categoryEle.setAttribute(XMLLiterals.CATEGORY_PATH, EMPTY_STRING);
    categoryEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, EMPTY_STRING);
    return getCategoryListTemp;
  }
  
 /**
  * This method calls getCategoryList API and 
  * @param categoryId
  * @param org
  * @param categoryPath
  * @return
  */
  
  public YFCDocument getCategoryList(String categoryId, String org,String categoryPath){
    return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
        getInputXmlForGetCategoryList(categoryId,org,categoryPath),formTemplateXmlForgetCategoryList());
  }
  
  /**
   * 
   * This method forms input XML for CreateCategoryAPI 
   * 
   * @param categoryId
   * @param categoryDomain
   * @param path
   * @param organizationCode
   * @return
   */
  
  public static YFCDocument getInputXmlForCreateCategory(String categoryId,String categoryDomain,
      String path,String organizationCode) {
    YFCDocument createCategoryIn = formTemplateXmlForgetCategoryList();
    YFCElement createCategoryEle = createCategoryIn.getDocumentElement().getChildElement(XMLLiterals.CATEGORY);
    createCategoryEle.setAttribute(XMLLiterals.CATEGORY_ID, categoryId);
    createCategoryEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
    createCategoryEle.setAttribute(XMLLiterals.CATEGORY_DOMAIN,categoryDomain);
    createCategoryEle.setAttribute(XMLLiterals.DESCRIPTION,categoryId);
    createCategoryEle.setAttribute(XMLLiterals.SHORT_DESCRIPTION,categoryId);
    createCategoryEle.setAttribute(XMLLiterals.IS_CLASSIFICATION,FLAG_NO);
    createCategoryEle.setAttribute(XMLLiterals.CATEGORY_PATH,path);
    createCategoryEle.setAttribute(XMLLiterals.STATUS, PUBLISH_STATUS);
   return createCategoryIn;
  }
  
  /**
   * This method call createCategoryAPI from passed input
   * 
   * @param categoryId
   * @param categoryDomain
   * @param path
   * @param orgCode
   */
  
  public void createCategory(String categoryId,String categoryDomain,String path,String orgCode) {
    invokeYantraApi(XMLLiterals.CREATE_CATEGORY, 
        getInputXmlForCreateCategory(categoryId,categoryDomain,path,orgCode));
  }
  
  
  /**
   * This is the method for forming input doc for
   * creating department group
   * 
   * @param categoryId
   * @return
   */
  
  private YFCDocument getInputDocForDepartmentCreation(String categoryId){
    YFCDocument inputXml = YFCDocument.createDocument(XMLLiterals.ORGANIZATION);
    YFCElement organizationEle = inputXml.getDocumentElement();
    organizationEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
    YFCElement deptEle =organizationEle.createChild(XMLLiterals.DEPARTMENT_LIST).createChild(XMLLiterals.DEPARTMENT);
    deptEle.setAttribute(XMLLiterals.DEPARTMENT_KEY, categoryId);
    deptEle.setAttribute(XMLLiterals.DEPARTMENT_NAME, categoryId);
    deptEle.setAttribute(XMLLiterals.DEPARTMENT_CODE, categoryId);
   return inputXml;  
  }
  
  /**
   * This method validates if the Item are mapped to existing 
   * category. If exist, removes the item before deleting the 
   * category and add to the newly created category.
   * 
   * @param categoryEle
   */
  
  private void manageCategoryItem(YFCElement categoryEle) {
    String categoryPath = categoryEle.getAttribute(XMLLiterals.CATEGORY_PATH);
    String action = CREATE_ACTION;
    YFCDocument itemList = getItemListDocumentFromList();
    if(XmlUtils.isVoid(itemList)) {
      itemList = getItemList(categoryPath);
      action = DELETE_ACTION;
    }
    if(itemList.getDocumentElement().hasChildNodes()) {
      YFCDocument deleteCategoryItemDoc = YFCDocument.createDocument(XMLLiterals.MODIFY_CATEGORY_ITEMS);
      YFCElement deleteCategoryItemEle = deleteCategoryItemDoc.getDocumentElement().createChild(XMLLiterals.CATEGORY);
      deleteCategoryItemEle.setAttribute(XMLLiterals.CATEGORY_PATH, categoryEle.getAttribute(XMLLiterals.CATEGORY_PATH));
      deleteCategoryItemEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
      YFCElement categoryItemList = deleteCategoryItemEle.createChild(XMLLiterals.CATEGORY_ITEM_LIST);
      YFCIterable<YFCElement> yfcItrator = itemList.getDocumentElement().getChildren(XMLLiterals.ITEM);
      for(YFCElement itemEle :yfcItrator){
        YFCElement categoryItem = categoryItemList.createChild(XMLLiterals.CATEGORY_ITEM);
        categoryItem.setAttribute(XMLLiterals.ACTION, action);
        categoryItem.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
        categoryItem.setAttribute(XMLLiterals.ITEM_ID, itemEle.getAttribute(XMLLiterals.ITEM_ID));
        categoryItem.setAttribute(XMLLiterals.UNIT_OF_MEASURE, DEFAULT_UOM);
        itemIDList.add(itemEle.getAttribute(XMLLiterals.ITEM_ID));
      }
      invokeYantraApi(XMLLiterals.MODIFY_CATEGORY_ITEM, deleteCategoryItemDoc);
    }
  }
  
  /**
   * This method forms input xml for getItemList API
   * 
   * @param categoryPath
   * @return
   */
  
  private YFCDocument getItemList(String categoryPath) {
    YFCDocument itemInXml = YFCDocument.createDocument(XMLLiterals.ITEM);
    itemInXml.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
    YFCElement categoryFilter = itemInXml.getDocumentElement().createChild(XMLLiterals.CATEGORY_FILTER);
    categoryFilter.setAttribute(XMLLiterals.CATEGORY_PATH,categoryPath);
    categoryFilter.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
    return invokeYantraApi(XMLLiterals.GET_ITEM_LIST_API,itemInXml,
       IndgManageItemFeed.getTemplateForGetItemList());
  }
  
  /**
   * This method validate if the Path matches with the Input and system.
   * If not deletes the category from System and creates new one from
   * the input
   * 
   * @param categoryId
   * @param categoryPath
   * @param categoryListApiOp
   */
  
  public void manageDeleteCategory(String categoryId,String categoryPath,YFCDocument categoryListApiOp) {
    YFCElement categoryEle = XPathUtil.getXPathElement(categoryListApiOp,
        "/CategoryList/Category[@CategoryID=\""+categoryId+"\"]");
    if(!XmlUtils.isVoid(categoryEle) && !categoryPath.equals(categoryEle.getAttribute(XMLLiterals.CATEGORY_PATH))) {
      categoryEle.setAttribute(XMLLiterals.ACTION,DELETE_ACTION);
      //manageCategoryItem(categoryEle);
      invokeYantraApi(XMLLiterals.MANAGE_CATEGORY, categoryListApiOp);
    }
  }
  
  /**
   * Form Output Document for Item from List
   * 
   * @return
   */
  
  public YFCDocument getItemListDocumentFromList(){
    YFCDocument itemListDoc  = null;
    	for(String itemID: itemIDList) {
    		itemListDoc = YFCDocument.createDocument(XMLLiterals.ITEM_LIST);
    		itemListDoc.getDocumentElement().createChild(XMLLiterals.ITEM).setAttribute(XMLLiterals.ITEM_ID, itemID);
    }
    return itemListDoc;
  }
  
  /**
   * 
   * This method calls getOrganization List with 
   * categoryID as search criteria
   * 
   * @param categoryId
   * @return
   */
  
  private boolean isDepartmentExist(String categoryId) {
    YFCDocument orgDoc = YFCDocument.createDocument(XMLLiterals.ORGANIZATION);
    orgDoc.getDocumentElement().createChild(XMLLiterals.DEPARTMENT_LIST)
    .createChild(XMLLiterals.DEPARTMENT).setAttribute(XMLLiterals.DEPARTMENT_NAME, categoryId);
    YFCDocument orgTempDoc = YFCDocument.createDocument(XMLLiterals.ORGANIZATION_LIST);
    orgTempDoc.getDocumentElement().createChild(XMLLiterals.ORGANIZATION).setAttribute(XMLLiterals.ORGANIZATION_CODE, EMPTY_STRING);
    YFCDocument orgListDoc = invokeYantraApi(XMLLiterals.GET_ORGANIZATION_LIST, orgDoc, orgDoc);
    if(orgListDoc.getDocumentElement().hasChildNodes()) {
      return true;
    }
    return false;
  }
}
