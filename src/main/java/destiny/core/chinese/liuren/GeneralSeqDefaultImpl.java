/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

import destiny.core.Descriptive;

import java.io.Serializable;
import java.util.Locale;

import static destiny.core.chinese.liuren.General.*;

public class GeneralSeqDefaultImpl implements GeneralSeqIF , Descriptive , Serializable {

  private final static General[] ARRAY = new General[] {
    貴人 , 螣蛇 , 朱雀 , 六合 , 勾陳 , 青龍 , 天空 , 白虎 , 太常 , 玄武 , 太陰 , 天后
  };

  @Override
  public String getTitle(Locale locale) {
    return "內定順序";
  }

  @Override
  public String getDescription(Locale locale) {
    return "貴蛇朱合勾青空，白常玄陰后";
  }

  @Override
  public General next(General from, int n) {
    return get(getIndex(from) + n);
  }

  private static General get(int index) {
    if (index < 0)
      return get(index + 12);
    else if (index >= 12)
      return get(index - 12);
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
