package com.bridge.sterling.utils;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.dom.YFCNodeList;

public class XPathUtil {

  /**
   * This method returns the value of attribute from YFCDocument matching the xpath string.
   * 
   * @param yDoc YFCDocument
   * @param xpath XPath URL
   * @return String Value of the attribute matching xpath
   */
  public static String getXpathAttribute(final YFCDocument yDoc, final String xpath) {
    validateInput(yDoc, xpath);
    return SCXmlUtil.getXpathAttribute(yDoc.getDocument().getDocumentElement(), xpath);
  }


  /**
   * This method returns the value of attribute from YFCDocument matching the xpath string. If no
   * value, returns the default value
   * 
   * @param yDoc YFCDocument
   * @param xpath XPath URL
   * @return String Value of the attribute matching xpath or default value
   */

  public static String getXpathAttributeWithDefaultValue(final YFCDocument yDoc,
      final String xpath, final String defaultValue) {
    String attrValue = getXpathAttribute(yDoc, xpath);
    return (SCUtil.isVoid(attrValue)) ? defaultValue : attrValue;
  }


  /**
   * This method returns the first element in a YFCDocument object for xpath specified .
   * 
   * @param yDoc YFCDocument Object
   * @param xpath XPath URL
   * @return YFCElement First element that matches xpath
   */
  public static YFCElement getXPathElement(final YFCDocument yDoc, final String xpath) {
    validateInput(yDoc, xpath);
    Element elem = SCXmlUtil.getXpathElement(yDoc.getDocument().getDocumentElement(), xpath);
    return SCUtil.isVoid(elem) ? null : new YFCElement(yDoc, elem);
  }

  /**
   * This method returns the list of YFCNodes from a YFCDocument object for xpath specified .
   * 
   * @param yDoc YFCDocument Object
   * @param xpath XPath URL
   * @return YFCNodeList<YFCNode> list of YFCNodes matching xpath
   */
  public static YFCNodeList<YFCNode> getXpathNodeList(final YFCDocument yDoc, final String xpath) {
    validateInput(yDoc, xpath);
    NodeList nodes = SCXmlUtil.getXpathNodes(yDoc.getDocument().getDocumentElement(), xpath);
    return SCUtil.isVoid(nodes) ? null : new YFCNodeList<YFCNode>(yDoc, nodes);
  }

  public static boolean isValidXPath(String xPath) {
    return !SCUtil.isVoid(xPath) && xPath.startsWith("/");
  }

  private static void validateInput(final YFCNode node, final String xpath) {
    if (SCUtil.isVoid(node)) {
      // ERR9900017: Input XML is Null
      throw ExceptionUtil.getYFSException("ERR9900017");
    }
    if (SCUtil.isVoid(xpath)) {
      // ERR0000171=Invalid XPath String
      throw ExceptionUtil.getYFSException("ERR0000171");
    }
  }
}
