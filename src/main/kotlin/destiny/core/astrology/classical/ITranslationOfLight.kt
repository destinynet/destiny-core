/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPointAspectPattern.AspectType
import destiny.core.astrology.Planet

/**
 * 一次光線傳遞：[translator] 剛離開 [from] 的 [fromAspect]（出相位，差 [fromOrb] 度），
 * 下一個要成形的是與 [to] 的 [toAspect]（入相位，差 [toOrb] 度），把 [from] 的光帶給 [to]。
 *
 * [between]：[from] 與 [to] 本身的交角（null ＝ 彼此不成相位，最典型的傳遞）
 */
data class TranslationOfLight(
  val translator: Planet,
  val from: Planet,
  val fromAspect: Aspect,
  val fromOrb: Double,
  val to: Planet,
  val toAspect: Aspect,
  val toOrb: Double,
  val between: Pair<Aspect, AspectType?>?,
)

interface ITranslationOfLight {

  /** 此星盤中，[planet] 是否在傳遞光線；沒有則為 null */
  fun getResult(planet: Planet, h: IHoroscopeModel): TranslationOfLight?

  /** 全盤的光線傳遞（傳遞者只看七顆古典行星） */
  fun getResults(h: IHoroscopeModel): List<TranslationOfLight> =
    Planet.classicalList.mapNotNull { getResult(it, h) }
}
