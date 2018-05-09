/**
 * @author smallufo
 * Created on 2007/11/22 at 下午 10:42:18
 */
package destiny.astrology.classical

import destiny.astrology.Aspect
import destiny.astrology.IAspectEffective
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Point
import java.io.Serializable

/**
 * <pre>
 * 「古典占星」的交角判定，古典占星只計算「星體光芒的容許度」與「交角」是否有效，並沒有考慮交角的類型。
 * （「現代占星」則是各種交角都有不同的容許度）
 * 演算法採用 Template Method design pattern
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 未來可以繼承此 Abstract Class , 呼叫資料庫 , 取得個人化的 OrbsMap
</pre> *
 */
class AspectEffectiveClassical : IAspectEffective, Serializable {
  /** 星芒交角 , 內定採用 PointDiameterAlBiruniImpl , 尚可選擇注入 PointDiameterLillyImpl  */
  var pointDiameterImpl: IPointDiameter? = null
    private set// = new PointDiameterAlBiruniImpl();

  constructor()

  constructor(planetOrbsImpl: IPointDiameter) {
    this.pointDiameterImpl = planetOrbsImpl
  }

  fun setPlanetOrbsImpl(impl: IPointDiameter) {
    this.pointDiameterImpl = impl
  }

  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, angle: Double): Boolean {
    return Math.abs(
      IHoroscopeModel.getAngle(deg1, deg2) - angle) <= (pointDiameterImpl!!.getDiameter(p1) + pointDiameterImpl!!.getDiameter(p2)) / 2
  }

  /**
   * @param p1 Point 1
   * @param deg1 Point 1 於黃道上的度數
   * @param p2 Point 2
   * @param deg2 Point 2 於黃道上的度數
   * @param angles 判定的角度，例如 [0,60,90,120,180]
   * @return 兩顆星是否形成有效交角
   */
  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, vararg angles: Double): Boolean {
    for (eachAngle in angles) {
      if (isEffective(p1, deg1, p2, deg2, eachAngle))
        return true
    }
    return false
  }

  /** 兩星體是否形成有效交角  */
  override fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Boolean {
    return isEffective(p1, deg1, p2, deg2, aspect.degree)
  }

}
