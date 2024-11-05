/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.Axis.RISING
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.prediction.MidPointFocalAspect
import destiny.core.astrology.prediction.SynastryAspect
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.*
import jakarta.inject.Named
import kotlinx.serialization.Serializable



@Serializable
data class PersonHoroscopeConfig(
  override val horoscopeConfig: HoroscopeConfig = HoroscopeConfig(),
  override var gender: Gender = Gender.男,
  override var name: String? = null) : IPersonHoroscopeConfig, IHoroscopeConfig by horoscopeConfig

context(IHoroscopeConfig)
@DestinyMarker
class PersonHoroscopeConfigBuilder : Builder<PersonHoroscopeConfig> {
  var gender: Gender = Gender.男
  var name: String? = null

  override fun build(): PersonHoroscopeConfig {
    return PersonHoroscopeConfig(horoscopeConfig, gender, name)
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
    mode: SynastryMode = SynastryMode.BOTH_FULL
  ): SynastryModel {
    val innerPoints = modelInner.points.let { points ->
      when (mode) {
        SynastryMode.BOTH_FULL, SynastryMode.INNER_FULL_OUTER_DATE -> points
        else                                                       -> points.filter { it != Planet.MOON }
      }
    }.toList()

    val outerPoints = modelOuter.points.let { points ->
      when (mode) {
        SynastryMode.BOTH_FULL, SynastryMode.INNER_DATE_OUTER_FULL -> points
        else                                                       -> points.filter { it != Planet.MOON }
      }
    }.toList()

    val posMapOuter = modelOuter.positionMap
    val posMapInner = modelInner.positionMap

    val synastryAspects = outerPoints.asSequence().flatMap { pOuter -> innerPoints.asSequence().map { pInner -> pOuter to pInner } }
      .mapNotNull { (pOuter, pInner) ->
        aspectCalculator.getAspectPattern(pOuter, pInner, posMapOuter, posMapInner, { null }, { null }, aspects)
          ?.let { p: IPointAspectPattern ->
            val pOuterHouse = posMapOuter[pOuter]?.lng?.toZodiacDegree()?.let { zDeg -> modelInner.getHouse(zDeg) }
            val pInnerHouse = posMapInner[pInner]?.lng?.toZodiacDegree()?.let { zDeg -> modelInner.getHouse(zDeg) }
            SynastryAspect(pOuter, pInner, pOuterHouse, pInnerHouse, p.aspect, p.orb, null, p.score)
          }
      }.toSet()

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
        Triple(outerFocal, innerFocal, midpointAspectCalculator.getAspectPattern(outerFocal.focal, innerFocal.focal, posMapOuter, posMapInner, { null }, { null }, synastryMidpointAspects))
      }.filter { (_, _, p) -> p != null }
        .map { (outerFocal, innerFocal , pattern) ->
          MidPointFocalAspect(outerFocal, innerFocal, pattern!!.aspect, pattern.orb)
        }
      }.toSet()

    return SynastryModel(mode, modelInner, modelOuter, synastryAspects, midpointFocalAspects)
  }
}


@Named
class PersonHoroscopeFeature(private val horoscopeFeature: Feature<IHoroscopeConfig, IHoroscopeModel>) :
  AbstractCachedPersonFeature<IPersonHoroscopeConfig, IPersonHoroscopeModel>(), IPersonHoroscopeFeature {

  override val key: String = "personHoroscope"

  override val defaultConfig: IPersonHoroscopeConfig = PersonHoroscopeConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: IPersonHoroscopeConfig): IPersonHoroscopeModel {
    val horoscopeModel = horoscopeFeature.getModel(gmtJulDay, loc, config.horoscopeConfig)
    return PersonHoroscopeModel(horoscopeModel, gender, name)
  }
}
