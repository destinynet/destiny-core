/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertNotNull

class ChartCanvasSimpleTest {


  /**
  ７９　２５　９７
  巽七　離三　坤五
  　　　　　　　　
  ８８　６１　４３
  震六　中八　兌一
  　　　　　　　　
  ３４　１６　５２
  艮二　坎四　乾九
   */
  @Test
  fun `八運，甲山庚向 , 坎底`() {
    val chart = ChartMntPresenter(8, Mountain.甲, Symbol.坎)
    val canvas = ChartCanvasSimple(chart)
    //println(canvas.htmlOutput)
    println(canvas.toString())
  }

  /**
  １４　６８　８６
  巽六　離二　坤四
  　　　　　　　　
  ９５　２３　４１
  震五　中七　兌九
  　　　　　　　　
  ５９　７７　３２
  艮一　坎三　乾八
   */
  @Test
  fun `七運，午山子向 , 坎底`() {
    val chart = ChartMntPresenter(7, Mountain.午, Symbol.坎)
    val canvas = ChartCanvasSimple(chart)
    println(canvas.htmlOutput)
    println(canvas.toString())
  }

  /**
  ５９　９５　１４
  艮一　震五　巽六
  　　　　　　　　
  ７７　２３　６８
  坎三　中七　離二
  　　　　　　　　
  ３２　４１　８６
  乾八　兌九　坤四
   */
  @Test
  fun `七運，午山子向 , 兌底`() {
    val chart = ChartMntPresenter(7, Mountain.午, Symbol.兌)
    val canvas = ChartCanvasSimple(chart)
    assertNotNull(canvas.htmlOutput)
    println(canvas.toString())
  }

}