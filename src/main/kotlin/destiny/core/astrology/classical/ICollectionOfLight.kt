/**
 * Created by smallufo on 2014-07-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.IPointAspectPattern.Type.APPLYING
import destiny.tools.KotlinLogging
import java.io.Serializable
import kotlin.math.abs

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
  fun isCollecting(point : AstroPoint, h: IHoroscopeModel, p1 : AstroPoint, p2 : AstroPoint) : Boolean

  /**
   * 「可能」不指定「光線蒐集模式」，若呈現任何一種，就傳回來
   *
   * @param collectType 若為 null , 則傳回的 CollectType 會計算出，是哪種 [CollectType]
   */
  fun getResultMaybeType(planet: Planet, h: IHoroscopeModel, collectType: CollectType?): Pair<List<Planet>, CollectType>?

}


class CollectionOfLightImpl(private val besiegedImpl: IBesieged,
                            private val essentialImpl: IEssential,
                            private val aspectsCalculator: IAspectsCalculator) : ICollectionOfLight, Serializable {

  /**
   * 指定某種「光線蒐集模式」
   *
   * @param collectType 詢問是否符合某種 「光線蒐集模式」 : [ICollectionOfLight.CollectType]
   */
  override fun getResult(planet: Planet,
                         h: IHoroscopeModel,
                         collectType: ICollectionOfLight.CollectType): List<Planet>? {
    // 若形成光線搜尋模式， 傳回的 List<Planet> size 必定 = 2

    val twoPlanets =
      besiegedImpl.getBesiegingPlanets(planet, h.gmtJulDay, true, Aspect.getAspects(Aspect.Importance.HIGH).toSet()).takeIf {
        it.size == setOf(it).size
      } ?: return null

    val (p1, p2) = twoPlanets.iterator().let { it.next() to it.next() }

    return h.getStarPosition(planet)?.speedLng?.let { planetSpeed ->
      h.getStarPosition(p1)?.speedLng?.let { p1Speed ->
        h.getStarPosition(p2)?.speedLng?.takeIf { p2Speed ->
          // 中間被夾的星，速度比較慢
          abs(planetSpeed) < abs(p1Speed) && abs(planetSpeed) < abs(p2Speed)
        }?.let { p2Speed ->
          logger.debug("{} 速度 {} 慢於 {}的{} 以及 {}的{}", planet, planetSpeed, p1, p1Speed, p2, p2Speed)
          with(essentialImpl) {
            when (collectType) {
              ICollectionOfLight.CollectType.DIGNITIES  -> {
                if (p1.isReceivingFromDignities(planet, h) && p2.isReceivingFromDignities(planet, h))
                  twoPlanets
                else
                  null
              }
              ICollectionOfLight.CollectType.DEBILITIES -> {
                if (p1.isReceivingFromDebilities(planet, h) && p2.isReceivingFromDebilities(planet, h)) {
                  logger.warn("isReceivingFromDebilities : planet = {} , p1 = {} , p2 = {}", planet, p1, p2)
                  twoPlanets
                } else
                  null
              }
              ICollectionOfLight.CollectType.ALL        -> twoPlanets
            }
          }
        }
      }
    }

  }

  /**
   * 「可能」不指定「光線蒐集模式」，若呈現任何一種，就傳回來
   *
   * @param collectType 若為 null , 則傳回的 CollectType 會計算出，是哪種 [ICollectionOfLight.CollectType]
   */
  override fun getResultMaybeType(planet: Planet,
                                  h: IHoroscopeModel,
                                  collectType: ICollectionOfLight.CollectType?): Pair<List<Planet>, ICollectionOfLight.CollectType>? {
    val twoPlanets =
      besiegedImpl.getBesiegingPlanets(planet, h.gmtJulDay, true, Aspect.getAspects(Aspect.Importance.HIGH).toSet())
    val p1 = twoPlanets[0]
    val p2 = twoPlanets[1]


    val planetSpeed = h.getStarPosition(planet)?.speedLng
    val p1Speed = h.getStarPosition(p1)?.speedLng
    val p2Speed = h.getStarPosition(p2)?.speedLng

    return with(essentialImpl) {
      if (planetSpeed != null && p1Speed != null && p2Speed != null
        && abs(planetSpeed) < abs(p1Speed) && abs(planetSpeed) < abs(p2Speed)
      ) {
        logger.debug("{} 速度 {} 慢於 {}的{} 以及 {}的{}", planet, planetSpeed, p1, p1Speed, p2, p2Speed)

        if (collectType == null) {
          //沒有指定蒐集光線的演算法
          val cType: ICollectionOfLight.CollectType =
            if (p1.isReceivingFromDignities(planet, h) && p2.isReceivingFromDignities(planet, h))
              ICollectionOfLight.CollectType.DIGNITIES
            else
              if (p1.isReceivingFromDebilities(planet, h) && p2.isReceivingFromDebilities(planet, h))
                ICollectionOfLight.CollectType.DEBILITIES
              else ICollectionOfLight.CollectType.ALL
          logger.debug("collectType = {}", cType)
          twoPlanets to cType
        } else {
          //有指定蒐集光線的演算法
          when (collectType) {
            ICollectionOfLight.CollectType.DIGNITIES  -> {
              //兩顆快星都必須透過 essential dignities 接納慢星
              if (p1.isReceivingFromDignities(planet, h) && p2.isReceivingFromDignities(planet, h)) {
                twoPlanets to ICollectionOfLight.CollectType.DIGNITIES
              } else
                null
            }

            ICollectionOfLight.CollectType.DEBILITIES -> {
              if (p1.isReceivingFromDebilities(planet, h) && p2.isReceivingFromDebilities(planet, h)) {
                twoPlanets to ICollectionOfLight.CollectType.DEBILITIES
              } else
                null
            }
            ICollectionOfLight.CollectType.ALL        -> twoPlanets to ICollectionOfLight.CollectType.ALL
          }
        } //有指定蒐集光線的演算法
      } else
        null
    }

  }

  override fun isCollecting(point: AstroPoint, h: IHoroscopeModel, p1: AstroPoint, p2: AstroPoint): Boolean {

    return aspectsCalculator.getAspectPatterns(p1, h, setOf(p1, p2), Aspect.getAspects(Aspect.Importance.HIGH).toSet())
      .takeIf { it.isEmpty() }
      ?.takeIf { aspectsCalculator.getAspectPatterns(h, setOf(point, p1), aspects = Aspect.getAspects(Aspect.Importance.HIGH).toSet()).firstOrNull()?.type === APPLYING }
      ?.takeIf { aspectsCalculator.getAspectPatterns(h, setOf(point, p2), aspects = Aspect.getAspects(Aspect.Importance.HIGH).toSet()).firstOrNull()?.type === APPLYING }
      ?.let { true }
      ?: false
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }

}
