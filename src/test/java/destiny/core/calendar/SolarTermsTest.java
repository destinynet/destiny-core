/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:29:46
 */
package destiny.core.calendar;

import org.junit.Test;

import static destiny.core.calendar.SolarTerms.*;
import static destiny.core.chinese.Branch.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


public class SolarTermsTest {

  /** 節氣 -> 地支 */
  @Test
  public void testGetBranch() {
    assertSame(寅 , 立春.getBranch());
    assertSame(寅 , 雨水.getBranch());
    assertSame(卯 , 驚蟄.getBranch());
    assertSame(卯 , 春分.getBranch());
    assertSame(辰 , 清明.getBranch());
    assertSame(辰 , 穀雨.getBranch());
    assertSame(巳 , 立夏.getBranch());
    assertSame(巳 , 小滿.getBranch());
    assertSame(午 , 芒種.getBranch());
    assertSame(午 , 夏至.getBranch());
    assertSame(未 , 小暑.getBranch());
    assertSame(未 , 大暑.getBranch());
    assertSame(申 , 立秋.getBranch());
    assertSame(申 , 處暑.getBranch());
    assertSame(酉 , 白露.getBranch());
    assertSame(酉 , 秋分.getBranch());
    assertSame(戌 , 寒露.getBranch());
    assertSame(戌 , 霜降.getBranch());
    assertSame(亥 , 立冬.getBranch());
    assertSame(亥 , 小雪.getBranch());
    assertSame(子 , 大雪.getBranch());
    assertSame(子 , 冬至.getBranch());
    assertSame(丑 , 小寒.getBranch());
    assertSame(丑 , 大寒.getBranch());
  }

  @Test
  public void testGetIndex() {
    assertEquals(0, getIndex(立春));
    assertEquals(1, getIndex(雨水));
    assertEquals(2, getIndex(驚蟄));
    assertEquals(3, getIndex(春分));
    assertEquals(4, getIndex(清明));
    assertEquals(5, getIndex(穀雨));
    assertEquals(6, getIndex(立夏));
    assertEquals(7, getIndex(小滿));
    assertEquals(8, getIndex(芒種));
    assertEquals(9, getIndex(夏至));
    assertEquals(10, getIndex(小暑));
    assertEquals(11, getIndex(大暑));
    assertEquals(12, getIndex(立秋));
    assertEquals(13, getIndex(處暑));
    assertEquals(14, getIndex(白露));
    assertEquals(15, getIndex(秋分));
    assertEquals(16, getIndex(寒露));
    assertEquals(17, getIndex(霜降));
    assertEquals(18, getIndex(立冬));
    assertEquals(19, getIndex(小雪));
    assertEquals(20, getIndex(大雪));
    assertEquals(21, getIndex(冬至));
    assertEquals(22, getIndex(小寒));
    assertEquals(23, getIndex(大寒));

  }

  @Test
  public void testNext() {
    assertSame(雨水, 立春.next());
    assertSame(驚蟄, 雨水.next());
    assertSame(春分, 驚蟄.next());
    assertSame(清明, 春分.next());
    assertSame(穀雨, 清明.next());
    assertSame(立夏, 穀雨.next());
    assertSame(小滿, 立夏.next());
    assertSame(芒種, 小滿.next());
    assertSame(夏至, 芒種.next());
    assertSame(小暑, 夏至.next());
    assertSame(大暑, 小暑.next());
    assertSame(立秋, 大暑.next());
    assertSame(處暑, 立秋.next());
    assertSame(白露, 處暑.next());
    assertSame(秋分, 白露.next());
    assertSame(寒露, 秋分.next());
    assertSame(霜降, 寒露.next());
    assertSame(立冬, 霜降.next());
    assertSame(小雪, 立冬.next());
    assertSame(大雪, 小雪.next());
    assertSame(冬至, 大雪.next());
    assertSame(小寒, 冬至.next());
    assertSame(大寒, 小寒.next());
    assertSame(立春, 大寒.next());
  }


  @Test
  public void testPrevious() {
    assertSame(大寒, 立春.previous());
    assertSame(立春, 雨水.previous());
    assertSame(雨水, 驚蟄.previous());
    assertSame(驚蟄, 春分.previous());
    assertSame(春分, 清明.previous());
    assertSame(清明, 穀雨.previous());
    assertSame(穀雨, 立夏.previous());
    assertSame(立夏, 小滿.previous());
    assertSame(小滿, 芒種.previous());
    assertSame(芒種, 夏至.previous());
    assertSame(夏至, 小暑.previous());
    assertSame(小暑, 大暑.previous());
    assertSame(大暑, 立秋.previous());
    assertSame(立秋, 處暑.previous());
    assertSame(處暑, 白露.previous());
    assertSame(白露, 秋分.previous());
    assertSame(秋分, 寒露.previous());
    assertSame(寒露, 霜降.previous());
    assertSame(霜降, 立冬.previous());
    assertSame(立冬, 小雪.previous());
    assertSame(小雪, 大雪.previous());
    assertSame(大雪, 冬至.previous());
    assertSame(冬至, 小寒.previous());
    assertSame(小寒, 大寒.previous());
  }

  @Test
  public void testGet() {
    assertSame(立春, SolarTerms.get(-24));
    assertSame(大寒, SolarTerms.get(-1));
    assertSame(立春, SolarTerms.get(0));
    assertSame(雨水, SolarTerms.get(1));
    assertSame(驚蟄, SolarTerms.get(2));
    assertSame(春分, SolarTerms.get(3));
    assertSame(清明, SolarTerms.get(4));
    assertSame(穀雨, SolarTerms.get(5));
    assertSame(立夏, SolarTerms.get(6));
    assertSame(小滿, SolarTerms.get(7));
    assertSame(芒種, SolarTerms.get(8));
    assertSame(夏至, SolarTerms.get(9));
    assertSame(小暑, SolarTerms.get(10));
    assertSame(大暑, SolarTerms.get(11));
    assertSame(立秋, SolarTerms.get(12));
    assertSame(處暑, SolarTerms.get(13));
    assertSame(白露, SolarTerms.get(14));
    assertSame(秋分, SolarTerms.get(15));
    assertSame(寒露, SolarTerms.get(16));
    assertSame(霜降, SolarTerms.get(17));
    assertSame(立冬, SolarTerms.get(18));
    assertSame(小雪, SolarTerms.get(19));
    assertSame(大雪, SolarTerms.get(20));
    assertSame(冬至, SolarTerms.get(21));
    assertSame(小寒, SolarTerms.get(22));
    assertSame(大寒, SolarTerms.get(23));
    assertSame(立春, SolarTerms.get(24));

  }
}
