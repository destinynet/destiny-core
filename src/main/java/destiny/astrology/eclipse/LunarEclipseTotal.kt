/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse

class LunarEclipseTotal(
  penumbraBegin: Double,
  partialBegin: Double,
  /** 月亮全部進入地球本影 剛開始 (U2)  */
  val totalBegin: Double,

  max: Double,

  /** 月亮全部進入地球本影 剛結束 (U3)  */
  val totalEnd: Double, partialEnd: Double, penumbraEnd: Double) : LunarEclipsePartial(penumbraBegin, partialBegin, max, partialEnd, penumbraEnd) {

  override val lunarType: ILunarEclipse.LunarType
    get() = ILunarEclipse.LunarType.TOTAL

}
