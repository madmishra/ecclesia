package com.indigo.masterupload.categoryupload;

import java.util.Collection;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.XPathUtil;
import com.indigo.utils.IndgManageDeltaLoadUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author BSG109
 * 
 * Custom API to manage Category to Publish and UN-Publish category
 * based on input Document and Category already exist.If category
 * preset in Input and but not in the system ManageAPI will be 
 * called to create.If the category not exist in InputDoc and but 
 * exist in System then Category will be unpublished.If category exist
 * in both system and input doc published to queue for ManageCategory.
 *
 */

public class IndgManageCatgoryLoad extends AbstractCustomApi {
  
  private static final String EMPTY_STRING = "";
  private static final String MANAGE_CATEGORY_UPLOADQ_FLOW = "Indg_CategoryFeed_Q";
  private static final String UN_PUBLISH_STATUS = "2000";
  private static final String BACK_SLASH = "/";
  private static final String CALL_DEPT_MAPPING_SERVICE = "Indg_CategoryFeed_Dept_Mapping";
  
  
  private String organizationCode = "";
  
  
  /**
   *  This method is invoke point of the service
   *  This calls manageDeltaLoadForDeletion method to
   *  get the list of Category that need to be un published 
   * 
   */
  @Override
  public YFCDocument invoke(YFCDocument inXml)  {
   createHeaderDeptDoc(inXml);
   setOrganizationCode(inXml);
   YFCDocument categoryListApiOp = getCategoryList(EMPTY_STRING, organizationCode);
   Collection<String> unpublishCategoryIDList = IndgManageDeltaLoadUtil.manageDeltaLoadForDeletion
       (inXml, categoryListApiOp,XMLLiterals.CATEGORY_ID, XMLLiterals.CATEGORY);
   addInputDocToManageCategory(inXml.getDocumentElement());
   manageUnPublishCategory(unpublishCategoryIDList,categoryListApiOp);
   return inXml;
  }
 
  /**
   * This method gets Organization code from Input Doc
   * 
   * @param inXml
   */
   private void setOrganizationCode(YFCDocument inXml){
     organizationCode = XPathUtil.getXpathAttribute(inXml, "/CategoryList/Category/@OrganizationCode");
   }
  
   
   /**
    * This forms Input Document for ManageCategory and calls
    * service to add the Document into Queue
    * 
    * @param inputEle
    */
   private void addInputDocToManageCategory(YFCElement inputEle){
     YFCIterable<YFCElement> yfsItator = inputEle.getChildren(XMLLiterals.CATEGORY);
     for(YFCElement categoryEle: yfsItator) {
       callManageCategoryQService(categoryEle);
     }
   }
   
   /**
    * This method calls custom server to drop Document 
    * to Queue
    * 
    * @param yfcInputDoc
    */
   private void callManageCategoryQService(YFCElement inEle) {
     YFCDocument inputDocForService = YFCDocument.createDocument(XMLLiterals.CATEGORY_LIST);
     inputDocForService.getDocumentElement().importNode(inEle);
     invokeYantraService(MANAGE_CATEGORY_UPLOADQ_FLOW, inputDocForService);
   }
   
   /**
    * 
    * This method iterates the Collection List and append
    * Status 2000 to un-publish the category.
    * 
    * @param categoryList
    * @param categoryListAPIDoc
    */
   private void manageUnPublishCategory(Collection<String> categoryList, YFCDocument categoryListAPIDoc) {
    for(String categoryId:categoryList) {
      YFCElement inEle = XPathUtil.getXPathElement(categoryListAPIDoc, "/CategoryList/Category[@CategoryID=\""+categoryId+"\"]");
      if(!XmlUtils.isVoid(inEle)) {
        inEle.setAttribute(XMLLiterals.STATUS, UN_PUBLISH_STATUS);
        callManageCategoryQService(inEle);
      }
    }
   }
   
  /**
   * This method calls getCategoryList API and 
   * @param categoryId
   * @param org
   * @return
   */
   public YFCDocument getCategoryList(String categoryId, String org){
     return invokeYantraApi(XMLLiterals.GET_CATEGORY_LIST, 
         IndgCategoryMasterUpload.getInputXmlForGetCategoryList(categoryId,org,EMPTY_STRING),
         IndgCategoryMasterUpload.formTemplateXmlForgetCategoryList());
   }
   
   /**
    * 
    * 
    * @param inXml
    */
   
   private void createHeaderDeptDoc (YFCDocument inXml) {
		YFCDocument departmentDoc = YFCDocument.createDocument(XMLLiterals.DEPARTMENT_GROUP_LIST);
		YFCElement categoryListEle = inXml.getDocumentElement();
		YFCIterable<YFCElement> yfsItrator = categoryListEle.getChildren(XMLLiterals.CATEGORY);
		for(YFCElement categoryEle :yfsItrator) {
			String status = categoryEle.getAttribute(XMLLiterals.STATUS);
			if(!status.equals(UN_PUBLISH_STATUS)) {
				String path = categoryEle.getAttribute(XMLLiterals.CATEGORY_PATH);
				String [] categoryPath = path.split(BACK_SLASH);
				int len = categoryPath.length;
				createDepartmentDoc(len, departmentDoc, categoryPath);
			}
		}
		callDeptMappingQueue(departmentDoc);
	}
	
	private void createDepartmentDoc(int len, YFCDocument departmentDoc, String [] categoryPath) {
	if (len >= 4 ) {
		String deptGroupName = categoryPath[2];
		String department = categoryPath[3];
		YFCElement deptGroupEle = XPathUtil.getXPathElement(departmentDoc, "/DepartmentGroupList/DepartmentGroup"
			  	+ "[@GroupName = \""+deptGroupName+"\"]");
		if(XmlUtils.isVoid(deptGroupEle)) {
			YFCElement groupEle = departmentDoc.getDocumentElement().createChild(XMLLiterals.DEPARTMENT_GROUP);
			groupEle.setAttribute(XMLLiterals.GROUP_NAME, deptGroupName);
			groupEle.createChild(XMLLiterals.DEPARTMENT).setAttribute(XMLLiterals.DEPARTMENT_NAME, department);
		} else {
			deptGroupEle.createChild(XMLLiterals.DEPARTMENT).setAttribute(XMLLiterals.DEPARTMENT_NAME, department);
			}
		}
		else if(len == 3) {
			String deptGroupName = categoryPath[2];
			YFCElement deptGroupEle = XPathUtil.getXPathElement(departmentDoc, "/DepartmentGroupList/DepartmentGroup"
		  		+ "[@GroupName = \""+deptGroupName+"\"]");
			if(XmlUtils.isVoid(deptGroupEle)) {
				YFCElement groupEle = departmentDoc.getDocumentElement().createChild(XMLLiterals.DEPARTMENT_GROUP);
				groupEle.setAttribute(XMLLiterals.GROUP_NAME, deptGroupName);
			}
		}
	}
	
	private void callDeptMappingQueue(YFCDocument doc) {
	     invokeYantraService(CALL_DEPT_MAPPING_SERVICE, doc);
	}
}
