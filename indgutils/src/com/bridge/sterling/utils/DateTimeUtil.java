package com.bridge.sterling.utils;

import com.sterlingcommerce.baseutil.SCUtil;
import com.yantra.yfc.date.YTimestamp;

public final class DateTimeUtil {

  private static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static String formatDate(YTimestamp yDate, final String outFormat) {
    if (SCUtil.isVoid(yDate)) {
      // ERR0000173=Date cannot be null
      throw ExceptionUtil.getYFSException("ERR0000173");
    }
    String format = (SCUtil.isVoid(outFormat)) ? DEFAULT_DATE_FORMAT : outFormat;
    return yDate.getString(format);
  }
  
  public static String getFormattedCurrentDate(final String outFormat) {
    return formatDate(YTimestamp.newTimestamp(), outFormat);
  }
}
