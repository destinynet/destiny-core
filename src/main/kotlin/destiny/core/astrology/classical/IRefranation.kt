/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.Planet
import destiny.core.astrology.Stationary

/** 臨陣退縮軌跡的一個取樣點：盤面時刻後 [days] 日，退縮者與對方的黃經 */
data class RefranationSample(
  val days: Double,
  val refranatorLng: Double,
  val otherLng: Double,
)

/**
 * 一次臨陣退縮（Refranation）：盤面時刻 [refranator] 與 [other] 正以 [applying] 入相位，
 * 成形之前 [refranator] 停滯轉向（[stationary]），兩星在 [closestDays] 日後最接近、仍差 [closestOrb] 度，之後漸行漸遠。
 *
 * [track]：自盤面時刻起到兩星再度拉開為止的黃經取樣（等距），給動畫重演用
 */
data class Refranation(
  val refranator: Planet,
  val other: Planet,
  val applying: IPointAspectPattern,
  val stationary: Stationary,
  val stationDays: Double,
  val closestDays: Double,
  val closestOrb: Double,
  val track: List<RefranationSample>,
)

/**
 * Refranation 定義：返回、臨陣脫逃
 * 這是六種 Denials of Perfection 之一 ，定義於此：
 * http://www.skyscript.co.uk/tobyn2.html#ref
 * 當兩顆星正在 apply 某交角，在形成 Perfect 之前，其中一顆轉為逆行（或逆轉順），相位因此沒有成形
 *
 * 兩種情形：
 * 1. 快星即將 apply 他星，而在 perfect 前，快星停滯轉向，代表自我退縮。
 * 2. 快星即將 apply 他星，而在 perfect 前，他星停滯轉向、走得比快星還遠，代表對方退縮。
 *
 * 對方停滯轉向、反而迎上來的（例：月亮入相位土星，土星轉逆往月亮走），相位照樣成形，不算。
 *
 * TODO : antiscia / antiscion (映點) 臨陣退縮
 */
interface IRefranation {

  /** [p1] 與 [p2] 是否臨陣退縮（不分順序）；沒有則為 null */
  fun getResult(h: IHoroscopeModel, p1: Planet, p2: Planet): Refranation?

  /** 全盤（七顆古典行星兩兩之間）的臨陣退縮 */
  fun getResults(h: IHoroscopeModel): List<Refranation> =
    Planet.classicalList.flatMapIndexed { i, p1 ->
      Planet.classicalList.drop(i + 1).mapNotNull { p2 -> getResult(h, p1, p2) }
    }
}
