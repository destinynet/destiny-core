/**
 * 後天八卦
 * @author smallufo
 * Created on 2002/8/13 at 下午 05:24:40
 */
package destiny.iching;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <pre>
 * 後天八卦
 *  
 * 巽4 | 離9 | 坤2
 * ----+-----+----
 * 震3 |     | 兌7
 * ----+-----+----
 * 艮8 | 坎1 | 乾6
 * 
 * </pre>
 */
public class SymbolAcquired implements Comparator<Symbol>
{
  protected final static Symbol[] 後天八卦 = new Symbol[] 
    { Symbol.坎 , Symbol.坤 , Symbol.震 , Symbol.巽 , 
      Symbol.乾 , Symbol.兌 , Symbol.艮 , Symbol.離 };
  
  private final static List<Symbol> 後天八卦List = Arrays.asList(後天八卦);
  
  public SymbolAcquired()
  {}
  
  /**
   * 取得後天八卦的卦序
   * 坎1 , 坤2 , ... , 離 9
   * 因為沒有 5 , 所以得把 5 給跳過
   */
  public static int getIndex(Symbol s)
  {
    int tempIndex = (後天八卦List.indexOf(s) +1);
    if (tempIndex >=5 )
      tempIndex ++ ;
    return tempIndex;
  }
  
  /** 
   * 由後天八卦卦序取得 Symbol
   */
  @Nullable
  public static Symbol getSymbol(int index)
  {
    if (index == 5)
      return null;
    else if (index > 5 )
      index--;
    return 後天八卦[index-1];
  }
  
  
  /**
   * 以順時針方向取得一卦
   */
  @Nullable
  public static Symbol getClockwiseSymbol(@NotNull Symbol s)
  {
    switch (s)
    {
      case 坎: return Symbol.艮;
      case 坤: return Symbol.兌;
      case 震: return Symbol.巽;
      case 巽: return Symbol.離;
      case 乾: return Symbol.坎;
      case 兌: return Symbol.乾;
      case 艮: return Symbol.震;
      case 離: return Symbol.坤;
      default : return null;
    }
  }
  
  /**
   * 取得對沖之卦
   */
  @Nullable
  public static Symbol getOppositeSymbol(@NotNull Symbol s)
  {
    switch (s)
    {
      case 坎: return Symbol.離;
      case 坤: return Symbol.艮;
      case 震: return Symbol.兌;
      case 巽: return Symbol.乾;
      case 乾: return Symbol.巽;
      case 兌: return Symbol.震;
      case 艮: return Symbol.坤;
      case 離: return Symbol.坎;
      default : return null;
    }
  }
  
  public int compare(Symbol s1 , Symbol s2)
  {
    return (後天八卦List.indexOf( s1 ) -  後天八卦List.indexOf( s2 ));
  }
}
