/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 8:35:16
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.IDayNight
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Point
import destiny.core.astrology.ZodiacSign
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


  override fun Point.getDignitiesFromSignMap(map: Map<Point, ZodiacSign>, dayNight: DayNight?): List<Dignity> {
    return map[this]?.let { sign ->
      mutableSetOf<Dignity>().apply {
        with(rulerImpl) {
          if (this@getDignitiesFromSignMap === sign.getRulerPoint(dayNight))
            add(Dignity.RULER)
        }
        with(exaltImpl) {
          if (this@getDignitiesFromSignMap === sign.getExaltPoint())
            add(Dignity.EXALTATION)
        }
        with(triplicityImpl) {
          if (dayNight != null) {
            if (this@getDignitiesFromSignMap === sign.getTriplicityPoint(dayNight)) {
              add(Dignity.TRIPLICITY)
            }
          } else {
            // 如果沒有傳 DayNight , 但是 DAY / NIGHT 的 Triplicity 都是同一顆星 , 且就是 此 point , 那也算
            // 例如 托勒密設定下 , 巨蟹座 , 日、夜 的 Triplicity 都是火星 , 以及 , 雙魚座 , 日、夜的 Triplicity 都是火星
            if (sign.getTriplicityPoint(DayNight.DAY) === sign.getTriplicityPoint(DayNight.NIGHT) && this@getDignitiesFromSignMap === sign.getTriplicityPoint(DayNight.DAY)) {
              add(Dignity.TRIPLICITY)
            }
          }
        }
        with(fallImpl) {
          if (this@getDignitiesFromSignMap === sign.getFallPoint())
            add(Dignity.FALL)
        }
        with(detrimentImpl) {
          if (this@getDignitiesFromSignMap === sign.getDetrimentPoint())
            add(Dignity.DETRIMENT)
        }
      }.toSortedSet().toList()
    } ?: emptyList()
  }

  /**
   * 承上，完整度數版 (可回傳 [Dignity.TERM] 以及 [Dignity.FACE] )
   */
  override fun Point.getDignities(map: Map<Point, Double>, dayNight: DayNight?): List<Dignity> {
    return map[this]?.let { deg: Double ->
      val sign = ZodiacSign.of(deg)
      this.getDignitiesFromSignMap(mapOf(this to sign)).toMutableSet().apply {
        with(termImpl) {
          if (this@getDignities === sign.getTermPoint(deg))
            add(Dignity.TERM)
          with(faceImpl) {
            if (this@getDignities === sign.getFacePoint(deg))
              add(Dignity.FACE)
          }
        }
      }.toSortedSet().toList()
    } ?: emptyList()
  }

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
      with(triplicityImpl) { it.getTriplicityPoint(dayNight) }.takeIf { point -> map.containsKey(point) }
    }
  }

  /** 那一顆星，透過 [Dignity.TERM] 接納了 [this]顆星 */
  override fun Point.receivingTermFrom(map: Map<Point, Double>): Point? {
    return map[this]?.let { termImpl.getPoint(it).takeIf { point -> map.containsKey(point) } }
  }

  /** 哪一顆星，透過 [Dignity.FACE] 接納了 [this]顆星 */
  override fun Point.receivingFaceFrom(map: Map<Point, Double>): Point? {
    return map[this]?.let { faceImpl.getPoint(it).takeIf { point -> map.containsKey(point) } }
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
        with(triplicityImpl) { receiveeSign.getTriplicityPoint(dayNightDifferentiator.getDayNight(h.lmt, h.location)) } -> {
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
    private val logger = KotlinLogging.logger { }
  }
}
