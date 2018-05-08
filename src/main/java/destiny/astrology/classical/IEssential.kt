/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 8:29:27
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.IHoro
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import org.slf4j.LoggerFactory

/**
 * Facade Interface of Essential Dignities and Deblitities <br></br>
 * 具備計算 Ptolemy's Table of Essential Dignities and Deblities 的所有介面
 */
interface IEssential {

  /**
   * 那一顆星，透過 [Dignity.RULER] 接納了 p
   */
  fun receivingRulerFromSignMap(p: Point, map: Map<Point, ZodiacSign>): Point?

  /** 承上 , double map 版本 */
  fun receivingRulerFrom(p: Point, map: Map<Point, Double>): Point? {
    return map.mapValues { (_ , degree) -> ZodiacSign.getZodiacSign(degree) }.let { receivingRulerFromSignMap(p , it) }
  }

  /**
   * 哪一顆星，透過 [Dignity.EXALTATION] 接納了 p
   */
  fun receivingExaltFromSignMap(p: Point, map: Map<Point, ZodiacSign>): Point?

  /** 承上 , double map 版本 */
  fun receivingExaltFrom(p: Point, map: Map<Point, Double>): Point? {
    return map.mapValues { (_ , degree) -> ZodiacSign.getZodiacSign(degree) }.let { receivingExaltFromSignMap(p , it) }
  }

  /** 哪一顆星，透過 [Dignity.TRIPLICITY] 接納了 p */
  fun receivingTriplicityFromSignMap(p: Point, map: Map<Point, ZodiacSign>, dayNight: DayNight): Point?

  /** 承上 , double map 版本 */
  fun receivingTriplicityFrom(p: Point, map: Map<Point, Double>, dayNight: DayNight): Point? {
    return map.mapValues { (_ , degree) -> ZodiacSign.getZodiacSign(degree) }.let { receivingTriplicityFromSignMap(p , it , dayNight) }
  }

  /** 那一顆星，透過 [Dignity.TERM] 接納了 p */
  fun receivingTermFrom(p: Point , map: Map<Point , Double>) : Point ?

  /** 哪一顆星，透過 [Dignity.FACE] 接納了 p */
  fun receivingFaceFrom(p:Point , map: Map<Point, Double>) : Point?

  /** 哪一顆星，透過 [Dignity.FALL] 接納了 p */
  fun receivingFallFromSignMap(p: Point, map: Map<Point, ZodiacSign>): Point?

  /** 承上 , double map 版本 */
  fun receivingFallFrom(p: Point, map: Map<Point, Double>): Point? {
    return map.mapValues { (_, degree) -> ZodiacSign.getZodiacSign(degree) }.let { receivingFallFromSignMap(p, it) }
  }

  /** 哪一顆星，透過 [Dignity.DETRIMENT] 接納了 p */
  fun receivingDetrimentFromSignMap(p:Point, map: Map<Point, ZodiacSign>): Point?

  fun receivingDetrimentFrom(p:Point , map: Map<Point, Double>): Point? {
    return map.mapValues { (_, degree) -> ZodiacSign.getZodiacSign(degree) }.let { receivingDetrimentFromSignMap(p, it) }
  }

  /** 取得此顆星，各從哪些星體，接受哪種 [Dignity] 的招待 */
  fun getReceptions(p: Point, map: Map<Point, Double>, dayNight: DayNight? , dignities: Collection<Dignity>): Map<Dignity, Point>

  /** 取得此顆星，各從哪些星體，接受哪種 [Dignity] 的招待 , 但是不計算 [Dignity.TERM] 以及 [Dignity.FACE] , 因為這兩者需要度數 */
  fun getReceptionsFromSign(p: Point, map: Map<Point, ZodiacSign>, dayNight: DayNight? , dignities: Collection<Dignity>): Map<Dignity, Point>

  /**
   * 製作出 Reception 表格
   * 參考 : http://www.skyscript.co.uk/dig6.html
   * */
  fun getReceptionMap(map: Map<Point, Double>, dayNight: DayNight , dignities: Set<Dignity>): Set<Triple<Point, Dignity, Point?>> {
    return map.keys.flatMap { p ->
      getReceptions(p, map, dayNight , dignities).map { (dignity, point) ->
        Triple(p, dignity, point)
      }
    }.toSet()
  }

  /** 查詢 p 在此星盤中 , 是否有與其他任何星，互相接納 (不論 Dignity 是否相等) */
  fun getMutualData(p: Point , map: Map<Point, Double> , dayNight: DayNight? , dignities: Collection<Dignity>) : Set<MutualData> {
    return map.keys.filter { it !== p }
      .flatMap { p2 -> getReceptions(p2 , map , dayNight , dignities)
        .filter { (dig1 , p1) -> p1 === p }
        .map { (dig1,p1) -> p1 to dig1 }
        .flatMap { (p1 , dig1) -> getReceptions(p1 , map , dayNight , dignities)
          .filter { (dig2 , p) -> p === p2 }
          .map { (dig2 , p2) -> MutualData(p1, dig1, p2, dig2) }
        }
      }.toSet()
  }

