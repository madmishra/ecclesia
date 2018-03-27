package com.bridge.sterling.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.yantra.yfc.dblayer.YFCDBException;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;

public final class LoggerUtil {
  private static final int MAX_DEPTH = 5;

  protected LoggerUtil() {}

  /**
   * Method: fatalLog This method will print 'fatal' information.
   * 
   * @param message : Initial message
   * @param logger : YFCLogCategory
   * @param object : Object
   */
  public static void fatalLog(String message, final YFCLogCategory logger, final Object object) {
    String outputLogMessage = generateLogMessage(message, object);

    if (outputLogMessage != null) {
      logger.fatal(outputLogMessage);
    }
  }

  /**
   * Method: errorLog This method will print 'error' information.
   * 
   * @param message : Initial message
   * @param logger : YFCLogCategory
   * @param object : Object
   */
  public static void errorLog(String message, final YFCLogCategory logger, final Object object) {
    String outputLogMessage = generateLogMessage(message, object);

    if (outputLogMessage != null) {
      logger.error(outputLogMessage);
    }
  }

  /**
   * Method: errorDtlLog This method will print 'errordtl' information.
   * 
   * @param message : Initial message
   * @param logger : YFCLogCategory
   * @param object : Object
   */
  public static void errorDtlLog(String message, final YFCLogCategory logger, final Object object) {
    String outputLogMessage = generateLogMessage(message, object);

    if (outputLogMessage != null) {
      logger.errordtl(outputLogMessage);
    }
  }

  /**
   * Method: warnLog This method will print 'warn' information.
   * 
   * @param message : Initial message
   * @param logger : YFCLogCategory
   * @param object : Object
   */
  public static void warnLog(String message, final YFCLogCategory logger, final Object object) {
    if (logger.isEnabledFor(YFCLogLevel.WARN)) {
      String outputLogMessage = generateLogMessage(message, object);

      if (outputLogMessage != null) {
        logger.warn(outputLogMessage);
      }
    }
  }

  /**
   * Method: infoLog This method will print 'info' information to log file configured with log4j. It
   * can print the following String, HashMap, Document, any object's toString(), Properties.
   * 
   * @param message : Initial message
   * @param logger : YFCLogCategory
   * @param object : Object
   * @deprecated - Yantra does not handle info logs
   */
  public static void infoLog(String message, final YFCLogCategory logger, final Object object) {
    if (logger.isEnabledFor(YFCLogLevel.INFO)) {
      String outputLogMessage = generateLogMessage(message, object);

      if (outputLogMessage != null) {
        logger.info(outputLogMessage);
      }
    }
  }

  /**
   * Method: debugLog This method will print 'debug' information.
   * 
   * @param message : Initial message
   * @param logger : YFCLogCategory
   * @param object : Object
   */
  public static void debugLog(String message, final YFCLogCategory logger, final Object object) {
    if (logger.isEnabledFor(YFCLogLevel.DEBUG)) {
      String outputLogMessage = generateLogMessage(message, object);

      if (outputLogMessage != null) {
        logger.debug(outputLogMessage);
      }
    }
  }

  /**
   * Method: verboseLog This method will print 'verbose' information.
   * 
   * @param message : Initial message
   * @param logger : YFCLogCategory
   * @param object : Object
   */
  public static void verboseLog(String message, final YFCLogCategory logger, final Object object) {
    if (logger.isEnabledFor(YFCLogLevel.VERBOSE)) {
      String outputLogMessage = generateLogMessage(message, object);

      if (outputLogMessage != null) {
        logger.verbose(outputLogMessage);
      }
    }
  }

