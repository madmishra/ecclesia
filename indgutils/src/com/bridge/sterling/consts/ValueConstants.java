/**
 * Copyright 2012, Bridge Solutions Inc. All rights reserved. Change Log:
 */
package com.bridge.sterling.consts;

/**
 * Class to hold value constants.
 */
public final class ValueConstants {

  /**
   * Private constructor.
   */
  private ValueConstants() {

  }

  /**
   * The value of the MQ_ICF to be picked from the constants.
   */
  public static final String MQ_ICF = "icf";

  /**
   * The value of the MQ_URL to be picked from the constants.
   */
  public static final String MQ_URL = "url";

  /**
   * The value of the MQ_CONNECTION_FACTORY to be picked from the constants.
   */
  public static final String MQ_CONNECTION_FACTORY = "connectionFactory";

  /**
   * The value of the MQ_QUEUE_NAME to be picked from the constants.
   */
  public static final String MQ_QUEUE_NAME = "queueName";

  /**
   * The value of the ELE_MESSAGE to be picked from the constants.
   */
  public static final String ELE_MESSAGE = "MESSAGE";

  /**
   * The value of the ORDER_NO to be picked from the constants.
   */
  public static final String ORDER_NO = "OrderNo";

  /**
   * The value of the DOCUMENT_TYPE to be picked from the constants.
   */
  public static final String DOCUMENT_TYPE = "DocumentType";

  /**
   * The value of the KEY to be picked from the constants.
   */
  public static final String KEY = "Key";

  /**
   * The value of the VALUE to be picked from the constants.
   */
  public static final String VALUE = "Value";

  /**
   * The value of the ENTERPRISE_CODE to be picked from the constants.
   */
  public static final String ENTERPRISE_CODE = "EnterpriseCode";

  /**
   * The value of the ATTRIBUTE_NODE_NAME to be picked from the constants.
   */
  public static final String ATTRIBUTE_NODE_NAME = "ATTRIBUTE_NODE";

  /**
   * The value of the CDATA_SECTION_NODE_NAME to be picked from the constants.
   */
  public static final String CDATA_SECTION_NODE_NAME = "CDATA_SECTION_NODE";

  /**
   * The value of the COMMENT_NODE_NAME to be picked from the constants.
   */
  public static final String COMMENT_NODE_NAME = "COMMENT_NODE";

  /**
   * The value of the DOCUMENT_FRAGMENT_NODE_NAME to be picked from the constants.
   */
  public static final String DOCUMENT_FRAGMENT_NODE_NAME = "DOCUMENT_FRAGMENT_NODE";

  /**
   * The value of the DOCUMENT_NODE_NAME to be picked from the constants.
   */
  public static final String DOCUMENT_NODE_NAME = "DOCUMENT_NODE";

  /**
   * The value of the DOCUMENT_TYPE_NODE_NAME to be picked from the constants.
   */
  public static final String DOCUMENT_TYPE_NODE_NAME = "DOCUMENT_TYPE_NODE";

  /**
   * The value of the ELEMENT_NODE_NAME to be picked from the constants.
   */
  public static final String ELEMENT_NODE_NAME = "ELEMENT_NODE";

  /**
   * The value of the ENTITY_NODE_NAME to be picked from the constants.
   */
  public static final String ENTITY_NODE_NAME = "ENTITY_NODE";

  /**
   * The value of the ENTITY_REFERENCE_NODE_NAME to be picked from the constants.
   */
  public static final String ENTITY_REFERENCE_NODE_NAME = "ENTITY_REFERENCE_NODE";

  /**
   * The value of the NOTATION_NODE_NAME to be picked from the constants.
   */
  public static final String NOTATION_NODE_NAME = "NOTATION_NODE";

  /**
   * The value of the PROCESSING_INSTRUCTION_NODE_NAME to be picked from the constants.
   */
  public static final String PROCESSING_INSTRUCTION_NODE_NAME = "PROCESSING_INSTRUCTION_NODE";

