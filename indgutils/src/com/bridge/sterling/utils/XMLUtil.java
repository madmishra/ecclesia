package com.bridge.sterling.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.bridge.sterling.consts.ExceptionLiterals;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;

public class XMLUtil {

  /**
   * Takes a YFCDocument as input and returns equivalent DOM document if it is not null. Else, it
   * returns null
   * 
   * @param yDoc YFCDocument
   * @return DOM document if yDoc is not null
   */
  public static Document getDocument(YFCDocument yDoc) {
    return SCUtil.isVoid(yDoc) ? null : yDoc.getDocument();
  }

  /**
   * Takes a DOM Document as input and returns equivalent YFCDocument if it is not null. Else, it
   * returns null
   * 
   * @param doc Document
   * @return YFCDocument if input is not null
   */
  public static YFCDocument getYFCDocument(Document doc) {
    return SCUtil.isVoid(doc) ? null : YFCDocument.getDocumentFor(doc);
  }

  /**
   * Takes YFCDocument as input and returns root Element if root element is not null. Else throws
   * exception Else, it returns null
   * 
   * @param yDoc YFCDocument
   * @return YFCElement if root element is not null
   */
  public static YFCElement getRootElement(YFCDocument yDoc) {
    if (SCUtil.isVoid(yDoc) || SCUtil.isVoid(yDoc.getDocumentElement())) {
      // ERR9900017: Input XML is Null
      throw ExceptionUtil.getYFSException(ExceptionLiterals.STERLING_XML_NULL);
    }
    return yDoc.getDocumentElement();
  }

  /**
   * Takes an YFCElement and child name and return an list of YFCDocuments for the children. If
   * child name is null or blank, all the children will be returned
   * 
   * @param yElement YFCElement
   * @param String childName
   * @return YFCIterable<YFCElement> of children
   */
  public static YFCIterable<YFCElement> getChildren(YFCElement yElement, String childName) {
    YFCIterable<YFCElement> children = null;
    if (!SCUtil.isVoid(yElement)) {
      children =
          SCUtil.isVoid(childName) ? yElement.getChildren() : yElement.getChildren(childName);
    }
    return children;
  }

  /**
   * Takes and iterable (YFCIterable, List, any collection, ..) of YFCNode/YFCElement and returns a
   * list of YFCDocument
   * 
   * @param listOfElements Iterable<? extends YFCNode>
   * @return List<YFCDocument> for the elements in List
   */
  public static List<YFCDocument> getDocumentsFromElements(
      Iterable<? extends YFCNode> listOfElements) {
    List<YFCDocument> listDocument = new ArrayList<YFCDocument>();
    if (!SCUtil.isVoid(listOfElements)) {
      for (YFCNode eleNode : listOfElements) {
        listDocument.add(getDocumentFor(eleNode));
      }
    }
    return listDocument;
  }

  /**
   * Takes a YFCNode returns a YFCDocument with that node as root
   * 
   * @param listOfElements Iterable<? extends YFCNode>
   * @return List<YFCDocument> for the elements in List
   */
  public static YFCDocument getDocumentFor(YFCNode node) {
    YFCDocument doc = YFCDocument.createDocument();
    YFCNode nodeImp = doc.importNode(node, true);
    doc.appendChild(nodeImp);
    return doc;
  }

  /**
   * Takes an YFCElement and child name and return a list of YFCDocuments for the children. If child
   * name is null or blank, documents for all the children will be returned list of YFCDocument
   * 
   * @param yElement YFCElement
   * @param String childName
   * @return List<YFCDocument> for all the children
   */
  public static List<YFCDocument> getDocumentsForChildElements(YFCElement yElement, String childName) {
    return getDocumentsFromElements(getChildren(yElement, childName));
  }
  
  /**
   * import element to a parent element.
   * @param yfcEleParentEle
   * @param yfcEleChildEle
   */
  public static void importNode(YFCElement yfcEleParentEle,
			YFCElement yfcEleChildEle) {
		YFCDocument yfcDocOwnerDoc = yfcEleParentEle.getOwnerDocument();
		YFCElement newEle = yfcDocOwnerDoc.importNode(yfcEleChildEle, true);
		yfcEleParentEle.appendChild(newEle);
	} 
}
