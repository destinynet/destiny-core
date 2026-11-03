/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 受剋（希臘化的 kakōsis，英文 maltreatment）與受扶（agathunsis，bonification）：一顆星被凶星（或吉星）以下列條件之一「處置」。
 * 條件來自 Antiochus／Porphyry 的清單（[Condition]）；一筆是一顆星、一種方向（[polarity]）符合的全部條件，至少一條。
 *
 * 同一組條件換成吉星就是受扶 —— 兩者是同一套演算法（[IMaltreatment]），只差施予者是凶星還是吉星
 */
data class Maltreatment(
  val planet: Planet,
  val polarity: Polarity,
  val conditions: List<Condition>,
) : ClassicalFact {

  enum class Polarity {
    /** 受剋：施予者是凶星 */
    MALTREATMENT,
    /** 受扶：施予者是吉星 */
    BONIFICATION
  }

  /** 牽涉到的施予者（凶星或吉星） */
  val sources: Set<Planet> get() = conditions.flatMapTo(LinkedHashSet()) { it.by }

  /** 一條條件；[by] 是施予者（包夾有兩顆） */
  sealed interface Condition {
    val by: List<Planet>

    /** 被 [overcoming] 的居上者居上（以 [IOvercoming] 的基準：星座或度數） */
    data class Overcome(val overcoming: Overcoming) : Condition {
      override val by: List<Planet> get() = listOfNotNull(overcoming.superior as? Planet)
    }

    /** 與 [source] 對沖；[orb] 是實際角距與 180° 的差 */
    data class Opposed(val source: Planet, val orb: Double) : Condition {
      override val by: List<Planet> get() = listOf(source)
    }

    /** 與 [source] 貼合（合相，[orb] 度以內） */
    data class Adherence(val source: Planet, val orb: Double) : Condition {
      override val by: List<Planet> get() = listOf(source)
    }

    /** [source] 以 [aspect] 的光線打中這顆星；[orb] 是實際角距與 [aspect] 標準角度的差；[dexter] ＝ 光線從 [source] 往黃道後方（逆黃道方向）投射 */
    data class RayHurled(val source: Planet, val aspect: Aspect, val orb: Double, val dexter: Boolean) : Condition {
      override val by: List<Planet> get() = listOf(source)
    }

    /** 被包夾（[IBesiegement] 的判準） */
    data class Besieged(val besiegement: Besiegement) : Condition {
      override val by: List<Planet> get() = listOf(besiegement.before.by, besiegement.after.by)
    }
  }
}

/**
 * 放射光線算哪一個方向：[DEXTER] ＝ 施予者在黃道後方（度數較大），以逆黃道方向的光線打到這顆星；[SINISTER] 相反；[EITHER] 兩者都算。
 * 傳本與現代譯者對 aktinobolia 的方向說法不一，所以做成選項；預設 [DEXTER]（使用者 2026-11-02 定）
 */
enum class RayDirection { DEXTER, SINISTER, EITHER }

/**
 * 受剋與受扶的判準選項（同一套演算法的參數；不同的條件清單是另一個 [IMaltreatment] 實作）。
 *
 * - [malefics]／[benefics]：施予者
 * - [maleficOvercoming]／[beneficOvercoming]：被居上要算哪些居上相位，預設與本命居上同一組（[NatalOvercomingOptions.MALEFIC_ASPECTS]／[NatalOvercomingOptions.BENEFIC_ASPECTS]）；
 * - [overcomingBasis]：居上以星座還是度數計；實作注入的 [IOvercoming] 必須是同一個基準（建構時檢查）
 * - [oppositionOrb]：null ＝ 以星座計（兩星座相對即算）；有值 ＝ 以度數計、此容許度內
 * - [adherenceOrb]、[adherenceSameSign]：貼合（合相）的容許度、是否要同星座
 * - [rayOrb]、[maleficRays]／[beneficRays]、[rayDirection]：放射光線的容許度、交角、方向
 * - [maleficConditions]／[beneficConditions]：受剋、受扶各判哪些條件。受扶預設不含對沖 —— 對沖本身是不和的配置，與吉星對沖不算受扶
 */
data class MaltreatmentOptions(
  val malefics: Set<Planet> = setOf(Planet.MARS, Planet.SATURN),
  val benefics: Set<Planet> = setOf(Planet.VENUS, Planet.JUPITER),
  val maleficOvercoming: Set<Aspect> = NatalOvercomingOptions.MALEFIC_ASPECTS,
  val beneficOvercoming: Set<Aspect> = NatalOvercomingOptions.BENEFIC_ASPECTS,
  val overcomingBasis: OvercomingBasis = OvercomingBasis.SIGN,
  val oppositionOrb: Double? = null,
  val adherenceOrb: Double = 3.0,
  val adherenceSameSign: Boolean = true,
  val rayOrb: Double = 3.0,
  val maleficRays: Set<Aspect> = setOf(Aspect.SQUARE, Aspect.OPPOSITION),
  val beneficRays: Set<Aspect> = setOf(Aspect.SEXTILE, Aspect.TRINE),
  val rayDirection: RayDirection = RayDirection.DEXTER,
  val maleficConditions: Set<ConditionType> = ConditionType.entries.toSet(),
  val beneficConditions: Set<ConditionType> = ConditionType.entries.toSet() - ConditionType.OPPOSED,
) {
  enum class ConditionType { OVERCOME, OPPOSED, ADHERENCE, RAY, BESIEGED }

  companion object {
    /** 以星座計（居上、對沖只看星座；貼合、放射光線本來就看度數）。實務上用這一組 */
    val BY_SIGN = MaltreatmentOptions()

    /** 對沖也看度數（與貼合、放射光線同一個 3° 容許度）；居上要以度數計，另注入度數版的 [IOvercoming] */
    val BY_DEGREE = MaltreatmentOptions(oppositionOrb = 3.0, overcomingBasis = OvercomingBasis.DEGREE)
  }
}

/**
 * 受剋與受扶。實作決定「條件清單」；同一清單的細節由 [MaltreatmentOptions] 調整
 */
interface IMaltreatment {

  /** [planet] 在 [polarity] 方向符合的條件；一條都沒有則為 null */
  fun getMaltreatment(h: IHoroscopeModel, planet: Planet, polarity: Maltreatment.Polarity): Maltreatment?

  /** 全盤：七顆古典行星各自的受剋、受扶 */
  fun getResults(h: IHoroscopeModel): List<Maltreatment> =
    Maltreatment.Polarity.entries.flatMap { polarity ->
      Planet.classicalList.mapNotNull { getMaltreatment(h, it, polarity) }
    }
}
