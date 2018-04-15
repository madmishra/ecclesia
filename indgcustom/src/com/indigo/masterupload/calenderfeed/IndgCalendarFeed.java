package com.indigo.masterupload.calenderfeed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

public class IndgCalendarFeed extends AbstractCustomApi{
	private static final String EMPTY_STRING = "";
	private static final String CALENDER_NODE = "CALENDER_NODE";
	private static final String CALENDARS = "Calendars";
	private String effectiveToDate="";
	private  String effectiveFromDate="";
	/**
	   * This is the invoke point of the Service
	   * 
	   */
	/*public static void main(String[] args) {
		YFCDocument inXml=YFCDocument.getDocumentFor("<CalendarList>\r\n"+ 
				"<Calendar CalendarId=\"Calendar11\" " + 
				"    EffectiveFromDate=\"2017/04/13\" " + 
				"  OrganizationCode=\"Mtrx_Store_1\"/> " + 
				"<Calendar CalendarId=\"Calendar11\" " + 
				"    EffectiveFromDate=\"2017/04/14\" " + 
				"    OrganizationCode=\"Mtrx_Store_2\"/> " + 
				"</CalendarList>");
		IndgCalendarFeed calFeed=new IndgCalendarFeed();
		calFeed.invoke(inXml);
		 
	} */
	  @Override
	 public YFCDocument invoke(YFCDocument inXml) {

	YFCElement calInEle = inXml.getDocumentElement();
	System.out.println(calInEle+"KAVYA_InputDocumentElement");
	 YFCDocument createCalenderXml =null;
		  YFCIterable<YFCElement> calendarEle = calInEle.getChildren(XMLLiterals.CALENDAR);
		  String organizationCode="";
		  String effectiveFromDate="";
		  String toDate="";
		  YFCElement createCalenderEle =null;
		  YFCElement effectivePeriods = null;
		  for(YFCElement element : calendarEle) {
	 if(XmlUtils.isVoid(organizationCode)) {
		 		try {
		 			effectiveFromDate = dateFormatter(element.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE));
		 		} catch(Exception e) {
		 			System.out.println(e.toString());
		 		}
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
				  effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE, effectiveToDate);
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
			  System.out.println(createCalenderXml+"KAVYA_OutputCalenderXml");
	
}
		  createCalendar(createCalenderXml);
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
		 public void createCalendar(YFCDocument createCalenderXml ) {
			    invokeYantraApi(XMLLiterals.CREATE_CALENDAR, createCalenderXml);
			    System.out.println("KAVYA_CALENDAR_CREATED");
			  }
}