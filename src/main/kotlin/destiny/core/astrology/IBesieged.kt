/**
 * Created by smallufo on 2017-02-16.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.TimeTools
import destiny.tools.KotlinLogging
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算此顆星被哪兩顆星包夾
 * 這裡的「包夾」，是中性字眼。代表此星，先前與A星形成交角，後與B星形成交角
 * 也就是說，任何星在任何時候都處於「被包夾」狀態
 * 至於被包夾的好壞，要看星性以及更進一步的交角 [IAspectData.Type.APPLYING] / [IAspectData.Type.SEPARATING] 決定
 */
interface IBesieged {

  /**
   * 最 Generalized 的介面
   * @param planet 計算此顆行星，被哪兩顆行星所夾角
   * @param gmtJulDay GMT 時間
   * @param otherPlanets 其他行星
   * @param angles 欲計算的交角角度
   * @return 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  fun getBesiegingPlanetsByDegrees(planet: Planet,
                                   gmtJulDay: GmtJulDay,
                                   otherPlanets: Set<Planet>,
                                   angles: Set<Double>): Pair<IAngleData? , IAngleData?>

  fun getBesiegingPlanetsByAspects(planet: Planet,
                                   gmtJulDay: GmtJulDay,
                                   otherPlanets: Set<Planet>,
                                   aspects: Set<Aspect>): Pair<IAspectData?, IAspectData?> {
    return getBesiegingPlanetsByDegrees(planet, gmtJulDay, otherPlanets, aspects.map { it.degree }.toSet()).let { (prior, after) ->
      prior?.toAspectData() to after?.toAspectData()
    }
  }


  /**
   * @param classicalPlanets 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @return 傳回的 List[Planet] 必定 size = 2
   */
  fun getBesiegingPlanets(planet: Planet,
                          gmtJulDay: GmtJulDay,
                          classicalPlanets: Boolean = true,
                          aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet()): List<Planet> {
    val otherPlanets = getPlanetsExcept(planet, classicalPlanets)

    return getBesiegingPlanetsByAspects(planet, gmtJulDay, otherPlanets, aspects).let { (prior, after) ->
      val p1 = prior?.points?.first { otherPlanets.contains(it) } as Planet
      val p2 = after?.points?.first { otherPlanets.contains(it) } as Planet
      listOf(p1, p2)
    }
  }

  // 承上 , gmt 版本
  fun getBesiegingPlanets(planet: Planet, gmt: ChronoLocalDateTime<*>, classicalPlanets: Boolean, aspects: Set<Aspect>): List<Planet> {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getBesiegingPlanets(planet, gmtJulDay, classicalPlanets, aspects)
  }

  /**
   * @param planet 此 planet 是否被 p1 , p2 所包夾
   * @param classicalPlanets 是否只計算古典占星學派。如果「是」的話，則不考慮三王星
   * @param onlyHardAspects 是否只計算 「艱難交角」 : 0/90/180 ; 如果「false」的話，連 60/120 也算進去
   * @return 是否被包夾
   */
  fun isBesieged(planet: Planet, p1: Planet, p2: Planet, gmtJulDay: GmtJulDay, classicalPlanets: Boolean, onlyHardAspects: Boolean): Boolean {

    val otherPlanets = getPlanetsExcept(planet, classicalPlanets)

    val searchingAspects = Aspect.getAspects(Aspect.Importance.HIGH).toSet()

    val constrainingAspects = searchingAspects.filter {
      if (onlyHardAspects) {
        //只考量「硬」角度，所以 移除和諧的角度 (60/120)
        !arrayOf(Aspect.SEXTILE, Aspect.TRINE).contains(it)
      } else
        true
    }

    val (besiegingPlanets, aspectPrior, aspectAfter) = getBesiegingPlanetsByAspects(planet , gmtJulDay , otherPlanets , searchingAspects).let { (prior , after) ->
      val planet1 = prior?.points?.first { otherPlanets.contains(it) } as Planet
      val planet2 = after?.points?.first { otherPlanets.contains(it) } as Planet
      Triple(listOf(planet1, planet2), prior.aspect, after.aspect)
    }

    logger.debug("包夾 {} 的是 {}({}) 以及 {}({})", planet, besiegingPlanets[0], aspectPrior, besiegingPlanets[1], aspectAfter)
    return if (besiegingPlanets.contains(p1) && besiegingPlanets.contains(p2)) {
      constrainingAspects.contains(aspectPrior) && constrainingAspects.contains(aspectAfter)
    } else false
  }

  /**
   * @param planet  取得除了 planet 之外的其他行星
   * @param classical 是否只取得 classical 之星 (true = 過濾三王星)
   */
  private fun getPlanetsExcept(planet: Planet, classical: Boolean): Set<Planet> {
    return Planet.values.filter { it !== planet }.filter {
      if (classical) {
        !arrayOf(Planet.URANUS, Planet.NEPTUNE, Planet.PLUTO).contains(it)
      } else
        true
    }.toSet()
  }

  companion object {

    val logger = KotlinLogging.logger {  }
  }

}
