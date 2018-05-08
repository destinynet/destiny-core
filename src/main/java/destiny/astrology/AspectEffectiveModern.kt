/**
 * @author smallufo
 * Created on 2007/11/24 at 下午 9:01:16
 */
package destiny.astrology

import java.io.Serializable

/**
 * <pre>
 * 現代占星的交角容許度
 * 參考資料
 * http://www.myastrologybook.com/aspects-and-orbs.htm
</pre> *
 */
class AspectEffectiveModern : Serializable, IAspectEffective {

  /** 「不考慮行星」的交角容許度 , 內定採用 AspectOrbsDefaultImpl , 未來可以注入資料庫實作的版本  */
  private var aspectOrbsImpl: IAspectOrbs = AspectOrbsDefaultImpl()

  /** 「考量行星的」交角容許度實作，內定採用 AspectOrbsPlanetDefaultImpl , 未來可以注入資料庫實作的版本  */
  private var aspectOrbsPlanetImpl: IAspectOrbsPlanet = AspectOrbsPlanetDefaultImpl()

  fun setAspectOrbsImpl(impl: IAspectOrbs) {
    this.aspectOrbsImpl = impl
  }

  fun setAspectOrbsPlanetImpl(impl: IAspectOrbsPlanet) {
    this.aspectOrbsPlanetImpl = impl
  }

  /** 直接比對度數是否形成交角，不考慮星體  */
  fun isEffective(deg1: Double, deg2: Double, aspect: Aspect): Boolean {
    val angle = IHoro.getAngle(deg1, deg2)
    return Math.abs(angle - aspect.degree) <= aspectOrbsImpl.getAspectOrb(aspect)
  }

  /** 有些版本有考慮星體，例如：太陽月亮的交角，會有較高的容許度  */
  override fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Boolean {
     //從「考量行星」的交角容許度實作找起
    val orb = aspectOrbsPlanetImpl.getPlanetAspectOrb(p1, p2, aspect)
      ?: aspectOrbsImpl.getAspectOrb(aspect) // 再從「不考慮行星」的交角容許度尋找
    val angle = IHoro.getAngle(deg1, deg2)

    return orb.let {
      Math.abs(angle - aspect.degree) <= it
    }
  }

  companion object {

    fun isEffective(deg1: Double, deg2: Double, aspect: Aspect, orb: Double): Boolean {
      val angle = IHoro.getAngle(deg1, deg2)
      return Math.abs(angle - aspect.degree) <= orb
    }
  }


}
