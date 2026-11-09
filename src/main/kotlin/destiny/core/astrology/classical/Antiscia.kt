/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import kotlin.reflect.KClass
import destiny.core.DayNight
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 映點（antiscia）：[planet] 的映點落在 [other] 上（[orb] 度以內）。映點是對稱的 —— [other] 的映點也落在 [planet] 上，所以一對星只記一筆。
 *
 * - [contra] = false：映點，以夏至、冬至軸對稱（[destiny.core.astrology.ZodiacDegree.antiscion]），古典視同暗中的合相
 * - [contra] = true：反映點，以春分、秋分軸對稱（[destiny.core.astrology.ZodiacDegree.contraAntiscion]），視同暗中的對沖
 */
data class Antiscion(
  val planet: Planet,
  val other: Planet,
  val contra: Boolean,
  val orb: Double,
) : ClassicalFact

/**
 * 映點的判準選項。
 *
 * - [orb]：映點與對方的容許度（各家從 1° 到依星體光度不等；預設取最嚴的 1°）
 * - [contra]：要不要判反映點
 * - [points]：看哪些星
 */
data class AntisciaOptions(
  val orb: Double = 1.0,
  val contra: Boolean = true,
  val points: List<Planet> = Planet.classicalList,
) {
  companion object {
    val DEFAULT = AntisciaOptions()
  }
}

/**
 * 映點。實作決定映點落在對方上的判法（目前只有以度數容許度判的 [AntisciaImpl]）
 */
interface IAntiscia : IClassicalFactSource {

  /** 全盤的映點、反映點；一對星一筆，[Antiscion.planet] 是 [AntisciaOptions.points] 裡排在前面的那顆 */
  fun getResults(h: IHoroscopeModel): List<Antiscion>

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = getResults(h)

  override val produces: Set<KClass<out ClassicalFact>> get() = setOf(Antiscion::class)
}
