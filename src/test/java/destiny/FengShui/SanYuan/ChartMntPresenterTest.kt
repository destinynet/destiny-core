/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import kotlin.test.Test

class ChartMntPresenterTest {

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
    val chart = ChartMntPresenter(7, Mountain.午, Symbol.坎)
    println(chart.posMap)
  }

  @Test
  fun `七運，午山子向 , 乾底`() {
    val chart = ChartMntPresenter(7, Mountain.午, Symbol.乾)
    println(chart.posMap)
  }

}