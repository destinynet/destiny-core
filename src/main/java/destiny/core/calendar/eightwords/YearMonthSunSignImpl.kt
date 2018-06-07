/**
 * Created by smallufo on 2018-06-07.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.IStarPosition
import destiny.core.calendar.ILocation
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUnconstrained

/**
 * 依據星座劃分月令 , coolwind 提出的理論
 *
 * 以此盤而言 : 1985年 4月11日 9時50分 , https://goo.gl/FCZ6iU
 * 傳統排法， 清明 -> 立夏 , 辰月
 * 但此盤出生於 牡羊(3/21~4/20) 範圍內 , 故，天干不變，地支改為 卯
 *
 * [StemBranchUnconstrained.庚辰.previous] ==> [StemBranchUnconstrained.庚卯]
 * 庚辰.prev -> 庚卯
 *
 * 辛 庚 庚 乙　　
 * 巳 辰 辰 丑
 *
 * 時 日 月 年 順 坤
 * 辛 庚 庚 乙
 * 辰 辰 卯 丑
 *
 */
class YearMonthSunSignImpl(
  /** 換年的度數 , 通常是立春點 (315) 換年 */
  changeYearDegree: Double = 315.0,
  starPositionImpl: IStarPosition<*>,
  override val southernHemisphereOpposition: Boolean = false,
  override val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR) :
  YearEclipticDegreeImpl(changeYearDegree, starPositionImpl), IYearMonth {

  override fun getMonth(gmtJulDay: Double, location: ILocation): StemBranch {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}