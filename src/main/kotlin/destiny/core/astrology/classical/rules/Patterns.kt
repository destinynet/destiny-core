/**
 * Created by smallufo on 2017-12-18.
 */
package destiny.core.astrology.classical.rules

import destiny.core.DayNight
import destiny.core.astrology.*
import destiny.core.astrology.classical.ApplicationOutcome
import destiny.core.astrology.classical.Besiegement
import destiny.core.astrology.classical.Dignity
import destiny.core.astrology.classical.MutualDataWithSign
import destiny.core.calendar.GmtJulDay
import destiny.core.chinese.YinYang
import destiny.core.toString
import destiny.tools.serializers.IZodiacDegreeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import destiny.tools.defaultLocale
import destiny.tools.Lang

/**
 * 行星的 25種狀態
 * https://site.douban.com/183595/widget/notes/192509582/note/600376742/
 */

sealed class EssentialDignity : IPlanetPattern {

  override val ruleType: RuleType = RuleType.ESSENTIAL

  data class Ruler(override val planet: Planet, val sign: ZodiacSign) : EssentialDignity()
  data class Exaltation(override val planet: Planet, val sign: ZodiacSign) : EssentialDignity()
  data class Triplicity(override val planet: Planet, val sign: ZodiacSign, val dayNight: DayNight) : EssentialDignity()
  data class Term(override val planet: Planet, val lngDeg: ZodiacDegree) : EssentialDignity()
  data class Face(override val planet: Planet, val lngDeg: ZodiacDegree) : EssentialDignity()

  data class MutualReception(override val planet: Planet,
                             val sign1: ZodiacSign,
                             val dig1: Dignity,
                             val p2: AstroPoint,
                             val sign2: ZodiacSign,
                             val dig2: Dignity) : EssentialDignity() {
    private val mutualData = MutualDataWithSign(planet, sign1, dig1, p2, sign2, dig2)
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is MutualReception) return false

      return mutualData == other.mutualData
    }

    override fun hashCode(): Int {
      return mutualData.hashCode()
    }

  }
}

sealed class AccidentalDignity : IPlanetPattern {

  override val ruleType: RuleType = RuleType.ACCIDENTAL

  data class House_1_10(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class House_4_7_11(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class House_2_5(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class House_9(override val planet: Planet) : AccidentalDignity()
  data class House_3(override val planet: Planet) : AccidentalDignity()
  data class Direct(override val planet: Planet) : AccidentalDignity()
  data class Swift(override val planet: Planet) : AccidentalDignity()
  data class Oriental(override val planet: Planet) : AccidentalDignity()
  data class Occidental(override val planet: Planet) : AccidentalDignity()
  data object Moon_Increase_Light : AccidentalDignity() {
    private fun readResolve(): Any = Moon_Increase_Light
    override val planet: Planet = Planet.MOON
  }

  data class Free_Combustion(override val planet: Planet) : AccidentalDignity()
  data class Cazimi(override val planet: Planet) : AccidentalDignity()
  data class Partile_Conj_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity()
  data class Partile_Conj_North_Node(override val planet: Planet, val node: LunarNode) : AccidentalDignity()
  data class Partile_Trine_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity()
  data class Partile_Sextile_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity()
  data class Partile_Conj_Regulus(override val planet: Planet) : AccidentalDignity()
  data class Partile_Conj_Spica(override val planet: Planet) : AccidentalDignity()
  data class JoyHouse(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class Hayz(override val planet: Planet, val dayNight: DayNight, val yinYang: YinYang, val sign: ZodiacSign) : AccidentalDignity()
  data class Besieged_Jupiter_Venus(override val planet: Planet) : AccidentalDignity()
  data class Translation_of_Light(override val planet: Planet, val from: Planet, val to: Planet, val angle: Double, val aspectType: IPointAspectPattern.AspectType?) :
    AccidentalDignity()

  data class Collection_of_Light(override val planet: Planet, val twoPlanets: List<Planet>, val angle: Double) : AccidentalDignity()
  data class Refrain_from_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet, val aspect: Aspect) : AccidentalDignity()
}


sealed class Debility : IPlanetPattern {
  override val ruleType: RuleType = RuleType.DEBILITY

  data class Detriment(override val planet: Planet, val sign: ZodiacSign) : Debility()
  data class Fall(override val planet: Planet, val sign: ZodiacSign) : Debility()
  data class Peregrine(override val planet: Planet) : Debility()
  data class House_12(override val planet: Planet) : Debility()
  data class House_6_8(override val planet: Planet, val house: Int) : Debility()
  data class Retrograde(override val planet: Planet) : Debility()
  data class Slower(override val planet: Planet) : Debility()
  data class Occidental(override val planet: Planet) : Debility()
  data class Oriental(override val planet: Planet) : Debility()
  data object Moon_Decrease_Light : Debility() {
    override val planet: Planet = Planet.MOON
    private fun readResolve(): Any = Moon_Decrease_Light
  }

  data class Combustion(override val planet: Planet) : Debility()
  data class Sunbeam(override val planet: Planet) : Debility()
  data class Partile_Conj_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet) : Debility()
  data class Partile_Conj_South_Node(override val planet: Planet) : Debility()
  data class Besieged_Mars_Saturn(override val planet: Planet) : Debility()
  data class Partile_Oppo_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet) : Debility()
  data class Partile_Square_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet) : Debility()
  data class Conj_Algol(override val planet: Planet) : Debility()
  data class Out_of_Sect(override val planet: Planet, val dayNight: DayNight, val yinYang: YinYang, val sign: ZodiacSign) : Debility()
  data class Refrain_from_Venus_Jupiter(override val planet: Planet, val venusOrJupiter: Planet, val aspect: Aspect) : Debility()

