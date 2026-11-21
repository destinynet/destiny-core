/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import kotlin.time.Duration.Companion.days
import destiny.tools.MemoCaches
import destiny.tools.KvCache
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
import kotlinx.serialization.Serializable
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
  override val relocations: Map<AstroPoint, Double> = emptyMap(),
  override val starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN
) : IHoroscopeConfig

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
   *
   * 每張盤的 Axis (ASC/MC) 是否參與，由「**那張盤自己的**時刻精度」決定，與另一張盤無關 ——
   * 例如本命無時辰、但事件時刻精確已知時，行運盤（外盤）的 ASC/MC 仍然有效；
   * 反之若事件時刻是捏造的正午，即使本命時辰精確，行運 ASC/MC 也是假的。
   *
   * @param innerGrain 內盤自身的時刻精度；DAY 時內盤 Axis 不參與相位，且不計算宮位落入
   * @param outerGrain 外盤自身的時刻精度；DAY 時外盤 Axis 不參與相位
   */
  fun synastry(
    outer: IHoroscopeModel,
    inner: IHoroscopeModel,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    innerGrain: BirthDataGrain,
    outerGrain: BirthDataGrain,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH),
    laterForOuter: ((AstroPoint) -> IZodiacDegree?)? = null,
    laterForInner: ((AstroPoint) -> IZodiacDegree?)? = null
  ): Synastry {
    val posMapOuter = if (outerGrain.includeAxis) outer.positionMap else outer.positionMap.filterKeys { it !is Axis }

    val synastryAspects: List<SynastryAspect> = if (innerGrain.includeAxis) {
      synastryAspectsFine(posMapOuter, inner, laterForOuter, laterForInner, aspectCalculator, threshold, aspects)
    } else {
      // 當 inner 不含 Axis 時，過濾掉 Axis 點，因為沒有精確出生時間時 Axis 沒有意義
      val filteredInnerMap = inner.positionMap.filterKeys { it !is Axis }
      synastryAspectsCoarse(posMapOuter, filteredInnerMap, laterForOuter, laterForInner, aspectCalculator, threshold, aspects)
    }

    val houseOverlayStars = outer.points.filter { it is Planet || it is FixedStar || it is LunarPoint }

    val houseOverlayMap = if (innerGrain.includeAxis) {
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
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH)
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
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH),
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

  /**
   * 太陽弧在 [viewTime] 走了幾度：收斂時刻的太陽領先本命太陽的度數（[forward] 為 false 時取負值）。
   * 太陽弧盤上每一點的黃經 ＝ 本命黃經加上這個度數（[getSolarArc] 的 [ISolarArcModel.degreeMoved] 就是它）；
   * 只要其中幾點的位置時用這個，不必排整張盤
   */
  fun IHoroscopeModel.getSolarArcDegree(viewTime: GmtJulDay, config: IHoroscopeConfig, forward: Boolean = true): Double


  fun IHoroscopeModel.getHarmonic(n : Int, aspectCalculator: IAspectCalculator) : Harmonic

}

