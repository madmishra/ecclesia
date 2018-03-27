package com.bridge.sterling.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.bridge.sterling.consts.CommonLiterals;
import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;

public class SuspendErrorUtil {

  private static Map<String, String> suspendErrorMap = new ConcurrentHashMap<String, String>();
  private static YFCLogCategory logger = YFCLogCategory.instance(SuspendErrorUtil.class);

  public static boolean suspendErrorMapLoaded() {
    return suspendErrorMap.size() > 0;
  }

  public static void loadSuspendErrorMap(YFCDocument suspendErrorDoc) {
    if (!suspendErrorMapLoaded() && !SCUtil.isVoid(suspendErrorDoc)) {
      YFCElement eleRoot = XMLUtil.getRootElement(suspendErrorDoc);
      for (YFCElement eleErrorCode : eleRoot.getChildren("ErrorCode")) {
        String sKey = eleErrorCode.getAttribute(CommonLiterals.KEY);
        String sValue = eleErrorCode.getAttribute(CommonLiterals.VALUE);
        if (!SCUtil.isVoid(sKey) && !SCUtil.isVoid(sValue)) {
          suspendErrorMap.put(sKey, sValue);
        }
      }
    }
  }

  public static boolean isServiceSuspendError(String errorString) {
    boolean suspendError = false;
    if (suspendErrorMapLoaded() && !SCUtil.isVoid(errorString)) {
      String sError = errorString.replace("\n", " ");
      LoggerUtil.verboseLog("errorString to check for suspend error: " + sError, logger, null);
      for (Map.Entry<String, String> entry : suspendErrorMap.entrySet()) {
        String sMatchType = entry.getKey();
        String sMatcher = entry.getValue();
        LoggerUtil.verboseLog("Suspend Errors - key: " + sMatchType + " value: " + sMatcher,
            logger, null);
        if (isValidMatchType(sMatchType) && isMatchingError(sMatchType, sMatcher, sError)) {
          suspendError = true;
          break;
        }
      }
    }
    return suspendError;
  }

  public static boolean isValidMatchType(String sMatchType) {
    return isStringMatcher(sMatchType) || isRegexMatcher(sMatchType);
  }

  // private methods

  private static boolean isStringMatcher(String sMatchType) {
    return !SCUtil.isVoid(sMatchType)
        && sMatchType.toLowerCase().startsWith(CommonLiterals.MATCH_TYPE_STRING_PREFIX);
  }

  private static boolean isRegexMatcher(String sMatchType) {
    return !SCUtil.isVoid(sMatchType)
        && sMatchType.toLowerCase().startsWith(CommonLiterals.MATCH_TYPE_REGEX_PREFIX);
  }

  private static boolean isMatchingError(String sMatchType, String sMatcher, String sError) {
    boolean suspendError = false;
    if (isStringMatcher(sMatchType)) {
      suspendError = sError.contains(sMatcher);
    } else if (isRegexMatcher(sMatchType)) {
      try {
        suspendError = Pattern.matches(sMatcher, sError);
      } catch (Exception e) {
        LoggerUtil.warnLog("Invalid RegEx Pattern: " + sMatcher, logger, e);
        // Consume the exception
      }
    }
    return suspendError;
  }
}
