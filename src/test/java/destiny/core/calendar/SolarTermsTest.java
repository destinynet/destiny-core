/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:29:46
 */
package destiny.core.calendar;

import junit.framework.TestCase;


public class SolarTermsTest extends TestCase
{

  public void testGetIndex()
  {
    assertEquals( 0 , SolarTerms.getIndex(SolarTerms.立春));
    assertEquals( 1 , SolarTerms.getIndex(SolarTerms.雨水));
    assertEquals( 2 , SolarTerms.getIndex(SolarTerms.驚蟄));
    assertEquals( 3 , SolarTerms.getIndex(SolarTerms.春分));
    assertEquals( 4 , SolarTerms.getIndex(SolarTerms.清明));
    assertEquals( 5 , SolarTerms.getIndex(SolarTerms.榖雨));
    assertEquals( 6 , SolarTerms.getIndex(SolarTerms.立夏));
    assertEquals( 7 , SolarTerms.getIndex(SolarTerms.小滿));
    assertEquals( 8 , SolarTerms.getIndex(SolarTerms.芒種));
    assertEquals( 9 , SolarTerms.getIndex(SolarTerms.夏至));
    assertEquals(10 , SolarTerms.getIndex(SolarTerms.小暑));
    assertEquals(11 , SolarTerms.getIndex(SolarTerms.大暑));
    assertEquals(12 , SolarTerms.getIndex(SolarTerms.立秋));
    assertEquals(13 , SolarTerms.getIndex(SolarTerms.處暑));
    assertEquals(14 , SolarTerms.getIndex(SolarTerms.白露));
    assertEquals(15 , SolarTerms.getIndex(SolarTerms.秋分));
    assertEquals(16 , SolarTerms.getIndex(SolarTerms.寒露));
    assertEquals(17 , SolarTerms.getIndex(SolarTerms.霜降));
    assertEquals(18 , SolarTerms.getIndex(SolarTerms.立冬));
    assertEquals(19 , SolarTerms.getIndex(SolarTerms.小雪));
    assertEquals(20 , SolarTerms.getIndex(SolarTerms.大雪));
    assertEquals(21 , SolarTerms.getIndex(SolarTerms.冬至));
    assertEquals(22 , SolarTerms.getIndex(SolarTerms.小寒));
    assertEquals(23 , SolarTerms.getIndex(SolarTerms.大寒));
    
  }

