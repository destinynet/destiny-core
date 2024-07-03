/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import com.github.benmanes.caffeine.cache.Caffeine
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.IVoidCourseFeature
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.prediction.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import destiny.tools.serializers.AstroPointSerializer
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.cache.Cache


@Serializable
data class HoroscopeConfig(
  override var points: Set<@Serializable(with = AstroPointSerializer::class) AstroPoint> = setOf(*Planet.values, *LunarNode.values, Axis.RISING, Axis.MERIDIAN),
  override var houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override var coordinate: Coordinate = Coordinate.ECLIPTIC,
  override var centric: Centric = Centric.GEO,
  override var temperature: Double = 0.0,
  override var pressure: Double = 1013.25,
  override var vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval,
  override var place: String? = null,
  override val relocations: Map<@Serializable(with = AstroPointSerializer::class) AstroPoint , Double> = emptyMap()
) : IHoroscopeConfig


@DestinyMarker
class HoroscopeConfigBuilder : Builder<HoroscopeConfig> {
  var points: Set<AstroPoint> = setOf(*Planet.values, *LunarNode.values)
  var houseSystem: HouseSystem = HouseSystem.PLACIDUS
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var centric: Centric = Centric.GEO
  var temperature: Double = 0.0
  var pressure: Double = 1013.25
  var vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval
  var place: String? = null
  var relocations: Map<AstroPoint , Double> = emptyMap()

  override fun build(): HoroscopeConfig {
    return HoroscopeConfig(points, houseSystem, coordinate, centric, temperature, pressure, vocImpl, place, relocations)
  }

  companion object {
    fun horoscope(block: HoroscopeConfigBuilder.() -> Unit = {}): HoroscopeConfig {
      return HoroscopeConfigBuilder().apply(block).build()
    }
  }
}

interface IHoroscopeFeature : Feature<IHoroscopeConfig, IHoroscopeModel> {

  /**
   * secondary progression calculation
   */
  fun getSecondaryProgression(
    model: IHoroscopeModel, progressionTime: GmtJulDay, aspects: Set<Aspect>,
    aspectsCalculator: IAspectsCalculator, config: IHoroscopeConfig, forward: Boolean = true
  ): IProgressionModel {
    val progression = ProgressionSecondary(forward)

    return getProgression(progression, model, progressionTime, aspects, aspectsCalculator, config)
  }

  /**
   * Tertiary Progression calculation
   */
  fun getTertiaryProgression(
    model: IHoroscopeModel, progressionTime: GmtJulDay, aspects: Set<Aspect>,
    aspectsCalculator: IAspectsCalculator, config: IHoroscopeConfig, converse: Boolean = false
  ): IProgressionModel {
    val progression = ProgressionTertiary(converse)

    return getProgression(progression, model, progressionTime, aspects, aspectsCalculator, config)
  }

  /**
   * Minor Progression calculation
   */
  fun getMinorProgression(
    model: IHoroscopeModel, progressionTime: GmtJulDay, aspects: Set<Aspect>,
    aspectsCalculator: IAspectsCalculator, config: IHoroscopeConfig, converse: Boolean = false
  ): IProgressionModel {
    val progression = ProgressionMinor(converse)

    return getProgression(progression, model, progressionTime, aspects, aspectsCalculator, config)
  }

  fun transit(
    model: IHoroscopeModel, transitTime: GmtJulDay, aspects: Set<Aspect>,
    aspectsCalculator: IAspectsCalculator, config: IHoroscopeConfig, forward: Boolean = true
  ): IProgressionModel {
    val progression = Transit(forward)
    return getProgression(progression, model, transitTime, aspects, aspectsCalculator, config)
  }

  fun getProgression(
    progression: AbstractProgression,
    model: IHoroscopeModel,
    progressionTime: GmtJulDay,
    aspects: Set<Aspect>,
    aspectsCalculator: IAspectsCalculator,
    config: IHoroscopeConfig
  ): IProgressionModel

}

data class ProgressionCalcObj(
  val convergentTime: GmtJulDay
)