  /**
   * The value of the TEXT_NODE_NAME to be picked from the constants.
   */
  public static final String TEXT_NODE_NAME = "TEXT_NODE";

  /**
   * The value of the UNKNOWN_NODE_NAME to be picked from the constants.
   */
  public static final String UNKNOWN_NODE_NAME = "UNKNOWN NODE";

  // number of seconds to use for buffering a customer's preferred time slot
  // i.e. avoid an 8-10 slot matching a preferred slot of 10-12

  /**
   * The value of the TIME_SLOT_BUFFER to be picked from the constants.
   */
  public static final long TIME_SLOT_BUFFER = 60;

  /**
   * The value of the API_GET_ORGANIZATION_LIST to be picked from the constants.
   */
  public static final String API_GET_ORGANIZATION_LIST = "getOrganizationList";

  /**
   * The value of the BUSINESS_CALENDAR_UTIL_GET_CALENDAR_KEY to be picked from the constants.
   */
  public static final String BUSINESS_CALENDAR_UTIL_GET_CALENDAR_KEY =
      "BusinessCalendarUtil_GetCalendarKey";

  /**
   * The value of the BUSINESS_CALENDAR_UTIL_GET_VALID_BUSINESS_DATES to be picked from the
   * constants.
   */
  public static final String BUSINESS_CALENDAR_UTIL_GET_VALID_BUSINESS_DATES =
      "BusinessCalendarUtil_" + "GetValidBusinessDates";

  /**
   * The value of the GET_CALENDAR_DAY_DETAILS to be picked from the constants.
   */
  public static final String GET_CALENDAR_DAY_DETAILS = "getCalendarDayDetails";

  // //////////////////////

  /**
   * The value of the YYYYMMDD to be picked from the constants.
   */
  public static final String YYYYMMDD = "yyyyMMdd";

  /**
   * The value of the YANTRA_DATE_FORMAT to be picked from the constants.
   */
  public static final String YANTRA_DATE_FORMAT = "yyyyMMdd'T'HH:mm:ss";

  /**
   * The value of the ISO_CODE_LENGTH to be picked from the constants.
   */
  public static final String ISO_CODE_LENGTH = "ISO_CODE_LENGTH";

  /**
   * The value of the ISO_CODE_LENGTH_FDC to be picked from the constants.
   */
  public static final String ISO_CODE_LENGTH_FDC = "ISO_CODE_LENGTH_FDC";

  /**
   * The value of the TV_KEY_LENGTH to be picked from the constants.
   */
  public static final int TV_KEY_LENGTH = 16;

  /**
   * The value of the TV_KEY_PREFIX to be picked from the constants.
   */
  public static final String TV_KEY_PREFIX = "8";

  /**
   * The value of the INVALID_TV_KEY_PREFIX to be picked from the constants.
   */
  public static final String[] INVALID_TV_KEY_PREFIX = new String[] {"850", "855"};

  /**
   * The value of the ERROR_ILLEGAL_ARGUMENT_CODE to be picked from the constants.
   */
  public static final String ERROR_ILLEGAL_ARGUMENT_CODE = "BBY0249";

  /**
   * The value of the API_GET_COMMON_CODE_LIST to be picked from the constants.
   */
  public static final String API_GET_COMMON_CODE_LIST = "getCommonCodeList";

  /**
   * The value of the COMMON_CODE to be picked from the constants.
   */
  public static final String COMMON_CODE = "CommonCode";

  /**
   * The value of the CODE_TYPE to be picked from the constants.
   */
  public static final String CODE_TYPE = "CodeType";

  /**
   * The value of the COMMON_CODE_WAIT_TIME to be picked from the constants.
   */
  public static final String COMMON_CODE_WAIT_TIME = "WaitTime";

  /**
   * The value of the TEMPLATE_GET_COMMON_CODE_LIST_GENERAL to be picked from the constants.
   */
  public static final String TEMPLATE_GET_COMMON_CODE_LIST_GENERAL =
      "TEMPLATE_GET_COMMON_CODE_LIST_GENERAL";