  public void testNext()
  {
    assertSame(SolarTerms.雨水 , SolarTerms.立春.next());
    assertSame(SolarTerms.驚蟄 , SolarTerms.雨水.next());
    assertSame(SolarTerms.春分 , SolarTerms.驚蟄.next());
    assertSame(SolarTerms.清明 , SolarTerms.春分.next());
    assertSame(SolarTerms.榖雨 , SolarTerms.清明.next());
    assertSame(SolarTerms.立夏 , SolarTerms.榖雨.next());
    assertSame(SolarTerms.小滿 , SolarTerms.立夏.next());
    assertSame(SolarTerms.芒種 , SolarTerms.小滿.next());
    assertSame(SolarTerms.夏至 , SolarTerms.芒種.next());
    assertSame(SolarTerms.小暑 , SolarTerms.夏至.next());
    assertSame(SolarTerms.大暑 , SolarTerms.小暑.next());
    assertSame(SolarTerms.立秋 , SolarTerms.大暑.next());
    assertSame(SolarTerms.處暑 , SolarTerms.立秋.next());
    assertSame(SolarTerms.白露 , SolarTerms.處暑.next());
    assertSame(SolarTerms.秋分 , SolarTerms.白露.next());
    assertSame(SolarTerms.寒露 , SolarTerms.秋分.next());
    assertSame(SolarTerms.霜降 , SolarTerms.寒露.next());
    assertSame(SolarTerms.立冬 , SolarTerms.霜降.next());
    assertSame(SolarTerms.小雪 , SolarTerms.立冬.next());
    assertSame(SolarTerms.大雪 , SolarTerms.小雪.next());
    assertSame(SolarTerms.冬至 , SolarTerms.大雪.next());
    assertSame(SolarTerms.小寒 , SolarTerms.冬至.next());
    assertSame(SolarTerms.大寒 , SolarTerms.小寒.next());
    assertSame(SolarTerms.立春 , SolarTerms.大寒.next());
  }

  
  public void testPrevious()
  {
    assertSame(SolarTerms.大寒 , SolarTerms.立春.previous());
    assertSame(SolarTerms.立春 , SolarTerms.雨水.previous());
    assertSame(SolarTerms.雨水 , SolarTerms.驚蟄.previous());
    assertSame(SolarTerms.驚蟄 , SolarTerms.春分.previous());
    assertSame(SolarTerms.春分 , SolarTerms.清明.previous());
    assertSame(SolarTerms.清明 , SolarTerms.榖雨.previous());
    assertSame(SolarTerms.榖雨 , SolarTerms.立夏.previous());
    assertSame(SolarTerms.立夏 , SolarTerms.小滿.previous());
    assertSame(SolarTerms.小滿 , SolarTerms.芒種.previous());
    assertSame(SolarTerms.芒種 , SolarTerms.夏至.previous());
    assertSame(SolarTerms.夏至 , SolarTerms.小暑.previous());
    assertSame(SolarTerms.小暑 , SolarTerms.大暑.previous());
    assertSame(SolarTerms.大暑 , SolarTerms.立秋.previous());
    assertSame(SolarTerms.立秋 , SolarTerms.處暑.previous());
    assertSame(SolarTerms.處暑 , SolarTerms.白露.previous());
    assertSame(SolarTerms.白露 , SolarTerms.秋分.previous());
    assertSame(SolarTerms.秋分 , SolarTerms.寒露.previous());
    assertSame(SolarTerms.寒露 , SolarTerms.霜降.previous());
    assertSame(SolarTerms.霜降 , SolarTerms.立冬.previous());
    assertSame(SolarTerms.立冬 , SolarTerms.小雪.previous());
    assertSame(SolarTerms.小雪 , SolarTerms.大雪.previous());
    assertSame(SolarTerms.大雪 , SolarTerms.冬至.previous());
    assertSame(SolarTerms.冬至 , SolarTerms.小寒.previous());
    assertSame(SolarTerms.小寒 , SolarTerms.大寒.previous());
  }
  
  public void testGet()
  {
    assertSame(SolarTerms.立春 , SolarTerms.get(-24));
    assertSame(SolarTerms.大寒 , SolarTerms.get(-1));
    assertSame(SolarTerms.立春 , SolarTerms.get( 0));
    assertSame(SolarTerms.雨水 , SolarTerms.get( 1));
    assertSame(SolarTerms.驚蟄 , SolarTerms.get( 2));
    assertSame(SolarTerms.春分 , SolarTerms.get( 3));
    assertSame(SolarTerms.清明 , SolarTerms.get( 4));
    assertSame(SolarTerms.榖雨 , SolarTerms.get( 5));
    assertSame(SolarTerms.立夏 , SolarTerms.get( 6));
    assertSame(SolarTerms.小滿 , SolarTerms.get( 7));
    assertSame(SolarTerms.芒種 , SolarTerms.get( 8));
    assertSame(SolarTerms.夏至 , SolarTerms.get( 9));
    assertSame(SolarTerms.小暑 , SolarTerms.get(10));
    assertSame(SolarTerms.大暑 , SolarTerms.get(11));
    assertSame(SolarTerms.立秋 , SolarTerms.get(12));
    assertSame(SolarTerms.處暑 , SolarTerms.get(13));
    assertSame(SolarTerms.白露 , SolarTerms.get(14));
    assertSame(SolarTerms.秋分 , SolarTerms.get(15));
    assertSame(SolarTerms.寒露 , SolarTerms.get(16));
    assertSame(SolarTerms.霜降 , SolarTerms.get(17));
    assertSame(SolarTerms.立冬 , SolarTerms.get(18));
    assertSame(SolarTerms.小雪 , SolarTerms.get(19));
    assertSame(SolarTerms.大雪 , SolarTerms.get(20));
    assertSame(SolarTerms.冬至 , SolarTerms.get(21));
    assertSame(SolarTerms.小寒 , SolarTerms.get(22));
    assertSame(SolarTerms.大寒 , SolarTerms.get(23));
    assertSame(SolarTerms.立春 , SolarTerms.get(24));
    
  }
}
