package com.bridge.sterling.service.utility;

import java.util.HashMap;
import java.util.Map;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.bridge.sterling.framework.api.AbstractCustomApi;
import com.bridge.sterling.utils.ExceptionUtil;
import com.bridge.sterling.utils.LoggerUtil;
import com.bridge.sterling.utils.XMLUtil;
import com.bridge.sterling.utils.XPathUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;

public class ForEach extends AbstractCustomApi {

  String sXPath = null;
  String sMergeOutput = null;
  String sMergeElement = null;
  String sAPIName = null;
  String sServiceName = null;

  private static YFCLogCategory logger = YFCLogCategory.instance(ForEach.class);

  public YFCDocument invoke(YFCDocument inXML) throws YFSException {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "invoke", inXML);

    YFCDocument outXML = null;
    setVariables();
    if (validateProperties()) {
      outXML = doForEach(inXML);
    }

    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "invoke", outXML);
    return outXML;
  }

  public void setVariables() throws YFSException {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "setVariables", "");
    this.sXPath = getProperty("XPath").trim();
    this.sMergeOutput = getProperty("MergeOutput").trim().toUpperCase();
    this.sMergeElement = getProperty("MergeElement").trim();
    this.sAPIName = getProperty("APIName").trim();
    this.sServiceName = getProperty("ServiceName").trim();
    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "setVariables", "");
  }

  private boolean validateProperties() {
    if (SCUtil.isVoid(this.sXPath) || SCUtil.isVoid(this.sMergeOutput)) {
      Map<String, Object> expMap = new HashMap<String, Object>();
      expMap.put("XPath", this.sXPath);
      expMap.put("MergeOutput", this.sMergeOutput);
      throw ExceptionUtil.getYFSException(ExceptionLiterals.MISSING_REQ_CONFIG, expMap);
    }

    if (!this.sMergeOutput.equals("R") && !this.sMergeOutput.equals("A")
        && !this.sMergeOutput.equals("N")) {
      Map<String, Object> expMap = new HashMap<String, Object>();
      expMap.put("MergeOutput", this.sMergeOutput);
      expMap.put("Valid values", "[A, R, N] - [Append, Replace, None]");
      // ERR0000164: Invalid Properties
      throw ExceptionUtil.getYFSException("ERR0000164", expMap);
    }

    if (SCUtil.isVoid(this.sAPIName) && SCUtil.isVoid(this.sServiceName)) {
      throw ExceptionUtil.getYFSException(ExceptionLiterals.MISSING_REQ_CONFIG,
          "Either APIName or ServiceName should be specified");
    }
    return true;
  }

  private YFCDocument doForEach(YFCDocument inXML) throws YFSException {
    LoggerUtil.startComponentLog(logger, this.getClass().getName(), "doForEach", inXML);

    YFCNodeList<YFCNode> nlNodeList = XPathUtil.getXpathNodeList(inXML, this.sXPath);

    if (nlNodeList.getLength() <= 0) {
      LoggerUtil.debugLog("No elements to process : XPath " + this.sXPath, logger, inXML);
    }

    for (YFCNode node : nlNodeList) {
      LoggerUtil.verboseLog("Node being processed by For each", logger, node);
      YFCDocument outDoc = callApiOrService(node);
      if (!SCUtil.isVoid(outDoc)) {
        mergeOutput(inXML, node, outDoc);
      }
    }

    LoggerUtil.endComponentLog(logger, this.getClass().getName(), "doForEach", inXML);
    return inXML;
  }



  private YFCDocument callApiOrService(YFCNode node) {
    YFCDocument outDoc;
    if (!SCUtil.isVoid(sAPIName)) {
      outDoc = invokeYantraApi(sAPIName, XMLUtil.getDocumentFor(node));
    } else {
      outDoc = invokeYantraService(sServiceName, XMLUtil.getDocumentFor(node));
    }
    return outDoc;
  }

  private void mergeOutput(YFCDocument inXML, YFCNode node, YFCDocument outDoc) {
    if("A".equals(sMergeOutput)) {
      appendToNode(inXML, node, outDoc);
    } else if("R".equals(sMergeOutput)){
      replaceNode(inXML, node, outDoc);
    }
  }

  private void appendToNode(YFCDocument currDoc, YFCNode inNode, YFCDocument newDoc) {
    YFCNode newNode = currDoc.importNode(newDoc.getDocumentElement(), true);
    if (!SCUtil.isVoid(sMergeElement)) {
      YFCElement tempEle = currDoc.createElement(sMergeElement);
      newNode = tempEle.appendChild(newNode);
      newNode = tempEle;
    }
    inNode.appendChild(newNode);
  }

  private void replaceNode(YFCDocument currDoc, YFCNode inNode, YFCDocument newDoc) {
    YFCNode newNode = currDoc.importNode(newDoc.getDocumentElement(), true);
    inNode.getParentNode().replaceChild(newNode.getDOMNode(), inNode.getDOMNode());
  }
}