  data class MutualDeception(override val planet: Planet,
                             val sign1: ZodiacSign,
                             val dig1: Dignity,
                             val p2: AstroPoint,
                             val sign2: ZodiacSign,
                             val dig2: Dignity) : Debility() {
    private val mutualData = MutualDataWithSign(planet, sign1, dig1, p2, sign2, dig2)
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is MutualDeception) return false

      return mutualData == other.mutualData
    }

    override fun hashCode(): Int {
      return mutualData.hashCode()
    }

  }
}

sealed class Misc : IPlanetPattern {
  override val ruleType: RuleType = RuleType.MISC

  /**
   * 插隊：[planet]（追的那顆）的入相位在成形之前被第三顆星先碰到被追的那顆。
   * 判斷層只**持有**事實 [outcome]（[ApplicationOutcome.Prohibition]），不另抄一份欄位；下面的屬性都是給翻譯層的捷徑。
   * [byBody]：以合相插隊（實體攔截），否則是透過光線；[ingress]：插隊之前已換星座的那顆
   */
  data class Prohibited(override val planet: Planet, val outcome: ApplicationOutcome.Prohibition) : Misc() {
    val applied: Planet get() = outcome.applied
    val aspect: Aspect get() = outcome.pattern.aspect
    val prohibitor: Planet get() = outcome.prohibitor
    val with: Planet get() = outcome.with
    val interruption: Aspect get() = outcome.interruption.aspect
    val ingress: Planet? get() = outcome.ingress
    val byBody: Boolean get() = outcome.byBody
  }

  /**
   * 撲空：[planet]（追的那顆）的入相位在成形之前，被追的那顆先與更慢的星成形然後離開（[ApplicationOutcome.Frustration]）。
   * 判斷層只持有事實 [outcome]；[ingress]：撲空之前已換星座的那顆
   */
  data class Frustrated(override val planet: Planet, val outcome: ApplicationOutcome.Frustration) : Misc() {
    val applied: Planet get() = outcome.applied
    val aspect: Aspect get() = outcome.pattern.aspect
    val frustrator: Planet get() = outcome.frustrator
    val interruption: Aspect get() = outcome.interruption.aspect
    val ingress: Planet? get() = outcome.ingress
  }

  /**
   * 臨陣退縮：[planet]（追的那顆）的入相位在成形之前，兩者之一停滯轉向（[stationary]），兩星最接近時仍差 [closestOrb] 度便漸行漸遠
   * （[ApplicationOutcome.Refranation]）。判斷層只持有事實 [outcome]。
   * 不限哪兩顆星；只與金木、火土有關的計分判斷另見 [Debility.Refrain_from_Venus_Jupiter]、[AccidentalDignity.Refrain_from_Mars_Saturn]
   */
  data class Refrained(override val planet: Planet, val outcome: ApplicationOutcome.Refranation) : Misc() {
    val applied: Planet get() = outcome.applied
    val aspect: Aspect get() = outcome.pattern.aspect
    val refranator: Planet get() = outcome.refranator
    val stationary: StationaryType get() = outcome.stationary.type
    val closestOrb: Double get() = outcome.closestOrb
    val ingress: Planet? get() = outcome.ingress
  }

  /**
   * 包夾：[planet] 被兩側夾住（[Besiegement]：每一側是星體本身或投射的光線）。判斷層只持有事實 [besiegement]。
   * 計分判斷另見 [Debility.Besieged_Mars_Saturn]、[AccidentalDignity.Besieged_Jupiter_Venus]
   */
  data class Besieged(override val planet: Planet, val besiegement: Besiegement) : Misc() {
    val before: Planet get() = besiegement.before.by
    val beforeAspect: Aspect? get() = besiegement.before.aspect
    val beforeDistance: Double get() = besiegement.before.distance
    val after: Planet get() = besiegement.after.by
    val afterAspect: Aspect? get() = besiegement.after.aspect
    val afterDistance: Double get() = besiegement.after.distance
  }

