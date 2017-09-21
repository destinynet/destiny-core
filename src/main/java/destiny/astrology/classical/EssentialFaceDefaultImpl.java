/**
 * @author smallufo 
 * Created on 2007/11/28 at 下午 9:08:16
 */ 
package destiny.astrology.classical;

import com.google.common.collect.ImmutableList;
import destiny.astrology.Planet;
import destiny.astrology.Star;
import destiny.astrology.Utils;
import destiny.astrology.ZodiacSign;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Essential Face 內定實作 , , 參考 Ptolemy's Table , 以三分法 . Al-Biruni 利用 Chaldean order 排列，從戌宮零度開始， 火 -> 日 -> 金 -> 水 -> 月 -> 土 -> 木 ，依序下去，每星佔 10度 <br>
 * 另一種做法，是 Ptolemy 的定義： <br> 
 * 根據此網站說明： http://www.gotohoroscope.com/dictionary/astrological-F.html <br/>
 * A planet is said to be in its own face when located in a house that is distant from the Moon or the Sun by the same number of houses as the sign it rules is distant from the sign ruled by the Moon or Sun respectively.
 * 也就是說 : <br>
 * 水星若與日月呈現 30度角，則得 Face<br>
 * 金星若與日月呈現 60度角，則得 Face<br>
 * 火星若與日月呈現 90度角，則得 Face<br>
 * 木星若與日月呈現120度角，則得 Face<br>
 * 土星若與日月呈現150度角，則得 Face<br>
 * 
 */
public class EssentialFaceDefaultImpl implements EssentialFaceIF , Serializable
{
  /** 因為間距固定 10度 , 所以 list 不用儲存度數 */
  private final static ImmutableList<Star> starList = new ImmutableList.Builder<Star>()
    //戌
    .add(Planet.MARS)
    .add(Planet.SUN)
    .add(Planet.VENUS)
    //酉
    .add(Planet.MERCURY)
    .add(Planet.MOON)
    .add(Planet.SATURN)
    //申
    .add(Planet.JUPITER)
    .add(Planet.MARS)
    .add(Planet.SUN)
    //未
    .add(Planet.VENUS)
    .add(Planet.MERCURY)
    .add(Planet.MOON)
    //午
    .add(Planet.SATURN)
    .add(Planet.JUPITER)
    .add(Planet.MARS)
    //巳
    .add(Planet.SUN)
    .add(Planet.VENUS)
    .add(Planet.MERCURY)
    //辰
    .add(Planet.MOON)
    .add(Planet.SATURN)
    .add(Planet.JUPITER)
    //卯
    .add(Planet.MARS)
    .add(Planet.SUN)
    .add(Planet.VENUS)
    //寅
    .add(Planet.MERCURY)
    .add(Planet.MOON)
    .add(Planet.SATURN)
    //丑
    .add(Planet.JUPITER)
    .add(Planet.MARS)
    .add(Planet.SUN)
    //子
    .add(Planet.VENUS)
    .add(Planet.MERCURY)
    .add(Planet.MOON)
    //亥
    .add(Planet.SATURN)
    .add(Planet.JUPITER)
    .add(Planet.MARS)
    .build();

  @NotNull
  @Override
  public Star getFaceStar(double degree) {
    int index = (int) (Utils.getNormalizeDegree(degree) / 10);
    return starList.get(index);
  }

  @NotNull
  @Override
  public Star getFaceStar(@NotNull ZodiacSign sign, double degree) {
    return getFaceStar(sign.getDegree() + degree);
  }

}