@Named
class HoroscopeFeature(
  private val pointPosFuncMap: Map<AstroPoint, IPosition<*>>,
  private val houseCuspFeature: IHouseCuspFeature,
  private val voidCourseFeature: IVoidCourseFeature,
  private val planetHourFeature: Feature<PlanetaryHourConfig, PlanetaryHour?>,
  private val julDayResolver: JulDayResolver,
  @Transient
  private val horoscopeFeatureCache: Cache<GmtCacheKey<*>, IHoroscopeModel>
) : AbstractCachedFeature<IHoroscopeConfig, IHoroscopeModel>(), IHoroscopeFeature {
  override val key: String = "horoscope"

  override val defaultConfig: HoroscopeConfig = HoroscopeConfig()

  @Suppress("UNCHECKED_CAST")
  override val gmtCache: Cache<GmtCacheKey<IHoroscopeConfig>, IHoroscopeModel>
    get() = horoscopeFeatureCache as Cache<GmtCacheKey<IHoroscopeConfig>, IHoroscopeModel>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: IHoroscopeConfig): IHoroscopeModel {

    val positionMap: Map<AstroPoint, IPosWithAzimuth> = config.points.map { point ->
      point to pointPosFuncMap[point]?.getPosition(gmtJulDay, loc, config.centric, config.coordinate, config.temperature, config.pressure)
    }.filter { (_, v) -> v != null }
      .associate { (point, pos) ->
        point to (pos!! as IPosWithAzimuth).let {
          if (config.relocations.containsKey(point)) {
            val newLng = config.relocations[point]!!
            val newPos = Pos(newLng, it.lat)
            val az = Azimuth(it.azimuthDeg , it.trueAltitude, it.apparentAltitude)
            PosWithAzimuth(newPos, az)
          } else {
            it
          }
        }
      }


    // [1] 到 [12] 宮首黃道度數
    val cuspDegreeMap: Map<Int, ZodiacDegree> = houseCuspFeature.getModel(gmtJulDay, loc, HouseConfig(config.houseSystem, config.coordinate))

    // 行星空亡表
    val vocMap: Map<Planet, Misc.VoidCourseSpan> = voidCourseFeature.getVocMap(gmtJulDay, loc, config.points, VoidCourseConfig(vocImpl = config.vocImpl))

    // 行星時 Planetary Hour
    val planetaryHour =
      planetHourFeature.getModel(gmtJulDay, loc, PlanetaryHourConfig(PlanetaryHourType.ASTRO, TransConfig(temperature = config.temperature, pressure = config.pressure)))

    return HoroscopeModel(gmtJulDay, loc, config, positionMap, cuspDegreeMap, vocMap, planetaryHour)
  }

  override fun getProgression(
    progression: AbstractProgression,
    model: IHoroscopeModel,
    progressionTime: GmtJulDay,
    aspects: Set<Aspect>,
    aspectsCalculator: IAspectsCalculator,
    config: IHoroscopeConfig
  ): IProgressionModel {

    val convergentTime = progression.getConvergentTime(model.gmtJulDay, progressionTime)
    logger.info { "convergentGmt = ${julDayResolver.getLocalDateTime(convergentTime)}" }
    val param = ProgressionCalcObj(progressionTime)

    fun performOperation(param: ProgressionCalcObj): CompletableFuture<IProgressionModel> {
      return progressionCache.get(param) {
        logger.info { "cache missed , calculating... param.hashCode = ${param.hashCode()}" }

        val convergentModel = getModel(param.convergentTime, model.location, config)

        // inner : natal chart
        val posMapInner = model.positionMap
        // outer : progression chart
        val posMapOuter = convergentModel.positionMap

        // 2.4 hours later
        val later = progressionTime.plus(0.1)

        CompletableFuture.supplyAsync {
          progression.getConvergentTime(model.gmtJulDay, later).let { laterConvergentTime ->
            val laterModel = getModel(laterConvergentTime, model.location, config)
            val posMapLater = laterModel.positionMap


            val progressedAspects = config.points.asSequence().flatMap { p1 -> config.points.asSequence().map { p2 -> p1 to p2 } }
              .mapNotNull { (p1, p2) ->
                aspectsCalculator.getAspectPatterns(p1, p2, posMapOuter, posMapInner, { posMapLater[p1] }, { posMapInner[p2] }, aspects)
                  ?.let { p: IPointAspectPattern ->
                    val p1House = model.getHouse(posMapOuter[p1]!!.lng.toZodiacDegree())
                    val p2House = model.getHouse(posMapInner[p2]!!.lng.toZodiacDegree())
                    ProgressedAspect(p1, p2, p1House, p2House, p.aspect, p.orb, p.type!!, p.score)
                  }
              }.toSet()

            ProgressionModel(progression.type, model.gmtJulDay, progressionTime, param.convergentTime, progressedAspects)
          }
        }
      }
    }
    return performOperation(param).get()
  }

  companion object {
    private val progressionCache: com.github.benmanes.caffeine.cache.Cache<ProgressionCalcObj, CompletableFuture<IProgressionModel>> = Caffeine.newBuilder()
      .maximumSize(10000)
      .expireAfterWrite(1, TimeUnit.DAYS)
      .build()

    const val CACHE_HOROSCOPE_FEATURE = "horoscopeFeatureCache"
    private val logger = KotlinLogging.logger { }
  }
}
