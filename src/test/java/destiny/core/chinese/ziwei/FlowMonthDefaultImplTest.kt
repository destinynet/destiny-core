/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class FlowMonthDefaultImplTest {

  private val logger = KotlinLogging.logger { }

  internal var impl: IFlowMonth = FlowMonthDefaultImpl()

  @Test
  fun testString() {
    assertNotNull(impl.toString(Locale.TAIWAN))
    assertNotNull(impl.toString(Locale.SIMPLIFIED_CHINESE))
    logger.info("title tw = {} , cn = {}", impl.toString(Locale.TAIWAN), impl.toString(Locale.SIMPLIFIED_CHINESE))
  }


  /**
   * 參考資料
   * https://tw.answers.yahoo.com/question/index?qid=20070122000010KK01428
   *
   * 舉例 ：某甲為八月，（23~01時）子時出生，今年為乙酉（雞）年，求乙酉年九月之命宮。
   *
   * 解：地支酉宮起正月，逆數生月至八月，即『酉、申、未、午、巳、辰、卯、寅』，是為【寅宮】；
   * 再從此順數生時，而生時（子時）為1，即『寅』，如此可知【寅宮】為某甲流年之正月命宮。
   *
   * 餘依此類推，未宮為二月、申宮為三月．．．戌宮即為九月命宮。
   */
  @Test
  fun getFlowMonth1() {
    assertSame(戌, impl.getFlowMonth(酉, 戌, 8, 子))
  }


  /**
   * 根據此頁面範例 https://goo.gl/zwWsmO
   * 農曆：(民國)56年11月×日辰時
   *
   * 一個在2002年新暦2月25日・中六合彩的命例:
   * 男:壬午年壬寅月甲子日36歳,大運乙巳
   * 天府天馬同宮,雙祿在辰午二宮夾輔,壬年祿存在亥照會,祿馬同鄕主横財,「斗君子」. // ==> 流年午年 , 斗君在子
   *
   * 農暦正月十四日命宮在丑,                                            // ==> 流月 寅月
   * 甲子日廉貞在卯福徳宮化祿,
   * 天盤財帛宮在大運流年雙化禄,
   * 此日買中六合彩中齊六個字,發了一筆橫財。
   */
  @Test
  fun testFlowMonth2() {
    // 午年斗君在子 , 寅月（一月），走0 步，所以流月命宮還是在子
    assertSame(子, impl.getFlowMonth(午, 寅, 11, 辰))
  }


}
