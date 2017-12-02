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
 * 中州派(王亭之)
 */
public class TransFourMiddleImpl extends TransFourAbstractImpl {
  
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
    .put(戊 , 科 , Companion.get太陽())
    .put(戊 , 忌 , Companion.get天機())

    .put(己 , 祿 , Companion.get武曲())
    .put(己 , 權 , Companion.get貪狼())
    .put(己 , 科 , Companion.get天梁())
    .put(己 , 忌 , StarLucky.Companion.get文曲())

    // 庚 有差別
    .put(庚 , 祿 , Companion.get太陽())
    .put(庚 , 權 , Companion.get武曲())
    .put(庚 , 科 , Companion.get天府())
    .put(庚 , 忌 , Companion.get天同())

    .put(辛 , 祿 , Companion.get巨門())
    .put(辛 , 權 , Companion.get太陽())
    .put(辛 , 科 , StarLucky.Companion.get文曲())
    .put(辛 , 忌 , StarLucky.Companion.get文昌())

    // 壬 有差別
    .put(壬 , 祿 , Companion.get天梁())
    .put(壬 , 權 , Companion.get紫微())
    .put(壬 , 科 , Companion.get天府())
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
    return "中州派(王亭之)";
  }
}
