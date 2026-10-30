/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.Planet

/**
 * 一次光線收集：兩顆比較快的星彼此不相見，卻都入相位到比較慢的 [collector]（[first]、[second]），由它把兩者的光收在一起。
 *
 * [between]：兩顆快星本身的相位（null ＝ 不成相位；出相位也算「不相見」）
 * [firstReception]／[secondReception]：該快星以哪一種 essential dignity 接納 [collector]（[collector] 落在它的廟旺三分界面），沒有接納為 null
 */
data class CollectionOfLight(
  val collector: Planet,
  val first: IPointAspectPattern,
  val second: IPointAspectPattern,
  val between: IPointAspectPattern?,
  val firstReception: Dignity?,
  val secondReception: Dignity?,
) {
  /** 兩顆快星都接納收集者 —— Lilly 的定義要求這一點，沒有接納的收集不會成就事情 */
  val received: Boolean get() = firstReception != null && secondReception != null

  /** 兩顆被收集的快星（依成形先後） */
  val planets: List<Planet> get() = listOf(first, second).map { p -> p.points.first { it != collector } as Planet }
}

interface ICollectionOfLight {

  /**
   * 此星盤中，[planet] 是否在收集光線；沒有則為 null。
   * [receivedOnly]：只認兩顆快星都接納收集者的（[CollectionOfLight.received]，Lilly 的定義）
   */
  fun getResult(planet: Planet, h: IHoroscopeModel, receivedOnly: Boolean = false): CollectionOfLight?

  /** 全盤的光線收集（收集者只看七顆古典行星） */
  fun getResults(h: IHoroscopeModel, receivedOnly: Boolean = false): List<CollectionOfLight> =
    Planet.classicalList.mapNotNull { getResult(it, h, receivedOnly) }
}
