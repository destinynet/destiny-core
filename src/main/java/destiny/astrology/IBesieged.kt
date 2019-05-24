/**
 * Created by smallufo on 2017-02-16.
 */
package destiny.astrology

import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import java.time.chrono.ChronoLocalDateTime

/**
 * <pre>
 * 計算此顆星被哪兩顆星包夾
 * 這裡的「包夾」，是中性字眼。代表此星，先前與A星形成交角，後與B星形成交角
 * 也就是說，任何星在任何時候都處於「被包夾」狀態
 * 至於被包夾的好壞，要看星性以及更進一步的交角 Apply / Separate 決定
</pre> *
 */
interface IBesieged {

  /**
   * 最 Generalized 的介面
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmtJulDay GMT 時間
   * @param otherPlanets 其他行星
   * @param angles 欲計算的交角角度
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者 . 傳回的 List[Planet] 必定 size = 2
   * TODO : 目前的交角都只考慮「perfect」準確交角（一般行星三分容許度，日月17分），並未考慮容許度（即 applying），未來要改進
   */
  fun getBesiegingPlanetsByAngleDegrees(planet: Planet, gmtJulDay: Double,
                                        otherPlanets: Collection<Planet>,
                                        angles: Collection<Double>): Triple<List<Planet>, Aspect?, Aspect?>


  /**
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmtJulDay GMT 時間
   * @param otherPlanets 其他行星
   * @param searchingAspects 欲計算的交角
   * @return 兩顆行星 , 前者為「之前」形成交角者。後者為「之後」形成交角者 ,  傳回的 List[Planet] 必定 size = 2
   */
  fun getBesiegingPlanets(planet: Planet, gmtJulDay: Double,
                          otherPlanets: Collection<Planet>,
                          searchingAspects: Collection<Aspect>): Triple<List<Planet>, Aspect?, Aspect?> {
    val angles = searchingAspects.map { it.degree }
    return getBesiegingPlanetsByAngleDegrees(planet, gmtJulDay, otherPlanets, angles)
  }

  /**
   * 角度皆為 0/60/90/120/180
   * @param planet 計算此顆星 被哪兩顆行星 包夾
   * @param gmtJulDay GMT 時間
   * @param isClassical 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @return 第一顆星是「之前」形成交角的星 ; 第二顆星是「之後」會形成交角的星
   */
  fun getBesiegingPlanets(planet: Planet, gmtJulDay: Double, isClassical: Boolean): List<Planet> {

    val otherPlanets = getPlanetsExcept(planet, isClassical)

    val majorAspects = Aspect.getAngles(Aspect.Importance.HIGH)
    val mediumAspects = Aspect.getAngles(Aspect.Importance.MEDIUM)

    val searchingAspects = majorAspects.let {
      if (isClassical)
        it
      else {
        it + mediumAspects
      }
    }

    return getBesiegingPlanets(planet, gmtJulDay, otherPlanets, searchingAspects).first
  }

  // 承上 , gmt 版本
  fun getBesiegingPlanets(planet: Planet, gmt: ChronoLocalDateTime<*>, isClassical: Boolean): List<Planet> {
    return getBesiegingPlanets(planet, TimeTools.getGmtJulDay(gmt), isClassical)
  }


  fun getBesiegingPlanets(planet: Planet, gmt: ChronoLocalDateTime<*>,
                          otherPlanets: Collection<Planet>,
                          searchingAspects: Collection<Aspect>): Triple<List<Planet>, Aspect?, Aspect?> {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getBesiegingPlanets(planet, gmtJulDay, otherPlanets, searchingAspects)
  }


  /**
   * 傳回的 List[Planet] 必定 size = 2
   * @param classical 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   */
  fun getBesiegingPlanets(planet: Planet, gmtJulDay: Double, classical: Boolean, aspects: Collection<Aspect>): List<Planet> {
    val otherPlanets = getPlanetsExcept(planet, classical)
    return getBesiegingPlanets(planet, gmtJulDay, otherPlanets, aspects).first
  }

  // 承上 , gmt 版本
  fun getBesiegingPlanets(planet: Planet, gmt: ChronoLocalDateTime<*>, classical: Boolean, aspects: Collection<Aspect>): List<Planet> {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getBesiegingPlanets(planet, gmtJulDay, classical, aspects)
  }

  /**
   * @param planet 此 planet 是否被 p1 , p2 所包夾
   * @param classical 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @param isOnlyHardAspects 是否只計算 「艱難交角」 : 0/90/180 ; 如果「false」的話，連 60/120 也算進去
   * @return 是否被包夾
   */
  fun isBesieged(planet: Planet, p1: Planet, p2: Planet, gmt: ChronoLocalDateTime<*>, classical: Boolean, isOnlyHardAspects: Boolean): Boolean {

    val otherPlanets = getPlanetsExcept(planet, classical)

    val searchingAspects = Aspect.getAngles(Aspect.Importance.HIGH)

    val constrainingAspects = searchingAspects.filter {
      if (isOnlyHardAspects) {
        //只考量「硬」角度，所以 移除和諧的角度 (60/120)
        !arrayOf(Aspect.SEXTILE, Aspect.TRINE).contains(it)
      } else
        true
    }

    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    val (besiegingPlanets, aspectPrior, aspectAfter) = getBesiegingPlanets(planet, gmtJulDay, otherPlanets, searchingAspects)

    logger.debug("包夾 {} 的是 {}({}) 以及 {}({})", planet, besiegingPlanets[0], aspectPrior, besiegingPlanets[1], aspectAfter)
    return if (besiegingPlanets.contains(p1)
      && besiegingPlanets.contains(p2)
      && aspectPrior != null
      && aspectAfter != null) {
      constrainingAspects.contains(aspectPrior) && constrainingAspects.contains(aspectAfter)
    } else false
  }

  /**
   * @param planet  取得除了 planet 之外的其他行星
   * @param classical 是否只取得 classical 之星 (true = 過濾三王星)
   */
  private fun getPlanetsExcept(planet: Planet, classical: Boolean): List<Planet> {
    return Planet.array.filter { it !== planet }.filter {
      if (classical) {
        !arrayOf(Planet.URANUS, Planet.NEPTUNE, Planet.PLUTO).contains(it)
      } else
        true
    }
  }

  companion object {

    val logger = KotlinLogging.logger {  }
  }

}
