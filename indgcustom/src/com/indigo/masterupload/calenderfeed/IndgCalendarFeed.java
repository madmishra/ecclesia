package com.indigo.masterupload.calenderfeed;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgCalendarFeed extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static final String CALENDER= "_Calender";
	private static final String CALENDARS = "Calendars";
	private static final String POOLID="_PICK_RLS_RP";
	private static final String EXCEPTION_TIME = "00 00";
	private static final String OFF_DAY = "0";
	private static final String PICK="_PICK_";
	Map<String, String> map=new HashMap<>();
	/**
	   * This is the invoke point of the Service
	 * @throws  
	   * 
	   */
	  @Override
	 public YFCDocument invoke(YFCDocument inXml) {

	YFCElement calInEle = inXml.getDocumentElement();
	List<String> exceptionList = new ArrayList<>();
	 YFCDocument createCalenderXml =null;
		  YFCIterable<YFCElement> calendarEle = calInEle.getChildren(XMLLiterals.CALENDAR);
		  String organizationCode="";
		  String calenadrId="";
		  String effectiveFromDate="";
		  YFCElement createCalenderEle =null;
		  YFCElement effectivePeriods = null;
		  String effectiveToDate=getProperty("EFFECTIVE_TO_DATE");
		  for(YFCElement element : calendarEle) {
	 if(XmlUtils.isVoid(organizationCode)) {
		 		try {
		 			effectiveFromDate = dateFormatter(element.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE));
		 		} catch(Exception e) {
		 		}
				  organizationCode=element.getAttribute(XMLLiterals.ORGANIZATION_CODE);
				  calenadrId=organizationCode+CALENDER;
				  createCalenderXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
				  createCalenderEle =  createCalenderXml.getDocumentElement();
				  createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID,calenadrId);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION,calenadrId);
				  effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
						  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, effectiveFromDate);
				  
				}
			  else if(!organizationCode
					  .equals(element.getAttribute(XMLLiterals.ORGANIZATION_CODE))) {
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE, effectiveToDate);
				  System.out.println(createCalenderXml+"--mismatch calendar cfreated---");
				  System.out.println(exceptionList+"---edxceptionlist--");
				 createCalendar(createCalenderXml,exceptionList);
				 //call resorce pool
				 createResourcePool(organizationCode,calenadrId);
				  organizationCode=element.getAttribute(XMLLiterals.ORGANIZATION_CODE);
				  calenadrId=organizationCode+CALENDER;
				  createCalenderXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
				  createCalenderEle =  createCalenderXml.getDocumentElement();
				  createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID,calenadrId);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION,calenadrId);
				  effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
						  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, effectiveFromDate);
			  }
	 			String startTime = element.getAttribute(XMLLiterals.SHIFT_START_TIME);
	 			String endTime = element.getAttribute(XMLLiterals.SHIFT_END_TIME);
	 					try {
	 						if(EXCEPTION_TIME.equals(startTime) && EXCEPTION_TIME.equals(endTime)) {
	 							exceptionList.add(dateFormatter(element.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE)));
	 						}
	 						setShiftValues(element);
	 					} catch (ParseException e) {
	 						
	 					}
			  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE,effectiveToDate);
}
		  createCalendar(createCalenderXml,exceptionList);
		  //read cread calendar output and pass node
		  createResourcePool(organizationCode,calenadrId);
		  System.out.println(createCalenderXml+"---after createResourcePool---");
			 
		  return createCalenderXml;
}
	  /**
		 * This method formats the date
		 * @param fromDate
		 * @return
		 * @throws ParseException
		 */
		
		
		public String dateFormatter(String fromDate) throws ParseException
		{
			final String OLD_FORMAT = "yyyy/MM/dd";
			final String NEW_FORMAT = "yyyy-MM-dd";
			String newDateString;
			SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
			Date d = sdf.parse(fromDate);
			sdf.applyPattern(NEW_FORMAT);
			newDateString = sdf.format(d);
			return newDateString;
		}
		
		public String timeFormatter(String time) throws ParseException {
			final String OLD_FORMAT = "HH mm";
			final String NEW_FORMAT = "HH:mm:ss";
			String newTimeString;
			SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
			Date d = sdf.parse(time);
			sdf.applyPattern(NEW_FORMAT);
			newTimeString = sdf.format(d);
			return newTimeString;
			
		}
		 
		 /**  This method forms the template XML for getCelendarListListApi
			 * 
			 * @return
			 */
		 private YFCDocument formTemplateXmlForgetCalendarList() {
			    YFCDocument getCalendarTemp = YFCDocument.createDocument(CALENDARS);
			    YFCElement calendarEle = getCalendarTemp.getDocumentElement().createChild(XMLLiterals.CALENDAR);
			    calendarEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, EMPTY_STRING);
			    calendarEle.setAttribute(XMLLiterals.CALENDER_ID, EMPTY_STRING);
			    calendarEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION, EMPTY_STRING);
			    System.out.println(getCalendarTemp+"--formTemplateXmlForgetCalendarList--");
			    return getCalendarTemp;
		  }
		 /**
		  * This method forms input XML for getCelendarListListApi
		  * @param organizationCode
		  * @param effectiveToDate
		  * @param effectiveFromDate
		  * @return
		  */
		 private YFCDocument formInputXmlForGetCalendarList(String organizationCode,String calenderId) {
			 YFCDocument getCalendarXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
			    YFCElement calendarEle = getCalendarXml.getDocumentElement();
			    calendarEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
			    calendarEle.setAttribute(XMLLiterals.CALENDER_ID, calenderId);
			    calendarEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION, calenderId);
	System.out.println(getCalendarXml+"--formInputXmlForGetCalendarList--");
			    return getCalendarXml;
			  }
		 
		 /**
		  * This method invoke createCalendarApi and changeCalendarApi
		  * @param createCalenderXml
		  */
		 public void createCalendar(YFCDocument createCalenderInXml,List<String> exceptionList ) {
			 YFCElement exceptioncalEle = createCalenderInXml.getDocumentElement().createChild(XMLLiterals.CALENDAR_DAY_EXCEPTIONS);
			 for(String exceptionDate:exceptionList) {
				 YFCElement excepDayEle = exceptioncalEle.createChild(XMLLiterals.CALENDAR_DAY_EXCEPTION);
				 excepDayEle.setAttribute(XMLLiterals.DATE, exceptionDate);
				 excepDayEle.setAttribute(XMLLiterals.EXCEPTION_TYPE, OFF_DAY);
			 }
			 String calenderId = createCalenderInXml.getDocumentElement().getAttribute(XMLLiterals.CALENDER_ID);
			 String orgCode = createCalenderInXml.getDocumentElement().getAttribute(XMLLiterals.ORGANIZATION_CODE);
			 if(!XmlUtils.isVoid(calenderId) && getCalendarList(orgCode,calenderId).getDocumentElement().hasChildNodes()) {
					 exceptionList.clear();
					 invokeYantraApi(XMLLiterals.CHANGE_CALENDAR, createCalenderInXml);
					 manageSerSlot(orgCode);
					 return;
				 }
			    invokeYantraApi(XMLLiterals.CREATE_CALENDAR, createCalenderInXml);
			    manageSerSlot(orgCode);
			    exceptionList.clear();
			    
			  }
		 
		 /**
		  * this method invokes getCalendarList
		  * @param organizationCode
		  * @param effectiveToDate
		  * @param effectiveFromDate
		  * @return
		  */
		 public YFCDocument getCalendarList(String organizationCode, String calenderId){
			 System.out.println("--getCalendarList--");
			    return invokeYantraApi(XMLLiterals.GET_CALENDAR_LIST, 
			    		formInputXmlForGetCalendarList(organizationCode,calenderId),formTemplateXmlForgetCalendarList());
			  }
		 
		 /**
		  * this method is used to store the shiftStartTime and shiftEndTime
		  * @param calenderInEle
		  * @throws ParseException
		  */
		 
		 private void setShiftValues(YFCElement calenderInEle) throws ParseException {
			 	String startTime=timeFormatter(calenderInEle.getAttribute(XMLLiterals.SHIFT_START_TIME));
				String endTime=timeFormatter(calenderInEle.getAttribute(XMLLiterals.SHIFT_END_TIME));
				String hashKey=startTime+"-"+endTime;
				if(!map.containsKey(hashKey))
				{
					map.put(hashKey, hashKey);
				}
		 }
		 
		 /** 
		  * this method invokes manageServiceSlot
		  * @param orgCode
		  */
			private void manageSerSlot(String orgCode) {
				String itemGroupCode="PROD";
				Set<String> keySet=map.keySet();
				YFCDocument mangSlotDoc = YFCDocument.createDocument(XMLLiterals.SERVICE_SLOT_GROUP);
				YFCElement serviceSlotEle=mangSlotDoc.getDocumentElement();
				serviceSlotEle.setAttribute(XMLLiterals.ITEM_GROUP_CODE, itemGroupCode);
				serviceSlotEle.setAttribute(XMLLiterals.ORGANIZATION_KEY,XMLLiterals.INDIGO_CA);
				serviceSlotEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,orgCode+PICK+XMLLiterals.SERVICE_SLOT_GROUP);
				serviceSlotEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_DESC,orgCode+"_"+PICK+XMLLiterals.SERVICE_SLOT_GROUP);
				YFCElement slotList=serviceSlotEle.createChild(XMLLiterals.SERVICE_SLOT_LIST);
				for(Object s:keySet) {
					String[] shiftTime = map.get(s).split("-");
					YFCElement sortEle = slotList.createChild(XMLLiterals.SERVICE_SLOT);
					sortEle.setAttribute(XMLLiterals.START_TIME,shiftTime[0]);
					sortEle.setAttribute(XMLLiterals.END_TIME, shiftTime[1]);
				}
			System.out.println(mangSlotDoc+"--mangSlotDoc_ManageServSlot--");
			invokeYantraApi(XMLLiterals.MANAGE_SERVICE_SLOT_GROUP,mangSlotDoc,mangSlotTemp());
			map.clear();
			}
			
			public YFCDocument mangSlotTemp()
			{
				YFCDocument managDoc = YFCDocument.createDocument(XMLLiterals.SERVICE_SLOT_GROUP);
				YFCElement managEle=managDoc.getDocumentElement();
				managEle.setAttribute(XMLLiterals.ITEM_GROUP_CODE, EMPTY_STRING);
				managEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,EMPTY_STRING);
				managEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_DESC,EMPTY_STRING);
				managEle.setAttribute(XMLLiterals.ORGANIZATION_KEY,EMPTY_STRING);
				YFCElement slotList=managEle.createChild(XMLLiterals.SERVICE_SLOT_LIST);
				slotList.setAttribute(XMLLiterals.START_TIME,EMPTY_STRING);
				slotList.setAttribute(XMLLiterals.END_TIME, EMPTY_STRING);
				System.out.println(managDoc);
				return managDoc;
				
			}
			/**
			 * this method creates input for getResourceList api
			 * @param node
			 * @param capacityUnitOfMeasure
			 * @return
			 */
			public YFCDocument formInputXmlForGetResourceList(String organizationCode, String capacityUnitOfMeasure,String resourcePoolId){
				YFCDocument resourceList = YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
			    YFCElement resourcePoolEle = resourceList.getDocumentElement();
			    resourcePoolEle.setAttribute(XMLLiterals.NODE,organizationCode);
			    resourcePoolEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, capacityUnitOfMeasure);
			    resourcePoolEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID, resourcePoolId);
			    System.out.println(resourceList+"--formInputXmlForGetResourceList---");
			    return resourceList;
			   
			 }
			/**
			 * this method creates template for getRsourcePoolList api
			 * @return
			 */
			public YFCDocument formTemplateXmlForgetResourcePoolList()
			{
				 YFCDocument getResourcePoolTemp = YFCDocument.createDocument(XMLLiterals.RESOURCE_POOLS);
				    YFCElement resourcePoolEle = getResourcePoolTemp.getDocumentElement().createChild(XMLLiterals.RESOURCE_POOL);
				    resourcePoolEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID, EMPTY_STRING);
				    resourcePoolEle.setAttribute(XMLLiterals.NODE, EMPTY_STRING);
				    resourcePoolEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, EMPTY_STRING);
				    System.out.println(getResourcePoolTemp+"--formtemplateforgetResourcePoolList--");
				    return getResourcePoolTemp;
			}
			/**
			 * this method invokes getResourcePoolList api
			 * @param node
			 * @param capacityUnitOfMeasure
			 * @return
			 */
			 public YFCDocument getResourcepool(String node, String capacityUnitOfMeasure,String resourcePoolId){
				    return invokeYantraApi(XMLLiterals.GET_RESOURCE_POOL_LIST, 
				    		formInputXmlForGetResourceList(node,capacityUnitOfMeasure,resourcePoolId),formTemplateXmlForgetResourcePoolList());
				  }
		/**
		 * this method formd input for createResourcePool api
		 * @param purpose
		 * @param organizationCode
		 * @param resourcePoolId
		 * @param deliveryMethod
		 * @param capacityUnitOfMeasure
		 * @param calendarId
		 * @return
		 */
			 public YFCDocument inputXmlForCreateResourcePool(String purpose,String organizationCode, String resourcePoolId, String deliveryMethod,String capacityUnitOfMeasure,String calendarId)
			 {
				 YFCDocument inputXmlDoc=YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
				 YFCElement inputEle=inputXmlDoc.getDocumentElement();
				 inputEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,organizationCode+PICK+XMLLiterals.SERVICE_SLOT_GROUP);
				 inputEle.setAttribute(XMLLiterals.PURPOSE,purpose );
				 inputEle.setAttribute(XMLLiterals.NODE, organizationCode);
				 inputEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, capacityUnitOfMeasure);
				 inputEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID,resourcePoolId);
				 YFCElement resourceCalendarEle=inputEle.createChild(XMLLiterals.RESOURCE_CALENDAR);
				 resourceCalendarEle.setAttribute(XMLLiterals.CALENDER_ID, calendarId);
				 YFCElement resourcePoolAttributeEle=inputXmlDoc.getDocumentElement().createChild(XMLLiterals.RESOURCE_POOL_ATTRIBUTE_LIST).createChild(XMLLiterals.RESOURCE_POOL_ATTRIBUTE);
				 resourcePoolAttributeEle.setAttribute(XMLLiterals.DELIVERY_METHOD, deliveryMethod);
				 System.out.println(inputXmlDoc+"----inputXmlForCreateResourcePool---");
				 return inputXmlDoc;
			 }
			 
			 /**
			  * this method forms template for createResourcePool api
			  * @return
			  */
			 public YFCDocument xmlTempForCreateResourcePool() {
				 YFCDocument inputXmlDoc=YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
				 YFCElement inputEle=inputXmlDoc.getDocumentElement();
				 inputEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,EMPTY_STRING);
				 inputEle.setAttribute(XMLLiterals.PURPOSE,EMPTY_STRING );
				 inputEle.setAttribute(XMLLiterals.NODE, EMPTY_STRING);
				 inputEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, EMPTY_STRING);
				 inputEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID,EMPTY_STRING);
				 YFCElement resourceCalendarEle=inputXmlDoc.getDocumentElement().createChild(XMLLiterals.RESOURCE_CALENDAR);
				 resourceCalendarEle.setAttribute(XMLLiterals.CALENDER_ID, EMPTY_STRING);
				 YFCElement resourcePoolAttributeEle=inputXmlDoc.getDocumentElement().createChild(XMLLiterals.RESOURCE_POOL_ATTRIBUTE_LIST).createChild(XMLLiterals.RESOURCE_POOL_ATTRIBUTE);
				 resourcePoolAttributeEle.setAttribute(XMLLiterals.DELIVERY_METHOD, EMPTY_STRING);
				 System.out.println(inputXmlDoc+"---xmlTempForCreateResourcePool--");
				 return inputXmlDoc;
				 
			 }
			 /**
			  * this method invokes createResourcePool Api
			  * @param organizationCode
			  * @param calendarId
			  */
			 
			public void createResourcePool(String organizationCode,String calendarId) {
				//gat the calendarId and orgCode
				String resourcePoolId=organizationCode+POOLID;
				String deliveryMethod="PICK";
				String capacityUnitOfMeasure="RELEASE";
				String purpose="Inventory";
				 
				if(!getResourcepool(organizationCode,capacityUnitOfMeasure,resourcePoolId).getDocumentElement().hasChildNodes()) {
					invokeYantraApi(XMLLiterals.CREATE_RESOURCE_POOL,inputXmlForCreateResourcePool(purpose,organizationCode,resourcePoolId,deliveryMethod,capacityUnitOfMeasure,calendarId),xmlTempForCreateResourcePool());
                
					}
				}
				
				
			}

		 
			
