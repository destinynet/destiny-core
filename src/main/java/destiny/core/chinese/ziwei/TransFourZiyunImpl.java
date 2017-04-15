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
import static destiny.core.chinese.Stem.癸;
import static destiny.core.chinese.ziwei.LuckyStar.*;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.ITransFour.Value.*;
import static org.jooq.lambda.tuple.Tuple.tuple;

/**
 * 紫雲派
 */
public class TransFourZiyunImpl extends TransFourAbstractImpl {

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

    // 「紫雲派」，與「全集」的差別，就只有「癸干：破巨陽貪」
    .put(tuple(癸 , 祿) , 破軍)
    .put(tuple(癸 , 權) , 巨門)
    .put(tuple(癸 , 科) , 太陽)  // 唯一與「全集」不同之處
    .put(tuple(癸 , 忌) , 貪狼)

    .build();

  @Override
  protected Map<Tuple2<Stem, Value>, ZStar> getTransMap() {
    return transMap;
  }

  @Override
  public String getTitle(Locale locale) {
    return "紫雲";
  }

  @Override
  public String getDescription(Locale locale) {
    return "紫雲派";
  }

}
