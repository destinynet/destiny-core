/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

import java.io.Serializable;
import java.util.Locale;

import static destiny.core.chinese.liuren.General.*;

public class GeneralZhaoSeqImpl implements GeneralSeqIF , Serializable {

  private final static General[] ARRAY = new General[] {
    貴人 , 青龍 , 六合 , 勾陳 ,
    螣蛇 , 朱雀 , 太常 , 白虎 ,
    太陰 , 天空 , 玄武 , 天后
  };

  @Override
  public String getTitle(Locale locale) {
    return "趙氏六壬";
  }

  @Override
  public String getDescription(Locale locale) {
    return "貴青合勾 蛇朱常白 陰空玄后";
  }

  @Override
  public General next(General from, int n) {
    return get(getIndex(from) + n);
  }

  private static General get(int index) {
    if (index < 0)
      return get(index + 12);
    else if (index >= 12)
      return get(index % 12);
    else
      return ARRAY[index];
  }

  private static int getIndex(General g) {
    int index = -1;
    for (int i = 0; i < ARRAY.length; i++) {
      if (g == ARRAY[i])
        index = i;
    }
    return index;
  }
}
