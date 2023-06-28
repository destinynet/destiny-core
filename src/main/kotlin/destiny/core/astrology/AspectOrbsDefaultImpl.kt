/**
 * @author smallufo
 * Created on 2007/11/24 at 下午 11:51:07
 */
package destiny.core.astrology

import destiny.core.astrology.Aspect.*
import destiny.core.astrology.Aspect.Importance.HIGH
import destiny.core.astrology.Aspect.Importance.MEDIUM
import java.io.Serializable

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
      CONJUNCTION -> 11.0
      SEMISEXTILE -> 1.5 //30
      DECILE -> 1.0 //36
      NOVILE -> 1.0 //40
      SEMISQUARE -> 2.0 //45
      SEPTILE -> 1.5 // 360x1/7
      SEXTILE -> 4.5 //60
      QUINTILE -> 2.0 //72
      BINOVILE -> 1.0 //80
      SQUARE -> 7.5 //90
      BISEPTILE -> 1.5 // 360x2/7
      SESQUIQUINTLE -> 1.5 //108
      TRINE -> 7.5 //120
      SESQUIQUADRATE -> 2.0 //135
      BIQUINTILE -> 2.0 //144
      QUINCUNX -> 2.0 //150
      TRISEPTILE -> 1.5 //360x3/7
      QUATRONOVILE -> 1.0 //160
      OPPOSITION -> 11.0
    }
  }


  companion object {

    val aspectThresholdMap = mapOf(
      CONJUNCTION to  0.6,
      OPPOSITION to  0.6,
      TRINE to 0.7,
      SQUARE to 0.7,
      SEXTILE to 0.75,
      SEMISQUARE to 0.75,
      SESQUIQUADRATE to 0.8,
      SEMISEXTILE to 0.9,
      QUINCUNX to 0.9
    )


  }

}
