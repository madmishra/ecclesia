package com.bridge.sterling.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sterlingcommerce.baseutil.SCUtil;


public final class StringUtil {

  private static final int DEFAULT_STRING_BUFFER_SIZE = 100;

  public static String removeFileExtension(String fileName) {
    String baseName = null;
    String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
    if (!SCUtil.isVoid(tokens) && tokens.length > 0) {
      baseName = tokens[0];
    }
    return baseName;
  }

  /**
   * Pads string with space to specified length. If the string length >= the length parameter, no
   * change is made.
   * 
   * @param strPad the string to pad.
   * @param maxlength the full desired string length after padding.
   * @return string padded with spaces
   */
  public static String padSpaces(final String strPad, final int maxlength) {
    if (strPad == null) {
      throw new IllegalArgumentException("Input value cannot "
          + "be null in StringUtil.padSpace method");
    }

    // Validate Maximum length
    if (maxlength < 0) {
      throw new IllegalArgumentException("Invalid maximum "
          + "length be null in StringUtil.padSpace method");
    }
    int origLength = strPad.length();
    StringBuffer strPadTmp = new StringBuffer();
    if (origLength != maxlength) {
      strPadTmp.append(strPad.trim());
      origLength = strPadTmp.length();
      if (origLength > maxlength) {
        return strPadTmp.toString();
      } else {
        int spaceLength = maxlength - origLength - 1;
        strPadTmp.append(" ");
        for (int i = 0; i < spaceLength; i++) {
          strPadTmp.append(" ");
        }
        return strPadTmp.toString();
      }
    }
    return strPad;
  }

  /**
   * Returns the subclass name without specified suffix.
   * 
   * @param value Input Class name in String
   * @param suffix Suffix in String
   * @return Name without suffix
   */
  public static String stripSuffix(final String value, final String suffix) {
    if (value != null && !value.equals("")) {
      int suffixStartingIndex = value.lastIndexOf(suffix);
      if (suffixStartingIndex != -1) {
        return value.substring(0, suffixStartingIndex);
      }
    }
    return value;
  }

  /**
   * Convert null String to empty String.
   * 
   * @param value String Input
   * @return empty String if input was null
   */
  public static String nonNull(final String value) {
    return SCUtil.isVoid(value) ? "" : value;
  }

  /**
   * Append '/' or '\' to the end of input, based on the input already has '/' or '\'. If the input
   * does not have '/' or '\', then System property "file.separator" is provided Throws exception if
   * invalid characters are given in input string.
   * 
   * @param dirName Name of directory
   * @return edited directory name in String
   */
  public static String formatDirectoryName(final String dirName) {
    // Validate Input String
    if (dirName.indexOf("/") > -1 && dirName.indexOf("\\") > -1) {
      throw new IllegalArgumentException("Input String cannot contain both / and \\ ");
    }
    if (dirName.indexOf("<") > -1 || dirName.indexOf(">") > -1 || dirName.indexOf("|") > -1) {
      throw new IllegalArgumentException(
          " Input String cannot contain Reserved Characters like <,>,|,?,:,*");
    }
    if (dirName.indexOf("?") > -1 || dirName.indexOf(":") > -1 || dirName.indexOf("*") > -1) {
      throw new IllegalArgumentException(
          " Input String cannot contain Reserved Characters like <,>,|,?,:,*");
    }

    if (dirName.charAt(dirName.length() - 1) == '\\' || dirName.charAt(dirName.length() - 1) == '/') {
      return dirName;
    } else {
      if (dirName.indexOf("/") != -1) {
        return dirName + "/";
      }

      if (dirName.indexOf("\\") != -1) {
        return dirName + "\\";
      }
    }
    return dirName + System.getProperty("file.separator");
  }

