/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse

/**
 * 半影月食
 */
open class LunarEclipsePenumbra(
  /** 月亮觸碰地球半影 , P1 , 可視為整個 eclipse 的 begin  */
  penumbraBegin: Double,

  max: Double,

  /** 月亮完全離開地球半影 P4 , 可視為整個 eclipse 的 end  */
  penumbraEnd: Double) : AbstractLunarEclipse(penumbraBegin, max, penumbraEnd) {

  override val lunarType: AbstractLunarEclipse.LunarType
    get() = AbstractLunarEclipse.LunarType.PENUMBRA


}
