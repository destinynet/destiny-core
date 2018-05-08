/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:28:26
 */
package destiny.astrology

import org.slf4j.LoggerFactory
import java.io.Serializable

/** 利用 HoroscopeAspectsCalculatorIF , 計算命盤之中，星體所呈現的交角，及其容許度  */
class HoroscopeAspectsCalculator(private val horoscope: IHoro, private val calculator: IHoroscopeAspectsCalculator) : Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)


  /**
   * 計算 points 之間所形成的交角 . aspects 為要計算的交角 , 如果 aspects 為 null , 代表不過濾任何交角 <br></br>
   * 如果沒有形成任何交角（不太可能 , 除非 points 很少 ），則傳回 size = 0 之 Set
   */
  @JvmOverloads
  fun getAspectDataSet(points: Collection<Point>, aspects: Collection<Aspect>? = null): Collection<HoroscopeAspectData> {
    val dataSet = mutableSetOf<HoroscopeAspectData>()

    for (point in points) {
      val map = calculator.getPointAspect(point, horoscope, points)
      logger.debug("與 {} 形成所有交角的 pointAspect Map = {}", point, map)

      val setOfPoint = map.filter { (key , value) -> aspects?.contains(value)?:false }
        .map { (key , value) -> HoroscopeAspectData(point , key , value , horoscope.getAspectError(point , key , value)) }
        .toSet()

      dataSet.addAll(setOfPoint)

//      for ((key, value) in map) {
//        //處理過濾交角的事宜
//        if (aspects == null || aspects.size == 0 || aspects.contains(value)) {
//          val data = HoroscopeAspectData(point, key, value, horoscope.getAspectError(point, key, value))
//          logger.debug("data : twoPoints = {} 形成 {} 角 , 交角 {} 度", data.twoPoints, data.aspect, data.orb)
//          dataSet.add(data)
//        }
//      }
    }
    return dataSet
  }

}
