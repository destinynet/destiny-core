/**
 * @author smallufo
 * Created on 2007/11/28 at 下午 9:08:16
 */
package destiny.astrology.classical

import destiny.astrology.Planet
import destiny.astrology.Star
import destiny.astrology.Utils
import destiny.astrology.ZodiacSign
import java.io.Serializable

/**
 * Essential Face 內定實作 , , 參考 Ptolemy's Table , 以三分法 . Al-Biruni 利用 Chaldean order 排列，從戌宮零度開始， 火 -> 日 -> 金 -> 水 -> 月 -> 土 -> 木 ，依序下去，每星佔 10度 <br></br>
 * 另一種做法，是 Ptolemy 的定義： <br></br>
 * 根據此網站說明： http://www.gotohoroscope.com/dictionary/astrological-F.html <br></br>
 * A planet is said to be in its own face when located in a house that is distant from the Moon or the Sun by the same number of houses as the sign it rules is distant from the sign ruled by the Moon or Sun respectively.
 * 也就是說 : <br></br>
 * 水星若與日月呈現 30度角，則得 Face<br></br>
 * 金星若與日月呈現 60度角，則得 Face<br></br>
 * 火星若與日月呈現 90度角，則得 Face<br></br>
 * 木星若與日月呈現120度角，則得 Face<br></br>
 * 土星若與日月呈現150度角，則得 Face<br></br>
 *
 */
class EssentialFaceDefaultImpl : EssentialFaceIF, Serializable {

  override fun getFaceStar(degree: Double): Star {
    val index = (Utils.getNormalizeDegree(degree) / 10).toInt()
    return starList[index]
  }

  override fun getFaceStar(sign: ZodiacSign, degree: Double): Star {
    return getFaceStar(sign.degree + degree)
  }

  companion object {
    /** 因為間距固定 10度 , 所以 list 不用儲存度數  */
    private val starList = listOf(
      //戌
        Planet.MARS
      , Planet.SUN
      , Planet.VENUS
      //酉
      , Planet.MERCURY
      , Planet.MOON
      , Planet.SATURN
      //申
      , Planet.JUPITER
      , Planet.MARS
      , Planet.SUN
      //未
      , Planet.VENUS
      , Planet.MERCURY
      , Planet.MOON
      //午
      , Planet.SATURN
      , Planet.JUPITER
      , Planet.MARS
      //巳
      , Planet.SUN
      , Planet.VENUS
      , Planet.MERCURY
      //辰
      , Planet.MOON
      , Planet.SATURN
      , Planet.JUPITER
      //卯
      , Planet.MARS
      , Planet.SUN
      , Planet.VENUS
      //寅
      , Planet.MERCURY
      , Planet.MOON
      , Planet.SATURN
      //丑
      , Planet.JUPITER
      , Planet.MARS
      , Planet.SUN
      //子
      , Planet.VENUS
      , Planet.MERCURY
      , Planet.MOON
      //亥
      , Planet.SATURN
      , Planet.JUPITER
      , Planet.MARS
    )
  }

}
