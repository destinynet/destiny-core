/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.IHoroscopeModel

/**
 * 以度數容許度判映點：A 的映點（或反映點）與 B 的黃經相差 [AntisciaOptions.orb] 度以內。只看盤面時刻的位置，不需要星曆。
 * 同一對星同時符合映點與反映點不會發生（兩者差 180°）
 */
class AntisciaImpl(
  val options: AntisciaOptions = AntisciaOptions.DEFAULT,
) : IAntiscia {

  override fun getResults(h: IHoroscopeModel): List<Antiscion> {
    val pts = options.points.mapNotNull { p -> h.positionMap[p]?.lngDeg?.let { p to it } }
    return pts.indices.flatMap { i ->
      (i + 1 until pts.size).mapNotNull { j ->
        val (a, aDeg) = pts[i]
        val (b, bDeg) = pts[j]
        val antiscionOrb = aDeg.antiscion.getAngle(bDeg)
        val contraOrb = aDeg.contraAntiscion.getAngle(bDeg)
        when {
          antiscionOrb <= options.orb -> Antiscion(a, b, contra = false, orb = antiscionOrb)
          options.contra && contraOrb <= options.orb -> Antiscion(a, b, contra = true, orb = contraOrb)
          else -> null
        }
      }
    }
  }
}
