/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.SynastryGrain
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.Axis.RISING
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.*
import jakarta.inject.Named
import kotlinx.serialization.Serializable


@Serializable
data class PersonHoroscopeConfig(
  // 暫時只有此 horoscopeConfig , 未來再依情形添加其他可能的設定
  override val horoscopeConfig: HoroscopeConfig = HoroscopeConfig(),
) : IPersonHoroscopeConfig, IHoroscopeConfig by horoscopeConfig

context(IHoroscopeConfig)
@DestinyMarker
class PersonHoroscopeConfigBuilder : Builder<PersonHoroscopeConfig> {

  override fun build(): PersonHoroscopeConfig {
    return PersonHoroscopeConfig(horoscopeConfig)
  }

  companion object {
    context(IHoroscopeConfig)
    fun personHoroscope(block: PersonHoroscopeConfigBuilder.() -> Unit = {}): PersonHoroscopeConfig {
      return PersonHoroscopeConfigBuilder().apply(block).build()
    }
  }
}

interface IPersonHoroscopeFeature : PersonFeature<IPersonHoroscopeConfig, IPersonHoroscopeModel> {

  fun synastry(
    modelInner: IPersonHoroscopeModel, modelOuter: IPersonHoroscopeModel,
    aspectCalculator: IAspectCalculator,
    midpointAspectCalculator: IAspectCalculator,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet(),
    mode: SynastryGrain = SynastryGrain.BOTH_FULL
  ): SynastryHoroscope
}


@Named
class PersonHoroscopeFeature(
  private val horoscopeFeature: IHoroscopeFeature
) :
  AbstractCachedPersonFeature<IPersonHoroscopeConfig, IPersonHoroscopeModel>(), IPersonHoroscopeFeature {

  override val key: String = "personHoroscope"

  override val defaultConfig: IPersonHoroscopeConfig = PersonHoroscopeConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: IPersonHoroscopeConfig): IPersonHoroscopeModel {

    val newConfig = config.horoscopeConfig.copy(place = place)

    val horoscopeModel: IHoroscopeModel = horoscopeFeature.getModel(gmtJulDay, loc, newConfig)
    return PersonHoroscopeModel(horoscopeModel, gender, name)
  }

  override fun synastry(
    modelInner: IPersonHoroscopeModel,
    modelOuter: IPersonHoroscopeModel,
    aspectCalculator: IAspectCalculator,
    midpointAspectCalculator: IAspectCalculator,
    aspects: Set<Aspect>,
    mode: SynastryGrain
  ): SynastryHoroscope {
    val innerPoints = modelInner.points.let { points ->
      when (mode) {
        SynastryGrain.BOTH_FULL, SynastryGrain.INNER_FULL_OUTER_DATE -> points
        else                                                         -> points.filter { it != Planet.MOON }
      }
    }.toList()

    val outerPoints: List<AstroPoint> = modelOuter.points.let { points ->
      when (mode) {
        SynastryGrain.BOTH_FULL, SynastryGrain.INNER_DATE_OUTER_FULL -> points
        else                                                         -> points.filter { it != Planet.MOON }
      }
    }.toList()

    val posMapOuter = modelOuter.positionMap
    val posMapInner = modelInner.positionMap

    val synastryAspects: List<SynastryAspect> = horoscopeFeature.synastry(modelOuter, modelInner, aspectCalculator, null, aspects)
      .aspects
      .filter { aspect ->
        outerPoints.contains(aspect.outerPoint) && innerPoints.contains(aspect.outerPoint)
      }.toList()

    // 中點
    val midPointFocals = buildSet {
      addAll(Planet.values)
      add(RISING)
      add(Axis.MERIDIAN)
      add(LunarNode.NORTH_TRUE)
    }
    val midPointOrb = 2.0

    val synastryMidpointAspects = setOf(CONJUNCTION, SEMISQUARE, SQUARE, SESQUIQUADRATE, OPPOSITION)
    val midpointFocalAspects = modelOuter.getMidPointsWithFocal(midPointFocals, midPointOrb).flatMap { outerFocal: IMidPointWithFocal ->
      modelInner.getMidPointsWithFocal(midPointFocals, midPointOrb).map { innerFocal: IMidPointWithFocal ->
        Triple(
          outerFocal,
          innerFocal,
          midpointAspectCalculator.getAspectPattern(outerFocal.focal, innerFocal.focal, posMapOuter, posMapInner, { null }, { null }, synastryMidpointAspects)
        )
      }.filter { (_, _, p) -> p != null }
        .map { (outerFocal, innerFocal, pattern) ->
          MidPointFocalAspect(outerFocal, innerFocal, pattern!!.aspect, pattern.orb)
        }
    }.toSet()

    return SynastryHoroscope(mode, modelInner, modelOuter, synastryAspects, midpointFocalAspects)
  }


}