  /**
   * Method: buildLogMessage This method adds the string representation of the object in the string
   * buffer. It works recursively for nested data structures till the max depth of MAX_DEPTH
   * 
   * @param outputMessageBuffer - String Buffer
   * @param object - Object needed to be printed
   * @see
   * @since
   */
  private static void buildLogMessage(StringBuffer outputMessageBuffer, final Object object,
      final int depth) {
    // This check is in place to avoid infinite
    // loops if a data structure has references
    // to it self
    if (depth >= MAX_DEPTH) {
      return;
    }

    if (object instanceof Map) {
      // The object is a HashMap. The object needs to be iterated
      // across and then logged.
      @SuppressWarnings("unchecked")
      Map<Object, Object> map = (Map<Object, Object>) object;
      outputMessageBuffer.append("\n");
      outputMessageBuffer.append(map.getClass().getName());
      outputMessageBuffer.append(" Size - ");
      outputMessageBuffer.append(map.size());
      outputMessageBuffer.append("\n");

      Set<Map.Entry<Object, Object>> set = map.entrySet();
      Iterator<Map.Entry<Object, Object>> iterator = set.iterator();

      if (iterator != null) {
        while (iterator.hasNext()) {
          Map.Entry<Object, Object> me = iterator.next();

          String key = String.valueOf(me.getKey());
          Object val = me.getValue();

          for (int i = 0; i < depth; i++) {
            outputMessageBuffer.append(" ");
          }

          outputMessageBuffer.append("Key - [");
          outputMessageBuffer.append(key);
          outputMessageBuffer.append("]");
          outputMessageBuffer.append(" Value - [");
          buildLogMessage(outputMessageBuffer, val, depth + 1);
          outputMessageBuffer.append("]");
          outputMessageBuffer.append("\n");
        }
      }
    } else if (object instanceof Collection) {
      // The object is a ArrayList. The object needs to be iterated across
      // and then logged.
      @SuppressWarnings("unchecked")
      Collection<Object> col = (Collection<Object>) object;
      outputMessageBuffer.append("\n");
      outputMessageBuffer.append(col.getClass().getName());
      outputMessageBuffer.append(" Size - ");
      outputMessageBuffer.append(col.size());
      outputMessageBuffer.append("\n");

      Iterator<Object> itr = col.iterator();

      while (itr.hasNext()) {
        Object val = itr.next();

        for (int i = 0; i < depth; i++) {
          outputMessageBuffer.append(" ");
        }

        outputMessageBuffer.append("Value - [");
        buildLogMessage(outputMessageBuffer, val, depth + 1);
        outputMessageBuffer.append("]");
        outputMessageBuffer.append("\n");
      }
    } else if (object instanceof Enumeration) {
      @SuppressWarnings("unchecked")
      Enumeration<Object> e = (Enumeration<Object>) object;

      outputMessageBuffer.append("\n");
      outputMessageBuffer.append(e.getClass().getName());
      outputMessageBuffer.append("\n");

      while (e.hasMoreElements()) {
        Object val = e.nextElement();

        for (int i = 0; i < depth; i++) {
          outputMessageBuffer.append(" ");
        }

        outputMessageBuffer.append("Value - [");
        buildLogMessage(outputMessageBuffer, val, depth + 1);
        outputMessageBuffer.append("]");
        outputMessageBuffer.append("\n");
      }
    } else if (object instanceof Document) {
      // The object is a Document.
      outputMessageBuffer.append("Document - \n");
      YFCDocument yfcDoc = YFCDocument.getDocumentFor((Document) object);
      String outXMLStr = yfcDoc.getString();
      outputMessageBuffer.append(outXMLStr);
      maskCreditCardInfo(outputMessageBuffer);
    } else if (object instanceof YFCDocument) {
      // The object is a YFCDocument.
      outputMessageBuffer.append("Document - \n");
      YFCDocument yfcDoc = (YFCDocument) object;
      String outXMLStr = yfcDoc.getString();
      outputMessageBuffer.append(outXMLStr);
      maskCreditCardInfo(outputMessageBuffer);
    } else if (object instanceof Node) {
      // The object is a Node.
      Node node = (Node) object;
      Document tempDoc = new DocumentImpl();
      Node tempNode = tempDoc.importNode(node, true);
      tempDoc.appendChild(tempNode);

      // To print in XML structure, the node is added to the document.
      outputMessageBuffer.append("Node - \n");
      YFCDocument yfcDoc = YFCDocument.getDocumentFor((Document) tempDoc);
      String outXMLStr = yfcDoc.getString();
      outputMessageBuffer.append(outXMLStr);
      maskCreditCardInfo(outputMessageBuffer);
    } else if (object instanceof Throwable) {
      Throwable thw = (Throwable) object;
      outputMessageBuffer.append("\nException - ");
      outputMessageBuffer.append(thw);
      outputMessageBuffer.append("\nStack Trace - ");
      outputMessageBuffer.append(getStackTrace(thw, 0));
    } else {
      outputMessageBuffer.append(object);
    }

  }

  /**
   * Method: getStackTrace This method returns the stack trace of the input exception
   * 
   * @param ex - Exception
   * @return Stack Trace
   * @see
   * @since
   */
  public static String getStackTrace(final Throwable thw, final int depth) {
    StringBuffer buf = new StringBuffer("\n");

    if (depth >= MAX_DEPTH) {
      return buf.toString();
    }

    StringWriter strw = new StringWriter();
    PrintWriter prnw = new PrintWriter(strw);

    try {
      if (thw != null) {
        thw.printStackTrace(prnw);
        prnw.flush();
        buf.append(strw.getBuffer());
      }
    } finally {
      if (prnw != null) {
        try {
          prnw.close();
        } catch (Exception e) {
        } finally {
          prnw = null;
        }
      }

      if (strw != null) {
        try {
          strw.close();
        } catch (Exception e) {
        } finally {
          strw = null;
        }
      }
    }

    if (thw instanceof SQLException) {
      SQLException sqlexp = (SQLException) thw;
      buf.append(getStackTrace(sqlexp.getNextException(), depth + 1));
    } else if (thw instanceof YFCDBException) {
      YFCDBException yfcdbexp = (YFCDBException) thw;
      buf.append(getStackTrace(yfcdbexp.getSqlException(), depth + 1));
    }

    return buf.toString();
  }

