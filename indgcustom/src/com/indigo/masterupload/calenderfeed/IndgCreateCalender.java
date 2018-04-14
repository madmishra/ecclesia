package com.indigo.masterupload.calenderfeed;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.integration.adapter.SynchronousTransaction;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgCreateCalender extends AbstractCustomApi {
	private static final String EMPTY_STRING = "";
	private static final String CALENDER_NODE = "CALENDER_NODE";
	private String effectiveToDate="";
	private  String effectiveFromDate="";
	YFCElement effectivePeriods = null;
	 
	/**
	   * This is the invoke point of the Service
	   * 
	   */
	  @Override
	 public YFCDocument invoke(YFCDocument inXml) {
		YFCElement calInEle = inXml.getDocumentElement();
		System.out.println(calInEle+"KAVYA_getDocumentElement");
		 YFCDocument createCalenderXml =null;
		if(!XmlUtils.isVoid(calInEle)) {
			  YFCIterable<YFCElement> calendarEle = calInEle.getChildren(XMLLiterals.CALENDAR);
			  String organizationCode="";
			  String fromDate="";
			  String toDate="";
			  YFCElement createCalenderEle =null;
			  for(YFCElement element : calendarEle) {
				  fromDate = element.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE);
				  System.out.println(fromDate+"KAVYA_fromDate");
				  try {
					effectiveFromDate=dateFormatter(fromDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				  
				  
		 if(XmlUtils.isVoid(organizationCode)) {
					  organizationCode=element.getAttribute(XMLLiterals.ORGANIZATION_CODE);
					  createCalenderXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
					  createCalenderEle =  createCalenderXml.getDocumentElement();
					  createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
					  createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID,CALENDER_NODE);
					  effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
							  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
					  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, effectiveFromDate);
					  System.out.println(createCalenderXml+"KAVYA_createCalenderXml");
					  
					  
			  }
				  else if(!organizationCode
						  .equals(element.getAttribute(XMLLiterals.ORGANIZATION_CODE))) {
					  createCalendar(createCalenderXml);
					  organizationCode=element.getAttribute(XMLLiterals.ORGANIZATION_CODE);
					  createCalenderXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
					  createCalenderEle =  createCalenderXml.getDocumentElement();
					  createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
					  createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID,CALENDER_NODE);
					  effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
							  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
					  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, effectiveFromDate);
					  
					  System.out.println(createCalenderXml+"KAVYA_else_createCalenderXml");
				  }
				  
				  
				  toDate = element.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE);
				  try {
					effectiveToDate=dateFormatter(toDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE, effectiveToDate);
				  
				 YFCDocument calList= getCalendarList(organizationCode,effectiveToDate,effectiveFromDate);
				 YFCElement calEle=calList.getDocumentElement();
				// if(!XmlUtils.isVoid(calEle))
					// changeCalendar();
				 //else
				  createCalendar(createCalenderXml);
				  System.out.println("calendar_created");
	
	}
		}
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
		/**  This method forms the template XML for getCelendarListListApi
		 * 
		 * @return
		 */
	 private YFCDocument formTemplateXmlForgetCalendarList() {
		    YFCDocument getCalendarTemp = YFCDocument.createDocument(XMLLiterals.CALENDAR);
		    YFCElement calendarEle = getCalendarTemp.getDocumentElement().createChild(XMLLiterals.CALENDAR);
		    calendarEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, EMPTY_STRING);
		    effectivePeriods=calendarEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
					  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
		    effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE,EMPTY_STRING);
		    effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE,EMPTY_STRING);
		    System.out.println(getCalendarTemp+"KAVYA_TEMPLATE");
		    
		    return getCalendarTemp;
	  }
	 /**
	  * This method forms input XML for getCelendarListListApi
	  * @param organizationCode
	  * @param effectiveToDate
	  * @param effectiveFromDate
	  * @return
	  */
	 private YFCDocument formInputXmlForGetCalendarList(String organizationCode,String effectiveToDate,String effectiveFromDate) {
		 YFCDocument getCalendarXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
		    YFCElement calendarEle = getCalendarXml.getDocumentElement().createChild(XMLLiterals.CALENDAR);
		    calendarEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
		    effectivePeriods=calendarEle.createChild(XMLLiterals.EFFECTIVE_PERIODS)
					  .createChild(XMLLiterals.EFFECTIVE_PERIOD);
		    effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE,effectiveFromDate);
		    effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE,effectiveToDate);
		    System.out.println(getCalendarXml+"KAVYA_GETCALENDAR_LIST");
		    return getCalendarXml;
		  }
	 
	 /**
	  * This method invoke createCalendarApi
	  * @param createCalenderXml
	  */
	 public void createCalendar(YFCDocument createCalenderXml ) {
		    invokeYantraApi(XMLLiterals.CREATE_CALENDAR, createCalenderXml);
		    System.out.println("KAVYA_CALENDAR_CREATED");
		  }
	 
	 /**
	  * this method invokes getCalendarList
	  * @param organizationCode
	  * @param effectiveToDate
	  * @param effectiveFromDate
	  * @return
	  */
	 public YFCDocument getCalendarList(String organizationCode, String effectiveToDate,String effectiveFromDate){
		    return invokeYantraApi(XMLLiterals.GET_CALENDAR_LIST, 
		    		formInputXmlForGetCalendarList(organizationCode,effectiveToDate,effectiveFromDate),formTemplateXmlForgetCalendarList());
		  }
		
	/* public void changeCalendar()
	 {
		 
	 }*/
		
	}

		
		
		
