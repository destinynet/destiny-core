/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.Planet

/**
 * 一次光線傳遞：[translator] 剛離開與 [from] 的相位（[separating]，出相位），
 * 下一個要成形的是與 [to] 的相位（[applying]，入相位），把 [from] 的光帶給 [to]。
 *
 * [between]：[from] 與 [to] 本身的相位（null ＝ 彼此不成相位，最典型的傳遞）
 */
data class TranslationOfLight(
  val translator: Planet,
  val separating: IPointAspectPattern,
  val applying: IPointAspectPattern,
  val between: IPointAspectPattern?,
) {
  val from: Planet get() = separating.points.first { it != translator } as Planet
  val to: Planet get() = applying.points.first { it != translator } as Planet
}

interface ITranslationOfLight {

  /** 此星盤中，[planet] 是否在傳遞光線；沒有則為 null */
  fun getResult(planet: Planet, h: IHoroscopeModel): TranslationOfLight?

  /** 全盤的光線傳遞（傳遞者只看七顆古典行星） */
  fun getResults(h: IHoroscopeModel): List<TranslationOfLight> =
    Planet.classicalList.mapNotNull { getResult(it, h) }
}
