/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * Ptolemy 版持矛者：以黃經的先後判定 —— 黃經較小的星先升起。太陽由前方（先於它升起）的星護衛、月亮由後方的星護衛（[DoryphoriaOptions.PTOLEMY]）。
 * 只看盤面時刻的位置，不需要星曆；依距離由近到遠排序
 */
class DoryphoriaPtolemyImpl(
  val options: DoryphoriaOptions = DoryphoriaOptions.PTOLEMY,
) : IDoryphoria {

  override fun getBearers(h: IHoroscopeModel, light: Planet, sect: DayNight?): List<Doryphoria> {
    val lightDeg = h.positionMap[light]?.lngDeg ?: return emptyList()
    val wantPreceding = if (light === Planet.SUN) options.sunPreceding else options.moonPreceding
    return (options.bearers - light).mapNotNull { p ->
      val deg = h.positionMap[p]?.lngDeg ?: return@mapNotNull null
      // p 在光體前方 ＝ 從 p 順黃道走到光體的距離在 (0, 180)
      val behindLight = (lightDeg.value - deg.value).mod(360.0)
      val preceding = behindLight in 0.0..180.0 && behindLight > 0.0
      val distance = if (preceding) behindLight else 360.0 - behindLight
      if (preceding != wantPreceding || distance > options.maxDistance) return@mapNotNull null
      val inSect = p.sect?.let { s -> sect?.let { it == s } }
      if (options.sameSectOnly && inSect != true) return@mapNotNull null
      Doryphoria(light, p, distance, preceding, inSect)
    }.sortedBy { it.distance }
  }
}
