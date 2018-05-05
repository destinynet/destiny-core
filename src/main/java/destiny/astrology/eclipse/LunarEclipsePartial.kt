/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse

open class LunarEclipsePartial(
  penumbraBegin: Double,

  /** 月亮最先碰觸地球本影 (U1)  */
  val partialBegin: Double,

  max: Double,

  /** 月亮完全離開地球本影 (U4)  */
  val partialEnd: Double,

  penumbraEnd: Double) : LunarEclipsePenumbra(penumbraBegin, max, penumbraEnd) {

  override val lunarType: ILunarEclipse.LunarType
    get() = ILunarEclipse.LunarType.PARTIAL

}
