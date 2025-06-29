/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import com.github.benmanes.caffeine.cache.Caffeine
import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.Constants.TROPICAL_YEAR_DAYS
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.*
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.prediction.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.tools.*
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit
import javax.cache.Cache


@Serializable
data class HoroscopeConfig(
  override var points: Set<AstroPoint> = setOf(*Planet.values, *LunarNode.values, Axis.RISING, Axis.MERIDIAN),
  override var houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override var coordinate: Coordinate = Coordinate.ECLIPTIC,
  override var centric: Centric = Centric.GEO,
  override var temperature: Double = 0.0,
  override var pressure: Double = 1013.25,
  override var vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval,
  override var place: String? = null,
  override val relocations: Map<AstroPoint, Double> = emptyMap()
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
  var relocations: Map<AstroPoint, Double> = emptyMap()

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
    aspectCalculator: IAspectCalculator, config: IHoroscopeConfig, forward: Boolean = true
  ): IProgressionModel {
    val progression = ProgressionSecondary(forward)

    return getProgression(progression, model, progressionTime, aspects, aspectCalculator, config)
  }

  /**
   * Tertiary Progression calculation
   */
  fun getTertiaryProgression(
    model: IHoroscopeModel, progressionTime: GmtJulDay, aspects: Set<Aspect>,
    aspectCalculator: IAspectCalculator, config: IHoroscopeConfig, converse: Boolean = false
  ): IProgressionModel {
    val progression = ProgressionTertiary(converse)

    return getProgression(progression, model, progressionTime, aspects, aspectCalculator, config)
  }

  /**
   * Minor Progression calculation
   */
  fun getMinorProgression(
    model: IHoroscopeModel, progressionTime: GmtJulDay, aspects: Set<Aspect>,
    aspectCalculator: IAspectCalculator, config: IHoroscopeConfig, converse: Boolean = false
  ): IProgressionModel {
    val progression = ProgressionMinor(converse)

    return getProgression(progression, model, progressionTime, aspects, aspectCalculator, config)
  }

  fun transit(
    model: IHoroscopeModel, transitTime: GmtJulDay, aspects: Set<Aspect>,
    aspectCalculator: IAspectCalculator, config: IHoroscopeConfig, forward: Boolean = true
  ): IProgressionModel {
    val progression = Transit(forward)
    return getProgression(progression, model, transitTime, aspects, aspectCalculator, config)
  }

  fun getProgression(
    progression: AbstractProgression,
    model: IHoroscopeModel,
    progressionTime: GmtJulDay,
    aspects: Set<Aspect>,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig
  ): IProgressionModel

  fun synastry(
    outer: IHoroscopeModel,
    inner: IHoroscopeModel,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()
  ): Synastry {
    val posMapOuter = outer.positionMap

    val synastryAspects: List<SynastryAspect> = synastryAspects(
      outer.positionMap, inner,
      null, null,
      aspectCalculator, threshold, aspects)

    val houseOverlayStars = outer.points.filter { it is Planet || it is FixedStar || it is LunarPoint }
    val houseOverlayMap = houseOverlayStars.asSequence().map { pOuter: AstroPoint ->
      posMapOuter[pOuter]?.lngDeg?.let { zDeg ->
        val pOuterHouse = inner.getHouse(zDeg)
        val degreeToCusp = inner.getCuspDegree(pOuterHouse).getAngle(zDeg)
        HouseOverlay(pOuter, pOuterHouse,  degreeToCusp)
      }
    }.filterNotNull()
      .groupBy { it.innerHouse }
      .mapValues { (_: Int, overlays: List<HouseOverlay>) -> overlays.sortedBy { it.degreeToCusp } }
      .toMap()

    return Synastry(synastryAspects, houseOverlayMap)
  }

  /**
   * 適用於 inner 具備完整時辰的情形
   */
  fun synastryAspects(
    outer : Map<AstroPoint, IZodiacDegree>,
    inner : IHoroscopeModel,
    laterForP1: ((AstroPoint) -> IZodiacDegree?)?, laterForP2: ((AstroPoint) -> IZodiacDegree?)?,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()
  ): List<SynastryAspect> {
    return outer.keys.asSequence().flatMap { pOuter -> inner.positionMap.keys.asSequence().map { pInner -> pOuter to pInner } }
      .mapNotNull { (pOuter, pInner) ->
        aspectCalculator.getAspectPattern(pOuter, pInner, outer, inner.positionMap, laterForP1, laterForP2, aspects)
          ?.let { p: IPointAspectPattern ->
            outer[pOuter]?.zDeg?.toZodiacDegree()?.let { zDeg -> inner.getHouse(zDeg) }?.let { pOuterHouse ->
              inner.positionMap[pInner]?.lng?.toZodiacDegree()?.let { zDeg -> inner.getHouse(zDeg) }?.let { pInnerHouse ->
                SynastryAspect(pOuter, pInner, pOuterHouse, pInnerHouse, p.aspect, p.orb, p.aspectType, p.score)
              }
            }
          }
      }
      .filter {
        if (threshold != null) {
          it.score != null && it.score.value > threshold
        } else {
          true
        }
      }
      .sortedByDescending { it.score }
      .toList()
  }

  /**
   * 適用於 inner (maybe natal) 不具備時辰的情形
   */
  fun synastryAspects(
    outer: Map<AstroPoint, IZodiacDegree>,
    inner: Map<AstroPoint, IZodiacDegree>,
    laterForP1: ((AstroPoint) -> IZodiacDegree?)?, laterForP2: ((AstroPoint) -> IZodiacDegree?)?,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()
  ): List<SynastryAspect> {
    return outer.keys.asSequence().flatMap { pOuter -> inner.keys.asSequence().map { pInner -> pOuter to pInner } }
      .mapNotNull { (pOuter, pInner) ->
        aspectCalculator.getAspectPattern(pOuter, pInner, outer, inner, laterForP1, laterForP2, aspects)
          ?.let { p: IPointAspectPattern ->
            SynastryAspect(pOuter, pInner, null, null, p.aspect, p.orb, p.aspectType, p.score)
          }
      }
      .filter {
        if (threshold != null) {
          it.score != null && it.score.value > threshold
        } else {
          true
        }
      }
      .sortedByDescending { it.score }
      .toList()
  }

  fun getSolarArc(model: IHoroscopeModel, viewTime: GmtJulDay, innerConsiderHour: Boolean, aspectCalculator: IAspectCalculator, threshold: Double?, config: IHoroscopeConfig, forward: Boolean = true) : ISolarArcModel

  fun getPrimaryDirection(model: IHoroscopeModel, viewTime: GmtJulDay, timeKey: ITimeKey, aspectCalculator: IAspectCalculator, threshold: Double?, config: IHoroscopeConfig, forward: Boolean = true) : IPrimaryDirectionModel
}