  /**
   * Prepad string with '0'. If the length of the InputString is >= the Desired size, no change is
   * made.
   * 
   * @param value the string to pad
   * @param size the full desired String length after the pad.
   * @return padded string
   */
  public static String prepadWithZeros(final String value, final int size) {
    if (value != null) {
      int length = value.length();
      if (length > size) {
        return value;
      } else {
        StringBuffer buffer = new StringBuffer();
        for (int count = 0; count < size - length; count++) {
          buffer.append("0");
        }
        buffer.append(value);
        return buffer.toString();
      }
    }
    return value;
  }

  /**
   * Remove character from a string.
   * 
   * @param val Input string
   * @param ch character to be removed
   * @return string
   */
  public static String removeCharacter(final String val, final char ch) {
    StringBuffer buff = new StringBuffer(val);
    int length = buff.length();
    StringBuffer newBuff = new StringBuffer();
    for (int count = 0; count < length; count++) {
      char character = buff.charAt(count);
      if (ch == character) {
        continue;
      } else {
        newBuff.append(character);
      }
    }
    return newBuff.toString();
  }

  /**
   * Converts object to string. Returns null if object is null, return object cast as String if it
   * is a String object, else return toString
   * 
   * @param inputObject Input Object
   * @return Returns null if input object is null; return object cast as String if it is a String
   *         object; else return toString
   */
  public static String getStringValue(final Object inputObject) {
    String retValue = null;
    if (!SCUtil.isVoid(inputObject)) {
      if (inputObject instanceof String) {
        retValue = (String) inputObject;
      } else {
        retValue = inputObject.toString();
      }
    }
    return retValue;
  }

  /**
   * This method is used to capitalize the first character of a String.
   * 
   * @param inputValue The string whose first character has to be capitalized
   * @return The input String with the first character capitalized.
   */
  public static String firstCharToUpperCase(final String inputValue) {
    // Get the character representation of the
    char[] inputValueCharArray = inputValue.toCharArray();
    // Convert the first character to Upper Case
    inputValueCharArray[0] = Character.toUpperCase(inputValueCharArray[0]);
    // return the new String
    return String.copyValueOf(inputValueCharArray);

  }

  /**
   * Parses input value based on delimiter and returns desired token.
   * 
   * @param inputValue String to be parsed
   * @param delimiter Delimiter for parsing
   * @param tokenNumber Desired token number(0 for First token, 1 for Second token etc)
   * @return Returns desired token
   */
  public static String getToken(final String inputValue, final String delimiter,
      final int tokenNumber) {
    String resultValue = null;

    // Validate Input Value // Instead of StringTokenizer, String split is
    // recommended

    if (inputValue == null) {
      throw new IllegalArgumentException("Input value cannot"
          + "be null in StringUtil.getToken method");
    }

    // Validate Delimiter
    if (delimiter == null) {
      throw new IllegalArgumentException("Delimiter cannot be"
          + " null in StringUtil.getToken method");
    }

    // Validate token number
    if (tokenNumber < 0) {
      throw new IllegalArgumentException("Invalid token number" + " in StringUtil.getToken method");
    }

    StringTokenizer st = new StringTokenizer(inputValue, delimiter);
    for (int tokenIndex = 0; tokenIndex < tokenNumber; tokenIndex++) {
      st.nextToken();
    }

    if (st.hasMoreTokens()) {
      resultValue = st.nextToken();
    }

    return resultValue;
  }

  /**
   * A method that uses a stringtokenizer to parse a NameValue pair and returns a map. A NameValue
   * Pair is in the following format: OrderNo=Y100089&EnterpriseCode=MS_UK&ShiptoID=493848234
   * 
   * @param inputValue String to be parsed
   * @return Map object that contains name value pairs
   */
  public static java.util.Map<String, String> getMap(final String inputValue) {
    // Validate Input Value
    if (inputValue == null) {
      throw new IllegalArgumentException("Input value cannot"
          + "be null in StringUtil.getMap method");
    }

    // Validate Input Value
    if (inputValue.indexOf("&") <= -1) {
      throw new IllegalArgumentException("Invalid Input value" + " in StringUtil.getMap method");
    }

    // Validate Input Value
    if (inputValue.indexOf("=") <= -1) {
      throw new IllegalArgumentException("Invalid Input value" + " in StringUtil.getMap method");
    }

    Map<String, String> map = Collections.synchronizedMap(new HashMap<String, String>());
    StringTokenizer st = new StringTokenizer(inputValue, "&");
    while (st.hasMoreTokens()) {
      String nameValuePair = st.nextToken();
      map.put(getToken(nameValuePair, "=", 0), getToken(nameValuePair, "=", 1));
    }
    return map;
  }

