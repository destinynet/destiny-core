/**
 * Created by smallufo on 2019-04-26.
 */
package destiny.core.chinese

import destiny.core.astrology.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.GmtJulDay
import destiny.core.iching.Congenital.Circle.aheadOf
import destiny.core.iching.Congenital.Circle.next
import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram
import destiny.core.iching.Symbol
import destiny.tools.KotlinLogging
import java.io.Serializable
import java.util.*

/**
 * 伏羲先天六十四卦天圓地方圖
 * https://i.imgur.com/g34Zeup.jpg
 */
class DailyHexagramCongenitalImpl(val starTransitImpl: IStarTransit,
                                  private val starPosImpl: IStarPosition<*>) : IDailyHexagram, Serializable {

  override fun getHexagram(gmtJulDay: GmtJulDay): Pair<Hexagram, Pair<GmtJulDay, GmtJulDay>> {
    val lng = starPosImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC).lng
    // 冬至點為起點 , 計算太陽領先冬至點 幾度
    val aheadDegrees = (lng - 270).let {
      if (it < 0)
        it + 360
      else
        it
    }

    val steps = (aheadDegrees / GAP).toInt()

    val hexStartDegree = (270 + steps * GAP).let { if (it >= 360) it - 360 else it }.toZodiacDegree()
    val hexEndDegree = (270 + (steps + 1) * GAP).let { if (it >= 360) it - 360 else it }.toZodiacDegree()

    val hexStart = starTransitImpl.getNextTransitGmt(Planet.SUN, hexStartDegree, gmtJulDay, false, Coordinate.ECLIPTIC)
    val hexEnd = starTransitImpl.getNextTransitGmt(Planet.SUN, hexEndDegree, gmtJulDay, true, Coordinate.ECLIPTIC)


    val hex = Hexagram.復.next(steps)

    return hex to (hexStart to hexEnd)

  }

  /**
   * 取得某時刻之後 (或之前)，出現此卦的 時間點範圍
   * @param forward true : 順查 , false : 逆查
   */
  override fun getDutyDays(hexagram: IHexagram, gmtJulDay: GmtJulDay, forward: Boolean): Pair<GmtJulDay, GmtJulDay> {

    // 現在是什麼卦 , 以及此卦的起迄時刻
    val (hex, timeRange: Pair<GmtJulDay, GmtJulDay>) = getHexagram(gmtJulDay)
    val degreeRange = getDegreeRange(hex)

    val targetHex = Hexagram.of(hexagram)


    val steps = if (forward) {
      targetHex.aheadOf(hex)
    } else {
      hex.aheadOf(targetHex)
    }


    logger.trace("from {} to {} , 共 {} steps", hex, targetHex, steps)
    logger.trace("{} range = {}", hex, timeRange)

    return if (forward) {
      val startDeg = (degreeRange.first + steps * GAP).let { if (it >= 360) (it - 360) else it }.toZodiacDegree()
      val endDeg = (degreeRange.first + (steps + 1) * GAP).let { if (it >= 360) (it - 360) else it }.toZodiacDegree()
      logger.trace("startDeg = {} , endDeg = {}" , startDeg , endDeg)
      val hexStart = starTransitImpl.getNextTransitGmt(Planet.SUN, startDeg, timeRange.first + 0.1, true, Coordinate.ECLIPTIC)
      val hexEnd = starTransitImpl.getNextTransitGmt(Planet.SUN, endDeg, timeRange.second + 0.1, true, Coordinate.ECLIPTIC)
      hexStart to hexEnd
    } else {
      // 逆推

      val startDeg = (degreeRange.first - steps * GAP).let { if (it < 0) it + 360 else it }.toZodiacDegree()
      val endDeg = (degreeRange.first + (steps - 1) * GAP).let { if (it < 0) it + 360 else it }.toZodiacDegree()
      val hexStart = starTransitImpl.getNextTransitGmt(Planet.SUN, startDeg, timeRange.first - 0.1, false, Coordinate.ECLIPTIC)
      val hexEnd = starTransitImpl.getNextTransitGmt(Planet.SUN, endDeg, timeRange.second - 0.1, false, Coordinate.ECLIPTIC)
      hexStart to hexEnd
    }
  }

  override fun getTitle(locale: Locale): String {
    return "先天卦氣"
  }

  override fun getDescription(locale: Locale): String {
    return "伏羲先天六十四卦天圓地方圖"
  }

  companion object {
    const val GAP: Double = 360 / 64.0 // 5.625

    val logger = KotlinLogging.logger { }

    /** 此 卦 , 在黃道帶上 , 的度數範圍 , 必定佔據一個 GAP */
    fun getDegreeRange(hex: Hexagram): Pair<Double, Double> {

      val steps = hex.aheadOf(Hexagram.復)

      val hexStartDegree = (270 + steps * GAP).let { if (it >= 360) it - 360 else it }
      val hexEndDegree = (270 + (steps + 1) * GAP).let { if (it >= 360) it - 360 else it }
      return hexStartDegree to hexEndDegree
    }

    // 從冬至 (270) 起，順推，每卦佔 45度
    val list = listOf(
      Symbol.震, Symbol.離, Symbol.兌, Symbol.乾,
      Symbol.巽, Symbol.坎, Symbol.艮, Symbol.坤)
  }

}