private data class ProgressionCalcObj(
  val type: ProgressionType,
  val convergentTime: GmtJulDay,
  val forward: Boolean
)

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
  private val horoscopeFeatureCache: KvCache<GmtCacheKey<*>, IHoroscopeModel>,
) : AbstractCachedFeature<IHoroscopeConfig, IHoroscopeModel>(), IHoroscopeFeature {
  override val key: String = "horoscope"

  override val defaultConfig: HoroscopeConfig = HoroscopeConfig()

  @Suppress("UNCHECKED_CAST")
  override val gmtCache: KvCache<GmtCacheKey<IHoroscopeConfig>, IHoroscopeModel>
    get() = horoscopeFeatureCache as KvCache<GmtCacheKey<IHoroscopeConfig>, IHoroscopeModel>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: IHoroscopeConfig): IHoroscopeModel {
    val positionMap = positionsOf(gmtJulDay, loc, config)
    val cuspDegreeMap = cuspsOf(gmtJulDay, loc, config)

    // 行星空亡表
    val vocMap: Map<Planet, Misc.VoidCourseSpan> = try {
      voidCourseFeature.getVocMap(gmtJulDay, loc, config.points, VoidCourseConfig(vocImpl = config.vocImpl))
    } catch (e : Exception) {
      logger.error { "無法計算行星空亡表, gmtJulDay = $gmtJulDay , loc = $loc , configPoints = ${config.points} , e = $e , e.message = ${e.message}" }
      emptyMap()
    }

    // 星體逆行狀態
    val retrogradePhaseMap: Map<Star, RetrogradePhase> =
      config.points.asSequence().filter {
        it is Planet
          || (it is LunarNode && config.starTypeOptions.nodeType == NodeType.TRUE)
          || (it is LunarApsis && config.starTypeOptions.apsisType == MeanOscu.OSCU)
      }.map { it as Star }.map { star ->
        star to retrogradeImpl.getRetrogradePhase(star, gmtJulDay, starPositionImpl, starTransitImpl)
      }.filter { (_, v) -> v != null }.associate { (k, v) -> k to v!! }

    return HoroscopeModel(gmtJulDay, loc, config, positionMap, cuspDegreeMap, vocMap, planetaryHourOf(gmtJulDay, loc, config), retrogradePhaseMap, rulingHousesOf(cuspDegreeMap))
  }

  /**
   * 同一時刻、換一個地點的盤（換置盤）：位置（方位角、高度）、宮頭、行星時依新地點重算；
   * 行星空亡表與逆行狀態與觀測地無關（地心），沿用 [model] 的 —— 這兩項要往前後搜尋星體運動，是整張盤最貴的部分。
   * 不是地心（[Centric.GEO]）的盤就整張重算。
   */
  fun relocate(model: IHoroscopeModel, loc: ILocation): IHoroscopeModel {
    val src = model as? HoroscopeModel
    if (src == null || src.config.centric != Centric.GEO)
      return calculate(model.gmtJulDay, loc, (model as? HoroscopeModel)?.config ?: defaultConfig)
    val cuspDegreeMap = cuspsOf(src.gmtJulDay, loc, src.config)
    return src.copy(
      location = loc,
      positionMap = positionsOf(src.gmtJulDay, loc, src.config),
      cuspDegreeMap = cuspDegreeMap,
      planetaryHour = planetaryHourOf(src.gmtJulDay, loc, src.config),
      rulingHouseMap = rulingHousesOf(cuspDegreeMap),
    )
  }

  private fun positionsOf(gmtJulDay: GmtJulDay, loc: ILocation, config: IHoroscopeConfig): Map<AstroPoint, IPosWithAzimuth> =
    config.points.map { point ->
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
  private fun cuspsOf(gmtJulDay: GmtJulDay, loc: ILocation, config: IHoroscopeConfig): Map<Int, ZodiacDegree> =
    houseCuspFeature.getModel(gmtJulDay, loc, HouseConfig(config.houseSystem, config.coordinate))

  // 行星時 Planetary Hour
  private fun planetaryHourOf(gmtJulDay: GmtJulDay, loc: ILocation, config: IHoroscopeConfig) =
      planetHourFeature.getModel(gmtJulDay, loc, PlanetaryHourConfig(PlanetaryHourType.ASTRO, TransConfig(temperature = config.temperature, pressure = config.pressure)))

  private fun rulingHousesOf(cuspDegreeMap: Map<Int, ZodiacDegree>): Map<Planet, Set<RulingHouse>> {
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
    return rulingHouseMap
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
      return progressionCache.getOrPut(param) {
        logger.debug { "cache missed , calculating... param = $param" }

        val convergentModel = getModel(param.convergentTime, model.location, config)

        // inner : natal chart
        val posMapInner = model.positionMap
        // outer : progression chart
        // 推運（非行運）不取收斂盤的軸點：收斂時刻差一天 = 一年（三推是一月），
        // 收斂盤的 ASC/MC 隨地球自轉一年轉一圈（即 Solar Fire 的 "Mean Quotidian"），不是一般意義的推運軸點。
        // Lots 由 ASC 導出，同理排除。本命側（inner）的軸點仍是有效的觸發標的。
        val posMapOuter = if (param.type == ProgressionType.TRANSIT)
          convergentModel.positionMap
        else
          convergentModel.positionMap.filterKeys { it !is Axis && it !is Lot }

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

          val progressedAspects = config.points.asSequence().filter { it in posMapOuter }.flatMap { p1 -> config.points.asSequence().map { p2 -> p1 to p2 } }
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

    require(viewTime >= this.gmtJulDay) { "viewTime ($viewTime) should be after model.gmtJulDay (${this.gmtJulDay})" }

    // 使用 Secondary Progression 來獲得精確的 convergent time
    val convergentJulDay = ProgressionSecondary(forward).getConvergentTime(this.gmtJulDay, viewTime)

    val degreeMoved: Double = getSolarArcDegree(viewTime, config, forward)

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

    return SolarArcModel(this.gmtJulDay, innerGrain, viewTime,
                         forward,
                         convergentJulDay, degreeMoved,
                         this.location, posMap, synastryAspects)
  }

  override fun IHoroscopeModel.getSolarArcDegree(viewTime: GmtJulDay, config: IHoroscopeConfig, forward: Boolean): Double {
    require(viewTime >= this.gmtJulDay) { "viewTime ($viewTime) should be after model.gmtJulDay (${this.gmtJulDay})" }

    // 使用 Secondary Progression 來獲得精確的 convergent time
    val convergentJulDay = ProgressionSecondary(forward).getConvergentTime(this.gmtJulDay, viewTime)

    // 計算精確的太陽弧度數
    val convergentSunPos: IStarPos = starPositionImpl.calculate(
      Planet.SUN, convergentJulDay, config.centric, config.coordinate, config.starTypeOptions
    )
    val natalSunPos = this.getPosition(Planet.SUN)!!

    return convergentSunPos.lngDeg.aheadOf(natalSunPos.lngDeg).let {
      if (!forward) -it else it
    }
  }

  override fun IHoroscopeModel.getHarmonic(n: Int, aspectCalculator: IAspectCalculator): Harmonic {
    require(n > 0) { "Harmonic number 'n' must be a positive integer." }

    // 計算新的泛音盤星體位置
    val harmonicStarPosMap = this.positionMap
      .filterKeys { it is Planet || it is Axis }
      .mapValues { (_, pos) -> (pos.zDeg * n).toZodiacDegree() }

    // 在新的泛音盤星體位置上，計算行星之間的相位
    val highImportanceAspects = Aspect.getAspects(Importance.HIGH)

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
    private val progressionCache: KvCache<ProgressionCalcObj, IProgressionModel> =
      MemoCaches.lru(maxSize = 10000, expireAfterWrite = 1.days)

    const val CACHE_HOROSCOPE_FEATURE = "horoscopeFeatureCache"
    private val logger = KotlinLogging.logger { }
  }
}
