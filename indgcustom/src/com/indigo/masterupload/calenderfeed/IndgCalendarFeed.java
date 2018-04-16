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
	private static final String EXCEPTION_TIME = "00 00";
	private static final String OFF_DAY = "0";
	private static final String ORGANIZATION_KEY="OrganizationKey";
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
				  createCalenderXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
				  createCalenderEle =  createCalenderXml.getDocumentElement();
				  createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID,organizationCode+CALENDER);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION,organizationCode+CALENDER);
				  effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
						  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, effectiveFromDate);
				  
				}
			  else if(!organizationCode
					  .equals(element.getAttribute(XMLLiterals.ORGANIZATION_CODE))) {
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE, effectiveToDate);
				 createCalendar(createCalenderXml,exceptionList);
				  organizationCode=element.getAttribute(XMLLiterals.ORGANIZATION_CODE);
				  createCalenderXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
				  createCalenderEle =  createCalenderXml.getDocumentElement();
				  createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID,organizationCode+CALENDER);
				  createCalenderEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION,organizationCode+CALENDER);
				  effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
						  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, effectiveFromDate);
			  }
	 			String startTime = element.getAttribute(XMLLiterals.SHIFT_START_TIME);
	 			String endTime = element.getAttribute(XMLLiterals.SHIFT_END_TIME);
	 				if(EXCEPTION_TIME.equals(startTime) && EXCEPTION_TIME.equals(endTime)) {
	 					try {
	 						exceptionList.add(dateFormatter(element.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE)));
	 						
	 					} catch (ParseException e) {
	 						
	 					}
	 				}
	 			
			 
			  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE,effectiveToDate);
}
		  createCalendar(createCalenderXml,exceptionList);
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
	
			    return getCalendarXml;
			  }
		 
		 /**
		  * This method invoke createCalendarApi
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
				Set<String> keySet=map.keySet();
				YFCDocument mangSlotDoc = YFCDocument.createDocument(XMLLiterals.SERVICE_SLOT_GROUP);
				YFCElement serviceSlotEle=mangSlotDoc.getDocumentElement();
				serviceSlotEle.setAttribute(ORGANIZATION_KEY,orgCode);
				serviceSlotEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,XMLLiterals.SERVICE_SLOT+orgCode);
				YFCElement slotList=serviceSlotEle.createChild("ServiceSlotList");
				for(Object s:keySet) {
					String[] shiftTime = map.get(s).split("-");
					YFCElement sortEle = slotList.createChild(XMLLiterals.SERVICE_SLOT);
					sortEle.setAttribute(XMLLiterals.START_TIME,shiftTime[0]);
					sortEle.setAttribute(XMLLiterals.END_TIME, shiftTime[1]);
				}
			YFCDocument temp = YFCDocument.createDocument(XMLLiterals.SERVICE_SLOT_GROUP);
			invokeYantraApi(XMLLiterals.MANAGE_SERVICE_SLOT_GROUP,mangSlotDoc,temp);
			map.clear();
			}
			
		 
			
}