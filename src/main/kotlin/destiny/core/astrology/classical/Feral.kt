/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import kotlin.reflect.KClass
import destiny.core.DayNight
import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 野生（feral）：[planet] 與其他古典行星都沒有相位，孤立無援、不受任何星牽制，也得不到幫助。
 * 中世紀起（Al-Biruni、Bonatti、Lilly）的術語，卜卦最常用在月亮上。[basis]：以什麼判定「沒有相位」
 */
data class Feral(
  val planet: Planet,
  val basis: FeralOptions.Basis,
) : ClassicalFact

/**
 * 野生的判準。
 *
 * - [basis]：以星芒容許度（[Basis.BY_ORB]）或以星座（[Basis.BY_SIGN]）判有沒有相位
 * - [aspects]：算哪些相位（托勒密五相位）
 * - [planets]：判哪些星；對象一律是其餘的古典行星
 * - [aspectEffective]：[Basis.BY_ORB] 用的容許度（兩星星芒半徑相加，與古典格局其餘判斷同一份）
 */
data class FeralOptions(
  val basis: Basis = Basis.BY_ORB,
  val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH),
  val planets: List<Planet> = Planet.classicalList,
  val aspectEffective: AspectEffectiveClassical = AspectEffectiveClassical(),
) {
  enum class Basis {
    /** 在星芒容許度內沒有任何相位 */
    BY_ORB,
    /** 以星座計沒有相位：其餘的古典行星都不在同星座、也不在六分、四分、三分、對沖的星座（更嚴格，少見） */
    BY_SIGN,
  }

  companion object {
    /** 中世紀到 Lilly 的通行讀法：以星芒容許度計，沒有任何托勒密相位 */
    val DEFAULT = FeralOptions()

    /** 以星座計 */
    val BY_SIGN = FeralOptions(basis = Basis.BY_SIGN)
  }
}

/** 野生 */
interface IFeral : IClassicalFactSource {

  /** [planet] 是否野生；不是（或盤上缺位置）為 null */
  fun getFeral(h: IHoroscopeModel, planet: Planet): Feral?

  /** 全盤，依古典行星順序 */
  fun getResults(h: IHoroscopeModel): List<Feral>

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = getResults(h)

  override val produces: Set<KClass<out ClassicalFact>> get() = setOf(Feral::class)
}
