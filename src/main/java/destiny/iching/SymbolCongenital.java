/**
 * 先天八卦
 * @author smallufo
 * Created on 2002/8/13 at 下午 01:13:22
 */
package destiny.iching;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static destiny.iching.Symbol.*;

/**
 * <pre>
 * 先天八卦
 * 
 * 兌2 | 乾1 | 巽5
 * ----+-----+----
 * 離3 |     | 坎6
 * ----+-----+----
 * 震4 | 坤8 | 艮7
 * </pre>
 */
public class SymbolCongenital implements Comparator<Symbol> {
  private final static Symbol[] 先天八卦 = new Symbol[]
    { 乾 , 兌 , 離 , 震 ,
      巽 , 坎 , 艮 , 坤 };
    
  private final static List<Symbol> 先天八卦List = Arrays.asList(先天八卦);
  
  SymbolCongenital()
  {}
  
  /**
   * 取得先天八卦的卦序
   * 乾1 , 兌2 , ... , 坤 8
   */
  public static int getIndex(Symbol s)
  {
    return 先天八卦List.indexOf(s)+1;
  }
  
  /**
   * 由先天八卦卦序取得 Symbol
   * 0->坤 , 1->乾 , 2->兌 , ... , 8->坤 , 繼續循環 9->乾...
   */
  public static Symbol getSymbol(int index) {
    if (index <= 0)
      return getSymbol(index + 8);
    if (index > 8)
      return getSymbol(index - 8);

    int remainder = index % 8;
    if (remainder == 0)
      remainder = 8;
    return (先天八卦List.get(remainder-1));
  }
  
  /**
   * 以順時針方向取得一卦
   */
  @NotNull
  public static Symbol getClockwiseSymbol(@NotNull Symbol s)
  {
    switch (s) {
      case 乾: return 巽;
      case 兌: return 乾;
      case 離: return 兌;
      case 震: return 離;
      case 巽: return 坎;
      case 坎: return 艮;
      case 艮: return 坤;
      case 坤: return 震;
    }
    throw new RuntimeException("impossible");
  }

  /**
   * 對沖的卦
   */
  @NotNull
  public static Symbol getOppositeSymbol(@NotNull Symbol s) {
    switch (s) {
      case 乾: return 坤;
      case 兌: return 艮;
      case 離: return 坎;
      case 震: return 巽;
      case 巽: return 震;
      case 坎: return 離;
      case 艮: return 兌;
      case 坤: return 乾;
    }
    throw new RuntimeException("impossible");
  }
  
  public int compare(Symbol s1 , Symbol s2)
  {
    return (先天八卦List.indexOf( s1 ) -  先天八卦List.indexOf( s2 )) ;
  }

}
