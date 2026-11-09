/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 不相見（希臘化的 apostrophē，英文 aversion），以星座計：兩個星座相隔 2、6、8、12 個位置（星座差 1、5、7、11），
 * 彼此看不到 —— 沒有任何托勒密相位能連起來。
 *
 * 只收古典本命實際會讀的幾種（[Basis]，由 [AversionOptions.bases] 選），不列全部兩兩配對：
 * - [Basis.RULER]：[planet] 與它所在星座的主人（入廟主）[from] 不相見 —— 主人照應不到它
 * - [Basis.EXALTATION]：[planet] 與它所在星座的入旺主 [from] 不相見
 * - [Basis.ASCENDANT]：命主星 [planet] 與上升（[from] ＝ 上升點）不相見 —— 代表本人的星看不到自己的第一宮
 *
 * [place]：[planet] 落在 [from] 所在星座起算的第幾個星座（2、6、8、12；[destiny.core.astrology.ZodiacSign.placeFrom]）。
 * 經由映點相連的星座對（「看得見」「命令與服從」）要不要例外，見 [AversionOptions.exceptions]
 */
data class Aversion(
  val planet: Planet,
  val from: AstroPoint,
  val place: Int,
  val basis: Basis,
) : ClassicalFact {

  enum class Basis { RULER, EXALTATION, ASCENDANT }
}

/**
 * 兩個不相見的星座之間，仍有古典認可的連結（Ptolemy《四書》第一卷）：
 * - [BEHOLDING]：映點星座（以巨蟹／摩羯軸對稱，[destiny.core.astrology.ZodiacSign.antiscionSign]），彼此「看得見」、等力。
 *   六組：雙子↔巨蟹、金牛↔獅子、牡羊↔處女、雙魚↔天秤、水瓶↔天蠍、摩羯↔射手
 * - [COMMANDING]：反映點星座（以牡羊／天秤軸對稱，[destiny.core.astrology.ZodiacSign.contraAntiscionSign]），「命令與服從」、等上升時間。
 *   六組：牡羊↔雙魚、金牛↔水瓶、雙子↔摩羯、巨蟹↔射手、獅子↔天蠍、處女↔天秤
 *
 * 每種各有兩組本身就是四分（金牛↔獅子、水瓶↔天蠍；金牛↔水瓶、獅子↔天蠍），本來就看得見，例外只對其餘四組有作用
 */
enum class SignLink { BEHOLDING, COMMANDING }

/**
 * 不相見的判準選項。
 *
 * - [bases]：收哪幾種關係（預設行星與入廟主、命主星與上升；可加 [Aversion.Basis.EXALTATION] 入旺主）
 * - [exceptions]：哪些星座連結可以抵銷不相見（[SignLink]）
 * - [points]：看哪些星
 *
 * 流派（[exceptions] 的差別；不相見的星座對共 24 組，兩種例外各抵銷 4 組）：
 * - [HELLENISTIC]（＝ [DEFAULT]）：Valens、Dorotheus、Porphyry 的整宮制用法 —— 只數第 2、6、8、12 個星座，不套例外
 * - [PTOLEMY]：Ptolemy 定義「不相連的星座」（asyndeta）是沒有相位、也沒有看得見或命令與服從關係的星座 ⇒ 兩種連結都抵銷不相見，剩 16 組。
 *   例：金星在牡羊、牡羊之主火星在處女 —— 牡羊在處女起算的第 8 個星座（不相見），但牡羊↔處女是映點星座，照 Ptolemy 不算不相見
 */
data class AversionOptions(
  val bases: Set<Aversion.Basis> = setOf(Aversion.Basis.RULER, Aversion.Basis.ASCENDANT),
  val exceptions: Set<SignLink> = emptySet(),
  val points: List<Planet> = Planet.classicalList,
) {
  companion object {
    /** 希臘化整宮制：不套例外 */
    val HELLENISTIC = AversionOptions()

    /** Ptolemy：看得見、命令與服從的星座對都不算不相見 */
    val PTOLEMY = AversionOptions(exceptions = setOf(SignLink.BEHOLDING, SignLink.COMMANDING))

    /** 預設：[HELLENISTIC]（2026-11-02 定） */
    val DEFAULT = HELLENISTIC
  }
}

/**
 * 不相見。實作決定「看不到」的判法（目前只有以星座計的 [AversionSignImpl]；以度數計 —— 容許度內沒有任何相位 —— 可另寫一個實作）
 */
interface IAversion : IClassicalFactSource {
  fun getResults(h: IHoroscopeModel): List<Aversion>

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = getResults(h)
}
