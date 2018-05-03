package com.indigo.utils;

import java.util.ArrayList;
import java.util.Collection;

import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;

/**
 * 
 * @author BSG109
 * 
 * This is an custom Utility class to find the difference in
 * Input Document with Output List document of specific List API.
 *
 */
public class IndgManageDeltaLoadUtil {
  
  /**
   * Private Constructor for Class
   */
  private IndgManageDeltaLoadUtil(){
    
  }
  /**
   * 
   * This method is invoke point of the class
   * 
   * @param inputFeedDoc
   * @param apiOutputDoc
   * @param attributeName
   * @param nodeName
   * @return
   */
  public static Collection<String> manageDeltaLoadForDeletion(YFCDocument inputFeedDoc, YFCDocument apiOutputDoc,String attributeName,
      String nodeName) {
    Collection<String> inputFeedList = addandCompareList(inputFeedDoc,attributeName,nodeName);
    Collection<String> apiOutputList = addandCompareList(apiOutputDoc,attributeName,nodeName);
    apiOutputList.removeAll(inputFeedList);
    return apiOutputList;
  }
  
  /**
   * 
   * This method is invoke point of the class
   * 
   * @param inputFeedDoc
   * @param apiOutputDoc
   * @param inputAttributeName
   * @param inputNodeName
   * @param apiAttributeName
   * @param apiNodeName
   * @return
   */
  public static Collection<String> manageDeltaLoadForDeletion(YFCDocument inputFeedDoc, YFCDocument apiOutputDoc,String inputAttributeName,
      String inputNodeName,String apiAttributeName,String apiNodeName) {
    Collection<String> inputFeedList = addandCompareList(inputFeedDoc,inputAttributeName,inputNodeName);
    Collection<String> apiOutputList = addandCompareList(apiOutputDoc,apiAttributeName,apiNodeName);
    apiOutputList.removeAll(inputFeedList);
    return apiOutputList;
  }
  
  /**
   * This method iterate throw the document to get the Attribute
   * value passed in the parameters and add them the list and return
   * difference in list.
   * 
   * @param yfcDoc
   * @param attributeName
   * @param nodeName
   * @return
   */
  private static Collection<String> addandCompareList(YFCDocument yfcDoc,String attributeName,
      String nodeName){
    YFCElement yfsElement = yfcDoc.getDocumentElement();
    YFCIterable<YFCElement> yfsItator = yfsElement.getChildren(nodeName);
    Collection<String> listCollection = new ArrayList<>() ;
    for(YFCElement yfcNode: yfsItator) {
      listCollection.add(yfcNode.getAttribute(attributeName));
    }
    return listCollection;
  }
  
}
