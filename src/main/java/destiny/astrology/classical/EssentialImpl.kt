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
import mu.KotlinLogging
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
   * 哪一顆星，透過 [Dignity.RULER] 接納了 [this]顆星
   */
  override fun Point.receivingRulerFromSignMap(map: Map<Point, ZodiacSign>): Point? {
    return map[this]?.let { it ->
      with(rulerImpl) { it.getRulerPoint() }.takeIf { map.containsKey(it) }
    }
  }


  /**
   * 哪一顆星，透過 [Dignity.EXALTATION] 接納了 [this]顆星
   */
  override fun Point.receivingExaltFromSignMap(map: Map<Point, ZodiacSign>): Point? {
    return map[this]?.let {
      with(exaltImpl) {
        it.getExaltPoint().takeIf { point -> map.containsKey(point) }
      }
    }
  }

  /** 哪一顆星，透過 [Dignity.TRIPLICITY] 接納了 [this]顆星 */
  override fun Point.receivingTriplicityFromSignMap(map: Map<Point, ZodiacSign>, dayNight: DayNight): Point? {
    return map[this]?.let {
      with(triplicityImpl) {it.getTriplicityPoint(dayNight)}.takeIf { point -> map.containsKey(point) }
    }
  }

  /** 那一顆星，透過 [Dignity.TERM] 接納了 [this]顆星 */
  override fun Point.receivingTermFrom(map: Map<Point, Double>): Point? {
    return map[this]?.let { termImpl.getPoint(it).takeIf { point ->  map.containsKey(point) } }
  }

  /** 哪一顆星，透過 [Dignity.FACE] 接納了 [this]顆星 */
  override fun Point.receivingFaceFrom(map: Map<Point, Double>): Point? {
    return map[this]?.let { faceImpl.getPoint(it).takeIf { point ->  map.containsKey(point) } }
  }

  /** 哪一顆星，透過 [Dignity.FALL] 接納了 [this]顆星 */
  override fun Point.receivingFallFromSignMap(map: Map<Point, ZodiacSign>): Point? {
    return map[this]?.let {
      with(fallImpl) { it.getFallPoint() }.takeIf { point -> map.containsKey(point) }
    }
  }

  /** 哪一顆星，透過 [Dignity.DETRIMENT] 接納了 [this]顆星 */
  override fun Point.receivingDetrimentFromSignMap(map: Map<Point, ZodiacSign>): Point? {
    return map[this]?.let { it: ZodiacSign ->
      with(detrimentImpl) {
        it.getDetrimentPoint().takeIf { point -> map.containsKey(point) }
      }
    }
  }

  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 empty ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 empty
   */
  override fun getPoint(sign: ZodiacSign, dignity: Dignity): Point? {
    return when (dignity) {
      Dignity.RULER -> with(rulerImpl) { sign.getRulerPoint() }
      Dignity.EXALTATION -> with(exaltImpl) { sign.getExaltPoint() }
      Dignity.TRIPLICITY -> null // 需要日夜
      Dignity.FALL -> with(fallImpl) { sign.getFallPoint() }
      Dignity.DETRIMENT -> with(detrimentImpl) { sign.getDetrimentPoint() }
      Dignity.TERM -> null // 需要度數
      Dignity.FACE -> null // 需要度數
    }
  }

  /** Triplicity of [DayNight.DAY]/[DayNight.NIGHT]  */
  override fun getTriplicityPoint(sign: ZodiacSign, dayNight: DayNight): Point {

    return with(triplicityImpl) { sign.getTriplicityPoint(dayNight) }
  }

  /**
   * [this] 是否 接納 [receivee] by Essential Dignities (Ruler/Exaltation/Triplicity/Term/Face) <br></br>
   * 主人是 [this] , 客人是 [receivee] , 如果客人進入了主人的地盤 ( 廟 / 旺 / 三分 / Terms / Faces ) , 則「主人接納客人」、「客人接收到主人的 Dignity」
   */
  override fun Point.isReceivingFromDignities(receivee: Point, h: IHoroscopeModel): Boolean {
    return h.getZodiacSign(receivee)?.let { receiveeSign ->
      return when (this) {
        getPoint(receiveeSign, Dignity.RULER) -> {
          logger.debug("{} 透過 {} 接納 {}", this, Dignity.RULER, receivee)
          true
        }
        getPoint(receiveeSign, Dignity.EXALTATION) -> {
          logger.debug("{} 透過 {} 接納 {}", this, Dignity.EXALTATION, receivee)
          true
        }
        with(triplicityImpl) {receiveeSign.getTriplicityPoint(dayNightDifferentiator.getDayNight(h.lmt, h.location))} -> {
          logger.debug("{} 透過 Triplicity 接納 {}", this, receivee)
          true
        }
        else -> {
          return h.getPosition(receivee)?.lng?.let { lngDegree ->
            return when (this) {
              termImpl.getPoint(lngDegree) -> {
                logger.debug("{} 透過 TERMS 接納 {}", this, receivee)
                true
              }
              faceImpl.getPoint(lngDegree) -> {
                logger.debug("{} 透過 FACE 接納 {}", this, receivee)
                true
              }
              else -> false
            }
          } ?: false
        } // else
      }
    } ?: false
  }

  /** [this] 是否 接納 receivee by Essential Debilities (Detriment/Fall)  */
  override fun Point.isReceivingFromDebilities(receivee: Point, h: IHoroscopeModel): Boolean {
    return h.getZodiacSign(this)?.let { receiveeSign ->
      this === getPoint(receiveeSign, Dignity.DETRIMENT) ||
        this === getPoint(receiveeSign, Dignity.FALL)
    } ?: false
  }

  /** 如果 兩顆星都處於 [Dignity.RULER] 或是  [Dignity.EXALTATION] , 則為 true  */
  override fun isBothInGoodSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return with(rulerImpl) {
      with(exaltImpl) {
        (p1 === sign1.getRulerPoint() || p1 === sign1.getExaltPoint())
          && (p2 === sign2.getRulerPoint() || p2 === sign2.getExaltPoint())
      }
    }

  }

  /** 是否兩顆星都處於不佳的狀態. 如果 兩顆星都處於 [Dignity.DETRIMENT] 或是  [Dignity.FALL] , 則為 true  */
  override fun isBothInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return with(detrimentImpl) {
      with(fallImpl) {
        (p1 === sign1.getDetrimentPoint() || p1 === sign1.getFallPoint())
          && (p2 === sign2.getDetrimentPoint() || p2 === sign2.getFallPoint())
      }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger {  }
  }
}
