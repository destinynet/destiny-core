/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import kotlin.reflect.KClass
import destiny.core.DayNight
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 持矛者（doryphoria，spear-bearing）：光體（[light]）的隨從星 [bearer]。古典用它論顯赫與地位。
 *
 * - [distance]：[bearer] 與光體的黃經距離
 * - [preceding]：[bearer] 在光體的前方（黃經較小、先於光體升起）；false ＝ 在後方（之後升起）
 * - [inSect]：[bearer] 的晝夜歸屬與盤的日夜相同（水星或日夜不明為 null）
 *
 * 定義有好幾種（Ptolemy 以先後升起、Porphyry 以同區分的光線…），由 [IDoryphoria] 的實作決定
 */
data class Doryphoria(
  val light: Planet,
  val bearer: Planet,
  val distance: Double,
  val preceding: Boolean,
  val inSect: Boolean?,
) : ClassicalFact

/**
 * Ptolemy 版持矛者的判準選項。
 *
 * - [maxDistance]：與光體的距離上限（Ptolemy 沒有給度數，後世常用一個星座的寬度）
 * - [sunPreceding]／[moonPreceding]：太陽、月亮各取哪一側。Ptolemy：太陽取先於它升起的（前方），月亮取在它之後升起的（後方）
 * - [sameSectOnly]：只收與盤同區分的星（有的派別要求；不要求時 [Doryphoria.inSect] 仍照記）
 * - [bearers]：持矛者的候選（預設五顆行星，不含日月互為持矛者）
 */
data class DoryphoriaOptions(
  val maxDistance: Double = 30.0,
  val sunPreceding: Boolean = true,
  val moonPreceding: Boolean = false,
  val sameSectOnly: Boolean = false,
  val bearers: Set<Planet> = Planet.classicalNonLuminaryList.toSet(),
) {
  companion object {
    /** Ptolemy《四書》：太陽由先於它升起的星護衛、月亮由在它之後升起的星護衛；一個星座寬 */
    val PTOLEMY = DoryphoriaOptions()
  }
}

/**
 * 持矛者。實作決定定義（Ptolemy、Porphyry…）
 */
interface IDoryphoria : IClassicalFactSource {

  /** [light]（太陽或月亮）的持矛者；盤的日夜由呼叫端給（[sect]），null ＝ 不判同區分 */
  fun getBearers(h: IHoroscopeModel, light: Planet, sect: DayNight?): List<Doryphoria>

  /** 太陽與月亮的全部持矛者 */
  fun getResults(h: IHoroscopeModel, sect: DayNight?): List<Doryphoria> =
    listOf(Planet.SUN, Planet.MOON).flatMap { getBearers(h, it, sect) }

  override val usesSect: Boolean get() = true

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = getResults(h, sect)

  override val produces: Set<KClass<out ClassicalFact>> get() = setOf(Doryphoria::class)
}
