/**
 * Created by smallufo on 2026-11-18.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.Axis
import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.IHoroscopeFeature
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IRelativeTransit
import destiny.core.astrology.Lot
import destiny.core.astrology.LunarPhase
import destiny.core.astrology.Planet
import destiny.core.astrology.StarTypeOptions
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.lngOf
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.I18nBundles
import destiny.tools.ILocaleString
import destiny.tools.Lang

// ───────────────────────── 出生前的朔望 ─────────────────────────

/**
 * 出生前最近一次朔望（prenatal syzygy）：[phase] 是 [LunarPhase.NEW]（合朔）或 [LunarPhase.FULL]（望），發生在 [gmtJulDay]。
 * [degree] 是它在黃道上的位置；合朔時日月同度（[luminary] 為 null），望時取哪一顆光體由 [SyzygyOptions.fullMoon] 決定（[luminary]）
 */
data class PrenatalSyzygy(
  val phase: LunarPhase,
  val gmtJulDay: GmtJulDay,
  val degree: ZodiacDegree,
  val luminary: Planet?,
)

/**
 * 望（滿月）時取哪一個度數：
 * - [FullMoonDegree.ABOVE_HORIZON]：當時在出生地地平線上的那一顆光體（Ptolemy《四書》III.2；Ibn Ezra、Bonatti 沿用）
 * - [FullMoonDegree.MOON]：一律取月亮（部分現代軟體的簡化）
 */
data class SyzygyOptions(val fullMoon: FullMoonDegree = FullMoonDegree.ABOVE_HORIZON) {
  enum class FullMoonDegree { ABOVE_HORIZON, MOON }

  companion object {
    val PTOLEMY = SyzygyOptions()
  }
}

interface IPrenatalSyzygy {
  /** [gmtJulDay] 之前最近的一次合朔或望；[loc] 用來判斷望時哪一顆光體在地平線上 */
  fun getPrenatalSyzygy(gmtJulDay: GmtJulDay, loc: ILocation): PrenatalSyzygy?
}

/**
 * 以日月的交角時刻（[IRelativeTransit]：月亮對太陽 0° 與 180°，往回找）取最近的一次；
 * 望時另排一張當時、出生地的盤判斷地平線上下（與 [SectImpl] 同一個判法：從上升點順黃道 180–360° 是地平線上）
 */
class PrenatalSyzygyImpl(
  private val relativeTransitImpl: IRelativeTransit,
  private val horoscopeFeature: IHoroscopeFeature,
  val options: SyzygyOptions = SyzygyOptions.PTOLEMY,
) : IPrenatalSyzygy {

  override fun getPrenatalSyzygy(gmtJulDay: GmtJulDay, loc: ILocation): PrenatalSyzygy? {
    val (phase, t) = listOf(LunarPhase.NEW, LunarPhase.FULL)
      .mapNotNull { p -> relativeTransitImpl.getRelativeTransit(Planet.MOON, Planet.SUN, p.angle, gmtJulDay, false, StarTypeOptions.MEAN)?.let { p to it } }
      .maxByOrNull { (_, t) -> t } ?: return null
    val model = horoscopeFeature.getModel(t, loc, CONFIG)
    val sun = model.positionMap[Planet.SUN]?.lngDeg ?: return null
    val moon = model.positionMap[Planet.MOON]?.lngDeg ?: return null
    if (phase == LunarPhase.NEW) return PrenatalSyzygy(phase, t, moon, null)

    val luminary = when (options.fullMoon) {
      SyzygyOptions.FullMoonDegree.MOON          -> Planet.MOON
      SyzygyOptions.FullMoonDegree.ABOVE_HORIZON -> {
        val asc = model.positionMap[Axis.RISING]?.lngDeg ?: return null
        if (sun.aheadOf(asc) > 180.0) Planet.SUN else Planet.MOON
      }
    }
    return PrenatalSyzygy(phase, t, if (luminary === Planet.SUN) sun else moon, luminary)
  }

  companion object {
    private val CONFIG = HoroscopeConfig(points = setOf(Planet.SUN, Planet.MOON, Axis.RISING), houseSystem = HouseSystem.WHOLE_SIGN)
  }
}

// ───────────────────────── 全盤守護星（Almuten Figuris） ─────────────────────────

/** 計算全盤守護星時取分的點（Ibn Ezra 的五個「生命點」：日、月、上升、福點、出生前朔望） */
enum class AlmutenPoint : ILocaleString {
  SUN, MOON, ASCENDANT, FORTUNE, SYZYGY;

  override fun getTitle(lang: Lang): String =
    I18nBundles.string("destiny.core.astrology.classical.Classical", lang, "AlmutenPoint.$name") ?: name
}

