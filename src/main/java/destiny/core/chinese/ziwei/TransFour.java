/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 四化
 * 參考實作 http://destiny66.com/xuetang/tzyy_wei/question/question.htm
 * */
public interface TransFour extends Descriptive {

  enum Type {祿 , 權 , 科 , 忌}

  /** 取得「某年的某四化」是哪顆星 */
  ZStar getStarOf(Stem year , Type type);

  /** 類似前者，但逆算：計算此星於此年，是否有四化，若有的話，其為何者 */
  default Optional<Type> getTypeOf(ZStar star , Stem year) {
    // 先把本年四化的四顆星都找出來
    Map<ZStar , Type> map =
      Arrays.stream(Type.values())
      .map(type -> Tuple.tuple(getStarOf(year , type) , type))
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    return Optional.ofNullable(map.getOrDefault(star , null));
  }
}
