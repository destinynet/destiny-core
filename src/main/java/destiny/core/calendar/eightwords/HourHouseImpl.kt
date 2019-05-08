/**
 * Created by smallufo on 2019-05-07.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.*
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import mu.KotlinLogging
import java.io.Serializable
import java.util.*

class HourHouseImpl(val houseCuspImpl : IHouseCusp,
                    val starPositionImpl: IStarPosition<*>,
                    val star : Star = Planet.SUN,
                    val houseSystem: HouseSystem = HouseSystem.MERIDIAN
                    ) : IHour, Serializable {

  override fun getHour(gmtJulDay: Double, location: ILocation): Branch {

    val lng = starPositionImpl.getPosition(star , gmtJulDay , location , Centric.GEO , Coordinate.ECLIPTIC).lng
    logger.trace("lng = {}" , lng)


    val house = houseCuspImpl.getHouseCusps(gmtJulDay , location , houseSystem , Coordinate.ECLIPTIC).drop(1).mapIndexed { index, d ->
      val houseIndex = index+1
      val angle = IHoroscopeModel.getAngle(lng , d)
      logger.trace("{} => {} , angle = {}" , houseIndex , d , angle)
      houseIndex to angle

    }.filter { (_ , angle) -> angle < 40 } // 只過濾 angle < 40 , 加快後面 sort 速度
      .sortedBy { (_ , angle) -> angle }
      .first().first

    logger.trace("nearest house = {}" , house)
    return when(house) {
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

  override fun getGmtNextStartOf(gmtJulDay: Double, location: ILocation, eb: Branch): Double {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getGmtPrevStartOf(gmtJulDay: Double, location: ILocation, eb: Branch): Double {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getTitle(locale: Locale): String {
    return "占星 分宮法"
  }

  override fun getDescription(locale: Locale): String {
    return "內定為 Meridian 分宮法，每宮位時間固定為 2恆星時"
  }

  companion object {
    val logger = KotlinLogging.logger {  }
  }
}