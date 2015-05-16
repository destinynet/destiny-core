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
import java.util.Optional;

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
  private final static Symbol[] 後天八卦 = new Symbol[]
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
   * TODO : 讓其可以支援 0 以及 >=9 的情況
   * 492
   * 357
   * 816
   * index = 0 : Exception !
   * index = 1 , 後天卦 = 坎
   * index = 2 , 後天卦 = 坤
   * index = 3 , 後天卦 = 震
   * index = 4 , 後天卦 = 巽
   * index = 5 , 後天卦 = null
   * index = 6 , 後天卦 = 乾
   * index = 7 , 後天卦 = 兌
   * index = 8 , 後天卦 = 艮
   * index = 9 , 後天卦 = 離
   * index > 9 : Exception !
   */
  @Nullable
  public static Symbol getSymbolNullable(int index)
  {
    if (index == 5)
      return null;
    else if (index > 5 )
      index--;
    return 後天八卦[index-1];
  }

  /**
   * 2015-05-12 : 傳入後天卦的卦序，傳回八純卦
   * 巽4 | 離9 | 坤2
   * ----+-----+----
   * 震3 |  5  | 兌7
   * ----+-----+----
   * 艮8 | 坎1 | 乾6
   * 若傳入 5 , 則傳回 Optional.empty()
   * 若傳入 0 則傳回離卦
   * 傳入 10 , 則傳回坎卦 (10 % 9 = 1)
   */
  public static Optional<Symbol> getSymbol(int index) {
    int reminder = index % 9;
    if (reminder == 5)
      return Optional.empty();
    else if (reminder == 0)
      return Optional.of(Symbol.離);
    else if (reminder > 5)
      return Optional.of(後天八卦[reminder-2]);
    else
      return Optional.of(後天八卦[reminder-1]);
  }
  
  
  /**
   * 以順時針方向取得一卦
   */
  @NotNull
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
    }
    throw new IllegalArgumentException("impossible");
  }
  
  /**
   * 取得對沖之卦
   */
  @NotNull
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
    }
    throw new IllegalArgumentException("impossible");
  }
  
  public int compare(Symbol s1 , Symbol s2)
  {
    return (後天八卦List.indexOf( s1 ) -  後天八卦List.indexOf( s2 ));
  }
}
