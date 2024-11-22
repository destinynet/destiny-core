package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.ZodiacSign
import java.io.Serializable

interface IMutualData : Serializable {

  /**
   * 意味此 [AstroPoint] 放射出哪種能量 ([Dignity]) 給另一顆星
   * 並不是此 [AstroPoint] 本身於此星座的 [Dignity]
   * 此星於此星座的強弱，靠 [IEssential.getDignities] 取得 , 不寫入此 interface 當中
   */
  val dignityMap: Map<AstroPoint, Dignity>

  val twoPoints: Set<AstroPoint>
    get() = dignityMap.keys

  fun getAnotherPoint(point: AstroPoint): AstroPoint {
    require(twoPoints.contains(point)) {
      twoPoints.joinToString(",") + " don't contain " + point
    }

    return twoPoints.first { it != point }
  }

  fun getDignityOf(point: AstroPoint): Dignity {
    require(twoPoints.contains(point)) {
      twoPoints.joinToString(",") + " don't contain " + point
    }

    return dignityMap.getValue(point)
  }
}

interface IMutualDataWithSign : IMutualData {

  /** 什麼星 在 什麼星座 , 此 map size 固定為 2 */
  val signMap : Map<AstroPoint, ZodiacSign>

}

data class MutualData(override val dignityMap: Map<AstroPoint, Dignity>) : IMutualData {
  /**
   * [p1] 以 [dig1] 的能量招待 (接納) [p2] ,
   * [p2] 以 [dig2] 的能量招待 (接納) [p1]
   *
   * 亦即：
   * [p1] 「給出」 [dig1] 的能量到 [p2]
   * [p2] 「給出」 [dig2] 的能量到 [p1]
   *
   * 另外翻譯為：
   * [p1] 位於 sign1 , 與 sign1 的 [dig2] ([p2]) 飛至 sign2 , 而 sign2 的 [dig1] ([p1]) 飛至 sign1
   * p1 在 sign1 得到 p2 所提供的 dig2 能量
   * p2 在 sign2 得到 p1 所提供的 dig1 能量
   * */
  constructor(p1: AstroPoint, dig1: Dignity, p2: AstroPoint, dig2: Dignity) : this(mapOf(p1 to dig1, p2 to dig2))

}

data class MutualDataWithSign(private val set: Set<Triple<AstroPoint, ZodiacSign, Dignity>>) : IMutualDataWithSign {

  /**
   * [p1] 位於 [sign1] , 與 [sign1] 的 [dig2] ([p2]) 飛至 [sign2] , 而 [sign2] 的 [dig1] ([p1]) 飛至 [sign1]
   * 亦即：
   * [p1] 在 [sign1] 得到 [p2] 所提供的 [dig2] 能量
   * [p2] 在 [sign2] 得到 [p1] 所提供的 [dig1] 能量
   */
  constructor(p1: AstroPoint, sign1: ZodiacSign, dig1: Dignity,
              p2: AstroPoint, sign2: ZodiacSign, dig2: Dignity) : this(setOf(Triple(p1, sign1, dig1), Triple(p2, sign2, dig2)))

  override val dignityMap: Map<AstroPoint, Dignity>
    get() = set.associate { triple -> triple.first to triple.third }


  override val signMap: Map<AstroPoint, ZodiacSign>
    get() = set.associate { t -> t.first to t.second }
}
