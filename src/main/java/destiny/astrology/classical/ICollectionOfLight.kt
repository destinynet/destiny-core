/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.astrology.classical

import destiny.astrology.AspectData.Type.APPLYING
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Point

interface ICollectionOfLight {

  /** 收集光線的形式  */
  enum class CollectType {
    DIGNITIES, DEBILITIES, ALL
  }

  /**
   * 查詢此 [planet] 是否有搜集光線
   * 可以想成 [planet].collectsLightFrom(h , collectType) : List<Planet>?
   *
   * @param collectType 詢問是否符合某種 「光線蒐集模式」 : [CollectType]
   * 傳回的 List<Planet> size 必定 = 2 , 且不為 null
   *
   * TODO : 改傳回 Set<Planet>? 或許比較適合
   */
  fun getResult(planet: Planet, h: IHoroscopeModel, collectType: CollectType): List<Planet>?

  /**
   * 此 point 是否從 p1 , p2 收集光線
   * 演算法 : p1 , p2 不能是 [APPLYING]
   * 而且 p1 , p2 都必須 與 point 形成 [APPLYING]
   */
  fun isCollecting(point : Point , h: IHoroscopeModel , p1 : Point , p2 : Point ) : Boolean

  /**
   * 「可能」不指定「光線蒐集模式」，若呈現任何一種，就傳回來
   *
   * @param collectType 若為 null , 則傳回的 CollectType 會計算出，是哪種 [CollectType]
   */
  fun getResultMaybeType(planet: Planet, h: IHoroscopeModel, collectType: CollectType?): Pair<List<Planet>, CollectType>?

}
