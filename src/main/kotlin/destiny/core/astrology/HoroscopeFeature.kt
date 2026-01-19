/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.collect.Sets
import destiny.core.DayNight
import destiny.core.Scale
import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.BirthDataGrain.MINUTE
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
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


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
  override val relocations: Map<AstroPoint, Double> = emptyMap(),
  override val starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN
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
  var starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN

  override fun build(): HoroscopeConfig {
    return HoroscopeConfig(points, houseSystem, coordinate, centric, temperature, pressure, vocImpl, place, relocations, starTypeOptions)
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
  fun IHoroscopeModel.getSecondaryProgression(
    progressionTime: GmtJulDay, aspects: Set<Aspect>,
    aspectCalculator: IAspectCalculator, config: IHoroscopeConfig, forward: Boolean = true
  ): IProgressionModel {
    val progression = ProgressionSecondary(forward)

    return getProgression(progression, this, progressionTime, aspects, aspectCalculator, config)
  }

  /**
   * Tertiary Progression calculation
   */
  fun IHoroscopeModel.getTertiaryProgression(
    progressionTime: GmtJulDay, aspects: Set<Aspect>,
    aspectCalculator: IAspectCalculator, config: IHoroscopeConfig, converse: Boolean = false
  ): IProgressionModel {
    val progression = ProgressionTertiary(converse)

    return getProgression(progression, this, progressionTime, aspects, aspectCalculator, config)
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

  /**
   * 計算兩個星盤之間的合盤相位與宮位落入
   * @param innerIncludeAxis 內盤是否包含 Axis 點 (ASC/MC)，通常由 grain.includeAxis 決定
   */
  fun synastry(
    outer: IHoroscopeModel,
    inner: IHoroscopeModel,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    innerIncludeAxis: Boolean,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet(),
    laterForOuter: ((AstroPoint) -> IZodiacDegree?)? = null,
    laterForInner: ((AstroPoint) -> IZodiacDegree?)? = null
  ): Synastry {
    val posMapOuter = outer.positionMap

    val synastryAspects: List<SynastryAspect> = if (innerIncludeAxis) {
      synastryAspectsFine(outer.positionMap, inner, laterForOuter, laterForInner, aspectCalculator, threshold, aspects)
    } else {
      // 當 inner 不含 Axis 時，過濾掉 Axis 點，因為沒有精確出生時間時 Axis 沒有意義
      val filteredInnerMap = inner.positionMap.filterKeys { it !is Axis }
      synastryAspectsCoarse(outer.positionMap, filteredInnerMap, laterForOuter, laterForInner, aspectCalculator, threshold, aspects)
    }

    val houseOverlayStars = outer.points.filter { it is Planet || it is FixedStar || it is LunarPoint }

    val houseOverlayMap = if (innerIncludeAxis) {
      houseOverlayStars.asSequence().mapNotNull { pOuter: AstroPoint ->
        posMapOuter[pOuter]?.lngDeg?.let { zDeg ->
          val pOuterHouse = inner.getHouse(zDeg)
          val degreeToCusp = inner.getCuspDegree(pOuterHouse).getAngle(zDeg)
          HouseOverlay(pOuter, pOuterHouse, degreeToCusp)
        }
      }
        .groupBy { it.innerHouse }
        .mapValues { (_: Int, overlays: List<HouseOverlay>) -> overlays.sortedBy { it.degreeToCusp } }
        .toMap()
    } else {
      emptyMap()
    }

    return Synastry(synastryAspects, houseOverlayMap)
  }

  /**
   * 適用於 inner 具備完整時辰的情形
   */
  fun synastryAspectsFine(
    outer : Map<AstroPoint, IZodiacDegree>,
    inner : IHoroscopeModel,
    laterForOuter: ((AstroPoint) -> IZodiacDegree?)?,
    laterForInner: ((AstroPoint) -> IZodiacDegree?)?,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()
  ): List<SynastryAspect> {
    return outer.keys.asSequence().flatMap { pOuter -> inner.positionMap.keys.asSequence().map { pInner -> pOuter to pInner } }
      .mapNotNull { (pOuter, pInner) ->
        aspectCalculator.getAspectPattern(pOuter, pInner, outer, inner.positionMap, laterForOuter, laterForInner, aspects)
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
   * 適用於 inner (maybe natal) 不具備時辰的情形 , 不具備 house 計算功能
   */
  fun synastryAspectsCoarse(
    outer: Map<AstroPoint, IZodiacDegree>,
    inner: Map<AstroPoint, IZodiacDegree>,
    laterForOuter: ((AstroPoint) -> IZodiacDegree?)?,
    laterForInner: ((AstroPoint) -> IZodiacDegree?)?,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet(),
  ): List<SynastryAspect> {
    return outer.keys.asSequence().flatMap { pOuter -> inner.keys.asSequence().map { pInner -> pOuter to pInner } }
      .mapNotNull { (pOuter, pInner) ->
        aspectCalculator.getAspectPattern(pOuter, pInner, outer, inner, laterForOuter, laterForInner, aspects)
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

  fun IHoroscopeModel.getSolarArc(
    viewTime: GmtJulDay,
    innerGrain: BirthDataGrain,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    config: IHoroscopeConfig,
    forward: Boolean = true
  ) : ISolarArcModel


  fun IHoroscopeModel.getHarmonic(n : Int, aspectCalculator: IAspectCalculator) : Harmonic

}

private data class ProgressionCalcObj(
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
      point to pointPosFuncMap[point]?.getPosition(gmtJulDay, loc, config.centric, config.coordinate, config.temperature, config.pressure, config.starTypeOptions)
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
    val vocMap: Map<Planet, Misc.VoidCourseSpan> = try {
      voidCourseFeature.getVocMap(gmtJulDay, loc, config.points, VoidCourseConfig(vocImpl = config.vocImpl))
    } catch (e : Exception) {
      logger.error { "無法計算行星空亡表, gmtJulDay = $gmtJulDay , loc = $loc , configPoints = ${config.points} , e = $e , e.message = ${e.message}" }
      emptyMap()
    }

    // 行星時 Planetary Hour
    val planetaryHour =
      planetHourFeature.getModel(gmtJulDay, loc, PlanetaryHourConfig(PlanetaryHourType.ASTRO, TransConfig(temperature = config.temperature, pressure = config.pressure)))

    // 星體逆行狀態
    val retrogradePhaseMap: Map<Star, RetrogradePhase> =
      config.points.asSequence().filter {
        it is Planet
          || (it is LunarNode && config.starTypeOptions.nodeType == NodeType.TRUE)
          || (it is LunarApsis && config.starTypeOptions.apsisType == MeanOscu.OSCU)
      }.map { it as Star }.map { star ->
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

          // laterForP1 計算外盤星體在未來的位置
          val laterForP1: (AstroPoint) -> IZodiacDegree? = { p -> posMapLater[p] }

          // laterForP2 應返回內盤星體「不變」的位置，以作為比較的基準點
          val laterForP2: (AstroPoint) -> IZodiacDegree? = { p -> posMapInner[p] }

          val progressedAspects = config.points.asSequence().flatMap { p1 -> config.points.asSequence().map { p2 -> p1 to p2 } }
            .mapNotNull { (p1, p2) ->
              aspectCalculator.getAspectPattern(p1, p2, posMapOuter, posMapInner, laterForP1, laterForP2, aspects)
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

  override fun IHoroscopeModel.getSolarArc(
    viewTime: GmtJulDay,
    innerGrain: BirthDataGrain,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    config: IHoroscopeConfig,
    forward: Boolean
  ): ISolarArcModel {

    require(viewTime >= this.gmtJulDay) { "viewTime should be after model.gmtJulDay" }

    // 使用 Secondary Progression 來獲得精確的 convergent time
    val progressionSecondary = ProgressionSecondary(forward)
    val convergentJulDay = progressionSecondary.getConvergentTime(this.gmtJulDay, viewTime)

    // 計算精確的太陽弧度數
    val convergentSunPos: IStarPos = starPositionImpl.calculate(
      Planet.SUN, convergentJulDay, config.centric, config.coordinate, config.starTypeOptions
    )
    val natalSunPos = this.getPosition(Planet.SUN)!!

    val degreeMoved: Double = convergentSunPos.lngDeg.aheadOf(natalSunPos.lngDeg).let {
      if (!forward) -it else it
    }

    val innerPosMap = this.positionMap.let {
      if (innerGrain == MINUTE)
        it
      else
        it.filter { (k, _) -> k !is Axis }
    }

    val posMap = innerPosMap.mapValues { (_, pos) ->
      pos.lngDeg + degreeMoved
    }

    logger.trace { "degreeMoved = $degreeMoved" }

    val later = if (forward)
      convergentJulDay + 0.01
    else
      convergentJulDay - 0.01


    val convergentAndLaterSunPos: IStarPos = starPositionImpl.calculate(Planet.SUN, later, config.centric, config.coordinate, config.starTypeOptions)
    // 計算 later 時間點相對於「本命太陽」的「完整弧角」
    val laterFullSunArc = convergentAndLaterSunPos.lngDeg - this.getPosition(Planet.SUN)!!.lngDeg
    val laterPosMap = innerPosMap.mapValues { (_, pos) ->
      pos.lngDeg + laterFullSunArc
    }

    val laterForP1: ((AstroPoint) -> IZodiacDegree?) = { p -> laterPosMap[p] }
    val laterForP2: ((AstroPoint) -> IZodiacDegree?) = { p -> this.getZodiacDegree(p) }

    val synastryAspects = if (innerGrain == MINUTE) {
      synastryAspectsFine(posMap, this, laterForP1, laterForP2, aspectCalculator, threshold)
    } else {
      synastryAspectsCoarse(posMap, innerPosMap, laterForP1, laterForP2, aspectCalculator, threshold)
    }

    return SolarArcModel(this.gmtJulDay, (innerGrain == MINUTE), viewTime,
                         forward,
                         convergentJulDay, degreeMoved,
                         this.location, posMap, synastryAspects)
  }

  override fun IHoroscopeModel.getHarmonic(n: Int, aspectCalculator: IAspectCalculator): Harmonic {
    require(n > 0) { "Harmonic number 'n' must be a positive integer." }

    // 計算新的泛音盤星體位置
    val harmonicStarPosMap = this.positionMap
      .filterKeys { it is Planet || it is Axis }
      .mapValues { (_, pos) -> (pos.zDeg * n).toZodiacDegree() }

    // 在新的泛音盤星體位置上，計算行星之間的相位
    val highImportanceAspects = Aspect.getAspects(Importance.HIGH).toSet()

    // 遍歷所有行星配對來尋找相位
    val aspects = Sets.combinations(harmonicStarPosMap.keys, 2)
      .asSequence()
      .mapNotNull { points ->
        val (p1, p2) = points.toList()
        aspectCalculator.getAspectPattern(p1, p2, harmonicStarPosMap, harmonicStarPosMap, null, null, highImportanceAspects)
      }
      .toList()

    return Harmonic(n, aspects, harmonicStarPosMap)
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
