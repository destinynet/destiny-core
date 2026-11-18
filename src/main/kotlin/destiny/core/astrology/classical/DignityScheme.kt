/**
 * Created by smallufo on 2026-11-18.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IDayNight
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree
import destiny.tools.I18nBundles
import destiny.tools.ILocaleString
import destiny.tools.Lang

/**
 * 本質尊貴的一套表。廟、旺、落、陷（托勒密）與面（迦勒底序）各家相同，分歧在**界**與**三分性**：
 *
 * - [EGYPTIAN]：埃及界＋Dorotheus 三分性 —— 希臘化與波斯占星（Valens、Dorotheus、Abu Ma'shar）用的那一套
 * - [PTOLEMAIC]：托勒密界＋托勒密（Lilly）三分性 —— 文藝復興與 Lilly 一系的卜卦、本命
 *
 * 三分性只取當令的那一顆（日盤取日主、夜盤取夜主）；Dorotheus 的共管星（[ITriplicity.getPartner]）不計分
 */
enum class DignityScheme(val triplicity: ITriplicity, val term: ITerm) : ILocaleString {
  EGYPTIAN(TriplicityDorotheusImpl, TermEgyptianImpl),
  PTOLEMAIC(TriplicityWilliamImpl, TermPtolomyImpl);

  /** 以這一套表組成的 [IEssential]（互容、接納要用）；[dayNight] 只在 [IEssential.receivingDignityOf] 判日夜時用到 */
  fun essential(dayNight: IDayNight): IEssential = EssentialImpl(
    RulerPtolemyImpl, ExaltationPtolemyImpl, FallPtolemyImpl, DetrimentPtolemyImpl,
    triplicity, term, FaceChaldeanImpl, dayNight,
  )

  /**
   * [degree] 這個位置上，[dignity] 歸哪一顆星管（例：[Dignity.TERM] ＝ 這一度的界主；[Dignity.DETRIMENT] ＝ 在這裡落陷的星）。
   * 沒有（某些星座沒有旺、落），或三分性而日夜不明 ⇒ null
   */
  fun lordOf(dignity: Dignity, degree: ZodiacDegree, sect: DayNight?): AstroPoint? {
    val sign = degree.sign
    return when (dignity) {
      Dignity.RULER      -> with(RulerPtolemyImpl) { sign.getRulerPoint() }
      Dignity.EXALTATION -> with(ExaltationPtolemyImpl) { sign.getExaltPoint() }
      Dignity.TRIPLICITY -> sect?.let { with(triplicity) { sign.getTriplicityPoint(it) } }
      Dignity.TERM       -> term.getPoint(degree)
      Dignity.FACE       -> FaceChaldeanImpl.getPoint(degree)
      Dignity.FALL       -> with(FallPtolemyImpl) { sign.getFallPoint() }
      Dignity.DETRIMENT  -> with(DetrimentPtolemyImpl) { sign.getDetrimentPoint() }
    }
  }

  /** [planet] 在 [degree] 的本質尊貴與計分（見 [PlanetDignities]） */
  fun of(planet: Planet, degree: ZodiacDegree, sect: DayNight?): PlanetDignities {
    val lords = Dignity.entries.associateWith { lordOf(it, degree, sect) }
    val own = Dignity.entries.filter { lords[it] === planet }
    return PlanetDignities(planet, degree, lords, own)
  }

  override fun getTitle(lang: Lang): String =
    I18nBundles.string("destiny.core.astrology.classical.Classical", lang, "DignityScheme.$name") ?: name

  companion object {
    /** 時主星技法（小限、黃道釋放、法達）來自希臘化與波斯傳統，配埃及界 */
    val DEFAULT = EGYPTIAN
  }
}

/**
 * 一顆星的本質尊貴：
 * - [lords]：它所在位置的七種尊貴各歸誰管（廟主、旺主、三分主、界主、面主、落、陷）；同一欄也是「誰接納它」
 * - [own]：它自己擁有的（[lords] 裡是它自己的那幾格），由強到弱
 * - [peregrine]：外來（漂泊）—— 廟、旺、三分、界、面都沒有，也不在落、陷（與 core 的 `Debility.Peregrine` 同一個判準）
 * - [score]：Lilly 的分數：[Dignity.score] 的總和，外來另扣 [PEREGRINE_SCORE]
 */
data class PlanetDignities(
  val planet: Planet,
  val degree: ZodiacDegree,
  val lords: Map<Dignity, AstroPoint?>,
  val own: List<Dignity>,
) {
  val peregrine: Boolean get() = own.isEmpty()
  val score: Int get() = own.sumOf { it.score } + (if (peregrine) PEREGRINE_SCORE else 0)

  companion object {
    /** Lilly《Christian Astrology》的外來（Peregrine）扣分 */
    const val PEREGRINE_SCORE = -5
  }
}
