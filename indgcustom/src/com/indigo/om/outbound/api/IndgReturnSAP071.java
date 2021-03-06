package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;


public class IndgReturnSAP071 extends AbstractCustomApi {
	private static final String NO = "N";
	private static final String YES = "Y";
	private static final String INDG_LEGACY072 = "INDG_LEGACY072";
	private static final String  INDG_RESEND_SAP071= "Indg_ResendSAP071";
	private static final String INDG_SAP071="Indg_SAP071";
	private static final String ALWAYS_PUBLISH_TO_LEGACY = "ALWAYS_PUBLISH_TO_LEGACY";
	@Override
	public YFCDocument invoke(YFCDocument inXml)  {
		
	
		YFCElement eleInXml = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			String sResendSAP071 = eleInXml.getAttribute(XMLLiterals.RESEND_SAP071);
		if(YFCObject.isVoid(sResendSAP071))
		{
			if(getProperty(ALWAYS_PUBLISH_TO_LEGACY).equals(YES)) {
			invokeYantraService(getProperty(INDG_LEGACY072), inXml);
			}
			eleInXml.setAttribute(XMLLiterals.RESEND_SAP071, NO);
			invokeYantraService(INDG_RESEND_SAP071, inXml);
			
		}
		else if(sResendSAP071.equals(NO) )
		{
			throwError();
			}
		
		else {
			if(sResendSAP071.equals(YES)) {
				invokeYantraService(getProperty(INDG_SAP071),inXml);
			}
		}
		return inXml;
	}
	
	private void throwError()
	{
		YFCDocument errorDoc = YFCDocument.createDocument("Errors");
		YFCElement eleErrors = errorDoc.getDocumentElement();
		YFCElement eleError = eleErrors.createChild("Error");
		eleError.setAttribute("ErrorCode", "ERRORCODE_RESEND_SAP071");
		eleError.setAttribute("ErrorDescription", "Resend SAP071 Message");
		throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_RESEND_SAP071, errorDoc.toString());  
			 
	}

}
