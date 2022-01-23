/**
 * Created by smallufo on 2019-11-26.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.calendar.SolarTerms.*
import destiny.core.calendar.chinese.Yuan
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch.*
import destiny.core.fengshui.sanyuan.NineStar.*
import destiny.core.iching.Symbol.*
import kotlin.test.Test
import kotlin.test.assertSame

class NineStarFunctionsTest {

  /**
   * 明清之後甲子年
   *
   * 明孝宗弘治17年(1504 甲子年)起，上元一運
   * 明世宗嘉靖43年(1564 甲子年)起，中元四運
   * 明熹宗天啟四年(1624 甲子年)起，下元七運
   *
   * 清聖祖康熙23年(1684 甲子年)起，上元一運
   * 清    乾隆九年(1744 甲子年)起，中元四運
   * 清    家慶九年(1804 甲子年)起，下元七運
   *
   * 清    同治三年(1864 甲子年)起，上元一運
   *       民國13年(1924 甲子年)起，中元四運
   */
  @Test
  fun testMingChinDynasty() {
    assertSame(1, NineStarFunctions.getYearCenterStar(Yuan.UP, 甲子).period)
    assertSame(4, NineStarFunctions.getYearCenterStar(Yuan.MID, 甲子).period)
    assertSame(7, NineStarFunctions.getYearCenterStar(Yuan.LOW, 甲子).period)
  }

  /**
   * 年紫白方位 (年星到方)
   */
  @Test
  fun testGetYearStar() {
    assertSame(NineStar.of(6), NineStarFunctions.getYearStar(貪狼, 坎))
    assertSame(NineStar.of(7), NineStarFunctions.getYearStar(貪狼, 坤))
    assertSame(NineStar.of(8), NineStarFunctions.getYearStar(貪狼, 震))
    assertSame(NineStar.of(9), NineStarFunctions.getYearStar(貪狼, 巽))
    assertSame(NineStar.of(2), NineStarFunctions.getYearStar(貪狼, 乾))
    assertSame(NineStar.of(3), NineStarFunctions.getYearStar(貪狼, 兌))
    assertSame(NineStar.of(4), NineStarFunctions.getYearStar(貪狼, 艮))
    assertSame(NineStar.of(5), NineStarFunctions.getYearStar(貪狼, 離))

    assertSame(廉貞, NineStarFunctions.getYearStar(右弼, 坎))
    assertSame(武曲, NineStarFunctions.getYearStar(右弼, 坤))
    assertSame(破軍, NineStarFunctions.getYearStar(右弼, 震))
    assertSame(左輔, NineStarFunctions.getYearStar(右弼, 巽))
    assertSame(貪狼, NineStarFunctions.getYearStar(右弼, 乾))
    assertSame(巨門, NineStarFunctions.getYearStar(右弼, 兌))
    assertSame(祿存, NineStarFunctions.getYearStar(右弼, 艮))
    assertSame(文曲, NineStarFunctions.getYearStar(右弼, 離))
  }


  /** 月紫白入中 */
  @Test
  fun testGetCenterStar() {
    // 子午卯酉年
    assertSame(左輔, NineStarFunctions.getMonthCenterStar(子, 寅)) // 1
    assertSame(武曲, NineStarFunctions.getMonthCenterStar(子, 丑)) // 12

    // 辰戌丑未年
    assertSame(廉貞, NineStarFunctions.getMonthCenterStar(辰, 寅)) // 1
    assertSame(祿存, NineStarFunctions.getMonthCenterStar(辰, 丑)) // 12

    // 寅巳申亥年
    assertSame(巨門, NineStarFunctions.getMonthCenterStar(寅, 寅)) // 1
    assertSame(右弼, NineStarFunctions.getMonthCenterStar(寅, 丑)) // 12
  }


  /**
   * 月紫白飛星到方
   * cf : page 236
   * */
  @Test
  fun testGetMonthStar() {
    // first row
    // 子午卯酉年一月/10月(亥) , 寅巳申亥 年四月 , 辰戌丑未年 7月
    listOf(
      子 to 寅, 午 to 寅, 卯 to 寅, 酉 to 寅,
      子 to 亥, 午 to 亥, 卯 to 亥, 酉 to 亥,
      寅 to 巳, 巳 to 巳, 申 to 巳, 亥 to 巳,
      辰 to 申, 戌 to 申, 丑 to 申, 未 to 申
    ).forEach { (year, month) ->
      assertSame(貪狼, NineStarFunctions.getMonthStar(year, month, 兌))
      assertSame(巨門, NineStarFunctions.getMonthStar(year, month, 艮))
      assertSame(祿存, NineStarFunctions.getMonthStar(year, month, 離))
      assertSame(文曲, NineStarFunctions.getMonthStar(year, month, 坎))
      assertSame(廉貞, NineStarFunctions.getMonthStar(year, month, 坤))
      assertSame(武曲, NineStarFunctions.getMonthStar(year, month, 震))
      assertSame(破軍, NineStarFunctions.getMonthStar(year, month, 巽))
      assertSame(右弼, NineStarFunctions.getMonthStar(year, month, 乾))
    }

    // last row
    // 子午卯酉年9月(戌) , 寅巳申亥年 3(辰)/12(丑)月 , 辰戌丑未年 6(未)月
    listOf(
      子 to 戌, 午 to 戌, 卯 to 戌, 酉 to 戌,
      寅 to 辰, 巳 to 辰, 申 to 辰, 亥 to 辰,
      寅 to 丑, 巳 to 丑, 申 to 丑, 亥 to 丑,
      辰 to 未, 戌 to 未, 丑 to 未, 未 to 未
    ).forEach { (year, month) ->
      assertSame(貪狼, NineStarFunctions.getMonthStar(year, month, 乾))
      assertSame(巨門, NineStarFunctions.getMonthStar(year, month, 兌))
      assertSame(祿存, NineStarFunctions.getMonthStar(year, month, 艮))
      assertSame(文曲, NineStarFunctions.getMonthStar(year, month, 離))
      assertSame(廉貞, NineStarFunctions.getMonthStar(year, month, 坎))
      assertSame(武曲, NineStarFunctions.getMonthStar(year, month, 坤))
      assertSame(破軍, NineStarFunctions.getMonthStar(year, month, 震))
      assertSame(左輔, NineStarFunctions.getMonthStar(year, month, 巽))
    }
  }


  /**
   * 日紫白飛星到方
   *
   * 夏至到冬至 , 逆飛
   */
  @Test
  fun testGetDayStar_夏至到冬至() {
    //    // 2019-11-24 乙亥月 , 乙丑日 , 參考 http://www.laohuangli.net/jiugongfeixing/2019/2019-11-24.html 有誤！
    assertSame(廉貞, NineStarFunctions.getDayCenterStar(小雪, 乙丑))
//    assertSame(貪狼, nineStarImpl.getDayStar(小雪, 乙丑, 坎))
//    assertSame(巨門, nineStarImpl.getDayStar(小雪, 乙丑, 坤))
//    assertSame(祿存, nineStarImpl.getDayStar(小雪, 乙丑, 震))
//    assertSame(文曲, nineStarImpl.getDayStar(小雪, 乙丑, 巽))
//    assertSame(武曲, nineStarImpl.getDayStar(小雪, 乙丑, 乾))
//    assertSame(破軍, nineStarImpl.getDayStar(小雪, 乙丑, 兌))
//    assertSame(左輔, nineStarImpl.getDayStar(小雪, 乙丑, 艮))
//    assertSame(右弼, nineStarImpl.getDayStar(小雪, 乙丑, 離))

    // 2017-11-8 8:00 辰時 , 丁酉年 , 辛亥月 , 己亥日 , 戊辰時 , 參考 https://play.google.com/store/apps/details?id=com.amtbfate.CalendarZibai&hl=zh_TW
    assertSame(破軍, NineStarFunctions.getDayCenterStar(立冬, 己亥))
    assertSame(巨門, NineStarFunctions.getDayStar(立冬, 己亥, 坎))
    assertSame(文曲, NineStarFunctions.getDayStar(立冬, 己亥, 艮))
    assertSame(右弼, NineStarFunctions.getDayStar(立冬, 己亥, 震))
    assertSame(左輔, NineStarFunctions.getDayStar(立冬, 己亥, 巽))
    assertSame(祿存, NineStarFunctions.getDayStar(立冬, 己亥, 離))
    assertSame(貪狼, NineStarFunctions.getDayStar(立冬, 己亥, 坤))
    assertSame(廉貞, NineStarFunctions.getDayStar(立冬, 己亥, 兌))
    assertSame(武曲, NineStarFunctions.getDayStar(立冬, 己亥, 乾))
  }


  /**
   * 日紫白飛星到方
   *
   * 冬至到夏至 , 順飛
   */
  @Test
  fun testGetDayStar_冬至到夏至() {
    // 2019-2-5 丙寅月 , 癸酉日 , 參考 http://www.laohuangli.net/jiugongfeixing/2019/2019-2-5.html
    assertSame(貪狼, NineStarFunctions.getDayCenterStar(立春, 癸酉))
    assertSame(巨門, NineStarFunctions.getDayStar(立春, 癸酉, 乾))
    assertSame(祿存, NineStarFunctions.getDayStar(立春, 癸酉, 兌))
    assertSame(文曲, NineStarFunctions.getDayStar(立春, 癸酉, 艮))
    assertSame(武曲, NineStarFunctions.getDayStar(立春, 癸酉, 坎))
    assertSame(破軍, NineStarFunctions.getDayStar(立春, 癸酉, 坤))
    assertSame(左輔, NineStarFunctions.getDayStar(立春, 癸酉, 震))
    assertSame(右弼, NineStarFunctions.getDayStar(立春, 癸酉, 巽))
  }

}
