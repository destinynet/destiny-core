/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 11:22:16
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertSame

class ChartTest {

  /**
   * 七運，午山子向
   *
   * 14  68  86
   * 六  二  四
   *
   * 95  23  41
   * 五  七  九
   *
   * 59  77  32
   * 一  三  八
   */
  @Test
  fun testChart1() {

    val chart = Chart(7, Mountain.午, Symbol.坎)

    var cb: ChartBlock

    cb = chart.getChartBlock(Symbol.坎)
    assertSame(7, cb.mountain)
    assertSame(7, cb.direction)
    assertSame(3, cb.period)

    cb = chart.getChartBlock(Symbol.艮)
    assertSame(5, cb.mountain)
    assertSame(9, cb.direction)
    assertSame(1, cb.period)

    cb = chart.getChartBlock(Symbol.震)
    assertSame(9, cb.mountain)
    assertSame(5, cb.direction)
    assertSame(5, cb.period)

    cb = chart.getChartBlock(Symbol.巽)
    assertSame(1, cb.mountain)
    assertSame(4, cb.direction)
    assertSame(6, cb.period)

    cb = chart.getChartBlock(Symbol.離)
    assertSame(6, cb.mountain)
    assertSame(8, cb.direction)
    assertSame(2, cb.period)

    cb = chart.getChartBlock(Symbol.坤)
    assertSame(8, cb.mountain)
    assertSame(6, cb.direction)
    assertSame(4, cb.period)

    cb = chart.getChartBlock(Symbol.兌)
    assertSame(4, cb.mountain)
    assertSame(1, cb.direction)
    assertSame(9, cb.period)

    cb = chart.getChartBlock(Symbol.乾)
    assertSame(3, cb.mountain)
    assertSame(2, cb.direction)
    assertSame(8, cb.period)
  }

  /**
   * 七運，午山子向 , 與前者一樣，只是換了觀點，不影響結果
   *
   * 14  68  86
   * 六  二  四
   *
   * 95  23  41
   * 五  七  九
   *
   * 59  77  32
   * 一  三  八
   */
  @Test
  fun testChart2() {
    val chart = Chart(7, Mountain.午, Symbol.乾)
    var cb: ChartBlock

    cb = chart.getChartBlock(Symbol.坎)
    assertSame(7, cb.mountain)
    assertSame(7, cb.direction)
    assertSame(3, cb.period)

    cb = chart.getChartBlock(Symbol.艮)
    assertSame(5, cb.mountain)
    assertSame(9, cb.direction)
    assertSame(1, cb.period)

    cb = chart.getChartBlock(Symbol.震)
    assertSame(9, cb.mountain)
    assertSame(5, cb.direction)
    assertSame(5, cb.period)

    cb = chart.getChartBlock(Symbol.巽)
    assertSame(1, cb.mountain)
    assertSame(4, cb.direction)
    assertSame(6, cb.period)

    cb = chart.getChartBlock(Symbol.離)
    assertSame(6, cb.mountain)
    assertSame(8, cb.direction)
    assertSame(2, cb.period)

    cb = chart.getChartBlock(Symbol.坤)
    assertSame(8, cb.mountain)
    assertSame(6, cb.direction)
    assertSame(4, cb.period)

    cb = chart.getChartBlock(Symbol.兌)
    assertSame(4, cb.mountain)
    assertSame(1, cb.direction)
    assertSame(9, cb.period)

    cb = chart.getChartBlock(Symbol.乾)
    assertSame(3, cb.mountain)
    assertSame(2, cb.direction)
    assertSame(8, cb.period)
  }


  /**
   * 一運丙山 (五入中)
   *
   * 47  92  29
   * 九  五  七
   *
   * 38  56  74
   * 八  一  三
   *
   * 83  11  65
   * 四  六  二
   *
   */
  @Test
  fun testChart3() {
    val chart = Chart(1, Mountain.丙, Symbol.乾)
    var cb: ChartBlock

    cb = chart.getChartBlock(Symbol.坎)
    assertSame(1, cb.mountain)
    assertSame(1, cb.direction)
    assertSame(6, cb.period)

    cb = chart.getChartBlock(Symbol.艮)
    assertSame(8, cb.mountain)
    assertSame(3, cb.direction)
    assertSame(4, cb.period)

    cb = chart.getChartBlock(Symbol.震)
    assertSame(3, cb.mountain)
    assertSame(8, cb.direction)
    assertSame(8, cb.period)

    cb = chart.getChartBlock(Symbol.巽)
    assertSame(4, cb.mountain)
    assertSame(7, cb.direction)
    assertSame(9, cb.period)

    cb = chart.getChartBlock(Symbol.離)
    assertSame(9, cb.mountain)
    assertSame(2, cb.direction)
    assertSame(5, cb.period)

    cb = chart.getChartBlock(Symbol.坤)
    assertSame(2, cb.mountain)
    assertSame(9, cb.direction)
    assertSame(7, cb.period)

    cb = chart.getChartBlock(Symbol.兌)
    assertSame(7, cb.mountain)
    assertSame(4, cb.direction)
    assertSame(3, cb.period)

    cb = chart.getChartBlock(Symbol.乾)
    assertSame(6, cb.mountain)
    assertSame(5, cb.direction)
    assertSame(2, cb.period)
  }
}
