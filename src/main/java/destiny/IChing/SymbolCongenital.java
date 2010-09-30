/**
 * 先天八卦
 * @author smallufo
 * Created on 2002/8/13 at 下午 01:13:22
 */
package destiny.IChing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 先天八卦
 */
public class SymbolCongenital implements Comparator<Symbol>
{
  protected final static Symbol[] 先天八卦 = new Symbol[] 
    { Symbol.乾 , Symbol.兌 , Symbol.離 , Symbol.震 , 
      Symbol.巽 , Symbol.坎 , Symbol.艮 , Symbol.坤 };
    
  private final static List<Symbol> 先天八卦List = Arrays.asList(先天八卦);
  
  public SymbolCongenital()
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
   */
  public static Symbol getSymbol(int index)
  {
    return (先天八卦List.get(index-1));
  }
  
  /**
   * 以順時針方向取得一卦
   */
  public static Symbol getClockwiseSymbol(Symbol s)
  {
    switch (s)
    {
      case 乾: return Symbol.巽;
      case 兌: return Symbol.乾;
      case 離: return Symbol.兌;
      case 震: return Symbol.離;
      case 巽: return Symbol.坎;
      case 坎: return Symbol.艮;
      case 艮: return Symbol.坤;
      case 坤: return Symbol.震;
      default : return null;
    }
  }
  
  public static Symbol getOppositeSymbol(Symbol s)
  {
    switch (s)
    {
      case 乾: return Symbol.坤;
      case 兌: return Symbol.艮;
      case 離: return Symbol.坎;
      case 震: return Symbol.巽;
      case 巽: return Symbol.震;
      case 坎: return Symbol.離;
      case 艮: return Symbol.兌;
      case 坤: return Symbol.乾;
      default : return null;
    }
  }
  
  public int compare(Symbol s1 , Symbol s2)
  {
    return (先天八卦List.indexOf( s1 ) -  先天八卦List.indexOf( s2 )) ;
  }

}
