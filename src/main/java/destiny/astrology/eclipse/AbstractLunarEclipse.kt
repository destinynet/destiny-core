/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse

@Deprecated("ILunarEclipse")
abstract class AbstractLunarEclipse(

  /** 半影開始 (P1) , 最早 , 可視為整個 eclipse 的 begin  */
  val penumbraBegin: Double,

  max: Double,

  /** 半影結束 (P4) , 最遲 , 可視為整個 eclipse 的 end    */
  val penumbraEnd: Double

                                   ) : AbstractEclipse(penumbraBegin, max, penumbraEnd) {


  abstract val lunarType: ILunarEclipse.LunarType


  override fun toString(): String {
    return "[AbstractLunarEclipse type=$lunarType, begin=$begin, max=$max, end=$end]"
  }
}
