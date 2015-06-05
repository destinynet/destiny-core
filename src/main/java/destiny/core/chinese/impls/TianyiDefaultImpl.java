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

public class TianyiDefaultImpl implements TianyiIF, Serializable {

  /**
   * 《協紀辨方書》 《蠡海集》
   * 甲戊庚牛羊，乙己鼠猴鄉；丙丁豬雞位，壬癸兔蛇藏；六辛逢馬虎，此是貴人鄉。
   * (甲羊戊庚牛)
   * <p>
   * 《大六壬探源》
   * 論旦貴：甲羊戊庚牛，乙猴己鼠求，丙雞丁豬位，壬兔癸蛇游，六辛逢虎上，陽貴日中儔。
   * 論暮貴：甲牛戊庚羊，乙鼠己猴鄉，丙豬丁雞位，壬蛇癸兔藏，六辛逢午馬，陰貴夜時當。
   * 指的亦是此種排列方法
   * <p>
   * 《六壬類聚》、《六壬摘要》、《壬學瑣記》及《六壬秘笈》亦是此法
   * <p>
   * 《考原》
   * 陽貴歌曰：庚戊見牛甲在羊，乙猴己鼠丙雞方，
   * 　　　　　丁豬癸蛇壬是兔，六辛逢虎貴為陽。
   * 陰貴歌曰：甲貴陰牛庚戊羊，乙貴在鼠己猴鄉，
   * 　　　　　丙豬丁雞辛遇馬，壬蛇癸兔屬陰方。
   */
  @Override
  public Branch getFirstTianyi(Stem stem, DayNight dayNight) {
    switch (stem) {
      case 甲:
        return dayNight == DAY ? 未 : 丑;
      case 戊:
      case 庚:
        return dayNight == DAY ? 丑 : 未;

      case 乙:
        return dayNight == DAY ? 申 : 子;
      case 己:
        return dayNight == DAY ? 子 : 申;

      case 丙:
        return dayNight == DAY ? 酉 : 亥;
      case 丁:
        return dayNight == DAY ? 亥 : 酉;

      case 壬:
        return dayNight == DAY ? 卯 : 巳;
      case 癸:
        return dayNight == DAY ? 巳 : 卯;

      case 辛:
        return dayNight == DAY ? 寅 : 午;

      default:
        throw new AssertionError(stem + " at " + dayNight);
    }
  }

  @Override
  public String getTitle(Locale locale) {
    return "《協紀辨方書》";
  }

  @Override
  public String getDescription(Locale locale) {
    return "《協紀辨方書》、《蠡海集》、《六壬類聚》、《果老星宗》、《考原》 推導天乙貴人的方法：甲羊戊庚牛 … 六辛逢馬虎";
  }
}