  /**
   * Constructs name value pair string in the following format from Map object
   * OrderNo=Y100089&EnterpriseCode=MS_UK&ShiptoID=493848234.
   * 
   * @param map Input Map object
   * @return Name value pair string
   */
  public static String getMapContent(final java.util.Map<String, String> map) {
    // Validate map
    if (map == null) {
      throw new IllegalArgumentException("Input value cannot"
          + " be null in StringUtil.getMap method");
    }

    // String result = "";
    StringBuffer resultB = new StringBuffer();
    // resultB.append("");
    Object[] keyList = map.keySet().toArray();
    for (int index = 0; index < keyList.length; index++) {
      String name = getStringValue(keyList[index]);
      String value = getStringValue(map.get(keyList[index]));
      /*
       * if (result.length() > 1) { result = result + "&"; } result = result + name + "=" + value; }
       */
      if (resultB.toString().length() > 1) {
        resultB.append("&");
      }
      resultB.append(name);
      resultB.append("=");
      resultB.append(value);
    }
    // return result;
    return resultB.toString();
  }

  /**
   * Replaces the searchString with the new string (replaceString) specified in the input string.
   * This works even if the replaceString is an empty String.
   * 
   * @param inputString Input
   * @param searchString String to be replaced
   * @param replaceString New string
   * @return string
   */
  public static String stringReplace(final String inputString, final String searchString,
      final String replaceString) {
    StringBuffer errMessage = new StringBuffer(DEFAULT_STRING_BUFFER_SIZE);

    // Validate inputString
    if (inputString == null || inputString.equals("")) {
      errMessage.append("Input String cannot be null or empty ");
      errMessage.append("in StringUtil.stringReplace().");
      throw new IllegalArgumentException(errMessage.toString());
    }

    // Validate searchString
    if (searchString == null || searchString.equals("")) {
      errMessage.append("Search String cannot be null or empty ");
      errMessage.append("in StringUtil.stringReplace().");
      throw new IllegalArgumentException(errMessage.toString());
    }

    // Validate replaceString
    if (replaceString == null) {
      errMessage.append("Replace String cannot be null ");
      errMessage.append("in StringUtil.stringReplace().");
      throw new IllegalArgumentException(errMessage.toString());
    }

    String outputString = inputString;
    int searchStart = 0;
    int charLoc = outputString.indexOf(searchString);

    while (charLoc > -1) {
      outputString =
          outputString.substring(0, charLoc) + replaceString
              + outputString.substring(charLoc + searchString.length());
      searchStart = charLoc + replaceString.length();
      charLoc = outputString.indexOf(searchString, searchStart);
    }
    return outputString;
  } // public String stringReplace

  /**
   * This method splits the input string with the passed delimiter.
   * 
   * @param delimiter delimiter
   * @param string string
   * @return array of strings
   */
  public static String[] split(final String delimiter, final String string) {
    // create a new String Tokenizer object
    StringTokenizer stringTokenizer = new StringTokenizer(string, delimiter);
    // get the number of tokens
    int countTokens = stringTokenizer.countTokens();
    // create the string array object
    String[] stringArray = new String[countTokens];
    // initialize the token counter
    int tokenCounter = 0;
    // while there are more tokens, add them to the string array.
    while (stringTokenizer.hasMoreTokens()) {
      stringArray[tokenCounter++] = stringTokenizer.nextToken();
    }
    return stringArray;
  }

}