  /** 查詢 p 在此星盤中 , 是否有與其他任何星，互相接納 (不論 Dignity 是否相等) . 只考量星座，故，無法計算 [Dignity.TERM] 或 [Dignity.FALL] */
  fun getMutualDataFromSign(p: Point, map: Map<Point, ZodiacSign>, dayNight: DayNight?, dignities: Collection<Dignity>) : Set<MutualData> {
    return map.keys.filter { it !== p }
      .flatMap { p2 -> getReceptionsFromSign(p2 , map , dayNight , dignities)
        .filter { (dig1 , p1) -> p1 === p }
        .map { (dig1,p1) -> p1 to dig1 }
        .flatMap { (p1 , dig1) -> getReceptionsFromSign(p1 , map , dayNight , dignities)
          .filter { (dig2 , p) -> p === p2 }
          .map { (dig2 , p2) ->
            MutualData(p1, dig1, p2, dig2) }
        }
      }.toSet()
  }

  /** 所有能量的互容 , 不論相等或是不相等 */
  fun getMutualReceptionMap(map: Map<Point, Double>, dayNight: DayNight? , dignities: Collection<Dignity>): Set<MutualData> {
    return map.keys
      .flatMap { p1 -> getReceptions(p1 , map , dayNight , dignities)
        .filter { (dig2 , p2) -> p2 !== p1 }
        .map { (dig2 , p2) -> p2 to dig2}
        .flatMap { (p2 , dig2) -> getReceptions(p2 , map , dayNight , dignities)
          .filter { (dig1 , point) -> point === p1 && p1 !== p2 }
          .map { (dig1 , point) -> MutualData(p1, dig1, p2, dig2) }
        }
    }.toSet()
  }

  /** 能量不相等的互容 */
  fun getMixedReceptionMap(map: Map<Point, Double>, dayNight: DayNight , dignities: Collection<Dignity>): Set<MutualData> {
    return map.keys.flatMap { p ->
      getReceptions(p , map , dayNight , dignities)
        .filter { (dig2 , p2) -> p2 !== p }
        .map { (dig2 , p2) -> p2 to dig2}
        .flatMap { (p2 , dig2) -> getReceptions(p2 , map , dayNight , dignities.filter { it !== dig2 } )
          .filter { (dig1 , point) -> point === p && p !== p2 }
          .map { (dig1 , point) -> MutualData(p, dig1, p2, dig2) }
        }
    }.toSet()
  }

  /**
   * 取得黃道帶上某星座，其 Dignity 之 廟旺陷落 各是何星
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 null ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 null
   */
  fun getPoint(sign: ZodiacSign, dignity: Dignity): Point?


  /** 取得黃道帶上某星座，其 Triplicity 是什麼星   */
  fun getTriplicityPoint(sign: ZodiacSign, dayNight: DayNight): Point


  /** receiver 是否 接納 receivee by Essential Debilities (Detriment/Fall)  */
  fun isReceivingFromDebilities(receiver: Point, receivee: Point, h: IHoro): Boolean

  /**
   * receiver 是否 接納 receivee by Essential Dignities (Ruler/Exaltation/Triplicity/Term/Face) <br></br>
   * 老闆是 receiver , 客人是 receivee , 如果客人進入了老闆的地盤 ( 旺 / 廟 / 三分 / Terms / Faces ) , 則「老闆接納外人」
   */
  fun isReceivingFromDignities(receiver: Point, receivee: Point, h: IHoro) : Boolean


  /** 如果 兩顆星都處於 [Dignity.RULER] 或是  [Dignity.EXALTATION] , 則為 true  */
  fun isBothInGoodSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean

  /** 是否兩顆星都處於不佳的狀態. 如果 兩顆星都處於 [Dignity.DETRIMENT] 或是  [Dignity.FALL] , 則為 true  */
  fun isBothInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean

   companion object {
    val logger = LoggerFactory.getLogger(IEssential::class.java)
  }

}

/**
 * p1 以 dig1 的能量招待 (接納) p2 ,
 * p2 以 dig2 的能量招待 (接納) p1
 *
 * p1 「給出」 dig1 的能量到 p2
 * p2 「給出」 dig2 的能量到 p1
 *
 * 另外翻譯為：
 * p1 位於 sign1 , 與 sign1 的 dig2 (p2) 飛至 sign2 , 而 sign2 的 dig1 (p1) 飛至 sign1
 * p1 在 sign1 得到 p2 所提供的 dig2 能量
 * p2 在 sign2 得到 p1 所提供的 dig1 能量
 * */
data class MutualData(val p1: Point, val dig1: Dignity, val p2: Point, val dig2: Dignity)