package destiny.astrology.classical

import destiny.astrology.Point
import destiny.astrology.ZodiacSign

interface IMutualData {

  val pairs: Map<Point, Dignity>

  val twoPoints: Set<Point>
    get() = pairs.keys

  fun getAnotherPoint(point: Point): Point {
    if (!twoPoints.contains(point))
      throw RuntimeException(twoPoints.joinToString(",") + " don't contain " + point)

    return twoPoints.first { it != point }
  }

  fun getDignityOf(point: Point): Dignity {
    if (!twoPoints.contains(point))
      throw RuntimeException(twoPoints.joinToString(",") + " don't contain " + point)

    return pairs.getValue(point)
  }

}

data class MutualData(override val pairs: Map<Point, Dignity>) : IMutualData {
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
  constructor(p1: Point, dig1: Dignity, p2: Point, dig2: Dignity) : this(mapOf(p1 to dig1, p2 to dig2))

}

data class MutualDataWithSign(private val set: Set<Triple<Point, ZodiacSign, Dignity>>) : IMutualData {

  /**
   * [p1] 位於 [sign1] , 與 [sign1] 的 [dig2] ([p2]) 飛至 [sign2] , 而 [sign2] 的 [dig1] ([p1]) 飛至 [sign1]
   * 亦即：
   * [p1] 在 [sign1] 得到 [p2] 所提供的 [dig2] 能量
   * [p2] 在 [sign2] 得到 [p1] 所提供的 [dig1] 能量
   */
  constructor(p1: Point, sign1: ZodiacSign, dig1: Dignity,
              p2: Point, sign2: ZodiacSign, dig2: Dignity) : this(setOf(Triple(p1, sign1, dig1), Triple(p2, sign2, dig2)))

  override val pairs: Map<Point, Dignity>
    get() = set.map { triple -> triple.first to triple.third }.toMap()

}
