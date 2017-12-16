/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:21:53
 */
package destiny.astrology.classical;

import destiny.astrology.DayNight;
import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;
import org.jetbrains.annotations.NotNull;

/**
 * 取得在某星座 ZodiacSign 得到 Triplicity 的 Star <BR>
 * 內定實作是 EssentialTriplicityDefaultImpl<BR>
 * 尚有 Dorotheus 的系統，參照此網址 http://en.wikipedia.org/wiki/Triplicity
 */
public interface EssentialTriplicityIF {

  /** 取得黃道帶上某星座，其 Triplicity 是什麼星  */
  @NotNull
  Point getTriplicityPoint(ZodiacSign sign, DayNight dayNight);

}
