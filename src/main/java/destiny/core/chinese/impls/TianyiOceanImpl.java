/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls;

import destiny.astrology.DayNight;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.TianyiIF;

import java.io.Serializable;
import java.util.Locale;

import static destiny.astrology.DayNight.DAY;
import static destiny.core.chinese.Branch.*;

/**
 * 《淵海子平》《六壬視斯》《六壬大占》《大六壬金口訣》
 * <p>
 * 《六壬視斯》說歌曰：甲戊兼牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，庚辛逢虎馬，永定貴人方。
 * 並釋說：“日用上一字，夜用下一字。
 * 如甲戊日日占應用牛字，便從天盤丑上起貴人，是為陽貴。甲戊日夜占，應用羊字，便從天盤未上起貴人，是為陰貴……”。
 */
public class TianyiOceanImpl implements TianyiIF, Serializable {


  /**
   * 甲戊兼牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，
   * 庚辛逢馬虎，此是貴人方，命中如遇此，定作紫薇郎。
   */
  @Override
  public Branch getFirstTianyi(Stem stem, DayNight dayNight) {
    switch (stem) {
      case 甲:
      case 戊:
        return dayNight == DAY ? 丑 : 未;

      case 乙:
      case 己:
        return dayNight == DAY ? 子 : 申;

      case 丙:
      case 丁:
        return dayNight == DAY ? 亥 : 酉;

      case 壬:
      case 癸:
        return dayNight == DAY ? 卯 : 巳;

      case 庚:
      case 辛:
        return dayNight == DAY ? 午 : 寅;

      default:
        throw new AssertionError(stem + " at " + dayNight);
    }
  }

  @Override
  public String getTitle(Locale locale) {
    return "《淵海子平》";
  }

  @Override
  public String getDescription(Locale locale) {
    return "《淵海子平》、《六壬視斯》、《六壬大占》、《大六壬金口訣》推導貴人法：甲戊兼牛羊 … 庚辛逢馬虎";
  }

}
