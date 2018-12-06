/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 8:35:16
 */
package destiny.astrology.classical

import destiny.astrology.IDayNight
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.core.DayNight
import java.io.Serializable

/** Facade Class of Ptolemy's Table of Essential Dignities and Debilities  */
class EssentialImpl(private val rulerImpl: IRuler,
                    private val exaltImpl: IExaltation,
                    private val fallImpl: IFall,
                    private val detrimentImpl: IDetriment,
                    private val triplicityImpl: ITriplicity,
                    private val termImpl: ITerm,
                    private val faceImpl: IFace,
                    private val dayNightDifferentiator: IDayNight) : IEssential, Serializable {


  /**
   * 那一顆星，透過 [Dignity.RULER] 接納了 p
   */
  override fun receivingRulerFromSignMap(p: Point, map: Map<Point, ZodiacSign>): Point? {
    return map[p]?.let { it -> rulerImpl.getPoint(it).takeIf { map.containsKey(it) } }
  }


  /**
   * 哪一顆星，透過 [Dignity.EXALTATION] 接納了 p
   */
  override fun receivingExaltFromSignMap(p: Point, map: Map<Point, ZodiacSign>): Point? {
    return map[p]?.let { exaltImpl.getPoint(it).takeIf { point -> map.containsKey(point) } }
  }

  /** 哪一顆星，透過 [Dignity.TRIPLICITY] 接納了 p */
  override fun receivingTriplicityFromSignMap(p: Point, map: Map<Point, ZodiacSign>, dayNight: DayNight): Point? {
    return map[p]?.let { triplicityImpl.getPoint(it, dayNight).takeIf { point -> map.containsKey(point) } }
  }

  /** 那一顆星，透過 [Dignity.TERM] 接納了 p */
  override fun receivingTermFrom(p: Point, map: Map<Point, Double>): Point? {
    return map[p]?.let { termImpl.getPoint(it).takeIf { point ->  map.containsKey(point) } }
  }

  /** 哪一顆星，透過 [Dignity.FACE] 接納了 p */
  override fun receivingFaceFrom(p: Point, map: Map<Point, Double>): Point? {
    return map[p]?.let { faceImpl.getPoint(it).takeIf { point ->  map.containsKey(point) } }
  }

  /** 哪一顆星，透過 [Dignity.FALL] 接納了 p */
  override fun receivingFallFromSignMap(p: Point, map: Map<Point, ZodiacSign>): Point? {
    return map[p]?.let { fallImpl.getPoint(it).takeIf { point ->  map.containsKey(point) } }
  }

  /** 哪一顆星，透過 [Dignity.DETRIMENT] 接納了 p */
  override fun receivingDetrimentFromSignMap(p: Point, map: Map<Point, ZodiacSign>): Point? {
    return map[p]?.let { detrimentImpl.getPoint(it).takeIf { point -> map.containsKey(point) } }
  }


  /** 取得此顆星，各從哪些星體，接受哪種 [Dignity] 的招待 */
  override fun getReceptions(p: Point, map: Map<Point, Double>, dayNight: DayNight?, dignities: Collection<Dignity>): Map<Dignity, Point> {
    return Dignity.values().filter { dignities.contains(it) }.map { dignity ->
      return@map when (dignity) {
        Dignity.RULER -> Dignity.RULER to receivingRulerFrom(p, map)
        Dignity.EXALTATION -> Dignity.EXALTATION to receivingExaltFrom(p, map)
        Dignity.TRIPLICITY -> Dignity.TRIPLICITY to dayNight?.let { receivingTriplicityFrom(p, map, it) }
        Dignity.TERM -> Dignity.TERM to receivingTermFrom(p, map)
        Dignity.FACE -> Dignity.FACE to receivingFaceFrom(p, map)
        Dignity.FALL -> Dignity.FALL to receivingFallFrom(p, map)
        Dignity.DETRIMENT -> Dignity.DETRIMENT to receivingDetrimentFrom(p, map)
      }
    }
      .filter { (_, point) -> point != null }
      .map { (k, v) -> k to v!! }
      .toMap()
  }

  /** 取得此顆星，各從哪些星體，接受哪種 [Dignity] 的招待 , 但是不計算 [Dignity.TERM] 以及 [Dignity.FACE] , 因為這兩者需要度數 */
  override fun getReceptionsFromSign(p: Point, map: Map<Point, ZodiacSign>, dayNight: DayNight?, dignities: Collection<Dignity>): Map<Dignity, Point> {
    return Dignity.values().filter { dignities.contains(it) }.map { dignity ->
      return@map when (dignity) {
        Dignity.RULER -> Dignity.RULER to receivingRulerFromSignMap(p, map)
        Dignity.EXALTATION -> Dignity.EXALTATION to receivingExaltFromSignMap(p, map)
        Dignity.TRIPLICITY -> Dignity.TRIPLICITY to dayNight?.let { receivingTriplicityFromSignMap(p, map, it) }
        Dignity.FALL -> Dignity.FALL to receivingFallFromSignMap(p, map)
        Dignity.DETRIMENT -> Dignity.DETRIMENT to receivingDetrimentFromSignMap(p, map)

        Dignity.TERM -> Dignity.TERM to null
        Dignity.FACE -> Dignity.FACE to null
      }
    }
      .filter { (_, point) -> point != null }
      .map { (k, v) -> k to v!! }
      .toMap()
  }

  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 empty ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 empty
   */
  override fun getPoint(sign: ZodiacSign, dignity: Dignity): Point? {
    return when (dignity) {
      Dignity.RULER -> rulerImpl.getPoint(sign)
      Dignity.EXALTATION -> exaltImpl.getPoint(sign)
      Dignity.FALL -> fallImpl.getPoint(sign)
      Dignity.DETRIMENT -> detrimentImpl.getPoint(sign)
      Dignity.FACE -> fallImpl.getPoint(sign)
      else -> null // TRIPLICITY 需要日夜 , TERM 需要度數
    }
  }

  /** Triplicity of DAY/NIGHT  */
  override fun getTriplicityPoint(sign: ZodiacSign, dayNight: DayNight): Point {

    return triplicityImpl.getPoint(sign, dayNight)
  }

  /**
   * receiver 是否 接納 receivee by Essential Dignities (Ruler/Exaltation/Triplicity/Term/Face) <br></br>
   * 老闆是 receiver , 客人是 receivee , 如果客人進入了老闆的地盤 ( 旺 / 廟 / 三分 / Terms / Faces ) , 則「老闆接納外人」
   */
  override fun isReceivingFromDignities(receiver: Point, receivee: Point, h: IHoroscopeModel): Boolean {
    return h.getZodiacSign(receivee)?.let { receiveeSign ->
      return when (receiver) {
        getPoint(receiveeSign, Dignity.RULER) -> {
          IEssential.logger.debug("{} 透過 {} 接納 {}", receiver, Dignity.RULER, receivee)
          true
        }
        getPoint(receiveeSign, Dignity.EXALTATION) -> {
          IEssential.logger.debug("{} 透過 {} 接納 {}", receiver, Dignity.EXALTATION, receivee)
          true
        }
        triplicityImpl.getPoint(receiveeSign, dayNightDifferentiator.getDayNight(h.lmt, h.location)) -> {
          IEssential.logger.debug("{} 透過 Triplicity 接納 {}", receiver, receivee)
          true
        }
        else -> {
          return h.getPosition(receivee)?.lng?.let { lngDegree ->
            return when (receiver) {
              termImpl.getPoint(lngDegree) -> {
                IEssential.logger.debug("{} 透過 TERMS 接納 {}", receiver, receivee)
                true
              }
              faceImpl.getPoint(lngDegree) -> {
                IEssential.logger.debug("{} 透過 FACE 接納 {}", receiver, receivee)
                true
              }
              else -> false
            }
          } ?: false
        } // else
      }
    } ?: false
  }

  /** receiver 是否 接納 receivee by Essential Debilities (Detriment/Fall)  */
  override fun isReceivingFromDebilities(receiver: Point, receivee: Point, h: IHoroscopeModel): Boolean {
    return h.getZodiacSign(receivee)?.let { receiveeSign ->
      receiver === getPoint(receiveeSign, Dignity.DETRIMENT) ||
        receiver === getPoint(receiveeSign, Dignity.FALL)
    } ?: false
  }

  /** 如果 兩顆星都處於 [Dignity.RULER] 或是  [Dignity.EXALTATION] , 則為 true  */
  override fun isBothInGoodSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return (p1 === rulerImpl.getPoint(sign1) || p1 === exaltImpl.getPoint(sign1))
        && (p2 === rulerImpl.getPoint(sign2) || p2 === exaltImpl.getPoint(sign2))
  }

  /** 是否兩顆星都處於不佳的狀態. 如果 兩顆星都處於 [Dignity.DETRIMENT] 或是  [Dignity.FALL] , 則為 true  */
  override fun isBothInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return (p1 === detrimentImpl.getPoint(sign1) || p1 === fallImpl.getPoint(sign1))
        && (p2 === detrimentImpl.getPoint(sign2) || p2 === fallImpl.getPoint(sign2))
  }
}
