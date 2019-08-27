/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * 
 * @author leeyazhou
 * 
 */
public final class StringUtil {
  private StringUtil() {

  }

  public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  public static boolean isNotEmpty(String str) {
    return !StringUtil.isEmpty(str);
  }

  public static boolean isBlank(String str) {
    int strLen;
    if (str == null || (strLen = str.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if ((Character.isWhitespace(str.charAt(i)) == false)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNotBlank(String str) {
    return !StringUtil.isBlank(str);
  }

  public static String[] tokenizeToStringArray(String str, String delimiters) {
    return tokenizeToStringArray(str, delimiters, true, true);
  }

  public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
      boolean ignoreEmptyTokens) {

    if (str == null) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<String>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }

  public static String[] toStringArray(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.toArray(new String[collection.size()]);
  }

  public static boolean hasText(CharSequence str) {
    if (!hasLength(str)) {
      return false;
    }
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasLength(CharSequence str) {
    return (str != null && str.length() > 0);
  }

  public static int countOccurrencesOf(String str, String sub) {
    if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
      return 0;
    }
    int count = 0;
    int pos = 0;
    int idx;
    while ((idx = str.indexOf(sub, pos)) != -1) {
      ++count;
      pos = idx + sub.length();
    }
    return count;
  }

  /**
   * Is String s in String ss?
   * 
   * @param str "service1"
   * @param strs “service1,service2”
   * @return boolean
   */
  public static boolean stringInStrings(String str, String strs) {
    String[] sa = strs.split(",");
    if (sa != null && sa.length > 0) {
      for (String se : sa) {
        if (se.equals(str)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean stringNotInStrings(String str, String strs) {
    String[] sa = strs.split(",");
    if (sa != null && sa.length > 0) {
      for (String se : sa) {
        if (se.equals(str)) {
          return false;
        }
      }
    }
    return true;
  }

  public static String uuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static String[] delimitedListToStringArray(String str, String delimiter) {
    return delimitedListToStringArray(str, delimiter, null);
  }

  public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {

    if (str == null) {
      return new String[0];
    }
    if (delimiter == null) {
      return new String[] {str};
    }

    List<String> result = new ArrayList<>();
    if (delimiter.isEmpty()) {
      for (int i = 0; i < str.length(); i++) {
        result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
      }
    } else {
      int pos = 0;
      int delPos;
      while ((delPos = str.indexOf(delimiter, pos)) != -1) {
        result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
        pos = delPos + delimiter.length();
      }
      if (str.length() > 0 && pos <= str.length()) {
        // Add rest of String, but not in case of empty input.
        result.add(deleteAny(str.substring(pos), charsToDelete));
      }
    }
    return toStringArray(result);
  }

  public static String deleteAny(String inString, String charsToDelete) {
    if (!hasLength(inString) || !hasLength(charsToDelete)) {
      return inString;
    }

    StringBuilder sb = new StringBuilder(inString.length());
    for (int i = 0; i < inString.length(); i++) {
      char ch = inString.charAt(i);
      if (charsToDelete.indexOf(ch) == -1) {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  public static String capitalize(String str) {
    return changeFirstCharacterCase(str, true);
  }

  private static String changeFirstCharacterCase(String str, boolean capitalize) {
    if (!hasLength(str)) {
      return str;
    }

    char baseChar = str.charAt(0);
    char updatedChar;
    if (capitalize) {
      updatedChar = Character.toUpperCase(baseChar);
    } else {
      updatedChar = Character.toLowerCase(baseChar);
    }
    if (baseChar == updatedChar) {
      return str;
    }

    char[] chars = str.toCharArray();
    chars[0] = updatedChar;
    return new String(chars, 0, chars.length);
  }
}
