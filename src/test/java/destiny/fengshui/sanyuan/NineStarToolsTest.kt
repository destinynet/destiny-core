/**
 * Created by smallufo on 2019-11-23.
 */
package destiny.fengshui.sanyuan

import destiny.core.calendar.SolarTerms.小雪
import destiny.core.calendar.SolarTerms.立春
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch.乙丑
import destiny.core.chinese.StemBranch.癸酉
import destiny.fengshui.sanyuan.NineStar.*
import destiny.iching.Symbol.*
import kotlin.test.Test
import kotlin.test.assertSame


class NineStarToolsTest {


  /**
   * 年紫白方位 (年星到方)
   */
  @Test
  fun testGetYearStar() {
    assertSame(NineStar.of(6), NineStarTools.getYearStar(貪狼, 坎))
    assertSame(NineStar.of(7), NineStarTools.getYearStar(貪狼, 坤))
    assertSame(NineStar.of(8), NineStarTools.getYearStar(貪狼, 震))
    assertSame(NineStar.of(9), NineStarTools.getYearStar(貪狼, 巽))
    assertSame(NineStar.of(2), NineStarTools.getYearStar(貪狼, 乾))
    assertSame(NineStar.of(3), NineStarTools.getYearStar(貪狼, 兌))
    assertSame(NineStar.of(4), NineStarTools.getYearStar(貪狼, 艮))
    assertSame(NineStar.of(5), NineStarTools.getYearStar(貪狼, 離))

    assertSame(廉貞, NineStarTools.getYearStar(右弼, 坎))
    assertSame(武曲, NineStarTools.getYearStar(右弼, 坤))
    assertSame(破軍, NineStarTools.getYearStar(右弼, 震))
    assertSame(左輔, NineStarTools.getYearStar(右弼, 巽))
    assertSame(貪狼, NineStarTools.getYearStar(右弼, 乾))
    assertSame(巨門, NineStarTools.getYearStar(右弼, 兌))
    assertSame(祿存, NineStarTools.getYearStar(右弼, 艮))
    assertSame(文曲, NineStarTools.getYearStar(右弼, 離))
  }


  /** 月紫白入中 */
  @Test
  fun testGetCenterStar() {
    // 子午卯酉年
    assertSame(左輔, NineStarTools.getCenterStar(子, 寅)) // 1
    assertSame(武曲, NineStarTools.getCenterStar(子, 丑)) // 12

    // 辰戌丑未年
    assertSame(廉貞, NineStarTools.getCenterStar(辰, 寅)) // 1
    assertSame(祿存, NineStarTools.getCenterStar(辰, 丑)) // 12

    // 寅巳申亥年
    assertSame(巨門, NineStarTools.getCenterStar(寅, 寅)) // 1
    assertSame(右弼, NineStarTools.getCenterStar(寅, 丑)) // 12
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
      assertSame(貪狼, NineStarTools.getMonthStar(year, month, 兌))
      assertSame(巨門, NineStarTools.getMonthStar(year, month, 艮))
      assertSame(祿存, NineStarTools.getMonthStar(year, month, 離))
      assertSame(文曲, NineStarTools.getMonthStar(year, month, 坎))
      assertSame(廉貞, NineStarTools.getMonthStar(year, month, 坤))
      assertSame(武曲, NineStarTools.getMonthStar(year, month, 震))
      assertSame(破軍, NineStarTools.getMonthStar(year, month, 巽))
      assertSame(右弼, NineStarTools.getMonthStar(year, month, 乾))
    }

