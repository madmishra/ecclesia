package com.indigo.masterupload.categoryupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSException;

/**
 * This class uploads Master data for Category. It also
 * validate the path if Sub Category is created already if not
 * Sub category will be created and then add the Category 
 * 
 * 
 * @author BSG109
 *
 */
public class IndgCategoryMasterUpload extends AbstractCustomApi {

  private static final String EMPTY_STRING = "";
  private static final String FLAG_NO = "N";
  private String organizationCode = "";
  private static final String BACK_SLASH = "/";
  private static final String FLAG_YES = "Y";
  
  
  /**
   * This is the invoke point of the Service
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml) throws YFSException {
    YFCElement categoryInEle = inXml.getDocumentElement().getChildElement(XMLLiterals.CATEGORY);
    if(!XmlUtils.isVoid(categoryInEle)) {
      categoryInEle.setAttribute(XMLLiterals.IS_CLASSIFICATION,FLAG_NO);
      categoryInEle.setAttribute(XMLLiterals.GET_UNPUBLISHED_CATEGORIES,FLAG_YES);
      organizationCode = categoryInEle.getAttribute(XMLLiterals.ORGANIZATION_CODE);
      manageSubCategory(categoryInEle.getAttribute(XMLLiterals.CATEGORY_PATH),
          categoryInEle.getAttribute(XMLLiterals.CATEGORY_DOMAIN));
    }
    invokeYantraApi(XMLLiterals.MANAGE_CATEGORY, inXml);
    return inXml;
  }
  
  /**
   * 
   * This method gets Category list from the Category
   * path and creates category if not available in System
   * 
   * @param categoryPath
   * @param categorydomain
   * @return
   */
  public void manageSubCategory(String categoryPath, String categorydomain) {
    String [] sCategoryList = categoryPath.split(BACK_SLASH);
    int iCategoryPathDepth = 0;
    String path = BACK_SLASH+categorydomain;
    for(String categoryId:  sCategoryList) {
      if(!XmlUtils.isVoid(categoryId) && iCategoryPathDepth > 1) {
        path = path+BACK_SLASH+categoryId;
        if(!isCategoryExist(categoryId)) {
          createCategory(categoryId,categorydomain,path);
        }
      }
      iCategoryPathDepth++;
    }
  }
  
  /**
   * 
   * This method forms the Input XML for getCategoryListApi
   * 
   * @param categoryId
   * @return
   */
  public YFCDocument formInputXmlForGetCategoryList(String categoryId) {
    YFCDocument getCategoryListDoc = YFCDocument.createDocument(XMLLiterals.CATEGORY);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.CATEGORY_ID, categoryId);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
    return getCategoryListDoc;
  }
  
  /**
   * This method forms templates for the getCateforyList 
   * 
   * @return
   */
  public YFCDocument formTemplateXmlForgetCateforyList() {
    YFCDocument getCategoryListTemp = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
    YFCElement categoryEle = getCategoryListTemp.getDocumentElement().createChild(XMLLiterals.CATEGORY);
    categoryEle.setAttribute(XMLLiterals.CATEGORY_ID, EMPTY_STRING);
    return getCategoryListTemp;
  }
  
 /**
  * This method calls getCategoryList API and 
  * @param categoryId
  * @return
  */
  public boolean isCategoryExist(String categoryId){
    YFCDocument categoryListOp =  invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
        formInputXmlForGetCategoryList(categoryId),formTemplateXmlForgetCateforyList());
    if(categoryListOp.getDocumentElement().hasChildNodes()) {
      return true;
    }
    return false;
  }
  
  /**
   * 
   * @param categoryId
   * @param categoryDomain
   * @param path
   * @return
   */
  public YFCDocument formInputXmlForCreateCategory(String categoryId,String categoryDomain,String path) {
    YFCDocument createCategoryIn = formTemplateXmlForgetCateforyList();
    YFCElement createCategoryEle = createCategoryIn.getDocumentElement().getChildElement(XMLLiterals.CATEGORY);
    createCategoryEle.setAttribute(XMLLiterals.CATEGORY_ID, categoryId);
    createCategoryEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
    createCategoryEle.setAttribute(XMLLiterals.CATEGORY_DOMAIN,categoryDomain);
    createCategoryEle.setAttribute(XMLLiterals.DESCRIPTION,categoryId);
    createCategoryEle.setAttribute(XMLLiterals.IS_CLASSIFICATION,FLAG_NO);
    createCategoryEle.setAttribute(XMLLiterals.CATEGORY_PATH,path);
   return createCategoryIn;
  }
  
  /**
   * 
   * @param categoryId
   * @param categoryDomain
   * @param path
   */
  public void createCategory(String categoryId,String categoryDomain,String path) {
    invokeYantraApi(XMLLiterals.CREATE_CATEGORY, 
        formInputXmlForCreateCategory(categoryId,categoryDomain,path));
  }
  
}