data class ProgressionCalcObj(
  val type: ProgressionType,
  val convergentTime: GmtJulDay,
  val forward: Boolean
)

@Named
class HoroscopeFeature(
  private val pointPosFuncMap: Map<AstroPoint, IPosition<*>>,
  private val houseCuspFeature: IHouseCuspFeature,
  private val voidCourseFeature: IVoidCourseFeature,
  private val planetHourFeature: Feature<PlanetaryHourConfig, PlanetaryHour?>,
  private val julDayResolver: JulDayResolver,
  private val retrogradeImpl: IRetrograde,
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val primaryDirectionImpl: IPrimaryDirection,
  @Transient
  private val horoscopeFeatureCache: Cache<GmtCacheKey<*>, IHoroscopeModel>,
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
            val az = Azimuth(it.azimuthDeg, it.trueAltitude, it.apparentAltitude)
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

    // 星體逆行狀態
    val retrogradePhaseMap: Map<Star, RetrogradePhase> =
      config.points.asSequence().filter { it is Planet || it is LunarNode.NORTH_TRUE || it is LunarNode.SOUTH_TRUE }.map { it as Star }.map { star ->
        star to retrogradeImpl.getRetrogradePhase(star, gmtJulDay, starPositionImpl, starTransitImpl)
      }.filter { (_, v) -> v != null }.associate { (k, v) -> k to v!! }

    val rulerPtolemyImpl: IRuler = RulerPtolemyImpl
    val rulingHouseMap: Map<Planet, Set<RulingHouse>> = with(rulerPtolemyImpl) {
      cuspDegreeMap.map { (house, zodiacDeg: ZodiacDegree) ->
        val ruler = zodiacDeg.sign.getRulerPoint(null) as Planet?
        Triple(house, ruler, zodiacDeg)
      }.filter { (_, ruler, _) -> ruler != null }
        .map { (house, ruler, zodiacDeg) -> Triple(house, ruler!!, zodiacDeg) }
        .groupBy { triple -> triple.second }
        .mapValues { (_: Planet, v: List<Triple<Int, Planet, ZodiacDegree>>) -> v.map { triple -> RulingHouse(triple.first, triple.third.sign) }.toSet() }
        .toMap()
    }

    return HoroscopeModel(gmtJulDay, loc, config, positionMap, cuspDegreeMap, vocMap, planetaryHour, retrogradePhaseMap, rulingHouseMap)
  }

  override fun getProgression(
    progression: AbstractProgression,
    model: IHoroscopeModel,
    progressionTime: GmtJulDay,
    aspects: Set<Aspect>,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig
  ): IProgressionModel {

    val convergentTime = progression.getConvergentTime(model.gmtJulDay, progressionTime)
    logger.debug { "convergentGmt = ${julDayResolver.getLocalDateTime(convergentTime)}" }
    val param = ProgressionCalcObj(progression.type, convergentTime, progression.forward)

    fun performOperation(param: ProgressionCalcObj): IProgressionModel {
      return progressionCache.get(param) {
        logger.debug { "cache missed , calculating... param = $param" }

        val convergentModel = getModel(param.convergentTime, model.location, config)

        // inner : natal chart
        val posMapInner = model.positionMap
        // outer : progression chart
        val posMapOuter = convergentModel.positionMap

        // 2.4 hours later
        val later = progressionTime.plus(0.1)

        progression.getConvergentTime(model.gmtJulDay, later).let { laterConvergentTime ->
          logger.info { "laterConvergentTime = ${julDayResolver.getLocalDateTime(laterConvergentTime)}" }
          val laterModel = getModel(laterConvergentTime, model.location, config)
          val posMapLater = laterModel.positionMap

          val progressedAspects = config.points.asSequence().flatMap { p1 -> config.points.asSequence().map { p2 -> p1 to p2 } }
            .mapNotNull { (p1, p2) ->
              aspectCalculator.getAspectPattern(p1, p2, posMapOuter, posMapInner, { posMapLater[p1] }, { posMapInner[p2] }, aspects)
                ?.let { p: IPointAspectPattern ->
                  val p1House = model.getHouse(posMapOuter[p1]!!.lng.toZodiacDegree())
                  val p2House = model.getHouse(posMapInner[p2]!!.lng.toZodiacDegree())
                  SynastryAspect(p1, p2, p1House, p2House, p.aspect, p.orb, p.aspectType!!, p.score)
                }
            }
            .sortedByDescending { it.score }
            .toList()

          ProgressionModel(progression.type, model.gmtJulDay, progressionTime, convergentTime, progressedAspects)
        }
      }
    }
    return performOperation(param)
  }

  override fun getSolarArc(model: IHoroscopeModel, viewTime: GmtJulDay, innerConsiderHour: Boolean, aspectCalculator: IAspectCalculator,
                           threshold: Double?, config: IHoroscopeConfig, forward: Boolean): ISolarArcModel {

    require(viewTime >= model.gmtJulDay) { "viewTime should be after model.gmtJulDay" }

    val diffDays = (viewTime - model.gmtJulDay) / TROPICAL_YEAR_DAYS
    val convergentJulDay = if (forward)
      model.gmtJulDay + diffDays
    else
      model.gmtJulDay - diffDays


    val convergentSunPos: IStarPos = starPositionImpl.getPosition(Planet.SUN, convergentJulDay, model.location, config.centric, config.coordinate, config.temperature, config.pressure)

    val natalSunPos = model.getPosition(Planet.SUN)!!
    logger.trace { "natal sun = ${natalSunPos.lngDeg}" }
    logger.trace { "convergent sun = ${convergentSunPos.lngDeg}" }

    // may be negative (if forward = false)
    val degreeMoved: Double = convergentSunPos.lngDeg.getAngle(natalSunPos.lngDeg).let {
      if (!forward) -it else it
    }

    val posMap = model.positionMap.mapValues { (_, pos) ->
      pos.lngDeg + degreeMoved
    }

    logger.trace { "degreeMoved = $degreeMoved" }

    val later = if (forward)
      convergentJulDay + 0.01
    else
      convergentJulDay - 0.01


    val convergentAndLaterSunPos: IStarPos = starPositionImpl.getPosition(Planet.SUN, later, model.location, config.centric, config.coordinate, config.temperature, config.pressure)
    // 計算 later 時間點相對於「本命太陽」的「完整弧角」
    val laterFullSunArc = convergentAndLaterSunPos.lngDeg - model.getPosition(Planet.SUN)!!.lngDeg
    val laterPosMap = model.positionMap.mapValues { (_, pos) ->
      pos.lngDeg + laterFullSunArc
    }

    val laterForP1: ((AstroPoint) -> IZodiacDegree?) = { p -> laterPosMap[p] }
    val laterForP2: ((AstroPoint) -> IZodiacDegree?) = { p -> model.getZodiacDegree(p) }

    val synastryAspects = if (innerConsiderHour) {
      synastryAspects(posMap, model, laterForP1, laterForP2, aspectCalculator, threshold)
    } else {
      synastryAspects(posMap, model.positionMap, laterForP1, laterForP2, aspectCalculator, threshold)
    }

    return SolarArcModel(model.gmtJulDay, innerConsiderHour, viewTime,
                         forward,
                         convergentJulDay, degreeMoved,
                         model.location, posMap, synastryAspects)
  }


  override fun getPrimaryDirection(model: IHoroscopeModel, viewTime: GmtJulDay, timeKey: ITimeKey, aspectCalculator: IAspectCalculator, threshold: Double?, config: IHoroscopeConfig, forward: Boolean): IPrimaryDirectionModel {
    val diffYears = (viewTime - model.gmtJulDay) / TROPICAL_YEAR_DAYS
    val diffArcs = timeKey.getArc(diffYears)

    val posMap = primaryDirectionImpl.getDirectedPositions(model , diffArcs, HouseSystem.PLACIDUS)

//    val laterDiffYears = diffYears + 0.0001 // about 53 minutes
//    val laterDiffArcs = timeKey.getArc(laterDiffYears)
//    val laterPosMap = primaryDirectionImpl.getDirectedPositions(model, laterDiffArcs, HouseSystem.PLACIDUS)
//    val laterForP1: ((AstroPoint) -> IZodiacDegree?) = { p -> laterPosMap[p] }
//    val laterForP2: ((AstroPoint) -> IZodiacDegree?) = { p -> model.getZodiacDegree(p) }

    val synastryAspects = synastryAspects(
      posMap, model.positionMap,
      laterForP1 = null,
      laterForP2 = null,
      aspectCalculator, threshold
    )

    return PrimaryDirectionModel(model.gmtJulDay, viewTime, timeKey, diffArcs, posMap, synastryAspects)
  }

  companion object {
    private val progressionCache: com.github.benmanes.caffeine.cache.Cache<ProgressionCalcObj, IProgressionModel> = Caffeine.newBuilder()
      .maximumSize(10000)
      .expireAfterWrite(1, TimeUnit.DAYS)
      .build()

    const val CACHE_HOROSCOPE_FEATURE = "horoscopeFeatureCache"
    private val logger = KotlinLogging.logger { }
  }
}
