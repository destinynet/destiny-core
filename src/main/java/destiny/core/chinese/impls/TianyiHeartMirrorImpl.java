/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls;

import destiny.astrology.DayNight;
import destiny.core.Descriptive;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.TianyiIF;

import java.io.Serializable;
import java.util.Locale;

import static destiny.astrology.DayNight.DAY;
import static destiny.core.chinese.Branch.*;

/**
 * 《大六壬心鏡》推導天乙貴人：
 * 甲戊庚游大小吉，乙己神傳晝夜分，丙丁早亥暮居酉，六辛常午複來寅，壬癸立處於已卯，不降天罡作貴人
 * <p>
 * 甲戊庚日旦治大吉（丑），暮治小吉（未）；
 * 乙己日旦治神後（子），暮治傳送（申）；
 * 丙丁日旦治登明（亥），暮治從魁（酉）；
 * 六辛日旦治勝光（午），暮治功曹（寅）；
 * 壬癸日旦治太乙（巳），暮治太沖（卯）
 */
public class TianyiHeartMirrorImpl implements TianyiIF, Descriptive, Serializable {

  @Override
  public Branch getFirstTianyi(Stem stem, DayNight dayNight) {
    switch (stem) {
      case 甲:
      case 戊:
      case 庚:
        return dayNight == DAY ? 丑 : 未;

      case 乙:
      case 己:
        return dayNight == DAY ? 子 : 申;

      case 丙:
      case 丁:
        return dayNight == DAY ? 亥 : 酉;

      case 辛:
        return dayNight == DAY ? 午 : 寅;

      case 壬:
      case 癸:
        return dayNight == DAY ? 巳 : 卯;

      default:
        throw new AssertionError(stem + " at " + dayNight);
    }
  }

  @Override
  public String getTitle(Locale locale) {
    return "《大六壬心鏡》";
  }

  @Override
  public String getDescription(Locale locale) {
    return "《大六壬心鏡》、《六壬景右神定經》、《六壬大全》、《大六壬指南》推導天乙貴人：甲戊庚牛羊 … 六辛逢馬虎…";
  }

}
