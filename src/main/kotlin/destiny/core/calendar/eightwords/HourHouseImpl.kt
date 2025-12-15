/**
 * Created by smallufo on 2019-05-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.chinese.Branch
import destiny.tools.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 以地盤每宮的宮首 (house cusp) 當作時辰的中間點
 * 第一宮 宮首 必為卯正 (但不一定是 ASC !!!)
 * 第七宮 宮首 必為酉正 (但不一定是 DES !!!)
 * 以上兩宮，若採用 [HouseSystem.MERIDIAN] , ASC / DES 可能不在 1/7 宮
 *
 * 至於...
 *
 * 第十宮 宮首 必為午正
 * 第四宮 宮首 必為子正
 *
 * 所以，計算方法，黃道上任一點，找出最近的 House Cusp , 就可得知其時辰
 */
class HourHouseImpl(val houseCuspImpl: IHouseCusp,
                    val starPositionImpl: IStarPosition<*>,
                    val star: Star = Planet.SUN,
                    val houseSystem: HouseSystem = HouseSystem.MERIDIAN) : IHour, Serializable {

  override fun getHour(gmtJulDay: GmtJulDay, loc: ILocation, config: IHourBranchConfig): Branch {

    val lng = starPositionImpl.calculate(star, gmtJulDay, loc, Centric.GEO, Coordinate.ECLIPTIC, StarTypeOptions.DEFAULT).lngDeg
    logger.trace("lng = {}", lng)

    val houseMap = houseCuspImpl.getHouseCuspMap(gmtJulDay, loc, houseSystem, Coordinate.ECLIPTIC)
    val houseIndex = findNearestHouseCusp(houseMap, lng)

    logger.trace("nearest house = {}", houseIndex)
    return houseToBranch(houseIndex)
  }

  /**
   * 取得「下一個」此地支的開始時刻
   */
  override fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, config: IHourBranchConfig): GmtJulDay {
    TODO("not implemented")
  }

  /**
   * 取得「前一個」此地支的開始時刻
   */
  override fun getGmtPrevStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, config: IHourBranchConfig): GmtJulDay {
    TODO("not implemented")
  }


  override fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>,
                                  loc: ILocation,
                                  next: Boolean,
                                  julDayResolver: JulDayResolver,
                                  config: HourBranchConfig): ChronoLocalDateTime<*> {
    TODO("Not yet implemented")
  }

//  override fun toString(locale: Locale): String {
//    return "占星 分宮法"
//  }
//
//  override fun getDescription(locale: Locale): String {
//    return "內定為 Meridian 分宮法，每宮位時間固定為 2恆星時"
//  }

  companion object {
    val logger = KotlinLogging.logger { }

    fun findNearestHouseCusp(houseMap: Map<Int, ZodiacDegree>, degree: ZodiacDegree): Int {
      return houseMap.map { (houseIndex, cuspDegree: ZodiacDegree) ->
        houseIndex to cuspDegree.getAngle(degree)
      }
        .minBy { it.second }.first
        // 只過濾 angle < 40 , 加快後面 sort 速度
        //.filter { (_, angle) -> angle < 40 }.minByOrNull { it.second }!!.first
    }

    fun houseToBranch(house: Int): Branch {
      return when (house) {
        1 -> Branch.卯
        2 -> Branch.寅

        3 -> Branch.丑
        4 -> Branch.子
        5 -> Branch.亥

        6 -> Branch.戌
        7 -> Branch.酉
        8 -> Branch.申

        9 -> Branch.未
        10 -> Branch.午
        11 -> Branch.巳

        12 -> Branch.辰
        else -> throw AssertionError("invalid house $house")
      }
    }
  }
}