  /**
   * [planet] 參與的居上關係 (可能是居上者，也可能是被壓者)。
   * 屬 [RuleType.MISC] , 不列入尊貴 / 無力的計分。
   * [ascRuler]：被居上的那顆是命主星（上升星座的主星）—— 本命篩選（[ClassicalPatternContext.overcome]）才會標，判定要靠上升點。
   * [houses]：兩顆星各落哪一宮、守護哪幾宮（盤本身的宮制）；[sect]：盤的日夜。兩者都是給判讀的細節，
   * 精度供不起時由 [trimmedFor] 拿掉，格局本身（只看星座）仍成立
   */
  data class Overcoming(
    override val planet: Planet,
    val overcoming: destiny.core.astrology.classical.Overcoming,
    val ascRuler: Boolean = false,
    val houses: OvercomingHouses? = null,
    val sect: DayNight? = null,
  ) : Misc() {
    /** 居上者與盤的日夜同區分（夜盤的火星、日盤的木星…）；水星或日夜不明為 null */
    val superiorInSect: Boolean?
      get() = (overcoming.superior as? Planet)?.sect?.let { s -> sect?.let { it == s } }

    /** [planet] 是否為居上者 */
    val superior: Boolean
      get() = overcoming.superior == planet
  }

  /**
   * 不相見（[destiny.core.astrology.classical.Aversion]，以星座計）：[planet] 與它所在星座的主人、或（命主星）與上升彼此看不到。
   * 屬 [RuleType.MISC]，不計分
   */
  data class Aversion(override val planet: Planet, val aversion: destiny.core.astrology.classical.Aversion) : Misc()

  /** 此星體 (mostly [Planet.MOON]) 目前處於空亡狀態 , 前一個準確交角資訊為 [exactAspectPrior] , 後一個準確交角資訊為 [exactAspectAfter]
   * */
  @Serializable
  data class VoidCourseSpan(override val planet: Planet,
                            @Contextual
                            override val begin : GmtJulDay,
                            @Serializable(with = IZodiacDegreeSerializer::class)
                            override val fromPos : IZodiacDegree,
                            @Contextual
                            override val end : GmtJulDay,
                            @Serializable(with = IZodiacDegreeSerializer::class)
                            override val toPos : IZodiacDegree,
                            val exactAspectPrior: AspectData, val exactAspectAfter: AspectData) : Misc(), IStarEventSpan {
    override val star: Star = planet

    override fun getTitle(lang: Lang): String {
      return planet.toString(defaultLocale) + " 空亡"
    }

    override fun getDescription(lang: Lang): String {
      return buildString {
        append(" 為期 ")
        if (duration.inWholeHours > 0) {
          append(duration.inWholeHours).append("小時")
        } else {
          append(duration.inWholeMinutes).append("分鐘")
        }
      }
    }
  }

}

/**
 * 這個格局牽涉到的星 —— 主角 [IPlanetPattern.planet] 加上格局裡的另一方（傳遞光線的兩端、包夾的兩側、互容的對象…）。
 *
 * 用途：某顆星的位置不可信時，判斷哪些格局跟著不可信。
 * 只列格局**本身記錄**的星；固定的參照（太陽之於焦傷、恆星之於 Algol）不算。
 */
val IPlanetPattern.participants: Set<AstroPoint>
  get() = setOf<AstroPoint>(planet) + when (this) {
    is EssentialDignity.MutualReception          -> setOf(p2)
    is Debility.MutualDeception                  -> setOf(p2)
    is AccidentalDignity.Translation_of_Light    -> setOf(from, to)
    is AccidentalDignity.Collection_of_Light     -> twoPlanets.toSet()
    is AccidentalDignity.Refrain_from_Mars_Saturn -> setOf(marsOrSaturn)
    is Debility.Refrain_from_Venus_Jupiter       -> setOf(venusOrJupiter)
    is AccidentalDignity.Partile_Conj_Jupiter_Venus    -> setOf(venusOrJupiter)
    is AccidentalDignity.Partile_Trine_Jupiter_Venus   -> setOf(venusOrJupiter)
    is AccidentalDignity.Partile_Sextile_Jupiter_Venus -> setOf(venusOrJupiter)
    is AccidentalDignity.Partile_Conj_North_Node -> setOf(node)
    is Debility.Partile_Conj_Mars_Saturn         -> setOf(marsOrSaturn)
    is Debility.Partile_Oppo_Mars_Saturn         -> setOf(marsOrSaturn)
    is Debility.Partile_Square_Mars_Saturn       -> setOf(marsOrSaturn)
    is Misc.Prohibited                           -> setOfNotNull(applied, prohibitor, with, ingress)
    is Misc.Frustrated                           -> setOfNotNull(applied, frustrator, ingress)
    is Misc.Refrained                            -> setOfNotNull(applied, refranator, ingress)
    is Misc.Besieged                             -> setOf(before, after)
    is Misc.Overcoming                           -> setOf(overcoming.superior, overcoming.inferior)
    is Misc.Aversion                             -> setOf(aversion.from)
    else                                         -> emptySet()
  }

