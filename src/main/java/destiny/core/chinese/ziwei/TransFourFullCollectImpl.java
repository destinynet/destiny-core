/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Locale;
import java.util.Map;

import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.StarLucky.*;
import static destiny.core.chinese.ziwei.StarMain.*;
import static destiny.core.chinese.ziwei.ITransFour.Value.*;
import static org.jooq.lambda.tuple.Tuple.tuple;

/**
 * 紫微斗數全集、中州派（陸斌兆）、欽天門
 *
 * 五十五年二月間，神州出版社出版了《十八飛星策天紫微斗數全集》( 下簡稱《全集》) ，由周應祥發行，
 * 著作人是大宋華山希夷陳圖南著；隱逸玉蟾白先生增。
 *
 * 此書在編者的凡例上，竟然提到：世傳之紫微斗數，計分南、北兩派，北派即本書，屬於真傳正統，奇驗無比，
 * 南派即流傳之俗本，是後人所偽託希夷之名(清朝無聊之徒所纂改)，而不應驗，純屬欺人之偽書…
 *
 * 而這本書自稱的北派，其實和集文書局在七十一年出版的《十八飛星策天紫微斗數全集》，其內容是完全相同，
 * 只是木刻的版本不一樣而已，因此李亨利老師認為，紫微斗數只有《全書》和《全集》之分，
 * 根本沒有南派和北派之說，更不要說爾後陸續出現的透派、四化派，甚至是中洲派等的派別。
 */
public class TransFourFullCollectImpl extends TransFourAbstractImpl {

  private final static Map<Tuple2<Stem, Value> , ZStar> transMap
    = new ImmutableMap.Builder<Tuple2<Stem, Value> , ZStar>()
    .put(tuple(甲 , 祿) , 廉貞)
    .put(tuple(甲 , 權) , 破軍)
    .put(tuple(甲 , 科) , 武曲)
    .put(tuple(甲 , 忌) , 太陽)

    .put(tuple(乙 , 祿) , 天機)
    .put(tuple(乙 , 權) , 天梁)
    .put(tuple(乙 , 科) , 紫微)
    .put(tuple(乙 , 忌) , 太陰)

    .put(tuple(丙 , 祿) , 天同)
    .put(tuple(丙 , 權) , 天機)
    .put(tuple(丙 , 科) , 文昌)
    .put(tuple(丙 , 忌) , 廉貞)

    .put(tuple(丁 , 祿) , 太陰)
    .put(tuple(丁 , 權) , 天同)
    .put(tuple(丁 , 科) , 天機)
    .put(tuple(丁 , 忌) , 巨門)

    // 戊 有差別
    .put(tuple(戊 , 祿) , 貪狼)
    .put(tuple(戊 , 權) , 太陰)
    .put(tuple(戊 , 科) , 右弼)
    .put(tuple(戊 , 忌) , 天機)

    .put(tuple(己 , 祿) , 武曲)
    .put(tuple(己 , 權) , 貪狼)
    .put(tuple(己 , 科) , 天梁)
    .put(tuple(己 , 忌) , 文曲)

    // 庚 有差別
    .put(tuple(庚 , 祿) , 太陽)
    .put(tuple(庚 , 權) , 武曲)
    .put(tuple(庚 , 科) , 太陰)
    .put(tuple(庚 , 忌) , 天同)

    .put(tuple(辛 , 祿) , 巨門)
    .put(tuple(辛 , 權) , 太陽)
    .put(tuple(辛 , 科) , 文曲)
    .put(tuple(辛 , 忌) , 文昌)

    // 壬 有差別
    .put(tuple(壬 , 祿) , 天梁)
    .put(tuple(壬 , 權) , 紫微)
    .put(tuple(壬 , 科) , 左輔)
    .put(tuple(壬 , 忌) , 武曲)

    .put(tuple(癸 , 祿) , 破軍)
    .put(tuple(癸 , 權) , 巨門)
    .put(tuple(癸 , 科) , 太陰)
    .put(tuple(癸 , 忌) , 貪狼)

    .build();

  @Override
  protected Map<Tuple2<Stem, Value>, ZStar> getTransMap() {
    return transMap;
  }

  @Override
  public String getDescription(Locale locale) {
    return "紫微斗數全集、中州派（陸斌兆）、欽天門";
  }
}
