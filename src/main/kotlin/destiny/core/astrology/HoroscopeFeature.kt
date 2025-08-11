/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import com.github.benmanes.caffeine.cache.Caffeine
import destiny.core.astrology.Aspect.Importance
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

    val synastryAspects: List<SynastryAspect> = synastryAspectsFine(
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
  fun synastryAspectsFine(
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
   * 適用於 inner (maybe natal) 不具備時辰的情形 , 不具備 house 計算功能
   */
  fun synastryAspectsCoarse(
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


  fun getFirdariaPeriods(model: IHoroscopeModel, gmtJulDay: GmtJulDay) : Pair<FirdariaMajorPeriod, FirdariaSubPeriod?> {
    require(gmtJulDay >= model.gmtJulDay) { "Query time must be at or after birth time." }

    val sunHouse = model.getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
    val diurnal = sunHouse in 7..12

    val fullMajorRulerSequence = getMajorRulers(diurnal)
    // 副運序列只包含七大行星，順序與主運序列一致
    val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()

    // 2. 建立一個無限循環的主星序列，以處理 > 75 歲的情況
    val infiniteRulerSequence = generateSequence(fullMajorRulerSequence) { it }.flatten()
    val yearDays = 365.25

    // 3. 使用 scan 產生一個主運時期的無限序列
    // scan 的累加器 (accumulator) 是一個 Pair，儲存 (剛產生出來的主運, 下一個主運的開始時間)
    val initialAccumulator: Pair<FirdariaMajorPeriod?, GmtJulDay> = null to model.gmtJulDay

    val majorPeriodSequence = infiniteRulerSequence.scan(initialAccumulator) { acc: Pair<FirdariaMajorPeriod?, GmtJulDay>, majorRuler: AstroPoint ->
      val currentMajorStartTime = acc.second
      val periodYears = majorRulerYearsMap.getValue(majorRuler)
      val periodDays = periodYears * yearDays
      val majorPeriodEndTime = currentMajorStartTime + periodDays

      // 計算副運
      val subPeriods = if (majorRuler is Planet) {
        val subPeriodDays = periodDays / 7.0
        val subRulerStartIndex = planetarySequence.indexOf(majorRuler)
        (0 until 7).map { i ->
          val subRuler = planetarySequence[(subRulerStartIndex + i) % 7]
          val subStartTime = currentMajorStartTime + (subPeriodDays * i)
          val subEndTime = subStartTime + subPeriodDays
          FirdariaSubPeriod(subRuler, subStartTime, subEndTime)
        }
      } else {
        emptyList()
      }

      val majorPeriod = FirdariaMajorPeriod(majorRuler, currentMajorStartTime, majorPeriodEndTime, subPeriods)
      majorPeriod to majorPeriodEndTime // 回傳新的 Pair 作為下一次的累加器
    }.drop(1) // 丟掉第一個由初始值產生的無用結果
      .map { it.first!! } // 只取 FirdariaMajorPeriod

    // 4. 從無限序列中找到第一個包含查詢時間點的主運
    // 區間是 [startTime, endTime)，所以用 < 判斷
    val foundMajorPeriod = majorPeriodSequence.first { gmtJulDay < it.endTime }

    // 5. 從找到的主運中，找出對應的副運
    val foundSubPeriod = foundMajorPeriod.subPeriods.find { gmtJulDay >= it.startTime && gmtJulDay < it.endTime }

    return foundMajorPeriod to foundSubPeriod
  }

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

  override fun getSolarArc(
    model: IHoroscopeModel,
    viewTime: GmtJulDay,
    innerConsiderHour: Boolean,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    config: IHoroscopeConfig,
    forward: Boolean
  ): ISolarArcModel {

    require(viewTime >= model.gmtJulDay) { "viewTime should be after model.gmtJulDay" }

    // 使用 Secondary Progression 來獲得精確的 convergent time
    val progressionSecondary = ProgressionSecondary(forward)
    val convergentJulDay = progressionSecondary.getConvergentTime(model.gmtJulDay, viewTime)

    // 計算精確的太陽弧度數
    val convergentSunPos: IStarPos = starPositionImpl.getPosition(
      Planet.SUN, convergentJulDay, model.location,
      config.centric, config.coordinate, config.temperature, config.pressure
    )
    val natalSunPos = model.getPosition(Planet.SUN)!!

    val degreeMoved: Double = convergentSunPos.lngDeg.aheadOf(natalSunPos.lngDeg).let {
      if (!forward) -it else it
    }

    val innerPosMap = model.positionMap.let {
      if (innerConsiderHour)
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


    val convergentAndLaterSunPos: IStarPos = starPositionImpl.getPosition(Planet.SUN, later, model.location, config.centric, config.coordinate, config.temperature, config.pressure)
    // 計算 later 時間點相對於「本命太陽」的「完整弧角」
    val laterFullSunArc = convergentAndLaterSunPos.lngDeg - model.getPosition(Planet.SUN)!!.lngDeg
    val laterPosMap = innerPosMap.mapValues { (_, pos) ->
      pos.lngDeg + laterFullSunArc
    }

    val laterForP1: ((AstroPoint) -> IZodiacDegree?) = { p -> laterPosMap[p] }
    val laterForP2: ((AstroPoint) -> IZodiacDegree?) = { p -> model.getZodiacDegree(p) }

    val synastryAspects = if (innerConsiderHour) {
      synastryAspectsFine(posMap, model, laterForP1, laterForP2, aspectCalculator, threshold)
    } else {
      synastryAspectsCoarse(posMap, innerPosMap, laterForP1, laterForP2, aspectCalculator, threshold)
    }

    return SolarArcModel(model.gmtJulDay, innerConsiderHour, viewTime,
                         forward,
                         convergentJulDay, degreeMoved,
                         model.location, posMap, synastryAspects)
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
