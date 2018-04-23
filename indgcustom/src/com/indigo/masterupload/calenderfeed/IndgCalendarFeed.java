package com.indigo.masterupload.calenderfeed;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgCalendarFeed extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static final String CALENDER= "_Calender12";
	private static final String POOLID="_PICK_RLS_RP";
	private static final String EXCEPTION_TIME = "00 00";
	private static final String OFF_DAY = "0";
	private static final String PICK="_PICK_";
	private static final String ITEM_GROUP_CODE="PROD";
	private static final String WORKING_DAY="1";
	YFCDocument createCalenderInXml = null;
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
		  YFCIterable<YFCElement> inputCalendarEle = calInEle.getChildren(XMLLiterals.CALENDAR);
		  String organizationCode="";
		  String calenadrId="";
		  String effectiveFromDate="";
		  
		  for(YFCElement calElement : inputCalendarEle) {
			  if(XmlUtils.isVoid(organizationCode)) {
		 		try {
		 			effectiveFromDate=anaylseDate(calElement.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE));
		 			if(effectiveFromDate=="null")
		 				break;
		 			
		 		} catch(Exception excep) {
		 			throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INVALID_DATE, excep);
		 		}
				  organizationCode=calElement.getAttribute(XMLLiterals.ORGANIZATION_CODE);
				  createCalendarInputDoc(organizationCode,effectiveFromDate);
				 }
	
			  else if(!organizationCode
					  .equals(calElement.getAttribute(XMLLiterals.ORGANIZATION_CODE))) {
				 createCalendar(exceptionList,effectiveFromDate);
				 createResourcePool(organizationCode,calenadrId);
				  organizationCode=calElement.getAttribute(XMLLiterals.ORGANIZATION_CODE);
				  createCalendarInputDoc(organizationCode,effectiveFromDate);
			  }
	 			String sSerSlotShiftStartTime = calElement.getAttribute(XMLLiterals.SHIFT_START_TIME);
	 			String sSerSlotShiftEndTime = calElement.getAttribute(XMLLiterals.SHIFT_END_TIME);
	 					try {
	 						if(EXCEPTION_TIME.equals(sSerSlotShiftStartTime) && EXCEPTION_TIME.equals(sSerSlotShiftEndTime)) 
	 							exceptionList.add(dateFormatter(calElement.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE)));
	 							else
	 								setShiftValues(calElement);
	 						}
	 						
	 					 catch (ParseException e) {
	 						throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INVALID_DATE, e);
	 					}
	 					
		  }
		  createCalendar(exceptionList,effectiveFromDate);
		  createResourcePool(organizationCode,calenadrId);
		  System.out.println("FINAL CALENDAR------------------------------"+createCalenderInXml);
		  return createCalenderInXml;
}
	  
	  /** this method create document for input
	   * 
	   */
	  public YFCDocument createCalendarInputDoc(String organizationCode,String effectiveFromDate)
		 {
		  	String calenadrId=organizationCode+CALENDER;
			createCalenderInXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
			YFCElement  createCalenderEle =  createCalenderInXml.getDocumentElement();
			  createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
			  createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID,calenadrId);
			  createCalenderEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION,calenadrId);
			  YFCElement effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
					  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
			  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, effectiveFromDate);
			  System.out.println("------createCalendarInputDoc-----------------------"+createCalenderInXml);
			  return createCalenderInXml;
			 
		 }
	  /**
	   * this method creates shift
	   * @param createCalenderXml
	   * @return
	   */
	 public void createCalShift() {
		 String yes="Y";
		 String effectiveToDate=getProperty("EFFECTIVE_TO_DATE");
		 System.out.println("EFFECTIVE_TO_DATE--------------------"+effectiveToDate);
		 String defShiftStartTime=getProperty("SHIFT_START_TIME");
		 System.out.println("----SHIFT_START_TIME-----------------"+defShiftStartTime);
		  String defShiftEndTime=getProperty("SHIFT_END_TIME");
		  System.out.println("-----SHIFT_END_TIME------------"+defShiftEndTime);
		 YFCElement effectiveEle=createCalenderInXml.getDocumentElement();
		 effectiveEle.getChildElement(XMLLiterals.EFFECTIVE_PERIODS).getChildElement(XMLLiterals.EFFECTIVE_PERIOD).setAttribute(XMLLiterals.EFFECTIVE_TO_DATE, effectiveToDate);
		YFCElement shiftele=effectiveEle.createChild(XMLLiterals.SHIFTS).createChild(XMLLiterals.SHIFT);
		  shiftele.setAttribute(XMLLiterals.WEDNESDAY_VALID,yes);
		  shiftele.setAttribute(XMLLiterals.TUESDAY_VALID,yes);
		  shiftele.setAttribute(XMLLiterals.THURSDAY_VALID,yes);
		  shiftele.setAttribute(XMLLiterals.SUNDAY_VALID,yes);
		  shiftele.setAttribute(XMLLiterals.SATURDAY_VALID,yes);
		  shiftele.setAttribute(XMLLiterals.MONDAY_VALID,yes);
		  shiftele.setAttribute(XMLLiterals.FRIDAY_VALID,yes);
		  shiftele.setAttribute(XMLLiterals.SHIFT_START_TIME,defShiftStartTime);
		  shiftele.setAttribute(XMLLiterals.SHIFT_END_TIME,defShiftEndTime);
		  
		  System.out.println("---CALENDAR SHIFT CREATION-----"+createCalenderInXml);
	 }
	 
	 
	 public String anaylseDate(String effectiveFromDate) throws ParseException{
		   DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date todayDate = new Date();  
		    Date effdate=new SimpleDateFormat("yyyy/MM/dd").parse(effectiveFromDate);  
		    
		    
		    if(todayDate.compareTo(effdate)==0 || todayDate.compareTo(effdate)>0) {
		    	String seffectiveFromDate = dateFormat.format(effdate);
		    	dateFormatter(seffectiveFromDate);
		    }
		    
		    	return null;
		
	}
	
	  /**
	   * 
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
			System.out.println("DATE FORMATTED"+newDateString);
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
			System.out.println("TIME_FORMATTED"+newTimeString);
			return newTimeString;
			
		}
		
		 
		 /**  This method forms the template XML for getCelendarListListApi
			 * 
			 * @return
			 */
		 private YFCDocument getCalendarDetailsTemplDoc() {
			    YFCDocument getCalendarTemp = YFCDocument.createDocument(XMLLiterals.CALENDAR);
			    YFCElement calendarEle = getCalendarTemp.getDocumentElement();
			    calendarEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, EMPTY_STRING);
			    calendarEle.setAttribute(XMLLiterals.CALENDER_ID, EMPTY_STRING);
			    calendarEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION, EMPTY_STRING);
			    YFCElement effecPeriodsEle=calendarEle.createChild(XMLLiterals.EFFECTIVE_PERIODS).createChild(XMLLiterals.EFFECTIVE_PERIOD);
			    effecPeriodsEle.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, EMPTY_STRING);
			    effecPeriodsEle.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE, EMPTY_STRING);
			    YFCElement calDayExcepEle=calendarEle.createChild(XMLLiterals.CALENDAR_DAY_EXCEPTIONS).createChild(XMLLiterals.CALENDAR_DAY_EXCEPTION);
			    calDayExcepEle.setAttribute(XMLLiterals.EXCEPTION_TYPE,EMPTY_STRING);
			    calDayExcepEle.setAttribute(XMLLiterals.DATE,EMPTY_STRING);
			    
			    System.out.println("---GETCALENDARDETAILS TEMPLATE----"+getCalendarTemp);
			    return getCalendarTemp;
		  }
		 /**
		  * This method forms input XML for getCelendarListListApi
		  * @param organizationCode
		  * @param effectiveToDate
		  * @param effectiveFromDate
		  * @return
		  */
		 private YFCDocument getCalendarDetailsInDoc (String organizationCode,String calenderId) {
			 YFCDocument getCalendarXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
			    YFCElement calendarEle = getCalendarXml.getDocumentElement();
			    calendarEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
			    calendarEle.setAttribute(XMLLiterals.CALENDER_ID, calenderId);
			    System.out.println("---GETCALENDAR DETAILS INPUT----"+getCalendarXml);
			    return getCalendarXml;
			  }
		 
		 /**
		  * This method invoke createCalendarApi and changeCalendarApi
		  * @param createCalenderXml
		  */
		 public void createCalendar(List<String> exceptionList,String effectiveFromDate) {
			 createCalShift();
			 YFCElement exceptioncalEle = createCalenderInXml.getDocumentElement().createChild(XMLLiterals.CALENDAR_DAY_EXCEPTIONS);
			 for(String exceptionDate:exceptionList) {
				 YFCElement excepDayEle = exceptioncalEle.createChild(XMLLiterals.CALENDAR_DAY_EXCEPTION);
				 excepDayEle.setAttribute(XMLLiterals.DATE, exceptionDate);
				 excepDayEle.setAttribute(XMLLiterals.EXCEPTION_TYPE, OFF_DAY);
			 }
			 String calenderId = createCalenderInXml.getDocumentElement().getAttribute(XMLLiterals.CALENDER_ID);
			 String orgCode = createCalenderInXml.getDocumentElement().getAttribute(XMLLiterals.ORGANIZATION_CODE);
			 if(!XmlUtils.isVoid(calenderId) && getCalendarDetails(orgCode,calenderId).getDocumentElement().hasChildNodes()) {
				 checkExceptionDate(orgCode,calenderId,effectiveFromDate);
					 exceptionList.clear();
					 invokeYantraApi(XMLLiterals.CHANGE_CALENDAR, createCalenderInXml);
					 manageSerSlot(orgCode);
				 }
			    invokeYantraApi(XMLLiterals.CREATE_CALENDAR, createCalenderInXml);
			    manageSerSlot(orgCode);
			    exceptionList.clear();
			    
			  }
		 
		 /** this method check whether the date is in exception list 
		  * 
		  */
		public void  checkExceptionDate(String orgCode,String calenderId,String effectiveFromDate){
			YFCElement getCalDetailsEle=getCalendarDetails(orgCode,calenderId).getDocumentElement();
			YFCElement calDayExcepEle=getCalDetailsEle.getChildElement(XMLLiterals.CALENDAR_DAY_EXCEPTIONS).getChildElement(XMLLiterals.CALENDAR_DAY_EXCEPTION);
			String sDate=calDayExcepEle.getAttribute(XMLLiterals.DATE);
			String sExceptionType=calDayExcepEle.getAttribute(XMLLiterals.EXCEPTION_TYPE);
			if(effectiveFromDate.equals(sDate) && sExceptionType.equals(OFF_DAY))
				calDayExcepEle.setAttribute(XMLLiterals.EXCEPTION_TYPE, WORKING_DAY);
		 }
		 
		 /**
		  * this method invokes getCalendarDetails
		  * @param organizationCode
		  * @param effectiveToDate
		  * @param effectiveFromDate
		  * @return
		  */
		 public YFCDocument getCalendarDetails(String organizationCode, String calenderId) {
			    return invokeYantraApi(XMLLiterals.GET_CALENDAR_LIST, 
			    		getCalendarDetailsInDoc(organizationCode,calenderId),getCalendarDetailsTemplDoc());
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
				Set<String> keySet=map.keySet();
				YFCDocument mangSlotDoc = YFCDocument.createDocument(XMLLiterals.SERVICE_SLOT_GROUP);
				YFCElement serviceSlotEle=mangSlotDoc.getDocumentElement();
				serviceSlotEle.setAttribute(XMLLiterals.ITEM_GROUP_CODE, ITEM_GROUP_CODE);
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
			System.out.println("---MANAGE SERVICE SLOT DOC---"+mangSlotDoc);
			invokeYantraApi(XMLLiterals.MANAGE_SERVICE_SLOT_GROUP,mangSlotDoc,manageServiceSlotTempDoc());
			map.clear();
			}
			/** this method creates template for manageserviceSlot api
			 * 
			 * @return
			 */
			
			public YFCDocument manageServiceSlotTempDoc()
			{
				YFCDocument managDoc = YFCDocument.createDocument(XMLLiterals.SERVICE_SLOT_GROUP);
				YFCElement serviceSlotEle=managDoc.getDocumentElement();
				serviceSlotEle.setAttribute(XMLLiterals.ITEM_GROUP_CODE, EMPTY_STRING);
				serviceSlotEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,EMPTY_STRING);
				serviceSlotEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_DESC,EMPTY_STRING);
				serviceSlotEle.setAttribute(XMLLiterals.ORGANIZATION_KEY,EMPTY_STRING);
				YFCElement slotList=serviceSlotEle.createChild(XMLLiterals.SERVICE_SLOT_LIST);
				slotList.setAttribute(XMLLiterals.START_TIME,EMPTY_STRING);
				slotList.setAttribute(XMLLiterals.END_TIME, EMPTY_STRING);
				System.out.println("--MANAGE SERVICE SLOT DOCUMNET----"+managDoc);
				return managDoc;
				
			}
			/**
			 * this method creates input for getResourceList api
			 * @param node
			 * @param capacityUnitOfMeasure
			 * @return
			 */
			public YFCDocument getResourcePoolListInDoc (String organizationCode, String capacityUnitOfMeasure,String resourcePoolId){
				YFCDocument resourceList = YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
			    YFCElement resourcePoolEle = resourceList.getDocumentElement();
			    resourcePoolEle.setAttribute(XMLLiterals.NODE,organizationCode);
			    resourcePoolEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, capacityUnitOfMeasure);
			    resourcePoolEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID, resourcePoolId);
			    System.out.println("---GET RESOURCE POOL LIST INPUT DOC-----"+resourceList);
			    return resourceList;
			   
			 }
			/**
			 * this method creates template for getRsourcePoolList api
			 * @return
			 */
			public YFCDocument getResourcePoolListTempDoc()
			{
				 YFCDocument getResourcePoolTemp = YFCDocument.createDocument(XMLLiterals.RESOURCE_POOLS);
				    YFCElement resourcePoolEle = getResourcePoolTemp.getDocumentElement().createChild(XMLLiterals.RESOURCE_POOL);
				    resourcePoolEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID, EMPTY_STRING);
				    resourcePoolEle.setAttribute(XMLLiterals.NODE, EMPTY_STRING);
				    resourcePoolEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, EMPTY_STRING);
				    System.out.println("-----GET RESOURCE POOL LIST TEMPLATE------"+getResourcePoolTemp);
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
				    		getResourcePoolListInDoc(node,capacityUnitOfMeasure,resourcePoolId),getResourcePoolListTempDoc());
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
			 public YFCDocument resourcePoolInDoc(String purpose,String organizationCode, String resourcePoolId, String deliveryMethod,String capacityUnitOfMeasure,String calendarId)
			 {
				 YFCDocument createResourcePoolXml =YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
				 YFCElement resPoolEle=createResourcePoolXml.getDocumentElement();
				 resPoolEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,organizationCode+PICK+XMLLiterals.SERVICE_SLOT_GROUP);
				 resPoolEle.setAttribute(XMLLiterals.PURPOSE,purpose );
				 resPoolEle.setAttribute(XMLLiterals.NODE, organizationCode);
				 resPoolEle.setAttribute(XMLLiterals.ITEM_GROUP_CODE, ITEM_GROUP_CODE);
				 resPoolEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, capacityUnitOfMeasure);
				 resPoolEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID,resourcePoolId);
				 resPoolEle.setAttribute(XMLLiterals.CAPACITYORGCODE,XMLLiterals.INDIGO_CA);
				 YFCElement resourceCalendarEle=resPoolEle.createChild(XMLLiterals.RESOURCE_CALENDAR);
				 resourceCalendarEle.setAttribute(XMLLiterals.CALENDER_ID, calendarId);
				 resourceCalendarEle.setAttribute(XMLLiterals.ORGANIZATION_CODE,organizationCode);
				 YFCElement resourcePoolAttributeEle=createResourcePoolXml.getDocumentElement().createChild(XMLLiterals.RESOURCE_POOL_ATTRIBUTE_LIST).createChild(XMLLiterals.RESOURCE_POOL_ATTRIBUTE);
				 resourcePoolAttributeEle.setAttribute(XMLLiterals.DELIVERY_METHOD, deliveryMethod);
				 System.out.println("-----RESOURCE POOL INPUT DOCUMENT------"+createResourcePoolXml);
				 return createResourcePoolXml;
			 }
			 
			
			 /**
			  * this method invokes createResourcePool Api
			  * @param organizationCode
			  * @param calendarId
			  */
			 
			public void createResourcePool(String organizationCode,String calendarId) {
				String resourcePoolId=organizationCode+POOLID;
				String deliveryMethod="PICK";
				String capacityUnitOfMeasure="RELEASE";
				String purpose="INVENTORY";
				 
				if(!getResourcepool(organizationCode,capacityUnitOfMeasure,resourcePoolId).getDocumentElement().hasChildNodes()) {
					invokeYantraApi(XMLLiterals.CREATE_RESOURCE_POOL,resourcePoolInDoc(purpose,organizationCode,resourcePoolId,deliveryMethod,capacityUnitOfMeasure,calendarId));
                
					}
				}
				
				
			}

		 
			
