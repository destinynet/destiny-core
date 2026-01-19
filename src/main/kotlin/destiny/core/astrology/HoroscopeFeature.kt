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

  fun synastry(
    outer: IHoroscopeModel,
    inner: IHoroscopeModel,
    aspectCalculator: IAspectCalculator,
    threshold: Double?,
    innerIncludeHouse: Boolean,
    aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet(),
    laterForOuter: ((AstroPoint) -> IZodiacDegree?)? = null,
    laterForInner: ((AstroPoint) -> IZodiacDegree?)? = null
  ): Synastry {
    val posMapOuter = outer.positionMap

    val synastryAspects: List<SynastryAspect> = if (innerIncludeHouse) {
      synastryAspectsFine(outer.positionMap, inner, laterForOuter, laterForInner, aspectCalculator, threshold, aspects)
    } else {
      // 當 inner 不含宮位資訊時，過濾掉 Axis 點，因為沒有精確出生時間時 Axis 沒有意義
      val filteredInnerMap = inner.positionMap.filterKeys { it !is Axis }
      synastryAspectsCoarse(outer.positionMap, filteredInnerMap, laterForOuter, laterForInner, aspectCalculator, threshold, aspects)
    }

    val houseOverlayStars = outer.points.filter { it is Planet || it is FixedStar || it is LunarPoint }

    val houseOverlayMap = if (innerIncludeHouse) {
      houseOverlayStars.asSequence().map { pOuter: AstroPoint ->
        posMapOuter[pOuter]?.lngDeg?.let { zDeg ->
          val pOuterHouse = inner.getHouse(zDeg)
          val degreeToCusp = inner.getCuspDegree(pOuterHouse).getAngle(zDeg)
          HouseOverlay(pOuter, pOuterHouse, degreeToCusp)
        }
      }.filterNotNull()
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


  fun IHoroscopeModel.getFirdariaTimeline(years: Int) : FirdariaTimeline {
    require(years > 0) { "Years must be positive." }

    val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
    val diurnal = sunHouse in 7..12

    val fullMajorRulerSequence = getMajorRulers(diurnal)
    val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()

    val infiniteRulerSequence = generateSequence(fullMajorRulerSequence) { it }.flatten()
    val yearDays = 365.25

    // 展開所有主運直到指定年數
    val startGmt = gmtJulDay
    val endGmt = startGmt + years * yearDays

    val majorPeriods = infiniteRulerSequence
      .runningFold(startGmt to null as FirdariaMajorPeriod?) { (currentStart, _), majorRuler ->
        val periodYears = majorRulerYearsMap.getValue(majorRuler)
        val periodDays = periodYears * yearDays
        val majorEnd = currentStart + periodDays

        val subPeriods = if (majorRuler is Planet) {
          val subPeriodDays = periodDays / 7.0
          val subStartIndex = planetarySequence.indexOf(majorRuler)
          (0 until 7).map { i ->
            val subRuler = planetarySequence[(subStartIndex + i) % 7]
            val subStart = currentStart + i * subPeriodDays
            // 確保最後一個副運的結束時間與主運的結束時間精確吻合
            val subEnd = if (i == 6) majorEnd else subStart + subPeriodDays
            FirdariaSubPeriod(subRuler, subStart, subEnd)
          }
        } else emptyList()

        majorEnd to FirdariaMajorPeriod(majorRuler, currentStart, majorEnd, subPeriods)
      }
      .mapNotNull { it.second }
      .takeWhile { it.fromTime < endGmt }
      .toList()

    return FirdariaTimeline(diurnal, majorPeriods)
  }


  fun IHoroscopeModel.getFirdaria(gmtJulDay: GmtJulDay): Firdaria {
    val (majorPeriod, subPeriod) = getFirdariaPeriods(gmtJulDay)
    val period = subPeriod?:majorPeriod

    return Firdaria(majorPeriod.ruler, period.ruler, period.fromTime, period.toTime)
  }

  fun IHoroscopeModel.getFirdariaPeriods(gmtJulDay: GmtJulDay): Pair<FirdariaMajorPeriod, FirdariaSubPeriod?> {
    require(gmtJulDay >= this.gmtJulDay) { "Query time must be at or after birth time." }

    val sunHouse = this.getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
    val diurnal = sunHouse in 7..12

    val fullMajorRulerSequence = getMajorRulers(diurnal)
    // 副運序列只包含七大行星，順序與主運序列一致
    val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()

    // 2. 建立一個無限循環的主星序列，以處理 > 75 歲的情況
    val infiniteRulerSequence = generateSequence(fullMajorRulerSequence) { it }.flatten()
    val yearDays = 365.25

    // 3. 使用 scan 產生一個主運時期的無限序列
    // scan 的累加器 (accumulator) 是一個 Pair，儲存 (剛產生出來的主運, 下一個主運的開始時間)
    val initialAcc: Pair<FirdariaMajorPeriod?, GmtJulDay> = null to this.gmtJulDay

    val majorPeriodSequence = infiniteRulerSequence
      .scan(initialAcc) { acc, majorRuler ->
        val startTime = acc.second
        val periodYears = majorRulerYearsMap.getValue(majorRuler)
        val periodDays = periodYears * yearDays
        val endTime = startTime + periodDays

        val subPeriods = if (majorRuler is Planet) {
          val subPeriodDays = periodDays / 7.0
          val subStartIndex = planetarySequence.indexOf(majorRuler)
          (0 until 7).map { i ->
            val subStart = startTime + subPeriodDays * i
            val subEnd = if (i == 6) endTime // 保證最後一段對齊
            else subStart + subPeriodDays
            FirdariaSubPeriod(
              ruler = planetarySequence[(subStartIndex + i) % 7],
              fromTime = subStart,
              toTime = subEnd
            )
          }
        } else emptyList()

        FirdariaMajorPeriod(majorRuler, startTime, endTime, subPeriods) to endTime
      }
      .drop(1)
      .map { it.first!! }

    val foundMajor = majorPeriodSequence.first { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }
    val foundSub = foundMajor.subPeriods.find { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }

    return foundMajor to foundSub
  }

  /**
   * 高效計算指定時間範圍內的法達星限時段。
   * 此方法使用惰性計算，避免預先產生整個時間線，可以處理任意長度的時間範圍。
   * @param from 開始時間
   * @param to 結束時間
   * @return 一個包含所有與指定範圍重疊的 Firdaria 時段的列表。
   */
  fun IHoroscopeModel.getRangeFirdaria(from: GmtJulDay, to: GmtJulDay): List<Firdaria> {
    val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
    val diurnal = sunHouse in 7..12

    val fullMajorRulerSequence = getMajorRulers(diurnal)
    val planetarySequence = fullMajorRulerSequence.filterIsInstance<Planet>()
    val yearDays = 365.25

    // 建立一個無限、惰性的主運序列
    val majorPeriodSequence = generateSequence(fullMajorRulerSequence) { it }.flatten()
      .runningFold(
        // 初始累加器：一個在出生時間點結束的虛擬週期
        FirdariaMajorPeriod(Planet.SUN, this.gmtJulDay, this.gmtJulDay, emptyList())
      ) { previousPeriod, currentRuler ->
        val startTime = previousPeriod.toTime
        val periodYears = majorRulerYearsMap.getValue(currentRuler)
        val periodDays = periodYears * yearDays
        val endTime = startTime + periodDays

        val subPeriods = if (currentRuler is Planet) {
          val subPeriodDays = periodDays / 7.0
          val subStartIndex = planetarySequence.indexOf(currentRuler)
          (0 until 7).map { i ->
            val subRuler = planetarySequence[(subStartIndex + i) % 7]
            val subStart = startTime + i * subPeriodDays
            val subEnd = if (i == 6) endTime else subStart + subPeriodDays
            FirdariaSubPeriod(subRuler, subStart, subEnd)
          }
        } else emptyList()

        FirdariaMajorPeriod(currentRuler, startTime, endTime, subPeriods)
      }
      .drop(1) // 丟棄第一個虛擬的初始值

    // 從惰性序列中篩選出與 [from, to] 範圍重疊的時段
    return majorPeriodSequence
      .dropWhile { it.toTime < from } // 跳過所有在查詢範圍開始前就已結束的主運
      .takeWhile { it.fromTime < to } // 截取所有在查詢範圍結束前開始的主運
      .flatMap { firdariaPeriodOverlapping.invoke(it, from, to) }
      .toList()
  }



  /**
   * 根據指定的尺度，高效計算特定時刻的小限法狀態。
   * @param gmtJulDay 欲查詢的時刻。
   * @param scale 欲查詢的時間尺度（年、月等）。
   * @return 一個包含從 YEAR 到指定 scale 的所有小限法狀態的 Map。
   */
  fun IHoroscopeModel.getProfection(gmtJulDay: GmtJulDay, scale: Scale): Map<Scale, Profection> {
    require(gmtJulDay >= this.gmtJulDay) { "Query time must be at or after birth time." }
    require(this.getHouse(Axis.RISING) == 1) { "Annual Profection requires a chart with a valid Ascendant and house system." }

    val rulerImpl: IRuler = RulerPtolemyImpl
    val houseCuspSigns = (1..12).associateWith { houseNumber ->
      this.getCuspDegree(houseNumber).sign
    }
    val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
    val dayNight = if (sunHouse in 7..12) DayNight.DAY else DayNight.NIGHT

    val resultMap = mutableMapOf<Scale, Profection>()

    // --- 年度計算 ---
    val age = floor((gmtJulDay - this.gmtJulDay) / TROPICAL_YEAR_DAYS).toInt()
    val annualFromTime = this.gmtJulDay + (age * TROPICAL_YEAR_DAYS)
    val annualToTime = annualFromTime + TROPICAL_YEAR_DAYS

    val annualProfectedHouse = (age % 12) + 1
    val annualAscSign = houseCuspSigns.getValue(annualProfectedHouse)
    val annualLord = with(rulerImpl) {
      (annualAscSign.getRulerPoint(dayNight) ?: annualAscSign.getRulerPoint()) as Planet
    }
    resultMap[Scale.YEAR] = Profection(Scale.YEAR, annualLord, annualAscSign, annualProfectedHouse, annualFromTime, annualToTime)
    if (scale == Scale.YEAR) return resultMap

    // --- 月度計算 ---
    val daysIntoYear = gmtJulDay - annualFromTime
    val monthlyPeriodDuration = TROPICAL_YEAR_DAYS / 12.0
    val monthIndex = floor(daysIntoYear / monthlyPeriodDuration).toInt()
    resultMap[Scale.MONTH] = getMonthProfection(annualFromTime, annualProfectedHouse, monthIndex, houseCuspSigns, dayNight)
    if (scale == Scale.MONTH) return resultMap

    // --- 日度計算 ---
    val monthlyProfection = resultMap.getValue(Scale.MONTH)
    val daysIntoMonth = floor(gmtJulDay - monthlyProfection.fromTime).toInt()
    resultMap[Scale.DAY] = getDayProfection(monthlyProfection.fromTime, monthlyProfection.house, daysIntoMonth, houseCuspSigns, dayNight)
    if (scale == Scale.DAY) return resultMap

    // --- 其他尺度 (待辦) ---
    when (scale) {
      Scale.HOUR -> TODO("Hourly profection calculation is not yet implemented.")
      else       -> return resultMap // Should not happen if logic is correct
    }
  }

  /**
   * 根據指定的尺度，高效計算時間範圍內的小限法時段列表。
   * @param fromTime 開始時間
   * @param toTime 結束時間
   * @param scale 欲查詢的時間尺度（年、月等）。
   * @return 一個只包含指定 scale 的 Profect 時段的列表。
   */
  fun IHoroscopeModel.getRangeProfections(fromTime: GmtJulDay, toTime: GmtJulDay, scale: Scale): List<Profection> {
    require(fromTime <= toTime) { "fromTime must be earlier than or equal to toTime." }
    require(this.getHouse(Axis.RISING) == 1) { "Annual Profection requires a chart with a valid Ascendant and house system." }

    val rulerImpl: IRuler = RulerPtolemyImpl
    val houseCuspSigns: Map<Int, ZodiacSign> = (1..12).associateWith { houseNumber ->
      this.getCuspDegree(houseNumber).sign
    }
    val sunHouse = getHouse(Planet.SUN) ?: throw IllegalStateException("Cannot determine sun's house.")
    val dayNight = if (sunHouse in 7..12) DayNight.DAY else DayNight.NIGHT

    // 計算查詢範圍開始時的約略年齡，並往前推一年以確保能捕捉到重疊的區間
    val startAge = max(0, floor((fromTime - this.gmtJulDay) / TROPICAL_YEAR_DAYS).toInt() - 1)

    // 建立一個從 startAge 開始的無限、惰性年齡序列
    val annualPeriodSequence: Sequence<Pair<Triple<GmtJulDay, Int, Planet>, ZodiacSign>> = generateSequence(startAge) { it + 1 }
      .map { age ->
        val annualFromTime = this.gmtJulDay + (age * TROPICAL_YEAR_DAYS)
        val annualProfectedHouse = (age % 12) + 1
        val annualAscSign = houseCuspSigns.getValue(annualProfectedHouse)
        val annualLord = with(rulerImpl) {
          (annualAscSign.getRulerPoint(dayNight) ?: annualAscSign.getRulerPoint()) as Planet
        }
        // 暫存年度資訊，以便月度計算使用
        Triple(annualFromTime, annualProfectedHouse, annualLord) to annualAscSign
      }
      .takeWhile { (annualInfo, _) ->
        val (annualFromTime, _, _) = annualInfo
        annualFromTime < toTime // 當年度週期的開始時間超出查詢範圍時，停止生成
      }

    // 從年度序列展開，產生與查詢範圍重疊的月度序列
    val monthPeriodSequence: Sequence<Profection> = annualPeriodSequence
      .flatMap { (annualInfo, _) ->
        val (annualFromTime, annualProfectedHouse, _) = annualInfo
        (0 until 12).asSequence().map { monthIndex ->
          getMonthProfection(annualFromTime, annualProfectedHouse, monthIndex, houseCuspSigns, dayNight)
        }
      }
      .filter { it.fromTime < toTime && fromTime < it.toTime } // 區間重疊檢查

    return when (scale) {
      Scale.YEAR  -> {
        annualPeriodSequence
          .filter { (annualInfo, _) ->
            val (annualFromTime, _, _) = annualInfo
            val annualToTime = annualFromTime + TROPICAL_YEAR_DAYS
            // 區間重疊檢查
            annualFromTime < toTime && fromTime < annualToTime
          }
          .map { (annualInfo, ascSign) ->
            val (annualFromTime, house, lord) = annualInfo
            Profection(Scale.YEAR, lord, ascSign, house, annualFromTime, annualFromTime + TROPICAL_YEAR_DAYS)
          }.toList()
      }

      Scale.MONTH -> { monthPeriodSequence.toList() }

      Scale.DAY -> {
        monthPeriodSequence
          .flatMap { monthlyProfection ->
            // 計算一個月大概有幾天，取整數，以產生足夠的日度小限
            val monthlyDurationDays = ceil(monthlyProfection.toTime - monthlyProfection.fromTime).toInt()
            // 產生該月份中每一天的序列
            (0 until monthlyDurationDays).asSequence().map { dayIndex ->
              getDayProfection(monthlyProfection.fromTime, monthlyProfection.house, dayIndex, houseCuspSigns, dayNight)
            }
          }
          .filter { it.fromTime < toTime && fromTime < it.toTime } // 再次過濾出與查詢範圍重疊的日期
          .toList()
      }
      Scale.HOUR  -> TODO("Hourly range profection calculation is not yet implemented.")
    }
  }

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
