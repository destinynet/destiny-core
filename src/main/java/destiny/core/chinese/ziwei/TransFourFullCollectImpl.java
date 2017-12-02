/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import destiny.core.chinese.Stem;

import java.util.Locale;

import static destiny.core.chinese.Stem.*;
import static destiny.core.chinese.ziwei.ITransFour.Value.*;
import static destiny.core.chinese.ziwei.StarMain.Companion;

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

  private final static Table<Stem , Value , ZStar> table = new ImmutableTable.Builder<Stem, Value, ZStar>()
    .put(甲 , 祿 , Companion.get廉貞())
    .put(甲 , 權 , Companion.get破軍())
    .put(甲 , 科 , Companion.get武曲())
    .put(甲 , 忌 , Companion.get太陽())

    .put(乙 , 祿 , Companion.get天機())
    .put(乙 , 權 , Companion.get天梁())
    .put(乙 , 科 , Companion.get紫微())
    .put(乙 , 忌 , Companion.get太陰())

    .put(丙 , 祿 , Companion.get天同())
    .put(丙 , 權 , Companion.get天機())
    .put(丙 , 科 , StarLucky.Companion.get文昌())
    .put(丙 , 忌 , Companion.get廉貞())

    .put(丁 , 祿 , Companion.get太陰())
    .put(丁 , 權 , Companion.get天同())
    .put(丁 , 科 , Companion.get天機())
    .put(丁 , 忌 , Companion.get巨門())

    // 戊 有差別
    .put(戊 , 祿 , Companion.get貪狼())
    .put(戊 , 權 , Companion.get太陰())
    .put(戊 , 科 , StarLucky.Companion.get右弼())
    .put(戊 , 忌 , Companion.get天機())

    .put(己 , 祿 , Companion.get武曲())
    .put(己 , 權 , Companion.get貪狼())
    .put(己 , 科 , Companion.get天梁())
    .put(己 , 忌 , StarLucky.Companion.get文曲())

    // 庚 有差別
    .put(庚 , 祿 , Companion.get太陽())
    .put(庚 , 權 , Companion.get武曲())
    .put(庚 , 科 , Companion.get太陰())
    .put(庚 , 忌 , Companion.get天同())

    .put(辛 , 祿 , Companion.get巨門())
    .put(辛 , 權 , Companion.get太陽())
    .put(辛 , 科 , StarLucky.Companion.get文曲())
    .put(辛 , 忌 , StarLucky.Companion.get文昌())

    // 壬 有差別
    .put(壬 , 祿 , Companion.get天梁())
    .put(壬 , 權 , Companion.get紫微())
    .put(壬 , 科 , StarLucky.Companion.get左輔())
    .put(壬 , 忌 , Companion.get武曲())

    .put(癸 , 祿 , Companion.get破軍())
    .put(癸 , 權 , Companion.get巨門())
    .put(癸 , 科 , Companion.get太陰())
    .put(癸 , 忌 , Companion.get貪狼())

    .build();

  @Override
  protected Table<Stem, Value, ZStar> getTable() {
    return table;
  }

  @Override
  public String getDescription(Locale locale) {
    return "紫微斗數全集、中州派（陸斌兆）、欽天門";
  }
}