    // last row
    // 子午卯酉年9月(戌) , 寅巳申亥年 3(辰)/12(丑)月 , 辰戌丑未年 6(未)月
    listOf(
      子 to 戌, 午 to 戌, 卯 to 戌, 酉 to 戌,
      寅 to 辰, 巳 to 辰, 申 to 辰, 亥 to 辰,
      寅 to 丑, 巳 to 丑, 申 to 丑, 亥 to 丑,
      辰 to 未, 戌 to 未, 丑 to 未, 未 to 未
    ).forEach { (year, month) ->
      assertSame(貪狼, NineStarTools.getMonthStar(year, month, 乾))
      assertSame(巨門, NineStarTools.getMonthStar(year, month, 兌))
      assertSame(祿存, NineStarTools.getMonthStar(year, month, 艮))
      assertSame(文曲, NineStarTools.getMonthStar(year, month, 離))
      assertSame(廉貞, NineStarTools.getMonthStar(year, month, 坎))
      assertSame(武曲, NineStarTools.getMonthStar(year, month, 坤))
      assertSame(破軍, NineStarTools.getMonthStar(year, month, 震))
      assertSame(左輔, NineStarTools.getMonthStar(year, month, 巽))
    }
  }


  /**
   * 日紫白飛星到方
   */
  @Test
  fun testGetDayStar() {
    // 2019-11-24 乙亥月 , 乙丑日 , 參考 http://www.laohuangli.net/jiugongfeixing/2019/2019-11-24.html
    assertSame(貪狼 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 坎))
    assertSame(巨門 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 坤))
    assertSame(祿存 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 震))
    assertSame(文曲 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 巽))
    //assertSame(廉貞 ,  NineStarTools.getDayStar(小雪 , StemBranch.乙丑 , null))
    assertSame(武曲 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 乾))
    assertSame(破軍 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 兌))
    assertSame(左輔 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 艮))
    assertSame(右弼 ,  NineStarTools.getDayStar(小雪 , 乙丑 , 離))

    // 2019-2-5 丙寅月 , 癸酉日 , 參考 http://www.laohuangli.net/jiugongfeixing/2019/2019-2-5.html
    //assertSame(貪狼 ,  NineStarTools.getDayStar(立春 , 癸酉 ))
    assertSame(巨門 ,  NineStarTools.getDayStar(立春 , 癸酉 , 乾))
    assertSame(祿存 ,  NineStarTools.getDayStar(立春 , 癸酉 , 兌))
    assertSame(文曲 ,  NineStarTools.getDayStar(立春 , 癸酉 , 艮))
    assertSame(武曲 ,  NineStarTools.getDayStar(立春 , 癸酉 , 坎))
    assertSame(破軍 ,  NineStarTools.getDayStar(立春 , 癸酉 , 坤))
    assertSame(左輔 ,  NineStarTools.getDayStar(立春 , 癸酉 , 震))
    assertSame(右弼 ,  NineStarTools.getDayStar(立春 , 癸酉 , 巽))
  }

  /**
   * 時紫白飛星到方
   * test data : 2019-11-24 12:30 , 己亥年 , 乙亥月 , 乙丑日 , 午時
   * 參考資料 http://www.laohuangli.net/jiugongfeixing/2019/2019-11-24.html
   */
  @Test
  fun testGetHourStar_夏至後冬至前() {
    assertSame(文曲 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 坎))
    assertSame(武曲 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 艮))
    assertSame(巨門 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 震))
    assertSame(貪狼 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 巽))
    assertSame(廉貞 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 離))
    assertSame(祿存 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 坤))
    assertSame(破軍 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 兌))
    assertSame(左輔 , NineStarTools.getHourStar(241.0 , 丑 , 午 , 乾))
  }

  /**
   * 時紫白飛星到方
   * test data : 2019-2-5 12:30 , 己亥年 , 丙寅月 , 癸酉日 , 午時
   * 參考資料 http://www.laohuangli.net/jiugongfeixing/2019/2019-2-5.html
   */
  @Test
  fun testGetHourStar_冬至後夏至前() {
    assertSame(祿存 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 坎))
    assertSame(貪狼 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 艮))
    assertSame(廉貞 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 震))
    assertSame(武曲 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 巽))
    assertSame(巨門 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 離))
    assertSame(文曲 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 坤))
    assertSame(右弼 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 兌))
    assertSame(左輔 , NineStarTools.getHourStar(316.0 , 酉 , 午 , 乾))
  }
}