  /**
   * The value of the EMPTY_STRING to be picked from the constants.
   */
  public static final String EMPTY_STRING = "";

  /**
   * The value of the CODE_VALUE to be picked from the constants.
   */
  public static final String CODE_VALUE = "CodeValue";

  /**
   * The value of the BLANK to be picked from the constants.
   */
  public static final String BLANK = "Blank";

  /**
   * The value of the ORDER to be picked from the constants.
   */
  public static final String ORDER = "Order";

  /**
   * The value of the ORDER_HEADER_KEY to be picked from the constants.
   */
  public static final String ORDER_HEADER_KEY = "OrderHeaderKey";

  /**
   * The value of the API_GET_SALES_ORDER_DETAILS to be picked from the constants.
   */
  public static final String API_GET_SALES_ORDER_DETAILS = "getSalesOrderDetails";

  /**
   * The value of the TEMPLATE_GET_ORDER_DETAILS_CONDITION_INT_CARDS to be picked from the
   * constants.
   */
  public static final String TEMPLATE_GET_ORDER_DETAILS_CONDITION_INT_CARDS =
      "TEMPLATE_GET_ORDER_DETAILS_" + "CONDITION_INT_CARDS";

  /**
   * The value of the CHARGE_TRANSACTION_DETAILS to be picked from the constants.
   */
  public static final String CHARGE_TRANSACTION_DETAILS = "ChargeTransactionDetails";

  /**
   * The value of the CHARGE_TYPE to be picked from the constants.
   */
  public static final String CHARGE_TYPE = "ChargeType";

  /**
   * The value of the STATUS to be picked from the constants.
   */
  public static final String STATUS = "Status";

  /**
   * The value of the AUTHORIZATION to be picked from the constants.
   */
  public static final String AUTHORIZATION = "AUTHORIZATION";

  /**
   * The value of the CHECKED to be picked from the constants.
   */
  public static final String CHECKED = "CHECKED";

  /**
   * The value of the CLOSED to be picked from the constants.
   */
  public static final String CLOSED = "CLOSED";

  /**
   * The value of the PAYMENT_METHOD to be picked from the constants.
   */
  public static final String PAYMENT_METHOD = "PaymentMethod";

  /**
   * The value of the PAYMENT_TYPE to be picked from the constants.
   */
  public static final String PAYMENT_TYPE = "PaymentType";

  /**
   * The value of the SUSPEND_ANY_MORE_CHARGES to be picked from the constants.
   */
  public static final String SUSPEND_ANY_MORE_CHARGES = "SuspendAnyMoreCharges";

  /**
   * The value of the CREDIT_CARD_PAYMENT_YANTRA to be picked from the constants.
   */
  public static final String CREDIT_CARD_PAYMENT_YANTRA = "CREDIT_CARD";

  /**
   * The value of the CREDIT_CARD_TYPE to be picked from the constants.
   */
  public static final String CREDIT_CARD_TYPE = "CreditCardType";

  /**
   * The value of the CREDIT_CARD_TRANSACTIONS to be picked from the constants.
   */
  public static final String CREDIT_CARD_TRANSACTIONS = "CreditCardTransactions";

  /**
   * The value of the CREDIT_CARD_TRANSACTION to be picked from the constants.
   */
  public static final String CREDIT_CARD_TRANSACTION = "CreditCardTransaction";

  /**
   * The value of the AUTH_AVS to be picked from the constants.
   */
  public static final String AUTH_AVS = "AuthAvs";

  /**
   * The value of the RW_REFERENCE1 to be picked from the constants.
   */
  public static final String RW_REFERENCE1 = "Reference1";

  /**
   * The value of the CHARGE to be picked from the constants.
   */
  public static final String CHARGE = "CHARGE";

  /**
   * The value of the NO to be picked from the constants.
   */
  public static final String NO = "N";

  /**
   * The value of the PAY_PAL to be picked from the constants.
   */
  public static final String PAY_PAL = "PAYPAL";

