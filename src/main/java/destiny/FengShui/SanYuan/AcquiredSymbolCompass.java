/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:30:40 
 */
package destiny.FengShui.SanYuan;

import destiny.astrology.Utils;
import destiny.iching.Symbol;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 後天八卦於羅盤上的位置
 */
public class AcquiredSymbolCompass extends AbstractSymbol implements Serializable
{
  @NotNull
  private static final List<Symbol> symbolList = new ArrayList<>();
  static
  {
    symbolList.add(Symbol.坎);
    symbolList.add(Symbol.艮);
    symbolList.add(Symbol.震);
    symbolList.add(Symbol.巽);
    symbolList.add(Symbol.離);
    symbolList.add(Symbol.坤);
    symbolList.add(Symbol.兌);
    symbolList.add(Symbol.乾);
  }
  
  /**
   * 取得某個卦的起始度數
   */
  @Override
  public double getStartDegree(Object o)
  {
    return Utils.getNormalizeDegree(symbolList.indexOf(o) * getStepDegree() + getInitDegree());
  }
  
  /**
   * 取得某個卦的結束度數
   */
  @Override
  public double getEndDegree(Object o)
  {
    return Utils.getNormalizeDegree( (symbolList.indexOf(o) +1 ) * getStepDegree() + getInitDegree());
  }
  
  /**
   * 取得目前這個度數位於哪個卦當中
   */
  public Object getSymbol(double degree)
  {
    int index = (int) ((degree + 360 - getInitDegree()) / getStepDegree()) ;
    if (index >= 8)
      index = index-8;
    return symbolList.get(index);
  }
}
