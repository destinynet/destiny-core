/**
 * Created by smallufo at 2008/11/6 下午 6:21:59
 */
package destiny.astrology

import destiny.astrology.AspectData.Type.APPLYING
import destiny.astrology.AspectData.Type.SEPARATING
import mu.KotlinLogging
import java.io.Serializable
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/** 一個星盤當中，兩個星體，是否形成交角。以及即將形成 ([APPLYING] , 入相位)，還是離開該交角 ([SEPARATING] , 出相位)  */
class AspectApplySeparateImpl(
  /** 可以注入現代占星 ( AspectEffectiveModern ) 或是古典占星 ( AspectEffectiveClassical ) 的實作  */
  private val aspectEffectiveImpl: IAspectEffective,
  private val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  private val houseCuspImpl: IHouseCusp,
  private val pointPosMap: Map<Point, IPosition<*>>
) : IAspectApplySeparate, Serializable {


  /**
   * 判斷兩顆星體是否形成某交角 , 如果是的話 , 傳回 入相位或是出相位 ; 如果沒有形成交角 , 傳回 null
   * 計算方式：這兩顆星的交角，與 Aspect 的誤差，是否越來越少
   */
  override fun getAspectType(h: IHoroscopeModel,
                             p1: Point,
                             p2: Point,
                             aspect: Aspect): AspectData.Type? {
    val deg1 = h.getPositionWithAzimuth(p1).lng
    val deg2 = h.getPositionWithAzimuth(p2).lng

    return if (aspectEffectiveImpl.isEffective(p1, deg1, p2, deg2, aspect)) {
      val planetsAngle = IHoroscopeModel.getAngle(deg1, deg2)
      val error = abs(planetsAngle - aspect.degree) //目前與 aspect 的誤差

      val lmt = h.lmt //目前時間
      val later = lmt.plus(1, ChronoUnit.SECONDS) // 一段時間後

      val hContext: IHoroscopeContext = HoroscopeContext(starPositionWithAzimuthImpl, houseCuspImpl, pointPosMap, h.points, h.houseSystem, h.coordinate, h.centric)
      val h2 = hContext.getHoroscope(lmt = later, loc = h.location, place = h.place, points = h.points)

      val deg1Next = h2.getPositionWithAzimuth(p1).lng
      val deg2Next = h2.getPositionWithAzimuth(p2).lng
      val planetsAngleNext = IHoroscopeModel.getAngle(deg1Next, deg2Next)
      val errorNext = abs(planetsAngleNext - aspect.degree)


      if (errorNext <= error) APPLYING else SEPARATING
    } else
      null //這兩顆星沒有形成交角
  }

  override fun getAspectAndType(h: IHoroscopeModel, p1: Point, p2: Point, aspects: Collection<Aspect>): Pair<Aspect, AspectData.Type>? {
    return aspects.asSequence().map { aspect ->
      aspect to getAspectType(h, p1, p2, aspect)
    }.filter { (_, type) ->
      type != null
    }.map { (aspect, type) ->
      aspect to type!!
    }.firstOrNull()
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }

}
