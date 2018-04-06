package com.indigo.masterupload.categoryupload;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

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
  public YFCDocument invoke(YFCDocument inXml) {
    
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
        if(!getCategoryList(categoryId,organizationCode).getDocumentElement().hasChildNodes()) {
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
   * @param org
   * @return
   */
  private YFCDocument formInputXmlForGetCategoryList(String categoryId,String org) {
    YFCDocument getCategoryListDoc = YFCDocument.createDocument(XMLLiterals.CATEGORY);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.CATEGORY_ID, categoryId);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.CATEGORY_KEY, categoryId);
    getCategoryListDoc.getDocumentElement().setAttribute(XMLLiterals.ORGANIZATION_CODE, org);
    return getCategoryListDoc;
  }
  
  /**
   * This method forms templates for the getCateforyList 
   * 
   * @return
   */
  private YFCDocument formTemplateXmlForgetCategoryList() {
    YFCDocument getCategoryListTemp = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
    YFCElement categoryEle = getCategoryListTemp.getDocumentElement().createChild(XMLLiterals.CATEGORY);
    categoryEle.setAttribute(XMLLiterals.CATEGORY_ID, EMPTY_STRING);
    categoryEle.setAttribute(XMLLiterals.CATEGORY_PATH, EMPTY_STRING);
    return getCategoryListTemp;
  }
  
 /**
  * This method calls getCategoryList API and 
  * @param categoryId
  * @param org
  * @return
  */
  public YFCDocument getCategoryList(String categoryId, String org){
    return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
        formInputXmlForGetCategoryList(categoryId,org),formTemplateXmlForgetCategoryList());
  }
  
  /**
   * 
   * This method forms input XML for CreateCategoryAPI 
   * 
   * @param categoryId
   * @param categoryDomain
   * @param path
   * @return
   */
  private YFCDocument formInputXmlForCreateCategory(String categoryId,String categoryDomain,String path) {
    YFCDocument createCategoryIn = formTemplateXmlForgetCategoryList();
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
   * This method call createCategoryAPI from passed input
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
