/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.tools;

import org.jetbrains.annotations.NotNull;

public class ChineseStringTools {

  @NotNull
  public final static String NULL_CHAR ="　"; //空白字元，使用全形的空白, 在 toString() 時使用

  public static String digitToChinese(int digit) {
    switch (digit) {
      case 1 : return "一";
      case 2 : return "二";
      case 3 : return "三";
      case 4 : return "四";
      case 5 : return "五";
      case 6 : return "六";
      case 7 : return "七";
      case 8 : return "八";
      case 9 : return "九";
    }
    throw new IllegalArgumentException("digitToChinese : " + digit);
  }
}
