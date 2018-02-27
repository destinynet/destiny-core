/**
 * Created by smallufo on 2018-02-27.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test

class ChartCanvasSimpleTest {

  @Test
  fun `八運，甲山庚向 , 坎底`() {
    val chart = Chart(8, Mountain.甲, Symbol.坎)
    val canvas = ChartCanvasSimple(chart)
    //println(canvas.htmlOutput)
    println(canvas.toString())
  }

  /**
  14 　 68 　 86
  巽六　離二　坤四
  　　　　　　　　
  95 　 23 　 41
  震五　中七　兌九
  　　　　　　　　
  59 　 77 　 32
  艮一　坎三　乾八
   */
  @Test
  fun `七運，午山子向 , 坎底`() {
    val chart = Chart(7, Mountain.午, Symbol.坎)
    val canvas = ChartCanvasSimple(chart)
    println(canvas.htmlOutput)
  }

  /**
  59 　 95 　 14
  艮一　震五　巽六
  　　　　　　　　
  77 　 23 　 68
  坎三　中七　離二
  　　　　　　　　
  32 　 41 　 86
  乾八　兌九　坤四
   */
  @Test
  fun `七運，午山子向 , 兌底`() {
    val chart = Chart(7, Mountain.午, Symbol.兌)
    val canvas = ChartCanvasSimple(chart)
    println(canvas.htmlOutput)
  }
}