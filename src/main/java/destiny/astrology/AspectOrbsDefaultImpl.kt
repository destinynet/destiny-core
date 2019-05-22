/**
 * @author smallufo
 * Created on 2007/11/24 at 下午 11:51:07
 */
package destiny.astrology

import java.io.Serializable
import destiny.astrology.Aspect.Importance.HIGH
import destiny.astrology.Aspect.Importance.MEDIUM

/**
 * 「現代占星術」的交角容許度，內定實作
 * 參考資料 : http://www.myastrologybook.com/aspects-and-orbs.htm
 *
 * @param defaultThreshold 符合容許度後，評分從幾分開始起跳 , 本實作 [HIGH] , [MEDIUM] 有設定 , 其餘不重要的度數 , 就從 0.9 起跳
 */
class AspectOrbsDefaultImpl(private val defaultThreshold : Double = 0.9) : IAspectOrbs, Serializable {

  override fun getAspectOrbAndThreshold(aspect: Aspect): Pair<Double, Double> {

    return getAspectOrb(aspect) to aspectThresholdMap.getOrDefault(aspect , defaultThreshold)

  }

  override fun getAspectOrb(aspect: Aspect): Double {
    return when (aspect) {
      Aspect.CONJUNCTION -> 11.0
      Aspect.SEMISEXTILE -> 1.5 //30
      Aspect.DECILE -> 1.0 //36
      Aspect.NOVILE -> 1.0 //40
      Aspect.SEMISQUARE -> 2.0 //45
      Aspect.SEPTILE -> 1.5 // 360x1/7
      Aspect.SEXTILE -> 4.5 //60
      Aspect.QUINTILE -> 2.0 //72
      Aspect.BINOVILE -> 1.0 //80
      Aspect.SQUARE -> 7.5 //90
      Aspect.BISEPTILE -> 1.5 // 360x2/7
      Aspect.SESQUIQUINTLE -> 1.5 //108
      Aspect.TRINE -> 7.5 //120
      Aspect.SESQUIQUADRATE -> 2.0 //135
      Aspect.BIQUINTILE -> 2.0 //144
      Aspect.QUINCUNX -> 2.0 //150
      Aspect.TRISEPTILE -> 1.5 //360x3/7
      Aspect.QUATRONOVILE -> 1.0 //160
      Aspect.OPPOSITION -> 11.0
    }
  }

  companion object {

    val aspectThresholdMap = mapOf(
      Aspect.CONJUNCTION to  0.6,
      Aspect.OPPOSITION to  0.6,
      Aspect.TRINE to 0.7,
      Aspect.SQUARE to 0.7,
      Aspect.SEXTILE to 0.75,
      Aspect.SEMISQUARE to 0.75,
      Aspect.SESQUIQUADRATE to 0.8,
      Aspect.SEMISEXTILE to 0.9,
      Aspect.QUINCUNX to 0.9
    )


  }

}
