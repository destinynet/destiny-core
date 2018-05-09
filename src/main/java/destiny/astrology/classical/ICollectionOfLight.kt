/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.astrology.classical

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

interface ICollectionOfLight {

  /** 收集光線的形式  */
  enum class CollectType {
    DIGNITIES, DEBILITIES, ALL
  }

  /**
   * 指定某種「光線蒐集模式」
   *
   * @param collectType @NotNull 詢問是否符合某種 「光線蒐集模式」 : [CollectType]
   * 傳回的 List<Planet> size 必定 = 2 , 且不為 null
   */
  fun getResult(planet: Planet, h: IHoroscopeModel, collectType: CollectType): List<Planet>?

  /**
   * 「可能」不指定「光線蒐集模式」，若呈現任何一種，就傳回來
   *
   * @param collectType 若為 null , 則傳回的 CollectType 會計算出，是哪種 [CollectType]
   */
  fun getResultMaybeType(planet: Planet, h: IHoroscopeModel, collectType: CollectType?): Pair<List<Planet>, CollectType>?

}
