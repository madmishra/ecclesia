package com.indigo.om.outbound.api;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.consts.XMLLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;


public class IndgReturnSAP071 extends AbstractCustomApi {

	@Override
	public YFCDocument invoke(YFCDocument inXml)  {
		String NO = "N";
		String YES = "Y";
		YFCElement eleInXml = inXml.getDocumentElement().getChildElement(XMLLiterals.MESSAGE_BODY).getChildElement(XMLLiterals.ORDER);
			String sResendSAP071 = eleInXml.getAttribute(XMLLiterals.RESEND_SAP071);
		if(YFCObject.isVoid(sResendSAP071))
		{
			sResendSAP071 = NO;
			throwError();
			invokeYantraService(XMLLiterals.RETURN_ORDER, inXml);
		}
		else
		{
			if(sResendSAP071.equals(YES)) {
				invokeYantraService(XMLLiterals.RETURN_ORDER_ELSE, inXml);
			}
		}
		return inXml;
	}
	
	private void throwError()
	{
		YFCDocument errorDoc = YFCDocument.createDocument("Errors");
		YFCElement eleErrors = errorDoc.getDocumentElement();
		YFCElement eleError = eleErrors.createChild("Error");
		eleError.setAttribute("ErrorCode", "ERRORCODE_NEW_TEST");
		eleError.setAttribute("ErrorDescription", "New Error test");
		throw ExceptionUtil.getYFSException(ExceptionLiterals.ERRORCODE_NEW_TEST, errorDoc.toString());  
			 
	}

}
