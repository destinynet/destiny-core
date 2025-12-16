/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.SynastryGrain
import destiny.core.SynastryRelationship
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.Axis.RISING
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import jakarta.inject.Named
import kotlinx.serialization.Serializable


@Serializable
data class PersonHoroscopeConfig(
  // 暫時只有此 horoscopeConfig , 未來再依情形添加其他可能的設定
  override val horoscopeConfig: HoroscopeConfig = HoroscopeConfig(),
) : IPersonHoroscopeConfig, IHoroscopeConfig by horoscopeConfig

@DestinyMarker
class PersonHoroscopeConfigBuilder(val config : IHoroscopeConfig) : Builder<PersonHoroscopeConfig> {

  override fun build(): PersonHoroscopeConfig {
    return PersonHoroscopeConfig(config.horoscopeConfig)
  }

  companion object {
    context(config: IHoroscopeConfig)
    fun personHoroscope(block: PersonHoroscopeConfigBuilder.() -> Unit = {}): PersonHoroscopeConfig {
      return PersonHoroscopeConfigBuilder(config).apply(block).build()
    }
  }
}

interface IPersonHoroscopeFeature : PersonFeature<IPersonHoroscopeConfig, IPersonHoroscopeModel> {

  fun synastry(
    modelInner: IPersonHoroscopeModel,
    modelOuter: IPersonHoroscopeModel,
    relationship: SynastryRelationship?,
    aspectCalculator: IAspectCalculator,
    midpointAspectCalculator: IAspectCalculator,
    grain: SynastryGrain = SynastryGrain.BOTH_FULL,
    aspects: Set<Aspect> = Companion.getAspects(Importance.HIGH).toSet()
  ): SynastryRequestDto

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
    relationship: SynastryRelationship?,
    aspectCalculator: IAspectCalculator,
    midpointAspectCalculator: IAspectCalculator,
    grain: SynastryGrain,
    aspects: Set<Aspect>
  ): SynastryRequestDto {
    val innerPoints = modelInner.points.let { points ->
      when (grain) {
        SynastryGrain.BOTH_FULL, SynastryGrain.INNER_FULL_OUTER_DATE -> points
        else                                                         -> points.filter { it != Planet.MOON }
      }
    }.toList()

    val outerPoints: List<AstroPoint> = modelOuter.points.let { points ->
      when (grain) {
        SynastryGrain.BOTH_FULL, SynastryGrain.INNER_DATE_OUTER_FULL -> points
        else                                                         -> points.filter { it != Planet.MOON }
      }
    }.toList()

    val posMapOuter = modelOuter.positionMap
    val posMapInner = modelInner.positionMap

    val threshold = 0.9
    val innerIncludeHouse = when(grain) {
      SynastryGrain.BOTH_FULL -> true
      SynastryGrain.INNER_FULL_OUTER_DATE -> true
      SynastryGrain.INNER_DATE_OUTER_FULL -> false
      SynastryGrain.BOTH_DATE -> false
    }
    val synastry = horoscopeFeature.synastry(modelOuter, modelInner, aspectCalculator, threshold, innerIncludeHouse, aspects)

    val synastryAspects: List<SynastryAspect> = synastry.aspects
      .filter { aspect ->
        // 因為 outerPoint / innerPoint 可能因為 GRAIN 之故（只有日期沒有時間），不能考慮 MOON , 因此要確定，形成交角的星體，可能不能有 MOON
        outerPoints.contains(aspect.outerPoint) && innerPoints.contains(aspect.innerPoint)
      }

    // 中點
    val midPointFocals = buildSet {
      addAll(Planet.values)
      add(RISING)
      add(Axis.MERIDIAN)
      add(LunarNode.NORTH)
    }
    val midPointOrb = 2.0

    val synastryMidpointAspects = setOf(CONJUNCTION, SEMISQUARE, SQUARE, SESQUIQUADRATE, OPPOSITION)
    val midpointTrees: List<SynastryMidpointTree> = modelOuter.getMidPointsWithFocal(midPointFocals, midPointOrb).flatMap { outerFocal: IMidPointWithFocal ->
      modelInner.getMidPointsWithFocal(midPointFocals, midPointOrb).map { innerFocal: IMidPointWithFocal ->
        Triple(
          outerFocal,
          innerFocal,
          midpointAspectCalculator.getAspectPattern(outerFocal.focal, innerFocal.focal, posMapOuter, posMapInner, { null }, { null }, synastryMidpointAspects)
        )
      }.filter { (_, _, p) -> p != null }
        .map { (outerFocal: IMidPointWithFocal, innerFocal, pattern) ->
          MidPointFocalAspect(outerFocal as MidPointWithFocal , innerFocal as MidPointWithFocal, pattern!!.aspect, pattern.orb)
        }
    }.groupBy { Triple(it.inner.focal, it.outer.focal, it.aspect) }
      .asSequence()
      .map { (triple: Triple<AstroPoint, AstroPoint, Aspect>, aspects: List<MidPointFocalAspect>) ->
        SynastryMidpointTree(triple.first, triple.second, triple.third, aspects.first().orb, aspects)
      }
      .sortedBy { it.orb }
      .toList()

    return SynastryRequestDto(modelInner, modelOuter, grain, relationship, synastryAspects, midpointTrees, synastry.houseOverlayMap)
  }



}
