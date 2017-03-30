/*
 * @author smallufo
 * Created on 2005/1/19 at 下午 06:27:55
 */
package destiny.iching;

import destiny.core.chinese.FiveElement;
import org.junit.Test;

import java.util.Arrays;

import static destiny.iching.Symbol.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SymbolTest
{
  /** 測試先天八卦排序 */
  @Test
  public void testSortingCongenital() {
    Symbol[] s = new Symbol[] { 兌 , 艮 , 坎 , 乾 , 離 , 巽 , 坤 , 震 };
    SymbolCongenital c1 = new SymbolCongenital();

    Arrays.sort(s,c1);
    
    Symbol[] expected = new Symbol[] {乾 , 兌 , 離 , 震 , 巽 , 坎 , 艮 , 坤};
    for (int i=0; i < s.length ; i++)
      assertEquals(expected[i] ,s[i]);
  }
  
  /** 後天八卦排序 */
  @Test
  public void testSortingAcquired()
  {
    Symbol[] s = new Symbol[] { 兌 , 艮 , 坎 , 乾 , 離 , 巽 , 坤 , 震 };
    SymbolAcquired c2 = new SymbolAcquired();
    
    Arrays.sort(s,c2);
    
    Symbol[] expected = new Symbol[] {坎 , 坤 , 震 , 巽 , 乾 , 兌 , 艮 , 離 };
    for (int i=0; i < s.length ; i++)
      assertEquals(expected[i] ,s[i]);
  }
  
  /** 測試後天八卦順時針轉 */
  @Test
  public void testSymbolAcquiredClockwise()
  {
    /* 後天 離卦 順時針下一卦為 坤 */
    assertSame(坤 , SymbolAcquired.getClockwiseSymbol(離) );
    /* 後天 坤卦 順時針下一卦為 兌 */
    assertSame(兌 , SymbolAcquired.getClockwiseSymbol(坤) );
    /* 後天 兌卦 順時針下一卦為 乾 */
    assertSame(乾 , SymbolAcquired.getClockwiseSymbol(兌) );
    /* 後天 乾卦 順時針下一卦為 坎 */
    assertSame(坎 , SymbolAcquired.getClockwiseSymbol(乾) );
    /* 後天 坎卦 順時針下一卦為 艮 */
    assertSame(艮 , SymbolAcquired.getClockwiseSymbol(坎) );
    /* 後天 艮卦 順時針下一卦為 震 */
    assertSame(震 , SymbolAcquired.getClockwiseSymbol(艮) );
    /* 後天 震卦 順時針下一卦為 巽 */
    assertSame(巽 , SymbolAcquired.getClockwiseSymbol(震) );
    /* 後天 巽卦 順時針下一卦為 離 */
    assertSame(離 , SymbolAcquired.getClockwiseSymbol(巽) );
  }

  @Test
  public void testGetSymbol()
  {
    assertSame(乾 , getSymbol(new boolean[] {true  , true  , true }));
    assertSame(兌 , getSymbol(new boolean[] {true  , true  , false}));
    assertSame(離 , getSymbol(new boolean[] {true  , false , true }));
    assertSame(震 , getSymbol(new boolean[] {true  , false , false}));
    assertSame(巽 , getSymbol(new boolean[] {false , true  , true }));
    assertSame(坎 , getSymbol(new boolean[] {false , true  , false}));
    assertSame(艮 , getSymbol(new boolean[] {false , false , true }));
    assertSame(坤 , getSymbol(new boolean[] {false , false , false}));
  }

  @Test
  public void testGetFiveElement()
  {
    assertSame(FiveElement.金 , 乾.getFiveElement());
    assertSame(FiveElement.金 , 兌.getFiveElement());
    assertSame(FiveElement.火 , 離.getFiveElement());
    assertSame(FiveElement.木 , 震.getFiveElement());
    assertSame(FiveElement.木 , 巽.getFiveElement());
    assertSame(FiveElement.水 , 坎.getFiveElement());
    assertSame(FiveElement.土 , 艮.getFiveElement());
    assertSame(FiveElement.土 , 坤.getFiveElement());
  }
}