  /**
   * Method: generateLogMessage This method will generate the log message based on the type of
   * component being logged. The following objects can be logged in more detail: String, HashMap,
   * Document, any object's toString(), Properties. This method also logs an error
   * 
   * @param message : Initial message
   * @param object : Object
   * @return String log message
   */
  public static String generateLogMessage(String message, final Object object) {
    // Initialize message if message is null
    message = (message == null) ? "" : message;

    StringBuffer outputMessageBuffer = new StringBuffer(message);
    outputMessageBuffer.append(" - ");

    buildLogMessage(outputMessageBuffer, object, 0);

    return outputMessageBuffer.toString();
  }

  /**
   * Method: startComponentLog This method will log the start of a component call The sequence of
   * logging is as follows: 1. Begin Timer - Begin Timer for methodName 2. InfoLog - componentName
   * Started the execution of methodName 3. DebugLog - Log the componentName input data to
   * methodName
   * 
   * @param logger : YFCLogCategory
   * @param componentName : String
   * @param methodName : String
   * @param data : Object
   * @see
   * @since
   */
  public static void startComponentLog(final YFCLogCategory logger, String componentName,
      String methodName, final Object data) {
    componentName = (componentName == null) ? "" : componentName;
    methodName = (methodName == null) ? "" : methodName;
    logger.beginTimer("ComponentName - " + componentName + " MethodName - " + methodName);
    LoggerUtil.verboseLog(componentName + " Started the execution of " + methodName, logger, "");
    LoggerUtil.verboseLog(componentName + " Input Data to " + methodName, logger, data);
  }

  /**
   * Method: endComponentLog This method will log the end of a component call The sequence of
   * logging is as follows: 1. DebugLog - Log the componentName output data of methodName 2. InfoLog
   * - componentName completed the execution of methodName 3. End Timer - End Timer for methodName
   * 
   * @param logger : YFCLogCategory
   * @param componentName : String
   * @param methodName : String
   * @param data : Object
   * @see
   * @since
   */
  public static void endComponentLog(final YFCLogCategory logger, String componentName,
      String methodName, final Object data) {
    componentName = (componentName == null) ? "" : componentName;
    methodName = (methodName == null) ? "" : methodName;
    LoggerUtil.verboseLog(componentName + " Output Data of " + methodName, logger, data);
    LoggerUtil.verboseLog(componentName + " Completed the execution of " + methodName, logger, "");
    logger.endTimer("ComponentName - " + componentName + " MethodName - " + methodName);
  }

  public static void logExceptionWithXML(final YFCLogCategory logger, String message,
      Document xml, Exception ex) {
    logger.error(generateLogMessage(message, xml));
    logger.error(generateLogMessage(ex.getClass().getName(), ex));
  }

  /**
   * Method : This method masked the value of CardNo attribute in the String. In future we can add
   * differnet combinations such as "CrediCard", "CreditCardNo" etc. Right now CardNo is the only
   * issue.
   * 
   * @param outputMessageBuffer
   */
  private static void maskCreditCardInfo(StringBuffer outputMessageBuffer) {
    Pattern pattern = Pattern.compile("CardNo|CreditCardNumber");
    Matcher matcher = pattern.matcher(outputMessageBuffer);

    while (matcher.find()) {

      int beginCardIndex = outputMessageBuffer.indexOf("\"", matcher.end());
      int endCardIndex = outputMessageBuffer.indexOf("\"", beginCardIndex + 1);

      if ((beginCardIndex > 0) && (beginCardIndex < endCardIndex)) {
        outputMessageBuffer.replace(beginCardIndex + 1, endCardIndex,
            mask(outputMessageBuffer.substring(beginCardIndex + 1, endCardIndex)));
      }
    }
  }

  /**
   * Method : Masks the whole card# except the last 4 digits.
   * 
   * @param cardNo
   * @return
   */

  private static String mask(String cardNo) {
    String maskedCardNo = "";
    String lastFourDigits = "";
    if (cardNo != null) {
      int len = cardNo.length();
      if (len >= 4) {
        lastFourDigits = cardNo.substring(len - 4, len);
        for (int i = 0; i < len - 4; i++) {
          maskedCardNo += "X";
        }
      }
    }
    return maskedCardNo + lastFourDigits;
  }

}
