/*
 * @author smallufo
 * Created on 2005/1/19 at 下午 06:27:55
 */
package destiny.iching;

import java.util.Arrays;

import junit.framework.TestCase;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.YinYang;

public class SymbolTest extends TestCase
{
  /** 測試先天八卦排序 */
  public void testSortingCongenital()
  {
    Symbol[] s = new Symbol[] { Symbol.兌 , Symbol.艮 , Symbol.坎 , Symbol.乾 , Symbol.離 , Symbol.巽 , Symbol.坤 , Symbol.震 };
    SymbolCongenital c1 = new SymbolCongenital();

    Arrays.sort(s,c1);
    
    Symbol[] expected = new Symbol[] {Symbol.乾 , Symbol.兌 , Symbol.離 , Symbol.震 , Symbol.巽 , Symbol.坎 , Symbol.艮 , Symbol.坤};
    for (int i=0; i < s.length ; i++)
      assertEquals(expected[i] ,s[i]);
  }
  
  /** 後天八卦排序 */
  public void testSortingAcquired()
  {
    Symbol[] s = new Symbol[] { Symbol.兌 , Symbol.艮 , Symbol.坎 , Symbol.乾 , Symbol.離 , Symbol.巽 , Symbol.坤 , Symbol.震 };
    SymbolAcquired c2 = new SymbolAcquired();
    
    Arrays.sort(s,c2);
    
    Symbol[] expected = new Symbol[] {Symbol.坎 , Symbol.坤 , Symbol.震 , Symbol.巽 , Symbol.乾 , Symbol.兌 , Symbol.艮 , Symbol.離 };
    for (int i=0; i < s.length ; i++)
      assertEquals(expected[i] ,s[i]);
  }
  
  /** 測試後天八卦順時針轉 */
  public void testSymbolAcquiredClockwise()
  {
    /* 後天 離卦 順時針下一卦為 坤 */
    assertSame(Symbol.坤 , SymbolAcquired.getClockwiseSymbol(Symbol.離) );
    /* 後天 坤卦 順時針下一卦為 兌 */
    assertSame(Symbol.兌 , SymbolAcquired.getClockwiseSymbol(Symbol.坤) );
    /* 後天 兌卦 順時針下一卦為 乾 */
    assertSame(Symbol.乾 , SymbolAcquired.getClockwiseSymbol(Symbol.兌) );
    /* 後天 乾卦 順時針下一卦為 坎 */
    assertSame(Symbol.坎 , SymbolAcquired.getClockwiseSymbol(Symbol.乾) );
    /* 後天 坎卦 順時針下一卦為 艮 */
    assertSame(Symbol.艮 , SymbolAcquired.getClockwiseSymbol(Symbol.坎) );
    /* 後天 艮卦 順時針下一卦為 震 */
    assertSame(Symbol.震 , SymbolAcquired.getClockwiseSymbol(Symbol.艮) );
    /* 後天 震卦 順時針下一卦為 巽 */
    assertSame(Symbol.巽 , SymbolAcquired.getClockwiseSymbol(Symbol.震) );
    /* 後天 巽卦 順時針下一卦為 離 */
    assertSame(Symbol.離 , SymbolAcquired.getClockwiseSymbol(Symbol.巽) );
  }
  
  public void testGetSymbol()
  {
    assertSame(Symbol.乾 , Symbol.getSymbol(new YinYang[] {YinYang.陽 , YinYang.陽 , YinYang.陽}));
    assertSame(Symbol.兌 , Symbol.getSymbol(new YinYang[] {YinYang.陽 , YinYang.陽 , YinYang.陰}));
    assertSame(Symbol.離 , Symbol.getSymbol(new YinYang[] {YinYang.陽 , YinYang.陰 , YinYang.陽}));
    assertSame(Symbol.震 , Symbol.getSymbol(new YinYang[] {YinYang.陽 , YinYang.陰 , YinYang.陰}));
    assertSame(Symbol.巽 , Symbol.getSymbol(new YinYang[] {YinYang.陰 , YinYang.陽 , YinYang.陽}));
    assertSame(Symbol.坎 , Symbol.getSymbol(new YinYang[] {YinYang.陰 , YinYang.陽 , YinYang.陰}));
    assertSame(Symbol.艮 , Symbol.getSymbol(new YinYang[] {YinYang.陰 , YinYang.陰 , YinYang.陽}));
    assertSame(Symbol.坤 , Symbol.getSymbol(new YinYang[] {YinYang.陰 , YinYang.陰 , YinYang.陰}));
  }
  
  public void testGetFiveElement()
  {
    assertSame(FiveElement.金 , Symbol.乾.getFiveElement());
    assertSame(FiveElement.金 , Symbol.兌.getFiveElement());
    assertSame(FiveElement.火 , Symbol.離.getFiveElement());
    assertSame(FiveElement.木 , Symbol.震.getFiveElement());
    assertSame(FiveElement.木 , Symbol.巽.getFiveElement());
    assertSame(FiveElement.水 , Symbol.坎.getFiveElement());
    assertSame(FiveElement.土 , Symbol.艮.getFiveElement());
    assertSame(FiveElement.土 , Symbol.坤.getFiveElement());
  }
}