  /**
   * The value of the CVV_AUTH_CODE to be picked from the constants.
   */
  public static final String CVV_AUTH_CODE = "CVVAuthCode";

  /**
   * The value of the CVV_MAP to be picked from the constants.
   */
  public static final String CVV_MAP = "CVVMap";

  /**
   * The value of the CODE_LONG_DESCRIPTION to be picked from the constants.
   */
  public static final String CODE_LONG_DESCRIPTION = "CodeLongDescription";

  /**
   * The value of the OOB_EXCEPTION_DOCUMENT_STRING to be picked from the constants.
   */
  public static final String OOB_EXCEPTION_DOCUMENT_STRING = "<Errors/>";

  /**
   * The value of the ERROR to be picked from the constants.
   */
  public static final String ERROR = "Error";

  /**
   * The value of the ERROR_CODE to be picked from the constants.
   */
  public static final String ERROR_CODE = "ErrorCode";

  /**
   * The value of the ERROR_DESCRIPTION to be picked from the constants.
   */
  public static final String ERROR_DESCRIPTION = "ErrorDescription";

  /**
   * The value of the ERROR_RELATED_MORE_INFO to be picked from the constants.
   */
  public static final String ERROR_RELATED_MORE_INFO = "ErrorRelatedMoreInfo";

  /**
   * The value of the ATTRIBUTE to be picked from the constants.
   */
  public static final String ATTRIBUTE = "Attribute";

  /**
   * The value of the NAME to be picked from the constants.
   */
  public static final String NAME = "Name";

  /**
   * The value of the STACK to be picked from the constants.
   */
  public static final String STACK = "Stack";

  /**
   * The value of the FILE_PROPERTIES_EXTN to be picked from the constants.
   */
  public static final String FILE_PROPERTIES_EXTN = ".properties";

  /**
   * The value of the MISC_UNDERSCORE to be picked from the constants.
   */
  public static final String MISC_UNDERSCORE = "_";

  /**
   * The value of the SYSPROP_PROPERTIES_FILE to be picked from the constants.
   */
  public static final String SYSPROP_PROPERTIES_FILE = "spf.interface.file";

  /**
   * The value of the DEFAULT_PROPERTIES_FILE to be picked from the constants.
   */
  public static final String DEFAULT_PROPERTIES_FILE = "/resources/spf.interface.properties";

  /**
   * The value of the EX_NO_INTERFACE_PROPS to be picked from the constants.
   */
  public static final String EX_NO_INTERFACE_PROPS =
      "Properties file \"{0}\" not found on class path.";

  /**
   * The value of the EX_NO_RESOURCE_BUNDLE_FOUND to be picked from the constants.
   */
  public static final String EX_NO_RESOURCE_BUNDLE_FOUND =
      "None of the following bundle files found on class path: " + "{0} {1} {2} {3}.";

  /**
   * The value of the ERROR_EXCEPTION_CODE to be picked from the constants.
   */
  public static final String ERROR_EXCEPTION_CODE = "BBY0248";

  /**
   * The value of the YIF_CLIENT_PROPERTY_FILE to be picked from the constants xml.
   */
  public static final String YIF_CLIENT_PROPERTY_FILE = "YIF_CLIENT_PROPERTY_FILE";

  /**
   * The value of the YIF_API_CLASS_NAME to be picked from the constants.
   */
  public static final String YIF_API_CLASS_NAME = "YIF_API_CLASS_NAME";

  /**
   * The value of the JAVA_INITIAL_FACTORY to be picked from the constants.
   */
  public static final String JAVA_INITIAL_FACTORY = "JAVA_INITIAL_FACTORY";

  /**
   * The value of the YANTRA_SECURITY_FILE to be picked from the constants.
   */
  public static final String YANTRA_SECURITY_FILE = "YANTRA_SECURITY_FILE";

  /**
   * The value of the JDBC_ORACLE_DRIVER to be picked from the constants. Added for SCR # 2640
   */
  public static final String JDBC_ORACLE_DRIVER = "JDBC_ORACLE_DRIVER";

}
