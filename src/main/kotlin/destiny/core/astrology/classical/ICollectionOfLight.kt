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
 * [type]：兩顆快星是否都以 essential dignities 接納 [collector]（[collector] 落在它們的廟旺三分界面）——
 * 都是 ⇒ [ICollectionOfLight.CollectType.DIGNITIES]；都以落陷入弱接納 ⇒ [ICollectionOfLight.CollectType.DEBILITIES]；其餘 ⇒ [ICollectionOfLight.CollectType.ALL]
 */
data class CollectionOfLight(
  val collector: Planet,
  val first: IPointAspectPattern,
  val second: IPointAspectPattern,
  val between: IPointAspectPattern?,
  val type: ICollectionOfLight.CollectType,
) {
  /** 兩顆被收集的快星（依成形先後） */
  val planets: List<Planet> get() = listOf(first, second).map { p -> p.points.first { it != collector } as Planet }
}

interface ICollectionOfLight {

  /** 收集光線的形式；查詢時 [ALL] ＝ 不限，結果的 [CollectionOfLight.type] 是 [ALL] 時表示兩顆快星的接納方式不一致或沒有接納 */
  enum class CollectType {
    DIGNITIES, DEBILITIES, ALL
  }

  /** 此星盤中，[planet] 是否在收集光線（符合 [collectType]）；沒有則為 null */
  fun getResult(planet: Planet, h: IHoroscopeModel, collectType: CollectType = CollectType.ALL): CollectionOfLight?

  /** 全盤的光線收集（收集者只看七顆古典行星） */
  fun getResults(h: IHoroscopeModel, collectType: CollectType = CollectType.ALL): List<CollectionOfLight> =
    Planet.classicalList.mapNotNull { getResult(it, h, collectType) }
}