/** 居上的兩顆星各落哪一宮（[superiorHouse]／[inferiorHouse]）、守護哪幾宮（宮頭落在它守護的星座；象限宮制下被劫奪的星座不算） */
data class OvercomingHouses(
  val superiorHouse: Int,
  val superiorRules: List<Int>,
  val inferiorHouse: Int,
  val inferiorRules: List<Int>,
)

/**
 * 精度供不起時，拿掉格局裡要靠軸點／日夜盤的**細節**（格局本身仍成立、照樣列出）。
 * 與 [requirements] 不同：那邊是整個格局不成立就濾掉，這裡是格局成立、只是有些附帶資訊不可信
 */
fun IPlanetPattern.trimmedFor(grain: BirthDataGrain, sect: DayNight?): IPlanetPattern = when (this) {
  is Misc.Overcoming -> copy(
    houses = houses.takeIf { PatternRequirement.AXIS.isMetBy(grain, sect) },
    sect = this.sect.takeIf { PatternRequirement.SECT.isMetBy(grain, sect) },
  )
  else -> this
}

/**
 * 一種格局要成立，出生資料得提供什麼。精度不夠時由 `DtoFactory.getClassicalPatterns` 依此濾掉，
 * 「哪種格局需要什麼」只在 [requirements] 一處宣告，不散在過濾器的 if 鏈裡
 */
enum class PatternRequirement {
  /** 要軸點／宮位（精確時刻）：星體在地平面上下 */
  AXIS,
  /** 要判得出日夜盤 */
  SECT,
  /** 要離太陽的細距離（時辰級以內）：日輪內 17′ 這種尺度，晝夜級 ±7 小時水星金星就差到 0.7° */
  SUN_DISTANCE_FINE;

  /** 這份出生資料（精度 [grain]、日夜盤 [sect]，null ＝ 判不出）供不供得起 */
  fun isMetBy(grain: BirthDataGrain, sect: DayNight?): Boolean = when (this) {
    AXIS              -> grain.includeAxis
    SECT              -> sect != null
    SUN_DISTANCE_FINE -> grain.timeToleranceHours <= 1.0
  }
}

/**
 * 這個格局需要出生資料提供什麼（[PatternRequirement]）。得時／不得時同時看日夜與地平面上下；三分性與依賴它的外來看日夜；
 * 日輪內看離太陽的細距離。其餘格局只要位置本身可信（[participants] 配合 `BirthDataGrain.allowsNatalTarget`）
 */
val IPlanetPattern.requirements: Set<PatternRequirement>
  get() = when (this) {
    is AccidentalDignity.Hayz, is Debility.Out_of_Sect    -> setOf(PatternRequirement.AXIS, PatternRequirement.SECT)
    is EssentialDignity.Triplicity, is Debility.Peregrine -> setOf(PatternRequirement.SECT)
    is AccidentalDignity.Cazimi                           -> setOf(PatternRequirement.SUN_DISTANCE_FINE)
    is Misc.Overcoming                                    -> if (ascRuler) setOf(PatternRequirement.AXIS) else emptySet()
    is Misc.Aversion                                      ->
      if (aversion.basis == destiny.core.astrology.classical.Aversion.Basis.ASCENDANT) setOf(PatternRequirement.AXIS) else emptySet()
    else                                                  -> emptySet()
  }

/**
 * 只看**星座**就判得出來的格局 —— 星的度數差幾度也不變（除非換了星座）。
 *
 * 入廟、入旺、三分性、落陷、入弱，以及互容與互陷（雙方各在對方守護的星座）。
 * 界、面、相位、離太陽多遠、包夾…都要看度數，不在此列。
 */
val IPlanetPattern.signBased: Boolean
  get() = this is EssentialDignity.Ruler || this is EssentialDignity.Exaltation || this is EssentialDignity.Triplicity ||
    this is Debility.Detriment || this is Debility.Fall ||
    this is EssentialDignity.MutualReception || this is Debility.MutualDeception