/**
 * 全盤守護星的判準：
 * - [points]：取分的點
 * - [scheme]：界與三分的表（三分只取當令的那一顆，與 [DignityScheme.lordOf] 相同）
 * - [dignities]：計分的尊貴（分數取 [Dignity.score]）；落、陷不算
 */
data class AlmutenOptions(
  val points: List<AlmutenPoint> = AlmutenPoint.entries,
  val scheme: DignityScheme = DignityScheme.DEFAULT,
  val dignities: List<Dignity> = listOf(Dignity.RULER, Dignity.EXALTATION, Dignity.TRIPLICITY, Dignity.TERM, Dignity.FACE),
) {
  init {
    require(dignities.none { it.score < 0 }) { "落、陷不計入全盤守護星" }
  }

  companion object {
    /** Ibn Ezra《The Book of Nativities》：五個點、廟 5 旺 4 三分 3 界 2 面 1 */
    val IBN_EZRA = AlmutenOptions()
  }
}

/** 一個點的計分：[point] 在 [degree]，[awards] 是哪顆星在這裡拿到哪幾種尊貴（沒拿到的星不列） */
data class AlmutenPointScore(
  val point: AlmutenPoint,
  val degree: ZodiacDegree,
  val awards: Map<Planet, List<Dignity>>,
) {
  fun scoreOf(planet: Planet): Int = awards[planet].orEmpty().sumOf { it.score }
}

/**
 * 全盤守護星：[points] 是各點的計分，[totals] 是七顆星的總分（由高到低，同分依古典行星順序），
 * [winners] 是最高分的星（同分時不只一顆，由使用者自行判斷）；[syzygy] 是取分用的那一次朔望（沒算或算不出來為 null）
 */
data class AlmutenFiguris(
  val options: AlmutenOptions,
  val points: List<AlmutenPointScore>,
  val totals: List<Pair<Planet, Int>>,
  val syzygy: PrenatalSyzygy? = null,
) {
  val winners: List<Planet> get() = totals.firstOrNull()?.second?.let { top -> totals.filter { it.second == top }.map { it.first } }.orEmpty()
}

/** 由各點的度數計分（純函式；點的位置由呼叫端算好）。缺的點略過 */
fun AlmutenOptions.score(degrees: Map<AlmutenPoint, ZodiacDegree>, sect: DayNight, syzygy: PrenatalSyzygy? = null): AlmutenFiguris {
  val scores = points.mapNotNull { p ->
    degrees[p]?.let { deg ->
      val awards = dignities
        .mapNotNull { d -> (scheme.lordOf(d, deg, sect) as? Planet)?.let { it to d } }
        .groupBy({ it.first }, { it.second })
      AlmutenPointScore(p, deg, awards)
    }
  }
  val totals = Planet.classicalList
    .map { planet -> planet to scores.sumOf { it.scoreOf(planet) } }
    .sortedWith(compareByDescending<Pair<Planet, Int>> { it.second }.thenBy { Planet.classicalList.indexOf(it.first) })
  return AlmutenFiguris(this, scores, totals, syzygy)
}

interface IAlmuten {
  /** 全盤守護星；盤的日夜由呼叫端給（判不出日夜就不算：福點與三分都要日夜） */
  fun getAlmuten(h: IHoroscopeModel, sect: DayNight): AlmutenFiguris?
}

/** Ibn Ezra 一系：日、月、上升取盤上位置，福點依日夜（[Lot.Fortune]），朔望由 [syzygyImpl] 算 */
class AlmutenImpl(
  private val syzygyImpl: IPrenatalSyzygy,
  val options: AlmutenOptions = AlmutenOptions.IBN_EZRA,
) : IAlmuten {

  override fun getAlmuten(h: IHoroscopeModel, sect: DayNight): AlmutenFiguris? {
    val asc = h.positionMap[Axis.RISING]?.lngDeg ?: return null
    val syzygy = if (AlmutenPoint.SYZYGY in options.points) syzygyImpl.getPrenatalSyzygy(h.gmtJulDay, h.location) else null
    val degrees = options.points.mapNotNull { p ->
      when (p) {
        AlmutenPoint.SUN       -> h.positionMap[Planet.SUN]?.lngDeg
        AlmutenPoint.MOON      -> h.positionMap[Planet.MOON]?.lngDeg
        AlmutenPoint.ASCENDANT -> asc
        AlmutenPoint.FORTUNE   -> Lot.Fortune.lngOf(h.pointDegreeMap, sect)
        AlmutenPoint.SYZYGY    -> syzygy?.degree
      }?.let { p to it }
    }.toMap()
    return options.score(degrees, sect, syzygy)
  }
}
