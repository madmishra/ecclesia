package com.indigo.masterupload.calenderfeed;
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
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * 
 * @author BSG170
 *
 */
public class IndgCalendarFeed extends AbstractCustomApi {
	private static final String EMPTY_STRING = "";
	private static final String CALENDER = "_Calendar";
	private static final String POOLID = "_PICK_RLS_RP";
	private static final String EXCEPTION_TIME = "00 00";
	private static final String OFF_DAY = "0";
	private static final String PICK = "_PICK_";
	private static final String ITEM_GROUP_CODE = "PROD";
	private static final String WORKING_DAY = "1";
	private static final String INDG_GET_RESOURCE_POOL_CAPACITY = "Indg_getResourcePoolCapacity"; 
	YFCDocument docCreateCalenderInXml = null;
	Map<String, String> map = new HashMap<>();
	List<String> shiftList = new ArrayList<>();
	
	/**
	 * This is the invoke point of the Service
	 * @throws  
	 * 
	 */
	
	@Override
	public YFCDocument invoke(YFCDocument inXml) {
		YFCElement calInEle = inXml.getDocumentElement();
		List<String> exceptionList = new ArrayList<>();
		YFCIterable<YFCElement> eleCalendar = calInEle.getChildren(XMLLiterals.CALENDAR);
		String organizationCode = "";
		for(YFCElement eleCal : eleCalendar) {
			if(XmlUtils.isVoid(organizationCode)) {
				organizationCode=eleCal.getAttribute(XMLLiterals.ORGANIZATION_CODE);
				String sEffectiveFromDate = dateFormatter(eleCal.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE));
				createCalendarInputDoc(organizationCode,sEffectiveFromDate);
			}
			else if(!organizationCode.equals(eleCal.getAttribute(XMLLiterals.ORGANIZATION_CODE))) {
				createCalendar(exceptionList);
				createResourcePool(organizationCode);
				organizationCode=eleCal.getAttribute(XMLLiterals.ORGANIZATION_CODE);
				String sEffectiveFromDate = dateFormatter(eleCal.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE));
				createCalendarInputDoc(organizationCode,sEffectiveFromDate);
			}
			String sSerSlotShiftStartTime = eleCal.getAttribute(XMLLiterals.SHIFT_START_TIME);
			String sSerSlotShiftEndTime = eleCal.getAttribute(XMLLiterals.SHIFT_END_TIME);
	 			try {
	 				if(EXCEPTION_TIME.equals(sSerSlotShiftStartTime) && EXCEPTION_TIME.equals(sSerSlotShiftEndTime)) 
	 					exceptionList.add(dateFormatter(eleCal.getAttribute(XMLLiterals.EFFECTIVE_FROM_DATE)));
	 				else
	 					setShiftValues(eleCal);
	 			}
	 			catch (ParseException e) {
	 				throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INVALID_DATE, e);
	 			}
		}
		createCalendar(exceptionList);
		createResourcePool(organizationCode);
		return docCreateCalenderInXml;
	}
	  
	/**
	 *This method form document for createCalendar API input
	 * @param organizationCode
	 * @param effectiveFromDate
	 * @return
	 */
	
	public YFCDocument createCalendarInputDoc(String organizationCode,String sEffectiveFromDate)
	{
		String calendarId = organizationCode + CALENDER;
		docCreateCalenderInXml = YFCDocument.createDocument(XMLLiterals.CALENDAR);
		YFCElement  createCalenderEle =  docCreateCalenderInXml.getDocumentElement();
		createCalenderEle.setAttribute(XMLLiterals.ORGANIZATION_CODE, organizationCode);
		createCalenderEle.setAttribute(XMLLiterals.CALENDER_ID, calendarId);
		createCalenderEle.setAttribute(XMLLiterals.CALENDAR_DESCRIPTION, calendarId);
		YFCElement effectivePeriods = createCalenderEle.createChild(XMLLiterals.EFFECTIVE_PERIODS).createChild(XMLLiterals.EFFECTIVE_PERIOD);
		effectivePeriods.setAttribute(XMLLiterals.EFFECTIVE_FROM_DATE, sEffectiveFromDate);
		return docCreateCalenderInXml;
	}
	 
	/**
	 * This method creates shift
	 * @param createCalenderXml
	 * @return
	 */
	
	public void createCalShift() {
		String yes = "Y";
		String effectiveToDate = getProperty("EFFECTIVE_TO_DATE");
		String defShiftStartTime = getProperty("SHIFT_START_TIME");
		String defShiftEndTime = getProperty("SHIFT_END_TIME");
		YFCElement effectivePeriodsEle = docCreateCalenderInXml.getDocumentElement().getChildElement(XMLLiterals.EFFECTIVE_PERIODS);
		YFCElement effectivePeriodEle = effectivePeriodsEle.getChildElement(XMLLiterals.EFFECTIVE_PERIOD);
		effectivePeriodEle.setAttribute(XMLLiterals.EFFECTIVE_TO_DATE, effectiveToDate);
		YFCElement shiftEle=effectivePeriodEle.createChild(XMLLiterals.SHIFTS).createChild(XMLLiterals.SHIFT);
		shiftEle.setAttribute(XMLLiterals.WEDNESDAY_VALID,yes);
		shiftEle.setAttribute(XMLLiterals.TUESDAY_VALID,yes);
		shiftEle.setAttribute(XMLLiterals.THURSDAY_VALID,yes);
		shiftEle.setAttribute(XMLLiterals.SUNDAY_VALID,yes);
		shiftEle.setAttribute(XMLLiterals.SATURDAY_VALID,yes);
		shiftEle.setAttribute(XMLLiterals.MONDAY_VALID,yes);
		shiftEle.setAttribute(XMLLiterals.FRIDAY_VALID,yes);
		shiftEle.setAttribute(XMLLiterals.SHIFT_START_TIME,defShiftStartTime);
		shiftEle.setAttribute(XMLLiterals.SHIFT_END_TIME,defShiftEndTime);
	}
	
	/**
	 * 
	 * This method formats the date
	 * @param fromDate
	 * @return
	 * @throws ParseException
	 */
		
	public String dateFormatter(String fromDate) {
		String newDateString="";
		try {
			final String oldFormat = "yyyy/MM/dd";
			final String newFormat = "yyyy-MM-dd";
			SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
			Date d = sdf.parse(fromDate);
			sdf.applyPattern(newFormat);
			newDateString = sdf.format(d);
		} catch(Exception exp) {
			throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_INVALID_DATE, exp);
		}
		return newDateString;
	}
		
	/**
	 * This method formats the time
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	
	public String timeFormatter(String time) throws ParseException {
		final String oldFormat = "HH mm";
		final String newFormat = "HH:mm:ss";
		String newTimeString;
		SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
		Date d = sdf.parse(time);
		sdf.applyPattern(newFormat);
		newTimeString = sdf.format(d);
		return newTimeString;	
	}
		
	/** This method forms the template XML for getCelendarListListApi
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
		effecPeriodsEle.createChild(XMLLiterals.SHIFTS).createChild(XMLLiterals.SHIFT).setAttribute(XMLLiterals.SHIFT_KEY, EMPTY_STRING);
		YFCElement calDayExcepEle=calendarEle.createChild(XMLLiterals.CALENDAR_DAY_EXCEPTIONS).createChild(XMLLiterals.CALENDAR_DAY_EXCEPTION);
		calDayExcepEle.setAttribute(XMLLiterals.EXCEPTION_TYPE,EMPTY_STRING);
		calDayExcepEle.setAttribute(XMLLiterals.DATE,EMPTY_STRING);
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
		return getCalendarXml;
	}
		 
	/**
	 * This method invoke createCalendarApi and changeCalendarApi
	 * @param exceptionList
	 * @param effectiveFromDate
	 */
		 
	public void createCalendar(List<String> exceptionList) {
		createCalShift();
		YFCElement createCalInputEle = docCreateCalenderInXml.getDocumentElement();
		YFCElement exceptioncalEle = createCalInputEle.createChild(XMLLiterals.CALENDAR_DAY_EXCEPTIONS);
		for(String exceptionDate:exceptionList) {
			YFCElement excepDayEle = exceptioncalEle.createChild(XMLLiterals.CALENDAR_DAY_EXCEPTION);
			excepDayEle.setAttribute(XMLLiterals.DATE, exceptionDate);
			excepDayEle.setAttribute(XMLLiterals.EXCEPTION_TYPE, OFF_DAY);
		}
		String calenderId = docCreateCalenderInXml.getDocumentElement().getAttribute(XMLLiterals.CALENDER_ID);
		String orgCode = docCreateCalenderInXml.getDocumentElement().getAttribute(XMLLiterals.ORGANIZATION_CODE);
		if(!XmlUtils.isVoid(calenderId) && getCalendarDetails(orgCode,calenderId).getDocumentElement().hasChildNodes()) {
			exceptionList.clear();
			invokeYantraApi(XMLLiterals.CHANGE_CALENDAR, docCreateCalenderInXml);
			manageSerSlot(orgCode);
		} else {
			invokeYantraApi(XMLLiterals.CREATE_CALENDAR, docCreateCalenderInXml);
			manageSerSlot(orgCode);
			exceptionList.clear();
		}
	}
		 
	/**
	 * This method invokes getCalendarDetails api
	 * @param organizationCode
	 * @param calenderId
	 * @return
	 */
	
	public YFCDocument getCalendarDetails(String organizationCode, String calenderId) {
		YFCDocument calenderList = invokeYantraApi(XMLLiterals.GET_CALENDAR_LIST, 
				getCalendarDetailsInDoc(organizationCode,calenderId),getCalendarListTempDoc());
		if(calenderList.getDocumentElement().hasChildNodes()) {
			YFCDocument calenderDetailDoc = invokeYantraApi(XMLLiterals.GET_CALENDAR_DETAILS, 
					getCalendarDetailsInDoc(organizationCode,calenderId),getCalendarDetailsTemplDoc());
			getCalenderListForException(calenderDetailDoc);
			}
		return calenderList;
	}
		 
	/**
	 * This method is used to store the shiftStartTime and shiftEndTime
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
	 * This method invokes manageServiceSlot API
	 * @param orgCode
	 */
		 
	private void manageSerSlot(String orgCode) {
		Set<String> keySet=map.keySet();
		YFCDocument mangSlotDoc = YFCDocument.createDocument(XMLLiterals.SERVICE_SLOT_GROUP);
		YFCElement serviceSlotEle=mangSlotDoc.getDocumentElement();
		serviceSlotEle.setAttribute(XMLLiterals.ITEM_GROUP_CODE, ITEM_GROUP_CODE);
		serviceSlotEle.setAttribute(XMLLiterals.ORGANIZATION_KEY,XMLLiterals.INDIGO_CA);
		serviceSlotEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_ID,orgCode+PICK+XMLLiterals.SERVICE_SLOT_GROUP);
		serviceSlotEle.setAttribute(XMLLiterals.SERVICE_SLOT_GROUP_DESC,orgCode+PICK+XMLLiterals.SERVICE_SLOT_GROUP);
		YFCElement slotList=serviceSlotEle.createChild(XMLLiterals.SERVICE_SLOT_LIST);
		for(Object s:keySet) {
			String[] shiftTime = map.get(s).split("-");
			if(!shiftList.contains(shiftTime[0]) ){
				shiftList.add(shiftTime[0]);	
			}
			YFCElement serlot=slotList.createChild(XMLLiterals.SERVICE_SLOT);
			serlot.setAttribute(XMLLiterals.SERVICE_SLOT_DESC, orgCode+PICK+XMLLiterals.SERVICE_SLOT_GROUP);
			serlot.setAttribute(XMLLiterals.START_TIME,shiftTime[0]);
			serlot.setAttribute(XMLLiterals.END_TIME, shiftTime[1]);
		}
		setShift(slotList,shiftList);
		invokeYantraApi(XMLLiterals.MANAGE_SERVICE_SLOT_GROUP,mangSlotDoc);
		map.clear();
	}

	/**
	 * This method form the shift list
	 * @param slotList
	 * @param shiftList
	 */
	
	private static void setShift(YFCElement slotList,List<String> shiftList ) {
		String sStartTime="00:00:00";
		for(String shiftTime:shiftList) {
			YFCElement serlot=slotList.createChild(XMLLiterals.SERVICE_SLOT);
			serlot.setAttribute(XMLLiterals.START_TIME,sStartTime);
			serlot.setAttribute(XMLLiterals.END_TIME, shiftTime);
		}
	}
			
	/**
	 * 
	 * This method creates input for getResourceList API
	 * @param organizationCode
	 * @param capacityUnitOfMeasure
	 * @param resourcePoolId
	 * @return
	 */
			
	public YFCDocument getResourcePoolListInDoc (String organizationCode, String capacityUnitOfMeasure,String resourcePoolId){
		YFCDocument resourceList = YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
		YFCElement resourcePoolEle = resourceList.getDocumentElement();
		resourcePoolEle.setAttribute(XMLLiterals.NODE,organizationCode);
		resourcePoolEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, capacityUnitOfMeasure);
		resourcePoolEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID, resourcePoolId);
		return resourceList; 
	}
			
	/**
	 * This method creates template for getRsourcePoolList API
	 * @return
	 */
			
	public YFCDocument getResourcePoolListTempDoc() {
		YFCDocument getResourcePoolTemp = YFCDocument.createDocument(XMLLiterals.RESOURCE_POOLS);
		YFCElement resourcePoolEle = getResourcePoolTemp.getDocumentElement().createChild(XMLLiterals.RESOURCE_POOL);
		resourcePoolEle.setAttribute(XMLLiterals.RESOURCE_POOL_ID, EMPTY_STRING);
		resourcePoolEle.setAttribute(XMLLiterals.NODE, EMPTY_STRING);
		resourcePoolEle.setAttribute(XMLLiterals.CAPACITY_UNIT_OF_MEASURE, EMPTY_STRING);
		return getResourcePoolTemp;
	}
	
	/**
	 * this method invokes getResourcePoolList api
	 * @param node
	 * @param capacityUnitOfMeasure
	 * @param resourcePoolId
	 * @return
	 */
	
	public YFCDocument getResourcepool(String node, String capacityUnitOfMeasure,String resourcePoolId) {
		return invokeYantraApi(XMLLiterals.GET_RESOURCE_POOL_LIST, 
				getResourcePoolListInDoc(node,capacityUnitOfMeasure,resourcePoolId),getResourcePoolListTempDoc());
	}
	
	/**
	 * this method form input for createResourcePool api
	 * @param purpose
	 * @param organizationCode
	 * @param resourcePoolId
	 * @param deliveryMethod
	 * @param capacityUnitOfMeasure
	 * @param calendarId
	 * @return
	 */
	
	public YFCDocument resourcePoolInDoc(String purpose,String organizationCode, String resourcePoolId, String deliveryMethod,
			String capacityUnitOfMeasure,String calendarId) {
		YFCDocument createResourcePoolXml = YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
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
		return createResourcePoolXml;
	}
			 
	/**
	 * This method invokes createResourcePool API
	 * @param organizationCode
	 * @param calendarId
	 */
			 
	public void createResourcePool(String organizationCode) {
		String resourcePoolId=organizationCode+POOLID;
		String calendarId=organizationCode+CALENDER;
		String deliveryMethod = "PICK";
		String capacityUnitOfMeasure = "RELEASE";
		String purpose = "INVENTORY";
		if(!getResourcepool(organizationCode,capacityUnitOfMeasure,resourcePoolId).getDocumentElement().hasChildNodes()) {
			invokeYantraApi(XMLLiterals.CREATE_RESOURCE_POOL,resourcePoolInDoc(purpose,organizationCode,resourcePoolId,deliveryMethod,capacityUnitOfMeasure,calendarId));
		}
	}
	
	/**
	 * This method forms template for getCalendarList API
	 * @return
	 */
	
	private YFCDocument getCalendarListTempDoc(){
		YFCDocument inputXml = YFCDocument.createDocument(XMLLiterals.CALENDERS);
		inputXml.getDocumentElement().createChild(XMLLiterals.CALENDAR).setAttribute(XMLLiterals.CALENDER_ID, EMPTY_STRING);
		return inputXml;
	}
	
	/**
	 * This method gets the list of exception dates
	 * @param calenderInXml
	 * @param calenderDetailXml
	 */
	
	private void getCalenderListForException(YFCDocument calenderDetailXml){
		YFCIterable<YFCElement> yfsIrator = calenderDetailXml.getDocumentElement()
				.getChildElement(XMLLiterals.CALENDAR_DAY_EXCEPTIONS).getChildren(XMLLiterals.CALENDAR_DAY_EXCEPTION);
		for(YFCElement calenderDayException : yfsIrator) {
	    String exceptionDate = calenderDayException.getAttribute(XMLLiterals.DATE);
	    String shiftKey = XPathUtil.getXpathAttribute(calenderDetailXml, "//Shifts/Shift/@ShiftKey");
	    YFCElement exceptionEle = XPathUtil.getXPathElement(docCreateCalenderInXml,"//CalendarDayExceptions/CalendarDayException[@Date=\""+exceptionDate+"\"]");
	    if(XmlUtils.isVoid(exceptionEle)) {
	    	YFCElement inputExcepEle = docCreateCalenderInXml.getDocumentElement().getChildElement(XMLLiterals.EFFECTIVE_PERIODS);
	    	if(XmlUtils.isVoid(inputExcepEle)) {
	    		inputExcepEle = docCreateCalenderInXml.getDocumentElement().createChild(XMLLiterals.CALENDAR_DAY_EXCEPTIONS)
	    				.createChild(XMLLiterals.CALENDAR_DAY_EXCEPTION);
	    		inputExcepEle.setAttribute(XMLLiterals.DATE, exceptionDate);
	    		inputExcepEle.setAttribute(XMLLiterals.EXCEPTION_TYPE, WORKING_DAY);
	    		inputExcepEle.createChild(XMLLiterals.EXCEPTION_SHIFTS).createChild(XMLLiterals.EXCEPTION_SHIFT).setAttribute(XMLLiterals.SHIFT_KEY, shiftKey);
	    		formMessageOnChange(calenderDetailXml);
	    	} 
	    	else {
	    		inputExcepEle = docCreateCalenderInXml.getDocumentElement().getChildElement(XMLLiterals.CALENDAR_DAY_EXCEPTIONS).createChild(XMLLiterals.CALENDAR_DAY_EXCEPTION);
	    		inputExcepEle.setAttribute(XMLLiterals.DATE, exceptionDate);
	    		inputExcepEle.setAttribute(XMLLiterals.EXCEPTION_TYPE, WORKING_DAY);
	    		inputExcepEle.createChild(XMLLiterals.EXCEPTION_SHIFTS).createChild(XMLLiterals.EXCEPTION_SHIFT).setAttribute(XMLLiterals.SHIFT_KEY, shiftKey);
	    		formMessageOnChange(calenderDetailXml);
	    		}
	    	}	
		}
	}
	
	/**
	 * This method drop a message when working day is changed from non working day and vice versa
	 * @param calenderDetailXml
	 */
	
	private void formMessageOnChange(YFCDocument calenderDetailXml) {
		String sProd = "PROD";		
		YFCElement docCalenderDetail=calenderDetailXml.getDocumentElement();
		YFCDocument docGetResPoolCapcityDetails=YFCDocument.createDocument(XMLLiterals.RESOURCE_POOL);
	    YFCElement eleResourcePool=docGetResPoolCapcityDetails.getDocumentElement();
	    eleResourcePool.setAttribute(XMLLiterals.CAPACITYORGCODE, XMLLiterals.INDIGO_CA);
	    eleResourcePool.setAttribute(XMLLiterals.RESOURCE_POOL_ID,docCalenderDetail.getAttribute(XMLLiterals.ORGANIZATION_CODE)+POOLID);
	    eleResourcePool.setAttribute(XMLLiterals.PROVIDER_ORGANIZATION_CODE,XMLLiterals.INDIGO_CA);
	    eleResourcePool.setAttribute(XMLLiterals.NODE,docCalenderDetail.getAttribute(XMLLiterals.ORGANIZATION_CODE));
	    eleResourcePool.setAttribute(XMLLiterals.ITEM_GROUP_CODE,sProd);
		invokeYantraService(INDG_GET_RESOURCE_POOL_CAPACITY , docGetResPoolCapcityDetails);
	}
}