/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class GeneralSeqDefaultImplTest {

  private GeneralSeqIF seq = new GeneralSeqDefaultImpl();

  @Test
  public void testNext() {
    assertSame(General.貴人, General.貴人.next(-12, seq));
    assertSame(General.螣蛇, General.貴人.next(-11, seq));
    assertSame(General.太陰, General.貴人.next(-2, seq));
    assertSame(General.天后, General.貴人.next(-1, seq));
    assertSame(General.貴人, General.貴人.next(0, seq));
    assertSame(General.螣蛇, General.貴人.next(1, seq));
    assertSame(General.朱雀, General.貴人.next(2, seq));
    assertSame(General.天后, General.貴人.next(11, seq));
    assertSame(General.貴人, General.貴人.next(12, seq));
  }

  @Test
  public void testPrev() {
    assertSame(General.貴人 , General.貴人.prev(-12 , seq));
    assertSame(General.天后 , General.貴人.prev(-11 , seq));
    assertSame(General.朱雀 , General.貴人.prev(-2 , seq));
    assertSame(General.螣蛇 , General.貴人.prev(-1 , seq));
    assertSame(General.貴人 , General.貴人.prev(0 , seq));
    assertSame(General.天后 , General.貴人.prev(1 , seq));
    assertSame(General.太陰 , General.貴人.prev(2 , seq));
    assertSame(General.螣蛇 , General.貴人.prev(11 , seq));
    assertSame(General.貴人 , General.貴人.prev(12 , seq));
  }
}